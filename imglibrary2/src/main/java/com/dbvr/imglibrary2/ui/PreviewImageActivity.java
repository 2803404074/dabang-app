package com.dbvr.imglibrary2.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.dbvr.imglibrary2.R;
import com.dbvr.imglibrary2.model.Image;
import com.dbvr.imglibrary2.widget.PreViewImageView;

import java.util.List;

public class PreviewImageActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    ViewPager mViewPager;

    TextView mCount;
    private List<Image> mSelectedImages;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_preview_image;
    }

    @Override
    protected void init() {
        mViewPager = findViewById(R.id.viewPager);
        mCount = findViewById(R.id.tv_count);

        mSelectedImages = getIntent().getParcelableArrayListExtra("preview_images");
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        mCount.setText(String.format("%s/%s", 1, mSelectedImages.size()));

    }

    private PagerAdapter mViewPagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return mSelectedImages.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            PreViewImageView imageView = new PreViewImageView(container.getContext());
            Glide.with(imageView.getContext())
                    .load(mSelectedImages.get(position).getPath())
                    .into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    };


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCount.setText(String.format("%s/%s", (position + 1), mSelectedImages.size()));

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
