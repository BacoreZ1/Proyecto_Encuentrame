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

/**
 * Es la actividad principal con la que va a iniciar el proyecto en la que tenemos el inicio de sesion de la actividad la que nos servira para autentificarnos en la aplicacion
 * @author:
 *
 */
class LogginActivity : AppCompatActivity() {
    //Campos de la clase
    var retrofitApi: RetrofitApi? = null
    var manager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loggin)

        retrofitApi = RetrofitApi()//Objeto para realizar las peticiones al servidor
        manager = CallbackManager.Factory.create();//Objeto que nos brinda facebook para el inicio de sesion

        login_button //es un plugin que nos permite hacer kotlin que nos deja obtener la referencia de los elementos visuales (el boton)
        login_button.setReadPermissions("email");//permisos de lectura del correo electronico
        /**
         * Metodo otorgado por facebook que nos permite que al Boton login_button
         *
         *Éste metodo nos permite autentificarnos con facebook y hacer un control de si esque se ha realizado un acceso previo ademas de controlar si esque existe un error al momento de realizar la sesion, en el caso de que este correcto el inicio de sesion nos envia al siguiente Layout MapsActivity
         *
         * @param manager  objeto brindado por facebook para el inicio de sesion
         * @param FacebookCallback interfaz de facebook que nos permite llamar funciones onSuccess el cual nos avisa si hemos iniciado session correctamente, onCancel nos avisa si cancelamos el inicio de sesion, onError en el caso de que se sucito un error de conexion o algun problema, estos callback se ejecutan internamente
         */
        login_button.registerCallback(manager, object : FacebookCallback<LoginResult> {
            /**
             * Metodo interno para realizar una autentificación
             *
             *Éste metodo nos permite verificar si esque hemos realizado un ingreso previo llamando al autentificarnos con facebook y hacer un control de si esque se ha realizado un acceso previo ademas de controlar si esque existe un error al momento de realizar la sesion, en el caso de que este correcto el inicio de sesion nos envia al siguiente Layout MapsActivity
             *
             * @param loginResult  objeto brindado por facebook para el inicio de sesion
             *
             */
            override fun onSuccess(loginResult: LoginResult) {
                if (Profile.getCurrentProfile() == null) {//llegan nuestros datos si la consulta se demora en traer los datos aunque suele demorar y se verifica si llegaron los datos
                    object : ProfileTracker() {//Un escuchador para avisar cuando los datos llegan y tiene
                        override fun onCurrentProfileChanged(oldProfile: Profile?, currentProfile: Profile?) {//Compara extrae y compara los datos del metodo registroUsuario quien compara en la base de datos si exite el usuario y si se lo va a crear

                            retrofitApi!!.resgistroUsuario(currentProfile!!.firstName, currentProfile.lastName, object : CallbackApi<String> {//nos envia el nombre, apellido  y un callcabk para que nos obtenga un mensaje
                                override fun correcto(Respuesta: String) {//si la respuesta es correcta no envia al siguiente layout
                                    startActivity(Intent(this@LogginActivity, MapaMapsActivity::class.java))
                                }

                                override fun error(error: String) {//si la respuesta es incorrecta nos muestra mensaje de error
                                    Toast.makeText(this@LogginActivity, error, Toast.LENGTH_LONG).show()
                                }
                            })
                        }
                    }
                } else {//en el caso de que esten cargados los datos del usuario se envia directamente a verificar perfil y a registrar usuario
                    Toast.makeText(this@LogginActivity, Profile.getCurrentProfile().toString(), Toast.LENGTH_LONG).show()
                    Log.e("Login activity", Profile.getCurrentProfile().lastName)
                    retrofitApi!!.resgistroUsuario(Profile.getCurrentProfile().firstName, Profile.getCurrentProfile().lastName, object : CallbackApi<String> {
                        override fun correcto(Respuesta: String) {
                            var intent = Intent(this@LogginActivity, MapaMapsActivity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        }

                        override fun error(error: String) {
                        }
                    })
                }
            }

            override fun onCancel() {//en en caso de que se cancele el inicio de sesion
            }

            override fun onError(exception: FacebookException) {// si esque sucede algun error
                Log.e("fb", exception.toString());
            }
        })
        if (Profile.getCurrentProfile() != null) {//verificamos si esque ya hemos iniciado sesion anteriormente para enviar directamente a al layout de mapsActivity
            var intent = Intent(this@LogginActivity, MapaMapsActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
    //es un metodo que nos muestra la interfaz para mostrar si deseamos acceder o no a facebook y nos devuelve una respuesta que se controla en manager
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        manager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}

