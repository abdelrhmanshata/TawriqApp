package com.example.tawriqapp.Model;

import java.io.Serializable;

public class Student implements Serializable {

    private String
            ID,
            Email,
            Password,
            FirstName,
            LastName,
            Phone,
            ImageUri,
            Status,
            TypingTo,
            AboutMe;

    public Student() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getTypingTo() {
        return TypingTo;
    }

    public void setTypingTo(String typingTo) {
        TypingTo = typingTo;
    }

    public String getAboutMe() {
        return AboutMe;
    }

    public void setAboutMe(String aboutMe) {
        AboutMe = aboutMe;
    }

    public interface Register {
        void Register(Student student);
    }

    public interface Login {
        void Login(String email, String password);
    }

    public interface UpdateAccount {
        void UpdateAccount(Student student);
    }

    public interface WriteComment {
        void ViewComments(String ItemID);

        void WriteComment(Comment comment);
    }

    public interface SearchItem {
        void SearchItem(String ItemTitle);
    }

    public interface ViewAllItem {
        void ViewAllItem();
    }

    public interface ViewMessages {
        void ViewMessages(String Sender, String Receiver);

        void sendPrivetMessage(String Sender, String Receiver, String Msg);
    }

    public interface AddItem {
        void AddItem(Item item);
    }

    public interface UpdateItem {
        void UpdateItem(Item item);
    }

    public interface DeleteItem {
        void DeleteItem(Item item);
    }


}
