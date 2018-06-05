package com.uk.cmo.Model;

/**
 * Created by usman on 11-02-2018.
 */

public class Person {
    //Todo : Use a boolean to identify btw representative or family member
    // Todo : if representative fetch its family members from MEMBERS node
    //Todo : if member display relation with representative (along with name of repres)
    //Todo : For that store Uid of the representative as well and name of representative
    String name;
    String email_id;
    String contact_number;
    String profile_pic;
    boolean married;
    boolean member;
    String id;
    String address;
    String blood_group;
    String relation;
    WorkingPerson workingPerson;
    StudyingPerson studyingPerson;

    public Person() {
    }


    public boolean isMarried() {
        return married;
    }

    public void setMarried(boolean married) {
        this.married = married;
    }

    public boolean isMember() {
        return member;
    }

    public void setMember(boolean member) {
        this.member = member;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public WorkingPerson getWorkingPerson() {
        return workingPerson;
    }

    public void setWorkingPerson(WorkingPerson workingPerson) {
        this.workingPerson = workingPerson;
    }

    public StudyingPerson getStudyingPerson() {
        return studyingPerson;
    }

    public void setStudyingPerson(StudyingPerson studyingPerson) {
        this.studyingPerson = studyingPerson;
    }
}
