package com.rayhan.githubuser.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.rayhan.githubuser.db.DatabaseContract.UserFavoriteColumns.Companion.TABLE_NAME

internal class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "dbgithubuserapp"
        private const val DATABASE_VERSION = 1
        private const val SQL_CREATE_TABLE_NOTE = "CREATE TABLE $TABLE_NAME" +
                " (${DatabaseContract.UserFavoriteColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                " ${DatabaseContract.UserFavoriteColumns.NAME} TEXT NOT NULL," +
                " ${DatabaseContract.UserFavoriteColumns.USERNAME} TEXT NOT NULL," +
                " ${DatabaseContract.UserFavoriteColumns.AVATAR} TEXT NOT NULL," +
                " ${DatabaseContract.UserFavoriteColumns.COMPANY} TEXT NOT NULL," +
                " ${DatabaseContract.UserFavoriteColumns.LOCATION} TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_NOTE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}