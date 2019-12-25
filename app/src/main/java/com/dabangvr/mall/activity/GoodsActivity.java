package com.dabangvr.mall.activity;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.comment.adapter.RecyclerAdapter;
import com.dabangvr.comment.application.MyApplication;
import com.dabangvr.databinding.ActivityGoodsBinding;
import com.dabangvr.mall.adapter.ViewPagerTransform;
import com.dbvr.baselibrary.model.GoodsVo;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.baselibrary.view.BaseActivityBinding;
import com.zego.zegoliveroom.ZegoLiveRoom;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class GoodsActivity extends BaseActivityBinding<ActivityGoodsBinding> {

    @BindView(R.id.viewpager)
    ViewPagerTransform viewPagerTransform;

    @BindView(R.id.recycle_comment)
    RecyclerView recyclerComment;
    private RecyclerAdapter adapter;
    @Override
    public int setLayout() {
        return R.layout.activity_goods;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void initView() {

        initGoodsMess();//初始化商品信息

        initViewPage();//初始化轮播图

        initComment();//初始化评论列表

        intitSpecifications();//初始化规格列表

    }

    private void initGoodsMess() {
    }

    private void intitSpecifications() {

    }

    private void initViewPage() {
        List<String>list = new ArrayList<>();
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1573317019422&di=f28c0022e5796b2e750b536a66c3f182&imgtype=0&src=http%3A%2F%2Fimg001.hc360.cn%2Fy6%2FM05%2F46%2F54%2FwKhQtFZexaiEFLRvAAAAAMDhtsQ300.jpg");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1573317019423&di=23088236d380373df986353614e00ff9&imgtype=0&src=http%3A%2F%2Fimg003.hc360.cn%2Fy5%2FM04%2F94%2F92%2FwKhQUVXUpuqEa3sKAAAAAJmEBtY356.jpg");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1573317019421&di=bdf605a8d015b88d8063dd8dffc5db36&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fphotoblog%2F1108%2F22%2Fc1%2F8726797_8726797_1313971832656_mthumb.jpg");

        viewPagerTransform.setPageMargin(20);
        viewPagerTransform.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                View view = null;
                String img = list.get(position);
                if (!StringUtils.isEmpty(img)) {
                    view = View.inflate(container.getContext(), R.layout.img_fragment, null);
                    ImageView iv =  view.findViewById(R.id.hxxq_img);
                    Glide.with(MyApplication.getInstance()).load(img).into(iv);
                    container.addView(view);
                }
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
    }

    private void initComment(){
        recyclerComment.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerComment.setNestedScrollingEnabled(false);
    }

    @Override
    public void initData() {

    }

}
