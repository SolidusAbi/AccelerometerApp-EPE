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
import java.io.FileWriter
import java.io.IOException
import java.util.UUID.randomUUID



class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var radioActivityGroup: RadioGroup
    lateinit var currentActivity: RadioButton

    lateinit var startButton: Button
    lateinit var stopButton: Button

    lateinit var sensorManager: SensorManager

    private val CSV_HEADER = "x,y,z"

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

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        this.startButton.setOnClickListener{
            var result = ""
            var checkedValue : Int = radioActivityGroup.checkedRadioButtonId
            if (checkedValue != -1)
            {
                this.currentActivity = findViewById(checkedValue)
                this.startButton.isEnabled = false
                this.stopButton.isEnabled = true

                result += "Selected ${this.currentActivity.text}"
                textView.text = result

                sensorManager.registerListener(
                    this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL
                )

                //createFile(this.currentActivity.text)
            }
        }

        this.stopButton.setOnClickListener{
            this.startButton.isEnabled = true
            this.stopButton.isEnabled = false

            sensorManager.unregisterListener(this)
            accelerometer_data.text = "Hello World"
        }
    }

    fun createFile(activity: CharSequence){
        val uuid = randomUUID().toString()
        val replace = activity.replace("\\s".toRegex(), "")
        val filename = "${replace}_$uuid.csv"
        textView.text = filename

//        val fileContents = "Hello world!"
        val fileWriter = FileWriter(filename)
        try {
            fileWriter.append(CSV_HEADER)
            fileWriter.append('\n')

            //for (customer in customers) {
                fileWriter.append("Hello world!")
                fileWriter.append('\n')
            //}

            println("Write CSV successfully!")
        } catch (e: Exception) {
            println("Writing CSV error!")
            e.printStackTrace()
        } finally {
            try {
                fileWriter.flush()
                fileWriter.close()
            } catch (e: IOException) {
                println("Flushing/closing error!")
                e.printStackTrace()
            }
        }
//        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
//            it.write(fileContents.toByteArray())
//        }
    }
}
