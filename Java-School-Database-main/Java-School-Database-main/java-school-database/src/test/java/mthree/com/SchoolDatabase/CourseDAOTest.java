package mthree.com.SchoolDatabase;

import mthree.com.SchoolDatabase.dao.SchoolDao;
import mthree.com.SchoolDatabase.dao.SchoolDaoImpl;
import mthree.com.SchoolDatabase.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
public class CourseDAOTest {

    private JdbcTemplate jdbcTemplate;
    private SchoolDao dao;

    @Autowired
    public CourseDAOTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        dao = new SchoolDaoImpl(jdbcTemplate);
    }

    @Test
    @DisplayName("Test for all students sorted by last name")
    public void ReturnAllStudentTests() {
        List<Student> students = dao.allStudents();
        assertEquals(8, students.size());
        assertEquals("Abbandonato", students.get(0).getStudentLName());
    }

    @Test
    @DisplayName("Test for all Computer Science Courses")
    public void GetAllComputerSciCourses() {
        List<Course> CSC = dao.csCourses();
        assertEquals(9, CSC.size());
        //contains CS
    }

    @Test
    @DisplayName("Test teacher count by department")
    public void GetTeacherCountByDept2() {
        List<TeacherCount> TC = dao.teacherCountByDept();
        int i = 0;
        for (TeacherCount englishClassCount : TC) {
            if (englishClassCount.getDept() != null && englishClassCount.getDept().contains("English")) {
                i++;
            }
        }
        //cs = Computer Science
        int j = 0;
        for (TeacherCount csClassCount : TC) {
            if (csClassCount.getDept() != null && csClassCount.getDept().contains("Computer Science")) {
                j++;
            }
        }
        assertTrue(i != 0);
        assertTrue(j != 0);
    }

    @Test
    @DisplayName("Test student count by class")
    public void testStudentCounts() {
        List<StudentClassCount> studentCounts = dao.studentsPerClass();
        int i = 0;
        for (StudentClassCount sc : studentCounts) {
            if (sc.getCourseCode().contains("CS305")) {
                assertEquals(4, sc.getStudentCount());
            }
            if (sc.getCourseCode().contains("CS148")) {
                assertEquals(6, sc.getStudentCount());
            }
        }
        assertEquals(7, studentCounts.size());
    }

    @Test
    @DisplayName("Test if Robert Dylan was added to course CS148")
    public void addStudentToCourse() {
        dao.addStudent();  //does not exec if run all together
        dao.addStudentToClass();
        List<Student> students = dao.studentsCS148();
        System.out.println(students);
        int i = 0;
        assertEquals(7, students.size());

        for (Student s : students) {
            if (s.getStudentLName().contains("Dylan")) {
                i++;
            }
        }
        assertTrue(i != 0);
    }


    @Test
    @DisplayName("Test if name updated to Advanced Python with Flask for CS305")
    public void editcourseCode() {
        dao.editCourseDescription();
        List<Course> courses = dao.csCourses();
        int i = 0;
        for (Course cs : courses) {
            if (cs.getCourseDesc().contains("Advanced Python with Flask")) {
                i++;
            }
        }
        assertTrue (i == 1 );
        int j = 0;
        for (Course cs : courses) {
            if (cs.getCourseCode().contains("CS305")) {
                j++;
            }
        }
        assertTrue(j != 0);
    }

    @Test
    @DisplayName("Test if teacher (Mitchell) still exists and RecordSet decreased by 1")
    public void deleteTeacher() {
        dao.deleteTeacher();
        List<Teacher> teachers = dao.listAllTeachers();
        assertEquals(9, teachers.size());
        int i = 0;
        for (Teacher ts : teachers) {
            if (ts.getTeacherLName().contains("Mitchell")) {
                i++;
            }
        }
        assertFalse(i != 0);
    }

}
