package com.hermes.android.data.local.db.converters

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.format.HumanReadableDecimalParsing
import kotlinx.datetime.utc

class Converters {

    @TypeConverter
    fun fromInstant(value: Instant?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toInstant(value: String?): Instant? {
        return value?.let { Instant.parse(it) }
    }

    @TypeConverter
    fun fromConnectionMode(value: com.hermes.android.domain.model.ConnectionMode?): String? {
        return value?.name
    }

    @TypeConverter
    fun toConnectionMode(value: String?): com.hermes.android.domain.model.ConnectionMode? {
        return value?.let { com.hermes.android.domain.model.ConnectionMode.valueOf(it) }
    }

    @TypeConverter
    fun fromMessageRole(value: com.hermes.android.domain.model.MessageRole?): String? {
        return value?.name
    }

    @TypeConverter
    fun toMessageRole(value: String?): com.hermes.android.domain.model.MessageRole? {
        return value?.let { com.hermes.android.domain.model.MessageRole.valueOf(it) }
    }
}