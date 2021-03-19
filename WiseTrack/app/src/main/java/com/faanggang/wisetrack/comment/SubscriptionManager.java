package com.faanggang.wisetrack.comment;
import android.util.Log;

import com.faanggang.wisetrack.experiment.Experiment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class manages how user subscriptions are added and removed from users.
 * Full functionality for subscriptions has not yet been implemented.
 *
 * */
public class SubscriptionManager {
    private FirebaseFirestore db;
    private subSearcher finder;

    public SubscriptionManager(){
        this.db = FirebaseFirestore.getInstance();
        this.finder = finder;
    }
    public SubscriptionManager(subSearcher finder){
        this.db = FirebaseFirestore.getInstance();
        this.finder = finder;
    }

    public interface subSearcher{
        void onSubscriptionsFound(ArrayList<Experiment> results);
    }


    /**
    * This method updates the "Subscription" field of a user on the firebase.
    * @param expID
     * expID is the ID of the experiment that you are adding to.
    * @param userID
     * userID is the ID of the user that you want to edit the subscription list of.
    */
    public void addSubscription(String expID, String userID){
        db.collection("Users").document(userID).get()
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ArrayList<String> subs = (ArrayList<String>) task.getResult().get("subscriptions");
                if (!subs.contains(expID)){
                    HashMap<String, Object> map = new HashMap<String,Object>();
                    subs.add(expID);
                    map.put("subscriptions",subs);
                    db.collection("Users").document(userID).update(map);
                }
            }
        });
        db.collection("Experiments").document(expID).get()
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ArrayList<String> subs = (ArrayList<String>) task.getResult().get("subscribers");
                if (!subs.contains(userID)){
                    HashMap<String, Object> map = new HashMap<String,Object>();
                    subs.add(userID);
                    map.put("subscribers",subs);
                    db.collection("Experiments").document(expID).update(map);
                }
            }
        });
    }

    /**
     * This method updates the "Subscription" field of a user on the firebase.
     * @param expID
     * expID is the ID of the experiment that you are removing.
     * @param userID
     * userID is the ID of the user that you want to edit the subscription list of.
     */
    public void removeSubscription(String expID, String userID){
        db.collection("Users").document(userID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        ArrayList<String> subs = (ArrayList<String>) task.getResult().get("Subscriptions");
                        HashMap<String, Object> map = new HashMap<String,Object>();
                        if (subs.contains(expID)){
                            subs.remove(expID);
                        } else {Log.w("SUBSCRIPTION", "Trying to remove sub that doesn't exist");}
                        map.put("Subscriptions",subs);
                        db.collection("Users").document(userID).update(map);
                        Log.w("SUBSCRIPTION","added new twitch prime sub :)");
                    }else {
                        Log.w("SUBSCRIPTION","NO USER FOUND BRUH");
                    }
                });
    }


    public void getSubscriptions(String userID){
        db.collection("Experiments").whereArrayContains("subscribers", userID).get()
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Experiment> results = new ArrayList<Experiment>();
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for(DocumentSnapshot doc: documents){
                    Log.w("EXPERIMENT", doc.getString("name"));
                    Experiment e = new Experiment(
                            doc.getString("name"),
                            doc.getString("description"),
                            doc.getString("region"),
                            doc.getLong("minTrials").intValue(),
                            doc.getLong("trialType").intValue(),
                            doc.getBoolean("geolocation"),
                            doc.getDate("datetime"),
                            doc.getString("uID")
                    );
                    e.setOpen(doc.getBoolean("open"));
                    e.setExpID(doc.getId());
                    results.add(e);
                    finder.onSubscriptionsFound(results);
                }
            } else{
                Log.w("EXPERIMENT","DID NOT FIND");
            }
        });
    }
}
