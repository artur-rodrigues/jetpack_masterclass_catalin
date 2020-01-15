package com.example.dogs.view.fragments


import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.dogs.R
import com.example.dogs.databinding.FragmentDetailBinding
import com.example.dogs.databinding.SendSmsDialogBinding
import com.example.dogs.model.SmsInfo
import com.example.dogs.model.entities.DogBreed
import com.example.dogs.model.entities.DogPalette
import com.example.dogs.utils.loadImage
import com.example.dogs.view.activities.MainActivity
import com.example.dogs.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.fragment_detail.*

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {

    private var dogUuid = 0
    private lateinit var viewModel: DetailViewModel
    private lateinit var binding: FragmentDetailBinding
    private var sendSmsStarted = false
    private var currentDog: DogBreed? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.fragment_detail,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid
        }

        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)

        setObservers()

        viewModel.fetch(dogUuid)
    }

    private fun setObservers() {
        viewModel.dogLiveData.observe(this, Observer {dog ->
            currentDog = dog
            dog.run {
                binding.dog = this
                imageUrl?.let {
                    setupBackgroundColor(it)
                }
            }

            /*with(it) {
                dogName.text = dogBreed
                dogPurpose.text = bredFor
                dogTemperament.text = temperament
                dogLifespan.text = lifeSpan
                dogImage.loadImage(imageUrl!!)
            }*/
        })
    }

    private fun setupBackgroundColor(url: String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object: CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource)
                        .generate {palette ->
                            val intColor = palette?.vibrantSwatch?.rgb ?: 0
                            val myPalette = DogPalette(intColor)
                            binding.palette = myPalette
                        }
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_send_sms -> {
                sendSmsStarted = true
                (activity as MainActivity).checkSmsPermission()
            }

            R.id.action_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this dog breed")
                intent.putExtra(Intent.EXTRA_TEXT, "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}")
                intent.putExtra(Intent.EXTRA_STREAM, currentDog?.imageUrl)
                startActivity(Intent.createChooser(intent, "Share with"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onPermissionResult(permissionGranted: Boolean) {
        if (sendSmsStarted && permissionGranted) {
            context.run {
                val smsInfo = SmsInfo("",
                    "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}",
                    currentDog?.imageUrl ?: "")

                val dialoBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(
                    LayoutInflater.from(this),
                    R.layout.send_sms_dialog,
                    null,
                    false
                )

                dialoBinding.smsInfo = smsInfo

                AlertDialog.Builder(this!!)
                    .setView(dialoBinding.root)
                    .setPositiveButton(getString(R.string.btn_send_sms)) {_, _ ->
                        if(!dialoBinding.smsDestination.text.isNullOrEmpty()) {
                            smsInfo.to = dialoBinding.smsDestination.text.toString()
                            sendSms(smsInfo)
                        }
                    }.setNegativeButton(getString(R.string.btn_cancel)) {_, _ ->}
                    .show()
            }
        }
    }

    private fun sendSms(smsInfo: SmsInfo) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(smsInfo.to, null, smsInfo.text, pendingIntent, null)
    }
}