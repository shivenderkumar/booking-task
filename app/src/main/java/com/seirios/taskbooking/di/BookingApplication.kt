package com.seirios.taskbooking.di

import android.app.Application

class BookingApplication : Application(){
    lateinit var applicationComponent: ApplicationComponent
    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.builder().dBModule(DBModule(applicationContext)).build()
    }
}