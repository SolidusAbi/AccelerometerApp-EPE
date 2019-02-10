package com.example.abian.accelerometer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_send_files.*

interface SendFilesInterface {
    fun sendFilesInterface (size : Int)
}

class SendFilesActivity : AppCompatActivity(), SendFilesInterface {

    var actionMode: ActionMode? = null
    lateinit var fileAdapter: FilesAdapter

    companion object {
        var filesSelected = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_files)

        filesView.layoutManager = LinearLayoutManager(this)
        fileAdapter = FilesAdapter(this.applicationContext.filesDir.toString(), this)
        filesView.adapter = fileAdapter
    }

    override fun sendFilesInterface(size: Int) {
        if (actionMode == null) actionMode = startActionMode(ActionModeCallback())
        if (size > 0)
            actionMode?.setTitle("$size")
        else
            actionMode?.finish()
    }

    // Action menu
    inner class ActionModeCallback : ActionMode.Callback {
        var shouldResetRecyclerView = true
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.getItemId()) {
                R.id.deleteAction -> {
                    shouldResetRecyclerView = false
                    fileAdapter?.deleteSelectedFiles()
                    actionMode?.title = "" //remove item count from action mode.
                    actionMode?.finish()
                    return true
                }

                R.id.sendEmailAction -> {
                    TODO()
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
