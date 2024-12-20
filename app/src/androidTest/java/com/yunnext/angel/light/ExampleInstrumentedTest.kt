package com.yunnext.angel.light

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yunnext.bluetooth.sample.repo.ble.datetimeFormat

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun t1() {
        println( "tttttt->"+datetimeFormat {
            (0xffffff *1000L).toStr()
        })
    }

    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.yunnext.angel.light", appContext.packageName)


    }
}

