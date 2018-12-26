package com.example.abian.accelerometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.RadioButton
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var radioActivityGroup: RadioGroup
    lateinit var currentActivity: RadioButton

    lateinit var startButton: Button
    lateinit var stopButton: Button

    lateinit var sensorManager: SensorManager

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        accelerometer_data.text  = "x = ${event!!.values[0]}\n\n" +
                "y = ${event.values[1]}\n\n" +
                "z = ${event.values[2]}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.radioActivityGroup = findViewById(R.id.radioActivityGroup)
        this.startButton = findViewById(R.id.startButton)
        this.stopButton = findViewById(R.id.stopButton)

        this.startButton.setOnClickListener{
            var result = ""
            var checkedValue : Int = radioActivityGroup.checkedRadioButtonId
            if (checkedValue != -1)
            {
                this.currentActivity = findViewById(checkedValue)
                this.startButton.isEnabled = false
                this.stopButton.isEnabled = true

                result += "Selected ${this.currentActivity.text}"
                /*if (findViewById<RadioButton>(R.id.radioButton4).isChecked)
                    result += radioActivityGroup.checkedRadioButtonId
                if (findViewById<RadioButton>(R.id.radioButton5).isChecked)
                    result += 2*/

                textView.text = result
            }
        }

        this.stopButton.setOnClickListener{
            this.startButton.isEnabled = true
            this.stopButton.isEnabled = false
        }


        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )

        /*val filename = "myfile"
        val fileContents = "Hello world!"
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }*/
    }
}
