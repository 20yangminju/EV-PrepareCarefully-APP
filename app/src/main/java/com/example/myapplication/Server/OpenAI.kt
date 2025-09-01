import com.example.myapplication.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
import okhttp3.Interceptor

// OpenAI API 인터페이스 정의

interface OpenAIApi {
    @POST("v1/responses")
    suspend fun createResponse(@Body request: ResponsesRequest): ResponsesResponse
}


// Retrofit 인스턴스 생성
object RetrofitInstance {
    private val logging = HttpLoggingInterceptor().apply {
        // 배포 시 Level.NONE 권장
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 공통 헤더 인터셉터
    private val authInterceptor = Interceptor { chain ->
        val req = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .build()
        chain.proceed(req)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build()

    val api: OpenAIApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAIApi::class.java)
    }
}

data class PlacesResponse(
    val results: List<Place>
)

data class Place(
    val name: String,
    val vicinity: String
)

interface GooglePlacesApi {
    @GET("nearbysearch/json")
    fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String,
        @Query("key") apiKey: String
    ): Call<PlacesResponse>
}

val GpsRetrofit = Retrofit.Builder()
    .baseUrl("https://maps.googleapis.com/maps/api/place/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val gps = GpsRetrofit.create(GooglePlacesApi::class.java)