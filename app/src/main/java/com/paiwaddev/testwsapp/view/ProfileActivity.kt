package com.paiwaddev.testwsapp.view

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.material.button.MaterialButton
import com.paiwaddev.ahorros.ui.main.goal.UsersViewModel
import com.paiwaddev.ahorros.ui.main.goal.UsersViewModelFactory
import com.paiwaddev.testwsapp.R
import com.paiwaddev.testwsapp.utils.MyApplication


class ProfileActivity : AppCompatActivity(), View.OnClickListener, AlertMessage.ItemListener {
    private val userViewModel: UsersViewModel by viewModels {
        UsersViewModelFactory(this, (application as MyApplication).repository)
    }

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null

    private lateinit var iamgeProfile: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvUser: TextView
    private lateinit var tvPass: TextView
    private lateinit var tvCardID: TextView
    private lateinit var tvPhone: TextView
    private lateinit var buttonLogout: MaterialButton
    private lateinit var buttonMap: MaterialButton
    private lateinit var alertMessage: AlertMessage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        this.title = "Profile"

        bindingView()
        val userID = getIntent().getIntExtra("userID", 0).toLong()

        userViewModel.getUserByID(userID).observe(this, { user ->

            user.image?.let {
                val bmp = BitmapFactory.decodeByteArray(user.image, 0, it.size)
                iamgeProfile.setImageBitmap(bmp)
            }
            tvName.text = user.firstname + " " + user.lastname
            tvUser.text = user.username
            tvPass.text = user.password
            tvCardID.text = user.cardId

            user.phone?.let {
                tvPhone.text = user.phone
            }

        })

        buttonLogout.setOnClickListener(this)
        buttonMap.setOnClickListener(this)
    }

    fun bindingView(){
        iamgeProfile = findViewById(R.id.iamgeProfile)
        tvName =findViewById(R.id.tvName)
        tvUser = findViewById(R.id.tvProfileUser)
        tvPass = findViewById(R.id.tvProfilePass)
        tvCardID = findViewById(R.id.tvProfileCardID)
        tvPhone = findViewById(R.id.tvProfilePhone)
        buttonLogout = findViewById(R.id.button_logout)
        buttonMap = findViewById(R.id.button_map)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onBackPressed() {
        println("back")
        super.onBackPressed();
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.button_logout -> {
                alertMessage = AlertMessage(
                    this,
                    resources.getString(R.string.message_logout_confirm),
                    "OK",
                    "Cancel"
                )
                alertMessage.setListener(this)
            }

            R.id.button_map -> {

                val intent = Intent(Intent.ACTION_VIEW)
                intent.data =
                    Uri.parse("geo:${(lastLocation)!!.latitude},${(lastLocation!!.longitude)}?q=${(lastLocation)!!.latitude},${(lastLocation!!.longitude)}(MyHome)")
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            }

        }
    }

    public override fun onStart() {
        super.onStart()
        if (!checkPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions()
            }
        }
        else {
            getLastLocation()
        }
    }

    override fun onItemClicked(IsAction: Int) {
        if(IsAction == AlertMessage.POSITIVE){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient?.lastLocation!!.addOnCompleteListener(this) { task ->
            if (task.isSuccessful && task.result != null) {
                lastLocation = task.result
            }
            else {
                Log.w(TAG, "getLastLocation:exception", task.exception)
                showMessage("No location detected. Make sure location is enabled on the device.")
            }
        }
    }
    private fun showMessage(string: String) {
        val container = findViewById<View>(R.id.linearLayout)
        if (container != null) {
            Toast.makeText(this@ProfileActivity, string, Toast.LENGTH_LONG).show()
        }
    }
    private fun showSnackbar(
        mainTextStringId: String, actionStringId: String,
        listener: View.OnClickListener
    ) {
        Toast.makeText(this@ProfileActivity, mainTextStringId, Toast.LENGTH_LONG).show()
    }
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }
    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this@ProfileActivity,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }
    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showSnackbar("Location permission is needed for core functionality", "Okay",
                View.OnClickListener {
                    startLocationPermissionRequest()
                })
        }
        else {
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> {
                    Log.i(TAG, "User interaction was cancelled.")
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    getLastLocation()
                }
                else -> {
                    showSnackbar("Permission was denied", "Settings",
                        View.OnClickListener {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                Build.DISPLAY, null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
    companion object {
        private val TAG = "LocationProvider"
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }
}