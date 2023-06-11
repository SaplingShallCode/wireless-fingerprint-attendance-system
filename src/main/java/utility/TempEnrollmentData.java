package utility;

/**
 * Temporary data to be used for enrollment of attendee.
 */
public class TempEnrollmentData {
    private String full_name;
    private Integer fingerprint_id;

    private String first_name;
    private String middle_name;
    private String last_name;
    private Short age;
    private String gender;
    private String phone_number;
    private String address;


    public String getFullName() {
        return full_name;
    }

    public int getFingerprintId() { return fingerprint_id; }

    public String getFirstName() {
        return first_name;
    }

    public String getMiddleName() {
        return middle_name;
    }

    public String getLastName() {
        return last_name;
    }

    public Short getAge() { return age; }

    public String getGender() {
        return gender;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public String getAddress() {
        return address;
    }

    public void buildEnrolleeName(String first_name, String middle_name, String last_name) {
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
        this.full_name = String.format("%s %s %s", first_name, middle_name, last_name);
    }

    public void buildEnrolleeInfo(String age, String gender, String phone_number, String address) {
        this.age = Short.parseShort(age);
        this.gender = gender;
        this.phone_number = phone_number;
        this.address = address;
    }

    public void setFingerprintId(String fingerprint_id) {
        this.fingerprint_id = Integer.parseInt(fingerprint_id);
    }
}
