package com.tencent.liteav.demo.videorecord.mybottom;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.dbvr.baselibrary.utils.BottomDialogUtil;
import com.tencent.liteav.demo.videorecord.R;
import com.tencent.liteav.demo.videorecord.RecordDef;
import com.tencent.liteav.demo.videorecord.view.MusicListView;
import com.tencent.liteav.demo.videorecord.view.TCAudioControl;
import com.tencent.liteav.demo.videorecord.view.TCMusicSelectView;
import com.tencent.ugc.TXRecordCommon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MusicBottom extends BottomDialogUtil implements SeekBar.OnSeekBarChangeListener, Button.OnClickListener{

    private Context mContext;
    public static final int NEXTBGM = 1;
    public static final int PREVIOUSBGM = 2;
    public static final int RANDOMBGM = 3;
    //Audio Control
    public static final String TAG = TCAudioControl.class.getSimpleName();
    private SeekBar mMicVolumeSeekBar;
    private SeekBar mBGMVolumeSeekBar;

    private Button mBtnReverbDefalult;
    private Button mBtnReverb1;
    private Button mBtnReverb2;
    private Button mBtnReverb3;
    private Button mBtnReverb4;
    private Button mBtnReverb5;
    private Button mBtnReverb6;
    private int mLastReverbIndex;

    private Button           mBtnVoiceChangerDefault;
    private Button           mBtnVoiceChanger1;
    private Button           mBtnVoiceChanger2;
    private Button           mBtnVoiceChanger3;
    private Button           mBtnVoiceChanger4;
    //    private Button           mBtnVoiceChanger5;
    private Button           mBtnVoiceChanger6;
    private Button           mBtnVoiceChanger7;
    private Button           mBtnVoiceChanger8;
    private Button           mBtnVoiceChanger9;
    private Button           mBtnVoiceChanger10;
    private Button           mBtnVoiceChanger11;
    private int              mLastVoiceChangerIndex;

    private Button mBtnStopBgm;
    private Button mBtnAutoSearch;
    private Button mBtnSelectActivity;
    private int mMicVolume = 100;
    private int mBGMVolume = 100;
    private boolean mBGMSwitch = false;
    private boolean mScanning = false;

    List<MediaEntity> mMusicListData;
    MusicListView mMusicList;
    public TCMusicSelectView mMusicSelectView;
    public LinearLayout mMusicControlPart;
    private int mSelectItemPos = -1;
    private int mLastPlayingItemPos = -1;
    public static final int REQUESTCODE = 1;
    private Map<String, String> mPathSet;
    private OnItemClickListener mOnItemClickListener;
    private AudioListener mAudioListener;

    public interface OnItemClickListener {
        void onBGMSelect(String path);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    public interface AudioListener{
        void onSetReverb(int reverbType);
        void onSetVoiceChangerType(int voiceChangeType);
        void onClickStopBgm();
        void onSetMicVolume(float volume);
        void onSetBGMVolume(float volume);
        int onGetMusicDuration(String musicPath);
    }

    public void setAudioListener(AudioListener audioListener){
        mAudioListener = audioListener;
        if(mAudioListener != null){
            mAudioListener.onSetBGMVolume(mBGMVolume / (float)100);
            mAudioListener.onSetMicVolume(mMicVolume / (float)100);
        }
    }
    public void setReturnListener(View.OnClickListener onClickListener){
        mMusicSelectView.setReturnListener(onClickListener);
    }
    private void playBGM(String name, String path, int pos) {
        if (mLastPlayingItemPos >= 0 && mLastPlayingItemPos != pos) {
            mMusicListData.get(mLastPlayingItemPos).state = 0;
        }
        if (mOnItemClickListener !=null){
            mOnItemClickListener.onBGMSelect(path);
        }
        mBGMSwitch = true;
        mMusicListData.get(pos).state = 1;
        mLastPlayingItemPos = pos;
        mMusicList.getAdapter().notifyDataSetChanged();
    }
    public void stopBGM() {
        mBGMSwitch = false;

        if(mMusicListData.size() != 0 && mLastPlayingItemPos >= 0){
            mMusicListData.get(mLastPlayingItemPos).state = 0;
            mMusicList.getAdapter().notifyDataSetChanged();
        }

        if (mOnItemClickListener !=null){
            mOnItemClickListener.onBGMSelect(null);
        }
    }

    public MusicBottom(Context mContext, int layoutId, double h) {
        super(mContext, layoutId, h);
    }

    static public boolean fPause = false;
    @Override
    public void convert(View holder) {

        mContext = holder.getContext();

        view = holder;
        mMicVolumeSeekBar = (SeekBar) holder.findViewById(R.id.seekBar_voice_volume);
        mMicVolumeSeekBar.setOnSeekBarChangeListener(this);
        mBGMVolumeSeekBar = (SeekBar) holder.findViewById(R.id.seekBar_bgm_volume);
        mBGMVolumeSeekBar.setOnSeekBarChangeListener(this);
        mBGMVolume = mBGMVolumeSeekBar.getProgress() * 2;
        mMicVolume = mMicVolumeSeekBar.getProgress() * 2;

        mBtnReverbDefalult = (Button) holder.findViewById(R.id.btn_reverb_default);
        mBtnReverbDefalult.setOnClickListener(this);
        mBtnReverb1 = (Button) holder.findViewById(R.id.btn_reverb_1);
        mBtnReverb1.setOnClickListener(this);
        mBtnReverb2 = (Button) holder.findViewById(R.id.btn_reverb_2);
        mBtnReverb2.setOnClickListener(this);
        mBtnReverb3 = (Button) holder.findViewById(R.id.btn_reverb_3);
        mBtnReverb3.setOnClickListener(this);
        mBtnReverb4 = (Button) holder.findViewById(R.id.btn_reverb_4);
        mBtnReverb4.setOnClickListener(this);
        mBtnReverb5 = (Button) holder.findViewById(R.id.btn_reverb_5);
        mBtnReverb5.setOnClickListener(this);
        mBtnReverb6 = (Button) holder.findViewById(R.id.btn_reverb_6);
        mBtnReverb6.setOnClickListener(this);

        mBtnVoiceChangerDefault = (Button) holder.findViewById(R.id.btn_voicechanger_default);
        mBtnVoiceChangerDefault.setOnClickListener(this);
        mBtnVoiceChanger1 = (Button) holder.findViewById(R.id.btn_voicechanger_1);
        mBtnVoiceChanger1.setOnClickListener(this);
        mBtnVoiceChanger2 = (Button) holder.findViewById(R.id.btn_voicechanger_2);
        mBtnVoiceChanger2.setOnClickListener(this);
        mBtnVoiceChanger3 = (Button) holder.findViewById(R.id.btn_voicechanger_3);
        mBtnVoiceChanger3.setOnClickListener(this);
        mBtnVoiceChanger4 = (Button) holder.findViewById(R.id.btn_voicechanger_4);
        mBtnVoiceChanger4.setOnClickListener(this);
//        mBtnVoiceChanger5 = (Button) findViewById(R.id.btn_voicechanger_5);
//        mBtnVoiceChanger5.setOnClickListener(this);
        mBtnVoiceChanger6 = (Button) holder.findViewById(R.id.btn_voicechanger_6);
        mBtnVoiceChanger6.setOnClickListener(this);
        mBtnVoiceChanger7 = (Button) holder.findViewById(R.id.btn_voicechanger_7);
        mBtnVoiceChanger7.setOnClickListener(this);
        mBtnVoiceChanger8 = (Button) holder.findViewById(R.id.btn_voicechanger_8);
        mBtnVoiceChanger8.setOnClickListener(this);
        mBtnVoiceChanger9 = (Button) holder.findViewById(R.id.btn_voicechanger_9);
        mBtnVoiceChanger9.setOnClickListener(this);
        mBtnVoiceChanger10 = (Button) holder.findViewById(R.id.btn_voicechanger_10);
        mBtnVoiceChanger10.setOnClickListener(this);
        mBtnVoiceChanger11 = (Button) holder.findViewById(R.id.btn_voicechanger_11);
        mBtnVoiceChanger11.setOnClickListener(this);
        mBtnStopBgm = (Button) holder.findViewById(R.id.btn_stop_bgm);
        mBtnStopBgm.setOnClickListener(this);

        mBtnSelectActivity = (Button) holder.findViewById(R.id.btn_select_bgm);
        mMusicSelectView = (TCMusicSelectView) holder.findViewById(R.id.xml_music_select_view);
        mMusicControlPart = (LinearLayout) holder.findViewById(R.id.xml_music_control_part);
        mMusicListData = new ArrayList<MediaEntity>();
        mMusicSelectView.init(mMusicListData);
        mMusicList = mMusicSelectView.mMusicList;
        mPathSet = new HashMap<String, String>();
        mBtnAutoSearch = mMusicSelectView.mBtnAutoSearch;
        mMusicSelectView.setBackgroundColor(0xffffffff);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        mMusicSelectView.setMinimumHeight(height);

        mBtnSelectActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicSelectView.setVisibility(mMusicSelectView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                // mMusicControlPart.setVisibility(View.GONE);
                //mTCBgmRecordView.setVisibility(mTCBgmRecordView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        mMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playBGM(mMusicListData.get(position).title, mMusicListData.get(position).path, position);
                mSelectItemPos = position;
            }
        });


        mBtnAutoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScanning) {
                    mScanning = false;
                    fPause = true;
                } else {
                    mScanning = true;
                    getMusicList(mContext, mMusicListData);
                    mScanning = false;
                    //mMusicScanner.startScanner(mContext,mCurScanPath,mMusicListData);
                    if (mMusicListData.size() > 0) {
                        mMusicList.setupList(LayoutInflater.from(mContext), mMusicListData);
                        mSelectItemPos = 0;
                        mMusicList.requestFocus();
                        mMusicList.setItemChecked(0, true);
                    }
                }
            }
        });

        setDefaultRevertAndVoiceChange();

    }
    private void setDefaultRevertAndVoiceChange() {
        mBtnReverbDefalult.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_button_3));
        mLastReverbIndex = R.id.btn_reverb_default;

        mBtnVoiceChangerDefault.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_button_3));
        mLastVoiceChangerIndex = R.id.btn_voicechanger_default;
    }

    public void getMusicList(Context context, List<MediaEntity> list) {
        Cursor cursor = null;
        List<MediaEntity> mediaList = list;
        try {
            cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Audio.Media._ID,
                            MediaStore.Audio.Media.TITLE,
                            MediaStore.Audio.Media.DISPLAY_NAME,
                            MediaStore.Audio.Media.DURATION,
                            MediaStore.Audio.Media.ARTIST,
                            MediaStore.Audio.Media.DATA,
                            MediaStore.Audio.Media.SIZE},
                    null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
            if (cursor == null) {
                Log.e(TAG, "GetMediaList cursor is null.");
                return;
            }
            int count = cursor.getCount();
            if (count <= 0) {
                Log.e(TAG, "GetMediaList cursor count is 0.");
                return;
            }
            MediaEntity mediaEntity = null;
            while (!fPause && cursor.moveToNext()) {
                mediaEntity = new MediaEntity();
                mediaEntity.id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                mediaEntity.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                mediaEntity.display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                mediaEntity.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                mediaEntity.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                mediaEntity.path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                if (mediaEntity.path == null) {
                    fPause = false;
                    Toast.makeText(mContext, "Get Music Path Error", Toast.LENGTH_SHORT);
                    return;
                } else {
                    if (mPathSet.get(mediaEntity.path) != null) {
                        Toast.makeText(mContext, "请勿重复添加", Toast.LENGTH_SHORT);
                        fPause = false;
                        return;
                    }
                }
                mPathSet.put(mediaEntity.path, mediaEntity.display_name);
                mediaEntity.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                if (mediaEntity.duration == 0) {
                    if (mAudioListener != null) {
                        mediaEntity.duration = mAudioListener.onGetMusicDuration(mediaEntity.path);
                    }
                }
                mediaEntity.durationStr = longToStrTime(mediaEntity.duration);
                mediaList.add(mediaEntity);
            }
            fPause = false;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return;
    }

    String longToStrTime(long time) {
        time /= 1000;
        return (time / 60) + ":" + ((time % 60) > 9 ? (time % 60) : ("0" + (time % 60)));
    }

    private View view;


    @Override
    public void show() {
        super.show();
    }

    public boolean isShow(){
        return true;
}

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_reverb_default) {
            if (mAudioListener != null) {
                mAudioListener.onSetReverb(TXRecordCommon.VIDOE_REVERB_TYPE_0);
            }

        } else if (i == R.id.btn_reverb_1) {
            if (mAudioListener != null) {
                mAudioListener.onSetReverb(TXRecordCommon.VIDOE_REVERB_TYPE_1);
            }

        } else if (i == R.id.btn_reverb_2) {
            if (mAudioListener != null) {
                mAudioListener.onSetReverb(TXRecordCommon.VIDOE_REVERB_TYPE_2);
            }

        } else if (i == R.id.btn_reverb_3) {
            if (mAudioListener != null) {
                mAudioListener.onSetReverb(TXRecordCommon.VIDOE_REVERB_TYPE_3);
            }

        } else if (i == R.id.btn_reverb_4) {
            if (mAudioListener != null) {
                mAudioListener.onSetReverb(TXRecordCommon.VIDOE_REVERB_TYPE_4);
            }

        } else if (i == R.id.btn_reverb_5) {
            if (mAudioListener != null) {
                mAudioListener.onSetReverb(TXRecordCommon.VIDOE_REVERB_TYPE_5);
            }

        } else if (i == R.id.btn_reverb_6) {
            if (mAudioListener != null) {
                mAudioListener.onSetReverb(TXRecordCommon.VIDOE_REVERB_TYPE_6);
            }

        } else if (i == R.id.btn_voicechanger_default) {
            if (mAudioListener != null) {
                mAudioListener.onSetVoiceChangerType(TXRecordCommon.VIDOE_VOICECHANGER_TYPE_0);
            }

        } else if (i == R.id.btn_voicechanger_1) {
            if (mAudioListener != null) {
                mAudioListener.onSetVoiceChangerType(TXRecordCommon.VIDOE_VOICECHANGER_TYPE_1);
            }

        } else if (i == R.id.btn_voicechanger_2) {
            if (mAudioListener != null) {
                mAudioListener.onSetVoiceChangerType(TXRecordCommon.VIDOE_VOICECHANGER_TYPE_2);
            }

        } else if (i == R.id.btn_voicechanger_3) {
            if (mAudioListener != null) {
                mAudioListener.onSetVoiceChangerType(TXRecordCommon.VIDOE_VOICECHANGER_TYPE_3);
            }

        } else if (i == R.id.btn_voicechanger_4) {
            if (mAudioListener != null) {
                mAudioListener.onSetVoiceChangerType(TXRecordCommon.VIDOE_VOICECHANGER_TYPE_4);
            }

        } else if (i == R.id.btn_voicechanger_6) {
            if (mAudioListener != null) {
                mAudioListener.onSetVoiceChangerType(TXRecordCommon.VIDOE_VOICECHANGER_TYPE_6);
            }

        } else if (i == R.id.btn_voicechanger_7) {
            if (mAudioListener != null) {
                mAudioListener.onSetVoiceChangerType(TXRecordCommon.VIDOE_VOICECHANGER_TYPE_7);
            }

        } else if (i == R.id.btn_voicechanger_8) {
            if (mAudioListener != null) {
                mAudioListener.onSetVoiceChangerType(TXRecordCommon.VIDOE_VOICECHANGER_TYPE_8);
            }

        } else if (i == R.id.btn_voicechanger_9) {
            if (mAudioListener != null) {
                mAudioListener.onSetVoiceChangerType(TXRecordCommon.VIDOE_VOICECHANGER_TYPE_9);
            }

        } else if (i == R.id.btn_voicechanger_10) {
            if (mAudioListener != null) {
                mAudioListener.onSetVoiceChangerType(TXRecordCommon.VIDOE_VOICECHANGER_TYPE_10);
            }

        } else if (i == R.id.btn_voicechanger_11) {
            if (mAudioListener != null) {
                mAudioListener.onSetVoiceChangerType(TXRecordCommon.VIDOE_VOICECHANGER_TYPE_11);
            }

        } else if (i == R.id.btn_stop_bgm) {
            stopBGM();

        }

        if(v.getId() == mLastReverbIndex || v.getId() == mLastVoiceChangerIndex){
            // 防止重复点击相同的按钮，导致的变调和变声二选一问题
            return;
        }

        if (R.id.btn_stop_bgm != v.getId() && v.getId() != mLastReverbIndex &&
                (v.getId() == R.id.btn_reverb_default || v.getId() == R.id.btn_reverb_1 ||
                        v.getId() == R.id.btn_reverb_2 || v.getId() == R.id.btn_reverb_3 ||
                        v.getId() == R.id.btn_reverb_4 || v.getId() == R.id.btn_reverb_5 ||
                        v.getId() == R.id.btn_reverb_6)) {   // 混响
            v.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_button_3));

            View lastV = view.findViewById(mLastReverbIndex);
            if (null != lastV) {
                lastV.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_button_2));
            }

            mLastReverbIndex = v.getId();

        } else if (R.id.btn_stop_bgm != v.getId() && v.getId() != mLastVoiceChangerIndex) {  // 变声
            v.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_button_3));

            View lastV = view.findViewById(mLastVoiceChangerIndex);
            if (null != lastV) {
                lastV.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_button_2));
            }

            mLastVoiceChangerIndex = v.getId();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
