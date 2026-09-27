package mthree.com.SchoolDatabase.model;

public class StudentClassCount {
    private String CourseCode;
    private int StudentCount;

    public String getCourseCode() {
        return CourseCode;
    }

    public void setCourseCode(String courseCode) {
        CourseCode = courseCode;
    }

    public int getStudentCount() {
        return StudentCount;
    }

    public void setStudentCount(int studentCount) {
        StudentCount = studentCount;
    }
}