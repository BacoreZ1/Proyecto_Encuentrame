package encuentrame.ec.proyecto_encuentrame

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.facebook.CallbackManager
import kotlinx.android.synthetic.main.activity_loggin.*
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.FacebookCallback
import com.facebook.login.widget.LoginButton


class LogginActivity : AppCompatActivity() {


    var manager: CallbackManager? = null//el signo de interrogacion nos permite asignar un nulo a una variable ?

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
            var correo = et_correo.text.toString()
            //obteniendo el texto que escriben en el campo contraseña
            var contraseña = et_contraseña.text.toString()
            //ACCION QUE QUEIRES REALIZAR
            Toast.makeText(this, "correo: $correo contraseña  $contraseña", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MapaMapsActivity::class.java)
            startActivity(intent)
        }
        btn_crear_cuenta.setOnClickListener {

            //NAVEGACION HACIA LA OTRA VISTA
            val intent = Intent(this, Crear_CuentaActivity::class.java)
            startActivity(intent)
        }


        //java et_correo.setText("sdsd")
        // referenciar en java PARA VARIABLESvar etcorre= findViewById<EditText>(R.id.et_correo)

        manager = CallbackManager.Factory.create();
        login_button //es un plugin que nos permite hacer kotlin que nos deja obtener la referencia de los elementos visuales
        login_button.setReadPermissions("email");


        login_button.registerCallback(manager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // Nos llega la respuesta
            }

            override fun onCancel() {
                // En caso cancelar el inisio de sesion
            }

            override fun onError(exception: FacebookException) {
                // En caso de que ocurra un error
            }
        })//el callback manda una referencia y esperas a que te devuelva una respuesta




    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        manager!!.onActivityResult(requestCode, resultCode, data)//nos sale error y kotlin usa el !! para asegurar que la variable que va a venir no va a ser nula
        super.onActivityResult(requestCode, resultCode, data)
    }
}

