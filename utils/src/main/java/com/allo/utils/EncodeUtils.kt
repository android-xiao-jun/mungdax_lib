package com.allo.utils

import android.os.Build
import android.text.Html
import android.util.Base64
import com.allo.utils.RegexUtils.RegexConstants.REGEX_BASE_64
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder

object EncodeUtils{


    /**
     * Return the urlencoded string.
     *
     * @param input       The input.
     * @param charsetName The name of charset.
     * @return the urlencoded string
     */
    /**
     * Return the urlencoded string.
     *
     * @param input The input.
     * @return the urlencoded string
     */
    @JvmOverloads
    @JvmStatic
    fun urlEncode(input: String?, charsetName: String = "UTF-8"): String {
        return if (input.isNullOrEmpty()) "" else try {
            URLEncoder.encode(input, charsetName)
        } catch (e: UnsupportedEncodingException) {
            throw AssertionError(e)
        }
    }
    /**
     * Return the string of decode urlencoded string.
     *
     * @param input       The input.
     * @param charsetName The name of charset.
     * @return the string of decode urlencoded string
     */
    /**
     * Return the string of decode urlencoded string.
     *
     * @param input The input.
     * @return the string of decode urlencoded string
     */
    @JvmOverloads
    @JvmStatic
    fun urlDecode(input: String?, charsetName: String? = "UTF-8"): String {
        return if (input.isNullOrEmpty()) "" else try {
            val safeInput = input.replace("%(?![0-9a-fA-F]{2})".toRegex(), "%25")
                .replace("\\+".toRegex(), "%2B")
            URLDecoder.decode(safeInput, charsetName)
        } catch (e: UnsupportedEncodingException) {
            throw AssertionError(e)
        }
    }

    /**
     * Return Base64-encode bytes.
     *
     * @param input The input.
     * @return Base64-encode bytes
     */
    @JvmStatic
    fun base64Encode(input: String): ByteArray {
        return base64Encode(input.toByteArray())
    }

    /**
     * Return Base64-encode bytes.
     *
     * @param input The input.
     * @return Base64-encode bytes
     */
    @JvmStatic
    fun base64Encode(input: ByteArray?): ByteArray {
        return if (input == null || input.isEmpty()) ByteArray(0) else Base64.encode(
            input,
            Base64.NO_WRAP
        )
    }

    /**
     * Return Base64-encode string.
     *
     * @param input The input.
     * @return Base64-encode string
     */
    @JvmStatic
    fun base64Encode2String(input: ByteArray?): String {
        return if (input == null || input.isEmpty()) "" else Base64.encodeToString(
            input,
            Base64.NO_WRAP
        )
    }

    /**
     * Return the bytes of decode Base64-encode string.
     *
     * @param input The input.
     * @return the string of decode Base64-encode string
     */
    @JvmStatic
    fun base64Decode(input: String?): ByteArray {
        return if (input.isNullOrEmpty()) ByteArray(0) else Base64.decode(
            input,
            Base64.NO_WRAP
        )
    }

    /**
     * Return the bytes of decode Base64-encode bytes.
     *
     * @param input The input.
     * @return the bytes of decode Base64-encode bytes
     */
    @JvmStatic
    fun base64Decode(input: ByteArray?): ByteArray {
        return if (input == null || input.isEmpty()) ByteArray(0) else Base64.decode(
            input,
            Base64.NO_WRAP
        )
    }

    /**
     * Return html-encode string.
     *
     * @param input The input.
     * @return html-encode string
     */
    @JvmStatic
    fun htmlEncode(input: CharSequence?): String {
        if (input.isNullOrEmpty()) return ""
        val sb = StringBuilder()
        var c: Char
        var i = 0
        val len = input.length
        while (i < len) {
            c = input[i]
            when (c) {
                '<' -> sb.append("&lt;") //$NON-NLS-1$
                '>' -> sb.append("&gt;") //$NON-NLS-1$
                '&' -> sb.append("&amp;") //$NON-NLS-1$
                '\'' ->                     //http://www.w3.org/TR/xhtml1
                    // The named character reference &apos; (the apostrophe, U+0027) was
                    // introduced in XML 1.0 but does not appear in HTML. Authors should
                    // therefore use &#39; instead of &apos; to work as expected in HTML 4
                    // user agents.
                    sb.append("&#39;") //$NON-NLS-1$
                '"' -> sb.append("&quot;") //$NON-NLS-1$
                else -> sb.append(c)
            }
            i++
        }
        return sb.toString()
    }

    /**
     * Return the string of decode html-encode string.
     *
     * @param input The input.
     * @return the string of decode html-encode string
     */
    @JvmStatic
    fun htmlDecode(input: String?): CharSequence {
        if (input.isNullOrEmpty()) return ""
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(input)
        }
    }

    /**
     * Return the binary encoded string padded with one space
     *
     * @param input The input.
     * @return binary string
     */
    @JvmStatic
    fun binaryEncode(input: String?): String {
        if (input.isNullOrEmpty()) return ""
        val sb = StringBuilder()
        for (i in input.toCharArray()) {
            sb.append(Integer.toBinaryString(i.code)).append(" ")
        }
        return sb.deleteCharAt(sb.length - 1).toString()
    }

    /**
     * Return UTF-8 String from binary
     *
     * @param input binary string
     * @return UTF-8 String
     */
    @JvmStatic
    fun binaryDecode(input: String?): String {
        if (input.isNullOrEmpty()) return ""
        val splits = input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val sb = StringBuilder()
        for (split in splits) {
            sb.append(split.toInt(2).toChar())
        }
        return sb.toString()
    }

    /**
     * Return the is the input was encode by base64
     * @param input The input
     * @return bool
     */
    fun isBase64(input: String?): Boolean {
        return input?.matches(Regex(REGEX_BASE_64)) == true
    }
}