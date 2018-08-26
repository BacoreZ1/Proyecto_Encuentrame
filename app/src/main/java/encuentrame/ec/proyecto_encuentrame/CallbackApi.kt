package encuentrame.ec.proyecto_encuentrame

interface CallbackApi<T> {
    fun correcto(respuesta: T)


    fun error(error: String)

}