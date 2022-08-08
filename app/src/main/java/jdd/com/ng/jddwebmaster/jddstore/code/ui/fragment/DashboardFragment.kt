package jdd.com.ng.jddwebmaster.jddstore.code.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore.FirestoreClass
import jdd.com.ng.jddwebmaster.jddstore.code.model.Product
import jdd.com.ng.jddwebmaster.jddstore.code.ui.activities.CartListActivity
import jdd.com.ng.jddwebmaster.jddstore.code.ui.activities.SettingsActivity
import jdd.com.ng.jddwebmaster.jddstore.code.ui.adapters.MyDashboardItemListAdapter

//import jdd.com.ng.jddwebmaster.jddstore.code.myactivities.databinding.FragmentDashboardBinding

class DashboardFragment : BaseFragment() {

    private  lateinit var rv_dashboard_item_list: RecyclerView
    private lateinit var tv_no_dashboard_item_found: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // allow menu in the fragment
        setHasOptionsMenu(true)
    }



    fun successDashboardItemListFromFirestore(dashboardItemList:ArrayList<Product>){
        dismissProgressDialogue()

        if (dashboardItemList.size > 0){
            rv_dashboard_item_list.visibility = View.VISIBLE
            tv_no_dashboard_item_found.visibility = View.GONE

            rv_dashboard_item_list.layoutManager = GridLayoutManager(activity, 2)
            rv_dashboard_item_list.setHasFixedSize(true)

            val dashboardAdapter = MyDashboardItemListAdapter(requireActivity(), dashboardItemList)
            rv_dashboard_item_list.adapter = dashboardAdapter

            // implement the custom onClickListener from the dashboard adapter activity
//           dashboardAdapter.setOnClickListener(object : MyDashboardItemListAdapter.OnClickListener {
//               override fun onClick(position: Int, product: Product) {
//                   val intent = Intent(context, ProductDetailsActivity::class.java)
//                   intent.putExtra(Constant.EXTRA_PRODUCT_ID, product.product_id)
//                   startActivity(intent)
//               }
//           })
        } else{
            rv_dashboard_item_list.visibility = View.GONE
            tv_no_dashboard_item_found.visibility = View.VISIBLE
        }
    }

    private fun getDashboardItemList(){
        showProgressDialogue("Please Wait...")
        FirestoreClass().getDashboardItemList(this)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_dashboard_item_list = view.findViewById(R.id.rv_product_item_dashboardFragment)
        tv_no_dashboard_item_found = view.findViewById(R.id.tv_no_item_found_dashboardFragment)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemList()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    // TODO:ensure the actionBar of each activity is not scrollViewed --- the actionBar should be visible when the user scrolls


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // create the method to activate the menu at the top
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_setting->{
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }

            R.id.action_cart->{
                startActivity(Intent(activity, CartListActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}