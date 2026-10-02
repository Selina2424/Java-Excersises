package mthree.com.fullstackschool.dao;

import mthree.com.fullstackschool.dao.mappers.CourseMapper;
import mthree.com.fullstackschool.model.Course;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CourseDaoImpl implements CourseDao {

    private final JdbcTemplate jdbcTemplate;

    public CourseDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Course createNewCourse(Course course) {
        //YOUR CODE STARTS HERE
        final String sql = "INSERT INTO course (courseCode, courseDesc, teacherId) VALUES (?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, course.getCourseName());
            ps.setString(2, course.getCourseDesc());
            if (course.getTeacherId() == 0) ps.setNull(3, java.sql.Types.INTEGER);
            else ps.setInt(3, course.getTeacherId());
            return ps;
        }, keyHolder);
        course.setCourseId(keyHolder.getKey().intValue());
        return course;

        //YOUR CODE ENDS HERE
    }

    @Override
    public List<Course> getAllCourses() {
        //YOUR CODE STARTS HERE


        return jdbcTemplate.query("SELECT * FROM course", new CourseMapper());

        //YOUR CODE ENDS HERE
    }

    @Override
    public Course findCourseById(int id) {
        //YOUR CODE STARTS HERE

        return jdbcTemplate.queryForObject("SELECT * FROM course WHERE cid = ?", new CourseMapper(), id);

        //YOUR CODE ENDS HERE
    }

    @Override
    public void updateCourse(Course course) {
        //YOUR CODE STARTS HERE

        jdbcTemplate.update("UPDATE course SET courseCode = ?, courseDesc = ?, teacherId = ? WHERE cid = ?",
                course.getCourseName(), course.getCourseDesc(),
                course.getTeacherId() == 0 ? null : course.getTeacherId(), course.getCourseId());
        //YOUR CODE ENDS HERE
    }

    @Override
    public void deleteCourse(int id) {
        //YOUR CODE STARTS HERE

        deleteAllStudentsFromCourse(id);
        jdbcTemplate.update("DELETE FROM course WHERE cid = ?", id);

        //YOUR CODE ENDS HERE
    }

    @Override
    public void deleteAllStudentsFromCourse(int courseId) {
        //YOUR CODE STARTS HERE

        jdbcTemplate.update("DELETE FROM course_student WHERE course_id = ?", courseId);

        //YOUR CODE ENDS HERE
    }
}
