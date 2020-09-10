package com.chatanoga.cab.rider.activities.looking

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.chatanoga.cab.common.interfaces.AlertDialogEvent
import com.chatanoga.cab.common.models.Request
import com.chatanoga.cab.common.networking.socket.CurrentRequestResult
import com.chatanoga.cab.common.networking.socket.GetCurrentRequestInfo
import com.chatanoga.cab.common.networking.socket.interfaces.EmptyClass
import com.chatanoga.cab.common.networking.socket.interfaces.RemoteResponse
import com.chatanoga.cab.common.networking.socket.interfaces.SocketNetworkDispatcher
import com.chatanoga.cab.common.utils.AlertDialogBuilder
import com.chatanoga.cab.common.utils.AlertDialogBuilder.show
import com.chatanoga.cab.common.utils.AlerterHelper
import com.chatanoga.cab.rider.R
import com.chatanoga.cab.rider.databinding.ActivityLookingBinding
import com.chatanoga.cab.rider.networking.socket.CancelRequest
import com.chatanoga.cab.rider.ui.RiderBaseActivity

class LookingActivity : RiderBaseActivity() {
    lateinit var binding: ActivityLookingBinding

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        immersiveScreen = true
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@LookingActivity, R.layout.activity_looking)
        SocketNetworkDispatcher.instance.onDriverAccepted = {
            runOnUiThread {
                binding.loadingIndicator.pauseAnimation()
                val intent = Intent()
                travel = it
                this.setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

    }

    fun requestRefresh() {
        GetCurrentRequestInfo().execute<CurrentRequestResult> {
            when(it) {
                is RemoteResponse.Success -> {
                    travel = it.body.request
                    refreshPage()
                }

                is RemoteResponse.Error -> {
                    this.setResult(Activity.RESULT_CANCELED)
                    finish()
                    it.error.showAlert(this)
                }
            }
        }
    }

    fun onCancelRequest(view: View) {
        CancelRequest().execute<EmptyClass> {
            when(it) {
                is RemoteResponse.Success -> {
                    this.setResult(Activity.RESULT_CANCELED)
                    finish()
                }

                is RemoteResponse.Error -> {
                    AlerterHelper.showError(this, it.error.message)
                }
            }

        }

    }

    override fun onReconnected() {
        super.onReconnected()
        requestRefresh()
    }

    private fun refreshPage() {
        val request = travel
        when (request!!.status) {
            Request.Status.Requested, Request.Status.Found, Request.Status.NotFound, Request.Status.Booked, Request.Status.NoCloseFound -> {

            }
            Request.Status.DriverAccepted, Request.Status.Started, Request.Status.Arrived, Request.Status.WaitingForReview, Request.Status.WaitingForPostPay, Request.Status.WaitingForPrePay, Request.Status.Finished -> {
                binding.loadingIndicator.pauseAnimation()
                val intent = Intent()
                this.setResult(Activity.RESULT_OK, intent)
                finish()
            }
            Request.Status.DriverCanceled, Request.Status.RiderCanceled -> {
                this.setResult(Activity.RESULT_CANCELED)
                finish()
            }
            Request.Status.Expired -> show(this@LookingActivity, getString(R.string.message_expired_trip), AlertDialogBuilder.DialogButton.OK, AlertDialogEvent {
                this@LookingActivity.setResult(Activity.RESULT_CANCELED)
                finish()
            })
        }
    }

    override fun onBackPressed() {
    }
}