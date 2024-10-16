package com.kotlin.sacalabici.framework

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider

import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.routes.RouteBase
import com.kotlin.sacalabici.framework.viewmodel.MapViewModel
import com.kotlin.sacalabici.framework.views.activities.ModifyRouteActivity
import com.kotlin.sacalabici.framework.views.fragments.DeleteRouteItemFragment

class RouteAdapter(
    private var rutasList: List<RouteBase>,
    private val onRutaSelected: (RouteBase) -> Unit,
    private val permit: Boolean
) : RecyclerView.Adapter<RouteAdapter.RutasViewHolder>() {

    private var selectedRuta: RouteBase? = null
    private var ruta: RouteBase? = null
    private lateinit var viewModelRoute: MapViewModel

    class RutasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloTextView: TextView = itemView.findViewById(R.id.TVTitulo)
        val distanciaTextView: TextView = itemView.findViewById(R.id.TVDistancia)
        val tiempoTextView: TextView = itemView.findViewById(R.id.TVTiempo)
        val nivelTextView: TextView = itemView.findViewById(R.id.TVNivel)
        val divider: View = itemView.findViewById(R.id.LLRutasDivider)
        val rutaContainer: View = itemView.findViewById(R.id.rutaContainer)
        val btnModificar: View = itemView.findViewById(R.id.btnMod)
        val btnEliminar: View = itemView.findViewById(R.id.btnDel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RutasViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ruta, parent, false)
        viewModelRoute = ViewModelProvider(parent.context as FragmentActivity).get(MapViewModel::class.java)
        return RutasViewHolder(view)
    }

    fun setSelectedRuta(ruta: RouteBase?) {
        selectedRuta = ruta
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RutasViewHolder, position: Int) {
        val ruta = rutasList[position]
        if (!permit) {
            holder.btnModificar.visibility = View.GONE
            holder.btnEliminar.visibility = View.GONE
        }
        holder.tituloTextView.text = ruta.titulo
        holder.distanciaTextView.text = "Distancia: ${ruta.distancia}"
        holder.tiempoTextView.text = "Tiempo Promedio: ${ruta.tiempo}"
        holder.nivelTextView.text = "Nivel: ${ruta.nivel}"

        // Cambiar color de background de elemento seleccionado
        if (selectedRuta == ruta) {
            holder.rutaContainer.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.selected_bg)
            )
        } else {
            holder.rutaContainer.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.white)
            )
        }

        // Manejar cambio de elemento seleccionado via input de usuario
        holder.itemView.setOnClickListener {
            val previousRuta = selectedRuta
            selectedRuta = ruta

            // Actualizar posición de elemento seleccionado
            notifyItemChanged(rutasList.indexOf(previousRuta))
            notifyItemChanged(rutasList.indexOf(selectedRuta))

            onRutaSelected(ruta)
            viewModelRoute.selectRuta(ruta)
            viewModelRoute.lastSelectedRuta = ruta
        }

        // Manejar clic en el botón de modificar
        holder.btnModificar.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ModifyRouteActivity::class.java)
            // Imprimir el valor exacto de la cadena
            Log.d("Ruta Seleccionada", "Valor de ruta.tiempo: '${ruta.tiempo}'")

            // Limpiar la cadena y dividirla por espacios
            val tiempoLimpio = ruta.tiempo.trim().replace("\\s+".toRegex(), " ")
            val partes = tiempoLimpio.split(" ")
            val horas = partes[0]
            val minutos = partes[2]

            intent.putExtra("ID", ruta.id)
            intent.putExtra("TITULO", ruta.titulo)
            intent.putExtra("DISTANCIA", ruta.distancia)
            intent.putExtra("TIEMPO", ruta.tiempo)
            intent.putExtra("HORAS", horas)
            intent.putExtra("MINUTOS", minutos)
            intent.putExtra("NIVEL", ruta.nivel)  // Si es necesario
            val coordenadasJson = Gson().toJson(ruta.coordenadas)
            intent.putExtra("COORDENADAS", coordenadasJson)

            context.startActivity(intent)
        }

        // Manejar clic en el botón de eliminar
        holder.btnEliminar.setOnClickListener {
            val rutaEliminar = ruta
            val context = holder.itemView.context
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager

            rutaEliminar.let { rutaEliminar.id }?.let { id ->
                val deleteFragment = DeleteRouteItemFragment.newInstance(id)
                deleteFragment.show(fragmentManager, "deleteFragment")
            }
        }


        // Desactivar línea divisora para el último elemento
        holder.divider.visibility = if (position == rutasList.size - 1) View.GONE else View.VISIBLE
    }

    override fun getItemCount(): Int {
        return rutasList.size
    }

    // Método para actualizar la lista de rutas
    fun updateRutas(newRutas: List<RouteBase>) {
        rutasList = newRutas
        notifyDataSetChanged()
    }
}
