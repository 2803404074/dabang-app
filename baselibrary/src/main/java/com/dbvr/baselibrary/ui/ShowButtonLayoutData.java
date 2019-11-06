package com.dbvr.baselibrary.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.dbvr.baselibrary.R;
import com.dbvr.baselibrary.model.HomeFindMo;

import java.util.List;

public class ShowButtonLayoutData<T> {
    private Context context;
    private ShowButtonLayout layout;
    private List<T> data;
    private MyClickListener mListener;

    public ShowButtonLayoutData(Context context, ShowButtonLayout layout, List<T> data, MyClickListener mListener) {
        this.context = context;
        this.layout = layout;
        this.data = data;
        this.mListener = mListener;
    }

    //自定义接口，用于回调按钮点击事件到Activity
    public interface MyClickListener{
        void clickListener(View v,String txt,int position, double lot, double lat, boolean isCheck);
    }


    private int drawableBg = 0;

    public void setDrawableBg(int drawableBg) {
        this.drawableBg = drawableBg;
    }

    public void setData() {
        if (drawableBg == 0){
            drawableBg = R.drawable.shape_db_search;
        }
        CheckBox views[] = new CheckBox[data.size()];
        //热门数据源
        for (int i = 0; i < data.size(); i++) {
            final CheckBox view = (CheckBox) LayoutInflater.from(context).inflate(R.layout.item_search, layout, false);
            view.setBackgroundResource(drawableBg);
            if (data.get(i) instanceof String){
                view.setText((String)data.get(i));
                view.setTag(data.get(i));
            }

            views[i] = view;
            final int finalI = i;
            view.setOnClickListener(v -> {
                //String tag = (String) v.getTag();
                if(data.get(finalI) instanceof String){
                    mListener.clickListener(v,view.getText().toString(),finalI,0,0,view.isChecked());
                }
                if(view.isChecked()){
                    view.setTextColor(context.getResources().getColor(R.color.colorWhite));
                }else {
                    view.setTextColor(context.getResources().getColor(R.color.black));
                }
                //getHttp(tag);
            });
            layout.addView(view);
        }
    }


    private int position = 0;
    public void setDataType() {
        if (drawableBg == 0){
            drawableBg = R.drawable.shape_db_search;
        }
        CheckBox views[] = new CheckBox[data.size()];
        //热门数据源
        for (int i = 0; i < data.size(); i++) {
            final CheckBox view = (CheckBox) LayoutInflater.from(context).inflate(R.layout.item_search, layout, false);
            view.setBackgroundResource(drawableBg);
            if (data.get(i) instanceof HomeFindMo.FourMo){
                HomeFindMo.FourMo fourMo = (HomeFindMo.FourMo) data.get(i);
                view.setText(fourMo.getName());
                view.setTag(fourMo.getId());
            }

            views[i] = view;
            final int finalI = i;
            view.setOnClickListener(v -> {
                this.position = finalI;
                //String tag = (String) v.getTag();
                if(data.get(finalI) instanceof HomeFindMo.FourMo){
                    mListener.clickListener(v,view.getText().toString(),position,0,0,view.isChecked());
                }
                if(view.isChecked()){
                    view.setTextColor(context.getResources().getColor(R.color.colorWhite));
                }else {
                    view.setTextColor(context.getResources().getColor(R.color.black));
                }
                //getHttp(tag);
            });
            layout.addView(view);
        }
    }
}
