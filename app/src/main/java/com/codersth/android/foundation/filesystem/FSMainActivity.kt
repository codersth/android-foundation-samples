package com.codersth.android.foundation.filesystem

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.codersth.android.foundation.R
import com.codersth.android.foundation.util.FileUtil
import java.io.BufferedReader
import java.io.File

/**
 * @author zhanglei at 2022/02/10
 * 测试不同版本下目录路径输出、读写文件。
 */
class FSMainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "FSMainActivity"
        /**
         * 控制存储路径的打印输出
         */
        private const val PRINT_INTERNAL_VOLUME = 1 shl 1
        private const val PRINT_EXTERNAL_VOLUME_SPECIFIC = 1 shl 2
        private const val PRINT_EXTERNAL_VOLUME_SHARE = 1 shl 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fsmain)
//        printStoragePaths(PRINT_INTERNAL_VOLUME)
//        readStorage(PRINT_INTERNAL_VOLUME or PRINT_EXTERNAL_VOLUME_SPECIFIC or PRINT_EXTERNAL_VOLUME_SHARE)
        writeStorage(PRINT_INTERNAL_VOLUME or PRINT_EXTERNAL_VOLUME_SPECIFIC or PRINT_EXTERNAL_VOLUME_SHARE)
    }

    /**
     * 打印内存存储、外部存储（私有目录）及外部存储（共享目录）的路径，会校验权限
     */
    private fun printStoragePaths(pathTypes: Int) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (isGranted) {
                        printStoragePathsUnChecked(pathTypes)
                    } else {
                        Log.e(TAG, "printStoragePaths: Permission not granted.")
                    }
                }
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            printStoragePathsUnChecked(pathTypes)
        }
    }

    /**
     * 打印内存存储、外部存储（私有目录）及外部存储（共享目录）的路径，不会校验权限
     * 内存存储：Context#getFilesDir，Context#getCacheDir
     * 外部存储（私有目录）：Context#getExternalFilesDir，Context#getExternalCacheDir
     * 外部存储（共享目录）：
     * @param pathTypes 打印的路径类型
     * @see [PRINT_EXTERNAL_VOLUME_SHARE][PRINT_EXTERNAL_VOLUME_SPECIFIC][PRINT_EXTERNAL_VOLUME_SHARE]
     */
    private fun printStoragePathsUnChecked(pathTypes: Int) {
        val appContext = this
        val paths = StringBuilder().apply {
            if (PRINT_INTERNAL_VOLUME and pathTypes != 0) {
                append("\n${getString(R.string.internal_storage_volume)}: ${appContext.filesDir}").append(
                    "\t${appContext.cacheDir}"
                )
            }
            if (PRINT_EXTERNAL_VOLUME_SPECIFIC and pathTypes != 0) {
                append(
                    "\n${getString(R.string.external_storage_volume_specific)}: ${
                        appContext.getExternalFilesDir(
                            Environment.DIRECTORY_MUSIC
                        )
                    }"
                ).append("\t${appContext.externalCacheDir}")
            }
            if (PRINT_EXTERNAL_VOLUME_SHARE and pathTypes != 0) {
                append("\n${getString(R.string.external_storage_volume_share)}:${Environment.getExternalStorageDirectory()}")
            }
        }
        Log.d(TAG, paths.toString())
    }

    /**
     * 在不同的目录下读取文件, 校验权限。
     * @see readStorageUnChecked
     */
    private fun readStorage(pathTypes: Int) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (isGranted) {
                        readStorageUnChecked(pathTypes)
                    } else {
                        Log.e(TAG, "printStoragePaths: Permission not granted.")
                    }
                }
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            readStorageUnChecked(pathTypes)
        }
    }
    /**
     * 在不同的目录下读取文件, 不校验权限。注意：先放入对应文件。
     * @param pathTypes 打印的路径类型
     * @see [PRINT_EXTERNAL_VOLUME_SHARE][PRINT_EXTERNAL_VOLUME_SPECIFIC][PRINT_EXTERNAL_VOLUME_SHARE]
     */
    private fun readStorageUnChecked(pathTypes: Int) {
        // 分别在指定目录放一个此名称的文件
        val fileName = "testread.txt"
        if (PRINT_INTERNAL_VOLUME and pathTypes != 0) {
            val file = File(filesDir, fileName)
            Log.d(TAG, "${getString(R.string.internal_storage_volume)}: read file content: ${FileUtil.readFileContent(file)}")
        }
        if (PRINT_EXTERNAL_VOLUME_SPECIFIC and pathTypes != 0) {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
            Log.d(TAG, "${getString(R.string.external_storage_volume_specific)}: read file content: ${FileUtil.readFileContent(file)}")
        }
        if (PRINT_EXTERNAL_VOLUME_SHARE and pathTypes != 0) {
            val file = File(Environment.getExternalStorageDirectory(), fileName)
            Log.d(TAG, "${getString(R.string.external_storage_volume_share)}: read file content: ${FileUtil.readFileContent(file)}")
        }
    }

    /**
     * 在不同的目录下写入文件, 校验权限。
     * @see readStorageUnChecked
     */
    private fun writeStorage(pathTypes: Int) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val permissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (isGranted) {
                        writeStorageUnChecked(pathTypes)
                    } else {
                        Log.e(TAG, "printStoragePaths: Permission not granted.")
                    }
                }
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 判断当前应用是否管理外部存储的权限
            if(Environment.isExternalStorageManager()) {
                writeStorageUnChecked(pathTypes)
            } else {
                // 赋予权限
                val forResultLauncher = registerForActivityResult(ManagerStorageContract()
                ) {
                    if (Environment.isExternalStorageManager()) {
                        writeStorageUnChecked(pathTypes)
                    } else {
                        Toast.makeText(this, "Permission not granted.", Toast.LENGTH_SHORT).show();
                    }
                }
                forResultLauncher.launch(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            }
        } else {
            writeStorageUnChecked(pathTypes)
        }
    }

    /**
     * 在不同的目录下创建文件, 不校验权限
     * @param pathTypes 打印的路径类型
     * @see [PRINT_EXTERNAL_VOLUME_SHARE][PRINT_EXTERNAL_VOLUME_SPECIFIC][PRINT_EXTERNAL_VOLUME_SHARE]
     */
    private fun writeStorageUnChecked(pathTypes: Int) {
        if (PRINT_INTERNAL_VOLUME and pathTypes != 0) {
            val file = File(filesDir, "${System.currentTimeMillis()}.txt")
            Log.d(TAG, "${getString(R.string.internal_storage_volume)}: create file ${file.createNewFile()}, file path: ${file.absolutePath}")
        }
        if (PRINT_EXTERNAL_VOLUME_SPECIFIC and pathTypes != 0) {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "${System.currentTimeMillis()}.txt")
            Log.d(TAG, "${getString(R.string.external_storage_volume_specific)}: create file ${file.createNewFile()}, file path: ${file.absolutePath}")
        }
        if (PRINT_EXTERNAL_VOLUME_SHARE and pathTypes != 0) {
            val file = File(Environment.getExternalStorageDirectory(), "${System.currentTimeMillis()}.txt")
            Log.d(TAG, "${getString(R.string.external_storage_volume_share)}: create file ${file.createNewFile()}, file path: ${file.absolutePath}")
        }
    }

    /**
     * 通过Media Api保存图片
     */
    private fun savePic() {
        val resultUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "pic_${System.currentTimeMillis()}.jpg")
            }
        )
        Log.d(TAG, "savePic: resultUri = $resultUri")
    }

    /**
     * Android R上申请共享目录权限
     */
    @RequiresApi(Build.VERSION_CODES.R)
    class ManagerStorageContract: ActivityResultContract<String, Void>() {
        override fun createIntent(context: Context, input: String): Intent {
            val intent = Intent(input)
//            intent.addCategory("android.intent.category.DEFAULT")
            intent.data =
                Uri.parse(String.format("package:${context.packageName}"))
            return intent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Void? {
            // 不需要知道结果，直接返回空即可。
            return null
        }
    }
}