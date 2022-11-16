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
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import okhttp3.*
import ru.gb.weather.AppState
import ru.gb.weather.BuildConfig
import ru.gb.weather.R
import ru.gb.weather.databinding.FragmentDetailsBinding
import ru.gb.weather.model.City
import ru.gb.weather.model.Weather
import ru.gb.weather.model.data.FactDTO
import ru.gb.weather.model.data.WeatherDTO
import ru.gb.weather.model.data.mapTo
import ru.gb.weather.ui.main.MainViewModel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

/*
file apikey.properties
yandex_weather_api_key = "..."
*/

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var city: City

    private val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this).get(DetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val weather = arguments?.getParcelable(BUNDLE_EXTRA) ?: Weather()
        city = weather.city;
        viewModel.getLiveData().observe(viewLifecycleOwner, { renderData(it) })
        viewModel.getWeatherFromRemoteSource("https://api.weather.yandex.ru/v2/informers?lat=${city.lat}&lon=${city.lon}")
    }

    private fun renderData(appState: AppState) {
        binding.mainView.show()
        binding.loadingLayout.hide()
        when (appState) {
            is AppState.Success -> {
                binding.mainView.show()
                binding.loadingLayout.hide()
                setWeather(appState.weatherData[0])
            }
            is AppState.Loading -> {
                binding.mainView.hide()
                binding.loadingLayout.show()
            }
            is AppState.Error -> {
                binding.mainView.show()
                binding.loadingLayout.hide()
                binding.mainView.showSnackBar(
                    getString(R.string.error),
                    getString(R.string.reload),
                    { viewModel.getWeatherFromRemoteSource("https://api.weather.yandex.ru/v2/informers?lat=${city.lat}&lon=${city.lon}") })
            }
        }
    }

    private fun setWeather(weather: Weather) {
        with(binding) {
            city.let { city ->
                cityName.text = city.city
                cityCoordinates.text = String.format(
                    getString(R.string.city_coordinates),
                    city.lat.toString(),
                    city.lon.toString()
                )
            }

            temperatureValue.text = weather.temperature.toString()
            feelsLikeValue.text = weather.feelsLike.toString()
            weatherCondition.text = weather.condition
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
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

    private fun View.show() {
        this.visibility = View.VISIBLE
    }

    private fun View.hide() {
        this.visibility = View.GONE;
    }

    fun View.showSnackBar(
        text: String,
        actionText: String,
        action: (View) -> Unit,
        length: Int = Snackbar.LENGTH_INDEFINITE
    ) {
        Snackbar.make(this, text, length).setAction(actionText, action).show()
    }
}


