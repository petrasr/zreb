package com.example.card.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.card.App
import com.example.card.data.preferences.CardDataStoreImpl
import com.example.card.data.workers.ActivateWorker
import com.example.card.databinding.ActivityActivateBinding
import com.example.card.repository.CardRepositoryImpl
import com.example.card.utils.PermissionUtil
import kotlinx.coroutines.launch

class ActivateActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 0x20

        fun getIntent(context: Context): Intent =
            Intent(context, ActivateActivity::class.java)
    }

    private lateinit var binding: ActivityActivateBinding
    private val viewModel: ActivateViewModel by viewModels {
        ActivateVMF(CardRepositoryImpl(CardDataStoreImpl()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityActivateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.activateButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (PermissionUtil.isPermissionGranted(this)) {
                    viewModel.activateCard()
                } else {
                    requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE)
                }
            } else {
                viewModel.activateCard()
            }

        }
        initData()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED)) {
                    viewModel.activateCard()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
                viewModel.buttonEnable.collect { enable ->
                    binding.activateButton.isEnabled = enable
                }
            }
        }
        WorkManager
            .getInstance(App.instance)
            .getWorkInfosByTagLiveData(ActivateWorker.WORKER_TAG)
            .observe(this) {
                if (it.isNotEmpty()) {
                    when (it.first().state) {
                        WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING ->
                            binding.progressbar.isVisible = true
                        else -> binding.progressbar.isVisible = false
                    }
                }
            }
    }


}