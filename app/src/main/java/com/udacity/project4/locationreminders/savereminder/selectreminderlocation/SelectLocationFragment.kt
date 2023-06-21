package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragmentDirections
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment() {

    // Use Koin to get the view model of the SaveReminder
    private lateinit var map: GoogleMap
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private val TAG = SelectLocationFragment::class.java.simpleName
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val _mapPosition = MutableLiveData<LatLng>()
    val mapPosition : LiveData<LatLng>
        get() = _mapPosition

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        enableMyLocation()
        setMapLongClick(map)
        setPoiClick(map)
        setMapStyle(map)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val layoutId = R.layout.fragment_select_location
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setDisplayHomeAsUpEnabled(true)

        // TODO: add the map setup implementation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        // TODO: zoom to the user location after taking his permission

        // TODO: add style to the map

        // TODO: put a marker to location that the user selected

        // TODO: call this function after the user confirms on the selected location
        binding.saveButton.setOnClickListener {
            onLocationSelected()
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.map_options, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.normal_map -> {
                        map.mapType = GoogleMap.MAP_TYPE_NORMAL
                    }

                    R.id.hybrid_map -> {
                        map.mapType = GoogleMap.MAP_TYPE_HYBRID
                    }

                    R.id.satellite_map -> {
                        map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                    }

                    R.id.terrain_map -> {
                        map.mapType = GoogleMap.MAP_TYPE_TERRAIN
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapClickListener { latLng ->
            _mapPosition.value = latLng
            val snippet = String.format(
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.clear()
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
        }
    }

    private fun setPoiClick(map: GoogleMap){
        map.setOnPoiClickListener { poi ->
            _mapPosition.value = poi.latLng
            val snippet = String.format(
                "Lat: %1$.5f, Long: %2$.5f",
                poi.latLng.latitude,
                poi.latLng.longitude
            )
            map.clear()
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
            poiMarker?.showInfoWindow()
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
            zoomToUserLocation()
            Toast.makeText(context, "Location permission is granted.", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    enableMyLocation()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    enableMyLocation()
                }

                else -> {
                    Snackbar.make(
                        requireView(),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(R.string.settings) {
                            startActivity(Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }.show()
                }
            }
        }

    @SuppressLint("MissingPermission")
    private fun zoomToUserLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
            } else {
                Toast.makeText(
                    requireContext(),
                    "Unable to get current location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun onLocationSelected() {
        // TODO: When the user confirms on the selected location,
        //  send back the selected location details to the view model
        //  and navigate back to the previous fragment to save the reminder and add the geofence
        val latitude = _mapPosition.value?.latitude
        val longitude = _mapPosition.value?.longitude
        if (latitude == null){
            Toast.makeText(requireActivity(), "No location picked yet", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireActivity(), "Location Saved $latitude, $longitude", Toast.LENGTH_SHORT).show()
            val directions = SelectLocationFragmentDirections
                .actionSelectLocationFragmentToSaveReminderFragment()
            _viewModel.navigationCommand.value = NavigationCommand.To(directions)

        }
    }



    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }
}