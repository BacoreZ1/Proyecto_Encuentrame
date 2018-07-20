package encuentrame.ec.proyecto_encuentrame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_crear__cuenta.*

class Crear_CuentaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear__cuenta)
        btn_crear_cuenta.setOnClickListener {
            //poner la accion
            Toast.makeText(this, "Creando Cuenta", Toast.LENGTH_LONG).show()
        }
        //se pone visible la flecha de atras
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    //controla los botones del toolbar
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        //Me indica a la  Item le de click
        when(item!!.itemId){
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
