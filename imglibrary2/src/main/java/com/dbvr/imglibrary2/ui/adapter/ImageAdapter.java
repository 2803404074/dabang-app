package com.dbvr.imglibrary2.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dbvr.imglibrary2.R;
import com.dbvr.imglibrary2.model.Image;
import com.dbvr.imglibrary2.ui.SelectImageActivity;
import com.dbvr.imglibrary2.widget.recyclerview.CommonRecycleAdapter;
import com.dbvr.imglibrary2.widget.recyclerview.CommonViewHolder;
import com.dbvr.imglibrary2.widget.recyclerview.MultiTypeSupport;

import java.util.List;

/**
 * Description:
 * Data：9/4/2018-3:14 PM
 *
 * @author yanzhiwen
 */
public class ImageAdapter extends CommonRecycleAdapter<Image> {
    private Context mContext;
    private onSelectImageCountListener mSelectImageCountListener;
    private List<Image> mSelectImages;

    public ImageAdapter(Context context, List<Image> images, List<Image> selectedImages, MultiTypeSupport typeSupport) {
        super(context, images, typeSupport);
        this.mContext = context;
        this.mSelectImages = selectedImages;
    }

    @Override
    protected void convert(CommonViewHolder holder, final Image image, int position) {
        if (!TextUtils.isEmpty(image.getPath())) {
            final ImageView chb_selected = holder.getView(R.id.iv_selected);
            final View maskView = holder.getView(R.id.mask);
            ImageView iv_image = holder.getView(R.id.iv_image);
            Glide.with(mContext).load(image.getPath()).into(iv_image);
            chb_selected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (image.isSelect()) {
                        image.setSelect(false);
                        mSelectImages.remove(image);
                        chb_selected.setSelected(false);
                    } else if (mSelectImages.size() < SelectImageActivity.MAX_SIZE) {
                        image.setSelect(true);
                        mSelectImages.add(image);
                        chb_selected.setSelected(true);
                        maskView.setVisibility(image.isSelect() ? View.VISIBLE : View.GONE);
                    }
                    if (mSelectImageCountListener != null) {
                        mSelectImageCountListener.onSelectImageCount(mSelectImages.size());
                        mSelectImageCountListener.onSelectImageList(mSelectImages);
                    }
                }
            });
            chb_selected.setSelected(image.isSelect());
            maskView.setVisibility(image.isSelect() ? View.VISIBLE : View.GONE);
        } else {
            holder.getView(R.id.iv_camera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnCameraClickListener != null) {
                        mOnCameraClickListener.onCameraClick();
                    }
                }
            });
        }
    }


    public void setSelectImageCountListener(onSelectImageCountListener selectImageCountListener) {
        mSelectImageCountListener = selectImageCountListener;
    }

    public interface onSelectImageCountListener {
        void onSelectImageCount(int count);

        void onSelectImageList(List<Image> images);
    }

    private onCameraClickListener mOnCameraClickListener;

    public void setOnCameraClickListener(onCameraClickListener onCameraClickListener) {
        this.mOnCameraClickListener = onCameraClickListener;
    }

    public interface onCameraClickListener {
        void onCameraClick();
    }
}
