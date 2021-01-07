package com.rayhan.consumerapp

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.rayhan.consumerapp.adapter.SectionsPagerAdapter
import com.rayhan.consumerapp.databinding.ActivityDetailBinding
import com.rayhan.consumerapp.db.DatabaseContract
import com.rayhan.consumerapp.db.DatabaseContract.UserFavoriteColumns.Companion.CONTENT_URI
import com.rayhan.consumerapp.db.helper.MappingHelper
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USER = "extra_user"
    }

    private lateinit var binding: ActivityDetailBinding
    private lateinit var uriWithUname: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = resources.getString(R.string.title_userdetail)

        val user: User = intent.getParcelableExtra<User>(EXTRA_USER) as User

        Glide.with(this.applicationContext)
                .load(user.avatar)
                .apply(RequestOptions().override(200, 200))
                .into(img_detail_photo)

        tv_nama_detail.text = user.name
        tv_username_detail.text = user.userName
        tv_location_detail.text = user.location
        tv_company_detail.text = user.company

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        sectionsPagerAdapter.userName = user?.userName
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        supportActionBar?.elevation = 0f

        // menggunakan content provider
        uriWithUname = Uri.parse(CONTENT_URI.toString() + "/" + user.userName)
        var favBtnStat = checkFavStat(user.userName) // nilai dari "apakah username sudah ada di daftar favorite?"

        // logika button favorite
        setStatFavBtn(favBtnStat)
        binding.fabFavorite.setOnClickListener{
            favBtnStat = !favBtnStat
            // kode instert database
            if(favBtnStat == true) addUserToFavorite(user)
            else if (favBtnStat == false) removeUserFromFav(user.userName.toString())
            setStatFavBtn(favBtnStat)
        }
    }

    // fungsi menangani query by uname
    private fun checkFavStat(uname: String?): Boolean {
        var stat = false
        val cursor = contentResolver.query(uriWithUname, null, null, null, null)

        if (cursor != null) {
            val unameDB = MappingHelper.mapCursorToString(cursor)
            cursor.close()
            stat = unameDB == uname
        }

        return stat
    }

    // fungsi set drawable icon dari floating action button
    private fun setStatFavBtn(stat: Boolean) {
        if(stat)
            // ganti ke fav
            binding.fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_24_pink)
        else
            // ganti ke unfav
            binding.fabFavorite.setImageResource(R.drawable.ic_baseline_unfavorite_border_24_pink)

    }

    // fungsi menangani insert
    private fun addUserToFavorite(user: User) {
        // nilai untuk query ke DB
        val values = ContentValues()
        values.put(DatabaseContract.UserFavoriteColumns.NAME, user.name)
        values.put(DatabaseContract.UserFavoriteColumns.USERNAME, user.userName)
        values.put(DatabaseContract.UserFavoriteColumns.AVATAR, user.avatar)
        values.put(DatabaseContract.UserFavoriteColumns.COMPANY, user.company)
        values.put(DatabaseContract.UserFavoriteColumns.LOCATION, user.location)

        // query insert ke DB menggunakan Content Resolver
        contentResolver.insert(CONTENT_URI, values)
        Snackbar.make(binding.root, resources.getString(R.string.query_result_ok), Snackbar.LENGTH_SHORT).show()
    }

    // fungsi menangani delete
    private fun removeUserFromFav(uname: String) {
        contentResolver.delete(uriWithUname, null, null)
        Snackbar.make(binding.root, resources.getString(R.string.delete_result_ok), Snackbar.LENGTH_SHORT).show()
    }

}