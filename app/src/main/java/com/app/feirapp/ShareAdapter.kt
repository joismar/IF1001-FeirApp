package com.app.feirapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ShareAdapter(private val shares: ArrayList<*>, private val activity: ShareActivity) : RecyclerView.Adapter<ShareAdapter.ShareViewHolder>() {
    class ShareViewHolder(val shareRview: View) : RecyclerView.ViewHolder(shareRview) {

        init {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ShareAdapter.ShareViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.sharelista, parent, false)
        return ShareAdapter.ShareViewHolder(v)
    }

    override fun onBindViewHolder(holder: ShareAdapter.ShareViewHolder, position: Int) {
        //val produtoModel = shares[position]

        //(holder.shareRview.findViewById(R.id.useremail) as TextView).text =
        //(holder.shareRview.findViewById(R.id.data) as TextView).text =

    }

    override fun getItemCount(): Int {
        return shares.size
    }
}