package com.example.barcode_a

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.addCallback

class TutorialPersonal : Fragment() {

    private lateinit var videoView: VideoView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_tutorial_personal, container, false)

        videoView = rootView.findViewById(R.id.videoView)

        val videoPath = "android.resource://${requireActivity().packageName}/${R.raw.personal}" // Replace "video" with your video file name (without extension)
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
            val fragment = Home()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()


        }
    }
}