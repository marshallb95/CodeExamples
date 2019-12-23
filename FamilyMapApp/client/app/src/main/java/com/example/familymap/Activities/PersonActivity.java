package com.example.familymap.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.familymap.Model.DataSingleton;
import com.example.familymap.Model.Event;
import com.example.familymap.Model.Person;
import com.example.familymap.PersonExpandableListAdapter;
import com.example.familymap.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class PersonActivity extends AppCompatActivity {
    private static final String TAG = "personActivity";
    private ArrayList<Event> personEvents;
    PersonExpandableListAdapter listAdapter;
    ExpandableListView listView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    HashMap<Integer,String> eventIDMap;
    HashMap<Integer,String> personIDMap;
    DataSingleton singleton = DataSingleton.getInstance();

    TextView mFirstName;
    TextView mLastName;
    TextView mGender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person);

        Bundle b = getIntent().getExtras();
        String personID = b.getString("personID");
        Person person = singleton.retrievePerson(personID);

        mFirstName = (TextView) findViewById(R.id.person_firstname);
        mFirstName.setText(person.getFirstName());

        mLastName = (TextView) findViewById(R.id.person_lastname);
        mLastName.setText(person.getLastName());

        mGender = (TextView) findViewById(R.id.person_gender);
        if(person.getGender().toLowerCase().equals("m")) {
            mGender.setText("Male");
        }
        else {
            mGender.setText("Female");
        }
        listView = (ExpandableListView) findViewById(R.id.personExpandableListView);
        Log.d(TAG,"List view was made");
        prepareListData();
        Log.d(TAG,"AFter prepare list data");
        listAdapter = new PersonExpandableListAdapter(this,listDataHeader,listDataChild,personIDMap,eventIDMap);
        Log.d(TAG,"AFter list adapter constructor");
        listView.setAdapter(listAdapter);
        Log.d(TAG,"After list view adapter setter");
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String type = listDataHeader.get(groupPosition);
                Log.d(TAG,"A child was clicked");
                Log.d(TAG,"type: " + type);
                System.out.println(type);
                if(type.equals("Life Events")) {
                    System.out.println("Event clicked");
                    Intent intent = new Intent(PersonActivity.this , EventActivity.class);
                    Bundle b = new Bundle();
                    b.putString("eventID",eventIDMap.get(childPosition));
                    intent.putExtras(b);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(PersonActivity.this , PersonActivity.class);
                    Bundle b = new Bundle();
                    b.putString("personID",personIDMap.get(childPosition));
                    intent.putExtras(b);
                    startActivity(intent);
                }
                return false;
            }
        });
        Log.d(TAG,"after onclick");

    }

    private void prepareListData() {
        Bundle extras = getIntent().getExtras();
        String personID = extras.getString("personID");
        Log.d(TAG,"in prepare list data");
        this.listDataHeader = new ArrayList<String>();
        this.listDataChild = new HashMap<String,List<String>>();
        listDataHeader.add("Life Events");
        listDataHeader.add("Family");
        //first we get the events related to this person, then add their relavant info
        HashSet<Event> filteredEvents = singleton.filterEvents();
        personEvents = singleton.retrievePersonEvents(personID);
        //add events  to life events
        List<String> lifeEvents = new ArrayList<String>();
        eventIDMap = new HashMap<Integer,String>();
        personIDMap = new HashMap<Integer,String>();
        int count = 0;
        for(Event event: personEvents) {
            if(filteredEvents.contains(event)) {
                String eventString = event.getEventType().toUpperCase() + ": " + event.getCity() + ", " + event.getCountry() + " (" + String.valueOf(event.getYear()) + ")";
                eventIDMap.put(count,event.getEventID());
                lifeEvents.add(eventString);
                count += 1;
            }
        }
        List<String> family = new ArrayList<String>();
        Person person = singleton.retrievePerson(personID);
        Person relative;
        String personString;
        count = 0;
        if(person.getMotherID() != null) {
            relative = singleton.retrievePerson(person.getMotherID());
            personString = relative.getFirstName() + " " + relative.getLastName() + "\nMother";
            personIDMap.put(count,relative.getPersonID());
            family.add(personString);
            count += 1;
        }
        if(person.getFatherID() != null) {
            relative = singleton.retrievePerson(person.getFatherID());
            personString = relative.getFirstName() + " " + relative.getLastName() + "\nFather";
            personIDMap.put(count,relative.getPersonID());
            family.add(personString);
            count += 1;
        }
        if(person.getSpouseID() != null) {
            relative = singleton.retrievePerson(person.getSpouseID());
            personString = relative.getFirstName() + " " + relative.getLastName() + "\nSpouse";
            personIDMap.put(count,relative.getPersonID());
            family.add(personString);
            count += 1;
        }
        if(person.getChildID() != null) {
            relative = singleton.retrievePerson(person.getChildID());
            personString = relative.getFirstName() + " " + relative.getLastName() + "\nChild";
            personIDMap.put(count,relative.getPersonID());
            family.add(personString);
            count += 1;
        }

        listDataChild.put(listDataHeader.get(0),lifeEvents);
        listDataChild.put(listDataHeader.get(1),family);

        //then we get the person family and add these people to the list
    }
}
