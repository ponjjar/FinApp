package com.caiqueponjjar.finapp;

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.caiqueponjjar.finapp.helper.usuario
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

import java.util.*


class friends : Fragment(R.layout.activity_friends){
    var gastosTotais = 0
    val onNotice: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // String pack = intent.getStringExtra("package");
            val title = intent?.getStringExtra("title")
            val text = intent?.getStringExtra("text")
            //int id = intent.getIntExtra("icon",0);
            //  val remotePackageContext: context = null
            try {
//                remotePackageContext = getApplicationContext().createPackageContext(pack, 0);
//                Drawable icon = remotePackageContext.getResources().getDrawable(id);
//                if(icon !=null) {
//                    ((ImageView) findViewById(R.id.imageView)).setBackground(icon);
//                }
                /*   val byteArray = intent.getByteArrayExtra("icon")
                   var bmp: Bitmap? = null
                   if (byteArray != null) {
                       bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                   }*/
                System.out.println("Chegou notificação " + title.toString())

                itemList.add(
                    Item(
                        title.toString(),
                        text.toString(),
                        Color.parseColor("#E05F22"),
                        "testedev",
                        1,
                        "Note",
                        ""
                    )
                )
                Toast.makeText( context,title.toString(), Toast.LENGTH_SHORT).show()
                //Toast.makeText(context,"",)
                /* model.setName("$title $text")
                 model.setImage(bmp)
                 if (modelList != null) {
                     modelList.add(model)
                     adapter.notifyDataSetChanged()
                 } else {
                     modelList = ArrayList<Model>()
                     modelList.add(model)
                     adapter =
                         CustomListAdapter(ApplicationProvider.getApplicationContext(), modelList)
                     list = findViewById(R.id.list) as ListView
                     list.setAdapter(adapter)*/

            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private lateinit var itemList : ArrayList<Item>
    private lateinit var listview : RecyclerView
    private var isOnBackground : Boolean = false
    private lateinit var loadingList : ConstraintLayout
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  val textInfo = arguments?.getString("key")
        //  val textView = view.findViewById<TextView>(R.id.textView)
        //  textView.text = textInfo

        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences(
                "sharedPrefs",
                Context.MODE_PRIVATE
            )
        if(!sharedPreferences.getString("meta", null).isNullOrEmpty()){
            view?.findViewById<TextView>(R.id.welcomeText2).text = sharedPreferences.getString("meta", null).toString() + "R$"
        }

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


        welcomeText.text = gastosTotais.toString()

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
        val metabtn = view.findViewById<LinearLayout>(R.id.meta)
        metabtn.setOnClickListener(
            // on click activate input builder
            {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Qual seu limite?")
                val input = EditText(context)
                input.inputType = InputType.TYPE_CLASS_NUMBER
                // center text
                input.gravity = Gravity.CENTER_HORIZONTAL
                // add text size
                input.textSize = 24f
                builder.setView(input)
                builder.setPositiveButton("Confirmar",
                    DialogInterface.OnClickListener { dialog, which ->
                        val m_Text = input.text.toString()
                        if (m_Text.isEmpty()) {
                            Toast.makeText(context, "Digite um valor", Toast.LENGTH_SHORT).show()
                            return@OnClickListener
                        }
                        val sharedPreferences: SharedPreferences =
                            requireActivity().getSharedPreferences(
                                "sharedPrefs",
                                Context.MODE_PRIVATE
                            )
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("meta", m_Text)
                        editor.apply()

                        view.findViewById<TextView>(R.id.welcomeText2).setText("Meta: " + m_Text + "R$")


                    })
                builder.setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
                builder.show()
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
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(onNotice,  IntentFilter("Msg"));

        val filtrarmes: Button = view.findViewById(R.id.filtrarmes)

        filtrarmes.setOnClickListener({

            val sdf = SimpleDateFormat("MM")
            val currentDate = sdf.format(Date())
            println(currentDate)
            LoadNotes(view, currentDate.toString(), "mes")
            })

        val filtrarsemana: Button = view.findViewById(R.id.filtrarsemana)

        filtrarsemana.setOnClickListener({

            val sdf = SimpleDateFormat("dd")
            val currentDate = sdf.format(Date())
            println(currentDate)
            LoadNotes(view, currentDate.toString(), "semana")
        })
        val filtrartodos: Button = view.findViewById(R.id.filtrartodos)

        filtrartodos.setOnClickListener({

            LoadNotes(view, "12/34/5678", "todos")
        })
        LoadNotes(view, "12/34/5678", "todos")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //requireContext().startService(Intent(requireActivity(), BackgroundService::class.java))
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
    fun LoadNotes (view: View, data: String, tipo: String) {

        //Configurando array adapter para criação de lista...
        var userId = activity?.let { usuario().getUniqueId(it) }
        val rootRef = Firebase.database.reference.child("users").child(userId!!).child("message")

        itemList = ArrayList<Item>()

        var scrollonfinish: Boolean  = false;
        var adapter = ListAdapter( itemList, requireActivity());

        listview.adapter = adapter
        gastosTotais=0

        rootRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println(error!!.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                var itemcountBefore = itemList.count()
                itemList.clear()
                for (postSnapshot in snapshot.children) {
                    //Carregando lista de dados
                    val text = postSnapshot.child("subtitle").getValue(String::class.java)
                    val subtitle = text.toString().split("-@").toTypedArray()[0]
                    var date = ""
                    if(text.toString().split("-@").toTypedArray().size >= 2) {
                        date = text.toString().split("-@").toTypedArray()[1]
                    }
                    var title = postSnapshot.child("title").getValue(String::class.java)
                    var itemColor = postSnapshot.child("color").getValue(Int::class.java)
                    var key = postSnapshot.child("key").getValue(String::class.java)
                    var category = postSnapshot.child("category").getValue(String::class.java)
                    var status = postSnapshot.child("status").getValue(String::class.java)
                    if(category == "null"){
                        category = "-1";
                    }

                    println("gastos" + gastosTotais + "\n subtitle:" + title?.dropLast(2))

                    if(tipo == "mes"){
                        if(data.toString() == date.toString().drop(3).dropLast(5)){
                            if (title?.dropLast(2)?.toIntOrNull() != null) {
                                val welcomeText = view.findViewById<TextView>(R.id.welcomeText)
                                gastosTotais = gastosTotais + title.dropLast(2).toInt()
                                welcomeText.text = gastosTotais.toString() + "R$"
                                //Write your code you want to execute if myString is (Int)
                            } else {
                                //Write your code you want to execute if myString is (not Int)
                            }
                            itemList.add(
                                Item(
                                    title.toString(),
                                    subtitle.toString(),
                                    itemColor?.toInt() ?: Color.parseColor("#E05F22"),
                                    key.toString(),
                                    getResources().getIdentifier(usuario().getIcons(category?.toInt() ?: 0) , "drawable", requireActivity().packageName)?: R.drawable.roundedconers,
                                    "Note",
                                    date.toString()
                                )
                            )
                        }
                    }else if(tipo == "semana"){
                        if(date.toString().length >= 8){
                            print("semana: "+ date.toString().dropLast(8).toInt() + "data>"+ data + ",")
                            if(data.toInt() - 7 < date.toString().dropLast(8).toInt()){
                                if (title?.dropLast(2)?.toIntOrNull() != null) {
                                    val welcomeText = view.findViewById<TextView>(R.id.welcomeText)
                                    gastosTotais = gastosTotais + title.dropLast(2).toInt()
                                    welcomeText.text = gastosTotais.toString() + "R$"
                                    //Write your code you want to execute if myString is (Int)
                                } else {
                                    //Write your code you want to execute if myString is (not Int)
                                }
                                itemList.add(
                                    Item(
                                        title.toString(),
                                        subtitle.toString(),
                                        itemColor?.toInt() ?: Color.parseColor("#E05F22"),
                                        key.toString(),
                                        getResources().getIdentifier(usuario().getIcons(category?.toInt() ?: 0) , "drawable", requireActivity().packageName)?: R.drawable.roundedconers,
                                        "Note",
                                        date.toString()
                                    )
                                )
                            }
                        }
                    }else if (tipo == "todos"){

                        if (title?.dropLast(2)?.toIntOrNull() != null) {
                            val welcomeText = view.findViewById<TextView>(R.id.welcomeText)
                            gastosTotais = gastosTotais + title.dropLast(2).toInt()
                            welcomeText.text = gastosTotais.toString() + "R$"
                            //Write your code you want to execute if myString is (Int)
                        } else {
                            //Write your code you want to execute if myString is (not Int)
                        }
                        try {

                        itemList.add(
                            Item(
                                title.toString(),
                                subtitle.toString(),
                                itemColor?.toInt() ?: Color.parseColor("#E05F22"),
                                key.toString(),
                                getResources().getIdentifier(usuario().getIcons(category?.toInt() ?: 0) , "drawable", requireActivity().packageName)?: R.drawable.roundedconers,
                                "Note",
                                date.toString()
                            )
                        )
                        } catch (e: Exception) {
                            println("erro: " + e)
                        }

                    }




                }
                val sharedPreferences = activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                val arrayDicas = arrayOf(
                    "Você sabia que o Brasil é o país que mais gasta com juros no mundo? Economize!",
                    "Você sabia que a cada 100 reais que você gasta, 40 reais são de impostos? Economize!",
                    "Está com dificuldades para economizar? Tente anotar todos os seus gastos!",
                    "Estabeleça metas de economia para você e sua família!",
                    "E se você tentasse economizar 10% do seu salário?",
                    "Seus gastos totais foram " + gastosTotais + "R$! Tente economizar mais!",

                    )
                val colors = arrayOf(
                    Color.parseColor("#8451ad"),
                    Color.parseColor("#515fad"),
                    Color.parseColor("#ad51ad"),
                    Color.parseColor("#ad5151"),
                    Color.parseColor("#ad7d51"),
                    Color.parseColor("#adad51"),
                    Color.parseColor("#7dad51"),
                    Color.parseColor("#51ad51"),
                    Color.parseColor("#51ad7d"),

                )
                if(tipo == "todos" && gastosTotais < 100){
                    // crie um array com dicas de economias aleatórias


                    itemList.add(
                        Item(
                            "Veja sua economia clicando nos filtros",
                            "Clique no botão azul para adicionar",
                            colors.random(),
                            "",
                            R.drawable.logo4,
                            "ClickItem",
                            ""
                        )
                    )
                }else if (tipo == "mes" && gastosTotais < 50) {
                    // give another tip
                    itemList.add(
                        Item(
                            "Controle seus gastos mensais",
                            "Clique no botão azul para adicionar",
                            colors.random(),
                            "",
                            R.drawable.logo4,
                            "ClickItem",
                            ""
                        )
                    )
                }else if (tipo == "semana" && gastosTotais < 50) {
                    // give another tip
                    itemList.add(
                        Item(
                            "Controle seus gastos semanais",
                            "Clique no botão azul para adicionar",
                            colors.random(),
                            "",
                            R.drawable.logo4,
                            "ClickItem",
                            ""
                        )
                    )
                }
                if ( sharedPreferences?.getString("meta", "0")?.toInt()!! < gastosTotais) {
                    // give another tip
                    if (
                        gastosTotais > sharedPreferences?.getString("meta", "0")!!.toInt() * 2
                    ) {
                        itemList.add(
                            Item(
                                "Você está gastando muito! Já atingiu o dobro da seu limite!",
                                "Clique no botão azul para adicionar",
                                colors.random(),
                                "",
                                R.drawable.logo4,
                                "ClickItem",
                                ""
                            )
                        )
                    }else if (gastosTotais > sharedPreferences?.getString("meta", "0")!!.toInt() * 1.5) {
                        itemList.add(
                            Item(
                                "Você está gastando muito! Já atingiu 1.5x do seu limite!",
                                "Clique no botão azul para adicionar",
                                colors.random(),
                                "",
                                R.drawable.logo4,
                                "ClickItem",
                                ""
                            )
                        )
                    }else if (gastosTotais > sharedPreferences?.getString("meta", "0")!!.toInt() * 1.25) {
                        itemList.add(
                            Item(
                                "Já atingiu 1.25x do seu limite!",
                                "Clique no botão azul para adicionar",
                                colors.random(),
                                "",
                                R.drawable.logo4,
                                "ClickItem",
                                ""
                            )
                        )
                    }else if (gastosTotais > sharedPreferences?.getString("meta", "0")!!.toInt() * 1.1) {
                    itemList.add(
                        Item(
                            "Você ultrapassou sua meta de economia! ",
                            "Clique no botão azul para adicionar",
                            colors.random(),
                            "",
                            R.drawable.logo4,
                            "ClickItem",
                            ""
                        )
                    )
                    }
                     }else if (gastosTotais > 50 ) {
                    itemList.add(
                        Item(

                            arrayDicas.random(),
                            "Você está gastando muito!",
                            colors.random(),
                            "",
                            R.drawable.logo4,
                            "ClickItem",
                            ""
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
                            ""
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
            }catch (e:Exception) {
                    System.out.println(
                        "Erro: " + e
                    )
                }
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
    }

    override fun onResume() {
        super.onResume()
        isOnBackground = false
    }

    override fun onPause() {
        super.onPause()
        isOnBackground = true
    }


}