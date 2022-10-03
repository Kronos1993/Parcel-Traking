package com.kronos.zipcargo.data.local.database

import android.graphics.Bitmap
import androidx.room.TypeConverter
import java.nio.ByteBuffer
import java.text.NumberFormat

class Converters {

    @TypeConverter
    fun from(value: Number?): Int? {
        return value?.let { Integer.parseInt(value.toString()) }
    }

    @TypeConverter
    fun from(value: Int?): Number? {
        return value?.let { NumberFormat.getNumberInstance().parse(value.toString()) }
    }

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray {
        return if (bitmap != null) {
            val size = bitmap.byteCount
            val buffer = ByteBuffer.allocate(size)
            bitmap.copyPixelsToBuffer(buffer)
            buffer.rewind()
            ByteArray(size).apply {
                buffer.get(this)
                shortIntToBytesPair(bitmap.width).let {
                    this[0] = it.first
                    this[1] = it.second
                }
                shortIntToBytesPair(bitmap.height).let {
                    this[2] = it.first
                    this[3] = it.second
                }
                shortIntToBytesPair(bitmap.density).let {
                    this[4] = it.first
                    this[5] = it.second
                }
            }
        } else {
            byteArrayOf()
        }
    }

    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap? {
        return if (bytes.isNotEmpty()) {
            val width = bytesPairToShortInt(bytes[0], bytes[1])
            val height = bytesPairToShortInt(bytes[2], bytes[3])
            val density = bytesPairToShortInt(bytes[4], bytes[5])
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                this.density = density
                val buffer = ByteBuffer.wrap(bytes)
                this.copyPixelsFromBuffer(buffer)
            }
        } else {
            null
        }
    }

    private fun shortIntToBytesPair(value: Int): Pair<Byte, Byte> {
        return ((value ushr 8) and 0x000000FF).toByte() to (value and 0x000000FF).toByte()
    }

    private fun bytesPairToShortInt(high: Byte, low: Byte): Int {
        return ((high.toInt() and 0x000000FF) shl 8) + (low.toInt() and 0x000000FF)
    }
}
