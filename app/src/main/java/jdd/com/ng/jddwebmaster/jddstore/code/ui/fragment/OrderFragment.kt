package jdd.com.ng.jddwebmaster.jddstore.code.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore.FirestoreClass
import jdd.com.ng.jddwebmaster.jddstore.code.model.Order
import jdd.com.ng.jddwebmaster.jddstore.code.ui.adapters.OrderAdapter
//import jdd.com.ng.jddwebmaster.jddstore.code.myactivities.databinding.FragmentNotificationsBinding
//import jdd.com.ng.jddwebmaster.jddstore.databinding.FragmentNotificationsBinding
import jdd.com.ng.jddwebmaster.jddstore.databinding.FragmentOrderBinding

class OrderFragment : BaseFragment() {

    private lateinit var rv_my_order_items: RecyclerView
    private lateinit var  tv_no_orders_found: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    fun populateOrderItemList(orderList: ArrayList<Order>){
        dismissProgressDialogue()
        if (orderList.size > 0) {

            rv_my_order_items.visibility = View.VISIBLE
            tv_no_orders_found.visibility = View.GONE

            rv_my_order_items.layoutManager = LinearLayoutManager(activity)
            rv_my_order_items.setHasFixedSize(true)

            val myOrdersAdapter = OrderAdapter(requireActivity(), orderList)
            rv_my_order_items.adapter = myOrdersAdapter
        } else {
            rv_my_order_items.visibility = View.GONE
            tv_no_orders_found.visibility = View.VISIBLE
        }
    }

    private fun getOrderList(){
        showProgressDialogue(resources.getString(R.string.please_wait))
        FirestoreClass().getMyOrderList(this)
    }

    override fun onResume() {
        super.onResume()
        getOrderList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_my_order_items = view.findViewById(R.id.rv_my_order_items)
        tv_no_orders_found = view.findViewById(R.id.tv_no_orders_found)
    }
}