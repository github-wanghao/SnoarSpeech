package example.snoarspeech.model;

/**
 * Created by owen_ on 2016-04-29.
 */
public class CallRecord {

    public int id;
    public String contactName;
    public String phoneNumber;
    public String callTime;

    public CallRecord(int id, String contactName, String phoneNumber, String callTime) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.contactName = contactName;
        this.callTime = callTime;
    }

    public CallRecord(){}
}
