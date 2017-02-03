package com.devfestmn.api;

import android.annotation.SuppressLint;

import com.devfestmn.api.model.Speaker;

/**
 * "API" to get image URLs for speakers
 *
 * @author bherbst
 */
public class ProfilePictureApi {
    private static final String IMAGE_URL_FORMAT = "https://devfest.fluin.io/speakers/%s/%dx%d";

    /**
     * Get the image URL for a speaker
     * @param speaker The speaker
     * @param imageWidth The desired image width, in pixels
     */
    @SuppressLint("DefaultLocale")
    public static String getImageUrl(Speaker speaker, int imageWidth) {
        // Note we are assuming that all images wil support roughly a 1:4 aspect ratio. This
        // API will find the closest possible thing.
        return String.format(IMAGE_URL_FORMAT, speaker.getId(), imageWidth, imageWidth * 4);
    }
}
