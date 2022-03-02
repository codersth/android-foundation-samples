package com.codersth.android.foundation.filesystem

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.codersth.android.foundation.R
import com.codersth.android.foundation.util.FileUtil
import java.io.File

/**
 * @author zhanglei at 2022/02/25
 * 测试应用间文件共享
 */
private const val TAG = "InterAppFileAccessActivity"
class InterAppFileAccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inter_app_file_access)
        rwInterApp()
    }

    /**
     * 不同系统操作操作其他应用的私有目录和内部存储
     */
    private fun rwInterApp() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val permissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (isGranted) {
                        rwInterAppUnChecked()
                    } else {
                        Log.e(TAG, "printStoragePaths: Permission not granted.")
                    }
                }
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            rwInterAppUnChecked()
        }
    }

    /**
     * 不同系统操作操作其他应用的私有目录和内部存储，无权限校验
     */
    private fun rwInterAppUnChecked() {
        // 读取当前应用私有目录的文件
//        Log.d(TAG, "onCreate:当前应用的私有目录 ${readFile("/sdcard/Android/data/com.codersth.android.foundation/files/hello.txt")}")
//        // 读取其他应用私有目录的文件（确保手机上其他应用的私有目录下有对应文件）
        Log.d(TAG, "onCreate:其他应用的私有目录 ${readFile("/storage/emulated/0/Android/data/com.example.demo/files/Download/hello.txt")}")
        // 删除其他应用私有目录的文件
        Log.d(TAG, "onCreate:其他应用的私有目录 ${File("/storage/emulated/0/Android/data/com.example.demo/files/Download/hello.txt").delete()}")

        // 读取其他应用内部存储的文件（确保手机上其他应用的内部存储下有对应文件）
        Log.d(TAG, "onCreate:其他应用的内部存储 ${readFile("/data/data/com.example.demo/files/hello.txt")}")
        // 删除其他应用内部存储的文件
        Log.d(TAG, "onCreate:其他应用的内部存储 ${File("/data/data/com.example.demo/files/hello.txt").delete()}")
    }

    /**
     * 读取指定路径下的文件内容
     * @param path 待读取文件的路径，路径可能是其他应用程序的私有目录
     * @return 文件文本内容，如果文件读取失败返回null
     */
    private fun readFile(path: String): String? {
        File(path).takeIf { it.exists() }?.also {
            return FileUtil.readFileContent(File(path))
        }
        Log.d(TAG, "readFile: $path not exists.")
        return null
    }
}