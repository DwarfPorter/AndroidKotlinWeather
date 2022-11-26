package ru.gb.weather.ui.main

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.gb.weather.AppState
import ru.gb.weather.R
import ru.gb.weather.databinding.FragmentMainBinding
import ru.gb.weather.model.Weather
import ru.gb.weather.ui.details.DetailsFragment
import ru.gb.weather.utils.hide
import ru.gb.weather.utils.show
import ru.gb.weather.utils.showSnackBar

private const val IS_RUSSIAN_KEY = "LIST_OF_RUSSIAN_KEY"

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private var isDataSetRus: Boolean = true
    private val adapter = MainFragmentAdapter(object : MainFragmentAdapter.OnItemViewClickListener {
        override fun onItemViewClick(weather: Weather) {
            activity?.supportFragmentManager?.let { manager ->
                manager.beginTransaction()
                    .add(R.id.container, DetailsFragment.newInstance(weather))
                    .addToBackStack("")
                    .commitAllowingStateLoss()
            }
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            binding.mainFragmentRecyclerView.adapter = adapter
            binding.mainFragmentFAB.setOnClickListener {
                changeWeatherDataSet()
                saveListOfTowns()
            }
        }
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        showListOfTowns();

        val observer = Observer<AppState> { a ->
            renderData(a)
        }

        viewModel.getLiveData().observe(viewLifecycleOwner, observer)
    }

    private fun showListOfTowns() {
        activity?.let {
            isDataSetRus = it.getPreferences(Context.MODE_PRIVATE).getBoolean(IS_RUSSIAN_KEY, true)
        }
        showWeatherDataSet()
    }

    private fun saveListOfTowns() {
        activity?.let {
            with(it.getPreferences(Context.MODE_PRIVATE).edit()) {
                putBoolean(IS_RUSSIAN_KEY, isDataSetRus)
                apply()
            }
        }
    }

    private fun changeWeatherDataSet() {
        isDataSetRus = !isDataSetRus
        showWeatherDataSet()
    }

    private fun showWeatherDataSet() {
        if (isDataSetRus) {
            viewModel.getWeatherFromLocalSourceRus()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        } else {
            viewModel.getWeatherFromLocalSourceWorld()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        }
    }

    override fun onDestroy() {
        adapter.removeListener()
        super.onDestroy()
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                binding.mainFragmentLoadingLayout.hide()
                adapter.setWeather(appState.weatherData)
            }
            is AppState.Loading -> {
                binding.mainFragmentLoadingLayout.show()
            }
            is AppState.Error -> {
                binding.mainFragmentLoadingLayout.hide()
                binding.root.showSnackBar(
                    getString(R.string.error),
                    getString(R.string.reload),
                    Snackbar.LENGTH_INDEFINITE
                ) { viewModel.getWeatherFromLocalSourceRus() }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter.removeListener()
    }

    companion object {
        fun newInstance() =
            MainFragment()
    }
}