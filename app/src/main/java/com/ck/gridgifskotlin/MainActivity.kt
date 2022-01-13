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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ck.gridgifskotlin.Interfaces.ImagesAPI
import com.ck.gridgifskotlin.Interfaces.ReterofitHelper
import com.ck.gridgifskotlin.MyAdapters.ImageAdapter
import com.ck.gridgifskotlin.MyAdapters.ThumbnailAdapter
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
    var gridview: RecyclerView? = null
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

        gridview = findViewById(R.id.gridview) as RecyclerView
//        gridview.setAdapter(new ImageAdapter(this));

        gridview!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    if (connected) {
                        loadNextDataFromApi()
                    }
                }
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
                        Log.d(ContentValues.TAG, "onResponse: original gif url is $urlGif")
                        val imageProperyPreview = allImages.preview_gif
                        val previewUrlGif = imageProperyPreview.url
                        Log.d(ContentValues.TAG, "onResponse: preview gif url is $previewUrlGif")
                        imgData = ImagesData(urlGif, previewUrlGif)
                        if (!imglist.contains(imgData)) {
                            imglist.add(imgData!!)
                        }
                        saveNewImage(urlGif, previewUrlGif)
                        loading!!.dismiss()
                    }
                    Log.d(TAG, "onResponse: imgData.getPreviewImage()" + imgData)
                    runOnUiThread {
                        setAdapterData(imglist)
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
                Toast.makeText(this@MainActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        }
//        runOnUiThread {
//            setAdapterData(imglist)
//        }

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
        setAdapterData(imglist)
//        imgAdapter = ImageAdapter(this@MainActivity, imglist)
        Log.d(ContentValues.TAG, "getLocalImages: images get here $images")
//        gridview!!.adapter = imgAdapter
    }

    fun setAdapterData(imglist: ArrayList<ImagesData>) {
        val adapter = ThumbnailAdapter(this@MainActivity)
        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        staggeredGridLayoutManager.gapStrategy =
            StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        gridview!!.layoutManager = staggeredGridLayoutManager
        gridview!!.setHasFixedSize(true)
        gridview!!.setItemViewCacheSize(imglist.size)
        gridview!!.isDrawingCacheEnabled = true
        gridview!!.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        gridview!!.adapter = adapter
        gridview!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    gridview?.invalidateItemDecorations()
                }
            }
        })

        adapter.setData(imglist)

        Log.d(TAG, "setAdapterData: scroll to position " + start)
        gridview!!.smoothScrollToPosition(offset * 25)

        adapter.setOnClickListener(object : ThumbnailAdapter.OnClickListener {
            override fun onClick(v: View, position: Int) {
                val intent = Intent(this@MainActivity, SingleViewActivity::class.java)
                intent.putExtra("url", imglist[position].originalImage)
                startActivity(intent)
            }
        })
    }

}

private fun RecyclerView.setOnScrollListener(onScrollListener: AbsListView.OnScrollListener) {

}
