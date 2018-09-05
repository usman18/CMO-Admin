package com.uk.cmo.Model;

/**
 * Created by usman on 11-02-2018.
 */

public class Person {
    private String name;
    private String name_lower_case;     //will be used for searching

    private String email_id;
    private String contact_number;
    private String profile_pic;
    private boolean married;
    private boolean member;
    private String id;
    private String address;
    private String blood_group;
    private String relation;


    private WorkingPerson workingPerson;
    private StudyingPerson studyingPerson;


    private String approved_by_id;    //id of the admin who approved the request
    private String approved_by_name;  // name of the admin who approved the request
    private long approved_at_time;          // time at which profile was approved

    private String declined_by_id;      // if the person is declined be some admin at any stage
    private String declined_by_name;    // name of the admin who declined the request
    private String declined_at_time;    // time at which this person's request was declined

    private long profile_created_at;    //time at which profile was created


    private boolean admin;
    private boolean legit;

    public Person() {
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public long getProfile_created_at() {
        return profile_created_at;
    }

    public void setProfile_created_at(long profile_created_at) {
        this.profile_created_at = profile_created_at;
    }

    public String getApproved_by_id() {
        return approved_by_id;
    }

    public void setApproved_by_id(String approved_by_id) {
        this.approved_by_id = approved_by_id;
    }

    public String getApproved_by_name() {
        return approved_by_name;
    }

    public void setApproved_by_name(String approved_by_name) {
        this.approved_by_name = approved_by_name;
    }

    public long getApproved_at_time() {
        return approved_at_time;
    }

    public void setApproved_at_time(long approved_at_time) {
        this.approved_at_time = approved_at_time;
    }

    public String getDeclined_by_id() {
        return declined_by_id;
    }

    public void setDeclined_by_id(String declined_by_id) {
        this.declined_by_id = declined_by_id;
    }

    public String getDeclined_by_name() {
        return declined_by_name;
    }

    public void setDeclined_by_name(String declined_by_name) {
        this.declined_by_name = declined_by_name;
    }

    public String getDeclined_at_time() {
        return declined_at_time;
    }

    public void setDeclined_at_time(String declined_at_time) {
        this.declined_at_time = declined_at_time;
    }

    public boolean isLegit() {
        return legit;
    }

    public void setLegit(boolean legit) {
        this.legit = legit;
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

    public String getName_lower_case() {
        return name_lower_case;
    }

    public void setName_lower_case(String name_lower_case) {
        this.name_lower_case = name_lower_case;
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
