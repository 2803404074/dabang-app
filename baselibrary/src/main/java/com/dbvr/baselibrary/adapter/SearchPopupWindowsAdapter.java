package com.dbvr.baselibrary.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.dbvr.baselibrary.R;
import com.dbvr.baselibrary.model.Search;


/**
 * Created by HZF on 2017/5/16.
 */

public class SearchPopupWindowsAdapter extends CommonBaseAdapter<Search> {

    public SearchPopupWindowsAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.item_list_selector, viewGroup, false);
        if (view != null) {
            RelativeLayout relativeLayout = view.findViewById(R.id.relativeLayout_search);
            TextView textView = view.findViewById(R.id.textView_listView_selector);
            final ImageView imageView = view.findViewById(R.id.image_search_check);
            imageView.setImageResource(itemList.get(i).isChecked() ? R.drawable.icon_selected : R.drawable.arc_bgo_white);
            textView.setText(itemList.get(i).getKeyWord());
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemList.get(i).setChecked(itemList.get(i).isChecked() ? false : true);
                    imageView.setImageResource(itemList.get(i).isChecked() ? R.drawable.icon_selected : R.drawable.arc_bgo_white);

                }
            });
        }
        return view;
    }

}