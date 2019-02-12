package com.example.abian.accelerometer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup
import android.widget.RadioButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.UUID.randomUUID
import java.io.File
import java.io.FileWriter
import java.io.IOException


private const val PERMISSION_REQUEST = 101

class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var radioActivityGroup: RadioGroup
    lateinit var currentActivity: RadioButton

    lateinit var startButton: Button
    lateinit var stopButton: Button
    lateinit var sendEmailButton: Button

    lateinit var sensorManager: SensorManager

    private val CSV_HEADER = "x,y,z"

    private var permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

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
        this.sendEmailButton = findViewById(R.id.sendEmailButton)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val file = File(this.applicationContext.filesDir.toString()).listFiles()
        val prueba = file.size

        this.startButton.setOnClickListener{
            if (!checkPermission(this, permissions)) {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }

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

                createFile(this.currentActivity.text)
            }
        }

        this.stopButton.setOnClickListener{
            this.startButton.isEnabled = true
            this.stopButton.isEnabled = false

            sensorManager.unregisterListener(this)
            accelerometer_data.text = "Hello World"
        }

        this.sendEmailButton.setOnClickListener{
            //val intent: Intent = Intent( this, SendFilesActivity::class.java)
            val intent: Intent = Intent( this, SendFilesActivity::class.java)
            this.startActivity(intent)
        }
    }

    fun createFile(activity: CharSequence){
        val uuid = randomUUID().toString()
        val replace = activity.replace("\\s".toRegex(), "")
        val filename = "${replace}_$uuid.csv"
        textView.text = Environment.getExternalStorageDirectory().toString()

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            val fileContents = "Hello world!" //Prueba!!!!
        }


        var fileWriter : FileWriter? = null
        try {
            //val file = File(this.applicationContext.filesDir.toString(), filename)
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            //val path = Environment.getExternalStorageDirectory()
            val a = path.mkdir() // Just be sure that directory exists
            val file = File(path, filename)

            fileWriter = FileWriter(file)
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
                fileWriter!!.flush()
                fileWriter.close()
            } catch (e: IOException) {
                println("Flushing/closing error!")
                e.printStackTrace()
            }
        }
        textView.text = File(this.applicationContext.filesDir.toString()).listFiles().size.toString()
    }

    fun checkPermission(context: Context, permissionsArray: Array<String>): Boolean{
        var allSuccess = true
        for (i in permissionsArray.indices){
            if (checkCallingOrSelfPermission(permissionsArray[i]) == PackageManager.PERMISSION_DENIED) {
                allSuccess = false
                break
            }
        }
        return allSuccess
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
}
