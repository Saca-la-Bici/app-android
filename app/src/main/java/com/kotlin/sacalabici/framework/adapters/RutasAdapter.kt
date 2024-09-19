import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.RutasBase

class RutasAdapter(
    private var rutasList: List<RutasBase>,
    private val onRutaSelected: (RutasBase) -> Unit // Add the callback as a second parameter
) : RecyclerView.Adapter<RutasAdapter.RutasViewHolder>() {

    class RutasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloTextView: TextView = itemView.findViewById(R.id.TVTitulo)
        val distanciaTextView: TextView = itemView.findViewById(R.id.TVDistancia)
        val tiempoTextView: TextView = itemView.findViewById(R.id.TVTiempo)
        val nivelTextView: TextView = itemView.findViewById(R.id.TVNivel)
        val divider: View = itemView.findViewById(R.id.LLRutasDivider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RutasViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ruta, parent, false)
        return RutasViewHolder(view)
    }

    override fun onBindViewHolder(holder: RutasViewHolder, position: Int) {
        val ruta = rutasList[position]
        holder.tituloTextView.text = ruta.titulo
        holder.distanciaTextView.text = "Distancia: ${ruta.distancia}"
        holder.tiempoTextView.text = "Tiempo Promedio: ${ruta.tiempo}"
        holder.nivelTextView.text = "Nivel: ${ruta.nivel}"

        // Desactivar línea divisora para el último elemento
        holder.divider.visibility = if (position == rutasList.size - 1) View.GONE else View.VISIBLE

        // Set click listener for the item
        holder.itemView.setOnClickListener {
            onRutaSelected(ruta) // Trigger the callback with the selected route
        }
    }

    override fun getItemCount(): Int {
        return rutasList.size
    }

    // Método para actualizar la lista de rutas
    fun updateRutas(newRutas: List<RutasBase>) {
        rutasList = newRutas
        notifyDataSetChanged()
    }
}


