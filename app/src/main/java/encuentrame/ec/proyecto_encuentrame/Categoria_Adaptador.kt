package encuentrame.ec.proyecto_encuentrame

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_categoria.view.*

class Categoria_Adaptador(var categorias: ArrayList<String>, var interfaz: interfazClickCategoria) : RecyclerView.Adapter<Categoria_Adaptador.ViewHolder>() {

    //inflamos el layout que es la forma en como se va a visualizar las categorias
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_categoria, parent, false)
        return ViewHolder(view)

    }

    //devuelve el numero de categorias
    override fun getItemCount(): Int {
        return categorias.size
    }

    //seteamos los elementos visuales
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var categoria = categorias.get(position)

        //seteo la categoria
        holder.view.tv_categoria.text = categoria
        //accion de clic
        holder.view.setOnClickListener {
            interfaz.filtrarPorCategoria(categoria)
        }

    }

    //Gestionar los elemento ( recibimos las refencias)
    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    }


    //creamos una interfaz para comunicar el adaptador con la clase Mapa
    interface interfazClickCategoria {

        fun filtrarPorCategoria(categoria: String)
    }
}