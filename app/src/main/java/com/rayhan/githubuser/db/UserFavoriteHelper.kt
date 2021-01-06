package com.rayhan.githubuser.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.rayhan.githubuser.db.DatabaseContract.UserFavoriteColumns.Companion.TABLE_NAME
import com.rayhan.githubuser.db.DatabaseContract.UserFavoriteColumns.Companion.USERNAME
import com.rayhan.githubuser.db.DatabaseContract.UserFavoriteColumns.Companion._ID
import java.sql.SQLException

class UserFavoriteHelper (context: Context) {


    private lateinit var database: SQLiteDatabase
    private var dataBaseHelper: DatabaseHelper =  DatabaseHelper(context)

    companion object {
        private const val DATABASE_TABLE = TABLE_NAME
        private var INSTANCE: UserFavoriteHelper? = null

        fun getInstance(context: Context): UserFavoriteHelper =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: UserFavoriteHelper(context)
                }
    }

    @Throws(SQLException::class)
    fun open() {
        database = dataBaseHelper.writableDatabase
    }

    fun close() {
        dataBaseHelper.close()

        if (database.isOpen)
            database.close()

    }

    // ambil semua data
    fun queryAll(): Cursor {
        return database.query(
                DATABASE_TABLE,
                null,
                null,
                null,
                null,
                null,
                "$_ID ASC")
    }

    // ambil berdasar ID
    fun queryById(id: String): Cursor {
        return database.query(
                DATABASE_TABLE,
                null,
                "$_ID = ?",
                arrayOf(id),
                null,
                null,
                null,
                null)
    }

    // masukan data
    fun insert(values: ContentValues?): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    // hapus data
    fun deleteByUserName(uname: String): Int {
        return database.delete(DATABASE_TABLE, "$USERNAME = '$uname'", null)
    }

    // cek username
    fun selectUserName(uname: String): Cursor? {
        return database.query(
                DATABASE_TABLE,
                null,
                "$USERNAME = ?",
                arrayOf(uname),
                null,
                null,
                null,
                null)
    }

}