package com.dev_candra.todolistapp.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

// Membuat entity dari DAO
// membuat nama tabel entity
@Entity(tableName = "tabel_task")
@Parcelize
data class Task(
    val name: String,
    val important: Boolean = false,
    val completed: Boolean = false,
    // mengambil berdasarkna detik
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
): Parcelable{
    val createdFormatted: String
    // mengambil sebuah format tanggal dan waktu
    get() = DateFormat.getDateTimeInstance().format(created)
}