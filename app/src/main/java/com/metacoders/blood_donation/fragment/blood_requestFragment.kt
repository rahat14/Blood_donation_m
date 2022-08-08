package com.metacoders.blood_donation.fragment

import com.metacoders.blood_donation.R
import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.metacoders.blood_donation.SharedPrefManager
import com.metacoders.blood_donation.databinding.FragmentBloodRequestBinding
import com.metacoders.blood_donation.model.Users


class blood_requestFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var binding: FragmentBloodRequestBinding
    private var uid = ""
    private var userLocation: LatLng = LatLng(0.0, 0.0)
    var bloodGroup = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBloodRequestBinding.inflate(inflater, container, false)
        uid = FirebaseAuth.getInstance().uid.toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bloods = resources.getStringArray(R.array.blood_group)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bloods)
        binding.bg.adapter = adapter

        binding.bg.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                bloodGroup = bloods[position].toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
                bloodGroup = ""
            }

        }

        Dexter
            .withContext(requireContext())
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                }
            })
            .check()
        fusedLocationClient = LocationServices
            .getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest.create()
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 20000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY


        binding.submitBtn.setOnClickListener {

            val phn = binding.ph.text.toString()
            val addres = binding.locationEt.text.toString()

            if (phn.isEmpty() || addres.isEmpty() || bloodGroup.contains("Blood")) {
                Toast.makeText(context, "Please Check Input Data ", Toast.LENGTH_SHORT).show()
            } else {
                uploadData(phn, addres)
            }


        }

        getCurrentLocations()

        binding.mapButton.setOnClickListener {
            startLocationService()
        }


    }

    private fun startLocationService() {
//        val b: ActivityShoplocationDialogBinding =
//            ActivityShoplocationDialogBinding.inflate(LayoutInflater.from(this@AddBusinessActivity))
        val dialog = Dialog(requireActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.map_dialouge)
        val mapView: MapView = dialog.findViewById(R.id.mapView)
        val submitBtn: MaterialButton = dialog.findViewById(R.id.saveLocation)

        MapsInitializer.initialize(requireContext())


        mapView.onCreate(dialog.onSaveInstanceState())
        mapView.onResume()
        mapView.getMapAsync { googleMap -> // storing location to temporary variable
            val latLng = LatLng(
                userLocation.latitude,
                userLocation.longitude
            ) //your lat lng
            val marker: Marker? = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("zxxxx")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            // Enable GPS marker in Map
            googleMap.isMyLocationEnabled = true
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 1000, null)
            googleMap.setOnCameraMoveListener {
                val midLatLng = googleMap.cameraPosition.target
                if (marker != null) {
                    marker.position = midLatLng
                    userLocation = marker.position
                    Log.d("TAG", "startLocationService: ${userLocation.latitude}   long ->  ${userLocation.longitude}")
                }
            }
        }


        dialog.setCancelable(false)
        
        submitBtn.setOnClickListener {

            dialog.dismiss()
        }
        dialog.show()
    }

    private fun uploadData(phn: String, addres: String) {
        val user : Users? = SharedPrefManager.get("user") as Users?
        val mref = FirebaseDatabase.getInstance().getReference("Blood_Req")
        val key = mref.push().key.toString()
        val map = HashMap<String, Any>()
        map["userId"] = uid
        map["userName"] = user?.userName.toString()
        map["address"] = addres
        map["phn"] = phn
        map["reqID"] = key
        map["lat"] = userLocation.latitude
        map["lon"] = userLocation.longitude
        map["reqBlood"] = bloodGroup
        map["time"] = System.currentTimeMillis() / 1000

        mref.child(key).setValue(map).addOnCompleteListener {
            Toast.makeText(context, "Data Requested", Toast.LENGTH_SHORT).show()
            val navOptions: NavOptions = NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build()
            findNavController().navigate(R.id.homeFragment , null , navOptions)
        }.addOnFailureListener {
            Toast.makeText(context, "Error : ${it.message}", Toast.LENGTH_LONG).show()
        }

    }

    private fun isLocataionEnabled(): Boolean {
        val locationManager: LocationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 100
        )
    }


    private fun checkPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED)

    }

    private fun getCurrentLocations() {
        if (checkPermission()) {

            if (isLocataionEnabled()) {

                val loccall: LocationCallback = object : LocationCallback() {
                    override fun onLocationResult(p0: LocationResult) {
                        super.onLocationResult(p0)
                        val locations = p0.locations
                        val location = locations[0]

                        userLocation = LatLng(location.latitude, location.longitude)



                        Log.d(
                            "TAG",
                            "onLocationResult:  ${location.latitude}  ${location.longitude}"
                        )


                    }
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    loccall,
                    Looper.getMainLooper()
                )

            } else {
                Toast.makeText(requireContext(), "Please  Enable Locaiton", Toast.LENGTH_SHORT)
                    .show()


                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

        } else {
            requestPermission()
        }
    }
}