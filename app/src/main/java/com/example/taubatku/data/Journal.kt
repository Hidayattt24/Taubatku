package com.example.taubatku.data

import com.google.firebase.Timestamp

data class Journal(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val userId: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) 