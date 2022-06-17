package com.madidulatov.viewmodelexmpl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.madidulatov.viewmodelexmpl.feed.FeedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val feedViewModel: FeedViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}