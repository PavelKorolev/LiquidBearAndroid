package com.pillowapps.liqear.models.lastfm;

import android.content.Context;

import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.callbacks.CompletionCallback;
import com.pillowapps.liqear.callbacks.LastfmCallback;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmAlbumRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmAlbumSearchResultsRoot;
import com.pillowapps.liqear.models.ImageModel;
import com.pillowapps.liqear.network.service.LastfmApiService;

import java.util.List;

import rx.Observable;

public class LastfmAlbumModel {
    private LastfmApiService lastfmService;

    public LastfmAlbumModel(LastfmApiService api) {
        this.lastfmService = api;
    }

    public void getAlbumInfo(Album album, final SimpleCallback<LastfmAlbum> callback) {
        lastfmService.getAlbumInfo(
                album.getArtist(),
                album.getTitle(),
                new LastfmCallback<LastfmAlbumRoot>() {
                    @Override
                    public void success(LastfmAlbumRoot lastfmAlbumRoot) {
                        callback.success(lastfmAlbumRoot.getAlbum());
                    }

                    @Override
                    public void failure(String error) {
                        callback.failure(error);
                    }
                }
        );
    }

    public Observable<LastfmAlbumRoot> getAlbumInfo(Album album) {
        return lastfmService.getAlbumInfo(
                album.getArtist(),
                album.getTitle()
        );
    }

    public void searchAlbum(String query, int limit, int page,
                            final SimpleCallback<List<LastfmAlbum>> callback) {
        lastfmService.searchAlbum(query, limit, page, new LastfmCallback<LastfmAlbumSearchResultsRoot>() {
            @Override
            public void success(LastfmAlbumSearchResultsRoot root) {
                callback.success(root.getResults().getAlbums().getAlbums());
            }

            @Override
            public void failure(String error) {
                callback.failure(error);
            }
        });
    }

    public void getCover(Context context, final Album album, final CompletionCallback callback) {
        if (album == null) {
            Timeline.getInstance().setAlbumCoverBitmap(null);
            callback.onCompleted();
            return;
        }
        new ImageModel().loadImage(context, album.getImageUrl(), bitmap -> {
            Timeline.getInstance().setAlbumCoverBitmap(bitmap);
            callback.onCompleted();
        });
    }

}
