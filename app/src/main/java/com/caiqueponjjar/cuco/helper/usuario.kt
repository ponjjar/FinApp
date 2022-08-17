package com.caiqueponjjar.cuco.helper;

import android.app.Activity
import com.caiqueponjjar.cuco.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.maxkeppeler.sheets.options.Option
import org.json.JSONObject
import java.util.*


class usuario {

    private lateinit var database: DatabaseReference
    fun getUser(): FirebaseUser? {

        val user = FirebaseAuth.getInstance().currentUser
        return(user);

    }fun getAccount(activity: Activity): GoogleSignInAccount? {

        val account = activity?.let { GoogleSignIn.getLastSignedInAccount(it) }
        return(account);

    }
    fun getUsername(activity: Activity) : String? {
        if(getUser()?.displayName != null) {
            return (getUser()?.displayName)
        }
        if(!getAccount(activity)?.displayName.equals(null) ){
            return (getAccount(activity)?.givenName)
        }
        return null
    }
    fun getEmail() : String? {
        return(getUser()?.email)
    }

    fun getUniqueId(activity: Activity) : String? {
        if(getUser()?.uid  != null) {
            return (getUser()?.uid)
        }
        if(!getAccount(activity)?.id.equals(null) ){
            return getAccount(activity)?.id
        }
        return null
    }

    fun commitNewData(activity: Activity, Titulo: String, Subtitulo: String, color: Int, category: String) {
        val userId = getUniqueId(activity)
        val username = getUsername(activity)
        database = Firebase.database.reference
        if (userId != null) {
            database.child("users").child(userId).child("username").setValue(username)
            database.child("users").child(userId).child("userID").setValue(userId)
            val DatabaseList = database.child("users").child(userId).child("message").push()
            //DatabaseList.child("username").setValue(username)
            DatabaseList.child("title").setValue(Titulo)
            DatabaseList.child("subtitle").setValue(Subtitulo)
            DatabaseList.child("color").setValue(color)
            DatabaseList.child("category").setValue(category)
            DatabaseList.child("key").setValue(DatabaseList.key)
            DatabaseList.child("status").setValue("sent")
        }
    }
    fun ChangeStatus( status: String, userId: String, key: String) {
        database = Firebase.database.reference
        if (userId != null) {
            val DatabaseList = database.child("users").child(userId).child("message").child(key)
            //DatabaseList.child("username").setValue(username)
            DatabaseList.child("status").setValue(status)
        }

    }
    fun sendData(activity: Activity, Titulo: String, Subtitulo: String, color: Int, category: String, userID: String) {
        val userId = userID;
        database = Firebase.database.reference
        if (userId != null) {
            val DatabaseList = database.child("users").child(userId).child("message").push()
            //DatabaseList.child("username").setValue(username)
            DatabaseList.child("title").setValue(Titulo)
            DatabaseList.child("subtitle").setValue(Subtitulo)
            DatabaseList.child("color").setValue(color)
            DatabaseList.child("category").setValue(category)
            DatabaseList.child("key").setValue(DatabaseList.key)
            DatabaseList.child("status").setValue("sent")

            database.child("users").child(userId).child("friends").push().child("key").setValue(DatabaseList.key.toString())
            database.child("users").child(userId).child("friends").push().child("key").setValue(getUsername(activity))
        }
    }

    fun deleteData(activity: Activity, Key: String){
        val userId = getUniqueId(activity)
        val ref = FirebaseDatabase.getInstance().reference
        val query = ref.child("users").child(userId!!).child("message").child(Key)
        query.removeValue()
    }
    fun getIcons(value:Int): String? {
        val emojis = JSONObject() // here response is your json string
                emojis.put("iconfavourite", "favorite")
                emojis.put("iconstar", "Estrela")
                emojis.put("iconvideogame", "VideoGame")
                emojis.put("iconlightbulb", "Lampada")
                emojis.put("iconwallclock", "Relógio")
                emojis.put("iconcalories", "Calorias")
                emojis.put("iconcheck", "Confirmar")
                emojis.put("iconcancel", "Cancelar") //8
                emojis.put("iconfruit", "Frutas")  //9
                emojis.put("iconapple", "maçã")  //10
                emojis.put("iconroastedchicken", "Frango")  //11
                emojis.put("iconfastfood", "FastFood")  //12
                emojis.put("iconsalad", "Salada")  //13
                emojis.put("iconemoji", "Feliz")  //14
                emojis.put("iconsurprised", "Surpreso")  //15
                emojis.put("iconparty", "Festa")  //16
                emojis.put("iconheart", "Amoroso") //17
                emojis.put("iconsad", "Chateado")  //18
                emojis.put("iconbad", "Triste")  //19
                emojis.put("iconscared", "Assustado")  //20
                emojis.put("iconangry", "Bravo")  //21
                emojis.put("iconshocked", "Chocado")  //21
        if(value == -1) {
            return "transparent"
        }
            return emojis.names().get(value).toString()

    }

}
