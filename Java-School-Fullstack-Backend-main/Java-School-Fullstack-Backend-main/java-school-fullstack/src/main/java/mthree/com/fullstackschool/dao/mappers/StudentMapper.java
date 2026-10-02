package mthree.com.fullstackschool.dao.mappers;

import mthree.com.fullstackschool.model.Student;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentMapper implements RowMapper<Student> {
    @Override
    public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
        //YOUR CODE STARTS HERE

        Student student = new Student();
        student.setStudentId(rs.getInt("sid"));
        student.setStudentFirstName(rs.getString("fName"));
        student.setStudentLastName(rs.getString("lName"));
        return student;

        //YOUR CODE ENDS HERE
    }
}
