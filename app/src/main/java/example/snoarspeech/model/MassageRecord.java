package example.snoarspeech.model;

/**
 * Created by owen_ on 2016-04-29.
 */
public class MassageRecord {
    public int id;
    public String contactName;
    public String phoneNumber;
    public String sendTime;
    public String massageContent;

    public MassageRecord(int id, String contactName, String phoneNumber, String massageContent, String sendTime) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.contactName = contactName;
        this.massageContent = massageContent;
        this.sendTime = sendTime;
    }

    public MassageRecord(){}
}
