package jdd.com.ng.jddwebmaster.jddstore.code.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.model.Product
import jdd.com.ng.jddwebmaster.jddstore.code.utils.GlideLoader

class MyDashboardAdapter(
    // context and the list of product are to be displayed here
    private val context: Context,
    private val list:ArrayList<Product>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // return my own viewHolder
        return MyProductListAdapter.MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_dashboard_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // get the current position of each product
        val model = list[position]
        if (holder is MyViewHolder){
            GlideLoader(context).loadProductImage(model.product_image, holder.itemView.findViewById(R.id.iv_dashboard_item_image))
            // set the item name and price
            holder.itemView.findViewById<TextView> (R.id.tv_dashboard_title_item).text = model.product_title
            holder.itemView.findViewById<TextView> (R.id.tv_dashboard_price_item).text = "(NGN ${model.product_price})"
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}