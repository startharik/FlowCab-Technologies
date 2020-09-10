package com.chatanoga.cab.rider.ui

import android.content.Intent
import android.os.Bundle
import com.chatanoga.cab.common.components.BaseActivity
import com.chatanoga.cab.common.models.Request
import com.chatanoga.cab.common.networking.socket.interfaces.SocketNetworkDispatcher
import com.chatanoga.cab.common.utils.MyPreferenceManager
import com.chatanoga.cab.common.utils.MyPreferenceManager.Companion.getInstance
import com.chatanoga.cab.common.utils.TravelRepository
import com.chatanoga.cab.common.utils.TravelRepository.get
import com.chatanoga.cab.common.utils.TravelRepository.set
import com.chatanoga.cab.rider.activities.splash.SplashActivity

abstract class RiderBaseActivity : BaseActivity() {
    var travel: Request?
        get() = get(this, TravelRepository.AppType.RIDER)
        protected set(request) {
            set(this, TravelRepository.AppType.RIDER, request!!)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(SocketNetworkDispatcher.instance.socket == null) {
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
            return
        }
    }
}