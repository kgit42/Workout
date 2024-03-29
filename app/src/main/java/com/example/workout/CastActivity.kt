package com.example.workout

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.workout.databinding.ActivityCastBinding
import com.example.workout.db.AppDatabase
import com.example.workout.db.RoutineWorkoutStatsElement
import com.google.android.gms.cast.Cast
import com.google.android.gms.cast.CastDevice
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.framework.*
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import org.json.JSONObject
import com.google.android.gms.cast.framework.CastContext
import kotlinx.coroutines.runBlocking


class CastActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityCastBinding

    private lateinit var mSessionManager: SessionManager
    private var mCastSession: CastSession? = null
    private val mSessionManagerListener: SessionManagerListener<CastSession> =
        SessionManagerListenerImpl()

    private val mCustomChannel: CustomChannel = CustomChannel()
    private val mCustomChannel2: CustomChannel2 = CustomChannel2()
    private val mCustomChannelDebug: CustomChannelDebug = CustomChannelDebug()

    private val mMyCastStateListener: CastStateListener = MyCastStateListener()


    private inner class SessionManagerListenerImpl : SessionManagerListener<CastSession> {
        override fun onSessionStarted(session: CastSession?, sessionId: String) {
            Log.v("hhh", "Cast Session started with sessionId $sessionId")

            //CustomChannels bei der CastSession registrieren

            mCastSession = session

            try {
                mCastSession?.setMessageReceivedCallbacks(
                    mCustomChannel.namespace,
                    mCustomChannel
                )
            } catch (e: IOException) {
                Log.e("hhh", "Exception while creating channel 1", e)
            } catch (e: Exception) {
                Log.e("hhh", "Exception", e)
            }

            try {
                mCastSession?.setMessageReceivedCallbacks(
                    mCustomChannel2.namespace,
                    mCustomChannel2
                )
            } catch (e: IOException) {
                Log.e("hhh", "Exception while creating channel 2", e)
            } catch (e: Exception) {
                Log.e("hhh", "Exception", e)
            }

            try {
                mCastSession?.setMessageReceivedCallbacks(
                    mCustomChannelDebug.namespace,
                    mCustomChannelDebug
                )
            } catch (e: IOException) {
                Log.e("hhh", "Exception while creating channel debug", e)
            } catch (e: Exception) {
                Log.e("hhh", "Exception", e)
            }

            //Nachricht über Custom Channel senden
            //Muss im JSON-Format sein; Nicht-JSON-Strings funktionieren nicht
            sendMessage(intent.getStringExtra("routineJson").toString(), 1)
        }

        override fun onSessionStarting(p0: CastSession?) {
            Log.v("hhh", "Cast Session starting...")

            //Ladesymbol einblenden
            showLoading()
        }

        override fun onSessionEnded(session: CastSession?, error: Int) {
            Log.v("hhh", "Cast Session ended with error ${error.toString()}")

            //Fernbedienung ausblenden
            showMessageAndHideButtons()

            //Bei Beenden der Session Activity beenden
            //finish()
        }

        override fun onSessionEnding(p0: CastSession?) {
            Log.v("hhh", "Cast Session ending...")

        }

        override fun onSessionResumed(session: CastSession?, wasSuspended: Boolean) {
            Log.v("hhh", "Cast Session resumed. wasSuspended = $wasSuspended")

            //CustomChannels bei der CastSession registrieren
            //Keine weitere Nachricht mit Workout senden, da Session fortgeführt wurde.

            mCastSession = session

            try {
                mCastSession?.setMessageReceivedCallbacks(
                    mCustomChannel.namespace,
                    mCustomChannel
                )
            } catch (e: IOException) {
                Log.e("hhh", "Exception while creating channel 1", e)
            } catch (e: Exception) {
                Log.e("hhh", "Exception", e)
            }

            try {
                mCastSession?.setMessageReceivedCallbacks(
                    mCustomChannel2.namespace,
                    mCustomChannel2
                )
            } catch (e: IOException) {
                Log.e("hhh", "Exception while creating channel 2", e)
            } catch (e: Exception) {
                Log.e("hhh", "Exception", e)
            }

            try {
                mCastSession?.setMessageReceivedCallbacks(
                    mCustomChannelDebug.namespace,
                    mCustomChannelDebug
                )
            } catch (e: IOException) {
                Log.e("hhh", "Exception while creating channel debug", e)
            } catch (e: Exception) {
                Log.e("hhh", "Exception", e)
            }

            //Fernbedienung einblenden
            hideMessageAndShowButtons()

            //Ladesymbol ausblenden
            hideLoading()

        }

        override fun onSessionResuming(p0: CastSession?, p1: String) {
            Log.v("hhh", "Cast Session resuming...")

            //Ladesymbol einblenden
            showLoading()

        }

        override fun onSessionResumeFailed(p0: CastSession?, p1: Int) {
            Log.v("hhh", "Cast Session resume failed.")
            Snackbar.make(binding.root, "Cast Session resume failed.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            //Ladesymbol ausblenden
            hideLoading()

        }

        override fun onSessionStartFailed(p0: CastSession?, p1: Int) {
            Log.v("hhh", "Cast Session start failed.")
            Snackbar.make(binding.root, "Cast Session start failed.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            //Ladesymbol ausblenden
            hideLoading()

        }

        override fun onSessionSuspended(p0: CastSession?, p1: Int) {
            Log.v("hhh", "Cast Session suspended")

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Bildschrim anlassen
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setSupportActionBar(binding.toolbar)


        //CastContext
        val castContext = CastContext.getSharedInstance(this)
        mSessionManager = castContext.sessionManager

        //CastStateListener: https://developers.google.com/android/reference/com/google/android/gms/cast/framework/CastState
        //https://developers.google.com/android/reference/com/google/android/gms/cast/framework/CastStateListener
        //https://developers.google.com/android/reference/com/google/android/gms/cast/framework/CastState
        castContext.addCastStateListener(mMyCastStateListener)


        //OnClickListener der Buttons:

        val pauseButton: Button = findViewById(R.id.buttonPause)
        pauseButton.setOnClickListener { v ->
            /*
                if (pauseButton?.text == "Pause") {
                    val outputJson: String = Gson().toJson("PAUSE")
                    sendMessage(outputJson, 2)
                } else {
                    val outputJson: String = Gson().toJson("CONTINUE")
                    sendMessage(outputJson, 2)
                }
                 */
            val outputJson: String = Gson().toJson("PAUSE")
            sendMessage(outputJson, 2)

            vibrate()

        }

        val skipButton: Button = findViewById(R.id.buttonSkip)
        skipButton.setOnClickListener { v ->
            val outputJson: String = Gson().toJson("SKIP")
            sendMessage(outputJson, 2)

            vibrate()

        }

        val stopButton: Button = findViewById(R.id.buttonStop)

        //Ein einfacher Klick auf Stopp zeigt Snackbar
        stopButton.setOnClickListener { v ->
            Snackbar.make(v, "Zum Stoppen gedrückt halten", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

        }
        stopButton.setOnLongClickListener { v ->
            val outputJson: String = Gson().toJson("STOP")
            sendMessage(outputJson, 2)

            vibrate()

            return@setOnLongClickListener true
        }


        val backButton: Button = findViewById(R.id.buttonBack)
        backButton.setOnClickListener { v ->
            val outputJson: String = Gson().toJson("BACK")
            sendMessage(outputJson, 2)

            vibrate()
        }


    }

    override fun onResume() {
        super.onResume()

        /*
        val intent = intent
        mSessionManager.startSession(intent)
         */

        mCastSession = mSessionManager.currentCastSession


        //Fernbedienung anzeigen, wenn Activity fortgeführt wird
        if (mCastSession?.isConnected == true) {
            hideMessageAndShowButtons()
        }

        mSessionManager.addSessionManagerListener(mSessionManagerListener, CastSession::class.java)

        //CastListener: https://developers.google.com/android/reference/com/google/android/gms/cast/Cast.Listener
        //mCastSession.addCastListener()
    }


    override fun onPause() {
        super.onPause()


        mSessionManager.removeSessionManagerListener(
            mSessionManagerListener,
            CastSession::class.java
        )


        //mCastSession = null
    }

    override fun onDestroy() {
        super.onDestroy()

        //Die folgende Zeile beendet die Session. 'false' als Parameter sollte dabei die Receiver Anwendung nicht beenden
        //Jedoch beendet die Receiver App, wenn keine Session mehr aktiv ist.
        //mSessionManager.endCurrentSession(false)
    }

    override fun onBackPressed() {
        //nichts tun, um zu verhindern, dass Back-Button betätigt wird und so Activity destroyed wird, während des Trainings
        if (mCastSession?.isConnected == true) {

        } else {
            super.onBackPressed();
        }
    }


    //Cast-Button anzeigen
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_cast, menu)
        CastButtonFactory.setUpMediaRouteButton(
            applicationContext,
            menu,
            R.id.media_route_menu_item
        )
        return true
    }


    private fun sendMessage(message: String, channelNumber: Int) {
        if (channelNumber == 1) {
            if (mCustomChannel != null) {
                try {
                    mCastSession?.sendMessage(mCustomChannel.namespace, message)
                        ?.setResultCallback { status ->
                            if (!status.isSuccess) {
                                Log.e("hhh", "Sending message failed")
                                Snackbar.make(
                                    binding.root,
                                    "Senden fehlgeschlagen",
                                    Snackbar.LENGTH_LONG
                                )
                                    .setAction("Action", null).show()
                            } else {
                                Log.v("hhh", "Sending successful")
                            }
                        }
                } catch (e: Exception) {
                    Log.e("hhh", "Exception while sending message on channel 1", e)
                }
            }
        } else if (channelNumber == 2) {
            if (mCustomChannel2 != null) {
                try {
                    mCastSession?.sendMessage(mCustomChannel2.namespace, message)
                        ?.setResultCallback { status ->
                            if (!status.isSuccess) {
                                Log.e("hhh", "Sending message failed")
                                Snackbar.make(
                                    binding.root,
                                    "Senden fehlgeschlagen",
                                    Snackbar.LENGTH_LONG
                                )
                                    .setAction("Action", null).show()
                            } else {
                                Log.v("hhh", "Sending successful")
                            }
                        }
                } catch (e: Exception) {
                    Log.e("hhh", "Exception while sending message on channel 2", e)
                }
            }
        }

    }

    private fun hideMessageAndShowButtons() {
        val skipButton: Button = findViewById(R.id.buttonSkip)
        val pauseButton: Button = findViewById(R.id.buttonPause)
        val backButton: Button = findViewById(R.id.buttonBack)
        val stopButton: Button = findViewById(R.id.buttonStop)
        val message: TextView = findViewById(R.id.message)
        skipButton.visibility = View.VISIBLE
        pauseButton.visibility = View.VISIBLE
        backButton.visibility = View.VISIBLE
        stopButton.visibility = View.VISIBLE

        message.visibility = View.INVISIBLE
    }

    private fun showMessageAndHideButtons() {
        val skipButton: Button = findViewById(R.id.buttonSkip)
        val pauseButton: Button = findViewById(R.id.buttonPause)
        val backButton: Button = findViewById(R.id.buttonBack)
        val stopButton: Button = findViewById(R.id.buttonStop)
        val message: TextView = findViewById(R.id.message)
        skipButton.visibility = View.INVISIBLE
        pauseButton.visibility = View.INVISIBLE
        backButton.visibility = View.INVISIBLE
        stopButton.visibility = View.INVISIBLE

        message.visibility = View.VISIBLE
    }

    private fun showLoading() {
        val progressbar: ProgressBar = findViewById(R.id.progressBar2)
        progressbar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        val progressbar: ProgressBar = findViewById(R.id.progressBar2)
        progressbar.visibility = View.INVISIBLE
    }

    //Vibration
    private fun vibrate() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(150)
        }
    }

    /*
    private fun changeTextPauseButton(){
            if(pauseButton?.text == "Weiter"){
                pauseButton?.text = "Pause"
            }else{
                pauseButton?.text = "Weiter"
            }

    }
     */


    /*
    private fun loadRemoteMedia(position: Int, autoPlay: Boolean) {
        if (mCastSession == null) {
            return
        }
        val remoteMediaClient = mCastSession!!.remoteMediaClient ?: return
        remoteMediaClient.registerCallback(object : RemoteMediaClient.Callback() {
            override fun onStatusUpdated() {
                val intent = Intent(this@LocalPlayerActivity, ExpandedControlsActivity::class.java)
                startActivity(intent)
                remoteMediaClient.unregisterCallback(this)
            }
        })
        remoteMediaClient.load(
            MediaLoadRequestData.Builder()
                .setMediaInfo(mSelectedMedia)
                .setAutoplay(autoPlay)
                .setCurrentTime(position.toLong()).build()
        )
    }*/


    //Erster Channel
    inner class CustomChannel : Cast.MessageReceivedCallback {
        val namespace: String
            //Namespace
            get() = "urn:x-cast:com.example.custom"

        override fun onMessageReceived(castDevice: CastDevice, namespace: String, message: String) {
            Log.v("hhh", "onMessageReceived (Channel 1): $message")

            hideMessageAndShowButtons()
            //Ladesymbol ausblenden
            hideLoading()

        }
    }

    //Zweiter Channel. Empfängt die Nachricht über erfolgreichen Abschluss der Routine.
    inner class CustomChannel2 : Cast.MessageReceivedCallback {
        val namespace: String
            //Namespace
            get() = "urn:x-cast:com.example.custom2"

        override fun onMessageReceived(castDevice: CastDevice, namespace: String, message: String) {
            Log.v("hhh", "onMessageReceived (Channel 2): $message")


            //Annahme einer Nachricht in JSON:
            val obj = JSONObject(message)

            try {
                val length = obj.getInt("length")
                val numberSetsDone = obj.getInt("numberSetsDone")
                val name = obj.getString("name")
                val timestamp = obj.getLong("timestamp")

                val statsObject =
                    RoutineWorkoutStatsElement(0, length, numberSetsDone, name, timestamp)


                //RoutineWorkoutStatsElement in DB einfügen
                runBlocking {
                    launch {
                        withContext(Dispatchers.IO) {
                            AppDatabase.getInstance(applicationContext).routineWorkoutStatsElementDao()
                                .insert(statsObject)
                        }
                    }
                }

                //Fernbedienung ausblenden
                showMessageAndHideButtons()

                //Senden, dass Daten erhalten. Receiver kann dann App beenden.
                val outputJson: String = Gson().toJson("OK")
                sendMessage(outputJson, 2)

                //Listener entfernen (nicht nötig)
                //mCastSession?.removeMessageReceivedCallbacks(mCustomChannel2.namespace)


                //Activity beenden
                finish()

            } catch (e: Exception) {

            }


        }
    }

    //Debug-Channel
    inner class CustomChannelDebug : Cast.MessageReceivedCallback {
        val namespace: String
            //Namespace
            get() = "urn:x-cast:com.google.cast.debuglogger"

        override fun onMessageReceived(castDevice: CastDevice, namespace: String, message: String) {
            Log.v("hhh", "onMessageReceived (Channel debug): $message")

        }
    }
}


class MyCastStateListener : CastStateListener {
    override fun onCastStateChanged(p0: Int) {
        Log.v("hhh", "State changed: $p0")
    }
}








