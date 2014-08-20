package com.lishier.app.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.lishier.app.MainActivity;
import com.lishier.app.R;


/**
 * Created by longjianlin on 14-7-30.
 * V 1.0
 * *********************************
 * Desc: 左侧menu
 * *********************************
 */
public class LeftMenuFragment extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] colors = getResources().getStringArray(R.array.color_names);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity()
                , android.R.layout.simple_list_item_1, android.R.id.text1, colors);
        setListAdapter(adapter);
    }

    /**
     * list item click
     *
     * @param l
     * @param v
     * @param position
     * @param id
     */
    public void onListItemClick(android.widget.ListView l, View v, int position, long id) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).switchContent(null);
        }
    }
}
