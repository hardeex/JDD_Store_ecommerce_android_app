package jdd.com.ng.jddwebmaster.jddstore.code.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.model.Product
import jdd.com.ng.jddwebmaster.jddstore.code.ui.activities.ProductDetailsActivity
import jdd.com.ng.jddwebmaster.jddstore.code.ui.fragment.ProductFragment
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant
import jdd.com.ng.jddwebmaster.jddstore.code.utils.GlideLoader

open class MyProductListAdapter (
    // context and the list of product are to be displayed here
        private val context: Context,
        private val list:ArrayList<Product>,
        private val fragment: ProductFragment

        ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                // return my own viewHolder
                return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_layout, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                // get the current position of each product
                val model = list[position]
                if (holder is MyViewHolder){
                        GlideLoader(context).loadProductImage(model.product_image, holder.itemView.findViewById(R.id.iv_image_item))
                        // set the item name and price
                        holder.itemView.findViewById<TextView> (R.id.tv_item_name).text = model.product_title
                        holder.itemView.findViewById<TextView> (R.id.tv_item_price).text = "(NGN ${model.product_price})"

                        // add onClick event to the image button
                        holder.itemView.findViewById<ImageButton>(R.id.ib_delete_product).setOnClickListener {
                                // call the delete method from the productFragment
                                fragment.deleteProduct(model.product_id)
                        }

                        // make the addProduct activity clickable and navigate to the product details activity
                        holder.itemView.setOnClickListener {
                                val intent = Intent(context, ProductDetailsActivity::class.java)
                                // pass the product id of each product in the cloud Firestore
                                intent.putExtra(Constant.EXTRA_PRODUCT_ID, model.product_id)
                                intent.putExtra(Constant.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                                // start the activity out of the context
                                context.startActivity(intent)
                        }
                }
        }

        override fun getItemCount(): Int {
                return list.size
        }

        private class MyViewHolder(view:View): RecyclerView.ViewHolder(view)
}