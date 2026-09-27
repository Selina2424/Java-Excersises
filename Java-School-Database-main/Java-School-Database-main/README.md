# Code Assignment: SQL Queries
## Overview

In this assignment, you will write SQL statements to retrieve specific data from an existing database.

### Database Information

The database for this assignment is embedded in the code provided in the GitHub Classroom repository, so you do not need to set up the database for use with this assignment. 

To see the database structure and the existing data in each table, see the page at <a href="DatabaseInformation.md">DatabaseInformation.md</a>.

> #### ⚠ Important!
>
> Because of how the database is set up in the program, the data in the database will reset each time you build the code. Changes to the data (including insertions, updates, and deletions) will not persist between builds. For this reason, you should complete the tasks in the order listed, because later queries may depend on earlier queries.

### Additional Tips

In this activity, you will be writing queries that run using Java's sql library, rather than running directly in an RDBMS like MySQL. Because you are working with Java, you must follow these guidelines:

- Because the SQL statement is inside double quotation marks (" "), any string values must be inside single quotation marks (' ').
- Strings are case sensitive, so `'john smith'` is not the same as `'John Smith'`.
- Line breaks are permitted but optional. If you hit the ENTER key while keying a SQL statement into the Java statement, the IDE may add additional characters such as extra quotation marks or newline breaks (`/n`). These will not affect how the SQL statement runs.
- Semi-colons are optional.
- The Java tests look at the output of each query to determine whether or not the tests pass. While the SQL syntax must produce the expected results, variation in the syntax is allowed.

You may, if you wish, test the SQL statements in MySQL Workbench before adding them to the Java statement. 

* The schema and data for the complete database are included inside the Resource folder in the Java project. These can be used to build the database in MySQL.

## Problem Statement

The methods you will implement are located within the dao package, in the *SchoolDaoImpl.java* file. You will update these methods to include a SQL statement that returns the information requested for that query.

1. `allStudents()`: Write a query that returns all students (first name, last name only) sorted by last name.
2. `CS_Courses()`: Write a query that lists the course code and course name for all courses in the Computer Science department.
3. `teacherCountByDept()`: Write a query that displays the department and the total number of teachers assigned to each department. Name the aggregate field `teacherCount`.
4. `studentsPerClass()`: Write a query that lists the course code and course description for each course, with the number of students enrolled in each course.
5. This step includes two parts. Both parts must be completed to pass the test.
   - Part 1: `addStudent()`: Write a query to add the student Robert Dylan to the student table.
   - Part 2: `addStudentToClass()`: Write a query to add Robert Dylan to CS148.

6. `editCourseDescription()`: Write a query to change the course description for course CS305 to `Advanced Python with Flask`.
7. `deleteTeacher()`: Write a query to remove Lewis Carroll as a teacher.

## Instructions

Update only the methods in the existing *SchoolDaoImpl.java* class in the starter code provided. Do not change the code in any other file provided.

Update each method as necessary to implement the requested query for that method, adding the SQL statement inside the  quotes of the statement, like: 

```java
String sql = "Add SQL statement here";
```

* Do not change any code outside of the quotation marks provided.  Only add code inside the quotation marks provided.
* Focus on one method (query statement) at a time. After committing the code and verifying that the test for that method passes,  start work on the next method.

## Steps to Solve the Problem

### Step 1: Verify the Setup Requirements
- Java version 11 installed with the JAVA_HOME path variable
- An IDE with required plugins for Java
- Git
- A GitHub account

### Step 2: Access GitHub Classroom and the Repository
1. Your instructor will provide a link for the assessment in GitHub Classroom.
2. Open the link and authorize access using your GitHub account.
3. Clone the repository to a local folder.
   - Instructions for working with GitHub repositories are provided in the Git and GitHub section of the course.
4. Open the project folder in your selected IDE.

### Step 3: Solve the Problem
Read the problem statement carefully to be sure you understand what the code is expected to do.

- You are encouraged to sketch out a flowchart, use pseudocode, or create class diagrams to plan out the code.

Open the indicated .java file on your computer. Make changes only to that file.

The .java file includes comments to indicate where you should add your code.
- All new code must be added inside the placeholders provided.
- Do not delete the placeholder comments.
- Do not change any existing code elsewhere in the project.
- Do not alter existing class names, interface names, method signatures, or attribute names.
- Use Java naming conventions for the code you create.
The placeholders for your code will look like:


    //YOUR CODE STARTS HERE


    //YOUR CODE ENDS HERE


All new code should be added inside these placeholders. For this specific assignment, the SQL statement must be inside the quotation marks in the `String sql = "";` statement provided in the code.

### Step 4: Submit Your Work
1. Code solutions to the problem statements and run the code in the local IDE to check it as you work.
2. Incrementally commit the changes to the repository, labeling each commit numerically (Commit1, Commit2, etc.).
    - The number of commits can be as high as necessary; commit as frequently as you wish.
3. Each commit will be tested automatically against the relevant test cases. Open the repository in GitHub to view the test results.
    - A checkmark indicates that a test passed.
    - An X indicates that the test failed.
4. If any test fails, return to the local code to make corrections or add more code, then recommit the changes.
5. When the code passes all tests, commit one more time, labeling the last commit as "final commit."
    - You may need to make a minor edit to the code, such as adding a comment, so that Git knows the code is different.
6. Copy the URL for the GitHub repository and submit it to the assessment page in Engage.
7. Your instructor may also ask you to submit a pull request on the repository.`
