package com.example.barcode_a

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.addCallback

class TutorialManufacturer : Fragment() {

    private lateinit var videoView: VideoView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_tutorial_manufacturer, container, false)

        videoView = rootView.findViewById(R.id.videoView)

        val videoPath = "android.resource://${requireActivity().packageName}/${R.raw.manufacturer}" // Replace "video" with your video file name (without extension)
        val videoUri = Uri.parse(videoPath)

        videoView.setVideoURI(videoUri)

        // Add media controller to enable play, pause, etc.
        val mediaController = MediaController(requireContext())
        videoView.setMediaController(mediaController)
        mediaController.setAnchorView(videoView)

        // Start playing the video
        videoView.start()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Back Button Function
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val fragment = Home_Manufacturer()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout2, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}