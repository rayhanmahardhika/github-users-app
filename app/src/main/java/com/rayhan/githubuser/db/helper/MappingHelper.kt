package com.rayhan.githubuser.db.helper

import android.database.Cursor
import com.rayhan.githubuser.User
import com.rayhan.githubuser.db.DatabaseContract

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