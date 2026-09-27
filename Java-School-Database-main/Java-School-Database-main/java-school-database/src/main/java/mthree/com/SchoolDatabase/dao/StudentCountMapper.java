package mthree.com.SchoolDatabase.dao;

import mthree.com.SchoolDatabase.model.StudentClassCount;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentCountMapper implements RowMapper<StudentClassCount> {
    @Override
    public StudentClassCount mapRow(ResultSet rs, int rowNum) throws SQLException {
        StudentClassCount studentClassCount = new StudentClassCount();
        studentClassCount.setCourseCode(rs.getString("COURSECODE"));
        studentClassCount.setStudentCount(rs.getInt("NUMSTUDENTS"));
        return studentClassCount;
    }
}