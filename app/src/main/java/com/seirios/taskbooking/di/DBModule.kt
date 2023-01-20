package com.seirios.taskbooking.di

import android.content.Context
import com.seirios.taskbooking.data.db.BookingDao
import com.seirios.taskbooking.data.db.DatabaseInstance
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DBModule(val context: Context) {
    @Singleton
    @Provides
    fun provideDBInstance(): DatabaseInstance {
        return  DatabaseInstance.getDatabaseInstance(context)
    }
    @Singleton
    @Provides
    fun providePostDao(databaseInstance: DatabaseInstance):BookingDao{
        return  databaseInstance.bookingdao()
    }
}