package com.cheeke.surfy.userdata.impl

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.cheeke.surfy.core.userdata.InternalDataPreferences
import com.cheeke.surfy.core.userdata.copy
import com.cheeke.surfy.model.defaultLanguage
import com.cheeke.surfy.model.defaultRegion
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class InternalDataPreferencesSerializer @Inject constructor(

) : Serializer<InternalDataPreferences> {
    override val defaultValue: InternalDataPreferences = InternalDataPreferences.newBuilder()
        .setLanguage(defaultLanguage)
        .setRegion(defaultRegion)
        .setImageQuality("original")
        .build()

    override suspend fun readFrom(input: InputStream): InternalDataPreferences =
        try {
            InternalDataPreferences.parseFrom(input).copy {
                if (region.isEmpty()) {
                    region = defaultRegion
                }
                if (language.isEmpty()) {
                    language = defaultLanguage
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