package android.example.speellees.activities.domain;

public class Client {
    private String clientId;
    private String firstname;
    private String lastname;
    private String birthdate;
    private String remark;

    public Client(String clientId, String firstname, String lastname, String birthdate, String remark) {
        this.clientId = clientId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthdate = birthdate;
        this.remark = remark;
    }

    public Client() {

    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
