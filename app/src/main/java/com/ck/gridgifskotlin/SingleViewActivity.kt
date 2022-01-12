package com.ck.gridgifskotlin

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide

class SingleViewActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.single_view)
        val i = intent
        val position = i.extras!!.getInt("id")
        val url = i.getStringExtra("url")
        Log.d(ContentValues.TAG, "onCreate: position is $position")
        val imageView = findViewById<View>(R.id.SingleView) as ImageView
        Glide.with(this@SingleViewActivity).load(url).into(imageView)
    }
}