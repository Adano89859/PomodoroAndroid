package com.example.pomodoro.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import kotlin.math.sin

class AudioGenerator(private val context: Context) {

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val DURATION_SECONDS = 10
    }

    fun generateFocusMusic(): Uri? {
        return generateTone(440.0, "focus_music.wav") // Tono A4 (La)
    }

    fun generateBreakMusic(): Uri? {
        return generateTone(523.25, "break_music.wav") // Tono C5 (Do)
    }

    private fun generateTone(frequency: Double, filename: String): Uri? {
        try {
            val numSamples = DURATION_SECONDS * SAMPLE_RATE
            val samples = DoubleArray(numSamples)
            val buffer = ByteArray(2 * numSamples)

            // Generar onda sinusoidal
            for (i in samples.indices) {
                samples[i] = sin(2.0 * Math.PI * i / (SAMPLE_RATE / frequency))
            }

            // Convertir a bytes (16-bit PCM)
            var idx = 0
            for (sample in samples) {
                val value = (sample * 32767).toInt().toShort()
                buffer[idx++] = (value.toInt() and 0x00ff).toByte()
                buffer[idx++] = ((value.toInt() and 0xff00) ushr 8).toByte()
            }

            // Guardar como archivo WAV
            val file = File(context.cacheDir, filename)
            val fos = FileOutputStream(file)

            // Escribir header WAV
            writeWavHeader(fos, buffer.size)
            fos.write(buffer)
            fos.close()

            return Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun writeWavHeader(out: FileOutputStream, dataSize: Int) {
        val header = ByteArray(44)

        val totalDataLen = dataSize + 36
        val byteRate = SAMPLE_RATE * 2

        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1
        header[21] = 0
        header[22] = 1
        header[23] = 0
        header[24] = (SAMPLE_RATE and 0xff).toByte()
        header[25] = ((SAMPLE_RATE shr 8) and 0xff).toByte()
        header[26] = ((SAMPLE_RATE shr 16) and 0xff).toByte()
        header[27] = ((SAMPLE_RATE shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = 2
        header[33] = 0
        header[34] = 16
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (dataSize and 0xff).toByte()
        header[41] = ((dataSize shr 8) and 0xff).toByte()
        header[42] = ((dataSize shr 16) and 0xff).toByte()
        header[43] = ((dataSize shr 24) and 0xff).toByte()

        out.write(header, 0, 44)
    }
}