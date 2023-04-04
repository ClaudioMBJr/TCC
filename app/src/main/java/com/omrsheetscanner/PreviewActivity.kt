package com.omrsheetscanner

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.omrsheetscanner.databinding.ActvityPreviewBinding
import java.io.File
import java.io.FileInputStream

class PreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActvityPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActvityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bitmapFile = File(applicationContext.cacheDir, Constants.FILE_NAME)
        val inputStream = FileInputStream(bitmapFile)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        binding.preview.setImageBitmap(bitmap)
    }

    override fun onDestroy() {
        super.onDestroy()
        applicationContext.cacheDir.listFiles()?.forEach { it.delete() }
    }

}