package com.chatanoga.cab.driver.activities.main.fragments

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.chatanoga.cab.common.models.Request
import com.chatanoga.cab.common.utils.CommonUtils
import com.chatanoga.cab.common.utils.DistanceFormatter
import com.chatanoga.cab.common.utils.LatLngDeserializer
import com.chatanoga.cab.common.utils.LocationHelper.Companion.distFrom
import com.chatanoga.cab.driver.R
import com.chatanoga.cab.driver.databinding.FragmentRequestBinding
import java.text.NumberFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class RequestFragment : Fragment() {
    private lateinit var request: Request
    lateinit var binding: FragmentRequestBinding
    private lateinit var countDownTimer: CountDownTimer
    private var mListener: OnFragmentInteractionListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val travelString = arguments!!.getString(ARG_REQUEST)
            val type = object : TypeToken<Request?>() {}.type
            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(com.google.type.LatLng::class.java, LatLngDeserializer())
            val customGson = gsonBuilder.create()
            request = customGson.fromJson<Request>(travelString, type)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_request, container, false)
        binding.request = request
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.currency = Currency.getInstance(request.currency)
        binding.textCost.text = format.format(request.costBest)
        if (CommonUtils.currentLocation != null) locationChanged(CommonUtils.currentLocation)
        countDownTimer = object : CountDownTimer(5 * 60000, 50) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                if (mListener != null) {
                    mListener!!.onInvisible(request)
                    mListener!!.onDecline(request)
                }
            }
        }
        countDownTimer.start()
        binding.textDriverUserDistance.text = DistanceFormatter.format(request.distanceBest!!)
        binding.buttonAccept.setOnClickListener {
            countDownTimer.cancel()
            mListener!!.onAccept(request)
        }
        binding.buttonDecline.setOnClickListener {
            countDownTimer.cancel()
            mListener!!.onDecline(request)
            mListener!!.onInvisible(request)
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is OnFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener!!.onInvisible(request)
        mListener = null
    }

    fun locationChanged(latLng: LatLng?) {
        val distanceDriver = distFrom(request.points[0], latLng!!)
        val lp = binding.root.findViewById<View>(R.id.guideline_start).layoutParams as ConstraintLayout.LayoutParams
        lp.guidePercent = distanceDriver.toFloat() / (request.distanceBest!! + distanceDriver)
        binding.root.findViewById<View>(R.id.guideline_start).layoutParams = lp
        binding.textDriverUserDistance.text = DistanceFormatter.format(distanceDriver)
    }

    interface OnFragmentInteractionListener {
        fun onAccept(request: Request)
        fun onDecline(request: Request)
        fun onVisible(request: Request)
        fun onInvisible(request: Request)
    }

    companion object {
        private const val ARG_REQUEST = "request"
        fun newInstance(request: Request?): RequestFragment {
            val fragment = RequestFragment()
            val args = Bundle()
            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(com.google.type.LatLng::class.java, LatLngDeserializer())
            val customGson = gsonBuilder.create()
            args.putString(ARG_REQUEST, customGson.toJson(request))
            fragment.arguments = args
            return fragment
        }
    }
}