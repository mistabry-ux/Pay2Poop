package com.pay2poo.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.pay2poo.app.databinding.FragmentMetricsBinding

class MetricsFragment : Fragment() {

    private var _binding: FragmentMetricsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MetricsViewModel by viewModels { MetricsViewModel.Factory(requireActivity().application) }
    private lateinit var sessionAdapter: SessionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMetricsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionAdapter = SessionAdapter { session ->
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Session")
                .setMessage("Remove this session from records?")
                .setPositiveButton("Delete") { _, _ -> viewModel.deleteSession(session) }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.rvRecentSessions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sessionAdapter
        }

        viewModel.totalSessions.observe(viewLifecycleOwner) { count ->
            binding.tvTotalSessions.text = count.toString()
        }

        viewModel.totalEarnings.observe(viewLifecycleOwner) { earned ->
            binding.tvTotalEarnings.text = getString(R.string.earnings_fmt, earned)
        }

        viewModel.totalDuration.observe(viewLifecycleOwner) { mins ->
            binding.tvTotalTime.text = formatDuration(mins)
        }

        viewModel.avgDuration.observe(viewLifecycleOwner) { mins ->
            binding.tvAvgDuration.text = formatDuration(mins)
        }

        viewModel.weekSessions.observe(viewLifecycleOwner) { count ->
            binding.tvWeekSessions.text = count.toString()
        }

        viewModel.weekEarnings.observe(viewLifecycleOwner) { earned ->
            binding.tvWeekEarnings.text = getString(R.string.earnings_fmt, earned)
        }

        viewModel.recentSessions.observe(viewLifecycleOwner) { sessions ->
            sessionAdapter.submitList(sessions)
            binding.tvNoSessions.visibility = if (sessions.isEmpty()) View.VISIBLE else View.GONE
            binding.rvRecentSessions.visibility = if (sessions.isEmpty()) View.GONE else View.VISIBLE
        }

        binding.btnResetProfile.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Reset Profile")
                .setMessage("This will delete your profile and all session data. Are you sure?")
                .setPositiveButton("Reset") { _, _ ->
                    viewModel.resetAll()
                    startActivity(Intent(requireContext(), SetupActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun formatDuration(mins: Double?): String {
        if (mins == null || mins <= 0.0) return "0m"
        val totalMins = mins.toLong()
        val hours = totalMins / 60
        val remaining = totalMins % 60
        return when {
            hours > 0 -> "${hours}h ${remaining}m"
            else -> "${remaining}m"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
