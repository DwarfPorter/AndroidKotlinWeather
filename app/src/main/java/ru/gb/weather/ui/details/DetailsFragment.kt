package ru.gb.weather.ui.details

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import ru.gb.weather.BuildConfig
import ru.gb.weather.R
import ru.gb.weather.databinding.FragmentDetailsBinding
import ru.gb.weather.model.City
import ru.gb.weather.model.Weather
import ru.gb.weather.model.data.FactDTO
import ru.gb.weather.model.data.WeatherDTO
import ru.gb.weather.model.data.mapTo
import ru.gb.weather.service.DetailsService
import ru.gb.weather.service.LATITUDE_EXTRA
import ru.gb.weather.service.LONGITUDE_EXTRA
import ru.gb.weather.ui.main.MainViewModel
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

const val DETAILS_INTENT_FILTER = "DETAILS INTENT FILTER"
const val DETAILS_LOAD_RESULT_EXTRA = "LOAD RESULT"
const val DETAILS_INTENT_EMPTY_EXTRA = "INTENT IS EMPTY"
const val DETAILS_DATA_EMPTY_EXTRA = "DATA IS EMPTY"
const val DETAILS_RESPONSE_EMPTY_EXTRA = "RESPONSE IS EMPTY"
const val DETAILS_REQUEST_ERROR_EXTRA = "REQUEST ERROR"
const val DETAILS_REQUEST_ERROR_MESSAGE_EXTRA = "REQUEST ERROR MESSAGE"
const val DETAILS_URL_MALFORMED_EXTRA = "URL MALFORMED"
const val DETAILS_RESPONSE_SUCCESS_EXTRA = "RESPONSE SUCCESS"
const val DETAILS_TEMP_EXTRA = "TEMPERATURE"
const val DETAILS_FEELS_LIKE_EXTRA = "FEELS LIKE"
const val DETAILS_CONDITION_EXTRA = "CONDITION"
private const val TEMP_INVALID = -100
private const val FEELS_LIKE_INVALID = -100
private const val PROCESS_ERROR = "Обработка ошибки"

/*
file apikey.properties
yandex_weather_api_key = "..."
*/

class DetailsFragment : Fragment() {


    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var city: City

    private val loadResultsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra(DETAILS_LOAD_RESULT_EXTRA)) {
                DETAILS_INTENT_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_DATA_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_RESPONSE_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_REQUEST_ERROR_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_REQUEST_ERROR_MESSAGE_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_URL_MALFORMED_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_RESPONSE_SUCCESS_EXTRA -> renderData(
                    WeatherDTO(
                        FactDTO(
                            intent.getIntExtra(
                                DETAILS_TEMP_EXTRA, TEMP_INVALID
                            ),
                            intent.getIntExtra(DETAILS_FEELS_LIKE_EXTRA, FEELS_LIKE_INVALID),
                            intent.getStringExtra(
                                DETAILS_CONDITION_EXTRA
                            )
                        )
                    )
                )
                else -> TODO(PROCESS_ERROR)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        requireActivity().let {
            LocalBroadcastManager.getInstance(it)
                .registerReceiver(loadResultsReceiver, IntentFilter(DETAILS_INTENT_FILTER))
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val weather = arguments?.getParcelable(BUNDLE_EXTRA) ?: Weather()
        city = weather.city
        getWeather()
    }

    private fun getWeather() {
        binding.mainView.hide()
        binding.loadingLayout.show()
        context?.let {
            it.startService(Intent(it, DetailsService::class.java).apply {
                putExtra(
                    LATITUDE_EXTRA,
                    city.lat
                )
                putExtra(
                    LONGITUDE_EXTRA,
                    city.lon
                )
            })
        }
    }

    private fun renderData(weatherDTO: WeatherDTO) {
        binding.mainView.show()
        binding.loadingLayout.hide()

        val fact = weatherDTO.fact
        val temp = fact!!.temp
        val feelsLike = fact.feels_like
        val condition = fact.condition
        if (temp == TEMP_INVALID || feelsLike == FEELS_LIKE_INVALID || condition == null) {
            TODO(PROCESS_ERROR)
        } else {
            binding.cityName.text = city.city
            binding.cityCoordinates.text = String.format(
                getString(R.string.city_coordinates),
                city.lat.toString(),
                city.lon.toString()
            )
            binding.temperatureValue.text = temp.toString()
            binding.feelsLikeValue.text = feelsLike.toString()
            binding.weatherCondition.text = condition
        }
    }

    override fun onDestroyView() {
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(loadResultsReceiver)
        }
        super.onDestroyView()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val BUNDLE_EXTRA = "weather"

        fun newInstance(weather: Weather): DetailsFragment {
            val fragment = DetailsFragment()
            val bundle = Bundle().also {
                it.putParcelable(BUNDLE_EXTRA, weather)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    fun View.show(){
        this.visibility = View.VISIBLE
    }

    fun View.hide(){
        this.visibility = View.GONE;
    }
}
