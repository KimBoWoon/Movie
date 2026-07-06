package com.cheeke.surfy.datastore.protobuf

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.cheeke.surfy.core.datastore.InternalDataPreferences
import com.cheeke.surfy.core.datastore.copy
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale
import javax.inject.Inject

class InternalDataPreferencesSerializer @Inject constructor(

) : Serializer<InternalDataPreferences> {
    override val defaultValue: InternalDataPreferences = InternalDataPreferences.newBuilder()
        .setLanguage(Locale.getDefault().language)
        .setRegion(Locale.getDefault().country)
        .setImageQuality("original")
        .build()

    override suspend fun readFrom(input: InputStream): InternalDataPreferences =
        try {
            InternalDataPreferences.parseFrom(input).copy {
                if (region.isEmpty()) {
                    region = Locale.getDefault().country
                }
                if (language.isEmpty()) {
                    language = Locale.getDefault().language
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