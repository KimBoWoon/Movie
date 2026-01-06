package com.bowoon.movie.deeplink

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation3.runtime.NavKey
import com.bowoon.detail.movie.navigation.MovieNavKey
import com.bowoon.home.navigation.HomeNavKey
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule


val deepLinkPatterns: List<DeepLinkPattern<out NavKey>> = listOf(
    // Exact match: "https://www.myapp.com/home"
//    DeepLinkPattern(
//        HomeKey.serializer(),
//        "https://www.myapp.com/home".toUri()
//    ),

    // Path arguments: "https://www.myapp.com/users/{filter}"
    DeepLinkPattern(
        serializer = MovieNavKey.serializer(),
        uri = "movieinfo://movie/movie/id".toUri()
    ),

    // Query arguments: "https://www.myapp.com/search?firstName=...&age=..."
//    DeepLinkPattern(
//        SearchKey.serializer(),
//        "https://www.myapp.com/search?{firstName}&{lastName}&{age}".toUri()
//    )
)

class DeepLinkPattern<T : NavKey>(
    val serializer: KSerializer<T>,
    val uri: Uri
) {
    val scheme: String? = uri.scheme
    val host: String? = uri.host
    val pathSegments: List<String> = uri.pathSegments
    // Store argument metadata from placeholders like {filter}
}

class DeepLinkRequest(val uri: Uri) {
    val scheme: String? = uri.scheme
    val host: String? = uri.host
    val pathSegments: List<String> = uri.pathSegments
    val queryParameters: Map<String, String?> = uri.queryParameterNames
        .associateWith { uri.getQueryParameter(it) }
    val queries = buildMap {
        uri.queryParameterNames.forEach { argName ->
            this[argName] = uri.getQueryParameter(argName)!!
        }
    }
}

class DeepLinkMatcher(
    private val request: DeepLinkRequest,
    private val pattern: DeepLinkPattern<*>
) {
    fun match(): DeepLinkMatchResult? {
        // Verify scheme and host match
        if (request.scheme != pattern.scheme) return null
        if (request.host != pattern.host) return null

        // Compare path segments, extracting argument values
        // where pattern has placeholders like {filter}
        val args = mutableMapOf<String, Any>()

        // ... matching logic ...

        args.put(key = "id", value = request.pathSegments[1].toInt())

        return DeepLinkMatchResult(
            args = args,
            serializer = pattern.serializer
        )
    }
}

data class DeepLinkMatchResult(
    val args: Map<String, Any>,
    val serializer: KSerializer<*>
)

fun parseDeepLink(uri: Uri?): NavKey? =
    uri?.let {
        val request = DeepLinkRequest(it)

        val match = deepLinkPatterns.firstNotNullOfOrNull { pattern ->
            DeepLinkMatcher(request, pattern).match()
        }

        match?.let {
            KeyDecoder(arguments = match.args).decodeSerializableValue(deserializer = match.serializer)
        } as NavKey?
    } ?: HomeNavKey

//fun buildSyntheticBackStack(deeplinkKey: NavKey): List<NavKey> {
//    return buildList {
//        var current: NavKey? = deeplinkKey
//        while (current != null) {
//            add(0, current)
//            current = (current as? DeepLinkKey)?.parent
//        }
//    }
//}

/**
 * Decodes the list of arguments into a a back stack key
 *
 * **IMPORTANT** This decoder assumes that all argument types are Primitives.
 */
@OptIn(ExperimentalSerializationApi::class)
internal class KeyDecoder(
    private val arguments: Map<String, Any>,
) : AbstractDecoder() {

    override val serializersModule: SerializersModule = EmptySerializersModule()
    private var elementIndex: Int = -1
    private var elementName: String = ""

    /**
     * Decodes the index of the next element to be decoded. Index represents a position of the
     * current element in the [descriptor] that can be found with [descriptor].getElementIndex.
     *
     * The returned index will trigger deserializer to call [decodeValue] on the argument at that
     * index.
     *
     * The decoder continually calls this method to process the next available argument until this
     * method returns [CompositeDecoder.DECODE_DONE], which indicates that there are no more
     * arguments to decode.
     *
     * This method should sequentially return the element index for every element that has its value
     * available within [arguments].
     */
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        var currentIndex = elementIndex
        while (true) {
            // proceed to next element
            currentIndex++
            // if we have reached the end, let decoder know there are not more arguments to decode
            if (currentIndex >= descriptor.elementsCount) return CompositeDecoder.DECODE_DONE
            val currentName = descriptor.getElementName(currentIndex)
            // Check if bundle has argument value. If so, we tell decoder to process
            // currentIndex. Otherwise, we skip this index and proceed to next index.
            if (arguments.contains(currentName)) {
                elementIndex = currentIndex
                elementName = currentName
                return elementIndex
            }
        }
    }

    /**
     * Returns argument value from the [arguments] for the argument at the index returned by
     * [decodeElementIndex]
     */
    override fun decodeValue(): Any {
        val arg = arguments[elementName]
        checkNotNull(arg) { "Unexpected null value for non-nullable argument $elementName" }
        return arg
    }

    override fun decodeNull(): Nothing? = null

    // we want to know if it is not null, so its !isNull
    override fun decodeNotNullMark(): Boolean = arguments[elementName] != null
}