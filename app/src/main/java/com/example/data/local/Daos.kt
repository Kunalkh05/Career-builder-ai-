package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ResumeDao {
    @Query("SELECT * FROM resumes ORDER BY updatedAt DESC")
    fun getAllResumes(): Flow<List<ResumeEntity>>

    @Query("SELECT * FROM resumes WHERE id = :id LIMIT 1")
    suspend fun getResumeById(id: String): ResumeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResume(resume: ResumeEntity)

    @Query("DELETE FROM resumes WHERE id = :id")
    suspend fun deleteResume(id: String)
}

@Dao
interface ResumeVersionDao {
    @Query("SELECT * FROM resume_versions ORDER BY lastModifiedAt DESC")
    fun getAllVersions(): Flow<List<ResumeVersionEntity>>

    @Query("SELECT * FROM resume_versions WHERE id = :id LIMIT 1")
    suspend fun getVersionById(id: String): ResumeVersionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: ResumeVersionEntity)

    @Query("DELETE FROM resume_versions WHERE id = :id")
    suspend fun deleteVersion(id: String)
}

@Dao
interface OptimizationSessionDao {
    @Query("SELECT * FROM optimization_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<OptimizationSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: OptimizationSessionEntity)

    @Query("DELETE FROM optimization_sessions WHERE id = :id")
    suspend fun deleteSession(id: String)
}
