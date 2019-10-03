package com.dabangvr.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dabangvr.R;
import com.dbvr.baselibrary.model.TestLive;

public class RoomFragment extends Fragment {

    private View view;
    private TestLive testLive;

    private TextView tvName;
    private TextView tvHot;
    public static RoomFragment newInstance() {
        Bundle args = new Bundle();
        RoomFragment fragment = new RoomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_room, container, false);
        tvName = view.findViewById(R.id.auth_name);
        tvHot  = view.findViewById(R.id.tvHots);
        Log.e("hahaha","执行onCreateView");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.e("hahaha","执行onViewCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("hahaha","执行onResume");
    }
    public void updata(TestLive testLive){
        if (view != null){
            tvName.setText(testLive.getName());
            tvHot.setText(testLive.getHost());
        }
    }
}