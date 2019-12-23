package com.example.familymap.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class DataSingleton {
    private static final String TAG = "Singleton";
    private static DataSingleton instance = null;
    private static Person[] persons;
    private static Event[] events;
    private static String userPersonID;
    private static String authToken;

    private static boolean lifeStoryLines;
    private static boolean familyTreeLines;
    private static boolean spouseLines;
    private static boolean fatherSide;
    private static boolean motherSide;
    private static boolean maleEvents;
    private static boolean femaleEvents;


    private static HashMap<String, Person> personMap;
    private static HashMap<String,Event> eventMap;
    private static HashMap<String, HashSet<Event>> filterMap;
    private static HashMap<String, ArrayList<Event>> personToEventsMap;
    private static HashMap<String, Event> spouseLinesMap;
    private static HashMap<String,HashSet<Event> > familyLinesMap;
    private DataSingleton() {
        //Log.d(TAG,"In constructor for singleton");
        reset();
    }
    public static DataSingleton getInstance() {
        if (instance == null)
            instance = new DataSingleton();
        return instance;
    }
    public static Person[] getPersons() {
        return persons;
    }

    public static void setPersons(Person[] persons) {
        DataSingleton.persons = persons;
    }

    public static Event[] getEvents() {
        return events;
    }

    public static void setEvents(Event[] events) {
        DataSingleton.events = events;
    }

    public static String getUserPersonID() {
        return userPersonID;
    }

    public static void setUserPersonID(String userPersonID) {
        DataSingleton.userPersonID = userPersonID;
    }

    public static boolean getLifeStoryLines() {
        return lifeStoryLines;
    }

    public static void setLifeStoryLines(boolean lifeStoryLines) {
        DataSingleton.lifeStoryLines = lifeStoryLines;
    }

    public static boolean getFamilyTreeLines() {
        return familyTreeLines;
    }

    public static void setFamilyTreeLines(boolean familyTreeLines) {
        DataSingleton.familyTreeLines = familyTreeLines;
    }

    public static boolean getSpouseLines() {
        return spouseLines;
    }

    public static void setSpouseLines(boolean spouseLines) {
        DataSingleton.spouseLines = spouseLines;
    }

    public static boolean getFatherSide() {
        return fatherSide;
    }

    public static void setFatherSide(boolean fatherSide) {
        DataSingleton.fatherSide = fatherSide;
    }

    public static boolean getMotherSide() {
        return motherSide;
    }

    public static void setMotherSide(boolean motherSide) {
        DataSingleton.motherSide = motherSide;
    }

    public static boolean getMaleEvents() {
        return maleEvents;
    }

    public static void setMaleEvents(boolean maleEvents) {
        DataSingleton.maleEvents = maleEvents;
    }

    public static boolean getFemaleEvents() {
        return femaleEvents;
    }

    public static void setFemaleEvents(boolean femaleEvents) {
        DataSingleton.femaleEvents = femaleEvents;
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static void setAuthToken(String authToken) {
        DataSingleton.authToken = authToken;
    }


    public static void reset() {
        setEvents(null);
        setPersons(null);
        setLifeStoryLines(true);
        setFamilyTreeLines(true);
        setSpouseLines(true);
        setFatherSide(false);
        setMotherSide(false);
        setMaleEvents(false);
        setFemaleEvents(false);
        setAuthToken(null);
        setUserPersonID(null);

        personMap = new HashMap<String,Person>();
        eventMap = new HashMap<String,Event>();
        filterMap = new HashMap<String,HashSet<Event> >();
        personToEventsMap = new HashMap<String,ArrayList<Event> >();
        spouseLinesMap = new HashMap<String,Event>();
        familyLinesMap = new HashMap<String, HashSet<Event> >();
    }
    public void createEventMap() {
        for(Event event: events) {
            eventMap.put(event.getEventID(),event);
        }
        System.out.println(eventMap.size());
    }
    public void createPersonMap() {
        for(Person person: persons) {
            personMap.put(person.getPersonID(),person);
        }
        System.out.println(personMap.size());
    }
    public void addChildAttribute() {
        //Adds who the child reference is for person, starting with the root user
        addChildAttributeHelper(personMap.get(userPersonID));
    }
    public void addChildAttributeHelper(Person person) {
        Person father = null;
        Person mother = null;
        if(person.getFatherID() != null) {
            father = personMap.get(person.getFatherID());
           // System.out.println("father " + father);
        }
        if(person.getMotherID() != null) {
            mother = personMap.get(person.getMotherID());
           //System.out.println("mother " + mother);
        }
        if(father != null) {
            father.setChildID(person.getPersonID());
            personMap.put(father.getPersonID(),father);
            addChildAttributeHelper(father);
        }
        if(mother != null) {
            mother.setChildID(person.getPersonID());
            personMap.put(mother.getPersonID(),mother);
            addChildAttributeHelper(mother);
        }
    }
    public void createFamilySidesPerson() {
        //get potential mother and father of user, and sets sides accordingly
        Person mother = null;
        Person father = null;
        Person user = personMap.get(userPersonID);
        user.setSide("User");
        if(user.getSpouseID() != null) {
            Person spouse = personMap.get(user.getSpouseID());
            spouse.setSide("User");
            personMap.put(spouse.getPersonID(),spouse);
        }
        //set user as having neither side
        personMap.put(userPersonID,user);
        if(user.getMotherID() != null) {
            mother = personMap.get(user.getMotherID());
        }
        if(user.getFatherID() != null) {
            father = personMap.get(user.getFatherID());
        }
        if(father != null) {
            setFatherSideAttributePerson(father);
        }
        if(mother != null) {
            setMotherSideAttributePerson(mother);
        }
    }
public void setFatherSideAttributePerson(Person person) {
        person.setSide("Father");
        personMap.put(person.getPersonID(),person);
        Person mother = null;
        Person father = null;
        if(person.getMotherID() != null) {
            mother = personMap.get(person.getMotherID());
            setFatherSideAttributePerson(mother);
        }
        if(person.getFatherID() != null) {
            father = personMap.get(person.getFatherID());
            setFatherSideAttributePerson(father);
        }
    }
    public void setMotherSideAttributePerson(Person person) {
        person.setSide("Mother");
        personMap.put(person.getPersonID(),person);
        Person mother = null;
        Person father = null;
        if(person.getMotherID() != null) {
            mother = personMap.get(person.getMotherID());
            setMotherSideAttributePerson(mother);
        }
        if(person.getFatherID() != null) {
            father = personMap.get(person.getFatherID());
            setMotherSideAttributePerson(father);
        }
    }

    public void createEventPersonAtrributes() {
        //creates family sides for each event
        Event event;
        Person person;
        for(Map.Entry<String,Event> entry: eventMap.entrySet()) {
            event = entry.getValue();
//            System.out.println(event);
//            Log.d(TAG,event.toString());
            person = personMap.get(event.getPersonID());
            event.setPersonGender(person.getGender());
            event.setPersonSide(person.getSide());
            entry.setValue(event);
        }
    }

    public void createMaleFilter() {
        HashSet<Event> maleEvents = new HashSet<Event>();
        for(Event event: eventMap.values()) {
            if(event.getPersonGender().toLowerCase().equals("m")) {
                maleEvents.add(event);
            }
        }
        filterMap.put("male", maleEvents);
    }
    public void createFemaleFilter() {
        HashSet<Event> femaleEvents = new HashSet<Event>();
        for(Event event: eventMap.values()) {
            if(event.getPersonGender().toLowerCase().equals("f")) {
                femaleEvents.add(event);
            }
        }
        filterMap.put("female", femaleEvents);
    }
    public void createFatherFilter() {
        HashSet<Event> fatherSideEvents = new HashSet<Event>();
        for(Event event: eventMap.values()) {
            System.out.println(event);
            if(event.getPersonSide().equals("Father")) {
                fatherSideEvents.add(event);
            }
        }
        filterMap.put("father", fatherSideEvents);
    }
    public void createMotherFilter() {
        HashSet<Event> motherSideEvents = new HashSet<Event>();
        for(Event event: eventMap.values()) {
            if(event.getPersonSide().equals("Mother")) {
                motherSideEvents.add(event);
            }
        }
        filterMap.put("mother", motherSideEvents);
    }
    public void addEventsToPeople() {
        for(Person person: personMap.values()) {
            ArrayList<Event> personEvents = new ArrayList<Event>();//new EventComparator());
            for(Event event: eventMap.values()) {
                if(event.getPersonID().equals(person.getPersonID())) {
                    personEvents.add(event);
                }
            }
            personEvents.sort(new EventComparator());
            personToEventsMap.put(person.getPersonID(),personEvents);
        }
    }
    public void createSpouseLines() {
        //this will map a personID, to the earliest event of their spouse, if it exists
        for(Person person: personMap.values()) {
            Event spouseFirstEvent = null;
            if(person.getSpouseID() != null) {
                ArrayList<Event> spouseEvents = personToEventsMap.get(person.getSpouseID());
                if(spouseEvents.size() != 0) {
                    spouseFirstEvent = spouseEvents.get(0);
                }
            }
            spouseLinesMap.put(person.getPersonID(),spouseFirstEvent);
        }
    }
    public void createFamilyLines() {
        for(Person person: personMap.values()) {
            HashSet<Event> parentEvents = new HashSet<Event>();
            if(person.getMotherID() != null) {
                System.out.println("Entered mom");
                ArrayList<Event> momEvents = personToEventsMap.get(person.getMotherID());
                if(momEvents.size() != 0) {
                    Event firstMom = momEvents.get(0);
                    parentEvents.add(firstMom);
                }
            }
            if(person.getFatherID() != null) {
                System.out.println("entered dad");
                ArrayList<Event> dadEvents = personToEventsMap.get(person.getFatherID());
                if(dadEvents.size() != 0) {
                    Event firstDad = dadEvents.get(0);
                    parentEvents.add(firstDad);
                }
            }
            System.out.println("Size of the create set is " + parentEvents.size());
            familyLinesMap.put(person.getPersonID(),parentEvents);
        }
    }
    public void prepareData() {
        //method will create maps to be used for the map object itself

        //first thing that needs to be done is to create the person and event maps
        //to do this we first create the event map
        createEventMap();
        createPersonMap();
        //now both the maps are created, we need to engineer the data so it has all the relevant data needed for the google map
        //this means persons will need a child reference, as well as a string indicating which side of the family they're on
        addChildAttribute();
        createFamilySidesPerson();
        createEventPersonAtrributes();
        addEventsToPeople();
        //now that the data has been given the needed attributes for filtering, we can create the sets needed for filtering
        createMaleFilter();
        createFemaleFilter();
        createFatherFilter();
        createMotherFilter();
        createSpouseLines();
        createFamilyLines();
        //once the sets for filtering are created, we can create the sets for the lines

    }

    //once the data has been prepped we can then run the filter function each time we need to get the set of events to return
    public static HashSet<Event> filterEvents() {
        //we first get the overall set of events
        HashSet<Event> events = new HashSet<Event>(eventMap.values());
        if(maleEvents) {
            events.removeAll(filterMap.get("male"));
        }
        if(femaleEvents) {
            events.removeAll(filterMap.get("female"));
        }
        if(fatherSide) {
            events.removeAll(filterMap.get("father"));
        }
        if(motherSide) {
            events.removeAll(filterMap.get("mother"));
        }
        return events;
    }

    public static Event retrieveEvent(String eventID) {
        return eventMap.get(eventID);
    }
    public static ArrayList<Event> retrievePersonEvents(String personID) {
        return personToEventsMap.get(personID);
    }
    public static Person retrievePerson(String personID) {
        return personMap.get(personID);
    }
    public static Event retrieveSpouseLines(String personID) {
        return spouseLinesMap.get(personID);
    }
    public static HashSet<Event> retrieveParentLines(String personID) {
        return familyLinesMap.get(personID);
    }
    public static ArrayList<Event> retrieveLifeStory(String personID) {
        return personToEventsMap.get(personID);
    }
    public static HashSet<Person> searchPeople(String searchText) {
        HashSet<Person> search = new HashSet<Person>();
        if(searchText == null) {
            return search;
        }
        else if(searchText.equals("")) {
            return search;
        }
        for(Person person: personMap.values()) {
            if(person.getLastName().toLowerCase().contains(searchText.toLowerCase()) || person.getFirstName().toLowerCase().contains(searchText.toLowerCase())) {
                search.add(person);
            }
        }
        return search;
    }
    public static HashSet<Event> searchEvents(String searchText) {
        HashSet<Event> search = new HashSet<Event>();
        HashSet<Event> filterEvents = filterEvents();
        if(searchText == null) {
            return search;
        }
        else if(searchText.equals("")) {
            return search;
        }
        for(Event event: filterEvents) {
                if (event.getCountry().toLowerCase().contains(searchText.toLowerCase()) || event.getCity().toLowerCase().contains(searchText.toLowerCase()) ||
                        event.getEventType().toLowerCase().contains(searchText.toLowerCase()) || (String.valueOf(event.getYear()).toLowerCase().contains(searchText.toLowerCase()))) {
                    search.add(event);
                }
        }
        return search;

    }
    public static boolean isLifeStoryLines() {
        return lifeStoryLines;
    }

    public static boolean isFamilyTreeLines() {
        return familyTreeLines;
    }

    public static boolean isSpouseLines() {
        return spouseLines;
    }

    public static boolean isFatherSide() {
        return fatherSide;
    }

    public static boolean isMotherSide() {
        return motherSide;
    }

    public static boolean isMaleEvents() {
        return maleEvents;
    }

    public static boolean isFemaleEvents() {
        return femaleEvents;
    }

    public static HashMap<String, Person> getPersonMap() {
        return personMap;
    }

    public static void setPersonMap(HashMap<String, Person> personMap) {
        DataSingleton.personMap = personMap;
    }

    public static HashMap<String, Event> getEventMap() {
        return eventMap;
    }

    public static void setEventMap(HashMap<String, Event> eventMap) {
        DataSingleton.eventMap = eventMap;
    }

    public static HashMap<String, HashSet<Event>> getFilterMap() {
        return filterMap;
    }

    public static void setFilterMap(HashMap<String, HashSet<Event>> filterMap) {
        DataSingleton.filterMap = filterMap;
    }

    public static HashMap<String, ArrayList<Event>> getPersonToEventsMap() {
        return personToEventsMap;
    }

    public static void setPersonToEventsMap(HashMap<String, ArrayList<Event>> personToEventsMap) {
        DataSingleton.personToEventsMap = personToEventsMap;
    }

    public static HashMap<String, Event> getSpouseLinesMap() {
        return spouseLinesMap;
    }

    public static void setSpouseLinesMap(HashMap<String, Event> spouseLinesMap) {
        DataSingleton.spouseLinesMap = spouseLinesMap;
    }

    public static HashMap<String, HashSet<Event>> getFamilyLinesMap() {
        return familyLinesMap;
    }

    public static void setFamilyLinesMap(HashMap<String, HashSet<Event>> familyLinesMap) {
        DataSingleton.familyLinesMap = familyLinesMap;
    }
}


class EventComparator implements Comparator<Event> {
    @Override
    //note that many of your problems may result from some assumptions made about the events
    public int compare(Event o1, Event o2) {
        //should return 0 if same, -1 if o1 < o2 and 1 if 01 > 02
        //check if birth events or not
        //if event 1 is birth
        if(o1.equals(o2)) {
            return 0;
        }
        //must be broken down into cases
        /*
        1) 1 birth, 2 not
        1) 1 birth, 2 is
        1) 2 birth, 1 not
        2) 1 death, 2 not
        2) 1 death, 2 death
        2) 2 death, 1 not
        3)neither birth nor death
         */
        //case 1 birth 2 not
        else if(o1.getEventType().toLowerCase().equals("birth") && !o2.getEventType().toLowerCase().equals("birth")) {
            return -1;
        }
        //case 1 birth, and 2 birth
        else if(o1.getEventType().toLowerCase().equals("birth") && o2.getEventType().toLowerCase().equals("birth")) {
            int diff = o1.getYear() - o2.getYear();
            //female events will come before male events (is arbitrary choice to help with containment checks
            if(diff == 0) {
                return o1.getPersonGender().toLowerCase().compareTo(o2.getPersonGender().toLowerCase());
            }
            return diff;
        }
        // case 2 birth 1 not
        else if(!o1.getEventType().toLowerCase().equals("birth") && o2.getEventType().toLowerCase().equals("birth")) {
            return 1;
        }
        //case 1 death 2 not

        else if(o1.getEventType().toLowerCase().equals("death") && !o2.getEventType().toLowerCase().equals("death")) {
            return 1;
        }
        //case 1 death and 2 death
        else if(o1.getEventType().toLowerCase().equals("death") && o2.getEventType().toLowerCase().equals("death")) {
            int diff = o1.getYear() - o2.getYear();
            if(diff == 0) {
                return o1.getPersonGender().toLowerCase().compareTo(o2.getPersonGender().toLowerCase());
            }
            return diff;
        }
        //case 2 death and 1 not
        else if(!o1.getEventType().toLowerCase().equals("death") && o2.getEventType().toLowerCase().equals("death")) {
            return -1;
        }
        //case neither birth nor death
        else {
            int diff = o1.getYear() - o2.getYear();
            if(diff == 0) {
                int eventDiff = o1.getEventType().toLowerCase().compareTo(o2.getEventType().toLowerCase());
                if(eventDiff == 0) {
                    return o1.getPersonGender().toLowerCase().compareTo(o2.getPersonGender().toLowerCase());
                }
                return eventDiff;
            }
            return diff;
        }
        //if one event is birth, this comes first
        //if neither is birth, check if either event is death
        //if one event is death, this comes last
        //if neither birth nor death, return year difference
    }
}


