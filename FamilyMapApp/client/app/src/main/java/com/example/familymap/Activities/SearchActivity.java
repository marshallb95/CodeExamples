package com.example.familymap.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.familymap.Model.DataSingleton;
import com.example.familymap.Model.Event;
import com.example.familymap.Model.Person;
import com.example.familymap.R;
import com.example.familymap.SearchAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SearchActivity extends AppCompatActivity {
    //note that we will be searching in events and in persons
    private static final String TAG = "search";
    private DataSingleton singleton = DataSingleton.getInstance();
    private HashMap<Integer, String> objectIDMap;
    private HashMap<Integer,String> objectTypeMap;
    private TextView textView;
    private SearchView searchView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("create was called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        searchView = findViewById(R.id.search_bar);
        Log.d(TAG,"before creating query listener");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG,"this changed");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG,newText);
                setListView(newText);
                return true;
            }
        });
    }
    void setListView(String searchText) {
        Log.d(TAG,"Enter set list view");
        objectIDMap = new HashMap<Integer,String>();
        objectTypeMap = new HashMap<Integer,String>();
        HashSet<Person> searchPeople = singleton.searchPeople(searchText);
        HashSet<Event> searchEvent = singleton.searchEvents(searchText);
        System.out.println(searchPeople.size());
        System.out.println(searchEvent.size());
        //add people, then add events
        int count = 0;
        ArrayList<String> searchObjects = new ArrayList<String>();
        for(Person person: searchPeople) {
            searchObjects.add(person.getFirstName() + " "  + person.getLastName());
            objectIDMap.put(count,person.getPersonID());
            objectTypeMap.put(count,"Person");
            count += 1;
        }
        for(Event event: searchEvent) {
            String eventString = event.getEventType() + ": " + event.getCity() + ", " + event.getCountry() + "(" +
                    String.valueOf(event.getYear()) + ")\n";
            Person person = singleton.retrievePerson(event.getPersonID());
            eventString += person.getFirstName() + " " + person.getLastName();
            searchObjects.add(eventString);
            objectIDMap.put(count,event.getEventID());
            objectTypeMap.put(count,"Event");
            count += 1;
        }
        System.out.println(searchObjects);
        listView = (ListView) findViewById(R.id.searchList);
        listView.setAdapter(new SearchAdapter(this,R.layout.search_item,searchObjects,objectIDMap,objectTypeMap));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //if event
                String type = objectTypeMap.get(position);
                if(type.equals("Event")) {
                    //start event activity
                    Intent intent = new Intent(SearchActivity.this , EventActivity.class);
                    Bundle b = new Bundle();
                    b.putString("eventID",objectIDMap.get(position));
                    intent.putExtras(b);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(SearchActivity.this , PersonActivity.class);
                    Bundle b = new Bundle();
                    b.putString("personID",objectIDMap.get(position));
                    intent.putExtras(b);
                    startActivity(intent);
                }
            }
        });
        Log.d(TAG,"List view has been set");
    }
}
