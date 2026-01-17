package com.bowoon.movie.deeplink

import androidx.navigation3.runtime.NavKey
import com.bowoon.common.Log
import kotlinx.serialization.KSerializer

internal class DeepLinkMatcher<T : NavKey>(
    val request: DeepLinkRequest,
    val deepLinkPattern: DeepLinkPattern<T>
) {
    /**
     * [DeepLinkRequest]를 [DeepLinkPattern]에 매치 시킨다
     *
     * 패턴과 일치하면 [DeepLinkMatchResult]를 반환, 그렇지 않으면 null을 반환
     */
    fun match(): DeepLinkMatchResult<T>? {
        if (request.pathSegments.size != deepLinkPattern.pathSegments.size) {
            return null
        }
        // 매개변수가 없이 uri가 패턴과 일치할 때
        if (request.uri == deepLinkPattern.uriPattern) {
            return DeepLinkMatchResult(serializer = deepLinkPattern.serializer, args = mapOf())
        }

        val args = mutableMapOf<String, Any>()

        // path match
        request.pathSegments
            .asSequence()
            .zip(other = deepLinkPattern.pathSegments.asSequence())
            .forEach { (requestedSegment, candidateSegment) ->
                // 경로를 분석하여 매개변수일 경우
                if (candidateSegment.isParamArg) {
                    val parsedValue = try {
                        candidateSegment.typeParser.invoke(requestedSegment)
                    } catch (e: IllegalArgumentException) {
                        Log.e("Failed to parse path value:[$requestedSegment].")
                        return null
                    }
                    args[candidateSegment.stringValue] = parsedValue
                } else if (requestedSegment != candidateSegment.stringValue) {
                    // 예상되는 유형이 아니며 일치하지 않는 경우
                    return null
                }
            }

        // query match
        request.queries.forEach { query ->
            val name = query.key
            val queryStringParser = deepLinkPattern.queryValueParsers[name]
            val queryParsedValue = try {
                queryStringParser!!.invoke(query.value)
            } catch (e: IllegalArgumentException) {
                Log.e("Failed to parse query name:[$name] value:[${query.value}].")
                return null
            }
            args[name] = queryParsedValue
        }

        return DeepLinkMatchResult(serializer = deepLinkPattern.serializer, args = args)
    }
}

/**
 * uri 구문 분석이 완료된 결과물
 *
 * @param [T] 딥링크에서 변환될 NavKey
 * @param serializer [T] 변환을 위한 직렬화
 * @param args 매칭된 매개변수 맵
 * */
internal data class DeepLinkMatchResult<T : NavKey>(
    val serializer: KSerializer<T>,
    val args: Map<String, Any>
)