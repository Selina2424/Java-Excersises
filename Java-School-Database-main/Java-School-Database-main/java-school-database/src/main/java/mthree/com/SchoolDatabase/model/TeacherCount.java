package mthree.com.SchoolDatabase.model;

public class TeacherCount {
    private String dept;
    private int count;


    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "TeacherCount{" +
                "dept='" + dept + '\'' +
                ", count=" + count +
                '}';
    }
}
