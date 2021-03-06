package com.pillowapps.liqear.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuInflater;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.audio.AudioTimeline;
import com.pillowapps.liqear.components.ResultSherlockActivity;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.PlaylistManager;
import com.pillowapps.liqear.models.Playlist;
import com.pillowapps.liqear.models.Track;

import java.util.ArrayList;
import java.util.List;

public class PlaylistsSherlockListActivity extends ResultSherlockActivity {
    public static final String AIM = "aim";
    @SuppressWarnings("rawtypes")
    private PlaylistsArrayAdapter adapter;
    private Bundle extras;
    private Aim aim;
    private ListView listView;

    public void onStart() {
        super.onStart();
        switch (aim) {
            case SAVE_AS_PLAYLIST: {
                saveAsPlaylist(false, -1);
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlists_layout);
        extras = getIntent().getExtras();
        aim = (Aim) extras.getSerializable(AIM);
        listView = (ListView) findViewById(android.R.id.list);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.playlist_tab));
        listView.setOnCreateContextMenuListener(this);
        List<Playlist> playlists = PlaylistManager.getInstance().getPlaylists();
        adapter = new PlaylistsArrayAdapter<Playlist>(
                PlaylistsSherlockListActivity.this, playlists, Playlist.class);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (aim) {
                    case ADD_TO_PLAYLIST: {
                        Playlist playlist = (Playlist) adapter.get(position);
                        PlaylistManager.getInstance().addTrackToPlaylist(
                                new Track(extras.getString("artist"),
                                        extras.getString("title")), playlist.getPid());
                        finish();
                        break;
                    }
                    case SHOW_PLAYLISTS: {
                        Intent intent = new Intent(
                                PlaylistsSherlockListActivity.this,
                                SearchSherlockListActivity.class);
                        intent.putExtra(SearchSherlockListActivity.SEARCH_MODE,
                                SearchSherlockListActivity.SearchMode.PLAYLIST_TRACKLIST);
                        Playlist playlist = (Playlist) adapter.get(position);
                        intent.putExtra("title", playlist.getTitle());
                        intent.putExtra("pid", playlist.getPid());
                        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                        break;
                    }
                    case SAVE_AS_PLAYLIST: {
                        saveAsPlaylist(false, -1);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (v.getId() == android.R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(((Playlist) adapter.getItem(info.position)).getTitle());
            String[] menuItems = getResources().getStringArray(R.array.playlists_item_menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(android.view.Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    /**
     * Context menu(Long click on playlist item) items logic.
     */
    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Playlist playlist = (Playlist) adapter.getItem(info.position);
        switch (item.getItemId()) {
            case 0:
                adapter.getValues().remove(info.position);
                adapter.notifyDataSetChanged();
                PlaylistManager.getInstance().removePlaylist(playlist.getPid());
                break;
            case 1:
                showSavePlaylistDialog(true, info.position);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.playlists_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
            case R.id.save_current_playlist:
                showSavePlaylistDialog(false, -1);
                break;
            case R.id.new_playlist:
                showNewDialog();
                break;
        }
        return true;
    }

    private void showNewDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle(getResources().getString(R.string.playlist));
        dialog.setMessage(getResources()
                .getString(R.string.enter_title_here));

        final EditText input = new EditText(this);
        dialog.setView(input);

        dialog.setPositiveButton(getResources().getString(R.string.save),
                new DialogInterface.OnClickListener() {
                    @SuppressWarnings("unchecked")
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String title = input.getText().toString();
                        if (title.length() < 1) {
                            title = getResources().getString(R.string.new_playlist);
                        }
                        long pid = PlaylistManager.getInstance().addPlaylist(title,
                                new ArrayList<Track>());
                        if (pid != -1) {
                            ((List<Playlist>) adapter.getValues()).add(0, new Playlist(pid, title));
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
        dialog.setNegativeButton(
                getResources().getString(android.R.string.cancel), null);

        dialog.show();
    }

    private void showSavePlaylistDialog(final boolean isRenaming, final int position) {
        AlertDialog.Builder editAlert = new AlertDialog.Builder(this);

        editAlert.setTitle(getResources().getString(R.string.playlist));
        editAlert.setMessage(getResources()
                .getString(R.string.enter_title_here));

        final EditText input = new EditText(this);
        editAlert.setView(input);

        editAlert.setPositiveButton(getResources().getString(R.string.save),
                new DialogInterface.OnClickListener() {
                    @SuppressWarnings("unchecked")
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String title = input.getText().toString();
                        if (title.length() < 1) {
                            title = getResources().getString(R.string.new_playlist);
                        }

                        if (isRenaming) {
                            PlaylistManager.getInstance().renamePlaylist(
                                    ((Playlist) adapter.get(position)).getPid(), title);
                            ((Playlist) adapter.get(position)).setTitle(title);
                            adapter.notifyDataSetChanged();
                        } else {
                            long pid = PlaylistManager.getInstance().addPlaylist(title,
                                    AudioTimeline.getPlaylist());
                            if (pid != -1) {
                                ((List<Playlist>) adapter.getValues()).add(0,
                                        new Playlist(pid, title));
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
        editAlert.setNegativeButton(getResources().getString(android.R.string.cancel), null);

        editAlert.show();
    }

    private void saveAsPlaylist(final boolean isRenaming, final int position) {
        AlertDialog.Builder editAlert = new AlertDialog.Builder(this);

        editAlert.setTitle(getResources().getString(R.string.playlist));
        editAlert.setMessage(getResources().getString(R.string.enter_title_here));

        final EditText input = new EditText(this);
        editAlert.setView(input);

        editAlert.setPositiveButton(getResources().getString(R.string.save),
                new DialogInterface.OnClickListener() {
                    @SuppressWarnings("unchecked")
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String title = input.getText().toString();
                        if (title.length() < 1) {
                            title = getResources().getString(R.string.new_playlist);
                        }

                        if (isRenaming) {
                            PlaylistManager.getInstance().renamePlaylist(
                                    ((Playlist) adapter.get(position)).getPid(), title);
                            ((Playlist) adapter.get(position)).setTitle(title);
                            adapter.notifyDataSetChanged();
                        } else {
                            List<Track> tracklist = getIntent()
                                    .getParcelableArrayListExtra(Constants.TRACKLIST);
                            long pid = PlaylistManager.getInstance().addPlaylist(title, tracklist);
                            if (pid != -1) {
                                ((List<Playlist>) adapter.getValues())
                                        .add(0, new Playlist(pid, title));
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
        editAlert.setNegativeButton(getResources().getString(android.R.string.cancel), null);

        editAlert.show();
    }

    public enum Aim {
        ADD_TO_PLAYLIST, SHOW_PLAYLISTS, SAVE_AS_PLAYLIST
    }

    private class PlaylistsArrayAdapter<T> extends ArrayAdapter<T> {
        private final Context context;
        private final List<T> values;
        private final Class<T> clazz;

        public PlaylistsArrayAdapter(Context context, List<T> values, Class<T> clazz) {
            super(context, R.layout.search_hint_item, values);
            this.context = context;
            this.values = values;
            this.clazz = clazz;
        }

        public T get(int position) {
            return values.get(position);
        }

        public List<T> getValues() {
            return values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.search_hint_item, parent,
                    false);
            TextView textView = (TextView) rowView.findViewById(R.id.text_list_item);
            if (clazz == Playlist.class) {
                textView.setText(((Playlist) values.get(position)).getTitle());
            }
            return rowView;
        }
    }
}
