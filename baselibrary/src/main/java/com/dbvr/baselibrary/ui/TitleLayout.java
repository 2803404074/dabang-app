package com.dbvr.baselibrary.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dbvr.baselibrary.R;

/**
 * 使用
 *
 *  <TitleLayout
 *                 android:id="@+id/title"
 *                 android:layout_width="match_parent"
 *                 android:layout_height="45dp"
 *                 app:name="@string/tx_publishing">
 *
 *                 <Button
 *                     android:id="@+id/go_back"
 *                     android:layout_width="40dp"
 *                     android:layout_height="40dp"
 *                     android:layout_centerVertical="true"
 *                     android:layout_marginLeft="10dp"
 *                     android:background="@mipmap/ic_round_left" />
 *
 *             </TitleLayout>
 *
 */
public class TitleLayout extends FrameLayout {
    private TextView titleView;

    public TitleLayout(@NonNull Context context) {
        super(context);
    }

    public TitleLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TitleLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = inflate(context, R.layout.activity_title, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.title);
        titleView = view.findViewById(R.id.txt_title);
        String title = typedArray.getString(R.styleable.title_name);
        titleView.setText(title);
    }

    @NonNull
    public void setTitleView(TextView titleView) {
        this.titleView = titleView;
    }
}
