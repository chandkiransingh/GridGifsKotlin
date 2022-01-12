package com.ck.gridgifskotlin.MyAdapters

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ck.gridgifskotlin.MyClasses.ImagesData
import java.util.*

class ImageAdapter : BaseAdapter {
    // Keep all Images in array
    private var mContext: Context
    private var imglist: ArrayList<ImagesData>? = null

    // Constructor
    constructor(c: Context, imglist: ArrayList<ImagesData>?) {
        mContext = c
        this.imglist = imglist
    }

    constructor(c: Context) {
        mContext = c
    }

    override fun getCount(): Int {
        return imglist!!.size
    }

    override fun getItem(position: Int): Any {
        val img = imglist!![position]
        return img.originalImage.toString()
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        val imageView: ImageView
        val metrics = mContext.resources.displayMetrics
        val screenWidth = metrics.widthPixels
        if (convertView == null) {
            imageView = ImageView(mContext)
            imageView.layoutParams = AbsListView.LayoutParams(screenWidth / 5, screenWidth / 5)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(8, 8, 8, 8)
        } else {
            imageView = convertView as ImageView
        }
        try {
            val img = imglist!![position]
            Glide.with(mContext).load(img.previewImage).into(imageView)
        } catch (e: Exception) {
            Log.d(ContentValues.TAG, "getView: exception is: " + e.message.toString())
        }
        return imageView
    } //    public Integer[] mThumbIds = {
    //            R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground,
    //            R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground,
    //            R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground,
    //            R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground,
    //            R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground,
    //            R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground,
    //            R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground,
    //            R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground,
    //            R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground,
    //            R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground,
    //            R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground,
    //            R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground,
    //            R.drawable.ic_launcher_background
    //    };
}