package com.ayberk.foodapp

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.ayberk.foodapp.databinding.FragmentMapBinding
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationCallback
import com.huawei.hms.location.LocationRequest
import com.huawei.hms.location.LocationResult
import com.huawei.hms.location.LocationServices
import com.huawei.hms.location.SettingsClient
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MapFragment : Fragment() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient
    private var _binding: FragmentMapBinding? = null
    private var mLocationCallback: LocationCallback? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        settingsClient = LocationServices.getSettingsClient(requireContext())

        binding.btnLocation.setOnClickListener {
            requestLocationPermission()
        }

        binding.btnLocationRemove.setOnClickListener {
            removeLocationUpdatesWithCallback()
        }
        return view
    }

    fun requestLocationUpdates(){

        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 3000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult != null) {

                    Toast.makeText(
                        requireContext(),
                        "LocationX: " + locationResult.lastLocation.latitude.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                    Toast.makeText(
                        requireContext(),
                        "LocationY: " + locationResult.lastLocation.longitude.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
            .addOnSuccessListener {

            }
            .addOnFailureListener { e: Exception ->
                Log.e(TAG, "checkLocationSetting onFailure:${e.message}")
            }
    }

    private fun requestLocationPermission() {
        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (arePermissionsGranted(permissions)) {
            // İzinler zaten verildi
            requestLocationUpdates()
        } else {
            // İzinleri kullanıcıdan iste
            requestPermissions(permissions,PERMISSION_GRANTED)
        }
    }

    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_GRANTED) {
            if (arePermissionsGranted(permissions)) {
                // İzinler verildi
                requestLocationUpdates()
            } else {
                // İzinler reddedildi, kullanıcıyı bilgilendirin
                Toast.makeText(
                    requireContext(),
                    "Konum izni reddedildi.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun removeLocationUpdatesWithCallback() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
            .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Lokasyon Durduruldu",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Lokasyon Durdurulamadi",
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.e(
                        TAG,
                        "removeLocationUpdatesWithCallback onFailure:${e.message}"
                    )
                }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Hata",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}