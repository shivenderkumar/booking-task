package com.seirios.taskbooking.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.seirios.taskbooking.data.model.BookingItem

@Database(entities = [BookingItem::class], version = 1, exportSchema = false)
abstract class DatabaseInstance : RoomDatabase() {
    abstract fun bookingdao(): BookingDao

    companion object{
        private var DBINTANCE : DatabaseInstance? = null
        fun getDatabaseInstance(context: Context): DatabaseInstance{
            if(DBINTANCE == null){
                synchronized(context){
                    DBINTANCE = Room.databaseBuilder(context, DatabaseInstance::class.java, "bookingdatabase")
                        .build()
                }
            }
            return DBINTANCE!!
        }
    }
}