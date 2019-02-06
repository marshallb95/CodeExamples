# solutions.py
"""Volume 3: SQL 2.
Brandon Marshalll
Section 2
November 15, 2018
"""
import sqlite3 as sql

# Problem 1
def prob1(db_file="students.db"):
    """Query the database for the list of the names of students who have a
    'B' grade in any course. Return the list.

    Parameters:
        db_file (str): the name of the database to connect to.

    Returns:
        (list): a list of strings, each of which is a student name.
    """
    try:
        with sql.connect(db_file) as conn:
            cur = conn.cursor()
            #query for student who have B in any course
            cur.execute("SELECT SI.StudentName FROM StudentInfo as SI INNER JOIN StudentGrades as SG ON SI.StudentID = SG.StudentID WHERE SG.Grade == 'B';")
            grades = cur.fetchall()
    finally:
        conn.close()
    #turn result set into list of strings of student names
    for i in range(0,len(grades)):
        grades[i] = grades[i][0]
    return grades
    raise NotImplementedError("Problem 1 Incomplete")


# Problem 2
def prob2(db_file="students.db"):
    """Query the database for all tuples of the form (Name, MajorName, Grade)
    where 'Name' is a student's name and 'Grade' is their grade in Calculus.
    Only include results for students that are actually taking Calculus, but
    be careful not to exclude students who haven't declared a major.

    Parameters:
        db_file (str): the name of the database to connect to.

    Returns:
        (list): the complete result set for the query.
    """
    try:
        with sql.connect(db_file) as conn:
            cur = conn.cursor()
            #query for asked tuple, use outer join first to include students who haven't included declared major
            cur.execute("SELECT SI.StudentName, MI.MajorName, SG.Grade FROM StudentInfo as SI LEFT OUTER JOIN MajorInfo as MI ON SI.MajorID == MI.MajorID INNER JOIN StudentGrades as SG ON SI.StudentID = SG.StudentID WHERE SG.CourseID = 1;") 
            info = cur.fetchall()
    finally:
        conn.close()
    return info
    raise NotImplementedError("Problem 2 Incomplete")


# Problem 3
def prob3(db_file="students.db"):
    """Query the database for the list of the names of courses that have at
    least 5 students enrolled in them.

    Parameters:
        db_file (str): the name of the database to connect to.

    Returns:
        ((list): a list of strings, each of which is a course name.
    """
    try:
        with sql.connect(db_file) as conn:
            cur = conn.cursor()
            #query for courses with at least 5 students in them
            cur.execute("SELECT CI.CourseName FROM StudentGrades as SG INNER JOIN CourseInfo AS CI ON SG.CourseID == CI.CourseID GROUP BY SG.CourseID HAVING COUNT(*) >= 5;")
            info = cur.fetchall()
    finally:
        conn.close()
    #turn result set into list of strings of course names
    for i in range(0,len(info)):
        info[i] = info[i][0]
    return info
    raise NotImplementedError("Problem 3 Incomplete")


# Problem 4
def prob4(db_file="students.db"):
    """Query the given database for tuples of the form (MajorName, N) where N
    is the number of students in the specified major. Sort the results in
    descending order by the counts N, then in alphabetic order by MajorName.

    Parameters:
        db_file (str): the name of the database to connect to.

    Returns:
        (list): the complete result set for the query.
    """
    try:
        with sql.connect(db_file) as conn:
            cur = conn.cursor()
            #query for asked tuple, use outer join to include students that haven't declared a major
            cur.execute("SELECT MI.MajorName, COUNT(*) AS num_major FROM StudentInfo AS SI LEFT OUTER JOIN MajorInfo AS MI ON SI.MajorID == MI.MajorID GROUP BY SI.MajorID ORDER BY num_major ASC, Mi.MajorName ASC;")
            info = cur.fetchall()
    finally:
        conn.close()
    return info   
    raise NotImplementedError("Problem 4 Incomplete")


# Problem 5
def prob5(db_file="students.db"):
    """Query the database for tuples of the form (StudentName, MajorName) where
    the last name of the specified student begins with the letter C.

    Parameters:
        db_file (str): the name of the database to connect to.

    Returns:
        (list): the complete result set for the query.
    """
    try:
        with sql.connect(db_file) as conn:
            cur = conn.cursor()
            #query for requested tuple, '%_% C_%' finds abitrary first name, with last name that starts with C
            cur.execute("SELECT SI.StudentName, MI.MajorName FROM StudentInfo AS SI LEFT OUTER JOIN MajorInfo AS MI ON SI.MajorID == MI.MajorID WHERE StudentName Like '%_% C_%';")
            info = cur.fetchall()
    finally:
        conn.close()
    return info
    raise NotImplementedError("Problem 5 Incomplete")


# Problem 6
def prob6(db_file="students.db"):
    """Query the database for tuples of the form (StudentName, N, GPA) where N
    is the number of courses that the specified student is in and 'GPA' is the
    grade point average of the specified student according to the following
    point system.

        A+, A  = 4.0    B  = 3.0    C  = 2.0    D  = 1.0
            A- = 3.7    B- = 2.7    C- = 1.7    D- = 0.7
            B+ = 3.4    C+ = 2.4    D+ = 1.4

    Order the results from greatest GPA to least.

    Parameters:
        db_file (str): the name of the database to connect to.

    Returns:
        (list): the complete result set for the query.
    """
    try:
        with sql.connect(db_file) as conn:
            cur = conn.cursor()
            #query for requested tuple. AVG with case inside returns the avg based off the grouping, which is the studentID, Inner Join makes sure all needed data is in the table
            cur.execute("SELECT SI.StudentName,COUNT(*), AVG(CASE SG.Grade WHEN 'A+' THEN 4.0 WHEN 'A' THEN 4.0 WHEN 'A-' THEN 3.7 WHEN 'B+' THEN 3.4 WHEN 'B' THEN 3.0 WHEN 'B-' THEN 2.7 WHEN 'C+' THEN 2.4 WHEN 'C' THEN 2.0 WHEN 'C-' THEN 1.7 WHEN 'D+' THEN 1.4 WHEN 'D' THEN 1.0 WHEN 'D-' THEN 0.7 ELSE 0 END) as avg FROM StudentInfo as SI INNER JOIN StudentGrades AS SG ON SI.StudentID == SG.StudentID GROUP BY SG.StudentID ORDER BY avg DESC")
            info = cur.fetchall()
    finally:
        conn.close()
    return info
    raise NotImplementedError("Problem 6 Incomplete")

