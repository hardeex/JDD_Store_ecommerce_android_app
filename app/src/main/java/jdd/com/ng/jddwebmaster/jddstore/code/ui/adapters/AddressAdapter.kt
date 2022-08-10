package jdd.com.ng.jddwebmaster.jddstore.code.ui.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.model.Address
import jdd.com.ng.jddwebmaster.jddstore.code.ui.activities.AddAndEditAddressListActivity
import jdd.com.ng.jddwebmaster.jddstore.code.ui.activities.CheckoutActivity
import jdd.com.ng.jddwebmaster.jddstore.code.ui.fragment.ProductFragment
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant

class AddressAdapter (
    // context and the list of product are to be displayed here
    private val context: Context,
    private val list:ArrayList<Address>,
    private val selectAddress: Boolean

): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    fun notifyEditItem(activity: Activity, position: Int){
        val intent = Intent(context, AddAndEditAddressListActivity::class.java)
        intent.putExtra(Constant.EXTRA_ADDRESS_DETAILS, list[position])
        activity.startActivityForResult(intent, Constant.ADD_ADDRESS_REQUEST_CODE)
        // call this kotlin method each time an item is changed
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // return my own viewHolder
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_address_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.findViewById<TextView>(R.id.tv_address_full_name).text = model.name
            holder.itemView.findViewById<TextView>(R.id.tv_address_type).text = model.type
            holder.itemView.findViewById<TextView>(R.id.tv_address_details).text = "${model.address}, ${model.additional_phoneNumber}"
            holder.itemView.findViewById<TextView>(R.id.tv_address_mobile_number).text = model.mobileNumber

            if (selectAddress){
                holder.itemView.setOnClickListener {
                   // Toast.makeText(context, "Selected Address ${model.address}, ${model.mobileNumber}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, CheckoutActivity::class.java)
                    intent.putExtra(Constant.EXTRA_SELECTED_ADDRESS, model)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}