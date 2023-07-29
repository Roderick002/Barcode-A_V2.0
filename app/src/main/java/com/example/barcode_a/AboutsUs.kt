package com.example.barcode_a

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.addCallback

class AboutsUs : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_abouts_us, container, false)

        val tv_email1 = view.findViewById<TextView>(R.id.tv_email1)
        val tv_email2 = view.findViewById<TextView>(R.id.tv_email2)
        val tv_email3 = view.findViewById<TextView>(R.id.tv_email3)
        val tv_email4 = view.findViewById<TextView>(R.id.tv_email4)

        val recipientEmails = listOf("gordoraxyra@gmail.com", "ballaranroderick@gmail.com", "louisasantosl12@gmail.com", "johnlawrence.e.trinidad@gmail.com")
        val subject = "Regarding Barcode-A App"

        val emailClickListener = View.OnClickListener { v ->
            val emailIndex = v.tag as Int
            val recipientEmail = recipientEmails[emailIndex]
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$recipientEmail")
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }
            startActivity(Intent.createChooser(emailIntent, "Send email"))
        }

        tv_email1.tag = 0
        tv_email2.tag = 1
        tv_email3.tag = 2
        tv_email4.tag = 3

        tv_email1.setOnClickListener(emailClickListener)
        tv_email2.setOnClickListener(emailClickListener)
        tv_email3.setOnClickListener(emailClickListener)
        tv_email4.setOnClickListener(emailClickListener)
        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rateUsButton = view.findViewById<TextView>(R.id.tv_rateus)
        rateUsButton.setOnClickListener {
            showRateUsDialog()
        }
        //Back Button Function
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val fragment = Home()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun showRateUsDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.rate_us_dialog, null)
        val imageViewRating = dialogView.findViewById<ImageView>(R.id.iv_ratingImage)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingbar)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set initial image based on rating
        updateRatingImage(imageViewRating, ratingBar.rating)

        // Add animation for rating change
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            updateRatingImageWithAnimation(imageViewRating, rating)
        }

        dialog.show()
    }

    private fun updateRatingImage(imageView: ImageView, rating: Float) {
        val drawableRes = when (rating.toInt()) {
            1 -> R.drawable.one_star
            2 -> R.drawable.two_star
            3 -> R.drawable.three_star
            4 -> R.drawable.four_star
            else -> R.drawable.five_star
        }
        imageView.setImageResource(drawableRes)
    }

    private fun updateRatingImageWithAnimation(imageView: ImageView, newRating: Float) {
        val drawableRes = when (newRating.toInt()) {
            1 -> R.drawable.one_star
            2 -> R.drawable.two_star
            3 -> R.drawable.three_star
            4 -> R.drawable.four_star
            else -> R.drawable.five_star
        }

        imageView.animate()
            .alpha(0f)
            .setDuration(150) // Adjust the duration as needed
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                imageView.setImageResource(drawableRes)
                imageView.animate()
                    .alpha(1f)
                    .setDuration(150) // Adjust the duration as needed
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }
            .start()
    }

}