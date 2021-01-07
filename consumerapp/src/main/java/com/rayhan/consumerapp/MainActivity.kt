package com.rayhan.consumerapp

import android.content.Intent
import android.database.ContentObserver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rayhan.consumerapp.databinding.ActivityMainBinding
import com.rayhan.consumerapp.db.DatabaseContract.UserFavoriteColumns.Companion.CONTENT_URI
import com.rayhan.consumerapp.db.helper.MappingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var list: ArrayList<User>

    private var binding: ActivityMainBinding? = null

    companion object {

        // saving state key
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = resources.getString(R.string.title_favorite)

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        val myObserver = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                getUserFavAsync()
            }
        }


        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)
    }

    // recyclerview handler
    private fun showRecyclerList() {
        binding?.rvUserFavorite?.layoutManager = LinearLayoutManager(this)
        val listUserAdapter = ListUserAdapter(list)
        binding?.rvUserFavorite?.adapter = listUserAdapter

        listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                showSelectedUser(data)
            }
        })
    }

    // listener recycler view
    private fun showSelectedUser(user: User) {
        val moveWithObjectIntent = Intent(this, DetailActivity::class.java)
        moveWithObjectIntent.putExtra(DetailActivity.EXTRA_USER, user)
        startActivity(moveWithObjectIntent)
    }

    // ambil list user favorite menggunakan caroutine
    private fun getUserFavAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            binding?.progressBar?.visibility = View.VISIBLE
            val defferedUser = async(Dispatchers.IO) {
                // CONTENT_URI = content://com.rayhan.githubuser/favorite
                val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            binding?.progressBar?.visibility = View.INVISIBLE

            val users = defferedUser.await()
            if(users.size > 0) {
                list = ArrayList<User>()
                list.addAll(users)
                showRecyclerList()
            }else {
                list = ArrayList<User>()
                Snackbar.make(binding!!.root, resources.getString(R.string.query_all_blank), Snackbar.LENGTH_SHORT).show()
                showRecyclerList()
            }
        }
    }
}