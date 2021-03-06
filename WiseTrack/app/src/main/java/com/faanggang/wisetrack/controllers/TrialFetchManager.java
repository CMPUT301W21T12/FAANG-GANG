package com.faanggang.wisetrack.controllers;

import android.location.Location;
import android.util.ArrayMap;
import android.util.Log;

import com.faanggang.wisetrack.model.executeTrial.Trial;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class controls fetching of trials from Cloud Firebase.
 */
public class TrialFetchManager {
    private TrialFetcher fetcher;
    private FirebaseFirestore db;

    /**
     * This constructor initializes instance variables via dependency injection.
     * @param fetcher
     * fetcher to be initialized
     */
    public TrialFetchManager(TrialFetcher fetcher) {
        this.db = FirebaseFirestore.getInstance();
        this.fetcher = fetcher;
    }

    /**
     * This interface is to be implemented by classes that use this class to fetch so as to
     * allow for asyncronous updating.
     */
    public interface TrialFetcher {
        void onSuccessfulFetch(ArrayList<Trial> trials);
    }

    /**
     * This method creates a trial from the document snapshot.
     * @param snapshot
     * a document snapshot of the current trial document fetched from Cloud Firebase
     * @param geolocation
     * a Location object of current trial's geolocation containing latitude and longitude etc.
     * information
     * @param trialType
     * trial type of the current trial
     * @return trial based off snapshot info
     */
    public Trial createTrialFromSnapshot(DocumentSnapshot snapshot, Location geolocation, int trialType) {
        Trial trial;
        trial = new Trial(
                geolocation,
                snapshot.getString("conductor id"),
                snapshot.getDate("date"),
                snapshot.getDouble("result"),
                trialType
        );
        return trial;
    }

    /**
     * This method creates the trial Location object from the document snapshot.
     * @param snapshot
     * a document snapshot of the current experiment document fetched from Cloud Firebase
     * @return location of the snapshot
     */
    public Location createLocationFromSnapshot(DocumentSnapshot snapshot) {
        // fetch geolocation field data
        GeoPoint location = snapshot.getGeoPoint("geolocation");
        // create a Location object
        Location geolocation = new Location("");
        if (location != null) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            geolocation.setLatitude(lat);
            geolocation.setLongitude(lon);
        } else {
            geolocation = null;
        }
        return geolocation;
    }

    /**
     * This method fetches trial documents that belong to a given experiment document from the
     * Cloud Firestore.
     * This method does not return anything. It instead calls on an interface method implemented
     * by the Object that is receiving the data.
     * @param expID ID of the experiment that you are fetching its trials from.
     */
    public void fetchUnblockedUserTrials(String expID) {
        db.collection("Experiments").document(expID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        int trialType = snapshot.getLong("trialType").intValue();

                        ArrayList<String> blocked;
                        if (snapshot.get("blockedUsers") == null) {
                            blocked = new ArrayList<String>();
                        } else {
                            blocked = (ArrayList<String>)snapshot.get("blockedUsers");
                        }

                        db.collection("Experiments").document(expID).collection("Trials").get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        if (task1.getResult().size() != 0) {
                                            ArrayList<Trial> trials = new ArrayList<Trial>();
                                            ArrayList<DocumentSnapshot> docSnapList = (ArrayList<DocumentSnapshot>)task1.getResult().getDocuments();
                                            for (DocumentSnapshot doc : docSnapList) {
                                                if (blocked.contains(doc.getString("conductor id"))) {
                                                    continue;
                                                } else {
                                                    Location geolocation = createLocationFromSnapshot(doc);
                                                    Trial trial = createTrialFromSnapshot(doc, geolocation, trialType);
                                                    trials.add(trial);
                                                }
                                            }
                                            fetcher.onSuccessfulFetch(trials);
                                        }
                                    } else {
                                        Log.w("TRIAL(S)","DID NOT FIND");
                                    }
                        });
                    } else {
                        Log.w("EXPERIMENT","DID NOT FIND");
                    }
                });
    }

}
