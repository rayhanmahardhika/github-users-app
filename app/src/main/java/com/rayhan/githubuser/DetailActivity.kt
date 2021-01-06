package com.rayhan.githubuser

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.rayhan.githubuser.adapter.SectionsPagerAdapter
import com.rayhan.githubuser.databinding.ActivityDetailBinding
import com.rayhan.githubuser.db.DatabaseContract
import com.rayhan.githubuser.db.UserFavoriteHelper
import com.rayhan.githubuser.db.helper.MappingHelper
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USER = "extra_user"
        const val EXTRA_POSITION = "extra_position"
        const val REQUEST_ADD = 100
        const val RESULT_ADD = 101
        const val REQUEST_UPDATE = 200
        const val RESULT_UPDATE = 201
        const val RESULT_DELETE = 301
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }

    private lateinit var binding: ActivityDetailBinding
    private lateinit var userFavoriteHelper: UserFavoriteHelper
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = resources.getString(R.string.title_userdetail)

        userFavoriteHelper = UserFavoriteHelper.getInstance(applicationContext)
        userFavoriteHelper.open()

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

        // logika button favorite
        var favBtnStat = MappingHelper.mapCursorToString(userFavoriteHelper.selectUserName(user.userName.toString())) == user.userName // nilai dari "apakah username sudah ada di daftar favorite?"
        setStatFavBtn(favBtnStat)
        binding.fabFavorite.setOnClickListener{
            favBtnStat = !favBtnStat
            // kode instert database
            if(favBtnStat == true) addUserToFavorite(user)
            else if (favBtnStat == false) removeUserFromFav(user.userName.toString())
            setStatFavBtn(favBtnStat)
        }
    }

    private fun setStatFavBtn(stat: Boolean) {
        if(stat)
            // ganti ke fav
            binding.fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_24_pink)
        else
            // ganti ke unfav
            binding.fabFavorite.setImageResource(R.drawable.ic_baseline_unfavorite_border_24_pink)

    }

    private fun addUserToFavorite(user: User) {
        // nilai untuk query ke DB
        val values = ContentValues()
        values.put(DatabaseContract.UserFavoriteColumns.NAME, user.name)
        values.put(DatabaseContract.UserFavoriteColumns.USERNAME, user.userName)
        values.put(DatabaseContract.UserFavoriteColumns.AVATAR, user.avatar)
        values.put(DatabaseContract.UserFavoriteColumns.COMPANY, user.company)
        values.put(DatabaseContract.UserFavoriteColumns.LOCATION, user.location)

        // query insert ke DB
        val berhasil = userFavoriteHelper.insert(values) > 0
        if (berhasil) {
            Snackbar.make(binding.root, resources.getString(R.string.query_result_ok), Snackbar.LENGTH_SHORT).show()
        }else {
            Snackbar.make(binding.root, resources.getString(R.string.query_result_bad), Snackbar.LENGTH_SHORT).show()
        }

    }

    private fun removeUserFromFav(uname: String) {
        if(userFavoriteHelper.deleteByUserName(uname).toLong() > 0) Snackbar.make(binding.root, resources.getString(R.string.delete_result_ok), Snackbar.LENGTH_SHORT).show()
        else Snackbar.make(binding.root, resources.getString(R.string.delete_result_bad), Snackbar.LENGTH_SHORT).show()
    }


    // ketika activity ditutup maka akses database di tutup
    override fun onDestroy() {
        super.onDestroy()
        userFavoriteHelper.close()
    }
}