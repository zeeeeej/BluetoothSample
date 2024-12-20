package com.yunnext.angel.light

import com.yunnext.bluetooth.sample.repo.ble.applyValueAtIndex
import com.yunnext.bluetooth.sample.repo.ble.bitCheck1
import com.yunnext.bluetooth.sample.repo.ble.display
import com.yunnext.bluetooth.sample.repo.ble.parseDeviceInfo
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun applyValueAtIndex() {
        val source = 0xFF01
        val result = source.applyValueAtIndex(1,0xE)
        println("result = ${result.toHexString()}")

        assert(0xFF01.applyValueAtIndex(1,0xE) == 0xFFE1)
        assert(0xF001.applyValueAtIndex(2,0x2) == 0xF201)
        assert(0xF001.applyValueAtIndex(3,0xE) == 0xE001)
//        assert(0xF001.applyValueAtIndex(4,0xF) == 0xE001)
//        assert(0xF001.applyValueAtIndex(3,0xFf) == 0xE001)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun addition_isCorrect() {
        val text = "barcodeId"
        val payload = text.encodeToByteArray()
        println(payload.toHexString())
        println(payload.size)
        println(payload.decodeToString())

    }


    @Test
    fun `测试deviceInfoSelectNotify`() {
//        val hex ="5aa5a2008a19013131373338373632303937363535373932383131000000000502006400640503006400000504000000000505000000000306000005070000000002080105090000000105100000000005130000000005140000000005150000001905160000000408175357312e302e35051800003bd205190000000005200000453105210000095f052200000000cc"
        val hex =
            "19013131373338373632303937363535373932383131000000000502006400640503006400000504000000000505000000000306000005070000000002080105090000000105100000000005130000000005140000000005150000001905160000000408175357312e302e35051800003bd205190000000005200000453105210000095f052200000000"

        @OptIn(ExperimentalStdlibApi::class)
        val data = hex.hexToByteArray()
        val map = parseDeviceInfo(data)
        println("map=${map.display}")

    }

    @Test
    fun `测试bitCheck1`(){
        val value = byteArrayOf(0b1101)
        val isOpen = value[0].bitCheck1(0)
        val actionState = value[0].bitCheck1(1)
        val washState = value[0].bitCheck1(2)
        val adminLock = value[0].bitCheck1(3)
        println("isOpen=$isOpen")
        println("actionState=$actionState")
        println("washState=$washState")
        println("adminLock=$adminLock")
    }
}


