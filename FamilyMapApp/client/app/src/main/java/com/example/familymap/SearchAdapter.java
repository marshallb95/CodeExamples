package com.example.familymap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.familymap.Model.DataSingleton;
import com.example.familymap.Model.Event;
import com.example.familymap.Model.Person;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.HashMap;
import java.util.List;

public class SearchAdapter extends ArrayAdapter<String> {
    private HashMap<Integer,String> objectIDMap;
    private HashMap<Integer,String> objectTypeMap;
    private DataSingleton singleton;

    public SearchAdapter(@NonNull Context context, int resource, @NonNull List<String> objects, HashMap<Integer,String> objectIDMap, HashMap<Integer,String> objectTypeMap)  {
        super(context, resource, objects);
        this.objectIDMap = objectIDMap;
        this.objectTypeMap = objectTypeMap;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String childText = getItem(position);
        Drawable icon;
        String type = objectTypeMap.get(position);
        if(type.equals("Event")) {
            Event event = singleton.retrieveEvent(objectIDMap.get(position));
            if(event.getEventType().toLowerCase().equals("birth")) {
                icon = new IconDrawable(getContext(), FontAwesomeIcons.fa_map_marker).colorRes(R.color.birth).sizeDp(30);
            }
            else if(event.getEventType().toLowerCase().equals("marriage")) {
                icon = new IconDrawable(getContext(), FontAwesomeIcons.fa_map_marker).colorRes(R.color.marriage).sizeDp(30);
            }
            else if(event.getEventType().toLowerCase().equals("death")) {
                icon = new IconDrawable(getContext(),FontAwesomeIcons.fa_map_marker).colorRes(R.color.death).sizeDp(30);
            }
            else {
                icon = new IconDrawable(getContext(),FontAwesomeIcons.fa_map_marker).colorRes(R.color.other).sizeDp(30);
            }
        }
        else {
            Person person = singleton.retrievePerson(objectIDMap.get(position));
            if(person.getGender().toLowerCase().equals("m")) {
                icon = new IconDrawable(getContext(),FontAwesomeIcons.fa_male).colorRes(R.color.male_icon).sizeDp(30);
            }
            else {
                icon = new IconDrawable(getContext(),FontAwesomeIcons.fa_female).colorRes(R.color.female_icon).sizeDp(30);
            }
        }
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.search_item,null);
        }
        TextView txtListChild = (TextView) convertView.findViewById((R.id.search_item_view));
        txtListChild.setText(childText);
        txtListChild.setCompoundDrawablesRelativeWithIntrinsicBounds(icon,null,null,null);
        return convertView;
    }
}
