package com.n.quickestnotes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Environment
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.lang.Exception

// leaving as comments because I can't be bothered to show this in-app
// user has to give storage permission manually
// user has to make sure Active folder exists

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main)
    }

    var editMode = false

    @ExperimentalStdlibApi
    override fun onResume() {
        super.onResume()

        var editText = findViewById<EditText>(R.id.editTextTextPersonName)
        editText.requestFocus()

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        editText.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                var path = Environment.getExternalStorageDirectory()
                val file = File(path, "Active/quickestnotes.txt")
                val myText = editText.text.toString()

                when {
                    myText.lowercase() == "copy" -> {
                        copyFileToClipboard(file)
                    }
                    myText.lowercase() == "clear" -> {
                        clearFileContent(file)
                    }
                    myText.lowercase() == "view" -> {
                        displayFileContent(file, editText)
                    }
                    else -> {
                        writeLineToFile(file, myText)
                    }
                }

                true
            } else false
        }
    }

    private fun writeLineToFile(file: File, myText: String) {
        val c: Calendar = Calendar.getInstance()
        val df = SimpleDateFormat("MMMM-dd HH:mm")
        val formatDate: String = df.format(c.getTime())

        if (editMode)
            file.writeText(myText) //this doesn't currently work because the enter action is hidden
        else
            file.appendText("$formatDate $myText\n")

        done("Note written!")
    }

    private fun displayFileContent(file: File, editText: EditText) {
        try {
            var text = file.readText()

            editText.isSingleLine = false
            editMode = true

            editText.setText(text)
        } catch (e: Exception){
            toast("Empty file!")
        }

    }

    private fun clearFileContent(file: File) {
        AlertDialog.Builder(this)
            .setTitle("Clear File?")
            .setMessage("Do you really want to empty the file?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener { dialog, whichButton ->
                    file.delete()
                    done("Cleared file")
                })
            .setNegativeButton(android.R.string.no, null).show()
    }

    private fun copyFileToClipboard(file: File) {
        var text = file.readText()

        val clipboard =
            getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Message", text)
        clipboard.setPrimaryClip(clip)

        done("Copied to clipboard!")
    }

    private fun done(msg: String){
        toast(msg)
        finish()
    }

    private fun toast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}