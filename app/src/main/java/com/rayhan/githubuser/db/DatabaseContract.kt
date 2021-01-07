package com.rayhan.githubuser.db

import android.net.Uri
import android.provider.BaseColumns

internal class DatabaseContract {

    companion object {
        const val AUTHORITY = "com.rayhan.githubuser"
        const val SCHEME = "content"
    }

    internal class UserFavoriteColumns : BaseColumns {

        companion object {
            const val TABLE_NAME = "userfavorite"
            const val _ID = "_id"
            const val NAME = "name"
            const val USERNAME = "username"
            const val AVATAR = "avatar"
            const val COMPANY = "company"
            const val LOCATION = "description"

            // untuk membuat URI content://com.rayhan.githubuser/favorite
            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                    .authority(AUTHORITY)
                    .appendPath(TABLE_NAME)
                    .build()
        }
    }

}