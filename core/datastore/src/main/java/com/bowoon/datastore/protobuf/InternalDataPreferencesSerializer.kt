package com.bowoon.datastore.protobuf

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.bowoon.movie.core.datastore.InternalDataPreferences
import com.bowoon.movie.core.datastore.copy
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class InternalDataPreferencesSerializer @Inject constructor(

) : Serializer<InternalDataPreferences> {
    override val defaultValue: InternalDataPreferences = InternalDataPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): InternalDataPreferences =
        try {
            InternalDataPreferences.parseFrom(input).copy {
                if (region.isEmpty()) {
                    region = "KR"
                }
                if (language.isEmpty()) {
                    language = "ko"
                }
                if (imageQuality.isEmpty()) {
                    imageQuality = "original"
                }
            }
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException(message = "Cannot read proto.", cause = exception)
        }

    override suspend fun writeTo(t: InternalDataPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}