package encuentrame.ec.proyecto_encuentrame

import java.io.Serializable

class Sitios: Serializable {
    var nombre: String = ""
    var descripcion: String = ""
    var foto: String = ""
    var categoria: String = ""
    var latitud: String = ""
    var longitud: String = ""
    var id: Int =0
    var distancia: Double= 0.0
}