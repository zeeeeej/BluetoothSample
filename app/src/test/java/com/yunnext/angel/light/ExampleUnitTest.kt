package com.yunnext.angel.light

import com.yunnext.angel.light.repo.ble.bitCheck1
import com.yunnext.angel.light.repo.ble.parseDeviceInfo
import com.yunnext.angel.light.repo.ble.display
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
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