package com.caiqueponjjar.cuco

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ClipDrawable.VERTICAL
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import com.caiqueponjjar.cuco.helper.usuario
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet


class ListAdapter(val itemList: ArrayList<Item>, val activity: Activity) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {


    val itensListed = ArrayList<String>()
    override fun onBindViewHolder(p0: ViewHolder, @SuppressLint("RecyclerView") p1: Int) {    //p0 = ViewHolder, p1 = position
        if (itemList[p1].itemTitle.isNotEmpty() || itemList[p1].itemTitle != null) {
            p0.titleTextView.text = itemList[p1].itemTitle
        p0.subtitleTextView.text = itemList[p1].itemSubtitle

    }
        if(itemList[p1].itemColor == Color.parseColor("#00000000")){
            p0.itemColor.visibility = View.GONE
        }
        p0.itemColor.setColorFilter(    //seta a cor do item
            itemList[p1].itemColor, android.graphics.PorterDuff.Mode.SRC_IN
        )
        p0.itemCategory.setImageResource(    //seta a categoria do item
            itemList[p1].itemCategory
        )
        if (itemList[p1].itemType.equals("Searcher")) {
          /*  var anim = AnimationUtils.loadAnimation(
                activity, R.anim.toptodown
            )

            anim.duration = 150

            p0.itemView.startAnimation(anim)*/

            p0.itemView.setOnClickListener {
                println("Clicado:" + itemList[p1].itemTitle)
                // val builder = AlertDialog.Builder(activity)
                //   builder.setTitle(itemList[p1].itemTitle)
                //   builder.setMessage(itemList[p1].itemSubtitle)
                OptionsSheet().show(activity) {
                    title(itemList[p1].itemTitle)
                    //displayMode(DisplayMode.GRID_VERTICAL)
                    preventIconTint(true)
                    multipleChoices(false)
                    with(
                        Option(android.R.drawable.ic_menu_share, "Compartilhar"),
                        Option(R.drawable.iconuser, "Enviar nota"),
                        // Option(R.drawable.iconvideogame, "Editar"),
                    )
                    displayButtons(false)
                    onNegative("Voltar") {

                    }
                    onPositive { index: Int, option: Option ->
                        // Handle selected option
                        if (index == 0) {

                            val shareIntent = Intent()
                            shareIntent.action = Intent.ACTION_SEND
                            shareIntent.type = "text/plain"
                            shareIntent.putExtra(
                                Intent.EXTRA_TEXT,
                                if(!itemList[p1].itemSubtitle.isEmpty()) itemList[p1].itemTitle  + ".\n" + itemList[p1].itemSubtitle else itemList[p1].itemTitle
                            )
                            activity?.startActivity(Intent.createChooser(shareIntent, "Compartilhar"))
                        } else if (index == 1) {
                            var activity:Activity = activity as Activity
                            val builder = AlertDialog.Builder(requireContext())
                            builder.setTitle("Enviar nota")
                            builder.setMessage("Digite o nome da nota:")
                            val input = EditText(activity)
                            builder.setView(input)
                            builder.setPositiveButton("Enviar") { dialog, which ->
                                val noteName = input.text.toString()
                                if (noteName.isEmpty()) {

                                } else {
                                    usuario().sendData(activity, noteName, " ", Color.parseColor("#E05F22"), "-1", itemList[p1].itemKey)
                                   // val db = NoteDatabase(activity)
                                   // db.insertNote(note)
                                }
                            }
                            builder.setNegativeButton("Cancelar") { dialog, which ->
                                dialog.cancel()
                            }
                            builder.show()
                        }
                    }
                }
            }


        }
        else if (itemList[p1].itemKey.equals("CucoMessage")) {
            var anim = AnimationUtils.loadAnimation(
                activity, R.anim.fadein
            )

            anim.duration = 500

            p0.itemView.startAnimation(anim)

        } else {
            var animating : Boolean = p0.animating
            if (!itensListed.contains(itemList[p1].itemKey)) {

                itensListed.add(itemList[p1].itemKey)
                var anim: Animation = AnimationUtils.loadAnimation(
                    activity, android.R.anim.slide_in_left
                )
                if (itemList.size - p1 > 1) {
                    if (itemList.size - p1 > 7) {
                        anim = AnimationUtils.loadAnimation(
                            activity, R.anim.toptodown
                        )
                        anim.duration = 500
                        anim.startOffset = 200;
                    } else {

                        anim.duration = ((itemList.size - p1) * 100).toLong().toLong()
                        anim.startOffset = (((itemList.size - p1) * 100) - 50).toLong();
                    }
                } else {
                    anim.duration = 400
                    anim.startOffset = 0
                }
                p0.itemView.startAnimation(anim)
            }

        var x1: Float? =null
        var x2: Float? =null

            if (!itemList[p1].itemKey.equals("CucoMessage")) {
        p0.itemView.setOnTouchListener { e, motionEvent ->



            val min_distance: Int  = 30
            var deltaX = 0.0f
            when (motionEvent.action) {

                MotionEvent.ACTION_DOWN -> {
                    //when user touch down
                    x1 = motionEvent.getX();
                }
                MotionEvent.ACTION_MOVE -> {
                    x2 = motionEvent.getX()
                    if(Math.abs(deltaX) <= min_distance  && deltaX <= 0) {
                        deltaX = x2!! - x1!!
                    }
                    if (Math.abs(deltaX) > min_distance && deltaX > 0) {

                        println(deltaX)
                        var anim: Animation = AnimationUtils.loadAnimation(
                            activity, android.R.anim.slide_out_right
                        )
                        anim.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(arg0: Animation) {
                                animating = true
                            }
                            override fun onAnimationRepeat(arg0: Animation) {}
                            override fun onAnimationEnd(arg0: Animation) {
                                usuario().deleteData(activity, itemList[p1].itemKey)
                                animating = false
                            }
                        })

                        if(animating == false) {
                            p0.itemView.startAnimation(anim)
                        }
                        //  Toast.makeText(this, "left2right swipe", Toast.LENGTH_SHORT).show()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    //when user touch release
                    x2 = motionEvent.getX()
                    var deltaX = x2!! - x1!!
                    if (Math.abs(deltaX) > min_distance && deltaX > 0) {

                        println(deltaX)
                        var anim: Animation = AnimationUtils.loadAnimation(
                            activity, android.R.anim.slide_out_right
                        )
                        anim.duration =  500
                        if(animating == false) {
                            p0.itemView.startAnimation(anim)
                        }
                        anim.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(arg0: Animation) {
                                animating = true}
                            override fun onAnimationRepeat(arg0: Animation) {}
                            override fun onAnimationEnd(arg0: Animation) {
                                usuario().deleteData(activity, itemList[p1].itemKey)
                                animating = false
                            }
                        })
                      //  Toast.makeText(this, "left2right swipe", Toast.LENGTH_SHORT).show()
                    } else {
                        // consider as something else - a screen tap for example
                        p0.itemView.callOnClick()
                    }
                }
            }
            true
        }}

        p0.itemView.setOnClickListener {
            println("Clicado:" + itemList[p1].itemTitle)
            // val builder = AlertDialog.Builder(activity)
            //   builder.setTitle(itemList[p1].itemTitle)
            //   builder.setMessage(itemList[p1].itemSubtitle)
            OptionsSheet().show(activity) {
                title(itemList[p1].itemTitle)
                //displayMode(DisplayMode.GRID_VERTICAL)
                preventIconTint(false)
                multipleChoices(false)
                with(
                    Option(android.R.drawable.ic_menu_share, "Compartilhar"),
                    Option(R.drawable.iconbin, "Deletar"),
                    // Option(R.drawable.iconvideogame, "Editar"),
                )
                displayButtons(false)
                onNegative("Voltar") {

                }
                onPositive { index: Int, option: Option ->
                    // Handle selected option
                    if (index == 0) {

                        val shareIntent = Intent()
                        shareIntent.action = Intent.ACTION_SEND
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(
                            Intent.EXTRA_TEXT,
                            if(!itemList[p1].itemSubtitle.isEmpty()) itemList[p1].itemTitle  + ".\n" + itemList[p1].itemSubtitle else itemList[p1].itemTitle
                        )
                        activity?.startActivity(Intent.createChooser(shareIntent, "Compartilhar"))
                    } else if (index == 1) {
                        var anim: Animation = AnimationUtils.loadAnimation(
                            activity, android.R.anim.fade_out
                        )
                        anim.duration = 100

                        if (animating == false) {
                            p0.itemView.startAnimation(anim)
                        }
                        var activity:Activity = activity as Activity
                        anim.setAnimationListener( object : Animation.AnimationListener {
                            override fun onAnimationStart(arg0: Animation) {
                                animating = true
                            }

                            override fun onAnimationRepeat(arg0: Animation) {}
                            override fun onAnimationEnd(arg0: Animation) {
                                usuario().deleteData(activity, itemList[p1].itemKey)
                                animating = false
                            }
                        })
                    }
                }
            }
        }
        }
           /* builder.setNegativeButton("deletar") { dialog, which ->
                //Toast.makeText(applicationContext,"continuar",Toast.LENGTH_SHORT).show()



            }
            builder.setNeutralButton(
                "compartilhar"
            ) {  dialog, which ->
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "" + itemList[p1].itemTitle + ": " + itemList[p1].itemSubtitle
                    )
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                activity.startActivity(shareIntent)
            }
            val dialog: AlertDialog = builder.create()

            dialog.show()
            val buttonNegative: Button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            buttonNegative.setTextColor(Color.parseColor("#960909"))
            val buttonNeutral: Button = dialog.getButton(DialogInterface.BUTTON_NEUTRAL)
            buttonNeutral.setTextColor(Color.parseColor("#009c3a"))
            val buttonPositive: Button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            buttonPositive.setTextColor(Color.parseColor("#00707a"))
        }
            //buttonPositive.setTextColor(BLUE)
          //  buttonbackground1.setBackgroundDrawable( activity.resources.getDrawable(R.drawable.roundedconers))
           /*  val fragment = details();
            val bundle = Bundle().apply {
                putString("Titulo", itemList[p1].itemTitle)
               putString("Subtitulo", itemList[p1].itemSubtitle)
             }
            fragment.arguments = bundle
            parentFragmentManager.beginTransaction().add(R.id.fragment_container_view, fragment).addToBackStack(null).commit()*/
        }*/
        //p0?.txtTitle?.text = androidVersionList[p1].codeName
        //p0?.txtContent?.text = "Version : ${androidVersionList[p1].versionName}, Api Name : ${androidVersionList[p1].apiLevel}"
        //p0?.image.setImageResource(androidVersionList[p1].imgResId!!)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.activity_listitem, p0, false)
        return ViewHolder(v);
    }
    /*override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        //val phraseIndex = position
        convertView = LayoutInflater.from(context).inflate(
            R.layout.activity_listitem,
            null
        )
        val titleTextView = convertView.findViewById<TextView>(R.id.title_textview)
        val authorTextView = convertView.findViewById<TextView>(R.id.subtitle_textview)

        titleTextView.text = itemList[position].itemTitle
        authorTextView.text = itemList[position].itemSubtitle
        return convertView
    }*/
    public override fun getItemCount(): Int {
        return itemList.size
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         var titleTextView : TextView = itemView.findViewById<TextView>(R.id.title_textview)
         var subtitleTextView : TextView = itemView.findViewById<TextView>(R.id.subtitle_textview)
         var cardView : CardView = itemView.findViewById(R.id.card_pertanyaan)
         var itemColor : ImageView = itemView.findViewById(R.id.colorItem)

        var itemCategory : ImageView = itemView.findViewById(R.id.category_image)
            var animating : Boolean = false
        //titleTextView.text = itemList[position].itemTitle
        //authorTextView.text = itemList[position].itemSubtitle
    }
}