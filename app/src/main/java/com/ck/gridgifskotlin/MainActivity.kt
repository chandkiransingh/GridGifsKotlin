package com.ck.gridgifskotlin

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ck.gridgifskotlin.Interfaces.ImagesAPI
import com.ck.gridgifskotlin.Interfaces.ReterofitHelper
import com.ck.gridgifskotlin.MyAdapters.ImageAdapter
import com.ck.gridgifskotlin.MyClasses.AppDatabase
import com.ck.gridgifskotlin.MyClasses.ImagesData
import com.ck.gridgifskotlin.MyClasses.ImagesDataOffline
import com.codemonkeylabs.fpslibrary.TinyDancer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    //    var loading: ProgressBar? = null
    var loading: ProgressDialog? = null
    private var start = 0
    private var end = 25
    private var offset = 0
    var gridview: GridView? = null
    var imglist: ArrayList<ImagesData> = ArrayList<ImagesData>()
    var imgAdapter: ImageAdapter? = null
    var connected = false
    var imgData: ImagesData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TinyDancer.create()
            .show(this@MainActivity)

        //alternatively
        TinyDancer.create()
            .redFlagPercentage(.1f)
            .startingXPosition(200)
            .startingYPosition(600)
            .show(this@MainActivity)
        TinyDancer.create()
            .addFrameDataCallback { previousFrameNS, currentFrameNS, droppedFrames ->
                //collect your stats here
            }
            .show(this@MainActivity)

        loading = ProgressDialog(this@MainActivity)
        loading!!.setMessage("Please wait.....")
//        loading = findViewById<View>(R.id.progressBar) as ProgressBar
//        var loadingbar: ProgressBar? = loading

        gridview = findViewById<View>(R.id.gridview) as GridView
//        gridview.setAdapter(new ImageAdapter(this));

        //        gridview.setAdapter(new ImageAdapter(this));
        gridview!!.onItemClickListener =
            OnItemClickListener { parent, v, position, id ->
                val i = Intent(applicationContext, SingleViewActivity::class.java)
                i.putExtra("id", position)
                i.putExtra("url", imgAdapter!!.getItem(position).toString())
                startActivity(i)
            }

        gridview!!.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, i: Int) {
                Log.d(
                    ContentValues.TAG,
                    "onScrollStateChanged: " + gridview!!.getChildAt(i).bottom + " ................." + (gridview!!.height + gridview!!.scrollY)
                )
                if (!gridview!!.canScrollVertically(1)) {
                    // bottom of scroll view
                    Log.d(ContentValues.TAG, "onScrollStateChanged: bottom")
                    if (connected) {
                        loadNextDataFromApi()
                    }
                }
                if (!gridview!!.canScrollVertically(-1)) {
                    // top of scroll view
                    Log.d(ContentValues.TAG, "onScrollStateChanged: Top")
                }
            }

            override fun onScroll(absListView: AbsListView, i: Int, i1: Int, i2: Int) {
//                Toast.makeText(MainActivity.this, "Scrolling", Toast.LENGTH_SHORT).show();
            }
        })

        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED ||
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED
        ) {
            //we are connected to a network
            connected = true
            getGifs(true)
        } else {
            connected = false
            Toast.makeText(
                this,
                "No internet connection loading previous images",
                Toast.LENGTH_SHORT
            ).show()
            getLocalImages()
        }
    }

    fun getGifs(showloader: Boolean) {
        loading!!.setMessage("Fetching Gifs ...")
        if (showloader) {
            loading!!.show()
        }
        val uri = String.format(
            "https://api.giphy.com/v1/stickers/trending?api_key=ihGPS35stL0VhASRNPAc6feokYuiTsCV&limit=25&rating=g&offset=%1\$s",
            start
        )
        Log.d(ContentValues.TAG, "getGifs: url got here is $uri and offset here is $offset")

//        val stringRequest = StringRequest(
//            Request.Method.GET, uri,
//            { response ->
//                Log.d(ContentValues.TAG, "onResponse: response is $response")
//                var jsonobject: JSONObject? = null
//                try {
//                    jsonobject = JSONObject(response)
//                    val dataArray = jsonobject.getJSONArray("data")
//                    var imageObject: JSONObject? = null
//                    var allImages: JSONObject? = null
//                    var imagePropery: JSONObject? = null
//                    var urlGif = ""
//                    var previewUrlGif = ""
//                    val size = dataArray.length()
//                    if (size > 0) {
//                        for (i in 0 until size) {
//                            imageObject = dataArray[i] as JSONObject
//                            Log.d(
//                                ContentValues.TAG,
//                                "onResponse: imageobject is $imageObject"
//                            )
//                            allImages = imageObject.getJSONObject("images")
//                            imagePropery = allImages.getJSONObject("original")
//                            urlGif = imagePropery["url"] as String
//                            Log.d(
//                                ContentValues.TAG,
//                                "onResponse: original gif url is $urlGif"
//                            )
//                            imagePropery = allImages.getJSONObject("preview_webp")
//                            previewUrlGif = imagePropery["url"] as String
//                            Log.d(
//                                ContentValues.TAG,
//                                "onResponse: preview gif url is $previewUrlGif"
//                            )
//                            imgData = ImagesData(urlGif, previewUrlGif)
//                            imglist.add(imgData!!)
//                            saveNewImage(urlGif, previewUrlGif)
//                            loading!!.dismiss()
//                        }
////                        Log.d(
////                            ContentValues.TAG,
//////                            "onResponse: imgData.getPreviewImage() " + imgData.getPreviewImage()
////                        )
//                        imgAdapter = ImageAdapter(this@MainActivity, imglist)
//                        gridview!!.setAdapter(imgAdapter)
//                        if (start > 0) {
//                            gridview!!.smoothScrollToPosition(start + 25)
//                        }
//                    } else {
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Sorry no more images found",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        loading!!.dismiss()
//                    }
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                    Log.d(ContentValues.TAG, "onResponse: Crashed json")
//                    loading!!.dismiss()
//                    Toast.makeText(this@MainActivity, "Something went wrong", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }
//        ) {
//            Toast.makeText(this@MainActivity, "Sorry no more images found", Toast.LENGTH_SHORT).show()
//            loading!!.dismiss()
//        }
//        val socketTimeout = 30000
//        val policy: RetryPolicy = DefaultRetryPolicy(
//            socketTimeout,
//            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//        )
//        stringRequest.retryPolicy = policy
//        ApplicationController.instance!!.addToRequestQueue(stringRequest)


        //retrofit

        val ImagesApi = ReterofitHelper.getInstances().create(ImagesAPI::class.java)
        GlobalScope.launch {
            val result = ImagesApi.getImages("ihGPS35stL0VhASRNPAc6feokYuiTsCV", 25, "g", start)
            if (result != null) {
                Log.d(TAG, "getGifs: result body is " + result.body().toString())
                val gotResponse = result.body()
                if (gotResponse != null) {
                    gotResponse.data.forEach {
                        Log.d(TAG, "getGifs: getting data values here " + it.images)
                        val allImages = it.images
                        val imagePropery = allImages.original
                        val urlGif = imagePropery.url
                        Log.d(
                            ContentValues.TAG,
                            "onResponse: original gif url is $urlGif"
                        )
                        val imageProperyPreview = allImages.preview_webp
                        val previewUrlGif = imageProperyPreview.url
                        Log.d(
                            ContentValues.TAG,
                            "onResponse: preview gif url is $previewUrlGif"
                        )
                        imgData = ImagesData(urlGif, previewUrlGif)
                        imglist.add(imgData!!)
//                        saveNewImage(urlGif, previewUrlGif)
                        loading!!.dismiss()
                    }
                    Log.d(TAG, "onResponse: imgData.getPreviewImage() " + imgData)
                    imgAdapter = ImageAdapter(this@MainActivity, imglist)
                    gridview!!.adapter = imgAdapter
                    if (start > 0) {
                        gridview!!.smoothScrollToPosition(start + 25)
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Sorry no more images found",
                        Toast.LENGTH_SHORT
                    ).show()
                    loading!!.dismiss()
                }
            } else {
                Toast.makeText(this@MainActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun loadNextDataFromApi() {
        println("page is $offset")
        start = offset * 25 + 1
        end = end + 25
        println("Start is $start")
        println("End is $end")
        offset = offset + 1
        getGifs(true)
    }

    fun saveNewImage(urlGif: String, previewUrlGif: String) {
        val db: AppDatabase = AppDatabase.getDbInstance(this.applicationContext)!!
        val imagesDataOffline = ImagesDataOffline()
        imagesDataOffline.OriginalImageLocal = urlGif
        imagesDataOffline.previewImageLocal = previewUrlGif
        db.imgDataDao()!!.insertImage(imagesDataOffline)
    }

    fun getLocalImages() {
        val db = AppDatabase.getDbInstance(this.applicationContext)
        val images: List<ImagesDataOffline?>? = db!!.imgDataDao()!!.allImages
        if (images != null) {
            for (j in images.indices) {
                Log.d(ContentValues.TAG, "getLocalImages: " + images!![j])
                val newImgobj = images[j]
                if (newImgobj != null) {
                    imgData = ImagesData(
                        newImgobj.OriginalImageLocal.toString(),
                        newImgobj.previewImageLocal.toString()
                    )
                }
                imglist.add(imgData!!)
            }
        }
        imgAdapter = ImageAdapter(this@MainActivity, imglist)
        Log.d(ContentValues.TAG, "getLocalImages: images get here $images")
        gridview!!.adapter = imgAdapter
    }
}