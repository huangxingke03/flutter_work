package com.example.flutter_work

import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    val C_NAME = "com.example.flutter_work/getBatteryLevel"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        findViewById<Button>(R.id.button1).setOnClickListener {
//         val batteryLevel = getBatteryLevel()
//          Log.d("电量","-------$batteryLevel")
//        }
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        val methodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, C_NAME)
        methodChannel.setMethodCallHandler { call, result ->
            if (call.method == "getBatteryLevel") {
                val batteryLevel = getBatteryLevel()
                if (batteryLevel != -1) {
                    result.success(batteryLevel)
                } else {
                    result.error("UNAVAILABLE", "Battery level not available.", null)
                }
            } else {
                result.notImplemented()
            }
        }
    }

    /**
     * 获取电量
     */
    private fun getBatteryLevel(): Int {
        val batteryLevel: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            (getSystemService(BATTERY_SERVICE) as BatteryManager).also {
                batteryLevel = it.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            }
        } else {
            val intent = ContextWrapper(applicationContext).registerReceiver(
                null, IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED
                )
            )
            batteryLevel =
                intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(
                    BatteryManager.EXTRA_SCALE,
                    -1
                )
        }
        return batteryLevel
    }

}
