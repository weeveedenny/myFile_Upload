package net.myFile_Upload.upload.task2

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileInputStream
import java.io.FileOutputStream
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.widget.Button
import java.io.File


class MainActivity : AppCompatActivity(), ImageUploadRequestBody.UploadCallback {

    private var selectedImageUri: Uri? = null
    private lateinit var uploadText: TextView
    private val PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE= 0
    private lateinit var reqButton: Button
    private lateinit var reqText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        activity_imageview.setOnClickListener {
            requestWritePermission()
        }

        upload_button.setOnClickListener {
            uploadImage()
        }
    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_PICK_IMAGE -> {
                    selectedImageUri = data?.data
                    activity_imageview.setImageURI(selectedImageUri)

                    uploadText = findViewById(R.id.activity_uploadImage_textview)
                    uploadText.visibility = View.GONE

                     reqText = findViewById<TextView>(R.id.activity_request_textview)
                    reqText.visibility = View.GONE

                    reqButton = findViewById(R.id.activity_request_button)
                    reqButton.visibility = View.GONE
                }
            }
        }
    }

    private fun uploadImage() {
        if (selectedImageUri == null) {
            toast(this@MainActivity, "Please select an Image")
            return
        }


        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(selectedImageUri!!, "r", null) ?: return

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(selectedImageUri!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        progress_bar.progress = 0
        val body = ImageUploadRequestBody(file, "image", this)
        ImageUploadAPI().uploadImage(
            MultipartBody.Part.createFormData(
                "image",
                file.name,
                body
            ),
            RequestBody.create(MediaType.parse("multipart/form-data"), "json")
        ).enqueue(object : Callback<ImageUploadResponse> {
            override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {

                t.message?.let {
                 //   toast(this@MainActivity, it)
                }
                progress_bar.progress = 0
            }

            override fun onResponse(
                call: Call<ImageUploadResponse>,
                response: Response<ImageUploadResponse>
            ) {
                progress_bar.progress = 100
               toast(this@MainActivity, "Successfully Uploaded Photo")


            }
        })

    }

    override fun onProgressUpdate(percentage: Int) {
        progress_bar.progress = percentage

    }

    companion object {
        const val REQUEST_CODE_PICK_IMAGE = 101
    }




    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            // Request for Storage permission.
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //my code
                openImageChooser()
            } else {
                // Permission request was denied.
                val reqText = findViewById<TextView>(R.id.activity_request_textview)
                reqText.visibility = View.VISIBLE
                reqButton = findViewById(R.id.activity_request_button)
                reqButton.visibility = View.VISIBLE
                reqButton.setOnClickListener {
                    requestWritePermission()
                }

            }
        }

    }




    private fun requestWritePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE)

        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE)
        }
    }


}







