package jdd.com.ng.jddwebmaster.jddstore.code.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore.FirestoreClass
import jdd.com.ng.jddwebmaster.jddstore.code.model.CartItem
import jdd.com.ng.jddwebmaster.jddstore.code.ui.activities.CartListActivity
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant
import jdd.com.ng.jddwebmaster.jddstore.code.utils.GlideLoader
import org.w3c.dom.Text

class CartItemListAdapter (
    private val context: Context,
    private val list: ArrayList<CartItem>,
    private val updateCartItems: Boolean

        ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // return my own viewHolder
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_cart_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder){
            GlideLoader(context).loadProductImage(model.product_image, holder.itemView.findViewById(R.id.iv_cart_item_image))
            // set the item name and price
            holder.itemView.findViewById<TextView> (R.id.tv_cart_item_title).text = model.product_title
            holder.itemView.findViewById<TextView> (R.id.tv_cart_item_price).text = "(NGN ${model.product_price})"
            holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).text = model.cart_quantity

            if (model.cart_quantity == "0"){
                holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility = View.GONE
                holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility = View.GONE

                if (updateCartItems){
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).visibility = View.VISIBLE
                } else{
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).visibility = View.GONE
                }

                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).text = context.resources.getString(R.string.lbl_out_of_stock)

                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).setTextColor( ContextCompat.getColor(context, R.color.red))

            } else{
                if (updateCartItems){
                    holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility = View.VISIBLE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility = View.VISIBLE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).visibility = View.VISIBLE
                } else{
                    holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility = View.GONE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility = View.GONE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).visibility = View.GONE
                }


                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).setTextColor( ContextCompat.getColor(context, R.color.colorSecondaryText))
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).setOnClickListener {
                when(context){
                    is CartListActivity->{
                        context.showProgressDialogue("Removing Item From Cart...")
                        // TODO: Alert Dialog
                    }
                }
                FirestoreClass().removeItemFromCart(context, model.id)
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).setOnClickListener {
                if (model.cart_quantity == "1"){
                    FirestoreClass().removeItemFromCart(context, model.id)
                } else{
                    val cartQuantity: Int = model.cart_quantity.toInt()
                    val itemHashMap = HashMap<String, Any>()
                    itemHashMap[Constant.CART_QUANTITY] = (cartQuantity - 1).toString()

                    if (context is CartListActivity){
                        context.showProgressDialogue("Please Wait...")
                    }
                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                }

            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).setOnClickListener {
                val cartQuantity:Int = model.cart_quantity.toInt()
                if (cartQuantity < model.stock_quantity.toInt()){
                    val itemHashMap = HashMap<String, Any>()
                    itemHashMap[Constant.CART_QUANTITY] = (cartQuantity + 1).toString()
                    if (context is CartListActivity){
                        context.showProgressDialogue("Please Wait...")
                    }
                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                } else{
                    if (context is CartListActivity){
                      context.showSnackBar(context.resources.getString(R.string.msg_for_available_stock, model.stock_quantity), true )
                    }
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

  private  class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}
