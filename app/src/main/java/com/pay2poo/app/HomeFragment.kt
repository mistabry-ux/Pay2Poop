package com.pay2poo.app

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pay2poo.app.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory(requireActivity().application) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                binding.tvHourlyRate.text = getString(R.string.hourly_rate_fmt, it.hourlyRate)
                binding.tvPerMinute.text = getString(R.string.per_minute_fmt, it.perMinuteRate)
                binding.tvOccupation.text = it.occupation
            }
        }

        viewModel.isSessionActive.observe(viewLifecycleOwner) { active ->
            if (active) {
                binding.btnToggleSession.text = getString(R.string.stop_session)
                binding.btnToggleSession.setIconResource(R.drawable.ic_stop)
                binding.chronometer.visibility = View.VISIBLE
                binding.tvSessionLabel.text = getString(R.string.session_active)
                binding.tvSessionLabel.visibility = View.VISIBLE
                binding.cardEarning.visibility = View.VISIBLE
                val pulse = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse)
                binding.btnToggleSession.startAnimation(pulse)
            } else {
                binding.btnToggleSession.text = getString(R.string.start_session)
                binding.btnToggleSession.setIconResource(R.drawable.ic_toilet)
                binding.chronometer.visibility = View.GONE
                binding.tvSessionLabel.visibility = View.GONE
                binding.btnToggleSession.clearAnimation()
            }
        }

        viewModel.sessionStartTime.observe(viewLifecycleOwner) { startTime ->
            if (startTime > 0) {
                val elapsed = System.currentTimeMillis() - startTime
                binding.chronometer.base = SystemClock.elapsedRealtime() - elapsed
                binding.chronometer.start()
            } else {
                binding.chronometer.stop()
            }
        }

        viewModel.liveEarnings.observe(viewLifecycleOwner) { earnings ->
            binding.tvLiveEarnings.text = getString(R.string.earnings_fmt, earnings)
        }

        viewModel.todaySessionCount.observe(viewLifecycleOwner) { count ->
            binding.tvTodayCount.text = resources.getQuantityString(R.plurals.sessions_today, count, count)
        }

        viewModel.todayEarnings.observe(viewLifecycleOwner) { earned ->
            binding.tvTodayEarnings.text = getString(R.string.earnings_fmt, earned)
        }

        binding.btnToggleSession.setOnClickListener {
            viewModel.toggleSession()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
