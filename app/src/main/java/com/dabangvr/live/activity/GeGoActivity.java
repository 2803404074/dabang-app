package com.dabangvr.live.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapter;
import com.dabangvr.comment.application.MyApplication;
import com.dabangvr.live.gift.GiftMo;
import com.dabangvr.live.gift.GiftViewBackup;
import com.dabangvr.live.gift.danmu.DanmuAdapter;
import com.dabangvr.live.gift.danmu.DanmuEntity;
import com.dabangvr.play.activity.verticle.LiveInterFace;
import com.dabangvr.user.activity.UserSJRZOneActivity;
import com.dabangvr.util.GDMapLocation;
import com.dabangvr.util.OpenCameUtil;
import com.dbvr.baselibrary.model.LiveMo;
import com.dbvr.baselibrary.model.TagMo;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.DataUtil;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.utils.UserHolper;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.orzangleli.xdanmuku.DanmuContainerView;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoLivePublisherCallback;
import com.zego.zegoliveroom.callback.im.IZegoIMCallback;
import com.zego.zegoliveroom.constants.ZegoBeauty;
import com.zego.zegoliveroom.constants.ZegoConstants;
import com.zego.zegoliveroom.constants.ZegoFilter;
import com.zego.zegoliveroom.constants.ZegoIM;
import com.zego.zegoliveroom.constants.ZegoVideoViewMode;
import com.zego.zegoliveroom.entity.AuxData;
import com.zego.zegoliveroom.entity.ZegoBigRoomMessage;
import com.zego.zegoliveroom.entity.ZegoPublishStreamQuality;
import com.zego.zegoliveroom.entity.ZegoRoomMessage;
import com.zego.zegoliveroom.entity.ZegoUserState;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.dabangvr.util.OpenCameUtil.REQ_1;
import static com.dabangvr.util.OpenCameUtil.REQ_2;
import static com.dabangvr.util.OpenCameUtil.REQ_4;
import static com.dabangvr.util.OpenCameUtil.imageUri;
import static com.zego.zegoliveroom.constants.ZegoConstants.RoomRole.Anchor;

public class GeGoActivity extends LiveBaseActivity implements GDMapLocation.MapEvevt {
    private boolean isStart = false;//直播成功标志

    @BindView(R.id.textTureView)
    TextureView textureView;

    private ImageView ivCover;//封面
    protected ZegoLiveRoom zegoLiveRoom;

    //评论列表
    @BindView(R.id.recycle_comment)
    RecyclerView recyclerView;
    //列表适配器
    private RecyclerAdapter commentAdapter;
    //评论数据源
    private List<ZegoRoomMessage> commentData = new ArrayList<>();

    //弹幕
    @BindView(R.id.danmuContainerView)
    DanmuContainerView danmuContainerView;

    //SVGA动画
    @BindView(R.id.sVGAImageView)
    SVGAImageView animationView;

    //打赏
    @BindView(R.id.giftView)
    GiftViewBackup giftView;

    @BindView(R.id.tvDzNum)
    TextView tvDzNum;
    private int dzNum = 0;

    @BindView(R.id.sdvHead)
    SimpleDraweeView sdvHead;

    @BindView(R.id.tvNickName)
    TextView tvNickName;

    @BindView(R.id.tvRoomNumber)
    TextView tvRoomNumber;

    @BindView(R.id.tvOnLine)
    TextView tvOnLine;

    @BindView(R.id.charCounter)
    Chronometer chronometer;//计时器
    private long mRecordTime;
    private int miss = 0;//总秒数

    @BindView(R.id.llNet)
    LinearLayout llNet;

    @Override
    public int setLayout() {
        return R.layout.activity_ge_go;
    }

    private ViewGroup viewGroup;
    private LinearLayout mRoomContainer;
    private GDMapLocation gdMapLocation;
    private GDMapLocation.MapEvevt evevt;
    private TextView tvLocation;

    /**
     * 初始化未开播前的view
     */
    private void initLoadInitialView() {
        userMess = UserHolper.getUserHolper(this).getUserMess();
        //获取activity的viewgroup
        viewGroup = (ViewGroup) getWindow().getDecorView();
        mRoomContainer = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.live_input_view, null);

        //关闭
        mRoomContainer.findViewById(R.id.ivClose).setOnClickListener((view) -> {
            AppManager.getAppManager().finishActivity(this);
        });

        //封面
        ivCover = mRoomContainer.findViewById(R.id.ivCover);
        Glide.with(MyApplication.getInstance()).load(userMess.getHeadUrl()).into(ivCover);
        ivCover.setOnClickListener((view) -> {
            BottomDialogUtil2.getInstance(this).showLive(R.layout.dialog_came, view1 -> {
                view1.findViewById(R.id.takePhoto).setOnClickListener(view2 -> {
                    zegoLiveRoom.stopPreview();
                    OpenCameUtil.init("cover");
                    OpenCameUtil.openCamera(this);
                    BottomDialogUtil2.getInstance(this).dess();
                });
                view1.findViewById(R.id.choosePhoto).setOnClickListener(view2 -> {
                    OpenCameUtil.init("cover");
                    OpenCameUtil.openAlbum(this);
                    BottomDialogUtil2.getInstance(this).dess();
                });
            });
        });
        //标题
        EditText etInput = mRoomContainer.findViewById(R.id.etInput);

        //定位
        tvLocation = mRoomContainer.findViewById(R.id.tvLocation);
        //关键代码,初始化
        gdMapLocation = GDMapLocation.getInstance(getContext(), null);
        //检查定位权限
        gdMapLocation.checkLocationPermission();
        evevt = this;
        gdMapLocation.setEvevt(evevt);

        //定位开启关闭的swich
        CheckBox checkBox = mRoomContainer.findViewById(R.id.switch_loa);
        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            isOpenLocation = b;
            if (b) {
                tvLocation.setTextColor(getResources().getColor(R.color.white));
            } else {
                tvLocation.setTextColor(getResources().getColor(R.color.color_e2e2e2));
            }
        });

        //美颜
        mRoomContainer.findViewById(R.id.switch_my).setOnClickListener((view) -> {
            beautySet();
        });

        //滤镜
        mRoomContainer.findViewById(R.id.llLj).setOnClickListener(view -> {
            beautySet();
        });
        //开始直播
        mRoomContainer.findViewById(R.id.tvStartLive).setOnClickListener((view) -> {
            if (StringUtils.isEmpty(etInput.getText().toString())) {
                ToastUtil.showShort(getContext(), "请添加直播标题");
                return;
            }
            mTitle = etInput.getText().toString();
            onStartLive();
        });
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getOnlineCategorys, null, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
                List<TagMo> mTypeList = new Gson().fromJson(result, new TypeToken<List<TagMo>>() {
                }.getType());
                if (mTypeList != null && mTypeList.size() > 0) {
                    mTypeList.get(0).setCheck(true);
                    RecyclerView recyclerType = mRoomContainer.findViewById(R.id.recyclerType);
                    recyclerType.setLayoutManager(new GridLayoutManager(getContext(), 4));
                    RecyclerAdapter typeAdapter = new RecyclerAdapter<TagMo>(getContext(), mTypeList, R.layout.item_txt) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, TagMo o) {
                            TextView checkBox = holder.getView(R.id.cb_txt);
                            checkBox.setText(o.getName());
                            if (o.isCheck()) {
                                checkBox.setBackgroundResource(R.drawable.shape_db);
                                checkBox.setTextColor(getResources().getColor(R.color.colorWhite));
                                typeId = o.getId();
                            } else {
                                checkBox.setBackgroundResource(R.drawable.shape_gray_w);
                                checkBox.setTextColor(getResources().getColor(R.color.textTitle));
                            }
                        }
                    };
                    recyclerType.setAdapter(typeAdapter);
                    typeAdapter.setOnItemClickListener((view, position) -> {
                        for (int i = 0; i < mTypeList.size(); i++) {
                            if (i == position) {
                                mTypeList.get(i).setCheck(true);
                            } else {
                                mTypeList.get(i).setCheck(false);
                            }
                        }
                        typeAdapter.updateDataa(mTypeList);
                    });
                    mRoomContainer.findViewById(R.id.llShow).setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailed(String msg) {
                mRoomContainer.findViewById(R.id.llShow).setVisibility(View.VISIBLE);
                mRoomContainer.findViewById(R.id.llShow).setOnClickListener((view) -> {
                    goTActivity(UserSJRZOneActivity.class, null);
                    AppManager.getAppManager().finishActivity(GeGoActivity.this);
                });
            }
        });
        viewGroup.addView(mRoomContainer);
    }

    //定位成功后处理方法
    @Override
    public void onGetMapInfo(AMapLocation amapLocation) {
        longitude = amapLocation.getLongitude();
        latitude = amapLocation.getLatitude();
        Map<String, Object> map = new HashMap<>();
        map.put("location", longitude+","+latitude);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getAmapReverse, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                tvLocation.setText(StringUtils.isEmptyTxt(result));
            }

            @Override
            public void onFailed(String msg) {
            }
        });
        gdMapLocation.onStop();
    }

    //反馈申请权限处理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        gdMapLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_1: {
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    OpenCameUtil.startPhotoZoom(GeGoActivity.this, imageUri);
                }
                break;
            }
            case REQ_2: {
                if (resultCode == RESULT_OK) {
                    OpenCameUtil.startPhotoZoom(GeGoActivity.this, data.getData());
                }
                break;
            }
            case REQ_4: {
                if (resultCode == RESULT_OK) {
                    try {
                        zegoLiveRoom.startPreview();
                        Bitmap bitmapCamera = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(OpenCameUtil.imageUris));
                        ivCover.setImageBitmap(bitmapCamera);
                        if (bitmapCamera != null) {
                            try {
                                file = new File(new URI(OpenCameUtil.imageUris.toString()));
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        zegoLiveRoom.startPreview();
                    }
                }
                break;
            }
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void initView() {
        initLoadInitialView();
        //网络状态监听
        MyApplication.getInstance().setNetCallBack(hasNet -> {
            if (!hasNet) {
                //显示网络
                llNet.setVisibility(View.VISIBLE);
            } else {
                llNet.setVisibility(View.GONE);
            }
        });
        //初始化
        zegoLiveRoom = MyApplication.getInstance().initGeGo();
        //预览
        zegoLiveRoom.setPreviewView(textureView);
        zegoLiveRoom.setPreviewViewMode(ZegoVideoViewMode.ScaleToFill);//ScaleToFill填充,ScaleAspectFill默认(可能有部分被裁减)
        zegoLiveRoom.startPreview();
    }


    /**
     * 美颜
     * 滤镜
     */
    private float mp = 1;
    private float mb = 1;
    private float rh = 0;

    private void beautySet() {
        BottomDialogUtil2.getInstance(this).showLive(R.layout.buttom_view_seebar, view -> {
            SeekBar sbMp = view.findViewById(R.id.sbMp);
            sbMp.setMax(16);
            sbMp.setProgress((int) mp);
            sbMp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    zegoLiveRoom.enableBeautifying(ZegoBeauty.POLISH | ZegoBeauty.WHITEN | ZegoBeauty.SHARPEN);
                    mp = i;
                    zegoLiveRoom.setPolishStep(mp);//1-16
                    zegoLiveRoom.setWhitenFactor(mb);//0-1,越小越白
                    zegoLiveRoom.setSharpenFactor(rh);//0-2
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            SeekBar sbMb = view.findViewById(R.id.sbMb);
            sbMb.setMax(10);
            sbMb.setProgress((int) mb);
            sbMb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    zegoLiveRoom.enableBeautifying(ZegoBeauty.POLISH | ZegoBeauty.WHITEN | ZegoBeauty.SHARPEN);
                    mb = (11 - i) * 0.1f;
                    zegoLiveRoom.setPolishStep(mp);//1-16
                    zegoLiveRoom.setWhitenFactor(mb);//0-1,越小越白
                    zegoLiveRoom.setSharpenFactor(rh);//0-2
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            SeekBar sbRh = view.findViewById(R.id.sbRh);
            sbRh.setMax(20);
            sbRh.setProgress((int) rh);
            sbRh.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    zegoLiveRoom.enableBeautifying(ZegoBeauty.POLISH | ZegoBeauty.WHITEN | ZegoBeauty.SHARPEN);
                    rh = i / 10;
                    zegoLiveRoom.setPolishStep(mp);//1-16
                    zegoLiveRoom.setWhitenFactor(mb);//0-1,越小越白
                    zegoLiveRoom.setSharpenFactor(rh);//0-2
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });


            view.findViewById(R.id.llHb).setOnClickListener((view1) -> {
                zegoLiveRoom.setFilter(ZegoFilter.BlackWhite); //此处滤镜类型采用黑白
            });
            view.findViewById(R.id.llLd).setOnClickListener((view1) -> {
                zegoLiveRoom.setFilter(ZegoFilter.Blue); //此处滤镜类型采用蓝调
            });
            view.findViewById(R.id.llYs).setOnClickListener((view1) -> {
                zegoLiveRoom.setFilter(ZegoFilter.Dark); //此处滤镜类型采用夜色
            });
            view.findViewById(R.id.llDy).setOnClickListener((view1) -> {
                zegoLiveRoom.setFilter(ZegoFilter.Fade); //此处滤镜类型采用淡雅
            });
            view.findViewById(R.id.llGt).setOnClickListener((view1) -> {
                zegoLiveRoom.setFilter(ZegoFilter.Gothic); //此处滤镜类型采用哥特
            });
            view.findViewById(R.id.llGy).setOnClickListener((view1) -> {
                zegoLiveRoom.setFilter(ZegoFilter.Halo); //此处滤镜类型采用光晕
            });
            view.findViewById(R.id.llMh).setOnClickListener((view1) -> {
                zegoLiveRoom.setFilter(ZegoFilter.Illusion); //此处滤镜类型采用梦幻
            });
            view.findViewById(R.id.llQn).setOnClickListener((view1) -> {
                zegoLiveRoom.setFilter(ZegoFilter.Lime); //此处滤镜类型采用青柠
            });
            view.findViewById(R.id.llJj).setOnClickListener((view1) -> {
                zegoLiveRoom.setFilter(ZegoFilter.Lomo); //此处滤镜类型采用简洁
            });
            view.findViewById(R.id.llNo).setOnClickListener((view1) -> {
                zegoLiveRoom.setFilter(ZegoFilter.None); //此处滤镜类型采用不使用滤镜
            });
            view.findViewById(R.id.llLh).setOnClickListener((view1) -> {
                zegoLiveRoom.setFilter(ZegoFilter.OldStyle); //此处滤镜类型采用老化
            });
            view.findViewById(R.id.llLm).setOnClickListener((view1) -> {
                zegoLiveRoom.setFilter(ZegoFilter.Romantic); //此处滤镜类型采用浪漫
            });
            view.findViewById(R.id.llRs).setOnClickListener((view1) -> {
                zegoLiveRoom.setFilter(ZegoFilter.SharpColor); //此处滤镜类型采用锐色
            });
            view.findViewById(R.id.llHj).setOnClickListener((view1) -> {
                zegoLiveRoom.setFilter(ZegoFilter.Wine); //此处滤镜类型采用红酒
            });
            view.findViewById(R.id.llHb).setOnClickListener((view1) -> {
                zegoLiveRoom.setFilter(ZegoFilter.BlackWhite); //此处滤镜类型采用黑白
            });
            view.findViewById(R.id.llHb).setOnClickListener((view1) -> {
                zegoLiveRoom.setFilter(ZegoFilter.BlackWhite); //此处滤镜类型采用黑白
            });
        });
    }

    private int isCom;//在线人数统计

    private void initCallBack() {
        setInterFace(new LiveInterFace() {
            @Override
            public void updateHots(int isComs) {
                isCom = isComs;
                tvOnLine.setText("在线" + isCom);
            }

            @Override
            public void updateComment(int size, ArrayList data) {
                recyclerView.smoothScrollToPosition(size);
                commentAdapter.addAll(data);
            }

            @Override
            public void updateDanMu(DanmuEntity danmuEntity) {
                danmuContainerView.addDanmu(danmuEntity);
            }

            @Override
            public void updateGift(GiftMo giftMo) {
                giftView.addGift(giftMo);
            }

            @Override
            public void updateGiftMall(SVGAVideoEntity giftMo, int arg0, boolean arg1) {
                animationView.setVideoItem(giftMo);
                animationView.stepToFrame(arg0, arg1);
            }

            @Override
            public void updateDz(int num) {
                dzNum += num;
                tvDzNum.setText("赞" + dzNum);
            }

            @Override
            public void updateRoom(LiveMo o) {

            }
        });
    }

    private void initGiftMall() {
        animationView.setCallback(new SVGACallback() {
            @Override
            public void onPause() {
                Log.e("aaaaaa", "onPause~~~~");
            }

            @Override
            public void onFinished() {
                Log.e("aaaaaa", "onFinished~~~~");
            }

            @Override
            public void onRepeat() {
                animationView.stopAnimation();
            }

            @Override
            public void onStep(int frame, double percentage) {

            }
        });
    }

    private void initGift() {
        giftView.setViewCount(2);
        giftView.init();
    }

    private void intDanmu() {
        danmuContainerView.setAdapter(new DanmuAdapter(this));
    }

    private void initCommentUi() {
        doTopGradualEffect(recyclerView);
        commentAdapter = new RecyclerAdapter<ZegoRoomMessage>(this, commentData, R.layout.item_comment) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, ZegoRoomMessage o) {
                TextView tvMess = holder.getView(R.id.tvMess);
                holder.setText(R.id.tvUser, o.fromUserName);
                tvMess.setText(o.content);
                int mColor = R.color.colorDb3;
                //系统
                if (o.messageCategory == ZegoIM.MessageCategory.System) {
                    mColor = R.color.colorDb3;
                }
                //聊天
                if (o.messageCategory == ZegoIM.MessageCategory.Chat) {
                    mColor = R.color.colorWhite;
                }
                //点赞
                if (o.messageCategory == ZegoIM.MessageCategory.Like) {
                    tvMess.setText("给你点了" + o.content + "个赞");
                    mColor = R.color.colorZi;
                }
                //礼物
                if (o.messageCategory == ZegoIM.MessageCategory.Gift) {
                    mColor = R.color.colorOrag;
                }
                tvMess.setTextColor(getResources().getColor(mColor));
            }
        };
        recyclerView.setAdapter(commentAdapter);
    }

    @Override
    public void initData() {

    }

    private boolean isBackCam = true;

    @OnClick({R.id.tvFinishLive, R.id.llMys, R.id.ivChangeCame})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.tvFinishLive:
                if (isStart) {
                    showTips();
                } else {
                    AppManager.getAppManager().finishActivity(this);
                }
                break;
            case R.id.llMys:
                beautySet();
                break;
            case R.id.ivChangeCame://摄像头切换
                zegoLiveRoom.setFrontCam(isBackCam);
                isBackCam = !isBackCam;
                break;
            default:
                break;
        }
    }

    /**
     * 开始直播
     */
    private void onStartLive() {
        //移除信息初始化视图
        viewGroup.removeView(mRoomContainer);
        initCallBack();
        initCommentUi();
        intDanmu();
        initGift();
        initGiftMall();

        //显示直播视图
        findViewById(R.id.rlLive).setVisibility(View.VISIBLE);
        //设置主播袭信息
        tvNickName.setText(userMess.getNickName());
        sdvHead.setImageURI(userMess.getHeadUrl());
        tvRoomNumber.setText("---");
        //房间相关设置
        setRoom();
        //推流相关设置
        setPublish();
    }

    private void setRoom() {
        //设置用户信息
        ZegoLiveRoom.setUser(String.valueOf(userMess.getId()), userMess.getNickName());
        //房间消息回调
        zegoLiveRoom.setZegoIMCallback(new IZegoIMCallback() {
            @Override
            public void onUserUpdate(ZegoUserState[] zegoUserStates, int i) {
                ZegoRoomMessage message = new ZegoRoomMessage();
                message.messageCategory = ZegoIM.MessageCategory.System;
                message.fromUserName = zegoUserStates[0].userName;
                int flag = zegoUserStates[0].updateFlag;//1 进入房间  2离开房间
                if (flag == 1) {
                    message.content = "进入房间";
                }
                if (flag == 2) {
                    message.content = "离开房间";
                }
                setNotifyUi(message, null);
            }

            @Override
            public void onRecvRoomMessage(String s, ZegoRoomMessage[] zegoRoomMessages) {
                setNotifyUi(zegoRoomMessages[0], null);
            }

            @Override
            public void onUpdateOnlineCount(String s, int i) {
                addAndRemoveUser(i);
            }

            @Override
            public void onRecvBigRoomMessage(String s, ZegoBigRoomMessage[] zegoBigRoomMessages) {

            }
        });
        //设置房间配置信息
        zegoLiveRoom.setRoomConfig(false, true);
        roomId = System.currentTimeMillis() + "" + userMess.getId();
        //登陆房间
        zegoLiveRoom.loginRoom(roomId, Anchor, (stateCode, zegoStreamInfos) -> {
            if (stateCode == 0) {
                Log.e("gego", "登录房间成功");
                tvRoomNumber.setText(roomId);
            } else {
                Log.e("gego", "登录房间失败" + stateCode);
            }
        });
    }

    //弹窗提示直播间信息
    private void showTips() {
        if (instance == null) return;
        if (!GeGoActivity.this.isFinishing()) {
            DialogUtil.getInstance(GeGoActivity.this).show(R.layout.dialog_live_tip, holder -> {
                holder.findViewById(R.id.tvConfirm).setOnClickListener(view -> stopFunction());
                holder.findViewById(R.id.tvKeepLive).setOnClickListener(view -> DialogUtil.getInstance(GeGoActivity.this).des());
                TextView tvDate = holder.findViewById(R.id.tv_tips);
                tvDate.setText("你已直播时长：" + DataUtil.formatMiss(miss));
                TextView tvSeeNum = holder.findViewById(R.id.tvPersonNum);
                tvSeeNum.setText("当前直播人数：" + isCom + "人");
            });
        } else {
            finish();
        }
    }

    public void stopFunction() {
        Intent intent = new Intent(getContext(), LiveFinishActivity.class);
        intent.putExtra("liveTime", DataUtil.formatMiss(miss));//直播时长
        intent.putExtra("liveSeeNum", String.valueOf(isCom));//观看人数
        //intent.putExtra("liveAddFansNum",liveAddFansNum);//新增粉丝
        intent.putExtra("liveDzNum", String.valueOf(dzNum));//点赞数量
        //intent.putExtra("liveDropNum",shouwyi);//跳币收益
        startActivity(intent);
        finish();
    }

    private void setPublish() {
        //设置推流回调
        zegoLiveRoom.setZegoLivePublisherCallback(new IZegoLivePublisherCallback() {
            @Override
            public void onPublishStateUpdate(int i, String s, HashMap<String, Object> hashMap) {
                Log.e("gego", "onPublishStateUpdate");
                if (i != 0) {//推流失败
                    isStart = false;
                    chronometer.stop();
                    mRecordTime = chronometer.getBase();
                } else {//将封面、标题、推流信息发送给后端
                    isStart = true;
                    //开始计时
                    runOnUiThread(() -> {
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        int hour = (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000 / 60);
                        chronometer.setFormat("0" + hour + ":%s");
                        chronometer.start();
                        chronometer.setOnChronometerTickListener(ch -> miss++);
                    });
                    //将房间信息发送给后端
                    senMessageToMyServer();
                }
            }

            @Override
            public void onJoinLiveRequest(int i, String s, String s1, String s2) {
                Log.e("gego", "onJoinLiveRequest");
            }

            @Override
            public void onPublishQualityUpdate(String s, ZegoPublishStreamQuality zegoPublishStreamQuality) {
                Log.e("gego", "onPublishQualityUpdate");
            }

            @Override
            public AuxData onAuxCallback(int i) {
                Log.e("gego", "onAuxCallback");
                return null;
            }

            @Override
            public void onCaptureVideoSizeChangedTo(int i, int i1) {
                Log.e("gego", "onCaptureVideoSizeChangedTo");
            }

            @Override
            public void onMixStreamConfigUpdate(int i, String s, HashMap<String, Object> hashMap) {
                Log.e("gego", "onMixStreamConfigUpdate");
            }

            @Override
            public void onCaptureVideoFirstFrame() {
                Log.e("gego", "onCaptureVideoFirstFrame");
            }

            @Override
            public void onCaptureAudioFirstFrame() {
                Log.e("gego", "onCaptureAudioFirstFrame");
            }
        });
        //开始推流
        stremId = "stream" + System.currentTimeMillis() + userMess.getId();
        zegoLiveRoom.startPublishing(stremId, mTitle, ZegoConstants.PublishFlag.JoinPublish);
    }

    /**
     * 将直播信息内容发送给后端
     */
    private String roomId;
    private String stremId;
    private String typeId;
    private String mTitle;
    private double longitude;
    private double latitude;
    private File file;
    private boolean isOpenLocation = true;

    private void senMessageToMyServer() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", mTitle);
        map.put("liveTag", typeId);
        if (file == null){
            map.put("coverUrl",userMess.getHeadUrl());
        }else {
            map.put("coverUrlFile",file);
        }
        map.put("roomId", roomId);
        map.put("streamId", stremId);
        if (isOpenLocation){
            map.put("longitude", longitude);//经度
            map.put("latitude", latitude);//纬度
        }
        OkHttp3Utils.getInstance(getContext()).upLoadFile(DyUrl.createStream, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                ZegoRoomMessage message = new ZegoRoomMessage();
                message.messageCategory = ZegoIM.MessageCategory.System;
                message.fromUserName = "海跳跳";
                message.content = "根据相关法律规定，直播过程中禁止涉黄、赌、毒等危害社会健康内容。";
                setNotifyUi(message, null);
            }

            @Override
            public void onFailed(String msg) {
                zegoLiveRoom.logoutRoom();
                zegoLiveRoom.stopPublishing();
                isLoading(true);
                isStart = false;
                new Handler().postDelayed(() -> {
                    findViewById(R.id.rlLive).setVisibility(View.GONE);
                    viewGroup.addView(mRoomContainer);
                    ToastUtil.showShort(getContext(),msg);
                    isLoading(false);
                    zegoLiveRoom.startPreview();
                },2000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //解决选封面的时候，不选-》返回，白屏的情况
        if (zegoLiveRoom!=null){
            if (textureView!=null){
                zegoLiveRoom.startPreview();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtil.getInstance(this).des();
        //停止推流
        zegoLiveRoom.stopPublishing();
        //退出房间
        zegoLiveRoom.logoutRoom();

        MyApplication.getInstance().desGeGo();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (isStart) { //已经开始了
                if (DialogUtil.getInstance(this).isShow()) {
                    DialogUtil.getInstance(this).des();
                } else {
                    showTips();
                }
            } else {
                return super.onKeyDown(keyCode, event);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
