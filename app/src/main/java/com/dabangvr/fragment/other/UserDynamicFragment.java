package com.dabangvr.fragment.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.view.BaseFragment;
import com.dbvr.imglibrary2.utils.TDevice;
import com.dbvr.imglibrary2.widget.recyclerview.SpaceGridItemDecoration;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 纯列表的fragment
 */
@SuppressLint("ValidFragment")
public class UserDynamicFragment extends BaseFragment {


    @BindView(R.id.recycler_head)
    RecyclerView recyclerView;

    private List<String> mData = new ArrayList<>();
    private RecyclerAdapter adapter;

    @Override
    public int layoutId() {
        return R.layout.recy_no_bg;
    }


    private HeadCallBack headCallBack;

    public interface HeadCallBack {
        void onclickCallBack();
    }

    public UserDynamicFragment() {
    }


    public UserDynamicFragment(HeadCallBack headCallBack) {
        this.headCallBack = headCallBack;
    }

    @Override
    public void initView() {
        for (int i = 0; i < 10; i++) {
            mData.add("");
        }
        String head = SPUtils.instance(getContext()).getUser().getHeadUrl();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 3)));
        adapter = new RecyclerAdapter<String>(getContext(), mData, R.layout.item_user_dynamic) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, String o) {
                //头像
                SimpleDraweeView simpleDraweeView = holder.getView(R.id.sdvHead);
                simpleDraweeView.setImageURI(head);
                simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (headCallBack != null) {
                            headCallBack.onclickCallBack();
                        }
                    }
                });
                RecyclerView recyclerViewx = holder.getView(R.id.recycle_img);
                recyclerViewx.setNestedScrollingEnabled(false);
                recyclerViewx.setLayoutManager(new GridLayoutManager(getContext(), 3));
                recyclerViewx.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 1)));
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 9; i++) {
                    list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1571136024322&di=9bdf31e105ad39fabe72e7ebc685e401&imgtype=0&src=http%3A%2F%2Fcdnimg103.lizhi.fm%2Faudio_cover%2F2016%2F11%2F28%2F2570760571378577415_320x320.jpg");
                }
                RecyclerAdapter adapterx = new RecyclerAdapter<String>(getContext(), list, R.layout.selected_image_item) {
                    @Override
                    public void convert(Context mContext, BaseRecyclerHolder holder, String o) {
                        holder.setImageByUrl(R.id.iv_selected_image, o);
                    }
                };
                recyclerViewx.setAdapter(adapterx);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {

    }
}
