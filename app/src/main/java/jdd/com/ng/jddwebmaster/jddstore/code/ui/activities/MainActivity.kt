package jdd.com.ng.jddwebmaster.jddstore.code.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant

class MainActivity : AppCompatActivity() {

    private lateinit var tvHello: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvHello = findViewById(R.id.tvhello)

        // get the sharedpreference
        val getSharedPreference = getSharedPreferences(Constant.JDD_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        val getUsername = getSharedPreference.getString(Constant.LOGGED_IN_USER, "")
        tvHello.text = "Hello ${getUsername}"
    }


}