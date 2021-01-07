package com.rayhan.consumerapp.db.helper

import android.database.Cursor
import com.rayhan.consumerapp.User
import com.rayhan.consumerapp.db.DatabaseContract

object MappingHelper {

    fun mapCursorToString(userFavCursor: Cursor?): String {
        var uname = ""
        userFavCursor?.apply {
            while(moveToNext()) {
                uname =  getString(getColumnIndexOrThrow(DatabaseContract.UserFavoriteColumns.USERNAME))
            }
        }

        return uname
    }

    fun mapCursorToArrayList(userFavCursor: Cursor?): ArrayList<User> {
        val list = ArrayList<User>()
        userFavCursor?.apply {
            while(moveToNext()) {
                val name =  getString(getColumnIndexOrThrow(DatabaseContract.UserFavoriteColumns.NAME))
                val username =  getString(getColumnIndexOrThrow(DatabaseContract.UserFavoriteColumns.USERNAME))
                val avatar =  getString(getColumnIndexOrThrow(DatabaseContract.UserFavoriteColumns.AVATAR))
                val company =  getString(getColumnIndexOrThrow(DatabaseContract.UserFavoriteColumns.COMPANY))
                val location =  getString(getColumnIndexOrThrow(DatabaseContract.UserFavoriteColumns.LOCATION))
                list.add(User(name, username, avatar, company, location))
            }
        }

        return list
    }
}