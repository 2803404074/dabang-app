package com.dabangvr.fragment.other;

import android.content.Context;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.application.MyApplication;
import com.dbvr.baselibrary.model.TagMo;
import com.dbvr.baselibrary.view.BaseFragment;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * 纯列表的fragment
 */
public class UserPersonalFragment extends BaseFragment {

    @BindView(R.id.recycler_head)
    RecyclerView recyclerView;

    private List<String>mData = new ArrayList<>();
    private RecyclerAdapter adapter;

    @Override
    public int layoutId() {
        return R.layout.recy_no_bg;
    }

    @Override
    public void initView() {
        for (int i = 0; i < 10; i++) {
            mData.add("");
        }

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter = new RecyclerAdapter<String>(getContext(),mData,R.layout.item_user_personal) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, String o) {

            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {
        Map<String,Object> map = new HashMap<>();
        map.put("page",1);
        map.put("limit",20);
        //获取标签
        OkHttp3Utils.getInstance(MyApplication.getInstance()).doPostJson(DyUrl.getChannelMenuList, map, new ObjectCallback<String>(MyApplication.getInstance()) {
            @Override
            public void onUi(String result)throws JSONException {
                JSONObject object = new JSONObject(result);
                String records = object.optString("records");
                List<TagMo> list = new Gson().fromJson(records, new TypeToken<List<TagMo>>() {}.getType());
                if (list!=null && list.size()>0){
//                    mData = list;
                }
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }
}
