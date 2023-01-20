package com.seirios.taskbooking.di

import com.seirios.taskbooking.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DBModule::class])
interface ApplicationComponent {
    fun inject(mainActivity: MainActivity)
}