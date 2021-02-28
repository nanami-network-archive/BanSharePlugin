package xyz.n7mn.dev.banshareplugin.data;

import java.util.Date;
import java.util.UUID;

public class BanData {

    private int BanID;
    private UUID UserUUID;
    private String Reason;
    private String Area;
    private String IP;
    private Date EndDate;
    private Date ExecuteDate;
    private UUID ExecuteUserUUID;
    private boolean Active;

    public BanData(){}

    public BanData(int banID, UUID userUUID, String reason, String area, String ip, Date endDate, Date executeDate, UUID executeUserUUID, boolean active){

        this.BanID = banID;
        this.UserUUID = userUUID;
        this.Reason = reason;
        this.Area = area;
        this.IP = ip;
        this.EndDate = endDate;
        this.ExecuteDate = executeDate;
        this.ExecuteUserUUID = executeUserUUID;
        this.Active = active;

    }


    public int getBanID() {
        return BanID;
    }

    public void setBanID(int banID) {
        BanID = banID;
    }

    public UUID getUserUUID() {
        return UserUUID;
    }

    public void setUserUUID(UUID userUUID) {
        UserUUID = userUUID;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public Date getEndDate() {
        return EndDate;
    }

    public void setEndDate(Date endDate) {
        EndDate = endDate;
    }

    public Date getExecuteDate() {
        return ExecuteDate;
    }

    public void setExecuteDate(Date executeDate) {
        ExecuteDate = executeDate;
    }

    public UUID getExecuteUserUUID() {
        return ExecuteUserUUID;
    }

    public void setExecuteUserUUID(UUID executeUserUUID) {
        ExecuteUserUUID = executeUserUUID;
    }

    public boolean isActive() {
        return Active;
    }

    public void setActive(boolean active) {
        Active = active;
    }
}
