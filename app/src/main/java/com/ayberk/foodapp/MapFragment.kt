package com.ayberk.foodapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.ayberk.foodapp.databinding.FragmentMapBinding
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationCallback
import com.huawei.hms.location.LocationRequest
import com.huawei.hms.location.LocationResult
import com.huawei.hms.location.LocationServices
import com.huawei.hms.location.SettingsClient
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapView
import com.huawei.hms.maps.MapsInitializer
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.SupportMapFragment
import com.huawei.hms.maps.model.BitmapDescriptorFactory
import com.huawei.hms.maps.model.CameraPosition
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MarkerOptions
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.Coordinate
import com.huawei.hms.site.api.model.LocationType
import com.huawei.hms.site.api.model.NearbySearchRequest
import com.huawei.hms.site.api.model.NearbySearchResponse
import com.huawei.hms.site.api.model.SearchStatus
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder


@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient
    private var _binding: FragmentMapBinding? = null
    private var mLocationCallback: LocationCallback? = null
    private val binding get() = _binding!!
    private var hMap: HuaweiMap? = null
    private var mMapView: MapView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
        MapsInitializer.initialize(requireContext())
        val mapViewBundle: Bundle? = null
        var mSupportMapFragment: SupportMapFragment? = null
        mSupportMapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?
        mSupportMapFragment?.getMapAsync(this@MapFragment)
        mMapView = view.findViewById(R.id.mapView)

        mMapView?.apply {
            onCreate(mapViewBundle)
            getMapAsync(this@MapFragment)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private fun getLocation() {

        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 180000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
       // requestLocationPermission()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val currentLatitude = locationResult.lastLocation.latitude
                val currentLongitude = locationResult.lastLocation.longitude
                val build = CameraPosition.Builder().target(LatLng(currentLatitude, currentLongitude)).zoom(12f).build()
                val cameraUpdate = CameraUpdateFactory.newCameraPosition(build)
                hMap?.animateCamera(cameraUpdate)
                hMap?.setMaxZoomPreference(20f)
                hMap?.setMinZoomPreference(1f)
                search(LatLng(currentLatitude, currentLongitude))
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.getMainLooper()
        ).addOnSuccessListener {}.addOnFailureListener {}
    }

    fun search(currentLocation: LatLng) {

        val searchService: SearchService
        searchService = SearchServiceFactory.create(requireContext(), URLEncoder.encode("DAEDAJqGqv3qLMkEkRGO1UArrTOo4NyRUIEyluie1ejXrbyesEd2Tx3NbUWbikCI4ph/rIOp7qdZKBh/RckmoINUAbMPhc9PINU38Q==", "utf-8"))
        val request = NearbySearchRequest()

        request.location = Coordinate(currentLocation.latitude, currentLocation.longitude)

        request.language = "tr"
        request.pageIndex = 1
        request.pageSize = 10

        request.query = "FOOD"
        request.radius = 5000 // 5 km civarini tarar
        request.poiType = LocationType.FOOD


        val resultListener: SearchResultListener<NearbySearchResponse?> =
            object : SearchResultListener<NearbySearchResponse?> {
                override fun onSearchResult(results: NearbySearchResponse?) {
                    val sites = results!!.sites
                    if (results == null || results.totalCount <= 0 || sites == null || sites.size <= 0) {
                        return
                    }
                    for (site in sites) {
                        if (site.name != null || site.poi.phone != null || site.formatAddress != null) {
                            val latLng = LatLng(site.location.lat, site.location.lng)
                            val title = site.name ?: ""
                            val snippet = site.formatAddress ?: ""
                            // Create a MarkerOptions for the location
                            val options = MarkerOptions()
                                .position(latLng)
                                .title(title)
                                .snippet(snippet)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.food))
                            hMap?.addMarker(options)

                        }
                        Log.i("TAG", String.format("siteId: '%s', name: %s\r\n", site.siteId, site.name))
                    }
                }

                override fun onSearchError(status: SearchStatus) {
                    Log.i("TAG", "Error : " + status.errorCode + " " + status.errorMessage)
                }
            }
        searchService.nearbySearch(request, resultListener)
    }


    fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE
        )

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (missingPermissions.isNotEmpty()) {
            // Eksik izinleri kullanıcıdan iste
            requestPermissions(missingPermissions, PERMISSION_GRANTED)
        } else {
            // İzinler zaten verilmiş, haritayı yükle
        }
    }

    // İzin iste sonuçları için onRequestPermissionsResult yöntemi
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_GRANTED) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Kullanıcı tüm izinleri kabul etti, haritayı yükle

            } else {
                // Kullanıcı izinleri reddetti, bir mesaj göster
                Toast.makeText(requireContext(), "İzinler reddedildi.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE])
    override fun onMapReady(map: HuaweiMap) {
        hMap = map
        hMap!!.isMyLocationEnabled = true
        getLocation()
        Toast.makeText(requireContext(), "Harita Yükleniyor...", Toast.LENGTH_SHORT).show()
        hMap!!.uiSettings.isMyLocationButtonEnabled = true


    }
}