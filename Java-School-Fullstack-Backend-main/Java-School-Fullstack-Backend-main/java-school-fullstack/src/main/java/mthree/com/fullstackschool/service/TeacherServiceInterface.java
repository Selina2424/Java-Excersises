package mthree.com.fullstackschool.service;

import mthree.com.fullstackschool.model.Teacher;

import java.util.List;

public interface TeacherServiceInterface {
    List<Teacher> getAllTeachers();

    Teacher getTeacherById(int id);

    Teacher addNewTeacher(Teacher teacher);

    Teacher updateTeacherData(int id, Teacher teacher);

    void deleteTeacherById(int id);
}
