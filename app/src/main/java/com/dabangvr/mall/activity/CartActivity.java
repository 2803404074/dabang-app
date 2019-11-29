package com.dabangvr.mall.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dabangvr.R;
import com.dabangvr.comment.application.MyApplication;
import com.dabangvr.mall.adapter.CartAdapter;
import com.dbvr.baselibrary.model.ShoppingCarDataBean;
import com.dbvr.baselibrary.ui.RoundCornerDialog;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 个人中心-我的购物车
 */
public class CartActivity extends BaseActivity implements CartAdapter.SendServerCallback {

    @BindView(R.id.tv_titlebar_center)
    TextView tvTitlebarCenter;
    @BindView(R.id.tv_titlebar_right)
    TextView tvTitlebarRight;
    @BindView(R.id.elv_shopping_car)
    ExpandableListView elvShoppingCar;

    @BindView(R.id.iv_select_all)
    ImageView ivSelectAll;

    @BindView(R.id.ll_select_all)
    LinearLayout llSelectAll;
    @BindView(R.id.btn_order)
    Button btnOrder;
    @BindView(R.id.btn_delete)
    Button btnDelete;

    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.rl)
    RelativeLayout rl;

    @BindView(R.id.rl_total_price)
    RelativeLayout rlTotalPrice;

    @BindView(R.id.iv_no_contant)
    ImageView ivNoContant;

    @BindView(R.id.rl_no_contant)
    RelativeLayout rlNoContant;

    private List<ShoppingCarDataBean.DatasBean> datas;
    private CartAdapter shoppingCarAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.my_cart_activity;
    }


    @Override
    public void initView() {
        ivNoContant.setVisibility(View.GONE);
        initExpandableListView();
    }

    @Override
    public void initData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("page", "1");
        map.put("limit", "10");
        OkHttp3Utils.getInstance(MyApplication.getInstance()).doPostJson(DyUrl.getGoods2CartList, map, new ObjectCallback<String>(MyApplication.getInstance()) {
            @Override
            public void onUi(String result) throws JSONException {
                try {
                    if (!TextUtils.isEmpty(result)) {
                        ShoppingCarDataBean bean = new Gson().fromJson(result, ShoppingCarDataBean.class);
                        datas = bean.getData().getGoods2CartList();
                        initExpandableListViewData(datas);
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String msg) {
            }
        });

    }


    /**
     * 初始化ExpandableListView
     * 创建数据适配器adapter，并进行初始化操作
     */
    private void initExpandableListView() {
        shoppingCarAdapter = new CartAdapter(getContext(), getToken(), llSelectAll, ivSelectAll, btnOrder, btnDelete, rlTotalPrice, tvTotalPrice);

        shoppingCarAdapter.setSendServerCallback(this);

        elvShoppingCar.setAdapter(shoppingCarAdapter);

        //删除的回调
        shoppingCarAdapter.setOnDeleteListener(new CartAdapter.OnDeleteListener() {
            @Override
            public void onDelete() {
                initDelete();
            }
        });

        //修改商品数量的回调
        shoppingCarAdapter.setOnChangeCountListener(new CartAdapter.OnChangeCountListener() {
            @Override
            public void onChangeCount(String goods_id, int count) {
                /**
                 * 实际开发中，在此请求修改商品数量的接口，商品数量修改成功后，
                 * 通过initExpandableListViewData（）方法刷新购物车数据。
                 */
                // TODO: 2019/10/21 token 加入方式修改
                HashMap<String, Object> map = new HashMap<>();

                map.put("cartId", goods_id);
                map.put("number", String.valueOf(count));

                OkHttp3Utils.getInstance(MyApplication.getInstance()).doPostJson(DyUrl.updateNumber2Cart, map, new ObjectCallback<String>(MyApplication.getInstance()) {
                    @Override
                    public void onUi(String result) throws JSONException {
                        //修改訂單數量成功
                    }

                    @Override
                    public void onFailed(String msg) {
                        //修改訂單數量失敗
                    }
                });

            }
        });
    }

    /**
     * 初始化ExpandableListView的数据
     * 并在数据刷新时，页面保持当前位置
     *
     * @param datas 购物车的数据
     */
    private void initExpandableListViewData(List<ShoppingCarDataBean.DatasBean> datas) {


        if (datas != null && datas.size() > 0) {
            //刷新数据时，保持当前位置
            shoppingCarAdapter.setData(datas);

            //使所有组展开
            for (int i = 0; i < shoppingCarAdapter.getGroupCount(); i++) {
                elvShoppingCar.expandGroup(i);
            }

            //使组点击无效果
            elvShoppingCar.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {
                    return true;
                }
            });

            tvTitlebarRight.setVisibility(View.VISIBLE);
            tvTitlebarRight.setText("编辑");
            rlNoContant.setVisibility(View.GONE);
            elvShoppingCar.setVisibility(View.VISIBLE);
            rl.setVisibility(View.VISIBLE);
            rlTotalPrice.setVisibility(View.VISIBLE);
            btnOrder.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.GONE);
        } else {
            tvTitlebarRight.setVisibility(View.GONE);
            rlNoContant.setVisibility(View.VISIBLE);
            elvShoppingCar.setVisibility(View.GONE);
            rl.setVisibility(View.GONE);
        }
    }

    /**
     * 判断是否要弹出删除的dialog
     * 通过bean类中的DatasBean的isSelect_shop属性，判断店铺是否被选中；
     * GoodsBean的isSelect属性，判断商品是否被选中，
     */
    private void initDelete() {
        //判断是否有店铺或商品被选中
        //true为有，则需要刷新数据；反之，则不需要；
        boolean hasSelect = false;
        //创建临时的List，用于存储没有被选中的购物车数据
        List<ShoppingCarDataBean.DatasBean> datasTemp = new ArrayList<>();

        List<String> isSelectCartId = new ArrayList<>();

        for (int i = 0; i < datas.size(); i++) {
            List<ShoppingCarDataBean.DatasBean.GoodsBean> goods = datas.get(i).getGcvList();
            boolean isSelect_shop = datas.get(i).getIsSelect_shop();
            for (int y = 0; y < goods.size(); y++) {
                ShoppingCarDataBean.DatasBean.GoodsBean goodsBean = goods.get(y);
                boolean isSelect = goodsBean.getIsSelect();
                if (isSelect) {
                    isSelectCartId.add(String.valueOf(goodsBean.getId()));
                }
            }

            if (isSelect_shop) {
                hasSelect = true;
                //跳出本次循环，继续下次循环。
                continue;
            } else {
                datasTemp.add(datas.get(i));
                datasTemp.get(datasTemp.size() - 1).setGcvList(new ArrayList<ShoppingCarDataBean.DatasBean.GoodsBean>());
            }

            for (int y = 0; y < goods.size(); y++) {
                ShoppingCarDataBean.DatasBean.GoodsBean goodsBean = goods.get(y);
                boolean isSelect = goodsBean.getIsSelect();

                if (isSelect) {
                    hasSelect = true;
                } else {
                    datasTemp.get(datasTemp.size() - 1).getGcvList().add(goodsBean);
                }
            }
        }

        if (hasSelect) {
            showDeleteDialog(datasTemp, isSelectCartId);
        } else {
            ToastUtil.showShort(getContext(), "请选择要删除的商品");
        }
    }

    /**
     * 展示删除的dialog（可以自定义弹窗，不用删除即可）
     *
     * @param datasTemp
     */
    private void showDeleteDialog(final List<ShoppingCarDataBean.DatasBean> datasTemp, final List<String> cartIdArr) {
        View view = View.inflate(getContext(), R.layout.dialog_two_btn, null);
        final RoundCornerDialog roundCornerDialog = new RoundCornerDialog(getContext(), 0, 0, view, R.style.RoundCornerDialog);
        roundCornerDialog.show();
        roundCornerDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        roundCornerDialog.setOnKeyListener(keylistener);//设置点击返回键Dialog不消失

        TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
        TextView tv_logout_confirm = (TextView) view.findViewById(R.id.tv_logout_confirm);
        TextView tv_logout_cancel = (TextView) view.findViewById(R.id.tv_logout_cancel);
        tv_message.setText("确定要删除商品吗？");

        //确定
        tv_logout_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundCornerDialog.dismiss();

                for (int i = 0; i < cartIdArr.size(); i++) {
                    System.out.println("删除了:" + cartIdArr.get(i));
                }

                //请求后端
                sendServer(datasTemp, cartIdArr);

            }
        });
        //取消
        tv_logout_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundCornerDialog.dismiss();
            }
        });
    }

    private void sendServer(final List<ShoppingCarDataBean.DatasBean> datasTemp, List<String> cartIdArr) {
        Map<String, Object> map = new HashMap<>();
        // TODO: 2019/10/21 原來是list轉string
//        map.put("cartIds", JsonUtil.obj2String(cartIdArr));
        map.put("cartIds", cartIdArr);


        OkHttp3Utils.getInstance(MyApplication.getInstance()).doPostJson(DyUrl.delete2Cart, map, new ObjectCallback<String>(MyApplication.getInstance()) {
            @Override
            public void onUi(String result) throws JSONException {
                //修改訂單數量成功
                datas = datasTemp;
                initExpandableListViewData(datas);
            }

            @Override
            public void onFailed(String msg) {
                //修改訂單數量失敗
            }
        });
    }

    DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                return true;
            } else {
                return false;
            }
        }
    };

    @OnClick({R.id.tv_titlebar_left, R.id.tv_titlebar_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_titlebar_left:
                AppManager.getAppManager().finishActivity(this);
                break;
            case R.id.tv_titlebar_right://编辑
                String edit = tvTitlebarRight.getText().toString().trim();
                if (edit.equals("编辑")) {
                    tvTitlebarRight.setText("完成");
                    rlTotalPrice.setVisibility(View.GONE);
                    btnOrder.setVisibility(View.GONE);
                    btnDelete.setVisibility(View.VISIBLE);
                } else {
                    tvTitlebarRight.setText("编辑");
                    rlTotalPrice.setVisibility(View.VISIBLE);
                    btnOrder.setVisibility(View.VISIBLE);
                    btnDelete.setVisibility(View.GONE);
                }
                break;

        }
    }


    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }
}
