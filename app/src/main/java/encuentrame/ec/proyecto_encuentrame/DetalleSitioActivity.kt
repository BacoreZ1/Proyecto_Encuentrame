package encuentrame.ec.proyecto_encuentrame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_detalle_sitio.*


class DetalleSitioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_sitio)

        var sitio= intent.getSerializableExtra("sitio") as Sitios

        //ya se tiene informacion y ahora se seta la activitada

        //setear las variables

        tv_descripcion.text = sitio.descripcion
        supportActionBar!!.title= sitio.nombre
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        Glide.with(this)
                .load(sitio.foto)
                .into(img_foto)


    }
//
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        //when swuit como n java
        when(item!!.itemId){
            //capturar evento de boton hacina atras
            android.R.id.home ->{
                //terminar evento
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
