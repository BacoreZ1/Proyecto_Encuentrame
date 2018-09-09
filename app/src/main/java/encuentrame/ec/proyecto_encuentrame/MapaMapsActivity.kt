package encuentrame.ec.proyecto_encuentrame

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_mapa_maps.*

/**
 * Esta esta clase es en la que vamos a controlar el mapa, las categorias y los sitios de nuestra aplicacion
 *
 *
 * @param AppCompatActivity  nos va a permitir crear un layout para poder insertar la lista de categorias
 * @param OnMapReadyCallback  nos va a permitir crear el mapa de google
 * @param Categoria_Adaptador  nos va a permitir crear un adaptador de cada categoria para poder cargarlo en la lista
 * @param OnMarkerClickListener  nos va a permitir dar click en los marcadores que se dibujan en el mapa
 * @param OnInfoWindowClickListener  nos va a permitir colocar informacion encima de los marcadores de los sitios en el mapa
 */
class MapaMapsActivity : AppCompatActivity(), OnMapReadyCallback, Categoria_Adaptador.interfazClickCategoria, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    /**
     * Este metodo nos va a permitir mostrar el titulo del sitio cuando demos click en el marcador del mismo y nos va a llevar a la siguiente ventana para mostrar los detalles del sitio
     *
     * @param p0 variable que almacena el sitio
     */
    override fun onInfoWindowClick(p0: Marker?) {
        var busqueda = sitios.find {
            it.nombre.equals(p0!!.title)
        }
        val intent = Intent(this@MapaMapsActivity, DetalleSitioActivity::class.java)
        //nos permite enviar la informacion de una ventana a otra que debe estar serializable para poder recibir estos parametros
        intent.putExtra("sitio", busqueda)
        startActivity(intent)
    }

    /**
     * Este metodo nos va a permitir dar click en el marcador del sitio para que nos cargue la informacion del sitio seleccionado
     *
     * @param p0 variable que almacena el sitio
     * @return boolean de de verdadero si se cargo el sitio
     */
    override fun onMarkerClick(p0: Marker?): Boolean {
        p0!!.showInfoWindow()
        return true
    }

    var marcadorUbicacion: Marker? = null

    var mMap: GoogleMap? = null
    var categorias = ArrayList<String>()
    var retrofitApi: RetrofitApi? = null
    var sitios = ArrayList<Sitios>()
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    var sitiosFiltrados: List<Sitios>? = null
    //Variables para poder obtener la ubicacion
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

    /**
     * Este metodo va a iniciarse al abrir el layout y nos permite cargar el mapa asi como inicializar los botones y cargar las categorias a la lista
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)//aqui almacenamos la informacion que tenemos previamente cargada OJOOJOJOJOJOJOJOJOJOJOJOJOJ
        setContentView(R.layout.activity_mapa_maps)//llamamos al layout
        //Estos parametros nos los otora google para que se muestre el mapa
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Variables utilizadas para obtener la ubicacion
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)
        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()

        //Boton que nos permite determinar la ubicacion de un cliente al hacer click
        btn_ubicacion.setOnClickListener {
            getLocation()
        }

        retrofitApi = RetrofitApi()
        /**
         * Este metodo nos ca a permitir llamar todas las categorias
         *
         * @param CallbackApi nos va a devolver todas las categorias en una lista
         */
        retrofitApi!!.obteneraCategorias(object : CallbackApi<Categoria> {
            /**
             * Este metodo nos va a recibir las categorias y las va a ir añadiendo en la lista de manera horizontal
             *
             * @param Categoria parametro del tipo categoria
             */
            override fun correcto(respuesta: Categoria) {
                respuesta.categorias.forEach {
                    categorias.add(it)//se añade las categorias a un array de categorias
                }
                //Se llena la lista con las categorias de manera visual en un recicler view mediante un adaptador
                var adaptador = Categoria_Adaptador(categorias, this@MapaMapsActivity) //creando adaptador con los iteq se realizcen
                rv_categorias.layoutManager = LinearLayoutManager(this@MapaMapsActivity, LinearLayout.HORIZONTAL, false)
                rv_categorias.adapter = adaptador

            }

            // este metodo recibe el mensaje de error al cargar las categorias y informa sobre el posible error
            override fun error(error: String) {
                Toast.makeText(this@MapaMapsActivity, error, Toast.LENGTH_SHORT).show()
            }
        })

        //Al realizar click en el Boton de salir cerramos sesion y retornamos al layout de login
        btn_salir.setOnClickListener {
            LoginManager.getInstance().logOut()
            val intent = Intent(this@MapaMapsActivity, LogginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

    }

    /**
     * Este metodo nos va a cargar el mapa de google ademas que nos va a permitir pintar los puntos en el mapa
     *
     * @param GoogleMap recibe el mapa de google
     */
    override fun onMapReady(googleMap: GoogleMap) {
        //Variable que nos permite almacenar el mapa de google
        mMap = googleMap
        //Parametro que nos permite dar click en los marcadores
        mMap!!.setOnMarkerClickListener(this)
        //Parametro que nos permite dar click en la informacion de los marcadores
        mMap!!.setOnInfoWindowClickListener(this)
        //variable provisional para almacenar una latitud y una longitud
        val ubicacion = LatLng(-4.030588, -79.199514)
        //Nos permite dar un acercamiento de camara cuando seleccionemos nuestra ubicacion
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(ubicacion))
        /**
         * Este metodo nos permite obtener todos los sitios y guardarlos en una lista
         *
         * @param CallbackApi recibe una lista de sitios
         */
        retrofitApi!!.obtenerSitios(object : CallbackApi<List<Sitios>> {
            //Si la respuesta al obtener los sitios es correcta agrega los sitios a la variable sitios
            override fun correcto(respuesta: List<Sitios>) {

                sitios.addAll(respuesta)
                //Recorre los sitios y va marcando en el mapa segun la longitud y latitud de cada sitio
                sitios.forEach {
                    val ubicacionSitio = LatLng(it.latitud.toDouble(), it.longitud.toDouble())
                    mMap!!.addMarker(MarkerOptions().position(ubicacionSitio)
                            .title(it.nombre)
                    )
                }

            }

            //marca error si no se pueden cargar la lista de los sitios correctamente
            override fun error(error: String) {
                Toast.makeText(this@MapaMapsActivity, error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Este metodo nos va a permitir filtrar por categoria al momento de dar click en alguna categoria nos permite filtrar que sitios pertenecen a la misma
     *
     * @param categoria recibe la categoria que se ha seleccionado para ser filtrada
     */
    override fun filtrarPorCategoria(categoria: String) {
        //esta variable almacena los sitios que sean pertenecientes a la categoria que seleccionemos
        sitiosFiltrados = sitios.filter {
            it.categoria.equals(categoria)
        }
        //nos permite limpiar el mapa para volver a pintar los sitios segun la categoria seleccionada
        mMap!!.clear()
        //Nos ubica en el mapa un indicador de todos los sitios que tenemos segun la categoria que hayamos seleccionado
        sitiosFiltrados!!.forEach {
            val ubicacionSitio = LatLng(it.latitud.toDouble(), it.longitud.toDouble())
            mMap!!.addMarker(MarkerOptions().position(ubicacionSitio).title(it.nombre))

        }
        //permite controlar si esque nos ubicamos primero para saber el sitoi mas cernado
        if (::mCurrentLocation.isInitialized) {

            var posicion = LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude)

            marcadorUbicacion = mMap!!.addMarker(MarkerOptions().position(posicion).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mi_posicion))
                    .title("Mi posición"))


            sitiosFiltrados!!.sortedBy { it.distancia }
            tv_title.text = sitiosFiltrados!![0].categoria + " cerca"
            tv_descripcion.text = sitiosFiltrados!![0].nombre
        }
    }

    //funcion por defecto para la localizacion
    private fun buildLocationSettingsRequest() {
        mLocationSettingsRequest = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build()
    }

    //nos permite obtener la ubicacion del usuario, son metodos por defecto
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

    /**
     * Este metodo nos va a permitir mostrar la ubicacion del usuario en el mapa
     *
     * @param mCurrentLocation nos va a recibir las cordenadas de donde se encientra el usuario
     */
    private fun mostrarUbicacionMapa(mCurrentLocation: Location?) {
        //en esta variable vamos a guardar la posicion del usuario y vamos a dirigirnos a donde se encuentra
        val cameraPosition = CameraPosition.Builder()
                .target(com.google.android.gms.maps.model.LatLng(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude))
                .zoom(14F)
                .build()
        //Este if nos permite controlar nos permite eliminar la posicion anterior
        if (marcadorUbicacion != null) {
            marcadorUbicacion!!.remove()
        }
        //en esta variable cuardarmos la latitud y longitud de la ubicacion
        var posicion = LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude)

        //Crear el marcador de mi ubicacion
        marcadorUbicacion = mMap!!.addMarker(MarkerOptions().position(posicion).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mi_posicion))
                .title("Mi posición"))
        //Vamos a mover la posicion de la camara a donde se encuentra el usuario
        mMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        //nos permite guarddar el sitio mas cercano del sujeto en una variable distancia
        if (sitiosFiltrados != null) {

            sitiosFiltrados!!.forEach {
                val distancia = getDistance(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude, it.latitud.toDouble(), it.longitud.toDouble())
                it.distancia = distancia
            }
            //Presentamos el sitio mas cercano en las cajas de texto
            sitiosFiltrados!!.sortedBy { it.distancia }

            tv_title.text = sitiosFiltrados!![0].categoria + " cerca"
            tv_descripcion.text = sitiosFiltrados!![0].nombre

        } else {// si no tenemos sitio por categoria filtrado nos va a presentar de todos los sitios cual es el mas cercano

            sitios.forEach {
                val distancia = getDistance(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude, it.latitud.toDouble(), it.longitud.toDouble())
                it.distancia = distancia
            }

            sitios.sortBy { it.distancia }

            tv_title.text = sitios!![0].categoria + " cerca"
            tv_descripcion.text = sitios[0].nombre
        }


    }

    //Metodos por defecto que se encarga de obtener la ubicacion
    fun createLocationRequest() {
        locationRequest = LocationRequest().apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

    }

    //metodos por defecto que se encargan de obtener la ubicacion
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

    // metodo por defecto para detener la ubicacion
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    //metodo por defecto para obtener la ubicacion y solicitar los permisos
    fun getLocation() {
        if (checkPermissions()) {
            startLocationUpdates();
        } else {
            requestPermissions();
        }
    }

    //Metodo que nos sirve para pedir los permisos de localizacion
    private fun requestPermissions() {

        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)


        ActivityCompat.requestPermissions(this@MapaMapsActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE)

    }

    // Metodo que nos permite verificar si ya tenemos los permisos
    private fun checkPermissions(): Boolean {
        var permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    // Metodo que nos permite saber la respuesta en el caso de aceptar o no los permisos
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

    //Metoro que tambien permite destionar los permisos
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

    /**
     * Este metodo nos va a permitir calcular la distancia mas corta entre dos punto en el cual se van a receptar las cordenadas del usuario junto con las del sitio que se desee comparar
     *
     * @param lat1 nos rercibe una latitud
     * @param lon1 nos rercibe una longitud
     * @param lat2 nos rercibe una latitud que usaremos para comparar
     * @param lon2 nos rercibe una longitud que usaremos para comparar
     * @return Double nos va a devolver un valor para saber cual es el sitio mas cercano
     */
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

    fun toRad(num: Double): Double {
        return num * Math.PI / 180
    }


}