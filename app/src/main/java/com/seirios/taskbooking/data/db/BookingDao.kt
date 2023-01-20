package com.seirios.taskbooking.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.seirios.taskbooking.data.model.BookingItem

@Dao
interface BookingDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertBooking(bookingItem: BookingItem)

    @Query("DELETE FROM booking WHERE id = :id")
    suspend fun deleteBookedItemById(id: Int)

    @Query("SELECT * FROM booking")
    fun getAllBookings() : LiveData<List<BookingItem>>

}
