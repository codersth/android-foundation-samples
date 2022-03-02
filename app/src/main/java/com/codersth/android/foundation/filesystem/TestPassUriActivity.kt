package com.codersth.android.foundation.filesystem

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.codersth.android.foundation.R
import kotlinx.android.synthetic.main.activity_test_pass_uri.*
import java.io.BufferedReader
import java.io.File
import java.io.IOException

/**
 * Test if we can pass an uri parameter to [TestCaptureActivity]
 */
class TestPassUriActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_pass_uri)
        button.setOnClickListener {
            startActivity(Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                var uri = FileProvider.getUriForFile(
                    this@TestPassUriActivity, "com.codersth.android.foundation.fileprovider", createFile()
                )
                uri = Uri.fromFile(createFile())
                data = uri
//                putExtra(MediaStore.EXTRA_OUTPUT, uri)
            })
        }
        // Detect existence of files in other app.
        val pkg = "com.example.demo"
        val dir = "/data/data/${pkg}/files/"
        val file = File(dir, "testread.txt")
//        Log.d("=====", "onCreate: file exist: ${File(dir).exists()}}")
//        Log.d("=====", "onCreate: read file: ${readFileContent(file)}")
        val dataDir = File("/data/data/com.example.demo")
        Log.d("=====", "onCreate: dataDir exist ${dataDir.exists()}")
        dataDir.listFiles().forEach {
            Log.d("=====", "onCreate: $it")
        }

    }

    /**
     * 读取文件内容
     * @return 文本内容，utf-8
     */
    private fun readFileContent(file: File): String {
        return file.bufferedReader().use(BufferedReader::readText)
    }


    /**
     * Create a temp file with name of current time.
     */
    @Throws(IOException::class)
    private fun createFile(): File {
        return File.createTempFile("${System.currentTimeMillis()}", ".txt", getExternalFilesDir(
            Environment.DIRECTORY_DOWNLOADS))
    }
}