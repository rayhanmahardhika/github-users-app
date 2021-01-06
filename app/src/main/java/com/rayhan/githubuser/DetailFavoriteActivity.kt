package com.rayhan.githubuser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rayhan.githubuser.databinding.ActivityDetailFavoriteBinding

class DetailFavoriteActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USER = "extra_user"
    }

    private lateinit var binding: ActivityDetailFavoriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFavoriteBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = resources.getString(R.string.title_favoriteuserdetail)
    }
}