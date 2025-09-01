import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.myapplication.network.RetrofitInstance

class CellBalanceViewModel : ViewModel() {
    private val _cells = MutableStateFlow<List<CellData>>(List(97) { idx -> CellData(index = idx, voltageDeviation = 0f) })
    val cells: StateFlow<List<CellData>> get() = _cells

    private fun defaultCells(size: Int = 97): List<CellData> = List(size) { idx -> CellData(index = idx, voltageDeviation = 0f) }

    fun fetchCellData(deviceNumber: String, tenNumRange: IntRange) {
        viewModelScope.launch {
            try {
                val cellDataList = mutableListOf<CellData>()
                for (tenNum in tenNumRange) {
                    val request = CellRequest(device_number = deviceNumber, ten_num = tenNum)
                    val response = RetrofitInstance.api.cellvoltage(request)
                    val voltageData = parseCellResponse(response)

                    cellDataList.addAll(voltageData)
                }
                val merged = cellDataList
                    .distinctBy { it.index }
                    .sortedBy { it.index }

                val finalList = run {
                    val byIndex = merged.associateBy { it.index }
                    (0 until 97).map { idx -> byIndex[idx] ?: CellData(index = idx, voltageDeviation = 0f) }
                }

                _cells.value = finalList
            } catch (e: Exception) {
                e.printStackTrace()
                _cells.value = defaultCells()
            }
        }
    }
}

fun parseCellResponse(response: Map<String, Any>): List<CellData> {
    val cellDataList = mutableListOf<CellData>()

    response.forEach { (key, value) ->
        if (key.startsWith("cell_") && value is Number) {
            val cellIndex = key.removePrefix("cell_").toIntOrNull() // "cell_00" -> 0
            cellIndex?.let {
                cellDataList.add(CellData(index = cellIndex, voltageDeviation = value.toFloat()))
            }
        }
    }
    return cellDataList

}