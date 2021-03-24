package world.shanya.serialport.tools

import java.util.*

/**
 * HexStringToString 十六进制字符串转换成字符串
 * @Author Shanya
 * @Date 2021-3-24
 * @Version 3.0.0
 */
object HexStringToString {

    /**
     * charToByte char转换成Byte
     * @param c 待转换char
     * @return 转换成功的Byte
     * @Author Shanya
     * @Date 2021-3-24
     * @Version 3.0.0
     */
    private fun charToByte(c: Char): Byte {
        return "0123456789ABCDEF".indexOf(c).toByte()
    }

    /**
     * hexStringToBytes 十六进制字符串转换成Byte数组
     * @param hexString 待转换十六进制字符串
     * @return 转换成功的Byte数组
     * @Author Shanya
     * @Date 2021-3-24
     * @Version 3.0.0
     */
    private fun hexStringToBytes(hexString: String): ByteArray? {
        var tempHexString = hexString
        if (tempHexString == "") {
            return null
        }
        tempHexString = tempHexString.replace(" ","")
        tempHexString = tempHexString.toUpperCase(Locale.ROOT)
        val length = tempHexString.length / 2
        val hexChars = tempHexString.toCharArray()
        val d = ByteArray(length)
        for (i in 0..length - 1) {
            val pos = i * 2
            d[i] = (charToByte(hexChars[pos]).toInt() shl 4 or charToByte(hexChars[pos + 1]).toInt()).toByte()
        }
        return d
    }

    /**
     * conversion 转换
     * @param hexString 待转换十六进制字符串
     * @return 转换成功的字符串
     * @Author Shanya
     * @Date 2021-3-24
     * @Version 3.0.0
     */
    fun conversion(hexString: String): String? {
        return hexStringToBytes(hexString)?.let { String(it) }
    }
}
