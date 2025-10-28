package com.dhil.post_4_fadhil

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WargaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(warga: Warga)

    @Query("SELECT * FROM warga ORDER BY id DESC")
    fun getAllWarga(): LiveData<List<Warga>>

    @Query("SELECT * FROM warga WHERE id = :wargaId LIMIT 1")
    fun getWargaById(wargaId: Int): Warga?

    @Update
    fun update(warga: Warga)
    @Delete
    fun delete(warga: Warga)
    @Query("DELETE FROM warga")
    fun deleteAll()
}