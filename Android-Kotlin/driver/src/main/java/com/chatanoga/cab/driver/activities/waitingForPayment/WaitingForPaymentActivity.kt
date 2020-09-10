package com.chatanoga.cab.driver.activities.waitingForPayment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.chatanoga.cab.common.models.Request
import com.chatanoga.cab.common.models.Service
import com.chatanoga.cab.common.networking.socket.CurrentRequestResult
import com.chatanoga.cab.common.networking.socket.GetCurrentRequestInfo
import com.chatanoga.cab.common.networking.socket.interfaces.EmptyClass
import com.chatanoga.cab.common.networking.socket.interfaces.RemoteResponse
import com.chatanoga.cab.common.networking.socket.interfaces.SocketNetworkDispatcher
import com.chatanoga.cab.common.utils.AlertDialogBuilder
import com.chatanoga.cab.common.utils.AlertDialogBuilder.show
import com.chatanoga.cab.common.utils.AlerterHelper
import com.chatanoga.cab.driver.R
import com.chatanoga.cab.driver.databinding.ActivityWaitingForPaymentBinding
import com.chatanoga.cab.driver.networking.socket.PaidInCash
import com.chatanoga.cab.driver.ui.DriverBaseActivity

class WaitingForPaymentActivity : DriverBaseActivity() {
    lateinit var binding: ActivityWaitingForPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        immersiveScreen = true
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@WaitingForPaymentActivity, R.layout.activity_waiting_for_payment)
        SocketNetworkDispatcher.instance.onPaid = {
            this.setResult(Activity.RESULT_OK)
            finish()
        }
    }

    fun onPaidCash(view: View) {
        PaidInCash().execute<EmptyClass> {
            when(it) {
                is RemoteResponse.Success -> {
                    this.setResult(Activity.RESULT_OK)
                    finish()
                }

                is RemoteResponse.Error -> {
                    AlerterHelper.showError(this, it.error.message)
                }
            }
        }
    }

    fun requestRefresh() {
        GetCurrentRequestInfo().execute<CurrentRequestResult> {
            when(it) {
                is RemoteResponse.Success -> {
                    travel = it.body.request
                    if(it.body.request.service!!.paymentMethod == Service.PaymentMethod.CashCredit || it.body.request.service!!.paymentMethod == Service.PaymentMethod.OnlyCash) {
                        binding.buttonCancel.visibility = View.VISIBLE
                    }
                    refreshPage()
                }

                is RemoteResponse.Error -> {
                    this.setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requestRefresh()
    }

    override fun onReconnected() {
        super.onReconnected()
        requestRefresh()
    }

    private fun refreshPage() {
        val request = travel
        when (request!!.status) {
            Request.Status.Finished, Request.Status.WaitingForReview -> {
                this.setResult(Activity.RESULT_OK)
                finish()
            }
            Request.Status.WaitingForPostPay -> {

            }
            else -> {
                show(this, "Unhandled service status: ${request.status}", AlertDialogBuilder.DialogButton.OK, null)
            }
        }
    }
}