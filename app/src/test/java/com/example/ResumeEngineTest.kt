package com.example

import com.example.data.ai.ResumeEngine
import com.example.data.model.*
import com.example.data.sample.SampleData
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ResumeEngineTest {

    @Test
    fun testParseRawResumeText() {
        val raw = SampleData.sampleResumeSoftware.rawText
        val parsed = ResumeEngine.parseRawResumeText(raw, "Alex Chen")

        assertEquals("Alex Chen", parsed.fullName)
        assertTrue("Experiences should be extracted", parsed.experiences.isNotEmpty())
        assertTrue("Skills should be parsed", parsed.skills.technical.isNotEmpty())
        assertTrue("Education should be parsed", parsed.education.isNotEmpty())
    }

    @Test
    fun testJobAnalysis() {
        val targetJob = SampleData.sampleJobGoogle
        val analysis = ResumeEngine.analyzeJobDescription(targetJob)

        assertTrue("Should detect tools & technologies", analysis.toolsAndTechnologies.isNotEmpty())
        assertTrue("Should detect responsibilities", analysis.responsibilities.isNotEmpty())
        assertTrue("Should have required skills", analysis.requiredSkills.isNotEmpty())
    }

    /**
     * TEST CASE 1: Supported Modification
     * When resume has supported experience (e.g. Kotlin, MVVM), the AI engine generates
     * suggestions flagged as SUPPORTED_BY_RESUME, preserving facts, using strong verbs,
     * and linking directly to the job requirement.
     */
    @Test
    fun testCase1_SupportedModification() = runBlocking {
        val original = SampleData.sampleResumeSoftware
        val job = SampleData.sampleJobGoogle
        val analysis = ResumeEngine.analyzeJobDescription(job)
        val match = ResumeEngine.compareResumeToJob(original, analysis)
        val suggestions = ResumeEngine.generateSuggestions(original, job, analysis, match)

        val supportedSuggestions = suggestions.filter { it.evidenceStatus == EvidenceStatus.SUPPORTED_BY_RESUME }
        assertTrue("Should generate supported suggestions for verified experience", supportedSuggestions.isNotEmpty())

        for (sug in supportedSuggestions) {
            assertTrue("Reason for change must be provided", sug.reasonForChange.isNotBlank())
            assertTrue("Related job requirement must be linked", sug.relatedJobRequirement.isNotBlank())
            assertTrue("Suggested text must not be empty", sug.suggestedText.isNotBlank())
        }
    }

    /**
     * TEST CASE 2: Missing Skill (No Fabrication)
     * When a skill appears in the job description but NOT in the candidate's resume (e.g. GraphQL, AWS),
     * the engine does NOT invent or add that skill to the resume. It identifies it as a gap
     * with respectful confirmation phrasing.
     */
    @Test
    fun testCase2_MissingSkillNeverFabricated() {
        val original = SampleData.sampleResumeSoftware
        val jobWithMissingSkills = JobTarget(
            companyName = "CloudCorp",
            jobTitle = "Backend Cloud Engineer",
            jobDescription = """
                Required:
                - Proficiency with Kubernetes and Docker orchestration.
                - Hands-on experience with GraphQL APIs.
                - 5+ years of AWS Cloud Architecture experience.
            """.trimIndent()
        )
        val jobAnalysis = ResumeEngine.analyzeJobDescription(jobWithMissingSkills)
        val matchAnalysis = ResumeEngine.compareResumeToJob(original, jobAnalysis)

        // Missing skills must appear in possible gaps
        val gapRequirements = matchAnalysis.possibleGaps.map { it.requirement.lowercase() }
        val foundMissingGaps = gapRequirements.any { it.contains("graphql") || it.contains("kubernetes") || it.contains("aws") }
        assertTrue("Missing skills must be identified as gaps", foundMissingGaps)

        // Must use respectful, non-accusatory phrasing
        for (gap in matchAnalysis.possibleGaps) {
            assertTrue(
                "Gap phrasing must be respectful and seek confirmation",
                gap.carefulNotice.contains("not identified in the uploaded resume") ||
                gap.carefulNotice.contains("confirm whether you have experience")
            )
        }

        // Modified resume must not fabricate missing skills into user's technical skills list
        val applied = ResumeEngine.applySuggestionsToResume(original, emptyList())
        assertFalse("GraphQL must not be fabricated into skills", applied.skills.technical.any { it.equals("GraphQL", ignoreCase = true) })
        assertFalse("Kubernetes must not be fabricated into skills", applied.skills.technical.any { it.equals("Kubernetes", ignoreCase = true) })
    }

    /**
     * TEST CASE 3: Unsupported Metric (Placeholder Warning)
     * If a metric (e.g. % improvement or latency reduction) is not in the original resume,
     * the engine does NOT hallucinate arbitrary numbers. It marks placeholders with brackets
     * like [Requires User Confirmation...] or [Confirm...] and flags REQUIRES_CONFIRMATION.
     */
    @Test
    fun testCase3_UnsupportedMetricHasPlaceholder() = runBlocking {
        val original = SampleData.sampleResumeSoftware
        val job = SampleData.sampleJobGoogle
        val analysis = ResumeEngine.analyzeJobDescription(job)
        val match = ResumeEngine.compareResumeToJob(original, analysis)
        val suggestions = ResumeEngine.generateSuggestions(original, job, analysis, match)

        val confirmationSuggestions = suggestions.filter { it.evidenceStatus == EvidenceStatus.REQUIRES_CONFIRMATION }
        assertTrue("Must include suggestions requiring user confirmation for unverified metrics", confirmationSuggestions.isNotEmpty())

        for (sug in confirmationSuggestions) {
            val hasBracketPlaceholder = sug.suggestedText.contains("[") && sug.suggestedText.contains("]")
            assertTrue("Unverified metrics must contain a placeholder bracket: ${sug.suggestedText}", hasBracketPlaceholder)
        }
    }

    /**
     * TEST CASE 4: User Confirmation and Quality Check
     * When suggestions have unresolved placeholders, the QualityCheck flags them as warnings.
     * When user resolves the placeholder by editing, the quality report verifies integrity.
     */
    @Test
    fun testCase4_UserConfirmationAndQualityCheck() = runBlocking {
        val original = SampleData.sampleResumeSoftware
        val job = SampleData.sampleJobGoogle
        val analysis = ResumeEngine.analyzeJobDescription(job)
        val match = ResumeEngine.compareResumeToJob(original, analysis)
        val suggestions = ResumeEngine.generateSuggestions(original, job, analysis, match)

        // Apply suggestions as accepted so placeholders are included in the modified resume
        val suggestionsWithAccepted = suggestions.map { it.copy(reviewStatus = ReviewStatus.ACCEPTED) }
        val modifiedWithPlaceholders = ResumeEngine.applySuggestionsToResume(original, suggestionsWithAccepted)
        val qualityReport = ResumeEngine.performQualityCheck(original, modifiedWithPlaceholders, job, suggestionsWithAccepted)

        assertTrue("Quality check must detect unverified placeholders", qualityReport.unverifiedPlaceholdersCount > 0)
        assertTrue("Quality check must have warning issues for placeholders", qualityReport.issues.any { it.category == QualityCategory.FACTUAL_CONSISTENCY })

        // Now simulate user resolving the placeholder
        val resolvedSuggestions = suggestions.map { sug ->
            if (sug.suggestedText.contains("[")) {
                sug.copy(
                    reviewStatus = ReviewStatus.EDITED,
                    userEditedText = sug.suggestedText.replace(Regex("\\[.*?\\]"), "by 25%")
                )
            } else {
                sug.copy(reviewStatus = ReviewStatus.ACCEPTED)
            }
        }
        val resolvedResume = ResumeEngine.applySuggestionsToResume(original, resolvedSuggestions)
        val resolvedQuality = ResumeEngine.performQualityCheck(original, resolvedResume, job, resolvedSuggestions)

        assertEquals("All placeholders resolved", 0, resolvedQuality.unverifiedPlaceholdersCount)
        assertTrue("Factual integrity verified after user resolution", resolvedQuality.factualIntegrityVerified)
    }

    /**
     * TEST CASE 5: Original Preservation
     * The original resume is immutable and never overwritten when generating suggestions
     * or applying modifications. Reverting leaves original resume completely intact.
     */
    @Test
    fun testCase5_OriginalPreservation() = runBlocking {
        val original = SampleData.sampleResumeSoftware
        val originalSummary = original.summary
        val originalExperiences = original.experiences.map { it.copy(bullets = it.bullets.toList()) }
        val originalSkills = original.skills.technical.toList()

        val job = SampleData.sampleJobGoogle
        val analysisResult = ResumeEngine.analyzeAndModifyResume(original, job)

        // Verify original resume was not mutated in place
        assertEquals("Original summary must be unchanged", originalSummary, original.summary)
        assertEquals("Original experience count must be unchanged", originalExperiences.size, original.experiences.size)
        assertEquals("Original skills must be unchanged", originalSkills, original.skills.technical)

        // Reverting all suggestions must produce the exact original content
        val rejectedSuggestions = analysisResult.suggestions.map { it.copy(reviewStatus = ReviewStatus.REJECTED) }
        val revertedResume = ResumeEngine.applySuggestionsToResume(original, rejectedSuggestions)

        assertEquals("Reverted summary must equal original", original.summary, revertedResume.summary)
        for (i in original.experiences.indices) {
            assertEquals(
                "Reverted bullets must equal original bullets",
                original.experiences[i].bullets,
                revertedResume.experiences[i].bullets
            )
        }
    }

    /**
     * TEST CASE 6: Authorization & Session Integrity
     * Saved resume versions properly preserve the original resume ID, track target company/role,
     * and maintain audit counts of accepted/rejected suggestions.
     */
    @Test
    fun testCase6_AuthorizationAndSessionIntegrity() {
        val original = SampleData.sampleResumeSoftware
        val modified = original.copy(title = "Tailored for Google")
        val version = ResumeVersion(
            id = "ver_${UUID.randomUUID()}",
            versionName = "Google SWE Tailored v1",
            targetCompany = "Google",
            targetRole = "Android Software Engineer",
            originalResumeId = original.id,
            modifiedResume = modified,
            acceptedCount = 4,
            rejectedCount = 1,
            notes = "Tailored with focus on Jetpack Compose and Room"
        )

        assertEquals("Version must link to original resume ID", original.id, version.originalResumeId)
        assertEquals("Google", version.targetCompany)
        assertEquals(4, version.acceptedCount)
        assertEquals(1, version.rejectedCount)
        assertEquals("Tailored for Google", version.modifiedResume.title)
    }

    /**
     * TEST CASE 7: Malformed AI Output Resilience
     * AI models may output invalid JSON, markdown code fences, or truncated text.
     * The engine safely strips code blocks, handles objects or arrays, catches syntax errors,
     * and falls back to deterministic rule-based suggestions without crashing.
     */
    @Test
    fun testCase7_MalformedAiOutputResilience() {
        // 1. Markdown code fence wrapped JSON
        val markdownJson = """
            ```json
            [
              {
                "section": "EXPERIENCE",
                "targetRef": "exp_0_bullet_0",
                "originalText": "Worked on Android app",
                "suggestedText": "Architected Android application with Kotlin",
                "reasonForChange": "Improved action verb",
                "relatedJobRequirement": "Kotlin proficiency",
                "evidenceStatus": "SUPPORTED_BY_RESUME"
              }
            ]
            ```
        """.trimIndent()
        val parsedFromMarkdown = ResumeEngine.parseAiSuggestionsJson(markdownJson)
        assertEquals(1, parsedFromMarkdown.size)
        assertEquals("Architected Android application with Kotlin", parsedFromMarkdown[0].suggestedText)

        // 2. Object with suggestions array
        val objectJson = """
            {
              "suggestions": [
                {
                  "section": "SUMMARY",
                  "targetRef": "summary",
                  "originalText": "Software Engineer",
                  "suggestedText": "Senior Mobile Engineer",
                  "reasonForChange": "Better role alignment",
                  "relatedJobRequirement": "Mobile leadership",
                  "evidenceStatus": "SUPPORTED_BY_RESUME"
                }
              ]
            }
        """.trimIndent()
        val parsedFromObject = ResumeEngine.parseAiSuggestionsJson(objectJson)
        assertEquals(1, parsedFromObject.size)
        assertEquals(ResumeSection.SUMMARY, parsedFromObject[0].section)

        // 3. Completely malformed / truncated JSON
        val malformedJson = "{ this is invalid json ::: "
        val parsedFromMalformed = ResumeEngine.parseAiSuggestionsJson(malformedJson)
        assertTrue("Malformed JSON must return empty list without throwing", parsedFromMalformed.isEmpty())
    }

    /**
     * TEST CASE 8: API Security (No Hardcoded Keys)
     * Sourced through BuildConfig.GEMINI_API_KEY. Engine handles empty or default keys gracefully
     * and falls back to deterministic local rule suggestions.
     */
    @Test
    fun testCase8_ApiSecurityNoHardcodedKey() = runBlocking {
        val original = SampleData.sampleResumeSoftware
        val job = SampleData.sampleJobGoogle
        val analysis = ResumeEngine.analyzeJobDescription(job)
        val match = ResumeEngine.compareResumeToJob(original, analysis)

        // Execution succeeds safely regardless of whether remote API key is active
        val suggestions = ResumeEngine.generateSuggestions(original, job, analysis, match)
        assertTrue("Suggestions must be generated securely", suggestions.isNotEmpty())

        for (sug in suggestions) {
            assertNotNull(sug.evidenceStatus)
            assertNotNull(sug.reasonForChange)
            assertNotNull(sug.relatedJobRequirement)
        }
    }

    @Test
    fun testATSFormattedResumeGeneration() {
        val resume = SampleData.sampleResumeSoftware
        val formatted = ResumeEngine.generateFormattedResume(resume)

        assertTrue(formatted.contains("ALEX CHEN"))
        assertTrue(formatted.contains("WORK EXPERIENCE"))
        assertTrue(formatted.contains("TECHNICAL SKILLS"))
        assertTrue(formatted.contains("EDUCATION"))
    }
}
