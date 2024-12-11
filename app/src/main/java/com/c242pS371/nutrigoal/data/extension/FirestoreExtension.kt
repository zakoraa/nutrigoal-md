package com.c242pS371.nutrigoal.data.extension

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

fun FirebaseFirestore.historiesCollection(): CollectionReference {
    return this.collection("histories")
}
