package mthree.com.fullstackschool.service;

import mthree.com.fullstackschool.model.Course;

import java.util.List;

public interface CourseServiceInterface {
    List<Course> getAllCourses();

    Course getCourseById(int id);

    Course addNewCourse(Course course);

    Course updateCourseData(int id, Course course);

    void deleteCourseById(int id);


}
