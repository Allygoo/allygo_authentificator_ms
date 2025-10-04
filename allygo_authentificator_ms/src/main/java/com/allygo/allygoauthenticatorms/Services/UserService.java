package com.allygo.allygoauthenticatorms.Services;

import com.allygo.allygoauthenticatorms.Record.NewUser;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Save a user with an auto-incremented numeric ID.
     * The password will be stored encrypted.
     * @param newUser the user to save
     * @return generated numeric ID + update time
     */
    public String saveUser(NewUser newUser) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        if (getUserByEmail(newUser.getEmail()) != null) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }

        Long newId = getNextUserId(db);

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setId(newId); // asignamos el id numérico

        DocumentReference docRef = db.collection("users").document(String.valueOf(newId));
        ApiFuture<WriteResult> result = docRef.set(newUser);

        return "User saved with numeric ID: " + newId +
                " at: " + result.get().getUpdateTime();
    }

    /**
     * Método para obtener el siguiente ID numérico de forma segura.
     */
    private Long getNextUserId(Firestore db) throws ExecutionException, InterruptedException {
        DocumentReference counterRef = db.collection("counters").document("user_counter");

        return db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(counterRef).get();
            Long lastId = snapshot.contains("last_id") ? snapshot.getLong("last_id") : 0L;
            Long newId = lastId + 1;

            transaction.update(counterRef, "last_id", newId);
            return newId;
        }).get();
    }

    /**
     * Retrieve a user by Firestore ID.
     * @param id Firestore document ID (numeric)
     * @return user object or null
     */
    public NewUser getUserById(Long id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("users").document(String.valueOf(id));
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
     */
    public NewUser getUserByEmail(String email) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

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

    /**
     * Checks if the provided raw password matches the hashed password.
     */
    public boolean checkPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
