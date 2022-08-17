package com.caiqueponjjar.cuco

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caiqueponjjar.cuco.helper.usuario
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList


class search : Fragment(R.layout.activity_search){

    private lateinit var itemList : ArrayList<Item>
    private lateinit var listview : RecyclerView
    private lateinit var loadingList : ConstraintLayout
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemList = ArrayList<Item>()
        var adapter = ListAdapter( itemList, requireActivity());
        //Configurando array adapter para criação de lista...
        var userId = activity?.let { usuario().getUniqueId(it) }
        val rootRef = Firebase.database.reference.child("users").child(userId!!).child("message")
        val mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference
        val search = view.findViewById<ImageView>(R.id.SearchBox)
        listview = view.findViewById<RecyclerView>(R.id.contacts)
        listview.setItemViewCacheSize(7);
        listview.setHasFixedSize(true);
        listview.setDrawingCacheEnabled(true);
        listview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        var mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,true)
        //var mLayoutManager = GridLayoutManager(activity,3)
        mLayoutManager.reverseLayout = false;
        mLayoutManager.stackFromEnd = false;

        listview.layoutManager =mLayoutManager

        listview.adapter = adapter
        var searchText = view.findViewById<TextInputEditText>(R.id.search_bar)
        searchText.doOnTextChanged(
            {
                text, start, count, after ->

                if(text!!.length > 0){

                    itemList.clear()
                    val query = mFirebaseDatabaseReference.child("users").orderByChild("username").startAt(view.findViewById<TextInputEditText>(R.id.search_bar).text.toString())
                    query.addValueEventListener(object : EventListener, ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var itemcountBefore = itemList.count()
                            itemList.clear()
                            for (postSnapshot in dataSnapshot.children) {
                                val title =  postSnapshot.child("username").getValue(String::class.java)
                                val userKey = postSnapshot.key.toString()
                                var itemColor = "-2072798"
                                var category = "-1"
                                itemList.add(
                                    Item(
                                        title.toString(),
                                        " ",
                                        itemColor?.toInt() ?: Color.parseColor("#E05F22"),
                                        userKey.toString(),
                                        getResources().getIdentifier(usuario().getIcons(category?.toInt() ?: 0) , "drawable", "com.caiqueponjjar.cuco")?: R.drawable.roundedconers,
                                        "Searcher"
                                    )
                                )
                                var adapter = ListAdapter( itemList, requireActivity());

                            }
                            if(itemList.count() != itemcountBefore) {
                                if (listview.adapter == null) {
                                    listview.adapter = adapter
                                } else {
                                    System.out.println("adapter")
                                    listview.adapter?.notifyDataSetChanged();
                                }
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            // Getting Item failed, log a message
                            // ...
                        }
                    })
                }else{
                    itemList.clear()
                }


            }
        )

    }
}