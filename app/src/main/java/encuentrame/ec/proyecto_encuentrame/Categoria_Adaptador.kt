package encuentrame.ec.proyecto_encuentrame

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class Categoria_Adaptador(var categorias:ArrayList<String>): RecyclerView.Adapter<Categoria_Adaptador.ViewHolder>(){

    //inflar nuestra vista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       var view = LayoutInflater.from(parent.context).inflate(R.layout.item_categoria, parent, false)
        return ViewHolder(view)
    }


    //numero de elementos que tiene el adaptador
    override fun getItemCount(): Int {
       return categorias.size
    }


    //setea los valores en la vista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    //gestionar los elementos
    class ViewHolder(var view:View):RecyclerView.ViewHolder(view){

    }
}