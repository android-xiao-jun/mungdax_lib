package com.gxx.logwritelibrary.utils

import android.content.Context
import com.gxx.logwritelibrary.model.TagLogModel
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.util.Calendar

class FileUtils {
    private val fileName = getFileNameCurrent()

    companion object {
        fun getLogPathName(context: Context): String {
            return context.cacheDir.path + File.separator + "log"
        }

        fun writeToFileBlock(file: File, block: (outputStreamWriter: OutputStreamWriter) -> Unit) {
            if (!file.exists()) {
                file.createNewFile()
            }
            var fos: FileOutputStream? = null
            var outputStreamWriter: OutputStreamWriter? = null
            try {
                fos = FileOutputStream(file, true)
                outputStreamWriter = OutputStreamWriter(fos)
                block(outputStreamWriter)
                outputStreamWriter.close()
                fos.close()
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                outputStreamWriter?.close()
                fos?.close()
            }
        }

        fun createOrExistsDir(file: File?): Boolean {
            return file != null && (if (file.exists()) file.isDirectory else file.mkdirs())
        }

        fun copyFile(srcFile: File?, destFile: File?): Boolean {
            if (srcFile == null || destFile == null) return false
            if (srcFile == destFile) return false
            if (!srcFile.exists() || !srcFile.isFile) return false
            if (destFile.exists()) {
                if (!destFile.delete()) { // unsuccessfully delete then return false
                    return false
                }
            }
            if (!createOrExistsDir(destFile.parentFile)) return false
            try {
                return writeFileFromIS(destFile, FileInputStream(srcFile))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                return false
            }
        }

        private fun writeFileFromIS(
            file: File,
            `is`: InputStream
        ): Boolean {
            var os: OutputStream? = null
            try {
                os = BufferedOutputStream(FileOutputStream(file))
                val data = ByteArray(8192)
                var len: Int
                while ((`is`.read(data, 0, 8192).also { len = it }) != -1) {
                    os.write(data, 0, len)
                }
                return true
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            } finally {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    os?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }


        fun moveToOther(context: Context){
            val fileName = getFileNameCurrent()
            val filePath = File(getLogPathName(context))
            val out = context.getExternalFilesDir(null)
            val file = File(filePath, fileName)
            val fileOut = File(out, fileName)
            copyFile(file, fileOut)
        }


        private fun getFileNameCurrent(): String {
            val instance = Calendar.getInstance()
            val year = instance.get(Calendar.YEAR)
            val month = instance.get(Calendar.MONTH)
            val day = instance.get(Calendar.DAY_OF_MONTH)
            return intToStringZero(year) + intToStringZero(month + 1) + intToStringZero(day) + ".txt"
        }

        private fun intToStringZero(i: Int): String {
            return if (i >= 10) i.toString() else "0$i"
        }

    }

    fun writeLineToFile(context: Context, list: MutableList<TagLogModel>): String {
        val filePath = File(getLogPathName(context))
        val file = File(filePath, fileName)

        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        writeToFileBlock(file) { outputStreamWriter ->
            for (tagLogModel in list) {
                outputStreamWriter.write("${tagLogModel.time}:${tagLogModel.tag}--->${tagLogModel.message} \n")
            }
        }

        return file.path
    }
}