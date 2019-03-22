package com.example.abian.accelerometer

import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.file_row.view.*
import java.io.File
import android.graphics.Color
import android.os.Build

class FilesAdapter(filenameDirectory:String, val sendFilesInterface: SendFilesInterface): RecyclerView.Adapter<FileViewHolder>(), FileViewHolderListener{

    private val fileDirectory: String = filenameDirectory
    private var filenames: Array<File> = File(filenameDirectory).listFiles()!!
    val selectedFiles: MutableList<String> = ArrayList<String>()

    override fun onTap(index: Int) {
        if (SendFilesActivity.filesSelected)
            addFileIntoSelectedFiles(index)
    }

    override fun onLongTap(index: Int) {
        if (!SendFilesActivity.filesSelected){
            SendFilesActivity.filesSelected = true
        }

        addFileIntoSelectedFiles(index)
    }

    override fun getItemCount(): Int {
        return filenames.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FileViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val cellForRow = layoutInflater.inflate(R.layout.file_row, p0, false)
        return FileViewHolder(cellForRow, this)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val filename = filenames[position].name
        holder.view.filename.text = filename

        if (selectedFiles.contains(filename)) {
            //if item is selected then,set foreground color of FrameLayout.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.layout.foreground = ColorDrawable(0x50FF4081)
            }
        } else {
            //else remove selected item color.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.layout.foreground = ColorDrawable(Color.TRANSPARENT)
            }
        }
    }

    private fun addFileIntoSelectedFiles(idx: Int){
        val filename = filenames[idx].name
        if (selectedFiles.contains(filename))
            selectedFiles.remove(filename)
        else
            selectedFiles.add(filename)

        notifyItemChanged(idx)
        if (selectedFiles.size < 1)
            SendFilesActivity.filesSelected = false

        sendFilesInterface.sendFilesInterface(selectedFiles.size)
    }

    fun deleteSelectedFiles() {
        if (selectedFiles.size < 1)
            return

        val selectedIdIteration = selectedFiles.listIterator()

        while (selectedIdIteration.hasNext()) {
            val selectedItemID = selectedIdIteration.next()
            var indexOfModelList = 0
            //val modelListIteration: MutableListIterator<MyModel> = modelList.listIterator();
            val fileListIteration = filenames.iterator()
            while (fileListIteration.hasNext()) {
                val filename = fileListIteration.next()

                if (selectedItemID.equals(filename.name)) {
                    filename.delete()
                    notifyItemRemoved(indexOfModelList)
                }
                indexOfModelList++
            }

            SendFilesActivity.filesSelected = false
            filenames = File(this.fileDirectory).listFiles()
        }
    }

    fun getSelectedActivitiesFiles(): ArrayList<File> {
        val selectedFiles: ArrayList<File> = ArrayList()
        val selectedFilesIterator = this.selectedFiles.listIterator()

        while (selectedFilesIterator.hasNext()){
            val selectedFile =selectedFilesIterator.next()
            val filesIterator = filenames.iterator()

            while (filesIterator.hasNext()) {
                val filename = filesIterator.next()

                if (selectedFile == filename.name) {
                    selectedFiles.add(filename)
                }
            }
        }

        return selectedFiles
    }
}
