package com.stopstone.musicplaylist.ui.signature_music

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.ActivitySignatureMusicBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignatureMusicActivity : AppCompatActivity() {
    private val binding: ActivitySignatureMusicBinding by lazy { ActivitySignatureMusicBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
