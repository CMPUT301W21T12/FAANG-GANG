package com.faanggang.wisetrack;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.*;


public class SearchManagerTest {
    private static MockSearcher mockSearcher;
    private static FirebaseFirestore mockDb;
    private static CollectionReference mockCollectionRef;
    private static Query mockQuery;
    private static ArrayList<String> keywords;
    private static Task mockTask;

    @BeforeClass
    public static void initializeObject() {
        mockDb = Mockito.mock(FirebaseFirestore.class);
        mockCollectionRef = Mockito.mock(CollectionReference.class);
        mockQuery = Mockito.mock(Query.class);
        mockTask = Mockito.mock(Task.class);

        mockSearcher = new MockSearcher(mockDb);
        keywords = new ArrayList<>();
        keywords.add("TEST");
        keywords.add("EXPERIMENTS");

        Mockito.when(mockDb.collection("Experiments"))
                .thenReturn(mockCollectionRef);
        Mockito.when(mockCollectionRef.orderBy("datetime"))
                .thenReturn(mockQuery);
        Mockito.when(mockQuery.get())
                .thenReturn(mockTask);
        Mockito.when(mockTask.addOnCompleteListener(Mockito.any()))
                .thenReturn(mockTask);
        Mockito.when(mockTask.addOnFailureListener(Mockito.any()))
                .thenReturn(mockTask);
    }

    @Before
    public void cleanUp() {
        Mockito.reset();
    }

    @Test
    public void testSearchByKeywordOnMockDB() {
        mockSearcher = new MockSearcher(mockDb);
        mockSearcher.makeSearchRequest("Test Experiments");

        Mockito.verify(mockDb, Mockito.times(1))
                .collection("Experiments");
        Mockito.verify(mockCollectionRef, Mockito.times(1))
                .orderBy("datetime");
        Mockito.verify(mockQuery, Mockito.times(1))
                .get();

    }

}
