package mthree.com.fullstackschool.dao;

import mthree.com.fullstackschool.dao.mappers.StudentMapper;
import mthree.com.fullstackschool.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.sql.*;
import java.util.List;
import java.util.Objects;

@Repository
public class StudentDaoImpl implements StudentDao {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public StudentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Student createNewStudent(Student student) {
        //YOUR CODE STARTS HERE


        final String sql = "INSERT INTO student (fName, lName) VALUES (?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, student.getStudentFirstName());
            ps.setString(2, student.getStudentLastName());
            return ps;
        }, keyHolder);
        student.setStudentId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return student;


        //YOUR CODE ENDS HERE
    }

    @Override
    public List<Student> getAllStudents() {
        //YOUR CODE STARTS HERE


        return jdbcTemplate.query("SELECT * FROM student", new StudentMapper());

        //YOUR CODE ENDS HERE
    }

    @Override
    public Student findStudentById(int id) {
        //YOUR CODE STARTS HERE

        return jdbcTemplate.queryForObject("SELECT * FROM student WHERE sid = ?", new StudentMapper(), id);

        //YOUR CODE ENDS HERE
    }

    @Override
    public void updateStudent(Student student) {
        //YOUR CODE STARTS HERE
        jdbcTemplate.update("UPDATE student SET fName = ?, lName = ? WHERE sid = ?",
                student.getStudentFirstName(), student.getStudentLastName(), student.getStudentId());

        //YOUR CODE ENDS HERE
    }

    @Override
    public void deleteStudent(int id) {
        //YOUR CODE STARTS HERE
        jdbcTemplate.update("DELETE FROM course_student WHERE student_id = ?", id);
        jdbcTemplate.update("DELETE FROM student WHERE sid = ?", id);

        //YOUR CODE ENDS HERE
    }

    @Override
    public void addStudentToCourse(int studentId, int courseId) {
        //YOUR CODE STARTS HERE

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM course_student WHERE student_id = ? AND course_id = ?",
                Integer.class, studentId, courseId);
        if (count != null && count > 0) {
            throw new org.springframework.dao.DuplicateKeyException("Student already enrolled in course");
        }
        jdbcTemplate.update("INSERT INTO course_student (student_id, course_id) VALUES (?, ?)", studentId, courseId);

        //YOUR CODE ENDS HERE
    }

    @Override
    public void deleteStudentFromCourse(int studentId, int courseId) {
        //YOUR CODE STARTS HERE

        jdbcTemplate.update("DELETE FROM course_student WHERE student_id = ? AND course_id = ?", studentId, courseId);

        //YOUR CODE ENDS HERE
    }
}
