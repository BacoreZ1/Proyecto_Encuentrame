package encuentrame.ec.proyecto_encuentrame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_mapa_maps.*
import retrofit2.Callback
import retrofit2.Retrofit

class MapaMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var categorias= ArrayList<String>()
    var retrofitApi:RetrofitApi?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        ///se hizo un cambio


        retrofitApi= RetrofitApi()

        retrofitApi!!.obteneraCategorias(object :CallbackApi<Categoria>{
            override fun correcto(respuesta: Categoria) {
                respuesta.categorias.forEach{
                    categorias.add(it)
                }
                //LLenar visualmente la lista de categorias
                var adaptador = Categoria_Adaptador(categorias) //creando adaptador con los iteq se realizcen
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        //lmacenar en una variable para usar luego
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        //la ubicacion ddonde se mostarra el mapa la podemos modificar
        val sydney = LatLng(-2.0, 11.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}
