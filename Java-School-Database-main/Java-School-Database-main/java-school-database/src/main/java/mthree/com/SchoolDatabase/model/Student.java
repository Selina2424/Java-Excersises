package mthree.com.SchoolDatabase.model;

public class Student {
    private int studentId;
    private String studentFName;
    private String studentLName;

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentFName() {
        return studentFName;
    }

    public void setStudentFName(String studentFName) {
        this.studentFName = studentFName;
    }

    public String getStudentLName() {
        return studentLName;
    }

    public void setStudentLName(String studentLName) {
        this.studentLName = studentLName;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", studentFName='" + studentFName + '\'' +
                ", studentLName='" + studentLName + '\'' +
                '}';
    }
}
