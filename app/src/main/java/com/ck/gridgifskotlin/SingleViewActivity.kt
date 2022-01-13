package com.ck.gridgifskotlin

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions


class SingleViewActivity : Activity() {
    var loading: ProgressDialog? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.single_view)
        loading = ProgressDialog(this@SingleViewActivity)
        loading!!.setMessage("Please wait.....")
        val i = intent
        val position = i.extras!!.getInt("id")
        val url = i.getStringExtra("url")
        Log.d(ContentValues.TAG, "onCreate: position is $position")
        val imageView = findViewById<View>(R.id.SingleView) as ImageView

        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.progress_animation)
            .error(R.drawable.loader_test)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)

        Glide.with(this@SingleViewActivity).load(url).apply(options).into(imageView)

    }
}