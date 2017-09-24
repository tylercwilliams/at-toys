/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.androidthings.myproject

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import com.google.android.things.pio.PeripheralManagerService

/**
 * Skeleton of the main Android Things activity. Implement your device's logic
 * in this class.

 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:

 * <pre>`PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
`</pre> *

 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.

 */
class MainActivity : Activity() {

    lateinit var periphManager: PeripheralManagerService
    lateinit var sensorManager: SensorManager
    lateinit var accelDriver: AcceleromterDriver

    val eventListener = object : SensorEventListener {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            println("accuracy changed")
        }

        override fun onSensorChanged(event: SensorEvent?) {
            val x = event?.values?.get(0)
            val y = event?.values?.get(1)
            val z = event?.values?.get(2)
            println("Sensor changed -- x: $x, y: $y, z: $z")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        periphManager = PeripheralManagerService()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelDriver = AcceleromterDriver("I2C1")

        accelDriver.register()

        sensorManager.registerDynamicSensorCallback(object : SensorManager.DynamicSensorCallback() {
            override fun onDynamicSensorConnected(sensor: Sensor) {
                if (sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    sensorManager.registerListener(eventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
                }
            }
        })

        accelDriver.test()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")

        accelDriver.unregister()
        accelDriver.close()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
