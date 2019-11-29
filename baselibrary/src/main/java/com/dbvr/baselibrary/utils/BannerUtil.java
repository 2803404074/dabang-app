package com.dbvr.baselibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dbvr.baselibrary.model.HomeFindMo;
import com.facebook.drawee.view.SimpleDraweeView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class BannerUtil {

    private Context mContext;
    private Banner banner;
    private List<String> mTitle;
    private List<String> mImgs;

    private BannerCallBack bannerCallBack;
    public interface BannerCallBack{
        void click(int position);
    }
    public void setBannerCallBack(BannerCallBack bannerCallBack) {
        this.bannerCallBack = bannerCallBack;
    }

    public BannerUtil(Context mContext, Banner banner, List<HomeFindMo.ThreeMo> mData) {
        this.mContext = mContext;
        this.banner = banner;
        mTitle = new ArrayList<>();
        mImgs = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            mImgs.add(mData.get(i).getChartUrl());
            mTitle.add(mData.get(i).getTitle());
        }

    }

    public void startBanner() {
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.Accordion);
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(mTitle);
        //设置轮播时间
        banner.setDelayTime(4500);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(mImgs);

        banner.setOnBannerListener(position -> {
           if (bannerCallBack!=null){
               bannerCallBack.click(position);
           }
        });
        banner.start();
    }

    class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            //Glide 加载图片简单用法
            Glide.with(context).load(path).into(imageView);
        }

        //提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
        @Override
        public ImageView createImageView(Context context) {
            //使用fresco，需要创建它提供的ImageView，当然你也可以用自己自定义的具有图片加载功能的ImageView
            SimpleDraweeView simpleDraweeView = new SimpleDraweeView(context);

            return simpleDraweeView;
        }
    }

    public void onStart(){
        banner.startAutoPlay();
    }
    public void onStop(){
        banner.stopAutoPlay();
    }
    public void onDestroy(){
        if (banner!=null){
            banner = null;
            mContext = null;
        }
    }
}
