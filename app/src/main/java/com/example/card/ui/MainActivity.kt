package com.example.card.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.card.R
import com.example.card.data.preferences.CardDataStoreImpl
import com.example.card.databinding.ActivityMainBinding
import com.example.card.repository.CardRepositoryImpl
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        MainVMF(CardRepositoryImpl(CardDataStoreImpl()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.scratchButton.setOnClickListener {
            startActivity(ScratchActivity.getIntent(this))
        }
        binding.activateButton.setOnClickListener {
            startActivity(ActivateActivity.getIntent(this))
        }
        initData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.reset -> {
                viewModel.resetCard()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun initData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    binding.stateView.setText(it.label)
                }
            }
        }
    }
}