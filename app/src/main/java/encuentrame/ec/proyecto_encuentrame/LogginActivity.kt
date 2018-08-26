package encuentrame.ec.proyecto_encuentrame

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.facebook.*
import kotlinx.android.synthetic.main.activity_loggin.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton


class LogginActivity : AppCompatActivity() {

    var retrofitApi:RetrofitApi?=null
    var manager: CallbackManager? = null//el signo de interrogacion nos permite asignar un nulo a una variable ?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loggin)
        //Instanciando la clase para el consumo del servidor
        retrofitApi=RetrofitApi()
        //obteniendo el texto que escriben en el campo correo
        //et_correo.text.toString()
        //obteniendo el texto que escriben en el campo contraseña
        // et_contraseña.text.toString()


        //java et_correo.setText("sdsd")
        // referenciar en java PARA VARIABLESvar etcorre= findViewById<EditText>(R.id.et_correo)

        manager = CallbackManager.Factory.create();
        login_button //es un plugin que nos permite hacer kotlin que nos deja obtener la referencia de los elementos visuales
        login_button.setReadPermissions("email");


        login_button.registerCallback(manager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // Nos llega la respuesta


                //verificamos si ya llegaron o existen datos del usuario q inició sesión

                if (Profile.getCurrentProfile() == null) {
                    object : ProfileTracker() {
                        override fun onCurrentProfileChanged(oldProfile: Profile?, currentProfile: Profile?) {
                            Log.e("Login activity",currentProfile!!.lastName)//te indica en donde surge el error de la clase
                            Toast.makeText(this@LogginActivity, currentProfile.toString(), Toast.LENGTH_LONG).show()
                            retrofitApi!!.resgistroUsuario(currentProfile!!.firstName, currentProfile.lastName, object :CallbackApi<String>{
                                override fun correcto(Respuesta: String) {
                                    //en caso de que sea correcto se envia a la ventana principal

                                    //iniciamos la actividad mediante un intent
                                    startActivity(Intent(this@LogginActivity,MapaMapsActivity::class.java))
                                }

                                override fun error(error: String) {
                                    //sino se presenta el error
                                    Toast.makeText(this@LogginActivity, error, Toast.LENGTH_LONG).show()
                                }
                            })//asi se llaman las interfaces
                            //!!-> indica q no va a ser un null
                        }
                    }

                } else {
                    Toast.makeText(this@LogginActivity, Profile.getCurrentProfile().toString(), Toast.LENGTH_LONG).show()
                    //creamos un objeto, en khotlin no es necesario el new

                    Log.e("Login activity",Profile.getCurrentProfile().lastName)
                    retrofitApi!!.resgistroUsuario(Profile.getCurrentProfile().firstName, Profile.getCurrentProfile().lastName,object :CallbackApi<String>{
                        override fun correcto(Respuesta: String) {
                            startActivity(Intent(this@LogginActivity,MapaMapsActivity::class.java))
                        }

                        override fun error(error: String) {

                        }
                    })
                }
            }

            override fun onCancel() {
                // En caso cancelar el inisio de sesion
            }

            override fun onError(exception: FacebookException) {
                Log.e("fb",exception.toString());
            }
        })//el callback manda una referencia y esperas a que te devuelva una respuesta


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        manager!!.onActivityResult(requestCode, resultCode, data)//nos sale error y kotlin usa el !! para asegurar que la variable que va a venir no va a ser nula
        super.onActivityResult(requestCode, resultCode, data)
    }
}

