package com.chatanoga.cab.driver.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.chatanoga.cab.common.components.BaseActivity
import com.chatanoga.cab.common.models.Request
import com.chatanoga.cab.common.networking.socket.interfaces.SocketNetworkDispatcher
import com.chatanoga.cab.common.utils.TravelRepository
import com.chatanoga.cab.common.utils.TravelRepository.get
import com.chatanoga.cab.common.utils.TravelRepository.set
import com.chatanoga.cab.driver.activities.splash.SplashActivity

@SuppressLint("Registered")
open class DriverBaseActivity : BaseActivity() {
    protected var travel: Request?
        get() = get(this, TravelRepository.AppType.DRIVER)
        protected set(request) {
            set(this, TravelRepository.AppType.DRIVER, request!!)
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