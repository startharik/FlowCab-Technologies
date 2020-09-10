package com.chatanoga.cab.driver.activities.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.chatanoga.cab.common.activities.chargeAccount.ChargeAccountActivity
import com.chatanoga.cab.common.activities.transactions.TransactionsActivity
import com.chatanoga.cab.common.activities.travels.TravelsActivity
import com.chatanoga.cab.common.models.Request
import com.chatanoga.cab.common.networking.socket.CurrentRequestResult
import com.chatanoga.cab.common.networking.socket.GetCurrentRequestInfo
import com.chatanoga.cab.common.networking.socket.interfaces.EmptyClass
import com.chatanoga.cab.common.networking.socket.interfaces.RemoteResponse
import com.chatanoga.cab.common.networking.socket.interfaces.SocketNetworkDispatcher
import com.chatanoga.cab.common.utils.AlertDialogBuilder
import com.chatanoga.cab.common.utils.AlerterHelper.showInfo
import com.chatanoga.cab.common.utils.CommonUtils
import com.chatanoga.cab.common.utils.DataBinder.setMedia
import com.chatanoga.cab.driver.R
import com.chatanoga.cab.driver.activities.about.AboutActivity
import com.chatanoga.cab.driver.activities.main.adapters.RequestsFragmentPagerAdapter
import com.chatanoga.cab.driver.activities.main.fragments.RequestFragment.OnFragmentInteractionListener
import com.chatanoga.cab.driver.activities.statistics.StatisticsActivity
import com.chatanoga.cab.driver.activities.travel.TravelActivity
import com.chatanoga.cab.driver.databinding.ActivityMainBinding
import com.chatanoga.cab.driver.networking.http.LocationUpdate
import com.chatanoga.cab.driver.networking.socket.AcceptOrder
import com.chatanoga.cab.driver.networking.socket.GetAvailableRequests
import com.chatanoga.cab.driver.networking.socket.UpdateStatus
import com.chatanoga.cab.driver.ui.DriverBaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

enum class LocationState {
    OK,
    LocationDisabled,
    PermissionNotAsked,
    PermissionDenied
}

class MainActivity : DriverBaseActivity(), OnMapReadyCallback, LocationListener, OnFragmentInteractionListener {
    private var mMap: GoogleMap? = null
    private var markerPickup: Marker? = null
    private var markerDropOff: Marker? = null
    private lateinit var binding: ActivityMainBinding
    private var requestCardsAdapter: RequestsFragmentPagerAdapter? = null
    private var mapFragment: SupportMapFragment? = null
    private val requestLocationCode = 432

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        requestCardsAdapter = RequestsFragmentPagerAdapter(supportFragmentManager, ArrayList())
        binding.requestsViewPager.adapter = requestCardsAdapter
        binding.requestsViewPager.offscreenPageLimit = 3
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding.buttonEnableLocation.setOnClickListener { startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
        binding.buttonEnablePermission.setOnClickListener {
            var permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions = permissions.plus(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            ActivityCompat.requestPermissions(this@MainActivity, permissions, requestLocationCode)
        }
        binding.buttonOpenLocationSettings.setOnClickListener { startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))) }
        setSupportActionBar(binding.appbar)
        val actionBar = supportActionBar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val value = TypedValue()
            theme.resolveAttribute(R.attr.colorPrimary, value, true)
            window.navigationBarColor = value.data
        }
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.menu)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        binding.navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            binding.drawerLayout.closeDrawers()
            when (menuItem.itemId) {
                R.id.nav_item_travels -> startActivity(Intent(this@MainActivity, TravelsActivity::class.java))
                R.id.nav_item_statistics -> startActivity(Intent(this@MainActivity, StatisticsActivity::class.java))
                R.id.nav_item_charge_account -> startActivityForResult(Intent(this@MainActivity, ChargeAccountActivity::class.java), ACTIVITY_WALLET)
                R.id.nav_item_transactions -> startActivity(Intent(this@MainActivity, TransactionsActivity::class.java))
                R.id.nav_item_about -> startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                R.id.nav_item_exit -> logout()
                else -> Toast.makeText(this@MainActivity, menuItem.title, Toast.LENGTH_SHORT).show()
            }
            true
        }
        fillInfo()
        checkPermissions()
        SocketNetworkDispatcher.instance.onCancelRequest = {
            val position = requestCardsAdapter!!.getPositionWithTravelId(it.toLong())
            if (position >= 0) requestCardsAdapter!!.remove(position)
        }
        SocketNetworkDispatcher.instance.onNewRequest = {
            requestCardsAdapter!!.add(it)
            requestCardsAdapter!!.notifyDataSetChanged()
        }
        GetCurrentRequestInfo().execute<CurrentRequestResult> {
            when (it) {
                is RemoteResponse.Success -> {
                    if (it.body.request.status != Request.Status.WaitingForReview) {
                        val intent = Intent(this@MainActivity, TravelActivity::class.java)
                        travel = it.body.request
                        startActivityForResult(intent, ACTIVITY_TRAVEL)
                    }
                }

                is RemoteResponse.Error -> {
                }
            }
        }
        binding.switchConnection.setOnClickListener { switchClicked() }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.uiSettings.isMapToolbarEnabled = false
        mMap!!.isMyLocationEnabled = true
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        val locationManager = (this.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        getLastKnownLocation()
        if (resources.getBoolean(R.bool.isNightMode)) {
            val success = mMap!!.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_night))
            if (!success) Log.e("MapsActivityRaw", "Style parsing failed.")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkPermissions()
    }

    private fun requestRefresh() {
        GetAvailableRequests().executeArray<Request> {
            when (it) {
                is RemoteResponse.Success -> {
                    binding.switchConnection.isChecked = true
                    requestCardsAdapter = RequestsFragmentPagerAdapter(supportFragmentManager, it.body)
                    binding.requestsViewPager.adapter = requestCardsAdapter
                    binding.requestsViewPager.offscreenPageLimit = 3
                }

                is RemoteResponse.Error -> {
                    binding.switchConnection.isChecked = false
                }
            }
        }
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && CommonUtils.isGPSEnabled(this@MainActivity)) {
            binding.locationState = LocationState.OK
            runOnUiThread {
                mapFragment!!.getMapAsync(this)
            }
            requestRefresh()
            return
        }
        if (!CommonUtils.isGPSEnabled(this@MainActivity)) {
            binding.locationState = LocationState.LocationDisabled
            return
        }
        binding.locationState = LocationState.PermissionNotAsked

        /*if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
        } else {
            binding.locationState = LocationState.PermissionDenied
        }*/
    }

    override fun onReconnected() {
        super.onReconnected()
        checkPermissions()
    }

    private fun switchClicked() {
        if (binding.switchConnection.isChecked && CommonUtils.currentLocation == null && mMap!!.myLocation == null) {
            AlertDialogBuilder.show(this@MainActivity, "Your exact current location is yet to be determined. Please try again after a few seconds.", AlertDialogBuilder.DialogButton.OK, null)
            binding.switchConnection.isChecked = false
            return
        }
        binding.switchConnection.isEnabled = false
        GlobalScope.launch(Dispatchers.Main) {
            UpdateStatus(binding.switchConnection.isChecked).execute<EmptyClass> {
                binding.switchConnection.isEnabled = true
                when (it) {
                    is RemoteResponse.Success -> {
                        if (binding.switchConnection.isChecked)
                            LocationUpdate(preferences.token!!, if(CommonUtils.currentLocation != null) CommonUtils.currentLocation!! else LatLng(mMap!!.myLocation.latitude, mMap!!.myLocation.longitude), false).execute<EmptyClass> {

                            }
                    }

                    is RemoteResponse.Error -> {
                        binding.switchConnection.isChecked = !binding.switchConnection.isChecked
                    }
                }
            }
        }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val manager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers: List<String>
        providers = manager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val l = manager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                bestLocation = l
            }
        }
        val latLng: LatLng
        latLng = if (bestLocation == null) LatLng(getString(R.string.default_location).split(",").toTypedArray()[0].toFloat().toDouble(), getString(R.string.default_location).split(",").toTypedArray()[1].toFloat().toDouble()) else LatLng(bestLocation.latitude, bestLocation.longitude)
        if (binding.switchConnection.isChecked) {
            LocationUpdate(preferences.token!!, latLng, false).execute<EmptyClass> {

            }
        }
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
    }

    private fun fillInfo() {
        try {
            val name: String
            val driver = preferences.driver!!
            name = if ((driver.firstName == null || driver.firstName!!.isEmpty()) && (driver.lastName == null || driver.lastName!!.isEmpty())) driver.mobileNumber.toString() else driver.firstName + " " + driver.lastName
            val header = binding.navigationView.getHeaderView(0)
            (header.findViewById<View>(R.id.navigation_header_name) as TextView).text = name
            (header.findViewById<View>(R.id.navigation_header_charge) as TextView).text = driver.mobileNumber.toString()
            val imageView = header.findViewById<ImageView>(R.id.navigation_header_image)
            setMedia(imageView, driver.media)
        } catch (ignored: Exception) {
        }
    }

    private fun logout() {
        preferences.clearPreferences()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ACTIVITY_PROFILE -> {
                if (resultCode == Activity.RESULT_OK) showInfo(this@MainActivity, getString(R.string.info_edit_profile_success))
                fillInfo()
            }
            ACTIVITY_WALLET -> {
                if (resultCode == Activity.RESULT_OK) showInfo(this@MainActivity, getString(R.string.account_charge_success))
                fillInfo()
            }
            ACTIVITY_TRAVEL -> {
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        CommonUtils.currentLocation = LatLng(location.latitude, location.longitude)
        if (binding.switchConnection.isChecked) {
            LocationUpdate(preferences.token!!, CommonUtils.currentLocation!!, false).execute<EmptyClass> {
                when (it) {
                    is RemoteResponse.Success -> {
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(CommonUtils.currentLocation, if (mMap!!.cameraPosition.zoom > 5) mMap!!.cameraPosition.zoom else 16f)
                        mMap!!.animateCamera(cameraUpdate)
                    }
                }
            }
        }
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
    override fun onProviderEnabled(s: String) {}
    override fun onProviderDisabled(s: String) {}
    override fun onAccept(request: Request) {
        AcceptOrder(request.id!!.toLong()).execute<Request> {
            when (it) {
                is RemoteResponse.Success -> {
                    val intentTravel = Intent(this@MainActivity, TravelActivity::class.java)
                    travel = it.body
                    startActivityForResult(intentTravel, ACTIVITY_TRAVEL)
                }

                is RemoteResponse.Error -> {
                    it.error.showAlert(this@MainActivity)
                }
            }
        }
        removeMarkers()
        while (requestCardsAdapter!!.count > 0) requestCardsAdapter!!.remove(0)
    }


    override fun onDecline(request: Request) {
        val position = requestCardsAdapter!!.getPositionWithTravelId(request.id!!)
        if (position >= 0) requestCardsAdapter!!.remove(position)
        requestCardsAdapter!!.notifyDataSetChanged()
    }

    override fun onVisible(request: Request) {
        //TODO: Show markers
        /*if (markerPickup == null) {
            markerPickup = mMap!!.addMarker(MarkerOptions()
                    .position(request.getPickupPoint())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pickup)))
        } else {
            markerPickup!!.setPosition(request.getPickupPoint())
        }
        if (markerDropOff == null) {
            markerDropOff = mMap!!.addMarker(MarkerOptions()
                    .position(request.getDestinationPoint())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_destination)))
        } else {
            markerDropOff!!.setPosition(request.getDestinationPoint())
        }
        val latLngs: MutableList<LatLng> = ArrayList()
        latLngs.add(request.getPickupPoint())
        latLngs.add(request.getDestinationPoint())
        latLngs.add(markerDriver!!.position)
        mMap!!.setPadding(0, 0, 0, 850)
        val builder = LatLngBounds.Builder()
        for (location in latLngs) builder.include(location)
        val bounds = builder.build()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, 150)
        mMap!!.animateCamera(cu)*/
        //((RequestFragment)requestCardsAdapter.getFragment(travel,requestCardsAdapter.getPositionWithTravelId(travel.getId()))).locationChanged(markerDriver.getPosition());
    }

    override fun onInvisible(request: Request) {
        removeMarkers()
    }

    private fun removeMarkers() {
        if (markerPickup != null) markerPickup!!.remove()
        if (markerDropOff != null) markerDropOff!!.remove()
        markerPickup = null
        markerDropOff = null
        mMap!!.setPadding(0, 0, 0, 0)
    }

    companion object {
        const val ACTIVITY_PROFILE = 11
        const val ACTIVITY_WALLET = 12
        const val ACTIVITY_TRAVEL = 14
    }
}