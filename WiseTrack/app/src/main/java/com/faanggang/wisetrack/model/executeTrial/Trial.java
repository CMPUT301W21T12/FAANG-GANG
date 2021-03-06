package com.faanggang.wisetrack.model.executeTrial;

import android.location.Location;

import java.util.Date;

public class Trial {
    private Location trialGeolocation;
    private String experimenterID;
    private Date datetime;
    private String trialID;  // trial document ID in firebase
    private double trialResult;
    private int trialType;

    /**
     * @param trialGeolocation Location object representing the geolocation for which current trial was conducted
     * @param uID user id of the trial conductor
     * @param date datetime of current trial conduction
     * @param trialResult result of current trial
     * @param trialType trial type of current trial: 0 - count | 1 - binomial | 2 - NNIC | 3 - measurement
     *                 NOTE: NNIC = Non-Negative Integer Count
     */
    public Trial(Location trialGeolocation, String uID, Date date, double trialResult, int trialType) {
        this.trialGeolocation = trialGeolocation;
        this.experimenterID = uID;
        this.datetime = date;
        this.trialResult = trialResult;
        this.trialType = trialType;
    }

    public Location getTrialGeolocation() {
        return trialGeolocation;
    }

    public void setTrialGeolocation(Location trialGeolocation) {
        this.trialGeolocation = trialGeolocation;
    }

    public String getExperimenterID() {
        return experimenterID;
    }

    public void setExperimenterID(String uID) {
        this.experimenterID = uID;
    }

    public String getTrialID() {
        return trialID;
    }

    public void setTrialID(String trialID) {
        this.trialID = trialID;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public double getTrialResult() {
        return trialResult;
    }

    public void setTrialResult(double trialResult) {
        this.trialResult = trialResult;
    }

    public int getTrialType() {
        return trialType;
    }

    public void setTrialType(int trialType) {
        this.trialType = trialType;
    }
}
