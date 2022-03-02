package com.codersth.android.foundation.filesystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codersth.android.foundation.R

class MediaStoreMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_store_main)
    }

    /**
     * 通过MediaStore api保存一张图片
     */
    fun saveImageByMediaStore() {

    }
}