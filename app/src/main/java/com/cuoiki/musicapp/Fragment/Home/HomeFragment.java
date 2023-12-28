package com.cuoiki.musicapp.Fragment.Home;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterViewFlipper;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.cuoiki.musicapp.Activity.MainActivity;
import com.cuoiki.musicapp.Database.DAO.AlbumDAO;
import com.cuoiki.musicapp.Database.DAO.UserDAO;
import com.cuoiki.musicapp.Database.Services.CallBack.UserCallBack;
import com.cuoiki.musicapp.Fragment.Home.Adapter.AlbumAdapter;
import com.cuoiki.musicapp.Fragment.Home.Adapter.RankAdapter;
import com.cuoiki.musicapp.Fragment.Home.Adapter.SongNewAdapter;
import com.cuoiki.musicapp.Fragment.Home.Adapter.SuggestAdapter;
import com.cuoiki.musicapp.Database.DAO.SongsDAO;
import com.cuoiki.musicapp.Database.Services.CallBack.AlbumCallBack;
import com.cuoiki.musicapp.Database.Services.CallBack.SongCallBack;
import com.cuoiki.musicapp.Fragment.Music.PlayMusicFragment;
import com.cuoiki.musicapp.Model.Album;
import com.cuoiki.musicapp.Model.Song;
import com.cuoiki.musicapp.Model.UserInfor;
import com.cuoiki.musicapp.R;
import com.squareup.picasso.Picasso;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;


public class HomeFragment extends Fragment {
    RecyclerView rcvalbum, rcvsuggest, rcvtop;
    ArrayList<Album> albumInRank;
    ArrayList<Song> SongList;
    ArrayList<Song> songsInRank;
    ArrayList<Song> songNew;
    AlbumAdapter albumAdapter;
    SuggestAdapter suggestAdapter;
    RankAdapter rankAdapter;
    SongNewAdapter songNewAdapter;

    DotsIndicator dotsIndicator;
    ViewPager viewPager;
    Handler handler;
    Runnable runnable;
    int currentSong;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        init(root);
        getData();
        autoSwipe();
        return root;
    }

    private void init(View root) {
        rcvalbum = root.findViewById(R.id.rcvalbum);
        rcvsuggest = root.findViewById(R.id.rcvsuggest);
        rcvtop = root.findViewById(R.id.rcvtop);
        dotsIndicator = root.findViewById(R.id.dots_indicator);
        viewPager = root.findViewById(R.id.viewPager);
    }

    private void getData() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.loading);
        dialog.show();
        final SongsDAO songsDAO = new SongsDAO(getContext());
        final AlbumDAO albumDAO = new AlbumDAO(getContext());


        songsDAO.getRankSongs(new SongCallBack() {
            @Override
            public void getCallBack(ArrayList<Song> song) {
                songsInRank = song;
                rankAdapter = new RankAdapter(getContext(), song, HomeFragment.this);
                rcvtop.setLayoutManager(new LinearLayoutManager(getActivity()));
                rcvtop.setAdapter(rankAdapter);
            }
        });
        albumDAO.getAlbum(new AlbumCallBack() {
            @Override
            public void getCallBack(ArrayList<Album> album) {
                albumInRank = album;
                albumAdapter = new AlbumAdapter(getContext(), albumInRank, HomeFragment.this);
                rcvalbum.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                rcvalbum.setHasFixedSize(true);
                rcvalbum.setAdapter(albumAdapter);
            }
        });

        songsDAO.getSuggest(new SongCallBack() {
            @Override
            public void getCallBack(ArrayList<Song> song) {
                SongList = song;
                suggestAdapter = new SuggestAdapter(getContext(), SongList, HomeFragment.this);
                rcvsuggest.setLayoutManager(new LinearLayoutManager(getActivity()));
                rcvsuggest.setAdapter(suggestAdapter);
            }
        });

        songsDAO.getNewSongs(new SongCallBack() {
            @Override
            public void getCallBack(ArrayList<Song> song) {
                songNew = song;
                Log.d("songNew", String.valueOf(songNew.size()));
                songNewAdapter = new SongNewAdapter(getContext(), songNew, HomeFragment.this);
                viewPager.setAdapter(songNewAdapter);
                dotsIndicator.setViewPager(viewPager);
            }
        });
        dialog.dismiss();
    }

    private void autoSwipe() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                currentSong = viewPager.getCurrentItem();
                currentSong++;
                try {
                    if (currentSong >= viewPager.getAdapter().getCount()) {
                        currentSong = 0;
                    }
                    viewPager.setCurrentItem(currentSong, true);
                    handler.postDelayed(runnable, 4500);
                } catch (Exception e) {
                }
            }
        };
        handler.postDelayed(runnable, 4500);

    }


}