package com.dbvr.baselibrary.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.dbvr.baselibrary.R;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Created by luhuas on 2017/9/20.
 */

public class RowView extends FrameLayout {

    private TextView mRow_title;
    private ImageView mRow_image;
    private RelativeLayout mRl_ma;
    private String mTitleMsg;
    private int miamg;


    public RowView(@NonNull Context context) {
        this(context, null);
    }

    public RowView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RowView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MoveView, defStyleAttr, 0);
        if (array != null) {
            mTitleMsg = array.getString(R.styleable.MoveView_move_title);
            miamg = array.getResourceId(R.styleable.MoveView_move_image, -1);
            array.recycle();
        }

        View view = View.inflate(context, R.layout.row_view, this);
        mRow_title = view.findViewById(R.id.row_title);
        mRow_image = view.findViewById(R.id.row_image);
        mRl_ma = view.findViewById(R.id.rl_ma);
        try {
            mRow_title.setText(mTitleMsg + "");
            mRow_image.setImageDrawable(context.getResources().getDrawable(miamg));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {

        return mRow_title.getText().toString().trim();
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mRow_title.setText(title);
        }
    }

}

