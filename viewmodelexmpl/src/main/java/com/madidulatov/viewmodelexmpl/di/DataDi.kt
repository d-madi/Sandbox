package com.madidulatov.viewmodelexmpl.di

import com.madidulatov.viewmodelexmpl.feed.FeedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {

    viewModel {
        FeedViewModel()
    }
}