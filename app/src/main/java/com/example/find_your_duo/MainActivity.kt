package com.example.find_your_duo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lorentzos.flingswipe.SwipeFlingAdapterView


class MainActivity : AppCompatActivity() {
  val al: MutableList<String> = mutableListOf()

    private val i = 0
    private var arrayAdapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        al.add("php");
        al.add("c");
        al.add("python");
        al.add("java");
        al.add("html");
        al.add("c++");
        al.add("css");
        al.add("javascript");

        arrayAdapter = ArrayAdapter(this, R.layout.item, R.id.name, al)
        val flingContainer = findViewById<View>(R.id.frame) as SwipeFlingAdapterView
        flingContainer.adapter = arrayAdapter
        flingContainer.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!")
                al?.removeAt(0)
                arrayAdapter!!.notifyDataSetChanged()
            }

            override fun onLeftCardExit(p0: Any?) {
                Toast.makeText(this@MainActivity, "Left", Toast.LENGTH_SHORT).show()
            }

            override fun onRightCardExit(p0: Any?) {
                Toast.makeText(this@MainActivity, "Right", Toast.LENGTH_SHORT).show()
            }

            override fun onAdapterAboutToEmpty(p0: Int) {}

            override fun onScroll(p0: Float) {}
        })
        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener { itemPosition, dataObject -> Toast.makeText(this@MainActivity, "Item Clicked", Toast.LENGTH_SHORT).show() }
    }
}