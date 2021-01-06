package com.rayhan.githubuser.db

import android.provider.BaseColumns

internal class DatabaseContract {

    internal class UserFavoriteColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "userfavorite"
            const val _ID = "_id"
            const val NAME = "name"
            const val USERNAME = "username"
            const val AVATAR = "avatar"
            const val COMPANY = "company"
            const val LOCATION = "description"
        }
    }

}