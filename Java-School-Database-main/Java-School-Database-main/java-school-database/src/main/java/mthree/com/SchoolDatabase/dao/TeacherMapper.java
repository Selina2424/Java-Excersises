package mthree.com.SchoolDatabase.dao;

import mthree.com.SchoolDatabase.model.Teacher;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeacherMapper implements RowMapper<Teacher> {
    @Override
    public Teacher mapRow(ResultSet rs, int rowNum) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setTeacherFName(rs.getString("TFNAME"));
        teacher.setTeacherLName(rs.getString("TLNAME"));
        return teacher;
    }
}
