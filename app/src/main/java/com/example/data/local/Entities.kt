package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resumes")
data class ResumeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val rawText: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val location: String,
    val summary: String,
    val experiencesJson: String, // serialized json
    val educationJson: String,
    val projectsJson: String,
    val skillsJson: String,
    val certificationsJson: String,
    val achievementsJson: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "resume_versions")
data class ResumeVersionEntity(
    @PrimaryKey val id: String,
    val versionName: String,
    val targetCompany: String,
    val targetRole: String,
    val originalResumeId: String,
    val modifiedResumeJson: String,
    val acceptedCount: Int,
    val rejectedCount: Int,
    val createdAt: Long,
    val lastModifiedAt: Long,
    val notes: String
)

@Entity(tableName = "optimization_sessions")
data class OptimizationSessionEntity(
    @PrimaryKey val id: String,
    val resumeId: String,
    val companyName: String,
    val jobTitle: String,
    val experienceLevel: String,
    val jobDescription: String,
    val matchScore: Int,
    val suggestionsJson: String,
    val timestamp: Long
)
