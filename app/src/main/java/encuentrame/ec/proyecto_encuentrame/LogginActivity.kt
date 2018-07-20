package encuentrame.ec.proyecto_encuentrame

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_loggin.*

class LogginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loggin)
        //obteniendo el texto que escriben en el campo correo
        //et_correo.text.toString()
        //obteniendo el texto que escriben en el campo contraseña
       // et_contraseña.text.toString()

        //
        btn_inicio_sesion.setOnClickListener {
            //obteniendo el texto que escriben en el campo correo
           var  correo= et_correo.text.toString()
            //obteniendo el texto que escriben en el campo contraseña
            var contraseña =  et_contraseña.text.toString()
            //ACCION QUE QUEIRES REALIZAR
            Toast.makeText(this, "correo: $correo contraseña  $contraseña", Toast.LENGTH_LONG ).show()
            val intent = Intent(this, MapaMapsActivity::class.java )
startActivity(intent)
        }
        btn_crear_cuenta.setOnClickListener{

            //NAVEGACION HACIA LA OTRA VISTA
            val intent= Intent(this, Crear_CuentaActivity::class.java)
            startActivity(intent)
        }
        //java et_correo.setText("sdsd")
       // referenciar en java PARA VARIABLESvar etcorre= findViewById<EditText>(R.id.et_correo)
    }
}
