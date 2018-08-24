package encuentrame.ec.proyecto_encuentrame

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

    fun resgistroUsuario(nombre: String, apellido: String) {
        val parametros = HashMap<String, Any>()
        parametros.put("nombre", nombre)
        parametros.put("apellido", apellido)
        //enquee lanza una peticiom en segundo plano
        request.registrarUsuario(parametros).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable?) {
                //en caso d errror
            }

            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                //en caso de q sea correcto
            }
        })
    }
}