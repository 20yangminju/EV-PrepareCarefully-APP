import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// 데이터 모델 (로그인 및 회원가입)
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val nickname: String, val email: String, val password: String)
data class TempResponse(val device_number: String, val module_number: Int,
                        val module_temp: Float, val created_at: String)

data class TempRequest(val device_number: String, val module_number: Int)

data class TempHistoryRequest(val device_number: String, val module_number: Int, val page: Int)
data class TempHistoryItem(val device_number: String, val module_number: Int, val module_temp: Float, val created_at: String)

data class CellRequest(val device_number: String, val ten_num: Int)
data class CellResponse(val cell_00: Float, val cell_01: Float, val cell_02: Float, val cell_03: Float, val cell_04: Float,
                        val cell_05: Float, val cell_06: Float, val cell_07: Float, val cell_08: Float, val cell_09: Float,)

data class CellHistoryRequest(val device_number: String, val cell_number: Int, val page: Int)
data class CellHistoryResponse(val device_number: String, val cell_number: Float, val created_at: String)

data class StatusRequest(val device_number: String)
data class StatusResponse(val charging_percent: Int, val charging: Int, val Hour: Int, val Minit: Int)

data class chargeRequest(val longitude: Float, val latitude: Float)
data class chargeResponse(val name: String, val address: String, val latitude: Float, val longitude: Float)



interface ApiService {
    @POST("cars/batteryTemp/app")
    suspend fun temperature(@Body tempRequest: TempRequest): TempResponse

    @POST("cars/batteryTemp/list/app")
    suspend fun tempHistory(@Body tempHistoryRequest: TempHistoryRequest): List<TempHistoryItem>

    @POST("cars/cellVoltage/app")
    suspend fun cellvoltage(@Body CellRequest: CellRequest): Map<String, Any>

    @POST("cars/cellVoltage/list/app")
    suspend fun cellHistory(@Body CellHistoryRequest: CellHistoryRequest): List<CellHistoryResponse>

    @POST("cars/batteryStatus/app")
    suspend fun status(@Body StatusRequest: StatusRequest): StatusResponse

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<Void>  // 서버로 로그인 요청

    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<Void>  // 회원가입 요청

    @POST("find-charger")
    suspend fun find_charger(@Body chargeRequest: chargeRequest): chargeResponse

    // TODO: 향후 추가 예정 - 충전소 안전 정보 API
    // 충전소 안전 등급, 최근 점검일, 실시간 이용 가능 충전기 수 등을 조회
    // @POST("charging-station/safety-info")
    // suspend fun getChargingStationSafetyInfo(@Body request: ChargingStationSafetyRequest): ChargingStationSafetyResponse

    // TODO: 향후 추가 예정 - 카카오 맵 API 연동
    // 카카오 맵 API를 통해 주변 충전소 목록 조회 후 자체 DB 정보와 결합
    // Retrofit 인스턴스를 별도로 생성하여 카카오 맵 API 호출
    // Base URL: https://dapi.kakao.com/
    // Endpoint: /v2/local/search/keyword.json?query=전기차충전소
}



