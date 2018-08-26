package encuentrame.ec.proyecto_encuentrame

import retrofit2.Call
import retrofit2.http.*

interface interfazApi{

    @Headers("Content-Type: application/json") //avisarle al servidor q estamos enviando datos tipo JSON
    @POST("registro/Usuario")
    fun registrarUsuario(@Body params: HashMap<String, Any>): Call<Respuesta>

    @Headers("Content-Type: application/json") //avisarle al servidor q estamos enviando datos tipo JSON
    @POST("usuario/iniciar_usuario")
    fun iniciarSesionUsuario(@Body params: HashMap<String, Any>): Call<String>
}