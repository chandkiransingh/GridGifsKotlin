package com.ck.gridgifskotlin.MyAdapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.ck.gridgifskotlin.MyClasses.ImagesData
import com.ck.gridgifskotlin.R


class ThumbnailAdapter(private val context: Context) : RecyclerView.Adapter<ThumbnailAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null
    private var mThumbnails = ArrayList<ImagesData>()

    fun setData(thumbnails: ArrayList<ImagesData>) {
        this.mThumbnails = thumbnails
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_thumbnail, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {

        return mThumbnails.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder?.onBind(context, mThumbnails[position].previewImage!!, position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var view = itemView
        private var ivImage: ImageView = itemView.findViewById(R.id.ivImage)

        fun onBind(context: Context, thumbnail: String, position: Int) {
            ivImage.requestLayout()
            Log.d("TAG", "onBind: "+thumbnail)

//            Glide.with(context).load(thumbnail).diskCacheStrategy(DiskCacheStrategy.DATA).into(new DrawableImageViewTarget(ivImage))

            view.setOnClickListener {
                onClickListener?.onClick(it, position)
            }
        }

    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(v: View, position: Int)
    }
}

