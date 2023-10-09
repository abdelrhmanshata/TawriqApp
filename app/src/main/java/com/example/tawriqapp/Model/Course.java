package com.example.tawriqapp.Model;

import java.io.Serializable;

public class Course implements Serializable {

    private String
            courseName,
            courseCode;

    public Course() {
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
