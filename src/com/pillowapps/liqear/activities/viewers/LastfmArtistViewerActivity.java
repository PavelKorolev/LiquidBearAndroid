package com.pillowapps.liqear.activities.viewers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.costum.android.widget.LoadMoreListView;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.MainActivity;
import com.pillowapps.liqear.adapters.ViewerViewAdapter;
import com.pillowapps.liqear.components.PagerResultActivity;
import com.pillowapps.liqear.components.TextViewerPage;
import com.pillowapps.liqear.components.viewers.LastfmAlbumViewerPage;
import com.pillowapps.liqear.components.viewers.LastfmArtistViewerPage;
import com.pillowapps.liqear.components.viewers.LastfmTracksViewerPage;
import com.pillowapps.liqear.components.viewers.ViewerPage;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.ErrorNotifier;
import com.pillowapps.liqear.models.lastfm.LastfmArtistModel;
import com.pillowapps.liqear.models.lastfm.LastfmDiscographyModel;
import com.pillowapps.liqear.network.callbacks.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LastfmArtistViewerActivity extends PagerResultActivity {
    public static final String ARTIST = "artist";
    public static final int PERSONAL_TOP_INDEX = 2;
    public static final int TOP_TRACKS_INDEX = 1;
    public static final int ALBUMS_INDEX = 0;
    public static final int PAGES_NUMBER = 5;
    public int ARTIST_INFO_INDEX = AuthorizationInfoManager.isAuthorizedOnLastfm() ? 4 : 3;
    public int SIMILAR_INDEX = AuthorizationInfoManager.isAuthorizedOnLastfm() ? 3 : 2;
    private Artist artist;
    private boolean infoLoaded = false;
    private LastfmArtistModel artistModel = new LastfmArtistModel();

    private View infoTab;
    @InjectView(R.id.progress_bar_scrallable_text_layout)
    protected ProgressBar artistInfoProgressBar;
    @InjectView(R.id.text_view_scrollable_text_layout)
    protected TextView artistInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_layout);
        Bundle extras = getIntent().getExtras();
        artist = new Artist(extras.getString(ARTIST));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(artist.getName());
        initUi();
    }

    private void initUi() {
        initViewPager();
        ButterKnife.inject(this, infoTab);
        artistInfoTextView.setMovementMethod(LinkMovementMethod.getInstance());
        int defaultIndex = TOP_TRACKS_INDEX;
        changeViewPagerItem(defaultIndex);
    }

    private void initViewPager() {
        List<ViewerPage> pages = new ArrayList<>(PAGES_NUMBER);
        List<View> views = new ArrayList<>(PAGES_NUMBER);
        List<String> titles = new ArrayList<>(PAGES_NUMBER);
        ViewerPage albumsPage = createAlbumsPage();
        views.add(albumsPage.getView());
        titles.add(albumsPage.getTitle());
        pages.add(albumsPage);
        ViewerPage topTracksPage = createTopTracksPage();
        views.add(topTracksPage.getView());
        titles.add(topTracksPage.getTitle());
        pages.add(topTracksPage);
        if (AuthorizationInfoManager.isAuthorizedOnLastfm()) {
            ViewerPage personalTopTracksPage = createPersonalTopTracksPage();
            views.add(personalTopTracksPage.getView());
            titles.add(personalTopTracksPage.getTitle());
            pages.add(personalTopTracksPage);
        }
        ViewerPage similarArtistsPage = createSimilarArtistsPage();
        views.add(similarArtistsPage.getView());
        titles.add(similarArtistsPage.getTitle());
        pages.add(similarArtistsPage);
        TextViewerPage bioPage = createBioPage();
        infoTab = bioPage.getView();
        views.add(infoTab);
        titles.add(bioPage.getTitle());
        setViewers(pages);
        final ViewerViewAdapter adapter = new ViewerViewAdapter(views, titles);

        injectViewPager(adapter);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int index) {
                invalidateOptionsMenu();
                if (index == ARTIST_INFO_INDEX) {
                    if (!infoLoaded) {
                        getArtistInfo(artist.getName(), AuthorizationInfoManager.getLastfmName());
                    }
                } else {
                    ViewerPage viewer = getViewer(index);
                    if (viewer.isNotLoaded()) {
                        viewer.showProgressBar(true);
                        viewer.onLoadMore();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    private TextViewerPage createBioPage() {
        return new TextViewerPage(this,
                View.inflate(this, R.layout.scrollable_text_layout, null),
                R.string.artist_info);
    }

    private ViewerPage createSimilarArtistsPage() {
        final LastfmArtistViewerPage viewer = new LastfmArtistViewerPage(this,
                View.inflate(this, R.layout.list_tab, null),
                R.string.similar_artists);
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getSimilarArtists(artist.getName(), getPageSize(), viewer.getPage(), viewer);
            }
        });
        viewer.setItemClickListener(artistClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createPersonalTopTracksPage() {
        final LastfmTracksViewerPage viewer = new LastfmTracksViewerPage(this,
                View.inflate(this, R.layout.list_tab, null),
                String.format("%s %s", getString(R.string.top).toLowerCase(), AuthorizationInfoManager.getLastfmName())
        );
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getPersonalTop(artist.getName(), AuthorizationInfoManager.getLastfmName(),
                        getPageSize(), viewer.getPage(), viewer);
            }
        });
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createAlbumsPage() {
        final LastfmAlbumViewerPage viewer = new LastfmAlbumViewerPage(this,
                View.inflate(this, R.layout.list_tab, null),
                R.string.albums);
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getArtistAlbums(artist.getName(), viewer);
            }
        });
        viewer.setItemClickListener(albumClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createTopTracksPage() {
        final LastfmTracksViewerPage viewer = new LastfmTracksViewerPage(this,
                View.inflate(this, R.layout.list_tab, null),
                R.string.top_tracks);
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getArtistTopTracks(artist, getPageSize(), viewer.getPage(), viewer);
            }
        });
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private void changeViewPagerItem(int currentItem) {
        pager.setCurrentItem(currentItem);
        indicator.setCurrentItem(currentItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        final int index = pager.getCurrentItem();
        if (index == PERSONAL_TOP_INDEX) {
            inflater.inflate(R.menu.to_playlist_menu, menu);
        } else if (index == TOP_TRACKS_INDEX) {
            inflater.inflate(R.menu.to_playlist_menu, menu);
        } else if (index == SIMILAR_INDEX) {
            inflater.inflate(R.menu.empty_menu, menu);
        } else if (index == ALBUMS_INDEX) {
            inflater.inflate(R.menu.play_discography, menu);
        } else if (index == ARTIST_INFO_INDEX) {
            inflater.inflate(R.menu.empty_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home: {
                finish();
                Intent intent = new Intent(LastfmArtistViewerActivity.this, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return true;
            case R.id.to_playlist: {
                LastfmTracksViewerPage viewer = (LastfmTracksViewerPage) getViewer(pager.getCurrentItem());
                if (viewer.isNotLoaded()) return true;
                List<Track> tracks = Converter.convertLastfmTrackList(viewer.getItems());
                addToMainPlaylist(tracks);
                Toast.makeText(LastfmArtistViewerActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
            }
            return true;
            case R.id.save_as_playlist: {
                LastfmTracksViewerPage viewer = (LastfmTracksViewerPage) getViewer(pager.getCurrentItem());
                if (viewer.isNotLoaded()) return true;
                List<Track> tracks = Converter.convertLastfmTrackList(viewer.getItems());
                saveAsPlaylist(tracks);
            }
            return true;
            case R.id.play_discography: {
                if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
                    Toast.makeText(LastfmArtistViewerActivity.this, R.string.vk_not_authorized,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                if (getViewer(ALBUMS_INDEX).isNotLoaded()) {
                    break;
                }
                LastfmAlbumViewerPage viewer = (LastfmAlbumViewerPage) getViewer(pager.getCurrentItem());
                viewer.showProgressBar(true);
                List<LastfmAlbum> values = viewer.getItems();
                new LastfmDiscographyModel().getDiscographyTracks(values, new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> tracks) {
                        openMainPlaylist(Converter.convertLastfmTrackList(tracks), 0);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        ErrorNotifier.showError(LastfmArtistViewerActivity.this, errorMessage);
                    }
                });
            }
            return true;
        }
        return false;
    }

    private void getArtistTopTracks(Artist artist, int limit, int page, final LastfmTracksViewerPage viewer) {
        artistModel.getArtistTopTracks(artist,
                limit, page, new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> lastfmTracks) {
                        viewer.fill(lastfmTracks);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        showError(errorMessage);
                    }
                });
    }

    private void getArtistAlbums(String artistName, final LastfmAlbumViewerPage viewer) {
        artistModel.getArtistAlbums(artistName, new SimpleCallback<List<LastfmAlbum>>() {
            @Override
            public void success(List<LastfmAlbum> albums) {
                viewer.fill(albums);
            }

            @Override
            public void failure(String errorMessage) {
                showError(errorMessage);
            }
        });
    }

    private void getPersonalTop(String artist, String username, int limit, int page, final LastfmTracksViewerPage viewer) {
        artistModel.getPersonalArtistTop(artist, username, limit, page,
                new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> lastfmTracks) {
                        viewer.fill(lastfmTracks);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        showError(errorMessage);
                    }
                });
    }

    private void getSimilarArtists(String artistName, int limit, int page,
                                   final LastfmArtistViewerPage viewer) {
        artistModel.getSimilarArtists(artistName, limit, page,
                new SimpleCallback<List<LastfmArtist>>() {
                    @Override
                    public void success(List<LastfmArtist> lastfmArtists) {
                        viewer.fill(lastfmArtists);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        showError(errorMessage);
                    }
                });
    }

    private void getArtistInfo(String artist, String username) {
        artistInfoProgressBar.setVisibility(View.VISIBLE);
        artistModel.getArtistInfo(artist, username,
                new SimpleCallback<LastfmArtist>() {
                    @Override
                    public void success(LastfmArtist lastfmArtist) {
                        artistInfoProgressBar.setVisibility(View.GONE);
                        String info = lastfmArtist.getBio().getContent().trim();
                        Spanned text = Html.fromHtml(info.replace("\n", "<br />"));
                        if (text.length() == 0) {
                            artistInfoTextView.setText(getString(R.string.not_found));
                        } else {
                            artistInfoTextView.setText(text);
                        }
                        artistInfoProgressBar.setVisibility(View.GONE);
                        infoLoaded = true;
                    }

                    @Override
                    public void failure(String errorMessage) {
                        showError(errorMessage);
                    }
                });
    }
}