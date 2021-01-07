package com.rayhan.githubuser.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.rayhan.githubuser.db.DatabaseContract.Companion.AUTHORITY
import com.rayhan.githubuser.db.DatabaseContract.UserFavoriteColumns.Companion.CONTENT_URI
import com.rayhan.githubuser.db.DatabaseContract.UserFavoriteColumns.Companion.TABLE_NAME
import com.rayhan.githubuser.db.UserFavoriteHelper

class GithubUserProvider : ContentProvider() {

    companion object {
        private const val FAVORITE = 1
        private const val FAVORITE_ID = 2
        private const val FAVORITE_UNAME = 3
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private lateinit var userFavoriteHelper: UserFavoriteHelper
        init {
            // content://com.rayhan.githubuser/favorite
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, FAVORITE)
            // content://com.rayhan.githubuser/favorite/id
            sUriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", FAVORITE_ID)
            // content://com.rayhan.githubuser/favorite/uname
            sUriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", FAVORITE_UNAME)
        }
    }

    override fun onCreate(): Boolean {
        userFavoriteHelper = UserFavoriteHelper.getInstance(context as Context)
        userFavoriteHelper.open()
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return when (sUriMatcher.match(uri)) {
            FAVORITE -> userFavoriteHelper.queryAll()
            FAVORITE_UNAME -> userFavoriteHelper.selectUserName(uri.lastPathSegment.toString())
            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val added: Long = when (FAVORITE) {
            sUriMatcher.match(uri) -> userFavoriteHelper.insert(values)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val deleted: Int = when (FAVORITE_UNAME) {
            sUriMatcher.match(uri) -> userFavoriteHelper.deleteByUserName(uri.lastPathSegment.toString())
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return deleted

    }
}