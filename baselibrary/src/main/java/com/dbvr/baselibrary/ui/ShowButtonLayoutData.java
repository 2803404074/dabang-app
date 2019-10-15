package com.dbvr.baselibrary.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.dbvr.baselibrary.R;

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
        public void clickListener(View v,String txt, double lot, double lat, boolean isCheck);
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
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //String tag = (String) v.getTag();

                    if(data.get(finalI) instanceof String){
                        mListener.clickListener(v,view.getText().toString(),0,0,view.isChecked());
                    }
                    if(view.isChecked()){
                        view.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    }else {
                        view.setTextColor(context.getResources().getColor(R.color.black));
                    }
                    //getHttp(tag);
                }
            });
            layout.addView(view);
        }
    }


    public void setData2() {
        CheckBox views[] = new CheckBox[data.size()];
        //热门数据源
        for (int i = 0; i < data.size(); i++) {
            final CheckBox view = (CheckBox) LayoutInflater.from(context).inflate(R.layout.item_search, layout, false);
            if (data.get(i) instanceof String){
                view.setText((String)data.get(i));
                view.setTag(i);
            }
            views[i] = view;
            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //String tag = (String) v.getTag();

                    if(data.get(finalI) instanceof String){
                        mListener.clickListener(v,view.getText().toString(),0,0,view.isChecked());
                    }
                    if(view.isChecked()){
                        view.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    }else {
                        view.setTextColor(context.getResources().getColor(R.color.black));
                    }
                    //getHttp(tag);
                }
            });
            layout.addView(view);
        }
    }
}
