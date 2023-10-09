package com.example.tawriqapp.Model;

import java.io.Serializable;

public class Slide extends Item implements Serializable {

    private String CourseTeacher;

    public Slide() {
    }

    public String getCourseTeacher() {
        return CourseTeacher;
    }

    public void setCourseTeacher(String courseTeacher) {
        CourseTeacher = courseTeacher;
    }
}
