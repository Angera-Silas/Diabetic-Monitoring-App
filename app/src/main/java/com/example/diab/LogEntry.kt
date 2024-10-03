package com.example.diab

import android.os.Parcel
import android.os.Parcelable

data class LogEntry(
    val userId: String = "",  // User ID associated with the log entry
    val bloodSugar: Int = 0,  // Blood sugar level
    val medication: String = "", // General medication for the time of day
    val timeOfDay: String = ""   // Indicates if it's morning, afternoon, or evening
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeInt(bloodSugar)
        parcel.writeString(medication)
        parcel.writeString(timeOfDay)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LogEntry> {
        override fun createFromParcel(parcel: Parcel): LogEntry {
            return LogEntry(parcel)
        }

        override fun newArray(size: Int): Array<LogEntry?> {
            return arrayOfNulls(size)
        }
    }
}
