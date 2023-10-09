package com.example.tawriqapp.Model;

import java.io.Serializable;

public class Item implements Serializable {

    protected String
            itemID,
            studentID,
            itemStyle,
            coverPhotoUrl_1,
            coverPhotoUrl_2,
            coverPhotoUrl_3,
            itemTitle,
            ownerName,
            quality,
            academicYear,
            semester,
            uploadDate;

    protected Course course = new Course();
    protected Program program = new Program();

    public Item() {
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getItemStyle() {
        return itemStyle;
    }

    public void setItemStyle(String itemStyle) {
        this.itemStyle = itemStyle;
    }

    public String getCoverPhotoUrl_1() {
        return coverPhotoUrl_1;
    }

    public void setCoverPhotoUrl_1(String coverPhotoUrl_1) {
        this.coverPhotoUrl_1 = coverPhotoUrl_1;
    }

    public String getCoverPhotoUrl_2() {
        return coverPhotoUrl_2;
    }

    public void setCoverPhotoUrl_2(String coverPhotoUrl_2) {
        this.coverPhotoUrl_2 = coverPhotoUrl_2;
    }

    public String getCoverPhotoUrl_3() {
        return coverPhotoUrl_3;
    }

    public void setCoverPhotoUrl_3(String coverPhotoUrl_3) {
        this.coverPhotoUrl_3 = coverPhotoUrl_3;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public static Item convertToItemData(Book book, Slide slide) {
        Item item;
        if (book != null) {
            item = book;
            item.setOwnerName(book.getBookAuthor());
        } else {
            item = slide;
            item.setOwnerName(slide.getCourseTeacher());
        }
        return item;
    }
}
