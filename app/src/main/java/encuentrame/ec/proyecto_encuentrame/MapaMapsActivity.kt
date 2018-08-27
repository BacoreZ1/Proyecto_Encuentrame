package encuentrame.ec.proyecto_encuentrame

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_mapa_maps.*
import retrofit2.Callback
import retrofit2.Retrofit
import android.Manifest.permission
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.annotation.NonNull
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*


class MapaMapsActivity : AppCompatActivity(), OnMapReadyCallback, Categoria_Adaptador.interfazClickCategoria {

    var marcadorUbicacion:MarkerOptions?=null

    var mMap: GoogleMap?= null
    var categorias= ArrayList<String>()
    var retrofitApi:RetrofitApi?= null
    var sitios=ArrayList<Sitios>()
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    var sitiosFiltrados: List<Sitios>?=null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var mSettingsClient: SettingsClient
    private lateinit var mCurrentLocation: Location
    private lateinit var mLocationSettingsRequest: LocationSettingsRequest

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private val REQUEST_CHECK_SETTINGS = 0x1
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2






    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        ///se hizo un cambio


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)
        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()

        btn_ubicacion.setOnClickListener{
            getLocation()
        }
        retrofitApi= RetrofitApi()

        retrofitApi!!.obteneraCategorias(object :CallbackApi<Categoria>{
            override fun correcto(respuesta: Categoria) {
                respuesta.categorias.forEach{
                    categorias.add(it)
                }
                //LLenar visualmente la lista de categorias
                var adaptador = Categoria_Adaptador(categorias,this@MapaMapsActivity) //creando adaptador con los iteq se realizcen
                rv_categorias.layoutManager= LinearLayoutManager(this@MapaMapsActivity, LinearLayout.HORIZONTAL, false)
                rv_categorias.adapter= adaptador

            }

            override fun error(error: String) {
              Toast.makeText(this@MapaMapsActivity, error, Toast.LENGTH_SHORT).show()
            }
        })

       // categorias.add("Hoteles")
       // categorias.add("Restaurantes")

    }



    override fun onMapReady(googleMap: GoogleMap) {
        //lmacenar en una variable para usar luego
        mMap = googleMap

        val ubicacion= LatLng(-4.0252113, -79.207801)
        //Crear el marcador de mi ubicacion

        marcadorUbicacion=MarkerOptions().position(ubicacion).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mi_posicion))
                .title("Mi posición")



        // Add a marker in Sydney and move the camera
        //la ubicacion ddonde se mostarra el mapa la podemos modificar

        //

        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(ubicacion))

        retrofitApi!!.obtenerSitios(object :CallbackApi<List<Sitios>>{

            override fun correcto(respuesta: List<Sitios>) {
                //Los vamos a mostrar en el mapa
                sitios.addAll(respuesta)
                sitios.forEach {
                    val ubicacionSitio = LatLng(it.latitud.toDouble(), it.longitud.toDouble())
                    mMap!!.addMarker(MarkerOptions().position(ubicacionSitio).title(it.nombre))
                }




            }

            override fun error(error: String) {
                Toast.makeText(this@MapaMapsActivity, error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun filtrarPorCategoria(categoria: String) {
        //esto filtra -> filter, dentro del cual va la confdicion con la cual quiero que se filttren mis elementos
        sitiosFiltrados = sitios.filter {
            it.categoria.equals(categoria)
        }

        //limpiar el mapa y dibujar nuevamente los markers
        mMap!!.clear()
        sitiosFiltrados!!.forEach {
            val ubicacionSitio = LatLng(it.latitud.toDouble(), it.longitud.toDouble())
            mMap!!.addMarker(MarkerOptions().position(ubicacionSitio).title(it.nombre))

        }
    }






    private fun buildLocationSettingsRequest() {
        mLocationSettingsRequest = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build()
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Log.e("Mapas", "" + location.latitude)
                    mCurrentLocation = location
                    mostrarUbicacionMapa(mCurrentLocation)
                    stopLocationUpdates()

                }
            }
        }
    }

    private fun mostrarUbicacionMapa(mCurrentLocation: Location?) {
        val cameraPosition = CameraPosition.Builder()
                .target(com.google.android.gms.maps.model.LatLng(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude))
                .zoom(14F)
                .build()

        var posicion=LatLng(mCurrentLocation.latitude,mCurrentLocation.longitude)

        marcadorUbicacion!!.position(posicion)
        mMap!!.addMarker(marcadorUbicacion)
        mMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))



        if (sitiosFiltrados != null) {

            sitiosFiltrados!!.forEach {
                val distancia = getDistance(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude, it.latitud.toDouble(), it.longitud.toDouble())
                it.distancia = distancia
            }

            sitiosFiltrados!!.sortedBy {it.distancia   }

            tv_title.text = sitiosFiltrados!![0].categoria + " cerca"
            tv_descripcion.text = sitiosFiltrados!![0].nombre

        } else {

            sitios.forEach {
                val distancia = getDistance(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude, it.latitud.toDouble(), it.longitud.toDouble())
                it.distancia = distancia
            }
            sitios.sortBy { it.distancia }

            tv_title.text = ""
            tv_descripcion.text = sitios[0].nombre
        }




    }

    fun createLocationRequest() {
        locationRequest = LocationRequest().apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this) {
                    Log.e("Mapas", "All location settings are satisfied.")
                    fusedLocationClient.requestLocationUpdates(locationRequest,
                            locationCallback, Looper.myLooper())

                }
                .addOnFailureListener(this) { e ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            Log.e("Mapas", "Location settings are not satisfied. Attempting to upgrade " + "location settings ")
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                val rae = e as ResolvableApiException
                                rae.startResolutionForResult(this@MapaMapsActivity, REQUEST_CHECK_SETTINGS)
                            } catch (se: IntentSender.SendIntentException) {
                                //   Log.i(FragmentActivity.TAG, "PendingIntent unable to execute request.")
                            }

                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage = "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                            //   Log.e(FragmentActivity.TAG, errorMessage)
                            Toast.makeText(this@MapaMapsActivity, errorMessage, Toast.LENGTH_LONG).show()
                            // requestingLocationUpdates = false
                        }
                    }
                    // updateUI()
                }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun getLocation() {
        if (checkPermissions()) {
            startLocationUpdates();
        } else {
            requestPermissions();
        }
    }


    /*Permisos*/
    private fun requestPermissions() {

        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)


        ActivityCompat.requestPermissions(this@MapaMapsActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE)

    }

    private fun checkPermissions(): Boolean {
        var permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i("Mapas", "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {

                Log.e("Mapas", "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();

            } else {
                Log.e("Mapas", "no se han habilitado los permisos")

                //notificar que el permiso no ha sido concedido
            }
        }
    }

    /*Activity Result */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
        // Check for the integer request code originally supplied to startResolutionForResult().
            REQUEST_CHECK_SETTINGS -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Log.e("Mapas", "User agreed to make required location settings changes.")
                        startLocationUpdates();
// Nothing to do. startLocationupdates() gets called in onResume again.
                    }
                    Activity.RESULT_CANCELED -> {
                        Log.e("Mapas", "User chose not to make required location settings changes.")


                        //requestingLocationUpdates = false;
                        //updateUI();
                    }
                }
            }
        }
    }


    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371; // km
        val dLat = toRad(lat1 - lat2);
        val dLon = toRad(lon1 - lon2);

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c;
    }

    fun toRad(num:Double): Double {
        return num * Math.PI / 180
    }


}
