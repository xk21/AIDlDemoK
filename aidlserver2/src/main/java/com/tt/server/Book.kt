package com.tt.server

import android.os.Parcel
import android.os.Parcelable

/**
 *   Created by chenmy on 2020/5/22.
 */
class Book() : Parcelable {
    var name: String = ""

    constructor(name: String) : this() {
        this.name = name
    }

    constructor(parcel: Parcel) : this() {
        name = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun readFromParcel(dest: Parcel) {
        name = dest.readString().toString()
    }


    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }

}