package com.example.data.repository

import android.content.Context
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.sample.SampleData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class ResumeRepository(context: Context) {

    private val database = AppDatabase.getDatabase(context)
    private val resumeDao = database.resumeDao()
    private val versionDao = database.resumeVersionDao()
    private val sessionDao = database.optimizationSessionDao()

    val allResumes: Flow<List<Resume>> = resumeDao.getAllResumes().map { entities ->
        if (entities.isEmpty()) {
            listOf(SampleData.sampleResumeSoftware)
        } else {
            entities.map { it.toDomain() }
        }
    }

    val allVersions: Flow<List<ResumeVersion>> = versionDao.getAllVersions().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun saveResume(resume: Resume) {
        resumeDao.insertResume(resume.toEntity())
    }

    suspend fun getResumeById(id: String): Resume? {
        if (id == SampleData.sampleResumeSoftware.id) return SampleData.sampleResumeSoftware
        return resumeDao.getResumeById(id)?.toDomain()
    }

    suspend fun deleteResume(id: String) {
        resumeDao.deleteResume(id)
    }

    suspend fun saveVersion(version: ResumeVersion) {
        versionDao.insertVersion(version.toEntity())
    }

    suspend fun deleteVersion(id: String) {
        versionDao.deleteVersion(id)
    }

    // Mapping helpers
    private fun Resume.toEntity(): ResumeEntity {
        val expArr = JSONArray()
        experiences.forEach { exp ->
            val obj = JSONObject().apply {
                put("id", exp.id)
                put("company", exp.company)
                put("role", exp.role)
                put("location", exp.location)
                put("startDate", exp.startDate)
                put("endDate", exp.endDate)
                put("isCurrent", exp.isCurrent)
                val bArr = JSONArray()
                exp.bullets.forEach { bArr.put(it) }
                put("bullets", bArr)
            }
            expArr.put(obj)
        }

        val eduArr = JSONArray()
        education.forEach { edu ->
            val obj = JSONObject().apply {
                put("id", edu.id)
                put("institution", edu.institution)
                put("degree", edu.degree)
                put("fieldOfStudy", edu.fieldOfStudy)
                put("graduationDate", edu.graduationDate)
                put("gpa", edu.gpa)
            }
            eduArr.put(obj)
        }

        val projArr = JSONArray()
        projects.forEach { proj ->
            val obj = JSONObject().apply {
                put("id", proj.id)
                put("name", proj.name)
                put("role", proj.role)
                put("description", proj.description)
                val bArr = JSONArray()
                proj.bullets.forEach { bArr.put(it) }
                put("bullets", bArr)
                val tArr = JSONArray()
                proj.technologies.forEach { tArr.put(it) }
                put("technologies", tArr)
            }
            projArr.put(obj)
        }

        val skillsObj = JSONObject().apply {
            val tArr = JSONArray()
            skills.technical.forEach { tArr.put(it) }
            put("technical", tArr)

            val sArr = JSONArray()
            skills.softSkills.forEach { sArr.put(it) }
            put("softSkills", sArr)

            val toolsArr = JSONArray()
            skills.toolsAndFrameworks.forEach { toolsArr.put(it) }
            put("tools", toolsArr)
        }

        val certsArr = JSONArray()
        certifications.forEach { certsArr.put(it) }

        val achArr = JSONArray()
        achievements.forEach { achArr.put(it) }

        return ResumeEntity(
            id = id,
            title = title,
            rawText = rawText,
            fullName = fullName,
            email = email,
            phone = phone,
            location = location,
            summary = summary,
            experiencesJson = expArr.toString(),
            educationJson = eduArr.toString(),
            projectsJson = projArr.toString(),
            skillsJson = skillsObj.toString(),
            certificationsJson = certsArr.toString(),
            achievementsJson = achArr.toString(),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun ResumeEntity.toDomain(): Resume {
        val expList = mutableListOf<WorkExperience>()
        try {
            val expArr = JSONArray(experiencesJson)
            for (i in 0 until expArr.length()) {
                val obj = expArr.getJSONObject(i)
                val bullets = mutableListOf<String>()
                val bArr = obj.optJSONArray("bullets")
                if (bArr != null) {
                    for (b in 0 until bArr.length()) bullets.add(bArr.getString(b))
                }
                expList.add(
                    WorkExperience(
                        id = obj.optString("id", "exp_$i"),
                        company = obj.optString("company"),
                        role = obj.optString("role"),
                        location = obj.optString("location"),
                        startDate = obj.optString("startDate"),
                        endDate = obj.optString("endDate"),
                        isCurrent = obj.optBoolean("isCurrent"),
                        bullets = bullets
                    )
                )
            }
        } catch (e: Exception) {}

        val eduList = mutableListOf<EducationItem>()
        try {
            val eduArr = JSONArray(educationJson)
            for (i in 0 until eduArr.length()) {
                val obj = eduArr.getJSONObject(i)
                eduList.add(
                    EducationItem(
                        id = obj.optString("id", "edu_$i"),
                        institution = obj.optString("institution"),
                        degree = obj.optString("degree"),
                        fieldOfStudy = obj.optString("fieldOfStudy"),
                        graduationDate = obj.optString("graduationDate"),
                        gpa = obj.optString("gpa")
                    )
                )
            }
        } catch (e: Exception) {}

        val projList = mutableListOf<ProjectItem>()
        try {
            val projArr = JSONArray(projectsJson)
            for (i in 0 until projArr.length()) {
                val obj = projArr.getJSONObject(i)
                val bullets = mutableListOf<String>()
                val bArr = obj.optJSONArray("bullets")
                if (bArr != null) {
                    for (b in 0 until bArr.length()) bullets.add(bArr.getString(b))
                }
                val techs = mutableListOf<String>()
                val tArr = obj.optJSONArray("technologies")
                if (tArr != null) {
                    for (t in 0 until tArr.length()) techs.add(tArr.getString(t))
                }
                projList.add(
                    ProjectItem(
                        id = obj.optString("id", "proj_$i"),
                        name = obj.optString("name"),
                        role = obj.optString("role"),
                        description = obj.optString("description"),
                        bullets = bullets,
                        technologies = techs
                    )
                )
            }
        } catch (e: Exception) {}

        var parsedSkills = ResumeSkills()
        try {
            val sObj = JSONObject(skillsJson)
            val tech = mutableListOf<String>()
            val tArr = sObj.optJSONArray("technical")
            if (tArr != null) for (k in 0 until tArr.length()) tech.add(tArr.getString(k))

            val soft = mutableListOf<String>()
            val sArr = sObj.optJSONArray("softSkills")
            if (sArr != null) for (k in 0 until sArr.length()) soft.add(sArr.getString(k))

            val tools = mutableListOf<String>()
            val toolsArr = sObj.optJSONArray("tools")
            if (toolsArr != null) for (k in 0 until toolsArr.length()) tools.add(toolsArr.getString(k))

            parsedSkills = ResumeSkills(tech, soft, tools)
        } catch (e: Exception) {}

        val certs = mutableListOf<String>()
        try {
            val cArr = JSONArray(certificationsJson)
            for (i in 0 until cArr.length()) certs.add(cArr.getString(i))
        } catch (e: Exception) {}

        val achs = mutableListOf<String>()
        try {
            val aArr = JSONArray(achievementsJson)
            for (i in 0 until aArr.length()) achs.add(aArr.getString(i))
        } catch (e: Exception) {}

        return Resume(
            id = id,
            title = title,
            rawText = rawText,
            fullName = fullName,
            email = email,
            phone = phone,
            location = location,
            summary = summary,
            experiences = expList,
            education = eduList,
            projects = projList,
            skills = parsedSkills,
            certifications = certs,
            achievements = achs,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun ResumeVersion.toEntity(): ResumeVersionEntity {
        return ResumeVersionEntity(
            id = id,
            versionName = versionName,
            targetCompany = targetCompany,
            targetRole = targetRole,
            originalResumeId = originalResumeId,
            modifiedResumeJson = modifiedResume.toEntity().let {
                JSONObject().apply {
                    put("id", it.id)
                    put("title", it.title)
                    put("rawText", it.rawText)
                    put("fullName", it.fullName)
                    put("email", it.email)
                    put("phone", it.phone)
                    put("location", it.location)
                    put("summary", it.summary)
                    put("experiencesJson", it.experiencesJson)
                    put("educationJson", it.educationJson)
                    put("projectsJson", it.projectsJson)
                    put("skillsJson", it.skillsJson)
                    put("certificationsJson", it.certificationsJson)
                    put("achievementsJson", it.achievementsJson)
                    put("createdAt", it.createdAt)
                    put("updatedAt", it.updatedAt)
                }.toString()
            },
            acceptedCount = acceptedCount,
            rejectedCount = rejectedCount,
            createdAt = createdAt,
            lastModifiedAt = lastModifiedAt,
            notes = notes
        )
    }

    private fun ResumeVersionEntity.toDomain(): ResumeVersion {
        val dummyEntity = try {
            val obj = JSONObject(modifiedResumeJson)
            ResumeEntity(
                id = obj.optString("id", id),
                title = obj.optString("title", versionName),
                rawText = obj.optString("rawText"),
                fullName = obj.optString("fullName"),
                email = obj.optString("email"),
                phone = obj.optString("phone"),
                location = obj.optString("location"),
                summary = obj.optString("summary"),
                experiencesJson = obj.optString("experiencesJson", "[]"),
                educationJson = obj.optString("educationJson", "[]"),
                projectsJson = obj.optString("projectsJson", "[]"),
                skillsJson = obj.optString("skillsJson", "{}"),
                certificationsJson = obj.optString("certificationsJson", "[]"),
                achievementsJson = obj.optString("achievementsJson", "[]"),
                createdAt = obj.optLong("createdAt", createdAt),
                updatedAt = obj.optLong("updatedAt", lastModifiedAt)
            )
        } catch (e: Exception) {
            ResumeEntity(id, versionName, "", "", "", "", "", "", "[]", "[]", "[]", "{}", "[]", "[]", createdAt, lastModifiedAt)
        }

        return ResumeVersion(
            id = id,
            versionName = versionName,
            targetCompany = targetCompany,
            targetRole = targetRole,
            originalResumeId = originalResumeId,
            modifiedResume = dummyEntity.toDomain(),
            acceptedCount = acceptedCount,
            rejectedCount = rejectedCount,
            createdAt = createdAt,
            lastModifiedAt = lastModifiedAt,
            notes = notes
        )
    }
}
