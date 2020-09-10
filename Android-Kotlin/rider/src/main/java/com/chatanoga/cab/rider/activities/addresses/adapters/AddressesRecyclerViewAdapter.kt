package com.chatanoga.cab.rider.activities.addresses.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chatanoga.cab.common.models.Address
import com.chatanoga.cab.rider.databinding.ItemAddressBinding

class AddressesRecyclerViewAdapter(private val addresses: List<Address>, private val listener: OnAddressItemInteractionListener) : RecyclerView.Adapter<AddressesRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(var binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Address, listener: OnAddressItemInteractionListener) {
            binding.address = address
            binding.buttonEdit.setOnClickListener { view: View? -> listener.onEdit(address) }
            binding.buttonDelete.setOnClickListener { view: View? -> listener.onDelete(address) }
            binding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemAddressBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = addresses[position]
        holder.bind(address, listener)
    }

    override fun getItemCount(): Int {
        return addresses.size
    }

    interface OnAddressItemInteractionListener {
        fun onEdit(address: Address)
        fun onDelete(address: Address)
    }

}