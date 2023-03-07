package com.omrsheetscanner

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.omrsheetscanner.databinding.ActvityPreviewBinding

class PreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActvityPreviewBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActvityPreviewBinding.inflate(layoutInflater)

        binding.preview.setImageBitmap(intent.getParcelableExtra("bm", Bitmap::class.java))
    }
}