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

class MapaMapsActivity : AppCompatActivity(), OnMapReadyCallback, Categoria_Adaptador.interfazClickCategoria {

    var mMap: GoogleMap?= null
    var categorias= ArrayList<String>()
    var retrofitApi:RetrofitApi?= null
    var sitios=ArrayList<Sitios>()

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

        // Add a marker in Sydney and move the camera
        //la ubicacion ddonde se mostarra el mapa la podemos modificar

        //
        val ubicacion= LatLng(-4.0252113, -79.207801)
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
        var  sitiosFiltrados = sitios.filter {
            it.categoria.equals(categoria)
        }

        //limpiar el mapa y dibujar nuevamente los markers
        mMap!!.clear()
        sitiosFiltrados.forEach {
            val ubicacionSitio = LatLng(it.latitud.toDouble(), it.longitud.toDouble())
            mMap!!.addMarker(MarkerOptions().position(ubicacionSitio).title(it.nombre))

        }
    }



}
