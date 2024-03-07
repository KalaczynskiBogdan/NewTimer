package com.example.newtimer.home

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.example.newtimer.databinding.FragmentTimerBinding
import com.example.newtimer.services.ForegroundService

class TimerFragment : Fragment() {
    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermission.launch(POST_NOTIFICATIONS)

        initObservers()

        buttonsClicks()

    }

    private fun buttonsClicks() {
        binding.btnStart.setOnClickListener {
            ForegroundService.startService(requireContext())
        }
        binding.btnStop.setOnClickListener {
            ForegroundService.stopService(requireContext())
        }
    }

    private fun initObservers() {
        ForegroundService.getTime().observe(viewLifecycleOwner) {
            binding.tvText.text = String.format("%s:%02d", "00", it)
            binding.progressBar.progress = it.toInt()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): TimerFragment = TimerFragment()
    }
}