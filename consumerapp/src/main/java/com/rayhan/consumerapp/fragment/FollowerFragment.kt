package com.rayhan.consumerapp.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.rayhan.consumerapp.DetailActivity
import com.rayhan.consumerapp.ListUserAdapter
import com.rayhan.consumerapp.R
import com.rayhan.consumerapp.User
import com.rayhan.consumerapp.databinding.FragmentFollowerBinding
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class FollowerFragment : Fragment() {

    companion object{

        private val ARG_USERNAME = "username"

        fun newInstance(username: String): FollowerFragment {
            val fragment = FollowerFragment()
            val bundle = Bundle()
            bundle.putString(ARG_USERNAME, username)
            fragment.arguments = bundle

            return fragment
        }

    }

    private var binding: FragmentFollowerBinding? = null
    private var list: ArrayList<User> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_follower, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFollowerBinding.bind(view)

        val username = arguments?.getString(ARG_USERNAME)
        getUserFollowerList(username!!)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    // recyclerview handler
    private fun showRecyclerList() {
        binding?.rvFollower?.layoutManager = LinearLayoutManager(context)
        val listUserAdapter = ListUserAdapter(list)
        binding?.rvFollower?.adapter = listUserAdapter

        listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                showSelectedUser(data)
            }
        })
    }

    // listener recyclerview
    private fun showSelectedUser(user: User) {
        val moveWithObjectIntent = Intent(context, DetailActivity::class.java)
        moveWithObjectIntent.putExtra(DetailActivity.EXTRA_USER, user)
        startActivity(moveWithObjectIntent)
    }

    // get data user menggunakan Users API
    private fun getUserFollowerList(user: String) {
        binding?.progressBar?.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$user/followers"

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
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
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

                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })

    }

    // get object user detail
    private fun getUserObject(client: AsyncHttpClient, user: String) {
        // get JSON url dari url follower
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

                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
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

                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })

    }

}