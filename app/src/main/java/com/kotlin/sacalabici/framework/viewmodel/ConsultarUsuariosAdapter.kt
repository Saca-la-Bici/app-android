import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosBase
import com.kotlin.sacalabici.databinding.ItemUserBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.modifyRole.ModifyRoleViewModel
import com.kotlin.sacalabici.framework.viewholders.ConsultarUsuariosViewHolder

class ConsultarUsuariosAdapter(
    private val modifyRoleViewModel: ModifyRoleViewModel, // ViewModel para modificar rol
    private val currentFragmentRole: String, // El rol actual del fragmento
) : RecyclerView.Adapter<ConsultarUsuariosViewHolder>() {
    var data: ArrayList<ConsultarUsuariosBase> = ArrayList()

    fun updateData(basicData: ArrayList<ConsultarUsuariosBase>) {
        this.data = basicData
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ConsultarUsuariosViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConsultarUsuariosViewHolder(binding, modifyRoleViewModel, currentFragmentRole)
    }

    override fun onBindViewHolder(
        holder: ConsultarUsuariosViewHolder,
        position: Int,
    ) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = data.size
}