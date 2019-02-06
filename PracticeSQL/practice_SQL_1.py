# sql1.py
"""Volume 3: SQL 1 (Introduction).
Brandon Marshall
Section 2
November 14, 2018
"""
import sqlite3 as sql
import csv
import numpy as np
from matplotlib import pyplot as plt
# Problems 1, 2, and 4
def student_db(db_file="students.db", student_info="student_info.csv",
                                      student_grades="student_grades.csv"):
    """Connect to the database db_file (or create it if it doesn’t exist).
    Drop the tables MajorInfo, CourseInfo, StudentInfo, and StudentGrades from
    the database (if they exist). Recreate the following (empty) tables in the
    database with the specified columns.

        - MajorInfo: MajorID (integers) and MajorName (strings).
        - CourseInfo: CourseID (integers) and CourseName (strings).
        - StudentInfo: StudentID (integers), StudentName (strings), and
            MajorID (integers).
        - StudentGrades: StudentID (integers), CourseID (integers), and
            Grade (strings).

    Next, populate the new tables with the following data and the data in
    the specified 'student_info' 'student_grades' files.

                MajorInfo                         CourseInfo
            MajorID | MajorName               CourseID | CourseName
            -------------------               ---------------------
                1   | Math                        1    | Calculus
                2   | Science                     2    | English
                3   | Writing                     3    | Pottery
                4   | Art                         4    | History

    Finally, in the StudentInfo table, replace values of −1 in the MajorID
    column with NULL values.

    Parameters:
        db_file (str): The name of the database file.
        student_info (str): The name of a csv file containing data for the
            StudentInfo table.
        student_grades (str): The name of a csv file containing data for the
            StudentGrades table.
    """
    with open("student_info.csv", 'r') as infile:
        info_rows = list(csv.reader(infile))
    with open("student_grades.csv", 'r') as infile:
        grades_rows = list(csv.reader(infile))
    
    try:
        with sql.connect(db_file) as conn:
            cur = conn.cursor()
            rows_major = [(1, "Math"), (2, "Science"), (3, "Writing"), (4, "Art")]
            rows_course = [(1, "Calculus"), (2, "English"), (3, "Pottery"), (4, "History")]
            #delete tables(if they exists)
            cur.execute("DROP TABLE IF EXISTS MajorInfo")
            cur.execute("Drop TABLE IF EXISTS CourseInfo")
            cur.execute("DROP TABLE IF EXISTS Studentinfo") 
            cur.execute("DROP TABLE IF EXISTS StudentGrades")
            #create tables
            cur.execute("CREATE TABLE MajorInfo (MajorID INTEGER, MajorName TEXT)") 
            cur.execute("CREATE TABLE CourseInfo (CourseID INTEGER, CourseName TEXT)") 
            cur.execute("CREATE TABLE StudentInfo (StudentID INTEGER, StudentName TEXT, MajorID INTEGER)")
            cur.execute("CREATE TABLE StudentGrades (StudentID INTEGER, CourseID INTEGER, Grade TEXT)") 
            #insert values into various tables 
            cur.executemany("INSERT INTO MajorInfo VALUES(?,?);", rows_major)
            cur.executemany("INSERT INTO CourseInfo VALUES(?,?);", rows_course)
            cur.executemany("INSERT INTO StudentInfo VALUES(?,?,?);", info_rows)
            cur.executemany("INSERT INTO StudentGrades VALUES(?,?,?);", grades_rows)
            #change -1 values to NULL
            cur.execute("UPDATE StudentInfo SET MajorID = NULL WHERE MajorID == -1;")
    finally:
        conn.close()
    return        
    raise NotImplementedError("Problem 1 Incomplete")


# Problems 3 and 4
def earthquakes_db(db_file="earthquakes.db", data_file="us_earthquakes.csv"):
    """Connect to the database db_file (or create it if it doesn’t exist).
    Drop the USEarthquakes table if it already exists, then create a new
    USEarthquakes table with schema
    (Year, Month, Day, Hour, Minute, Second, Latitude, Longitude, Magnitude).
    Populate the table with the data from 'data_file'.

    For the Minute, Hour, Second, and Day columns in the USEarthquakes table,
    change all zero values to NULL. These are values where the data originally
    was not provided.

    Parameters:
        db_file (str): The name of the database file.
        data_file (str): The name of a csv file containing data for the
            USEarthquakes table.
    """
    with open("us_earthquakes.csv") as infile:
        rows = list(csv.reader(infile))
    try:
        with sql.connect(db_file) as conn:
            cur = conn.cursor()
            #delete tables if they exist
            cur.execute("DROP TABLE IF EXISTS USEarthquakes")
            #create tables
            cur.execute("CREATE TABLE USEarthquakes (Year INTEGER, Month INTEGER, Day INTEGER, Hour INTEGER, Minute INTEGER, Second INTEGER, Latitude REAL, Longitude REAL, Magnitude REAL)")
            #insert data into the table
            cur.executemany("INSERT INTO USEarthquakes VALUES(?,?,?,?,?,?,?,?,?);", rows)
            #delete rows where magntitude is 0
            cur.execute("DELETE FROM USEarthquakes WHERE Magnitude == 0;")
            #change NULL hours to 0
            cur.execute("UPDATE USEarthquakes SET Day = NULL WHERE Day == 0;")
            cur.execute("UPDATE USEarthquakes SET Hour = NULL WHERE Hour == 0;")
            cur.execute("UPDATE USEarthquakes SET Minute = NULL WHERE Minute == 0;")
            cur.execute("UPDATE USEarthquakes SET Second = NULL WHERE Second == 0;")
    finally:
        conn.close()
    return
    raise NotImplementedError("Problem 3 Incomplete")


# Problem 5
def prob5(db_file="students.db"):
    """Query the database for all tuples of the form (StudentName, CourseName)
    where that student has an 'A' or 'A+'' grade in that course. Return the
    list of tuples.

    Parameters:
        db_file (str): the name of the database to connect to.

    Returns:
        (list): the complete result set for the query.
    """
    try:
        with sql.connect(db_file) as conn:
            cur = conn.cursor()
            #pull student name and coursename from their respective tables given the conditions
            cur.execute("SELECT SI.StudentName, CI.CourseName FROM StudentInfo AS SI, StudentGrades AS SG, CourseInfo AS CI WHERE SI.StudentID == SG.StudentID AND SG.CourseID == CI.CourseID AND (SG.Grade = 'A+' OR SG.Grade = 'A');")
            #store the information so we can return it
            tup = cur.fetchall()
    finally:
         conn.close()
    return tup
    raise NotImplementedError("Problem 5 Incomplete")


# Problem 6
def prob6(db_file="earthquakes.db"):
    """Create a single figure with two subplots: a histogram of the magnitudes
    of the earthquakes from 1800-1900, and a histogram of the magnitudes of the
    earthquakes from 1900-2000. Also calculate and return the average magnitude
    of all of the earthquakes in the database.

    Parameters:
        db_file (str): the name of the database to connect to.

    Returns:
        (float): The average magnitude of all earthquakes in the database.
    """
    try:
        with sql.connect(db_file) as conn:
            cur = conn.cursor()
            #get magnitudes from 1800-1899
            cur.execute("SELECT Magnitude FROM USEarthquakes WHERE Year <= 1899 AND Year >= 1800;")
            eighteen = cur.fetchall()
            #get magnitudes from 1900-1999
            cur.execute("SELECT Magnitude FROM USEarthquakes WHERE Year >= 1900 AND Year <= 1999;")
            nineteen = cur.fetchall()
            #get average magnitude of all earthquakes
            cur.execute("SELECT AVG(Magnitude) FROM USEarthquakes;")
            avg = cur.fetchall()
    finally:
        conn.close()
    #turn both list of tuples into 1d numpy arrays
    eighteen = np.ravel(eighteen)
    nineteen = np.ravel(nineteen)
    ax1 = plt.subplot(121)
    ax2 = plt.subplot(122)
    #plot histograms of the magnitudes of the earthquakes in the 1800's and 1900's
    ax1.hist(eighteen, edgecolor = 'k')
    ax1.set_ylabel("# of earthquakes")
    ax1.set_xlabel("magnitude of earthquake (as range)",fontsize = 10)
    ax1.set_title("1800's")
    ax2.hist(nineteen, edgecolor = 'k')
    ax2.set_xlabel("magnitude of earthquake (as range)",fontsize = 10)
    ax2.set_title("1900's")
    plt.show()
    return float(avg[0][0])
    raise NotImplementedError("Problem 6 Incomplete")

