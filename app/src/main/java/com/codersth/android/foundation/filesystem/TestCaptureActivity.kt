package com.codersth.android.foundation.filesystem

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.codersth.android.foundation.R
import kotlinx.android.synthetic.main.activity_test_capture.*
import java.io.File
import java.io.IOException
import android.content.ClipData




/**
 * @author zhanglei at 2022/02/11
 * Test FileProvider by a capture demo.That is, trigger a camera and display the image by the returned.
 */
class TestCaptureActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "TestCaptureActivity"

        /**
         * An ActivityResultContract to take a picture saving it into the provided content-Uri.Instead of using
         * the official [ActivityResultContracts.TakePicture], this support all of android versions.
         */
        class SupportTakePicture: ActivityResultContract<Uri, Boolean>() {
            override fun createIntent(context: Context, input: Uri?): Intent {
//                return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                        .putExtra(MediaStore.EXTRA_OUTPUT, input)
//                } else {
//                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).addFlags(
//                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//                    ).apply {
//                        clipData = ClipData.newRawUri("", input)
//                    }
//                }
                return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    .putExtra(MediaStore.EXTRA_OUTPUT, input)
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
                return Activity.RESULT_OK == resultCode
            }
        }
    }

    /**
     * Uri for capture to save.
     */
    private var mCaptureUri: Uri? = null

    private val mCaptureLauncher = registerForActivityResult(SupportTakePicture()) { success ->
        if(success) {
            Log.d(TAG, "dispatchTakePictureIntent: uri = $mCaptureUri")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_capture)
        capture.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    /**
     * Call camera component.
     */
    private fun dispatchTakePictureIntent() {
        var uri = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                this, "com.codersth.android.foundation.fileprovider", createFile()
            )
        } else {
            Uri.fromFile(createFile())
        }
        uri = Uri.fromFile(createFile())
        mCaptureLauncher.launch(uri)
        mCaptureUri = uri
    }

    /**
     * Create a temp file with name of current time.
     */
    @Throws(IOException::class)
    private fun createFile(): File {
        return File.createTempFile("${System.currentTimeMillis()}", ".txt", getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS))
    }
}