package com.rayhan.githubuser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rayhan.githubuser.databinding.ActivityFavoriteBinding
import com.rayhan.githubuser.db.UserFavoriteHelper
import com.rayhan.githubuser.db.helper.MappingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {

    private lateinit var list: ArrayList<User>

    private var binding: ActivityFavoriteBinding? = null

    private lateinit var userFavoriteHelper: UserFavoriteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = resources.getString(R.string.title_favorite)

        userFavoriteHelper = UserFavoriteHelper.getInstance(applicationContext)
        userFavoriteHelper.open()

        getUserFavAsync()

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
                MappingHelper.mapCursorToArrayList(userFavoriteHelper.queryAll())
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

    // ketika activity ditutup maka akses database di tutup
    override fun onDestroy() {
        super.onDestroy()
        userFavoriteHelper.close()
    }
}