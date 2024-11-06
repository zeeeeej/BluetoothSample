package com.yunnext.bluetooth.sample.repo

import android.content.Context
import android.util.Log
import com.yunnext.bluetooth.sample.domain.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserHolder(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("light_", Context.MODE_PRIVATE)

    var sToken: String
        get() = sharedPreferences.getString("sToken", "") ?: ""
        set(value) {
            sharedPreferences.edit()
                .putString("sToken", value)
                .apply()
        }

    var sUser: User?
        get() = try {
            (sharedPreferences.getString("sUser", "") ?: "").run {
                Json.decodeFromString(this)
            }
        } catch (e: Exception) {
            Log.w("UserHolder","sUser $e")
            null
        }
        set(value) {
            sharedPreferences.edit()
                .putString("sUser", Json.encodeToString(value))
                .apply()
        }


}