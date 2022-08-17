package com.caiqueponjjar.cuco;

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.caiqueponjjar.cuco.helper.usuario
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*


class FirstFragment : Fragment(R.layout.activity_firstfragment){
    private lateinit var itemList : ArrayList<Item>
    private lateinit var listview : RecyclerView
    private var isOnBackground : Boolean = false
    private lateinit var loadingList : ConstraintLayout
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

          //  val textInfo = arguments?.getString("key")
          //  val textView = view.findViewById<TextView>(R.id.textView)
          //  textView.text = textInfo
        var welcomeText = view.findViewById<TextView>(R.id.welcomeText)
        loadingList = view.findViewById<ConstraintLayout>(R.id.LoadingConstraint)
        var loadingImage = view.findViewById<ImageView>(R.id.LoadingImage)
        var anim: Animation = AnimationUtils.loadAnimation(
            activity, R.anim.floating
        )
        // This callback will only be called when MyFragment is at least Started.
        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        anim.repeatCount = Animation.INFINITE
        loadingImage.startAnimation(anim)
        var EyesBird = view.findViewById<TextView>(R.id.BirdEyes)
        var animEyes: Animation = AnimationUtils.loadAnimation(
            activity, R.anim.eyes
        )
        animEyes.repeatCount = Animation.INFINITE
        EyesBird.startAnimation(animEyes)


        welcomeText.text = "Olá, " + usuario().getUsername(requireActivity())

        val pullToRefresh: SwipeRefreshLayout = view.findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
             // your code
            pullToRefresh.isRefreshing = false
        }
        listview = view.findViewById<RecyclerView>(R.id.list_item)
        listview.setItemViewCacheSize(7);
        listview.setHasFixedSize(true);
        listview.setDrawingCacheEnabled(true);
        listview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        var pesquisar = view.findViewById<ImageView>(R.id.search)
        pesquisar.setOnClickListener({
            Toast.makeText(context, "Search", Toast.LENGTH_SHORT).show()
            val nextFrag = search()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, nextFrag)
                .addToBackStack(null)
                .commit()
        })
        val FloatButton = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        FloatButton.setOnClickListener {
         /*   val nextFrag = SecondFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit()*/

         //val editNameDialogFragment: EditNameDialogFragment = EditNameDialogFragment.newInstance("Some Title")
            val pop = SecondFragment()
            val fm = requireActivity().supportFragmentManager

            if (fm != null) {
                pop.show(fm, "name")
             //   fm.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
                pop.dialog?.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
            }




        /*if(! arguments?.getString("Titulo").equals(null)){
            itemList.add(Item(arguments?.getString("Titulo").toString(),
                arguments?.getString("Subtitulo").toString()
            ))
            listview.adapter =ListAdapter( itemList)
        }

        listview.adapter = ListAdapter( itemList)*/

        //var mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,true)
        var mLayoutManager = GridLayoutManager(activity,2)
       // mLayoutManager.reverseLayout = true;
       // mLayoutManager.stackFromEnd = true;

        listview.layoutManager =mLayoutManager

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //requireContext().startService(Intent(requireActivity(), BackgroundService::class.java))
        LoadNotes()
    }

    fun RecyclerView.smoothScrollToPosition(
        recyclerView: RecyclerView?,
        state: RecyclerView.State?, position: Int
    ) {
        val smoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(context) {
            //This controls the direction in which smoothScroll looks
            //for your view
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {

                    println("posicação:"+targetPosition)
                    return this
                        .computeScrollVectorForPosition(targetPosition)
            }

            //This returns the milliseconds it takes to
            //scroll one pixel.
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 990f / displayMetrics.densityDpi
            }
        }
        smoothScroller.targetPosition = position
        this.layoutManager?.startSmoothScroll(smoothScroller)
    }
    fun LoadNotes () {

        //Configurando array adapter para criação de lista...
        var userId = activity?.let { usuario().getUniqueId(it) }
        val rootRef = Firebase.database.reference.child("users").child(userId!!).child("message")

        itemList = ArrayList<Item>()

        var scrollonfinish: Boolean  = false;
        var adapter = ListAdapter( itemList, requireActivity());
        rootRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println(error!!.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                var itemcountBefore = itemList.count()
                itemList.clear()
                for (postSnapshot in snapshot.children) {
                    //Carregando lista de dados

                    var subtitle = postSnapshot.child("subtitle").getValue(String::class.java)
                    var title = postSnapshot.child("title").getValue(String::class.java)
                    var itemColor = postSnapshot.child("color").getValue(Int::class.java)
                    var key = postSnapshot.child("key").getValue(String::class.java)
                    var category = postSnapshot.child("category").getValue(String::class.java)
                    var status = postSnapshot.child("status").getValue(String::class.java)
                    if(category == "null"){
                        category = "-1";
                    }
                    itemList.add(
                        Item(
                            title.toString(),
                            subtitle.toString(),
                            itemColor?.toInt() ?: Color.parseColor("#E05F22"),
                            key.toString(),
                            getResources().getIdentifier(usuario().getIcons(category?.toInt() ?: 0) , "drawable", requireActivity().packageName)?: R.drawable.roundedconers,
                            "Note"
                        )
                    )




                }
                scrollonfinish = false
                System.out.println("Scroll on finish: " +itemList.count() +", " + itemcountBefore)
                if(itemList.count() > itemcountBefore){
                    scrollonfinish=true
                    if(isOnBackground){
                        if(itemList[itemList.count()-1].itemSubtitle != "null"){
                            onAppBackgrounded(itemList[itemList.size - 1].itemTitle + "",itemList[itemList.size - 1].itemSubtitle + "",itemList[itemList.size - 1].itemKey+"", "sent", userId)
                        }
                        else{
                            onAppBackgrounded(itemList[itemList.size - 1].itemTitle + "","Sem subtitulo.", itemList[itemList.size - 1].itemKey,"sent", userId)
                        }



                    }
                }
                if(itemList.count() == 0){

                    itemList.add(
                        Item(
                            "Gastou?",
                            "Clique no botão azul para adicionar",
                            Color.parseColor("#E05F22"),
                            "",
                            R.drawable.logo4,
                            "CucoMessage",
                        )
                    )

                    listview.adapter = adapter
                }
                adapter = ListAdapter( itemList, requireActivity());
                loadingList.visibility = View.GONE
                if(listview.adapter == null) {
                    listview.adapter = adapter
                }else {
                    listview.adapter?.notifyDataSetChanged();
                    if(scrollonfinish == true){
                        listview.smoothScrollToPosition(itemList.count())
                    }
                }
                //listview.adapter = adapter
            }
        })
    }
    fun onAppBackgrounded( title:String, subtitle:String, key:String, status:String, UserID:String){
                  //  var itemColor = postSnapshot.child("color").getValue(Int::class.java)
                  //  var status = postSnapshot.child("status").getValue(String::class.java)
                    if (status == "sent") {
                        var builder = NotificationCompat.Builder(requireContext(), "0")
                            .setSmallIcon(R.drawable.logo4)
                            .setContentTitle(title.toString())
                            .setContentText(subtitle.toString())
                            //.setStyle(NotificationCompat.BigTextStyle()
                            //  .bigText("Much longer text that cannot fit one line..."))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        createNotificationChannel()
                        var notificationManager = NotificationManagerCompat.from(requireContext())

                        val random = Random()
                        var m: Int = random.nextInt(9999 - 1000) + 1000
                        notificationManager.notify(m, builder.build())
                     //   usuario().ChangeStatus("read", UserID, key.toString())
                    }
            }
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name ="Mensagens"
            val descriptionText = "Mensagens recebidas"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("0", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }
    override fun onStart() {
        super.onStart()
        LoadNotes()
    }

    override fun onResume() {
        super.onResume()
        isOnBackground = false
        LoadNotes()
    }

     override fun onPause() {
        super.onPause()
        isOnBackground = true
    }
}
