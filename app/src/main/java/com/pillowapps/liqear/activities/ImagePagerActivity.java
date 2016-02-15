/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.pillowapps.liqear.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.TrackedBaseActivity;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.models.ImageModel;
import com.pillowapps.liqear.models.lastfm.LastfmArtistModel;
import com.pillowapps.liqear.views.TouchImageView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ImagePagerActivity extends TrackedBaseActivity {

    public static final String ARTIST = "artist";
    public static final String PAGE_FORMAT = "[%d/%d] ";
    private static final String STATE_POSITION = "STATE_POSITION";
    private static final String IMAGE_POSITION = "image_position";
    private ViewPager pager;
    private ActionBar actionBar;
    private List<String> imageUrls;
    private String artist;
    private boolean loading = false;
    private int page = 1;
    private ImagePagerActivity.ImagePagerAdapter adapter;
    private ProgressBar pageProgressBar;

    @Inject
    ImageModel imageModel;

    @Inject
    LastfmArtistModel lastfmArtistModel;

    public static Intent startIntent(Context context) {
        return new Intent(context, ImagePagerActivity.class);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_image_pager);

        LBApplication.get(this).applicationComponent().inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        imageUrls = new ArrayList<>();
        int pagerPosition = bundle.getInt(IMAGE_POSITION, 0);

        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }
        artist = bundle.getString(ARTIST);
        if (artist == null) {
            artist = "";
        }
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(artist);
        }

        pageProgressBar = (ProgressBar) findViewById(R.id.pageProgressBar);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new ImagePagerAdapter(imageUrls);
        pager.setAdapter(adapter);
        pager.setCurrentItem(pagerPosition);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getImages(0);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
                // No op.
            }

            @Override
            public void onPageSelected(final int i) {
                actionBar.setTitle(String.format(PAGE_FORMAT, i + 1, imageUrls.size()) + artist);
                if (!loading && i == imageUrls.size() - 1 && imageUrls.size() == 36 * (page - 1)) {
                    getImages(i);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                // No op.
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.download_image_button: {
                // todo
            }
            break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_pager_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void getImages(final int page) {
        loading = true;
        pageProgressBar.setVisibility(View.VISIBLE);
        lastfmArtistModel.getArtistImages(artist, this.page++, new SimpleCallback<List<String>>() {
            @Override
            public void success(List<String> images) {
                pageProgressBar.setVisibility(View.GONE);
                if (images == null) return;
                if (imageUrls == null) {
                    imageUrls = images;
                } else {
                    imageUrls.addAll(images);
                }
                adapter.notifyDataSetChanged();
                actionBar.setTitle(String.format(PAGE_FORMAT, page + 1, imageUrls.size()) + artist);
            }

            @Override
            public void failure(String errorMessage) {
                pageProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, pager.getCurrentItem());
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private final List<String> images;
        private final LayoutInflater inflater;

        ImagePagerAdapter(List<String> images) {
            this.images = images;
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
            TouchImageView imageView = (TouchImageView) imageLayout.findViewById(R.id.image);
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

            imageModel.loadImage(images.get(position), imageView, bitmap -> {
                spinner.setVisibility(View.GONE);
            });
            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            // No op.
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(ViewGroup container) {
            // No op.
        }
    }
}