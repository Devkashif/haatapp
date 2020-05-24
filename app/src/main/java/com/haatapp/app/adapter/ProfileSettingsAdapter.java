package com.haatapp.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haatapp.app.R;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 23-08-2017.
 */

public class ProfileSettingsAdapter extends BaseAdapter {

    private static final String LOG_TAG = ProfileSettingsAdapter.class.getSimpleName();
    private List<Integer> listIcon;
    private Context context_;
    private List<String> items;

    public ProfileSettingsAdapter(Context context, List<String> items, List<Integer> listIcon) {
        this.context_ = context;
        this.items = items;
        this.listIcon = listIcon;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context_.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.profile_settings_list_item, null);
        }

        ImageView setting_icon = (ImageView) convertView.findViewById(R.id.setting_icon);
        TextView tv = (TextView) convertView.findViewById(R.id.setting_label);
        setting_icon.setImageResource(listIcon.get(position));
        tv.setText(items.get(position));

        //Load the animation from the xml file and set it to the row
        Animation animation = AnimationUtils.loadAnimation(context_, R.anim.anim_push_left_in);
        animation.setDuration(400);
        convertView.startAnimation(animation);

        return convertView;
    }
}