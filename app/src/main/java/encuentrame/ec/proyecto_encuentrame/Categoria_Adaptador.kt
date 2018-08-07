package encuentrame.ec.proyecto_encuentrame

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_categoria.view.*

class Categoria_Adaptador(var categorias:ArrayList<String>): RecyclerView.Adapter<Categoria_Adaptador.ViewHolder>(){


    //Inflar nuestra vista(vamos a declar el layaout q vamos utilizar)
    //dibuja
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_categoria, parent, false)
        return ViewHolder(view)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //public suma ():int publuic int

    //devuelve cuantos elementos devuelve el adaptador
    //numero de elementos que tiene el adaptador*
        override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return  categorias.size
    }

    //setea los valores en la vista(o visualmente)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var categoria= categorias.get(position)

        //seteo la categoria
        holder.view.tv_categoria.text = categoria
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

//Gestionar los elemento ( recibimos las refencias)
    class ViewHolder(var view:View):RecyclerView.ViewHolder(view){

    }
}