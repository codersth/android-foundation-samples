package com.codersth.android.foundation.util

import java.io.BufferedReader
import java.io.File

/**
 * Created by zhanglei1 on 25,二月,2022
 */
object FileUtil {
    /**
     * 读取文件内容
     * @return 文本内容，utf-8
     */
    fun readFileContent(file: File): String {
        return file.bufferedReader().use(BufferedReader::readText)
    }

}