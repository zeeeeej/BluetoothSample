package com.yunnext.bluetooth.sample.repo.ble

import com.yunnext.bluetooth.sample.repo.ble.BleHelper.PREFIX
import com.yunnext.bluetooth.sample.repo.hdMD5

internal object Protocol {

    const val MTU = 512
    const val UUID_SERVICE = "616e6765-6c62-6c70-6573-657276696365"
    const val UUID_CH_WRITE = "616e6765-6c62-6c65-7365-6e6463686172"
    const val UUID_CH_NOTIFY = "616e6765-6c62-6c65-6e6f-746964796368"
    const val ACCESSKEY = "q1nejnnbdzownwsl"
    val HEAD by lazy {
        byteArrayOf(0x5a, 0xa5.toByte())
    }


     fun parseFromBroadcast(name: String): String? {
        return if (name.startsWith(PREFIX)) {
            name.substring(PREFIX.length, name.length)
        } else null
    }

    private fun createAuthentication(accessKey: String, dest: String): String? {
        return hdMD5("#${dest}#$accessKey")?.lowercase()
    }

     fun authenticationWrite(accessKey: String, dest: String): ProtocolData {
        val authentication = createAuthentication(accessKey, dest) ?: ""
        println("accessKey:$accessKey ,dest:$dest ,authentication:$authentication <>${authentication.length}")
        check(authentication.isNotEmpty()) {
            "authentication为空. accessKey:$accessKey dest:$dest"
        }
        @OptIn(ExperimentalStdlibApi::class)
        return rtcData(ProtocolCmd.AuthWrite, authentication.hexToByteArray())
    }

     fun rtcData(cmd: ProtocolCmd, payload: ByteArray): ProtocolData {
        val head = Protocol.HEAD
        val cmdData = cmd.cmd
        val length = payload.size.toByteArray2()
        val total = (head + cmdData + payload + length).sum()
        val crc = (total % 256).toByte()
        return ProtocolData(
            head = head,
            cmd = cmdData,
            payload = payload,
            length = length,
            crc = crc
        )
    }

    private fun Int.toByteArray2(): ByteArray {
        val byteArray = ByteArray(2)
        byteArray[0] = (this shr 8 and 0xFF).toByte() // 取高8位
        byteArray[1] = (this and 0xFF).toByte()       // 取低8位
        return byteArray
    }

}

