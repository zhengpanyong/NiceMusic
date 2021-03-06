package com.lzx.nicemusic.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.musiclibrary.utils.LogUtil;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.module.main.sectioned.BannerSectioned;
import com.lzx.nicemusic.module.main.sectioned.ChartTopSectioned;
import com.lzx.nicemusic.module.main.sectioned.HomeSongSection;
import com.lzx.nicemusic.module.main.sectioned.TitleSectioned;
import com.lzx.nicemusic.module.main.presenter.SongListContract;
import com.lzx.nicemusic.module.main.presenter.SongListPresenter;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * @author lzx
 * @date 2018/2/14
 */
@CreatePresenter(SongListPresenter.class)
public class HomeActivity extends BaseMvpActivity<SongListContract.View, SongListPresenter> implements SongListContract.View {

    private RecyclerView mRecyclerView;
    private SectionedRecyclerViewAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mRecyclerView = findViewById(R.id.recycle_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SectionedRecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);
        getPresenter().requestSongList("热歌榜");

        getNotificationIntentData(getIntent());

       // MusicManager.get().openCacheWhenPlaying(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getNotificationIntentData(intent);
    }

    /**
     * 示例：
     * 获取点击通知栏传递过来的信息
     */
    private void getNotificationIntentData(Intent intent) {
        SongInfo songInfo = intent.getParcelableExtra("songInfo");
        if (songInfo != null) {
            LogUtil.i("songInfo = " + songInfo.getSongName());
        }
        Bundle bundle = intent.getBundleExtra("bundleInfo");
        if (bundle != null) {
            LogUtil.i("bundle = " + bundle.getString("name"));
        }
    }

    @Override
    public void onGetSongListSuccess(List<SongInfo> list, String title) {
        switch (title) {
            case "热歌榜":
                mAdapter.addSection(new BannerSectioned(this));
                mAdapter.addSection(new TitleSectioned(this, "Chart Top 10"));
                mAdapter.addSection(new ChartTopSectioned(this, list));
                mAdapter.addSection(new TitleSectioned(this, "New Songs"));
                getPresenter().requestSongList("新歌榜");
                break;
            case "新歌榜":
                mAdapter.addSection(new HomeSongSection(this, list));
                mAdapter.addSection(new TitleSectioned(this, "Live"));
                getPresenter().requestLiveList("Live");
                break;
            case "Live":
                mAdapter.addSection(new HomeSongSection(this, list));
                mAdapter.notifyDataSetChanged();
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetLiveSongSuccess(List<SongInfo> list) {

    }

    @Override
    public void loadMoreSongListSuccess(List<SongInfo> list, String title) {

    }

    @Override
    public void loadFinishAllData() {

    }
}
