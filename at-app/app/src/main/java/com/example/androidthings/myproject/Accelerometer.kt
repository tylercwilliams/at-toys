package com.example.androidthings.myproject

import com.google.android.things.pio.I2cDevice
import com.google.android.things.pio.PeripheralManagerService


class Accelerometer(bus: String, private var gscale: Int = 2) : AutoCloseable {

    companion object {
        val RESOLUTION: Int = 12
        val ADDRESS: Int = 0x1D
        val MAX_RANGE: Float = 2f
        val MAX_POWER: Float = 165f
        val MIN_FREQ_HZ: Float = 1.56f
        val MAX_FREQ_HZ: Float = 800f

        //The default full scale value range is 2g and the high-pass filter is disabled.
    }

    override fun close() {
        device = null
        pioService = null
    }

    enum class Register(val location: Int) {
        STATUS(0x00),
        OUT_X_MSB(0x01),
        OUT_X_LSB(0x02),
        OUT_Y_MSB(0x03),
        OUT_Y_LSB(0x04),
        OUT_Z_MSB(0x05),
        OUT_Z_LSB(0x06),
        WHO_AM_I(0x0D),
        XYZ_DATA_CFG(0x0E),
        CTL_REG1(0x2A) // 0 == standby, 1 == active
    }

    fun standby() =
            device?.writeRegByte(Register.CTL_REG1.location, 0) ?: throw notConnectedError

    fun active() =
            device?.writeRegByte(Register.CTL_REG1.location, 1) ?: throw notConnectedError

    fun test(): Boolean =
            device?.readRegByte(Register.WHO_AM_I.location)?.toInt() == 0x2A

    fun readSample(): FloatArray? =
            device?.let {
                val sample = ByteArray(6)
                it.readRegBuffer(Register.OUT_X_MSB.location, sample, 6)

                val translations = FloatArray(3)
                for (i in 0..2) {
                    val gcount = ((sample[i * 2].toInt() shl 8) or sample[i * 2 + 1].toInt()) shr 4
                    if (sample[i * 2] > 0x7F) translations[i] = (gcount - 0x1000).toFloat() // make negative when signed neg
                    else translations[i] = gcount.toFloat()
                }

                return translations

            } ?: throw  notConnectedError

    private var device: I2cDevice?
    private var pioService: PeripheralManagerService? = PeripheralManagerService()

    private val notConnectedError: Throwable = Throwable("Device not connected")

    init {
        device = pioService?.openI2cDevice(bus, ADDRESS)

        if (gscale > 8) gscale = 8 // 8 is max value.
        val fsr = (gscale shr 2).toByte()

        standby()
        device?.writeRegByte(Register.XYZ_DATA_CFG.location, fsr)
        active()
    }
}