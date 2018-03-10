package com.uk.cmo.Model;

/**
 * Created by usman on 11-02-2018.
 */

public class WorkingPerson  {
    String occupation;
    String workplace_Address;
    String workplace_contact_num;
    String workplace_emailId;
    String qualifications;

    public WorkingPerson() {
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getWorkplace_Address() {
        return workplace_Address;
    }

    public void setWorkplace_Address(String workplace_Address) {
        this.workplace_Address = workplace_Address;
    }

    public String getWorkplace_contact_num() {
        return workplace_contact_num;
    }

    public void setWorkplace_contact_num(String workplace_contact_num) {
        this.workplace_contact_num = workplace_contact_num;
    }

    public String getWorkplace_emailId() {
        return workplace_emailId;
    }

    public void setWorkplace_emailId(String workplace_emailId) {
        this.workplace_emailId = workplace_emailId;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }
}
