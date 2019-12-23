package com.example.familymap.Model;

import com.example.familymap.Result.EventResult;
import com.example.familymap.Result.LoginRegisterResult;
import com.example.familymap.Result.PersonResult;
import com.example.familymap.Result.Result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class DataSingletonTest {
    private ServerProxy server;
    private DataSingleton singleton;
    String userName;
    String password;
    String firstName;
    String lastName;
    String email;
    String gender;
    Result result;
    LoginRegisterResult logResult;
    PersonResult personResult;
    EventResult eventResult;

    @BeforeEach
    @DisplayName("Initialization")
    void setup() {
        server = new ServerProxy("192.168.43.1","8080");
        singleton = DataSingleton.getInstance();
        singleton.reset();
        server.clear();
        userName = "marshallb95";
        password = "password";
        firstName = "Brandon";
        lastName = "Marshall";
        email = "myemail@gmail.com";
        gender = "f";
    }
    @Test
    @DisplayName("equals")
    void singletonSame() {
        //first we test that all singleton objects are the same object
        try {
            DataSingleton newSingleton = DataSingleton.getInstance();
            assertEquals(singleton, newSingleton);
            //test that altering values in one instance will alter it in all instances
            newSingleton.setUserPersonID("personID");
            assertEquals(newSingleton.getUserPersonID(),singleton.getUserPersonID());
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("All singletons should be the same instance");
        }
    }
    @Test
    @DisplayName("Testing arrays of full family")
    void testDataCreation() {
        try {
            //we test that family relations are created properly inside of the singleton class, we test this with user data, and with register data
            result = server.register(userName, password, email, firstName, lastName, gender);
            logResult = (LoginRegisterResult) result;
            singleton.setUserPersonID(logResult.getPersonID());
            singleton.setAuthToken(logResult.getAuthToken());

            result = server.getPeople(logResult.getAuthToken(), logResult.getPersonID());
            personResult = (PersonResult) result;
            Person[] persons = personResult.getPersons();
            result = server.getEvents(logResult.getAuthToken());
            eventResult = (EventResult) result;
            Event[] events = eventResult.getEvents();
            //make sure the arrays aren't altered
            singleton.setEvents(events);
            assertEquals(events, singleton.getEvents());
            singleton.setPersons(persons);
            assertEquals(persons, singleton.getPersons());
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("events are no longer the same");
        }

    }
    @Test
    @DisplayName("testing map creation from register")
    void registerMaps() {
        //next we test to make sure that the maps are created intitially correctly
        try {
            result = server.register(userName, password, email, firstName, lastName, gender);
            logResult = (LoginRegisterResult) result;
            singleton.setUserPersonID(logResult.getPersonID());
            singleton.setAuthToken(logResult.getAuthToken());

            result = server.getPeople(logResult.getAuthToken(), logResult.getPersonID());
            personResult = (PersonResult) result;
            Person[] persons = personResult.getPersons();
            result = server.getEvents(logResult.getAuthToken());
            singleton.setPersons(persons);
            eventResult = (EventResult) result;
            Event[] events = eventResult.getEvents();
            singleton.setEvents(events);

            singleton.createEventMap();
            HashMap<String, Event> eMap = singleton.getEventMap();
            for (Event event : events) {
                if (!eMap.keySet().contains(event.getEventID())) {
                    fail("map should contain all events from array");
                } else if (eMap.get(event.getEventID()) == null) {
                    fail("no event should be mapped to a null event");
                }
            }

            singleton.createPersonMap();
            HashMap<String, Person> pMap = singleton.getPersonMap();
            for (Person person : persons) {
                if (!pMap.keySet().contains(person.getPersonID())) {
                    fail("map should contain all person object from array");
                } else if (pMap.get(person.getPersonID()) == null) {
                    fail("no person should be mapped to a null person");
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Failed for map creation");
        }
    }
    @Test
    @DisplayName("Testing child attribute creation for register")
    void childAttributeRegister() {
        try {
            result = server.register(userName, password, email, firstName, lastName, gender);
            logResult = (LoginRegisterResult) result;
            singleton.setUserPersonID(logResult.getPersonID());
            singleton.setAuthToken(logResult.getAuthToken());

            result = server.getPeople(logResult.getAuthToken(), logResult.getPersonID());
            personResult = (PersonResult) result;
            Person[] persons = personResult.getPersons();
            result = server.getEvents(logResult.getAuthToken());
            eventResult = (EventResult) result;
            Event[] events = eventResult.getEvents();
            singleton.setEvents(events);
            singleton.setPersons(persons);
            singleton.createEventMap();
            singleton.createPersonMap();

            singleton.addChildAttribute();
            HashMap<String,Person> pMap = singleton.getPersonMap();
            //we check the first two generations for the validity of this, as it is done recursively
            Person user = pMap.get(logResult.getPersonID());
            if (user == null) {
                fail("User person must exist");
            }

            //check user, parents and grandparents
            Person father = pMap.get(user.getFatherID());
            Person mother = pMap.get(user.getMotherID());
            assertTrue(isChild(user, father));
            assertTrue(isChild(user, mother));
            //check for parents as well
            Person fFather = pMap.get(father.getFatherID());
            Person fMother = pMap.get(father.getMotherID());
            assertTrue(isChild(father, fFather));
            assertTrue(isChild(father, fMother));

            Person mFather = pMap.get(mother.getFatherID());
            Person mMother = pMap.get(mother.getMotherID());
            assertTrue(isChild(mother, mFather));
            assertTrue(isChild(mother, mFather));
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to create child attribute for registered data");
        }
    }
    @Test
    @DisplayName("Test family sides")
    void testFamilySidesRegister() {
        try {
            result = server.register(userName, password, email, firstName, lastName, gender);
            logResult = (LoginRegisterResult) result;
            singleton.setUserPersonID(logResult.getPersonID());
            singleton.setAuthToken(logResult.getAuthToken());

            result = server.getPeople(logResult.getAuthToken(), logResult.getPersonID());
            personResult = (PersonResult) result;
            Person[] persons = personResult.getPersons();
            result = server.getEvents(logResult.getAuthToken());
            eventResult = (EventResult) result;
            Event[] events = eventResult.getEvents();
            singleton.setPersons(persons);
            singleton.setEvents(events);

            singleton.createEventMap();
            singleton.createPersonMap();

            singleton.addChildAttribute();
            HashMap<String, Person> pMap = singleton.getPersonMap();
            //next check that the family sides are created properly
            singleton.createFamilySidesPerson();
            pMap = singleton.getPersonMap();
            Person user = pMap.get(logResult.getPersonID());
            assertEquals(user.getSide(), "User");
            Person father = pMap.get(user.getFatherID());
            Person fFather = pMap.get(father.getFatherID());
            Person fMother = pMap.get(father.getMotherID());
            assertEquals(father.getSide(), "Father");
            assertEquals(fFather.getSide(), "Father");
            assertEquals(fMother.getSide(), "Father");

            Person mother = pMap.get(user.getMotherID());
            Person mMother = pMap.get(mother.getMotherID());
            Person mFather = pMap.get(mother.getFatherID());

            assertEquals(mother.getSide(), "Mother");
            assertEquals(mMother.getSide(), "Mother");
            assertEquals(mMother.getSide(), "Mother");
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to create sides properly");
        }
    }

    @Test
    @DisplayName("Testing event attributes set properly")
    void testEventAttributes() {
        result = server.register(userName, password, email, firstName, lastName, gender);
        logResult = (LoginRegisterResult) result;
        singleton.setUserPersonID(logResult.getPersonID());
        singleton.setAuthToken(logResult.getAuthToken());

        result = server.getPeople(logResult.getAuthToken(), logResult.getPersonID());
        personResult = (PersonResult) result;
        Person[] persons = personResult.getPersons();
        result = server.getEvents(logResult.getAuthToken());
        eventResult = (EventResult) result;
        Event[] events = eventResult.getEvents();
        singleton.setPersons(persons);
        singleton.setEvents(events);

        singleton.createEventMap();
        singleton.createPersonMap();

        singleton.addChildAttribute();
        singleton.createFamilySidesPerson();
        singleton.createEventPersonAtrributes();
        HashMap<String,Event> eMap = singleton.getEventMap();
        HashMap<String,Person> pMap = singleton.getPersonMap();
        //make sure that each event has the correct attributes
        Event mapEvent;
        Person mapPerson;
        for(Map.Entry<String,Event> entry: eMap.entrySet()) {
            mapEvent = entry.getValue();
            mapPerson = pMap.get(mapEvent.getPersonID());
            System.out.println(mapPerson);
            System.out.println(mapEvent);
            System.out.println(mapPerson.getSide());
            System.out.println(mapEvent.getPersonSide());
            if(!mapPerson.getSide().equals(mapEvent.getPersonSide())) {
                fail("Event Side differs from person Side");
            }
            if(!mapPerson.getGender().equals(mapEvent.getPersonGender())) {
                fail("Event gender differs from person gender");
            }
        }

    }

    @Test
    @DisplayName("Testing adding events to people")
    void addEventsToPeople() {
        try {
            result = server.register(userName, password, email, firstName, lastName, gender);
            logResult = (LoginRegisterResult) result;
            singleton.setUserPersonID(logResult.getPersonID());
            singleton.setAuthToken(logResult.getAuthToken());

            result = server.getPeople(logResult.getAuthToken(), logResult.getPersonID());
            personResult = (PersonResult) result;
            Person[] persons = personResult.getPersons();
            result = server.getEvents(logResult.getAuthToken());
            eventResult = (EventResult) result;
            Event[] events = eventResult.getEvents();
            singleton.setPersons(persons);
            singleton.setEvents(events);

            singleton.createEventMap();
            singleton.createPersonMap();

            singleton.addChildAttribute();
            singleton.createFamilySidesPerson();
            singleton.createEventPersonAtrributes();
            singleton.addEventsToPeople();
            //make sure that each person has the correct attributes for that person
            HashMap<String, Event> eMap = singleton.getEventMap();
            HashMap<String, ArrayList<Event>> p2eMap = singleton.getPersonToEventsMap();
            for (Event event : eMap.values()) {
                //System.out.println(event.getClass().getName());
                //get the person that the event's person ID corresponds to
                ArrayList<Event> personEvents = p2eMap.get(event.getPersonID());
                if(!personEvents.contains(event)) {
                    fail("Event not found in array");
                }
            }
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to set events to each person");
        }
    }

    @Test
    @DisplayName("Testing order of events")
    void testOrder() {
        Person p = new Person("12345678","marshallb95","Brandon","Marshall","m", null,null,null,null);
        Person[] people = new Person[]{p};
        Event birth = new Event("abcdefgh","marshallb95","12345678",12.23f,34.5f, "usa", "provo", "birth", 1995);
        Event marriage = new Event("1a2b3c4d","marshallb95","12345678",12.3f,34.5f,"usa","provo","marriage",2019);
        Event death = new Event("4d3c2b1a","marshallb95","12345678",12.34f,45.67f,"usa","provo","death", 2089);
        Event[] events = new Event[] {death,birth,marriage};
        singleton.setUserPersonID("12345678");
        singleton.setEvents(events);
        singleton.setPersons(people);

        singleton.createEventMap();
        singleton.createPersonMap();

        singleton.addChildAttribute();
        singleton.createFamilySidesPerson();
        singleton.createEventPersonAtrributes();
        singleton.addEventsToPeople();
        ArrayList<Event> sortedUserEvents = singleton.getPersonToEventsMap().get("12345678");
        HashMap<String,Event> eMap = singleton.getEventMap();
        assertEquals(eMap.get(birth.getEventID()),sortedUserEvents.get(0),"First event should be the same");
        assertEquals(eMap.get(marriage.getEventID()),sortedUserEvents.get(1),"Second event should be marriage");
        assertEquals(eMap.get(death.getEventID()),sortedUserEvents.get(2),"Third event should be death");

    }

    @Test
    @DisplayName("testing male filter creation")
    void testCreatingMaleFilter() {
        try {
            result = server.register(userName, password, email, firstName, lastName, gender);
            logResult = (LoginRegisterResult) result;
            singleton.setUserPersonID(logResult.getPersonID());
            singleton.setAuthToken(logResult.getAuthToken());

            result = server.getPeople(logResult.getAuthToken(), logResult.getPersonID());
            personResult = (PersonResult) result;
            Person[] persons = personResult.getPersons();
            result = server.getEvents(logResult.getAuthToken());
            eventResult = (EventResult) result;
            Event[] events = eventResult.getEvents();
            singleton.setPersons(persons);
            singleton.setEvents(events);

            singleton.createEventMap();
            singleton.createPersonMap();

            singleton.addChildAttribute();
            singleton.createFamilySidesPerson();
            singleton.createEventPersonAtrributes();
            singleton.addEventsToPeople();
            singleton.createMaleFilter();
            //make sure that each person has the correct attributes for that person
            HashMap<String, Event> eMap = singleton.getEventMap();
            HashSet<Event> maleEvents = singleton.getFilterMap().get("male");
            for (Event event : eMap.values()) {
                if (event.getPersonGender().equals("m")) {
                    if (!maleEvents.contains(event)) {
                        fail("missing a male event");
                    }
                }
                else {
                    if(maleEvents.contains(event)) {
                        System.out.println(event);
                        fail("event is not male");
                    }
                }
            }
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to create male filter");
        }
    }
    @Test
    @DisplayName("Testing creating female filter")
    void testCreatingFemaleFilter() {
        try {
            result = server.register(userName, password, email, firstName, lastName, gender);
            logResult = (LoginRegisterResult) result;
            singleton.setUserPersonID(logResult.getPersonID());
            singleton.setAuthToken(logResult.getAuthToken());

            result = server.getPeople(logResult.getAuthToken(), logResult.getPersonID());
            personResult = (PersonResult) result;
            Person[] persons = personResult.getPersons();
            result = server.getEvents(logResult.getAuthToken());
            eventResult = (EventResult) result;
            Event[] events = eventResult.getEvents();
            singleton.setPersons(persons);
            singleton.setEvents(events);

            singleton.createEventMap();
            singleton.createPersonMap();

            singleton.addChildAttribute();
            singleton.createFamilySidesPerson();
            singleton.createEventPersonAtrributes();
            singleton.addEventsToPeople();
            singleton.createFemaleFilter();
            //make sure that each person has the correct attributes for that person
            HashMap<String, Event> eMap = singleton.getEventMap();
            HashSet<Event> femaleEvents = singleton.getFilterMap().get("female");
            for (Event event : eMap.values()) {
                if (event.getPersonGender().equals("f")) {
                    if (!femaleEvents.contains(event)) {
                        fail("missing a female event");
                    }
                } else {
                    if (femaleEvents.contains(event)) {
                        fail("contains event that isn't female");
                    }
                }
            }
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error occurred whilte testing creating female filter");
        }
    }

    @Test
    @DisplayName("testing creating father filter")
    void testCreatingFatherFilter() {
        try {
            result = server.register(userName, password, email, firstName, lastName, gender);
            logResult = (LoginRegisterResult) result;
            singleton.setUserPersonID(logResult.getPersonID());
            singleton.setAuthToken(logResult.getAuthToken());

            result = server.getPeople(logResult.getAuthToken(), logResult.getPersonID());
            personResult = (PersonResult) result;
            Person[] persons = personResult.getPersons();
            result = server.getEvents(logResult.getAuthToken());
            eventResult = (EventResult) result;
            Event[] events = eventResult.getEvents();
            singleton.setPersons(persons);
            singleton.setEvents(events);

            singleton.createEventMap();
            singleton.createPersonMap();

            singleton.addChildAttribute();
            singleton.createFamilySidesPerson();
            singleton.createEventPersonAtrributes();
            singleton.addEventsToPeople();
            singleton.createFatherFilter();
            //make sure that each person has the correct attributes for that person
            HashMap<String, Event> eMap = singleton.getEventMap();
            HashSet<Event> fatherEvents = singleton.getFilterMap().get("father");
            for (Event event : eMap.values()) {
                if (event.getPersonSide().equals("Father")) {
                    if (!fatherEvents.contains(event)) {
                        fail("missing a father event");
                    }
                } else {
                    if (fatherEvents.contains(event)) {
                        System.out.println(event);
                        fail("contains event that isn't father");
                    }
                }
            }
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error occurred whilte testing creating female filter");
        }
    }
    @Test
    @DisplayName("testing creating mother filter")
    void testCreatingMotherFilter() {
        try {
            result = server.register(userName, password, email, firstName, lastName, gender);
            logResult = (LoginRegisterResult) result;
            singleton.setUserPersonID(logResult.getPersonID());
            singleton.setAuthToken(logResult.getAuthToken());

            result = server.getPeople(logResult.getAuthToken(), logResult.getPersonID());
            personResult = (PersonResult) result;
            Person[] persons = personResult.getPersons();
            result = server.getEvents(logResult.getAuthToken());
            eventResult = (EventResult) result;
            Event[] events = eventResult.getEvents();
            singleton.setPersons(persons);
            singleton.setEvents(events);

            singleton.createEventMap();
            singleton.createPersonMap();

            singleton.addChildAttribute();
            singleton.createFamilySidesPerson();
            singleton.createEventPersonAtrributes();
            singleton.addEventsToPeople();
            singleton.createMotherFilter();
            //make sure that each person has the correct attributes for that person
            HashMap<String, Event> eMap = singleton.getEventMap();
            HashSet<Event> motherEvents = singleton.getFilterMap().get("mother");
            for (Event event : eMap.values()) {
                if (event.getPersonSide().equals("Mother")) {
                    if (!motherEvents.contains(event)) {
                        fail("missing a male event");
                    }
                } else {
                    if (motherEvents.contains(event)) {
                        fail("contains event that isn't male");
                    }
                }
            }
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error occurred whilte testing creating female filter");
        }
    }

    @Test
    @DisplayName("Testing creating spouselines")
    void testFamilyLines() {
        result = server.register(userName, password, email, firstName, lastName, gender);
        logResult = (LoginRegisterResult) result;
        singleton.setUserPersonID(logResult.getPersonID());
        singleton.setAuthToken(logResult.getAuthToken());

        result = server.getPeople(logResult.getAuthToken(), logResult.getPersonID());
        personResult = (PersonResult) result;
        Person[] persons = personResult.getPersons();
        result = server.getEvents(logResult.getAuthToken());
        eventResult = (EventResult) result;
        Event[] events = eventResult.getEvents();
        singleton.setPersons(persons);
        singleton.setEvents(events);

        singleton.createEventMap();
        singleton.createPersonMap();

        singleton.addChildAttribute();
        singleton.createFamilySidesPerson();
        singleton.createEventPersonAtrributes();
        singleton.addEventsToPeople();
        singleton.createFamilyLines();

        HashMap<String,HashSet<Event> > familyLines = singleton.getFamilyLinesMap();
        HashMap<String,ArrayList<Event> > p2eMap = singleton.getPersonToEventsMap();
        HashMap<String,Person> pMap = singleton.getPersonMap();

        for(Person person: pMap.values()) {
            HashSet<Event> parentEvents = familyLines.get(person.getPersonID());
            //check if 0,1, or 2 parents
            if(person.getFatherID() == null && person.getMotherID() == null) {
                assertEquals(parentEvents.size(),0,"Should have no parent events");
            }
            else if(person.getFatherID() != null && person.getMotherID() == null) {
                assertEquals(parentEvents.size(),1,"Should only contain father event");
                ArrayList<Event> fatherEvents = p2eMap.get(person.getFatherID());
                assertTrue(parentEvents.contains(fatherEvents.get(0)),"should contain father's first event only");
            }
            else if(person.getFatherID() == null && person.getMotherID() != null) {
                assertEquals(parentEvents.size(),1,"Should only contain mother event");
                ArrayList<Event> motherEvents = p2eMap.get(person.getMotherID());
                assertTrue(parentEvents.contains(motherEvents.get(0)),"should contain mother's first event only");
            }
            else {
                assertEquals(parentEvents.size(),2,"Should contain father event and mother event");
                ArrayList<Event> motherEvents = p2eMap.get(person.getMotherID());
                ArrayList<Event> fatherEvents = p2eMap.get(person.getFatherID());
                assertTrue(parentEvents.contains(motherEvents.get(0)),"should contain mother's first event for both parents");
                assertTrue(parentEvents.contains(fatherEvents.get(0)),"should contain father's first events as well");
            }
        }
    }
    @Test
    @DisplayName("Testing creating family lines")
    void testSpouseLines() {

    }
    @Test
    @DisplayName("Testing filter with male and mother")
    void testFilterMaleMother() {
        try {
            result = server.register(userName, password, email, firstName, lastName, gender);
            logResult = (LoginRegisterResult) result;
            singleton.setUserPersonID(logResult.getPersonID());
            singleton.setAuthToken(logResult.getAuthToken());
            result = server.getEvents(logResult.getAuthToken());
            eventResult = (EventResult) result;
            result = server.getPeople(logResult.getAuthToken(), logResult.getPersonID());
            personResult = (PersonResult) result;
            singleton.setPersons(personResult.getPersons());
            singleton.setEvents(eventResult.getEvents());
            singleton.prepareData();
            singleton.setMaleEvents(true);
            singleton.setMotherSide(true);
            HashSet<Event> filteredEvents = singleton.filterEvents();
            //we check to make sure only the events that are female and father are left
            HashMap<String, Event> eMap = singleton.getEventMap();
            for (Event event : eMap.values()) {
                if ((event.getPersonSide().equals("Father") || event.getPersonSide().equals("User")) && event.getPersonGender().equals("f")) {
                    if (!filteredEvents.contains(event)) {
                        fail("Missing an event that should be contained");
                    }
                } else {
                    if (filteredEvents.contains(event)) {
                        System.out.println(event);
                        System.out.println(filteredEvents);
                        fail("Contains an event that shouldn't be in set");
                    }
                }
            }
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed for filtering male and mother");
        }
    }
    @Test
    @DisplayName("Testing filtering male and female")
    void testFemaleMaleFilter() {
        try {
            result = server.register(userName, password, email, firstName, lastName, gender);
            logResult = (LoginRegisterResult) result;
            singleton.setUserPersonID(logResult.getPersonID());
            singleton.setAuthToken(logResult.getAuthToken());
            result = server.getEvents(logResult.getAuthToken());
            eventResult = (EventResult) result;
            result = server.getPeople(logResult.getAuthToken(), logResult.getPersonID());
            personResult = (PersonResult) result;
            singleton.setPersons(personResult.getPersons());
            singleton.setEvents(eventResult.getEvents());
            singleton.prepareData();
            singleton.setMaleEvents(true);
            singleton.setFemaleEvents(true);
            HashSet<Event> filteredEvents = singleton.filterEvents();
            assertEquals(filteredEvents.size(),0);
            //we check to make sure only the events that are female and father are left
            HashMap<String, Event> eMap = singleton.getEventMap();
            for (Event event : eMap.values()) {
                if (filteredEvents.contains(event)) {
                    System.out.println(event);
                    System.out.println(filteredEvents);
                    fail("Contains an event that shouldn't be in set");
                }
            }
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed for filtering male and mother");
        }
    }
    @Test
    @DisplayName("Testing remove mother and father side")
    void testMotherFatherFilter() {
        try {
            result = server.register(userName, password, email, firstName, lastName, gender);
            logResult = (LoginRegisterResult) result;
            singleton.setUserPersonID(logResult.getPersonID());
            singleton.setAuthToken(logResult.getAuthToken());
            result = server.getEvents(logResult.getAuthToken());
            eventResult = (EventResult) result;
            result = server.getPeople(logResult.getAuthToken(), logResult.getPersonID());
            personResult = (PersonResult) result;
            singleton.setPersons(personResult.getPersons());
            singleton.setEvents(eventResult.getEvents());
            singleton.prepareData();
            singleton.setFatherSide(true);
            singleton.setMotherSide(true);
            HashSet<Event> filteredEvents = singleton.filterEvents();
            assertEquals(filteredEvents.size(),1);
            //we check to make sure only the events that are female and father are left
            HashMap<String, Event> eMap = singleton.getEventMap();
            for (Event event : eMap.values()) {
                if(event.getPersonSide().equals("Father")||event.getPersonSide().equals("Mother")) {
                    if(filteredEvents.contains(event)) {
                        fail("Event contains a side that it shouldn't");
                    }
                }
                else {
                    if(!filteredEvents.contains(event)) {
                        fail("Missing event that should be contained");
                    }
                }

            }
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed for filtering male and mother");
        }
    }
    @Test
    @DisplayName("Testing searching people")
    void testPeopleSearch() {
        result = server.register(userName, password, email, firstName, lastName, gender);
        logResult = (LoginRegisterResult) result;
        singleton.setUserPersonID(logResult.getPersonID());
        singleton.setAuthToken(logResult.getAuthToken());
        result = server.getEvents(logResult.getAuthToken());
        eventResult = (EventResult) result;
        result = server.getPeople(logResult.getAuthToken(), logResult.getPersonID());
        personResult = (PersonResult) result;
        singleton.setPersons(personResult.getPersons());
        singleton.setEvents(eventResult.getEvents());
        singleton.prepareData();
        HashSet<Person> people = singleton.searchPeople("a");
        people = singleton.searchPeople("bRaNdON");
        assertEquals(people.size(),1);
        //assertEquals(people.size(),1,"Brandon should be only person with that name");

    }

    boolean isChild(Person child,Person parent) {
        if(parent.getChildID().equals(child.getPersonID())) {
            return true;
        }
        else {
            return false;
        }
    }
}