package com.caiqueponjjar.finapp;

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.caiqueponjjar.finapp.helper.usuario
import com.google.android.material.button.MaterialButton
import com.maxkeppeler.sheets.color.ColorSheet
import com.maxkeppeler.sheets.info.InfoSheet
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import java.text.SimpleDateFormat
import java.util.*

public class SecondFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var myView = inflater!!.inflate(R.layout.activity_secondfragment
            , container, false)

        val titulo = myView.findViewById<EditText>(R.id.EdtTitulo).text
            val subtitulo = myView.findViewById<EditText>(R.id.EdtSubtitulo).text
            val buttonShare = myView.findViewById<ImageButton>(R.id.ShareBtn)
        val buttonAdicionar = myView.findViewById<MaterialButton>(R.id.AddBtn)

        var category : String? = "null"
        var colorPicked: Int = Color.parseColor("#2291e0");
        val colorTextPrefered = myView.findViewById<ImageView>(R.id.imageViewColor)
        colorTextPrefered.setColorFilter(colorPicked - 5, android.graphics.PorterDuff.Mode.MULTIPLY)
        var getThisDialog = dialog;

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = sdf.format(Date())
        buttonAdicionar.setOnClickListener{
            if(titulo.toString().length >= 1 && subtitulo.toString().length >= 1){
                usuario().commitNewData(
                    requireActivity(),
                    titulo.toString() + "R$",
                    subtitulo.toString() + "-@" + currentDate,  // estilo da data: -@ 09/10/2022
                    colorPicked,
                    category.toString()

                )
       // val fragment = FirstFragment();
      //val bundle = Bundle().apply { putString("Titulo", titulo.toString())
        //    putString("Subtitulo", subtitulo.toString())
       // }
        //fragment.arguments = bundle
     //   parentFragmentManager.beginTransaction() .replace(R.id.fragment_container_view, fragment).addToBackStack(null).commit()
            dismiss()
            }else{

                InfoSheet().show(requireContext()) {
                    title("Opss! Você esqueceu de preencher algum campo!")
                    content("Que tal preencher todos os campos para adicionar um novo item?")
                    onPositive("OK") {

                    }

                    if(titulo.toString().length > 1){
                        onNegative("Adicionar mesmo assim") {
                            usuario().commitNewData(
                                requireActivity(),
                                titulo.toString(),
                                subtitulo.toString(),
                                colorPicked,
                                category.toString()
                            )

                            getThisDialog?.dismiss()
                        }
                    }else{
                        onNegative("VOLTAR") {
                            getThisDialog?.dismiss()
                        }
                    }

                }
            }
        }
        val button = Button(context, null, android.R.attr.buttonStyle)
        button.setTextSize(14f)
        button.setTextColor(Color.parseColor("#E05F22"))
        val colorButton = myView.findViewById<ImageView>(R.id.BtnColorpicker)
        myView.findViewById<ImageView>(R.id.categoryIcon).setColorFilter(colorPicked, android.graphics.PorterDuff.Mode.MULTIPLY)
        myView.findViewById<ImageView>(R.id.dateIcon).setColorFilter(colorPicked, android.graphics.PorterDuff.Mode.MULTIPLY)
       // val sheet = ColorSheet().build(requireActivity())
        //
        myView.findViewById<ConstraintLayout>(R.id.Category).setOnClickListener{
            OptionsSheet().show(requireContext()) {
                title("Selecione um icone")
                displayMode(DisplayMode.GRID_VERTICAL)
                preventIconTint(true)
                multipleChoices(false)
                with(
                    Option(R.drawable.iconfavourite, "favorite"),
                    Option(R.drawable.iconstar, "Estrela"),
                    Option(R.drawable.iconvideogame, "VideoGame"),
                    Option(R.drawable.iconlightbulb, "Lampada"),
                    Option(R.drawable.iconwallclock, "Relógio"),
                    Option(R.drawable.iconcalories, "Calorias"),
                    Option(R.drawable.iconcheck, "Confirmar"),
                    Option(R.drawable.iconcancel, "Cancelar"), //8
                    Option(R.drawable.iconfruit, "Frutas"),  //9
                    Option(R.drawable.iconapple, "maçã"),  //10
                    Option(R.drawable.iconroastedchicken, "Frango"),  //11
                    Option(R.drawable.iconfastfood, "FastFood"),  //12
                    Option(R.drawable.iconsalad, "Salada"),  //13
                    Option(R.drawable.iconemoji, "Feliz"),  //14
                    Option(R.drawable.iconsurprised, "Surpreso"),  //15
                    Option(R.drawable.iconparty, "Festa"),  //16
                    Option(R.drawable.iconheart, "Amoroso"), //17
                    Option(R.drawable.iconsad, "Chateado"),  //18
                    Option(R.drawable.iconbad, "Triste"),  //19
                    Option(R.drawable.iconscared, "Assustado"),  //20
                    Option(R.drawable.iconangry, "Bravo"),  //21
                    Option(R.drawable.iconshocked, "Chocado"),  //21
                )
                onPositive { index: Int, option: Option ->
                    // Handle selected option
                    category = index.toString()
                    println(category)
                }
            }
        }
        /*   myView.findViewById<ConstraintLayout>(R.id.Date).setOnClickListener{
     CalendarSheet().show(requireContext()) { // Build and show
              title("Para quando você deseja agendar?") // Set the title of the sheet
              onPositive ("Escolher") { dateStart, dateEnd ->
                  // Handle date or range
              }
              onNegative ("Cancelar") {
                  // Handle cancel
              }
        }*/
        myView.findViewById<ConstraintLayout>(R.id.PreferedColor).setOnClickListener {

            ColorSheet()
                .build(requireContext()) {

                title("Selecione uma cor")
                    disableSwitchColorView()
                    colorsInt(
                        Color.parseColor("#384fc7"),//Azul
                        Color.parseColor("#1f69d1"),//Azul normal
                        Color.parseColor("#7595ff"),//Azul claro
                        Color.parseColor("#1fd1cb"),//Ciano
                        Color.parseColor( "#fae739"),//Amarelo
                        Color.parseColor("#ed7207"),//Quentes
                        Color.parseColor("#d11f54"),//Vermelho claro
                        Color.parseColor("#db1818"),//Vermelhos
                        Color.parseColor("#750000"),//Vermelhos escuros
                        Color.parseColor("#a147e6"),//Roxos
                        Color.parseColor("#ec84f0"), //Rosa
                        Color.parseColor("#d11fce"),//Esquentando
                        Color.parseColor("#00ff00"), //verde normal
                        Color.parseColor("#88ff80"),//verde claro
                        Color.parseColor("#37913c"),//verde escuro

                        //Pastel:
                        Color.parseColor("#ffbd8a"),//pastel
                        Color.parseColor("#fbffab"),//pastel
                        Color.parseColor("#fbffab"),//pastel
                        Color.parseColor("#abfeff"),//pastel
                        Color.parseColor("#c3abff"),//pastel
                        Color.parseColor("#ffabfb"),//pastel

                        Color.parseColor("#ffffff"),//Branco
                        Color.parseColor("#737373"), //cinza
                        Color.parseColor("#00000000") //preto
                    )
                onPositive { color ->

                    colorPicked = color
                    colorTextPrefered.setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY)
                    myView.findViewById<ImageView>(R.id.categoryIcon).setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY)
                    myView.findViewById<ImageView>(R.id.dateIcon).setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY)
                    colorButton.setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY)
                }
            }
                .show()


                }
/*
                .addListenerButton(
                    "Selecionar", button
                ) { v, position, color ->
                    colorPicked = color
                    colorButton.setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY)
                    dialog?.dismiss()
                }
                .setDefaultColorButton(Color.parseColor("#f84c44"))
                .setColorButtonDrawable(R.color.blue_200)
                .disableDefaultButtons(true)
                .setColumns(5)
                .show()*/


        buttonShare.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "" + titulo + ": " + subtitulo)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
        return myView


    }

    override fun onStart() {
        super.onStart()
        getDialog()!!.getWindow()!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog()!!.getWindow()!!.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.dialogcorners, null))
        //getDialog()!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }
}
