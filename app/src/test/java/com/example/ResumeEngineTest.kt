package com.example

import com.example.data.ai.ResumeEngine
import com.example.data.model.EvidenceStatus
import com.example.data.model.ReviewStatus
import com.example.data.sample.SampleData
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

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

    @Test
    fun testMatchAnalysisAndRespectfulGapPhrasing() {
        val resume = SampleData.sampleResumeSoftware
        val job = SampleData.sampleJobGoogle
        val analysis = ResumeEngine.analyzeJobDescription(job)
        val match = ResumeEngine.compareResumeToJob(resume, analysis)

        assertTrue("Match score should be calculated", match.matchScore in 1..100)
        assertTrue("Potential matches should be detected", match.potentialMatches.isNotEmpty())

        // Check respectful gap phrasing rule
        for (gap in match.possibleGaps) {
            assertTrue(
                "Gap phrasing must be respectful and careful",
                gap.carefulNotice.contains("not identified in the uploaded resume") ||
                gap.carefulNotice.contains("confirm whether you have experience")
            )
        }
    }

    @Test
    fun testFactualIntegrityCheck() = runBlocking {
        val original = SampleData.sampleResumeSoftware
        val job = SampleData.sampleJobGoogle
        val analysis = ResumeEngine.analyzeJobDescription(job)
        val match = ResumeEngine.compareResumeToJob(original, analysis)
        val suggestions = ResumeEngine.generateSuggestions(original, job, analysis, match)

        // All generated suggestions must have evidence status
        for (s in suggestions) {
            assertNotNull(s.evidenceStatus)
            assertNotNull(s.reasonForChange)
            assertNotNull(s.relatedJobRequirement)
        }

        // Apply suggestions and perform quality check
        val modified = ResumeEngine.applySuggestionsToResume(original, suggestions)
        val quality = ResumeEngine.performQualityCheck(original, modified, job, suggestions)

        assertTrue("Quality score should be high for evidence-backed resume", quality.score >= 70)
        assertTrue("Factual integrity must be verified", quality.factualIntegrityVerified)
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
