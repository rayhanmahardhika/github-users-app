package com.rayhan.githubuser

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val name: String?,
    val userName: String?,
    val avatar:String?,
    val company: String?,
    val location: String?,
): Parcelable