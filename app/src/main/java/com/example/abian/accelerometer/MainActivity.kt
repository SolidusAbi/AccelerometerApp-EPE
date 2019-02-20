package com.example.abian.accelerometer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.RadioGroup
import android.widget.RadioButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.UUID.randomUUID
import java.io.File
import java.io.FileWriter
import java.io.IOException


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var radioActivityGroup: RadioGroup
    private lateinit var currentActivity: RadioButton

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var sendEmailButton: Button

    private lateinit var sensorManager: SensorManager

    private val CSV_HEADER = "x,y,z"
    private lateinit var fileWriter: FileWriter

    private val PERMISSION_REQUEST = 101
    private var permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        accelerometer_data.text  = "x = ${event!!.values[0]}\n\n" +
                "y = ${event.values[1]}\n\n" +
                "z = ${event.values[2]}"

        val csvContent: String = "${event!!.values[0]},${event!!.values[1]},${event!!.values[2]}"

        this.fileWriter.append(csvContent)
        this.fileWriter.append('\n')
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.radioActivityGroup = findViewById(R.id.radioActivityGroup)
        this.startButton = findViewById(R.id.startButton)
        this.stopButton = findViewById(R.id.stopButton)
        this.sendEmailButton = findViewById(R.id.sendEmailButton)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        this.startButton.setOnClickListener{
            if (!checkPermission(this, permissions)) {
                requestPermissions(permissions, PERMISSION_REQUEST)
            } else {

                var result = ""
                var checkedValue: Int = radioActivityGroup.checkedRadioButtonId
                if (checkedValue != -1) {
                    this.currentActivity = findViewById(checkedValue)
                    this.startButton.isEnabled = false
                    this.sendEmailButton.isEnabled = false
                    this.stopButton.isEnabled = true

                    result += "Selected ${this.currentActivity.text}"
                    textView.text = result

                    sensorManager.registerListener(
                        this,
                        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_NORMAL
                    )

                    createFile(this.currentActivity.text)
                }
            }
        }

        this.stopButton.setOnClickListener{
            this.startButton.isEnabled = true
            this.sendEmailButton.isEnabled = true
            this.stopButton.isEnabled = false

            try {
                this.fileWriter.flush()
                this.fileWriter.close()
            } catch (e: IOException) {
                println("Flushing/closing error!")
                e.printStackTrace()
            }

            sensorManager.unregisterListener(this)
            accelerometer_data.text = "Hello World"
        }

        this.sendEmailButton.setOnClickListener{
            if (!checkPermission(this, permissions)) {
                requestPermissions(permissions, PERMISSION_REQUEST)
            } else {
                val intent: Intent = Intent( this, SendFilesActivity::class.java)
                this.startActivity(intent)
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST){
            var allSuccess = true
            for (i in permissions.indices){
                if (grantResults[i] == PackageManager.PERMISSION_DENIED){
                    allSuccess = false
                    var requestAgain = shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain){
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Go to Settings and enable the permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (allSuccess)
                Toast.makeText(applicationContext, "Permission granted", Toast.LENGTH_SHORT).show()
        }

    }


    private fun checkPermission(context: Context, permissionsArray: Array<String>): Boolean{
        var allSuccess = true
        for (i in permissionsArray.indices){
            if (checkCallingOrSelfPermission(permissionsArray[i]) == PackageManager.PERMISSION_DENIED) {
                allSuccess = false
                break
            }
        }
        return allSuccess
    }


    private fun createFile(activity: CharSequence){
        val uuid = randomUUID().toString()
        val replace = activity.replace("\\s".toRegex(), "")
        val filename = "${replace}_$uuid.csv"
        textView.text = Environment.getExternalStorageDirectory().toString()


        //var fileWriter : FileWriter? = null
        try {
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            path.mkdir() // Just be sure that directory exists
            val file = File(path, filename)

            this.fileWriter = FileWriter(file)
            this.fileWriter.append(CSV_HEADER)
            this.fileWriter.append('\n')

//            this.fileWriter.append("Hello world!")
//            this.fileWriter.append('\n')

            println("Write CSV successfully!")
        } catch (e: Exception) {
            println("Writing CSV error!")
            e.printStackTrace()
        }
//        finally {
//            try {
//                this.fileWriter.flush()
//                this.fileWriter.close()
//            } catch (e: IOException) {
//                println("Flushing/closing error!")
//                e.printStackTrace()
//            }
//        }
    }
}
