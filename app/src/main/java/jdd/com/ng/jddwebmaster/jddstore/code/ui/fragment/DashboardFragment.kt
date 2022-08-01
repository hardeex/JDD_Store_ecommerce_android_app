package jdd.com.ng.jddwebmaster.jddstore.code.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore.FirestoreClass
import jdd.com.ng.jddwebmaster.jddstore.code.model.Product
import jdd.com.ng.jddwebmaster.jddstore.code.ui.activities.SettingsActivity
import jdd.com.ng.jddwebmaster.jddstore.code.ui.adapters.MyDashboardAdapter
import jdd.com.ng.jddwebmaster.jddstore.code.ui.adapters.MyProductListAdapter
//import jdd.com.ng.jddwebmaster.jddstore.code.myactivities.databinding.FragmentDashboardBinding
import jdd.com.ng.jddwebmaster.jddstore.databinding.FragmentDashboardBinding

class DashboardFragment : BaseFragment() {

    private  lateinit var rv_dashboard_item_list: RecyclerView
    private lateinit var tv_no_dashboard_item_found: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // allow menu in the fragment
        setHasOptionsMenu(true)
    }

    private fun getDashboardItemList(){
        showProgressDialogue("Please Wait...")
        FirestoreClass().getDashboardItemList(this)
    }

    fun successDashboardItemListFromFirestore(dashboardItemList: ArrayList<Product>){
        dismissProgressDialogue()

        if (dashboardItemList.size > 0){
            rv_dashboard_item_list.visibility = View.VISIBLE
            tv_no_dashboard_item_found.visibility = View.GONE

            rv_dashboard_item_list.layoutManager = GridLayoutManager(activity, 2)
            rv_dashboard_item_list.setHasFixedSize(true)

            val dashboardAdapter = MyDashboardAdapter(requireActivity(), dashboardItemList)
            rv_dashboard_item_list.adapter = dashboardAdapter
        } else{
            rv_dashboard_item_list.visibility = View.GONE
            tv_no_dashboard_item_found.visibility = View.VISIBLE
        }
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
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        return root
    }

    // TODO:ensure the actionBar of each activity is not scrollViewed --- the actionBar should be visible when the user scrolls


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // create the method to activate the menu at the top
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.setting->{
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}