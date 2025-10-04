package com.allygo.allygoauthenticatorms.Services;

import com.allygo.allygoauthenticatorms.Record.NewUser;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    /**
     * Save a user with an auto-generated Firestore ID.
     * @param newUser the user to save
     * @return generated Firestore ID + update time
     */
    public String saveUser(NewUser newUser) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        // Let Firestore generate a unique document ID
        DocumentReference docRef = db.collection("users").document();
        ApiFuture<WriteResult> result = docRef.set(newUser);

        return "User saved with ID: " + docRef.getId() +
                " at: " + result.get().getUpdateTime();
    }

    /**
     * Retrieve a user by Firestore ID.
     * @param id Firestore document ID
     * @return user object or null
     */
    public NewUser getUserById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return document.toObject(NewUser.class);
        } else {
            return null;
        }
    }

    /**
     * Retrieve a user by email.
     * @param email user email
     * @return user object or null
     */
    public NewUser getUserByEmail(String email) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        // Firestore query: users where email == provided email
        CollectionReference users = db.collection("users");
        Query query = users.whereEqualTo("email", email).limit(1);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (!documents.isEmpty()) {
            return documents.get(0).toObject(NewUser.class);
        } else {
            return null;
        }
    }
}
