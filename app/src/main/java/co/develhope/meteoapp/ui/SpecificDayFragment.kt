package co.develhope.meteoapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import co.develhope.meteoapp.data.domainmodel.HourlyForecast
import co.develhope.meteoapp.data.domainmodel.Place
import co.develhope.meteoapp.databinding.FragmentSpecificDayBinding
import co.develhope.meteoapp.network.NetworkProvider
import co.develhope.meteoapp.ui.adapter.SpecificDayAdapter
import co.develhope.meteoapp.ui.adapter.SpecificDayModel
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime


class SpecificDayFragment : Fragment() {

    private var _binding: FragmentSpecificDayBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpecificDayBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.itemSpecificday.layoutManager = LinearLayoutManager(view.context)

    }

    override fun onStart() {
        super.onStart()
        getHourlyForecast()
    }

    private fun getHourlyForecast() {
        lifecycleScope.launch {
            try {
                val hourlyForecast = NetworkProvider().getDailySummary(
                    getPlace().lat,
                    getPlace().log,
                    getDate().toLocalDateTime().toString(),
                    getDate().toLocalDateTime().toString()
                )

                Log.d("prova card specific day", "${hourlyForecast}")
                val specificDayItems: List<SpecificDayModel> = createListToShow(hourlyForecast)

                val adapter = SpecificDayAdapter(specificDayItems)
                binding.itemSpecificday.adapter = adapter

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("provadaily", "errore")
                Log.d("provadaily", "${OffsetDateTime.now().toLocalDate().toString()}")

            }
        }
    }

    private fun getPlace() : Place = Place(
        city = "Palermo",
        region = "Sicilia",
        lat = 38.116667,
        log = 13.366667
    )

    private fun getDate() : OffsetDateTime = OffsetDateTime.now()

    private fun createListToShow(list: List<HourlyForecast>): List<SpecificDayModel> {
        val listToReturn = mutableListOf<SpecificDayModel>()

        listToReturn.add(SpecificDayModel.SpecificDayTitle(place = getPlace(), date = getDate()))
        listToReturn.add(SpecificDayModel.SpecificDayHourly(list.first()))
        listToReturn.add(SpecificDayModel.SpecificDayCard(list.first().cardSpecificDay))

        val otherHours: MutableList<SpecificDayModel.SpecificDayHourly> = list.map {
            SpecificDayModel.SpecificDayHourly(it)
        }.toMutableList()
        listToReturn.addAll(otherHours)

        listToReturn.removeAt(3)
        return listToReturn
    }
}



