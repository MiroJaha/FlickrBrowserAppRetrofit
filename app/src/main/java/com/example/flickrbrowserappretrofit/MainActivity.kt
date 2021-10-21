package com.example.flickrbrowserappretrofit

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var list: ArrayList<Data>
    private lateinit var favoriteList: ArrayList<Data>
    private lateinit var rvMain: RecyclerView
    private lateinit var favoriteShowRV: RecyclerView
    private lateinit var rvAdapter: RVAdapter
    private lateinit var rvFavoriteAdapter: RVAdapter
    private lateinit var llBottom: LinearLayout
    private lateinit var etWord: EditText
    private lateinit var btSearch: Button
    private lateinit var moreImage: ImageView
    private lateinit var search: String
    private var count= 10
    private var mode= 1
    private var mode2=1
    private lateinit var photosGrid: GridView
    private lateinit var gridAdapter: GridAdapter
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.favorite -> {
                if (mode==1) {
                    this@MainActivity.title= "Favorites"
                    startFavorite()
                    item.setIcon(R.drawable.blank_heart)
                    favoriteShowRV.isVisible= true
                    rvMain.isVisible= false
                    moreImage.isVisible= false
                    llBottom.isVisible= false
                    photosGrid.isVisible= false
                    mode=2
                }
                else{
                    this@MainActivity.title = "FlickrBrowserApp"
                    endFavorite()
                    item.setIcon(R.drawable.full_heart)
                    favoriteShowRV.isVisible = false
                    moreImage.isVisible = true
                    llBottom.isVisible = true
                    mode= 1
                    if (mode2==1) {
                        rvMain.isVisible = true
                    }
                    else{
                        photosGrid.isVisible= true
                    }
                }
                return true
            }
            R.id.viewWay -> {
                startFavorite()
                this@MainActivity.title = "FlickrBrowserApp"
                endFavorite()
                mode= 1
                favoriteShowRV.isVisible = false
                moreImage.isVisible = true
                llBottom.isVisible = true
                if (mode2==1){
                    item.setIcon(R.drawable.list_view)
                    photosGrid.isVisible= true
                    rvMain.isVisible= false
                    mode2= 2
                }
                else{
                    item.setIcon(R.drawable.grid_view)
                    photosGrid.isVisible= false
                    rvMain.isVisible= true
                    mode2= 1
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun endFavorite() {
        for (image in list){
            image.checkBox= false
        }
        for (image in list)
            for (image2 in favoriteList)
                if (image.id == image2.id && image2.secret == image.secret)
                    image.checkBox= true
        rvAdapter.update()
        gridAdapter.notifyDataSetChanged()
    }

    private fun startFavorite() {
        for (image in list) {
            for (image2 in list){
                if (image.id == image2.id && image2.secret == image.secret)
                    image2.checkBox = image.checkBox
            }
        }
        favoriteList.removeAll { !it.checkBox }
        rvFavoriteAdapter.update()
        for (image in list){
            if (image.checkBox) {
                var check= false
                for (image2 in favoriteList)
                    if (image.id == image2.id && image2.secret == image.secret)
                        check= true
                if (!check)
                    favoriteList.add(image)
            }
        }
        for (image in favoriteList){
            image.checkBox =true
        }
        rvFavoriteAdapter.update()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list= arrayListOf()
        favoriteList= arrayListOf()
        rvMain = findViewById(R.id.rvMain)
        favoriteShowRV= findViewById(R.id.favoriteShowRV)
        etWord = findViewById(R.id.etWord)
        btSearch = findViewById(R.id.btSearch)
        moreImage= findViewById(R.id.moreImages)
        llBottom= findViewById(R.id.llBottom)
        photosGrid = findViewById(R.id.imageGrid)

        gridAdapter = GridAdapter(this,list)
        photosGrid.adapter = gridAdapter

        rvFavoriteAdapter= RVAdapter(favoriteList,2)
        favoriteShowRV.adapter= rvFavoriteAdapter
        favoriteShowRV.layoutManager= LinearLayoutManager(this)

        rvAdapter = RVAdapter(list,1)
        rvMain.adapter = rvAdapter
        rvMain.layoutManager = LinearLayoutManager(this)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please Wait:")
        progressDialog.setCancelable(false)

        btSearch.setOnClickListener {
            if(etWord.text.isNotEmpty()) {
                progressDialog.show()
                count= 10
                search= etWord.text.toString().replace(" ","&")
                search= search.replace(",","%2C")
                Data.search= search
                Data.count= count
                updateList()
                moreImage.isVisible= true
            }
            else{
                Toast.makeText(this, "Please Enter Something", Toast.LENGTH_SHORT).show()
            }
        }

        moreImage.setOnClickListener{
            progressDialog.show()
            count+=10
            Data.count= count
            updateList()
        }

        photosGrid.setOnItemClickListener{
                _, _, position, _ ->
            val showImage= list[position]
            val intent= Intent(this@MainActivity,ImageShow::class.java)
            intent.putExtra("title",showImage.title)
            intent.putExtra("serverID",showImage.server)
            intent.putExtra("photoID",showImage.id)
            intent.putExtra("secretNumber",showImage.secret)
            startActivity(intent)
        }

        rvAdapter.setOnItemClickListener(object : RVAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val showImage= list[position]
                val intent= Intent(this@MainActivity,ImageShow::class.java)
                intent.putExtra("title",showImage.title)
                intent.putExtra("serverID",showImage.server)
                intent.putExtra("photoID",showImage.id)
                intent.putExtra("secretNumber",showImage.secret)
                startActivity(intent)
            }
        })

        rvFavoriteAdapter.setOnItemClickListener(object : RVAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val showImage= favoriteList[position]
                val intent= Intent(this@MainActivity,ImageShow::class.java)
                intent.putExtra("title",showImage.title)
                intent.putExtra("serverID",showImage.server)
                intent.putExtra("photoID",showImage.id)
                intent.putExtra("secretNumber",showImage.secret)
                startActivity(intent)
            }
        })

    }
    private fun updateList(){
        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
        Log.d("MyData","$apiInterface")
        apiInterface?.getInformation(Data.search,Data.count.toString())?.enqueue(object: Callback<Photos> {
            @RequiresApi(Build.VERSION_CODES.S)
            override fun onResponse(call: Call<Photos>, response: Response<Photos>) {
                try {
                    list.clear()
                    val result= response.body()?.photos
                    val photos= result?.getAsJsonArray("photo")
                    Log.d("MyData", "$photos" )
                    //val photo= result?.getAsJsonArray("photo")
                    //Log.d("MyData", "$photo" )
                    //val photos= JSONObject(result.toString())
                    //val photo= photos.getJSONArray("photo")
                    //Log.d("MyData", "$photo" )
                    /*for (i in 0 until photo!!.size()) {
                        val photoID = photo.asJsonObject(i).getString("id")
                        val photoSecret = photo.getJSONObject(i).getString("secret")
                        val photoServer = photo.getJSONObject(i).getString("server")
                        val photoTitle = photo.getJSONObject(i).getString("title")
                        var checkBox = false
                        for (image in favoriteList)
                            if (photoID == image.id && photoSecret == image.secret)
                                checkBox = true
                        Log.d("MyData", "$photoTitle, $photoServer, $photoID, $photoSecret, $checkBox" )
                        list.add(Data(photoTitle, photoServer, photoID, photoSecret, checkBox))
                    }*/
                    if (photos != null) {
                        for (i in photos) {
                            Log.d("MyData", "$i")
                            val photoID = i.asJsonObject.get("id").asString
                            val photoSecret =  i.asJsonObject.get("secret").asString
                            val photoServer =  i.asJsonObject.get("server").asString
                            val photoTitle =  i.asJsonObject.get("title").asString
                            var checkBox = false
                            for (image in favoriteList)
                                if (photoID == image.id && photoSecret == image.secret)
                                    checkBox = true
                            Log.d("MyData", "$photoTitle, $photoServer, $photoID, $photoSecret, $checkBox" )
                            list.add(Data(photoTitle, photoServer, photoID, photoSecret, checkBox))
                        }
                    }
                    rvAdapter.update()
                    gridAdapter.notifyDataSetChanged()
                    etWord.text.clear()
                    val view: View? = this@MainActivity.currentFocus
                    if (view != null) {
                        val imm: InputMethodManager =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                    if (list.size > 10)
                        scrollDown()
                    progressDialog.dismiss()
                }
                catch (e: Exception){
                    Log.d("MyInformation","failed $e")
                    progressDialog.dismiss()
                }
            }

            override fun onFailure(call: Call<Photos>, t: Throwable) {
                Log.d("MyData","failed \n$t")
                Toast.makeText(this@MainActivity,"Failed ", Toast.LENGTH_LONG).show()
                progressDialog.dismiss()
            }
        })
    }
    private fun scrollDown() {
        rvMain.scrollToPosition(list.size - 10)
    }
}