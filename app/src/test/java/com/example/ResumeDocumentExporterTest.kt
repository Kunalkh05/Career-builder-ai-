package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.export.ResumeDocumentExporter
import com.example.data.sample.SampleData
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.zip.ZipFile

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ResumeDocumentExporterTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val sampleResume = SampleData.sampleResumeSoftware
    private val sampleJob = SampleData.sampleJobGoogle

    @Test
    fun testBaseFileNameGeneration() {
        val fileName = ResumeDocumentExporter.getBaseFileName(sampleResume, sampleJob)
        assertTrue("File name should contain candidate name", fileName.contains("Alex_Chen"))
        assertTrue("File name should contain role or company", fileName.contains("Google") || fileName.contains("Software_Engineer"))
        assertFalse("File name should not contain spaces or illegal characters", fileName.contains(" ") || fileName.contains("/"))
    }

    @Test
    fun testExportToPdf() {
        val pdfFile = ResumeDocumentExporter.exportToPdf(context, sampleResume, sampleJob)

        assertNotNull(pdfFile)
        assertTrue("PDF file should exist", pdfFile.exists())
        assertTrue("PDF file should have .pdf extension", pdfFile.name.endsWith(".pdf"))
        assertTrue("PDF file size should be greater than 0", pdfFile.length() > 0)

        val header = ByteArray(5)
        pdfFile.inputStream().use { it.read(header) }
        assertEquals("%PDF-", String(header, Charsets.US_ASCII))
    }

    @Test
    fun testExportToDocx() {
        val docxFile = ResumeDocumentExporter.exportToDocx(context, sampleResume, sampleJob)

        assertNotNull(docxFile)
        assertTrue("DOCX file should exist", docxFile.exists())
        assertTrue("DOCX file should have .docx extension", docxFile.name.endsWith(".docx"))
        assertTrue("DOCX file size should be greater than 0", docxFile.length() > 0)

        // Verify valid OpenXML zip archive structure
        ZipFile(docxFile).use { zip ->
            assertNotNull("Should contain [Content_Types].xml", zip.getEntry("[Content_Types].xml"))
            assertNotNull("Should contain word/document.xml", zip.getEntry("word/document.xml"))
            assertNotNull("Should contain word/styles.xml", zip.getEntry("word/styles.xml"))
            assertNotNull("Should contain _rels/.rels", zip.getEntry("_rels/.rels"))

            // Verify document.xml contains candidate name and keywords
            val docXmlEntry = zip.getEntry("word/document.xml")
            val content = zip.getInputStream(docXmlEntry).bufferedReader().readText()
            assertTrue("Document XML should contain candidate name", content.contains("Alex Chen"))
            assertTrue("Document XML should contain experience details", content.contains("Google") || content.contains("TechLead"))
        }
    }
}
