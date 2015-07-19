package com.pillowapps.liqear.helpers;

import com.pillowapps.liqear.entities.Playlist;

public class PlaylistUtils {
    public static int sizeOf(Playlist playlist) {
        if (playlist == null || playlist.getTracks() == null) return 0;
        return playlist.getTracks().size();
    }
}