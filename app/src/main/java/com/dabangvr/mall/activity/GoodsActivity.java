package com.dabangvr.mall.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapter;
import com.dabangvr.comment.application.MyApplication;
import com.dabangvr.databinding.ActivityGoodsBinding;
import com.dabangvr.mall.GoodsWebViewClient;
import com.dabangvr.mall.adapter.ViewPagerTransform;
import com.dabangvr.user.activity.UserAddressActivity;
import com.dbvr.baselibrary.model.GoodsProductVo;
import com.dbvr.baselibrary.model.GoodsVo;
import com.dbvr.baselibrary.model.LiveGoods;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.MyWebViewClient;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivityBinding;
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

import butterknife.BindView;

public class GoodsActivity extends BaseActivityBinding<ActivityGoodsBinding> {

    private RecyclerAdapter adapter;
    private GoodsVo goodsVo = new GoodsVo();
    private boolean isCollect = false;
    @Override
    public int setLayout() {
        return R.layout.activity_goods;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private int addressId;
    private int productId;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100){
            if(resultCode == 101){
                String addressStr = data.getStringExtra("str");
                addressId = data.getIntExtra("addressId",0);
                mBinding.tvAddress.setText(addressStr);
            }
        }
    }

    @Override
    public void initView() {
        mBinding.ivBack.setOnClickListener((view)-> AppManager.getAppManager().finishActivity(this));

        //去设置地址
        mBinding.llAddress.setOnClickListener((view)->{
            Map<String,Object>map = new HashMap<>();
            map.put("isGoods",true);
            goTActivityForResult(UserAddressActivity.class,map,100);
        });
        //收藏
        mBinding.rlCollect.setOnClickListener((view)->{
            if (isCollect){
                mBinding.cbC.setChecked(false);
                mBinding.tvCollect.setTextColor(getResources().getColor(R.color.color_8d8c8c));
                mBinding.tvCollect.setText("收藏");
            }else {
                mBinding.cbC.setChecked(false);
                mBinding.tvCollect.setTextColor(getResources().getColor(R.color.colorDb5));
                mBinding.tvCollect.setText("已收藏");
            }
            collectionFun();
        });
        //更多商品
        mBinding.rlGoods.setOnClickListener((view)->{
            isLoading(true);
            getLiveGoods();
        });
        //立即购买
        mBinding.tvBuyNow.setOnClickListener((view)->{
            buyFunction();
        });

        initProduct();//初始化产品列表
    }

    private void buyFunction() {
        if (addressId==0){
            ToastUtil.showShort(getContext(),"您漏了收货地址哟~~");
            return;
        }
        isLoading(true);
        Map<String,Object>map = new HashMap<>();
        map.put("addressId",addressId);
        if (productId != 0){
            map.put("productId",productId);
        }
        map.put("goodsId",goodsVo.getId());
        OkHttp3Utils.getInstance(getContext()).doPostJson("", map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {

                isLoading(false);
            }

            @Override
            public void onFailed(String msg) {
                isLoading(false);
            }
        });
    }

    private void collectionFun() {
        Map<String,Object>map = new HashMap<>();
        map.put("goodsId",goodsVo.getId());
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getCollectGoods, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                ToastUtil.showShort(getContext(),"已收藏");
            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(),"操作过于频繁，请稍候再试");
            }
        });
    }

    /**
     * 本直播间的相关商品
     */
    private void getLiveGoods() {
        Map<String,Object>map = new HashMap<>();
        map.put("roomId",getIntent().getStringExtra("roomId"));
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getRoomGoodsList, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                List<LiveGoods> liveGoods = new Gson().fromJson(result, new TypeToken<List<LiveGoods>>() {
                }.getType());
                if (liveGoods!=null && liveGoods.size()>0){
                    BottomDialogUtil2.getInstance(GoodsActivity.this).show2(R.layout.recy_no_bg,2, view16 -> {
                        RecyclerView recyclerGoods = view16.findViewById(R.id.recycler_head);
                        recyclerGoods = view16.findViewById(R.id.recycler_head);
                        recyclerGoods.setBackgroundResource(R.color.colorWhite);
                        recyclerGoods.setLayoutManager(new LinearLayoutManager(getContext()));
                        RecyclerAdapter goodsAdapter = new RecyclerAdapter<LiveGoods>(getContext(),liveGoods,R.layout.item_goods) {
                            @Override
                            public void convert(Context mContext, BaseRecyclerHolder holder, LiveGoods o) {
                                holder.setImageByUrl(R.id.ivContent,o.getListUrl());
                                holder.setText(R.id.tvContent,o.getName());
                                holder.setText(R.id.tvTitle,o.getTitle());
                                holder.setText(R.id.tvPrice,o.getRetailPrice());
                            }
                        };
                        recyclerGoods.setAdapter(goodsAdapter);
                        goodsAdapter.setOnItemClickListener((view17, position) -> {
                            getGoods(liveGoods.get(position).getId());
                            BottomDialogUtil2.getInstance(GoodsActivity.this).dess();
                        });
                    });
                }else {
                    ToastUtil.showShort(getContext(),"暂无相关商品");
                }
                isLoading(false);
            }
            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(),"暂无相关商品");
                isLoading(false);
            }
        });
    }


    private void initGoodsMess(String address) {
        mBinding.setGoodsVo(goodsVo);
        adapter.updateDataa(goodsVo.getGoodsProductList());
        mBinding.tvAddress.setText(StringUtils.isEmpty(address)?"未设置收货地址":address);
        //轮播图
        initViewPage(goodsVo.getGoodsImgList());

        //详情
        mBinding.mWebView.getSettings().setJavaScriptEnabled(true);
        mBinding.mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存
        mBinding.mWebView.getSettings().setUserAgentString(System.getProperty("http.agent"));
        mBinding.mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//把html中的内容放大webview等宽的一列中
        mBinding.mWebView.getSettings().setAppCacheEnabled(true);
        mBinding.mWebView.getSettings().setDomStorageEnabled(true);
        mBinding.mWebView.setWebViewClient(new GoodsWebViewClient(mBinding.mWebView));
        mBinding.mWebView.loadData(goodsVo.getGoodsDesc(), "text/html", "UTF-8");

        isCollect = goodsVo.isCollect();
        //是否已经收藏
        if(isCollect){
            mBinding.cbC.setChecked(true);
            mBinding.tvCollect.setTextColor(getResources().getColor(R.color.colorDb5));
            mBinding.tvCollect.setText("已收藏");
        }else {
            mBinding.cbC.setChecked(false);
            mBinding.tvCollect.setTextColor(getResources().getColor(R.color.color_8d8c8c));
            mBinding.tvCollect.setText("收藏");
        }
    }

    private void initViewPage(List<String>imgList) {
        if (imgList== null){
            imgList = new ArrayList<>();
            if (!StringUtils.isEmpty(goodsVo.getListUrl())){
                imgList.add(goodsVo.getListUrl());
            }
        }
        imgList.add(goodsVo.getListUrl());
        imgList.add(goodsVo.getListUrl());
        mBinding.viewpager.setPageMargin(20);
        List<String> finalImgList = imgList;
        mBinding.viewpager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return finalImgList.size();
            }
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                View view = null;
                String img = finalImgList.get(position);
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

    private void initProduct(){
        if (goodsVo.getGoodsProductList()==null){
            List<GoodsProductVo>list = new ArrayList<>();
            goodsVo.setGoodsProductList(list);
        }
        mBinding.recycleProduct.setLayoutManager(new GridLayoutManager(getContext(),3));
        mBinding.recycleProduct.setNestedScrollingEnabled(false);
        adapter = new RecyclerAdapter<GoodsProductVo>(getContext(),goodsVo.getGoodsProductList(),R.layout.item_txt) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, GoodsProductVo o) {
                TextView textView = holder.getView(R.id.cb_txt);
                textView.setText(o.getProductName());
                if (o.isCheck()){
                    textView.setBackgroundResource(R.drawable.shape_red);
                    textView.setTextColor(getResources().getColor(R.color.colorWhite));
                    mBinding.tvProductPrice.setText(o.getRetailPrice());
                    mBinding.tvProduct.setText(o.getProductName());
                    mBinding.tvKc.setText(o.getRemainingInventory()+"件");
                    productId = o.getId();
                }else {
                    textView.setBackgroundResource(R.drawable.shape_gray_w);
                    textView.setTextColor(getResources().getColor(R.color.textTitle));
                }

            }
        };
        mBinding.recycleProduct.setAdapter(adapter);

        adapter.setOnItemClickListener((view, position) -> {
            for (int i = 0; i < goodsVo.getGoodsProductList().size(); i++) {
                if (i==position){
                    goodsVo.getGoodsProductList().get(i).setCheck(true);
                }else {
                    goodsVo.getGoodsProductList().get(i).setCheck(false);
                }
            }
            adapter.updateDataa(goodsVo.getGoodsProductList());
        });
    }

    @Override
    public void initData() {
       getGoods(getIntent().getIntExtra("goodsId",0));
    }

    private void getGoods(int goodsId){
        Map<String,Object>map = new HashMap<>();
        map.put("goodsId",goodsId);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getGoodsDetails, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                JSONObject object = new JSONObject(result);
                String address = object.optString("address");
                addressId = object.optInt("addressId");
                String str = object.optString("goodsVo");
                goodsVo = new Gson().fromJson(str, GoodsVo.class);
                if (goodsVo!=null){
                    List<GoodsProductVo>list = new ArrayList<>();
                    for (int i = 0; i < 5; i++) {
                        GoodsProductVo vo = new GoodsProductVo();
                        if (i==0){
                            vo.setCheck(true);
                        }
                        vo.setProductName("产品"+i);
                        vo.setRemainingInventory(10+i);
                        vo.setRetailPrice("5"+i*2);
                        list.add(vo);
                    }
                    goodsVo.setGoodsProductList(list);

                    initGoodsMess(address);
                }else {
                    ToastUtil.showShort(getContext(),"商品已下架");
                }
            }
            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(),StringUtils.isEmptyTxt(msg));
            }
        });
    }
}
