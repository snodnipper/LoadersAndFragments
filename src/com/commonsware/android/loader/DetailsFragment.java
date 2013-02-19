package com.commonsware.android.loader;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsFragment extends Fragment {
	
	public static final String KEY_DETAILS = "keyDetails";
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		TextView textview = (TextView) getView().findViewById(R.id.details_here);
		Bundle args = getArguments();
		textview.setText(args.getString(KEY_DETAILS));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.details_fragment, null);
	}

}
