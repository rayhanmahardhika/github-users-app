package com.rayhan.githubuser

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.rayhan.githubuser.databinding.ActivityMainBinding
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var list: ArrayList<User>

    private var binding: ActivityMainBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = resources.getString(R.string.title_userlist)

        getUserList()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if(query.isEmpty()) return true
                else {
                    getUserSearchList(query)
                }
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.option) {
            val mIntent = Intent(this, SettingActivity::class.java)
            startActivity(mIntent)
        }else if (item.itemId == R.id.favorite) {
            val fIntent = Intent(this, FavoriteActivity::class.java)
            startActivity(fIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    // recyclerview handler
    private fun showRecyclerList() {
        binding?.rvUser?.layoutManager = LinearLayoutManager(this)
        val listUserAdapter = ListUserAdapter(list)
        binding?.rvUser?.adapter = listUserAdapter

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

    // get data user menggunakan Users API
    private fun getUserList() {
        binding?.progressBar?.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users"

        //header
        client.addHeader("Authorization", "token 3bb9539d4b5f2475a18609dcbd413377bb86aea9")
        client.addHeader("User-Agent", "request")
        //content
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {

                list = ArrayList<User>()
                val result = String(responseBody!!)

                try {

                    val responseArray = JSONArray(result)
                    for (i in 0 until responseArray.length()) {
                        // each user
                        val userName = responseArray.getJSONObject(i).getString("login")
                        getUserObject(client, userName)
                    }

                }catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                binding?.progressBar?.visibility = View.INVISIBLE

                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error!!.message}"
                }

                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })

    }

    // get data user menggunakan search API
    private fun getUserSearchList(user: String) {
        binding?.progressBar?.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://api.github.com/search/users?q=$user"

        //header
        client.addHeader("Authorization", "token 3bb9539d4b5f2475a18609dcbd413377bb86aea9")
        client.addHeader("User-Agent", "request")
        //content
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {

                list = ArrayList<User>()
                val result = String(responseBody!!)

                try {

                    val responseObject = JSONObject(result)
                    val items = responseObject.getJSONArray("items")

                    for (i in 0 until items.length()) {
                        // each user
                        val userName = items.getJSONObject(i).getString("login")
                        getUserObject(client, userName)

                    }

                }catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                binding?.progressBar?.visibility = View.INVISIBLE

                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error!!.message}"
                }

                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }


    // get object user detail
    private fun getUserObject(client: AsyncHttpClient, user: String) {
        val url = "https://api.github.com/users/$user"

        //header
        client.addHeader("Authorization", "token 3bb9539d4b5f2475a18609dcbd413377bb86aea9")
        client.addHeader("User-Agent", "request")
        //content
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {

                val result = String(responseBody!!)
                try {
                    val responseObject = JSONObject(result)
                    val company = if(responseObject.isNull("company")) resources.getString(R.string.affiliation) else responseObject.getString("company")
                    val location = if(responseObject.isNull("location")) resources.getString(R.string.unknown_location) else responseObject.getString("location")

                    Log.d("company", company)

                    list.add(
                        User(
                            responseObject.getString("name"),
                            responseObject.getString("login"),
                            responseObject.getString("avatar_url"),
                            company,
                            location,
                        )
                    )
                    binding?.progressBar?.visibility = View.INVISIBLE
                    showRecyclerList()
                }catch (e: Exception) {

                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()

                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                binding?.progressBar?.visibility = View.INVISIBLE

                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error!!.message}"
                }

                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })

    }

}