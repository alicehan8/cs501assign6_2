package com.example.assign6_2

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class SensorHelper(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelValues = FloatArray(3)
    private val magnetValues = FloatArray(3)

    private val _heading = MutableStateFlow(0f)
    val heading: StateFlow<Float> = _heading

    private val _pitch = MutableStateFlow(0f)
    val pitch: StateFlow<Float> = _pitch

    private val _roll = MutableStateFlow(0f)
    val roll: StateFlow<Float> = _roll

    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    fun start() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        when (event?.sensor?.type) {

            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, accelValues, 0, 3)
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, magnetValues, 0, 3)
                updateCompass()
            }

            Sensor.TYPE_GYROSCOPE -> {
                // Orientation from gyroscope
                val gx = event.values[0]
                val gy = event.values[1]
                val gz = event.values[2]

                // Simple integration for pitch/roll (small-angle approximation)
                _pitch.value += gy * 0.02f
                _roll.value += gx * 0.02f
            }
        }
    }

    private fun updateCompass() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, accelValues, magnetValues)) {
            SensorManager.getOrientation(rotationMatrix, orientation)
            val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
            _heading.value = (azimuth + 360) % 360
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

