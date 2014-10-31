package cz.pisnickyakordy.android;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.pisnickyakordy.android.R;

public class FindPeopleFragment extends Fragment {

	public FindPeopleFragment(){}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_main, container, false);

        Intent intent = new Intent(getActivity(), ListActivity.class);
        getActivity().startActivity(intent);

        return rootView;
    }
}
