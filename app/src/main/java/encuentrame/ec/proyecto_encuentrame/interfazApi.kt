package encuentrame.ec.proyecto_encuentrame

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface interfazApi{
    @POST(value = "sitios/registro")
    fun registrarUsuario(@QueryMap params: HashMap<String, Any>): Call<String>
}