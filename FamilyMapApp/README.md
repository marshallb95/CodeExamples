The Family Map App is a complete server and client. The server is split up to work as both a functioning website that gives the user a GUI with which to interact witht the API calls, as well as receive standard HTTP requests, allowing both DELETE, POST, and GET requests for the Family Map Information.

The Server is divided so that API calls are split into their respective handlers, which convert the HTTP requests into Request objects, which are then passed to the Service calls. These service calls create, modify, and delete the needed objects for the family map client, such as Events, Users, and Persons. 
Requests that involve user information (such as retrieving ancestors and events), require an AuthToken, which is returned to the user upon successful login or registration.

The Client makes the needed API calls for the user, and upon successful login, generates a Google Map with icons marking all of the user's ancestor's events. Events are color coordinated, and upon clicking an event information about the event is displayed below. Clicking the event information creates a new activity that shows that Ancestor's information for that particular event.
As well, user's can filter events and search for a specific ancestor or event.
