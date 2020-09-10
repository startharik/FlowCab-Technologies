package com.chatanoga.cab.driver.activities.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.db.williamchart.data.Scale
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.chatanoga.cab.common.components.BaseActivity
import com.chatanoga.cab.common.networking.socket.interfaces.EmptyClass
import com.chatanoga.cab.common.networking.socket.interfaces.RemoteResponse
import com.chatanoga.cab.common.utils.AlerterHelper.showInfo
import com.chatanoga.cab.common.utils.DistanceFormatter
import com.chatanoga.cab.driver.R
import com.chatanoga.cab.driver.databinding.ActivityStatisticsBinding
import com.chatanoga.cab.driver.networking.socket.GetStats
import com.chatanoga.cab.driver.networking.socket.QueryType
import com.chatanoga.cab.driver.networking.socket.RequestPayment
import com.chatanoga.cab.driver.networking.socket.StatisticsResult
import com.tylersuehr.esr.ContentItemLoadingStateFactory
import com.tylersuehr.esr.EmptyStateRecyclerView
import com.tylersuehr.esr.ImageTextStateDisplay
import java.text.NumberFormat
import java.util.*

class StatisticsActivity : BaseActivity() {
    lateinit var binding: ActivityStatisticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_statistics)
        binding.driver = preferences.driver
        initializeToolbar(getString(R.string.drawer_earnings))
        binding.emptyState.setStateDisplay(EmptyStateRecyclerView.STATE_LOADING, ContentItemLoadingStateFactory.newCardLoadingState(this))
        binding.emptyState.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY, ImageTextStateDisplay(this, com.chatanoga.cab.common.R.drawable.empty_state, getString(R.string.empty_state_driver_earning_title), getString(R.string.empty_state_driver_earning_message)))
        binding.tabDate.addOnTabSelectedListener(tabSelectedListener)
        refreshStats(QueryType.Daily)
    }

    private var tabSelectedListener: OnTabSelectedListener = object : OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            val qType = when(tab.position) {
                0 -> QueryType.Daily
                1 -> QueryType.Weekly
                2 -> QueryType.Monthly
                else -> QueryType.Daily
            }
            refreshStats(qType)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {}
        override fun onTabReselected(tab: TabLayout.Tab) {}
    }

    fun refreshStats(queryType: QueryType) {
        binding.tabDate.isEnabled = false
        binding.emptyState.invokeState(EmptyStateRecyclerView.STATE_LOADING)
        GetStats(queryType).execute<StatisticsResult> {
            binding.tabDate.isEnabled = true
            when(it) {
                is RemoteResponse.Success -> {
                    if(it.body.dataset.count() < 1) {
                        binding.emptyState.invokeState(EmptyStateRecyclerView.STATE_EMPTY)
                        return@execute
                    }
                    binding.emptyState.visibility = View.GONE
                    binding.tabDate.visibility = View.VISIBLE
                    binding.chartCard.visibility = View.VISIBLE
                    binding.incomeCard.visibility = View.VISIBLE
                    binding.ratingCard.visibility = View.VISIBLE
                    binding.serviceCard.visibility = View.VISIBLE
                    binding.buttonPaymentRequest.visibility = View.VISIBLE
                    val data = it.body.dataset.map { it2 -> it2.name to it2.earning }.toMap()
                    val map = LinkedHashMap(data)
                    binding.chart.gradientFillColors = intArrayOf(Color.parseColor("#81FFFFFF"), Color.TRANSPARENT)
                    binding.chart.animation.duration = 300L
                    val mx = it.body.dataset.maxBy { it2 -> it2.earning }!!.earning * 1.1
                    val mn = it.body.dataset.minBy { it2 -> it2.earning }!!.earning * 0.9
                    binding.chart.scale = Scale(mn.toFloat(), mx.toFloat())
                    binding.chart.postInvalidate()
                    binding.chart.animate(map)
                    val current = it.body.dataset.firstOrNull { it2 -> it2.current == it2.name }
                    binding.chart.labelsFormatter = { fl ->
                        val formatter: NumberFormat = NumberFormat.getCurrencyInstance()
                        formatter.currency = Currency.getInstance(it.body.currency)
                        formatter.format(fl)
                    }
                    if(current != null) {
                        val formatter: NumberFormat = NumberFormat.getCurrencyInstance()
                        formatter.currency = Currency.getInstance(it.body.currency)
                        binding.incomeText.text = formatter.format(current.earning)
                        binding.serviceText.text = current.count
                        binding.distanceText.text = DistanceFormatter.format(current.distance.toInt())
                    } else {
                        binding.incomeText.text = "-"
                        binding.serviceText.text = "-"
                        binding.distanceText.text = "-"
                    }

                }

                is RemoteResponse.Error -> {
                    it.error.showAlert(this@StatisticsActivity)

                }
            }
        }
    }

    fun onPaymentRequestClicked(view: View?) {
        binding.buttonPaymentRequest.isEnabled = false
        RequestPayment().execute<EmptyClass> {
            when(it) {
                is RemoteResponse.Success -> {
                    showInfo(this@StatisticsActivity, getString(R.string.message_payment_request_sent))

                }

                is RemoteResponse.Error -> {
                    it.error.showAlert(this@StatisticsActivity)
                }
            }

        }
    }
}