package com.example.workout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.workout.databinding.ActivityCastBinding
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.framework.*
import com.google.android.gms.cast.framework.media.RemoteMediaClient

class CastActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityCastBinding

    private lateinit var mSessionManager: SessionManager
    private var mCastSession: CastSession? = null
    private val mSessionManagerListener: SessionManagerListener<CastSession> =
        SessionManagerListenerImpl()

    private inner class SessionManagerListenerImpl : SessionManagerListener<CastSession> {
        override fun onSessionStarted(session: CastSession?, sessionId: String) {

        }

        override fun onSessionStarting(p0: CastSession?) {
            Log.v("hhh", "Cast Session starting...")
        }

        override fun onSessionEnded(session: CastSession?, error: Int) {
            //Bei Beenden der Session Activity beenden
            finish()
        }

        override fun onSessionEnding(p0: CastSession?) {

        }

        override fun onSessionResumed(session: CastSession?, wasSuspended: Boolean) {

        }

        override fun onSessionResuming(p0: CastSession?, p1: String) {

        }

        override fun onSessionResumeFailed(p0: CastSession?, p1: Int) {

        }

        override fun onSessionStartFailed(p0: CastSession?, p1: Int) {

        }

        override fun onSessionSuspended(p0: CastSession?, p1: Int) {

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        val castContext = CastContext.getSharedInstance(this)
        mSessionManager = castContext.sessionManager



    }

    override fun onResume() {
        super.onResume()
        mCastSession = mSessionManager.currentCastSession
        //mCastSession.sendMessage("fff")
        mSessionManager.addSessionManagerListener(mSessionManagerListener, CastSession::class.java)
    }

    override fun onPause() {
        super.onPause()
        mSessionManager.removeSessionManagerListener(mSessionManagerListener, CastSession::class.java)
        mCastSession = null
    }




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
}