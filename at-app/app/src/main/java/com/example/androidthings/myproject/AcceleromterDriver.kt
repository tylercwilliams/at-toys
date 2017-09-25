package com.example.androidthings.myproject

import android.hardware.Sensor
import android.hardware.SensorManager
import com.google.android.things.userdriver.UserDriverManager
import com.google.android.things.userdriver.UserSensor
import com.google.android.things.userdriver.UserSensorDriver
import com.google.android.things.userdriver.UserSensorReading
import java.util.*


class AcceleromterDriver(bus: String) : AutoCloseable {

    companion object {
        private val NAME: String = "mma8452qt"
        private val VENDOR: String = "xtrinsic"
        private val VERSION: Int = 1
        private val MAX_RANGE = Accelerometer.MAX_RANGE * SensorManager.GRAVITY_EARTH
        private val POWER = Accelerometer.MAX_POWER * 1000f
        private val RESOLUTION = Accelerometer.RESOLUTION * 32f
        private val MIN_DELAY = Math.round(1000000f / Accelerometer.MAX_FREQ_HZ)
        private val MAX_DELAY = Math.round(1000000f / Accelerometer.MIN_FREQ_HZ)

        fun build(device: Accelerometer): UserSensor =
                UserSensor.Builder()
                        .setType(Sensor.TYPE_ACCELEROMETER)
                        .setName(NAME)
                        .setVendor(VENDOR)
                        .setVersion(VERSION)
                        .setMaxRange(MAX_RANGE)
                        .setPower(POWER)
                        .setResolution(RESOLUTION)
                        .setMinDelay(MIN_DELAY)
                        .setMaxDelay(MAX_DELAY)
                        .setRequiredPermission("")
                        .setUuid(UUID.randomUUID())
                        .setDriver(object : UserSensorDriver() {
                            override fun read(): UserSensorReading =
                                    UserSensorReading(
                                            device.readSample(),
                                            SensorManager.SENSOR_STATUS_ACCURACY_HIGH)

                            override fun setEnabled(enabled: Boolean) =
                                    if (enabled) {
                                        device.active()
                                    } else device.standby()
                        })
                        .build()
    }

    fun register() {
        sensor = build(device ?: throw Throwable("No device to register"))
        UserDriverManager.getManager().registerSensor(sensor)
    }

    fun unregister() {
        sensor?.let {
            UserDriverManager.getManager().unregisterSensor(sensor)
            sensor = null
        }
    }

    fun test() =
            if (device?.test() ?: throw  Throwable("No device to test")) println("Online")
            else println("Offline")

    override fun close() {
        device?.close()
        device = null
    }

    private var sensor: UserSensor? = null
    private var device: Accelerometer? = Accelerometer(bus)
}