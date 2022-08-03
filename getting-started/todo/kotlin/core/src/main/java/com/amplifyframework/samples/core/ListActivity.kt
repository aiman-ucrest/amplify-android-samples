package com.amplifyframework.samples.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.samples.core.databinding.ActivityListBinding

abstract class ListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.floatingActionButton.setOnClickListener {
            fabAction()
        }

    }

    abstract fun fabAction()
}
