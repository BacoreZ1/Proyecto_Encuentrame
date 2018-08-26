package encuentrame.ec.proyecto_encuentrame

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitApi {

    companion object {
        val PATH_API = "https://encuentramepavel.000webhostapp.com/public/index.php/"
    }

    val retrofit = Retrofit.Builder()
            .baseUrl(PATH_API)
            .addConverterFactory(GsonConverterFactory.create())//se agrega para el tratamineto d GSON
            .build()

    //configurar el objeto peticion
    val request = retrofit.create(interfazApi::class.java)

    fun resgistroUsuario(nombre: String, apellido: String, callbackApi: CallbackApi<String>) {

        val parametros = HashMap<String, Any>()
        parametros.put("nombre", nombre)
        parametros.put("apellido", apellido)

        //enquee lanza una peticiom en segundo plano
        request.registrarUsuario(parametros).enqueue(object : Callback<Respuesta> {
            override fun onFailure(call: Call<Respuesta>?, t: Throwable?) {
                //en caso d errror
                Log.e("RetrofitApi: onError",t.toString())
                callbackApi.error(t.toString())
            }

            override fun onResponse(call: Call<Respuesta>?, response: Response<Respuesta>?) {
                //en caso de q sea correcto
                Log.e("RetrofitApi: onSucces",response!!.body()!!.Mensaje)
                callbackApi.correcto(response!!.body()!!.Mensaje)
            }
        })
    }

    fun obteneraCategorias(callbackApi: CallbackApi<Categoria>){
        request.obtenerCategorias().enqueue(object : Callback<Categoria>{
            override fun onFailure(call: Call<Categoria>, t: Throwable) {
                callbackApi.error(t.toString())
            }

            override fun onResponse(call: Call<Categoria>, response: Response<Categoria>) {
                callbackApi.correcto(response!!.body()!!)
            }
        })


    }

    fun obtenerSitios(callbackApi: CallbackApi<List<Sitios>>){
        request.obtenerSitios().enqueue(object : Callback<List<Sitios>>{
            override fun onFailure(call: Call<List<Sitios>>, t: Throwable) {
                callbackApi.error(t.toString())
            }

            override fun onResponse(call: Call<List<Sitios>>, response: Response<List<Sitios>>) {
                callbackApi.correcto(response!!.body()!!)
            }
        })


    }

}