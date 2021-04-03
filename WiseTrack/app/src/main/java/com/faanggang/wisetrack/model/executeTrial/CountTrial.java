package com.faanggang.wisetrack.model.executeTrial;

import java.util.Date;

public class CountTrial extends Trial{
    private int count;
    private int trialType;
    /**
     * @param count: current trial test result - count type or non-negative
     * @param trialGeolocation : string input of location for which current trial was conducted
     * @param uID              : user id of the trial conductor
     * @param date
     */
    public CountTrial(int count, int trialType, String trialGeolocation, String uID, Date date) {
        super(trialGeolocation, uID, date);
        this.trialType = trialType;

        if (trialType == 0) {
            this.count = count;  // count type
        } else if (trialType == 2) {
            // non-negative count type
            if (count < 0) {
                throw new IllegalArgumentException("Trial type must be non-negative integer count.");
            } else {
                this.count = count;
            }
        }
    }

    // dummy constructor for testing
    public CountTrial(int count, String trialGeolocation, String uID, Date date) {
        super(trialGeolocation, uID, date);
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
