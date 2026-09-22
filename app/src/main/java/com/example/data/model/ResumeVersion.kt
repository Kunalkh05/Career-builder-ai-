package com.example.data.model

data class ResumeVersion(
    val id: String,
    val versionName: String,
    val targetCompany: String,
    val targetRole: String,
    val originalResumeId: String,
    val modifiedResume: Resume,
    val acceptedCount: Int,
    val rejectedCount: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val lastModifiedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)
