package com.caiqueponjjar.finapp;


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.caiqueponjjar.finapp.helper.usuario
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*


class BackgroundService : Service() {
    var context: Context = this
    var handler: Handler? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    override fun onCreate() {
        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show()
        handler = Handler()
        println(    "Service created!")
        runnable = Runnable {
            Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show()
            checkNewMessages()
            handler!!.postDelayed(runnable!!, 1000)
            var userId: String = ""
            if(FirebaseAuth.getInstance().currentUser?.uid  != null) {
                userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
            }
            if( GoogleSignIn.getLastSignedInAccount(this)?.id.equals(null) ){
                userId = GoogleSignIn.getLastSignedInAccount(this)?.id.toString()
            }
            val rootRef = Firebase.database.reference.child("users").child(userId!!).child("message")
            rootRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    println(error!!.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        //Carregando lista de dados

                        var subtitle = postSnapshot.child("subtitle").getValue(String::class.java)
                        var title = postSnapshot.child("title").getValue(String::class.java)
                        var itemColor = postSnapshot.child("color").getValue(Int::class.java)
                        var key = postSnapshot.child("key").getValue(String::class.java)
                        var status = postSnapshot.child("status").getValue(String::class.java)
                        if (status.equals("sent")) {
                            var builder = NotificationCompat.Builder(context, "0")
                                .setSmallIcon(R.drawable.logo4)
                                .setContentTitle(title.toString())
                                .setContentText(subtitle.toString())
                                //.setStyle(NotificationCompat.BigTextStyle()
                                //  .bigText("Much longer text that cannot fit one line..."))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            createNotificationChannel()
                            var notificationManager = NotificationManagerCompat.from(context)

                            val random = Random()
                            var m: Int = random.nextInt(9999 - 1000) + 1000
                            notificationManager.notify(m, builder.build())
                            usuario().ChangeStatus("read", userId, key.toString())
                        }
                    }
                }
            })
        }
        handler!!.postDelayed(runnable!!, 15000)

    }
    fun checkNewMessages(){
    //App in background

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
            val notificationManager: NotificationManager = this.getSystemService(
                NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    override fun onStart(intent: Intent, startid: Int) {
        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show()

    }

    companion object {
        var runnable: Runnable? = null
    }
}