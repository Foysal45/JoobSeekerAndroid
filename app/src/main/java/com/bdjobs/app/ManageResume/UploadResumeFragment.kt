package com.bdjobs.app.ManageResume


import android.os.Bundle
import android.app.Fragment
import android.content.ActivityNotFoundException
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.bdjobs.app.R
import com.facebook.FacebookSdk.getApplicationContext
import droidninja.filepicker.FilePickerBuilder
import kotlinx.android.synthetic.main.fragment_upload_resume.*
import org.jetbrains.anko.runOnUiThread
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class UploadResumeFragment : Fragment() {
    lateinit var communicator: ManageResumeCommunicator
    internal var filePaths = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_resume, container, false)

    }

    override fun onResume() {
        super.onResume()
        communicator = activity as ManageResumeCommunicator
        submitTV?.setOnClickListener {
            //communicator.gotouploaddone()
            BrowseFile()
        }

    }

    private fun BrowseFile() {
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // intent.setType("*/*");
        //intent.addCategory(Intent.CATEGORY_OPENABLE);

        /*try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    READ_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }*/
        val wordFileTypes = arrayOf(".doc", ".docx")
        val pdfFileTypes = arrayOf(".pdf")
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(filePaths)
                .enableDocSupport(false)
                .showFolderView(true)
                .addFileSupport("MS WORD FILES", wordFileTypes, R.drawable.ic_microsoft_word)
                .addFileSupport("PDF FILES", pdfFileTypes, R.drawable.ic_pdf)
                .setActivityTheme(R.style.AppTheme)
                .pickFile(activity)
    }

}
