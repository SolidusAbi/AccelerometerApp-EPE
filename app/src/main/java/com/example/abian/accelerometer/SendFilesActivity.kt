package com.example.abian.accelerometer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_send_files.*
import java.io.File


class SendFilesActivity() : AppCompatActivity(), SendFilesInterface {

    var actionMode: ActionMode? = null
    private var filesDirectory: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    lateinit var fileAdapter: FilesAdapter

    private var PERMISSION_REQUEST = 102
    private var permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    companion object {
        var filesSelected = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_files)

        this.filesDirectory.mkdir()

        filesView.layoutManager = LinearLayoutManager(this)
        fileAdapter = FilesAdapter(filesDirectory.toString(), this)
        filesView.adapter = fileAdapter
    }

    override fun sendFilesInterface(size: Int) {
        if (actionMode == null) actionMode = startActionMode(ActionModeCallback())
        if (size > 0)
            actionMode?.title = "$size"
        else
            actionMode?.finish()
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

    // Action menu
    inner class ActionModeCallback : ActionMode.Callback {
        var shouldResetRecyclerView = true
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.getItemId()) {
                R.id.deleteAction -> {
                    shouldResetRecyclerView = false
                    fileAdapter.deleteSelectedFiles()
                    actionMode?.title = "" //remove item count from action mode.
                    actionMode?.finish()
                    return true
                }

                R.id.sendEmailAction -> {
                    val intent = Intent(Intent.ACTION_SEND_MULTIPLE)

                    val selectedFiles = fileAdapter.getSelectedActivitiesFiles()
                    val uriFiles: ArrayList<Uri> = ArrayList()

                    val filesIt = selectedFiles.listIterator()
                    while(filesIt.hasNext()){
                        val path = Uri.fromFile(filesIt.next())
                        uriFiles.add(path)
                    }


                    intent.type = "plain/text"
                    intent.putExtra(Intent.EXTRA_SUBJECT,"[EPE] Datos acelerometro")
                    intent.putExtra(Intent.EXTRA_TEXT, "Este correo contiene ficheros CSV extraídos del sensor del móvil")
                    intent.putExtra(Intent.EXTRA_STREAM, uriFiles)

                    startActivity(Intent.createChooser(intent, "Send email..."))
                }
            }
            return false
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val inflater = mode?.getMenuInflater()
            inflater?.inflate(R.menu.action_mode_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menu?.findItem(R.id.deleteAction)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            if (shouldResetRecyclerView) {
                fileAdapter?.selectedFiles?.clear()
                fileAdapter?.notifyDataSetChanged()
            }
            filesSelected = false
            actionMode = null
            shouldResetRecyclerView = true
        }
    }
}
