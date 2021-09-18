package dev.mijey.example.mediastore.gallery

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.View
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.mijey.example.mediastore.gallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true -> showImages()
            checkPermissionRationale() -> showPermissionRationaleView()
            else -> goToSettings()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val galleryAdapter = GalleryAdapter { image ->
            deleteImage(image)
        }

        binding.apply {
            activity = this@MainActivity

            gallery.also { view ->
                view.layoutManager = GridLayoutManager(this@MainActivity, 3)
                view.adapter = galleryAdapter
            }
        }

        viewModel.apply {
            images.observe(this@MainActivity) { images ->
                galleryAdapter.submitList(images)
            }

            permissionNeededForDelete.observe(this@MainActivity) { intentSender ->
                intentSender?.let {
                    try {
                        val deletePermissionLauncher = registerForActivityResult(
                            ActivityResultContracts.StartIntentSenderForResult()
                        ) { result ->
                            if (result.resultCode == Activity.RESULT_OK) {
                                viewModel.deletePendingImage()
                            }
                        }
                        val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()

                        deletePermissionLauncher.launch(intentSenderRequest)
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        if (checkPermission()) {
            showImages()
        } else {
            requestPermission()
        }
    }

    private fun showImages() {
        viewModel.loadImages()
        binding.permissionRationaleView.visibility = View.GONE
    }

    private fun deleteImage(image: MediaStoreImage) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_dialog_title)
            .setMessage(getString(R.string.delete_dialog_message, image.displayName))
            .setPositiveButton(R.string.delete_dialog_positive) { _: DialogInterface, _: Int ->
                viewModel.deleteImage(image)
            }
            .setNegativeButton(R.string.delete_dialog_negative) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Permission
     */

    fun requestPermission() {
        requestPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissionRationale(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private fun showPermissionRationaleView() {
        binding.permissionRationaleView.visibility = View.VISIBLE
    }

    private fun goToSettings() {
        Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent ->
            startActivity(intent)
        }
    }
}
