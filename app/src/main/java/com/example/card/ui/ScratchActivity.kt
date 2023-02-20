package com.example.card.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.card.data.preferences.CardDataStoreImpl
import com.example.card.databinding.ActivityScratchBinding
import com.example.card.repository.CardRepositoryImpl
import com.example.card.utils.Status
import kotlinx.coroutines.launch

class ScratchActivity : AppCompatActivity() {

    companion object {
        fun getIntent(context: Context): Intent =
            Intent(context, ScratchActivity::class.java)
    }

    private lateinit var binding: ActivityScratchBinding
    private val viewModel: ScratchViewModel by viewModels {
        ScratchVMF(CardRepositoryImpl(CardDataStoreImpl()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScratchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.scratchButton.setOnClickListener { viewModel.generateCode() }
        initData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun initData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.code.collect {
                    when (it.status) {
                        Status.SUCCESS -> {
                            binding.codeView.text = it.data
                            binding.scratchButton.isEnabled = it.data.isNullOrBlank()
                            toggleLoading(false)
                        }
                        Status.ERROR -> {
                            binding.codeView.text = it.message
                            toggleLoading(false)
                        }
                        Status.LOADING -> toggleLoading(true)
                    }
                }
            }
        }
    }

    private fun toggleLoading(visible: Boolean) {
        binding.progressbar.isVisible = visible
    }
}