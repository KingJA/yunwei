package com.tdr.yunwei.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {
	public Activity mActivity;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return initViews();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initData();
	}

	public abstract View initViews();

	public void initData() {

	}
	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

}
