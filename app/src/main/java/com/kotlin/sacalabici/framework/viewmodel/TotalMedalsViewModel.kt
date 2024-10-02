import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.medals.MedalBase
import com.kotlin.sacalabici.data.repositories.medals.MedalsRepository
import com.kotlin.sacalabici.domain.medals.ConsultMedalsListRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TotalMedalsViewModel : ViewModel() {
    val medalsObjectLiveData = MutableLiveData<List<MedalBase>>()
    val permissionsLiveData = MutableLiveData<List<String>>()
    private val medalsListRequirement = ConsultMedalsListRequirement()

    fun getAnnouncementList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result: MedalBase? = medalsListRequirement()
                val reversedResult = result!!.?.reversed()
                medalsObjectLiveData.postValue(reversedResult)
            } catch (e: Exception) {
                medalsObjectLiveData.postValue(emptyList())
            }
        }
    }

}