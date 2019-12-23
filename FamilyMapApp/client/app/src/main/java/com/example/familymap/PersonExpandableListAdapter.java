package com.example.familymap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.familymap.Model.DataSingleton;
import com.example.familymap.Model.Event;
import com.example.familymap.Model.Person;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.HashMap;
import java.util.List;

public class PersonExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    DataSingleton singleton = DataSingleton.getInstance();
    //private HashMap<String, List<Person>> listPersonChild;
    //private HashMap<String, List<Event>> listEventChild;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private HashMap<Integer, String> personIDMap;
    private HashMap<Integer, String> eventIDMap;

    public PersonExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listDataChild, HashMap<Integer, String> personIDMap,HashMap<Integer, String> eventIDMap) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        //this.listPersonChild = listPersonChild;
        //this.listEventChild = listEventChild;
        this.listDataChild = listDataChild;
        this.personIDMap = personIDMap;
        this.eventIDMap = eventIDMap;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final String headerTitle = (String) getGroup(groupPosition);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group,null);
        }
        TextView txtGroupHeader = (TextView) convertView.findViewById((R.id.person_group));
        txtGroupHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        Drawable icon;
        if(listDataHeader.get(groupPosition).equals("Life Events")) {
            //set icon as blue marker
            //get the eventID lying at the eventMap position
            Event event = singleton.retrieveEvent(eventIDMap.get(childPosition));
            if(event.getEventType().toLowerCase().equals("birth")) {
                icon = new IconDrawable(context, FontAwesomeIcons.fa_map_marker).colorRes(R.color.birth).sizeDp(30);
            }
            else if(event.getEventType().toLowerCase().equals("marriage")) {
                icon = new IconDrawable(context, FontAwesomeIcons.fa_map_marker).colorRes(R.color.marriage).sizeDp(30);
            }
            else if(event.getEventType().toLowerCase().equals("death")) {
                icon = new IconDrawable(context,FontAwesomeIcons.fa_map_marker).colorRes(R.color.death).sizeDp(30);
            }
            else {
                icon = new IconDrawable(context,FontAwesomeIcons.fa_map_marker).colorRes(R.color.other).sizeDp(30);
            }
        }
        else {
            Person person = singleton.retrievePerson(personIDMap.get(childPosition));
            if(person.getGender().toLowerCase().equals("m")) {
                icon = new IconDrawable(context,FontAwesomeIcons.fa_male).colorRes(R.color.male_icon).sizeDp(30);
            }
            else {
                icon = new IconDrawable(context,FontAwesomeIcons.fa_female).colorRes(R.color.female_icon).sizeDp(30);
            }
        }

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item,null);
        }
        TextView txtListChild = (TextView) convertView.findViewById((R.id.person_item));

        txtListChild.setText(childText);
        txtListChild.setCompoundDrawablesRelativeWithIntrinsicBounds(icon,null,null,null);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
