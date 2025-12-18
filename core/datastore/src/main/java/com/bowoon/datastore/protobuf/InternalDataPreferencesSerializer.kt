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
            val internalData = InternalDataPreferences.parseFrom(input)
            internalData.also {
                if (it.region.isEmpty()) {
                    it.copy { region = "KR" }
                }
                if (it.language.isEmpty()) {
                    it.copy { region = "ko" }
                }
                if (it.imageQuality.isEmpty()) {
                    it.copy { region = "original" }
                }
            }
            internalData
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException(message = "Cannot read proto.", cause = exception)
        }

    override suspend fun writeTo(t: InternalDataPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}