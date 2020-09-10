package com.chatanoga.cab.driver.activities.profile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chatanoga.cab.common.interfaces.IDocumentEvent
import com.chatanoga.cab.common.models.Media
import com.chatanoga.cab.driver.databinding.ItemDocumentBinding

class DocumentsRecyclerViewAdapter(var documents: List<Media>, val listener: IDocumentEvent) : RecyclerView.Adapter<DocumentsRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(var binding: ItemDocumentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(media: Media?, listener: IDocumentEvent) {
            binding.media = media
            binding.image.setOnClickListener { v: View? -> listener.onClicked(media!!) }
            binding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemDocumentBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val media = documents[position]
        holder.bind(media, listener)
    }

    override fun getItemCount(): Int {
        return documents.size
    }


}