package com.example.mydicodingsubmission.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId

data class FirebaseItems(
    val documentId: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val addInfo: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(imageUrl)
        parcel.writeString(addInfo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FirebaseItems> {
        override fun createFromParcel(parcel: Parcel): FirebaseItems {
            return FirebaseItems(parcel)
        }

        override fun newArray(size: Int): Array<FirebaseItems?> {
            return arrayOfNulls(size)
        }
    }
}