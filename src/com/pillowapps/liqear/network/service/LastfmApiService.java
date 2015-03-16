package com.pillowapps.liqear.network.service;

import com.pillowapps.liqear.entities.LastfmResponse;
import com.pillowapps.liqear.entities.lastfm.LastfmTag;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmAlbumRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmAlbumSearchResultsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmArtistRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmArtistSearchResultsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmArtistsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmFriendsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmLovedTracksRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmNeighboursRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmRecentTracksRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmRecommendationsArtistRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmSimilarArtistsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTagSearchResultsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTopAlbumsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTopArtistsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTopTracksRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTrackRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTracksRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmUserRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmWeeklyTrackChartRoot;
import com.pillowapps.liqear.network.callbacks.LastfmCallback;
import com.pillowapps.liqear.network.callbacks.VkCallback;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface LastfmApiService {

    @GET("/?method=user.getNeighbours")
    public void getNeighbours(@Query("user") String user,
                              @Query("limit") int limit,
                              Callback<LastfmNeighboursRoot> callback);

    @GET("/?method=user.getRecommendedArtists")
    public void getRecommendedArtists(@Query("limit") int limit,
                                      @Query("page") int page,
                                      @Query("api_sig") String apiSig,
                                      @Query("sk") String sessionKey,
                                      LastfmCallback<LastfmRecommendationsArtistRoot> callback);

    @GET("/?method=user.getTopArtists")
    public void getUserTopArtists(@Query("user") String user,
                                  @Query("period") String period,
                                  @Query("limit") int limit,
                                  @Query("page") int page,
                                  Callback<LastfmTopArtistsRoot> callback);

    @GET("/?method=artist.getSimilar")
    public void getSimilarArtists(@Query("artist") String artist,
                                  @Query("limit") int limit,
                                  @Query("page") int page,
                                  Callback<LastfmSimilarArtistsRoot> callback);

    @GET("/?method=artist.search")
    public void searchArtist(@Query("artist") String artist,
                             @Query("limit") int limit,
                             @Query("page") int page,
                             Callback<LastfmArtistSearchResultsRoot> callback);

    @GET("/?method=tag.search")
    public void searchTag(@Query("tag") String tag,
                          @Query("limit") int limit,
                          @Query("page") int page,
                          Callback<LastfmTagSearchResultsRoot> callback);

    @GET("/?method=album.search")
    public void searchAlbum(@Query("album") String album,
                            @Query("limit") int limit,
                            @Query("page") int page,
                            Callback<LastfmAlbumSearchResultsRoot> callback);

    @GET("/?method=chart.getTopArtists")
    public void getChartTopArtists(@Query("limit") int limit,
                                   @Query("page") int page,
                                   Callback<LastfmArtistsRoot> callback);

    @GET("/?method=chart.getHypedArtists")
    public void getChartHypedArtists(@Query("limit") int limit,
                                     @Query("page") int page,
                                     Callback<LastfmArtistsRoot> callback);

    @GET("/?method=user.getFriends")
    public void getFriends(@Query("user") String user,
                           @Query("limit") int limit,
                           @Query("page") int page,
                           Callback<LastfmFriendsRoot> callback);

    @GET("/?method=tag.getTopTracks")
    public void getTagTopTracks(@Query("tag") String tag,
                                @Query("limit") int limit,
                                @Query("page") int page,
                                Callback<LastfmTopTracksRoot> callback);

    @GET("/?method=artist.getTopTracks")
    public void getArtistTopTracks(@Query("artist") String artist,
                                   @Query("limit") int limit,
                                   @Query("page") int page,
                                   Callback<LastfmTopTracksRoot> callback);

    @GET("/?method=library.getTopTracks")
    public void getLibraryTracks(@Query("user") String user,
                                 @Query("limit") int limit,
                                 @Query("page") int page,
                                 Callback<List<LastfmTrack>> callback);

    @GET("/?method=library.getTracks")
    public void getPersonalArtistTopTracks(@Query("artist") String artist,
                                           @Query("user") String user,
                                           @Query("limit") int limit,
                                           @Query("page") int page,
                                           Callback<LastfmTracksRoot> callback);

    @GET("/?method=chart.getTopTracks")
    public void getChartTopTracks(@Query("limit") int limit,
                                  @Query("page") int page,
                                  Callback<LastfmTracksRoot> callback);

    @GET("/?method=chart.getLovedTracks")
    public void getChartLovedTracks(@Query("limit") int limit,
                                    @Query("page") int page,
                                    Callback<LastfmTracksRoot> callback);

    @GET("/?method=chart.getTopTags")
    public void getChartTopTags(@Query("limit") int limit,
                                @Query("page") int page,
                                Callback<List<LastfmTag>> callback);

    @GET("/?method=chart.getHypedTracks")
    public void getChartHypedTracks(@Query("limit") int limit,
                                    @Query("page") int page,
                                    Callback<LastfmTracksRoot> callback);

    @GET("/?method=user.getRecentTracks")
    public void getRecentTracks(@Query("user") String user,
                                @Query("limit") int limit,
                                @Query("page") int page,
                                Callback<LastfmRecentTracksRoot> callback);

    @GET("/?method=album.getInfo")
    public void getAlbumInfo(@Query("artist") String artist,
                             @Query("album") String album,
                             Callback<LastfmAlbumRoot> callback);

    @GET("/?method=artist.getInfo")
    public void getArtistInfo(@Query("artist") String artist,
                              @Query("user") String user,
                              @Query("lang") String language,
                              Callback<LastfmArtistRoot> callback);

    @GET("/?method=user.getInfo")
    public void getUserInfo(@Query("user") String user,
                            VkCallback<LastfmUserRoot> callback);

    @GET("/?method=track.getInfo")
    public void getTrackInfo(@Query("artist") String artist,
                             @Query("track") String track,
                             @Query("username") String username,
                             LastfmCallback<LastfmTrackRoot> callback);

    @GET("/?method=tag.getTopTags")
    public void getTopTags(Callback<LastfmTag> callback);

    @POST("/?method=track.love")
    public void love(@Query("artist") String artist,
                     @Query("track") String track,
                     @Query("api_sig") String apiSig,
                     @Query("sk") String sessionKey,
                     LastfmCallback<LastfmResponse> callback);

    @POST("/?method=track.unlove")
    public void unlove(@Query("artist") String artist,
                       @Query("track") String track,
                       @Query("api_sig") String apiSig,
                       @Query("sk") String sessionKey,
                       LastfmCallback<LastfmResponse> callback);

    @POST("/?method=track.updateNowPlaying")
    public void nowplaying(@Query("artist") String artist,
                           @Query("track") String track,
                           @Query("api_sig") String apiSig,
                           @Query("sk") String sessionKey,
                           LastfmCallback<LastfmResponse> callback);

    @POST("/?method=track.updateNowPlaying")
    public void nowplaying(@Query("artist") String artist,
                           @Query("track") String track,
                           @Query("album") String album,
                           @Query("api_sig") String apiSig,
                           @Query("sk") String sessionKey,
                           LastfmCallback<LastfmResponse> callback);

    @POST("/?method=track.scrobble")
    public void scrobble(@Query("artist") String artist,
                         @Query("track") String track,
                         @Query("album") String album,
                         @Query("timestamp") String timestamp,
                         @Query("api_sig") String apiSig,
                         @Query("sk") String sessionKey,
                         LastfmCallback<LastfmResponse> callback);

    @POST("/?method=track.scrobble")
    public Observable<LastfmResponse> scrobble(@Query("artist") String artist,
                                               @Query("track") String track,
                                               @Query("album") String album,
                                               @Query("timestamp") String timestamp,
                                               @Query("api_sig") String apiSig,
                                               @Query("sk") String sessionKey);

    @GET("/?method=artist.getTopAlbums")
    public void getArtistTopAlbums(@Query("artist") String artist,
                                   Callback<LastfmTopAlbumsRoot> callback);

    @GET("/?method=user.getTopTracks")
    void getUserTopTracks(@Query("user") String user,
                          @Query("period") String period,
                          @Query("limit") int limit,
                          @Query("page") int page,
                          Callback<LastfmTopTracksRoot> callback);

    @GET("/?method=user.getLovedTracks")
    void getLovedTracks(@Query("user") String user,
                        @Query("limit") int limit,
                        @Query("page") int page,
                        Callback<LastfmLovedTracksRoot> callback);

    @GET("/?method=user.getWeeklyTrackChart")
    public Observable<LastfmWeeklyTrackChartRoot> getWeeklyTracksChart(@Query("user") String user);

    @GET("/?method=user.getTopTracks")
    public Observable<LastfmTopTracksRoot> getUserTopTracks(@Query("user") String user,
                                                            @Query("period") String period,
                                                            @Query("limit") int limit,
                                                            @Query("page") int page);

    @GET("/?method=user.getLovedTracks")
    public Observable<LastfmLovedTracksRoot> getLovedTracks(@Query("user") String user,
                                                            @Query("limit") int limit,
                                                            @Query("page") int page);

    @GET("/?method=album.getInfo")
    public Observable<LastfmAlbumRoot> getAlbumInfo(@Query("artist") String artist,
                                                    @Query("album") String album);

    @GET("/?method=artist.getTopTracks")
    public Observable<LastfmTopTracksRoot> getArtistTopTracks(@Query("artist") String artist,
                                                              @Query("limit") int limit,
                                                              @Query("page") int page);
}
