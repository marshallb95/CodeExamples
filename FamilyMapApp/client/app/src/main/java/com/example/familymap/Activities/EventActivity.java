package com.example.familymap.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.familymap.Model.DataSingleton;
import com.example.familymap.Model.Event;
import com.example.familymap.Model.Person;
import com.example.familymap.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.HashSet;

public class EventActivity extends AppCompatActivity {
    private static final String TAG = "EventActivity";
    private GoogleMap map;
    private TextView textView;
    private DataSingleton singleton;
    int markerWidth = 20;
    ArrayList<Polyline> mapLines;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event);
        singleton = DataSingleton.getInstance();
        mapLines = new ArrayList<Polyline>();

        textView = (TextView) findViewById(R.id.event_text_map);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) textView.getTag();
                if(!tag.equals("Default")) {
                    Intent intent = new Intent(EventActivity.this, PersonActivity.class);
                    Bundle b = new Bundle();
                    b.putString("personID",tag);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            }

        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.event_map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                initMap();
            }
        });
    }
    public void onResume() {
        Log.d(TAG,"on resume was called");
        super.onResume();
        if(map != null) {
            initMap();
        }
    }
    public void initMap() {
        Log.d(TAG,"initializing");
        map.getUiSettings().setZoomControlsEnabled(true);
        addMarkers();
        setMarkerListener();
    }
    void addMarkers() {
        HashSet<Event> mapEvents = singleton.filterEvents();
        Bundle b = getIntent().getExtras();
        String eventID = b.getString("eventID");
        Event selected = singleton.retrieveEvent(eventID);
        for(Event event: mapEvents) {
            double lat = event.getLatitude();
            double longi = event.getLongitude();
            LatLng loc = new LatLng(lat,longi);
            Log.d(TAG,"mapping event");

            BitmapDescriptor mapIcon;
            if(event.getEventType().toLowerCase().equals("birth")) {
                mapIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
            }
            else if(event.getEventType().toLowerCase().equals("marriage")) {
                mapIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
            }
            else if(event.getEventType().toLowerCase().equals("death")) {
                mapIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            }
            else {
                mapIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
            }
            MarkerOptions marker = new MarkerOptions().position(loc).icon(mapIcon);
            Marker addMarker = map.addMarker(marker);
            addMarker.setTag(event);
            //once the event has been added, we need to check if this is the event on which we need to center

            if(event.equals(selected)) {
                centerMap(loc);
                setTextView(addMarker);
            }
        }
        setLines(selected);
    }
    void setMarkerListener() {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d(TAG,"Marker was clicked");
                setTextView(marker);
                clearLines();
                setLines(marker);
                return false;
            }
        });
    }
    void centerMap(LatLng loc) {
        Log.d(TAG,"Center map was called");
        CameraUpdate update = CameraUpdateFactory.newLatLng(loc);
        map.moveCamera(update);
        return;
    }
    void setTextView(Marker marker) {
        String eventInfo = "";
        Event markerEvent = (Event) marker.getTag();
        Person eventPerson = singleton.retrievePerson(markerEvent.getPersonID());
        //will need function to get event from map and get person from singleton
        eventInfo += eventPerson.getFirstName() + " " + eventPerson.getLastName() + "\n";
        eventInfo += markerEvent.getEventType() + ": " + markerEvent.getCity() + ", " + markerEvent.getCountry() + "\n";
        eventInfo += "Year: " + markerEvent.getYear();
        Drawable genderIcon;
        if(markerEvent.getPersonGender().equals("m")) {
            genderIcon = new IconDrawable(EventActivity.this, FontAwesomeIcons.fa_male).colorRes(R.color.male_icon).sizeDp(40);
        }
        else {
            genderIcon = new IconDrawable(EventActivity.this,FontAwesomeIcons.fa_female).colorRes(R.color.female_icon).sizeDp(40);
        }
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(genderIcon,null,null,null);
        textView.setText(eventInfo);
        textView.setTag(markerEvent.getPersonID());
    }

    void drawLine(LatLng p1, LatLng p2, int color, int width) {
        PolylineOptions options = new PolylineOptions().add(p1,p2).color(color).width(width);
        Polyline line = map.addPolyline(options);
        mapLines.add(line);
    }
    void clearLines() {
        for(Polyline line: mapLines) {
            line.remove();
        }
        mapLines.clear();
    }
    void setLines(Marker marker) {
        Event mEvent = (Event) marker.getTag();
        HashSet<Event> filteredEvents = singleton.filterEvents();
        if(singleton.getSpouseLines()) {
            //draw spouselines
            Event spouse = singleton.retrieveSpouseLines(mEvent.getPersonID());
            if(spouse == null || !filteredEvents.contains(spouse)) {
                Log.d(TAG,"Person doesn't have spouse or event has been filtered out");
            }
            else {
                double lat = mEvent.getLatitude();
                double longi = mEvent.getLongitude();
                LatLng p1 = new LatLng(lat,longi);
                lat = spouse.getLatitude();
                longi = spouse.getLongitude();
                LatLng p2 = new LatLng(lat,longi);
                int color = Color.MAGENTA;
                int width = 10;
                drawLine(p1,p2,color,width);
            }
        }
        if(singleton.getFamilyTreeLines()) {
            //starting with person of event, draw lines to parents, then so on recursively until there are not parents
            addFamilyLines(mEvent,filteredEvents,0);

        }
        if(singleton.getLifeStoryLines()) {
            addLIfeStoryLines(mEvent.getPersonID(),filteredEvents);
            //draw lines connecting all events of that person
        }
    }
    void setLines(Event mEvent) {
        HashSet<Event> filteredEvents = singleton.filterEvents();
        if(singleton.getSpouseLines()) {
            //draw spouselines
            Event spouse = singleton.retrieveSpouseLines(mEvent.getPersonID());
            if(spouse == null || !filteredEvents.contains(spouse)) {
                Log.d(TAG,"Person doesn't have spouse or event has been filtered out");
            }
            else {
                double lat = mEvent.getLatitude();
                double longi = mEvent.getLongitude();
                LatLng p1 = new LatLng(lat,longi);
                lat = spouse.getLatitude();
                longi = spouse.getLongitude();
                LatLng p2 = new LatLng(lat,longi);
                int color = Color.MAGENTA;
                int width = 10;
                drawLine(p1,p2,color,width);
            }
        }
        if(singleton.getFamilyTreeLines()) {
            //starting with person of event, draw lines to parents, then so on recursively until there are not parents
            addFamilyLines(mEvent,filteredEvents,0);

        }
        if(singleton.getLifeStoryLines()) {
            addLIfeStoryLines(mEvent.getPersonID(),filteredEvents);
            //draw lines connecting all events of that person
        }
    }
    void addLIfeStoryLines(String personID, HashSet<Event> filteredEvents) {
        //get all the events related to that person
        ArrayList<Event> personEvents = singleton.retrieveLifeStory(personID);
        if(personEvents.size() < 2) {
            return;
        }
        else {
            for(int n = 0; n < personEvents.size()-1; n++) {
                Event e1 = personEvents.get(n);
                Event e2 = personEvents.get(n+1);
                if(filteredEvents.contains(e1) && filteredEvents.contains(e2)) {
                    double lat = e1.getLatitude();
                    double longit = e1.getLongitude();
                    LatLng p1 = new LatLng(lat,longit);
                    lat = e2.getLatitude();
                    longit = e2.getLongitude();
                    LatLng p2 = new LatLng(lat,longit);
                    drawLine(p1,p2,Color.RED,10);
                }
            }
        }

    }

    void addFamilyLines(Event event,HashSet<Event> filteredEvents,int genLevel) {
        //mEvent represents the root user for which we're starting to make these events
        //get the family lines for the parents
        HashSet<Event> parentEvents = singleton.retrieveParentLines(event.getPersonID());
        Log.d(TAG,String.valueOf(parentEvents.size()));
        for(Event parEvent: parentEvents) {
            if(filteredEvents.contains(parEvent)) {
                int color;
                if(genLevel % 4 == 0) {
                    //add green
                    color = Color.GREEN;
                }
                else if(genLevel % 4 == 1) {
                    //add grey
                    color = Color.GRAY;
                }
                else if(genLevel % 4 == 2) {
                    //add Yellow
                    color = Color.YELLOW;
                }
                else {
                    //add cyan
                    color= Color.CYAN;
                }
                double lat = event.getLatitude();
                double longi = event.getLongitude();
                LatLng p1 = new LatLng(lat,longi);
                lat = parEvent.getLatitude();
                longi = parEvent.getLongitude();
                LatLng p2 = new LatLng(lat,longi);
                drawLine(p1,p2,color,markerWidth - 2*genLevel);
                addFamilyLines(parEvent,filteredEvents,genLevel +1);
            }
        }
    }
}
