package com.example.data.export

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.JobTarget
import com.example.data.model.Resume
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ResumeDocumentExporter {

    private const val PDF_WIDTH = 612 // 8.5 inches * 72 points
    private const val PDF_HEIGHT = 792 // 11 inches * 72 points
    private const val MARGIN = 40f
    private const val USABLE_WIDTH = PDF_WIDTH - (MARGIN * 2)

    /**
     * Directory inside app's cache directory where exported resumes are stored.
     */
    fun getResumesDir(context: Context): File {
        val dir = File(context.cacheDir, "resumes")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Generate a sanitized, clean filename based on candidate and target job.
     */
    fun getBaseFileName(resume: Resume, targetJob: JobTarget): String {
        val candidate = resume.fullName.trim().replace(Regex("[^a-zA-Z0-9]+"), "_").ifEmpty { "Resume" }
        val role = targetJob.jobTitle.trim().replace(Regex("[^a-zA-Z0-9]+"), "_").ifEmpty { "TargetRole" }
        val company = targetJob.companyName.trim().replace(Regex("[^a-zA-Z0-9]+"), "_").ifEmpty { "" }
        return if (company.isNotBlank()) {
            "${candidate}_${role}_${company}_Tailored"
        } else {
            "${candidate}_${role}_Tailored"
        }
    }

    /**
     * Helper to compute date range string for an experience item.
     */
    private fun formatExperienceDate(startDate: String, endDate: String, isCurrent: Boolean): String {
        val end = if (isCurrent) "Present" else endDate.ifEmpty { "Present" }
        return if (startDate.isNotBlank()) "$startDate – $end" else end
    }

    /**
     * Exports the given resume to a beautifully formatted, ATS-compliant PDF document.
     * Uses Android's native PdfDocument when supported, and falls back to a pure-Kotlin
     * PDF generator in JVM / headless test environments where libpdfium JNI is unavailable.
     */
    fun exportToPdf(context: Context, resume: Resume, targetJob: JobTarget): File {
        return try {
            exportWithAndroidPdf(context, resume, targetJob)
        } catch (e: Throwable) {
            // Fallback for Robolectric or environments where native PdfDocument fails
            exportWithStandardPdf(context, resume, targetJob)
        }
    }

    /**
     * Android native graphics PdfDocument implementation.
     */
    fun exportWithAndroidPdf(context: Context, resume: Resume, targetJob: JobTarget): File {
        val fileName = "${getBaseFileName(resume, targetJob)}.pdf"
        val outputFile = File(getResumesDir(context), fileName)

        val document = PdfDocument()

        val namePaint = Paint().apply {
            color = Color.parseColor("#111827")
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val contactPaint = Paint().apply {
            color = Color.parseColor("#4B5563")
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val sectionHeaderPaint = Paint().apply {
            color = Color.parseColor("#1E3A8A")
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val sectionLinePaint = Paint().apply {
            color = Color.parseColor("#D1D5DB")
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val subheaderPaint = Paint().apply {
            color = Color.parseColor("#1F2937")
            textSize = 10.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subheaderDatePaint = Paint().apply {
            color = Color.parseColor("#6B7280")
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = Color.parseColor("#374151")
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PDF_WIDTH, PDF_HEIGHT, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas
        var currentY = MARGIN + 10f

        fun checkPageBreak(neededHeight: Float) {
            if (currentY + neededHeight > PDF_HEIGHT - MARGIN) {
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PDF_WIDTH, PDF_HEIGHT, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                currentY = MARGIN + 10f
            }
        }

        fun drawSectionHeader(title: String) {
            checkPageBreak(35f)
            currentY += 10f
            canvas.drawText(title.uppercase(), MARGIN, currentY, sectionHeaderPaint)
            currentY += 4f
            canvas.drawLine(MARGIN, currentY, PDF_WIDTH - MARGIN, currentY, sectionLinePaint)
            currentY += 12f
        }

        fun drawWrappedText(text: String, paint: Paint, indent: Float = 0f, lineSpacing: Float = 13f) {
            val maxLineWidth = USABLE_WIDTH - indent
            val words = text.split(" ")
            var currentLine = StringBuilder()

            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                val measuredWidth = paint.measureText(testLine)
                if (measuredWidth > maxLineWidth && currentLine.isNotEmpty()) {
                    checkPageBreak(lineSpacing)
                    canvas.drawText(currentLine.toString(), MARGIN + indent, currentY, paint)
                    currentY += lineSpacing
                    currentLine = StringBuilder(word)
                } else {
                    currentLine = StringBuilder(testLine)
                }
            }

            if (currentLine.isNotEmpty()) {
                checkPageBreak(lineSpacing)
                canvas.drawText(currentLine.toString(), MARGIN + indent, currentY, paint)
                currentY += lineSpacing
            }
        }

        // 1. Header: Candidate Name
        val candidateName = resume.fullName.ifEmpty { "Candidate Resume" }
        canvas.drawText(candidateName, MARGIN, currentY, namePaint)
        currentY += 16f

        // Contact info line
        val contactItems = mutableListOf<String>()
        if (resume.email.isNotBlank()) contactItems.add(resume.email)
        if (resume.phone.isNotBlank()) contactItems.add(resume.phone)
        if (resume.location.isNotBlank()) contactItems.add(resume.location)
        if (resume.linkedinUrl.isNotBlank()) contactItems.add(resume.linkedinUrl)
        if (resume.githubUrl.isNotBlank()) contactItems.add(resume.githubUrl)

        val contactLine = contactItems.joinToString("  •  ")
        if (contactLine.isNotBlank()) {
            drawWrappedText(contactLine, contactPaint, indent = 0f, lineSpacing = 12f)
        }
        currentY += 4f
        canvas.drawLine(MARGIN, currentY, PDF_WIDTH - MARGIN, currentY, sectionLinePaint)
        currentY += 8f

        // 2. Summary
        if (resume.summary.isNotBlank()) {
            drawSectionHeader("Professional Summary")
            drawWrappedText(resume.summary, bodyPaint, indent = 0f, lineSpacing = 13f)
            currentY += 4f
        }

        // 3. Work Experience
        if (resume.experiences.isNotEmpty()) {
            drawSectionHeader("Work Experience")
            for (exp in resume.experiences) {
                checkPageBreak(40f)
                val roleCompany = "${exp.role}  —  ${exp.company}"
                canvas.drawText(roleCompany, MARGIN, currentY, subheaderPaint)

                val dateRange = formatExperienceDate(exp.startDate, exp.endDate, exp.isCurrent)
                val dateLocParts = listOfNotNull(
                    dateRange.takeIf { it.isNotBlank() },
                    exp.location.takeIf { it.isNotBlank() }
                )
                val dateLoc = dateLocParts.joinToString(" | ")
                if (dateLoc.isNotBlank()) {
                    val dateWidth = subheaderDatePaint.measureText(dateLoc)
                    val dateX = (PDF_WIDTH - MARGIN - dateWidth).coerceAtLeast(MARGIN + subheaderPaint.measureText(roleCompany) + 10f)
                    canvas.drawText(dateLoc, dateX, currentY, subheaderDatePaint)
                }
                currentY += 13f

                for (bullet in exp.bullets) {
                    checkPageBreak(18f)
                    canvas.drawText("•", MARGIN + 4f, currentY, bodyPaint)
                    drawWrappedText(bullet, bodyPaint, indent = 16f, lineSpacing = 12.5f)
                    currentY += 2f
                }
                currentY += 6f
            }
        }

        // 4. Projects (if present)
        if (resume.projects.isNotEmpty()) {
            drawSectionHeader("Key Projects")
            for (proj in resume.projects) {
                checkPageBreak(35f)
                canvas.drawText(proj.name, MARGIN, currentY, subheaderPaint)
                val techDetails = if (proj.technologies.isNotEmpty()) {
                    proj.technologies.joinToString(", ")
                } else {
                    proj.role
                }
                if (techDetails.isNotBlank()) {
                    val techWidth = subheaderDatePaint.measureText(techDetails)
                    val techX = (PDF_WIDTH - MARGIN - techWidth).coerceAtLeast(MARGIN + subheaderPaint.measureText(proj.name) + 10f)
                    canvas.drawText(techDetails, techX, currentY, subheaderDatePaint)
                }
                currentY += 13f

                for (bullet in proj.bullets) {
                    checkPageBreak(18f)
                    canvas.drawText("•", MARGIN + 4f, currentY, bodyPaint)
                    drawWrappedText(bullet, bodyPaint, indent = 16f, lineSpacing = 12.5f)
                    currentY += 2f
                }
                currentY += 6f
            }
        }

        // 5. Technical Skills
        if (resume.skills.technical.isNotEmpty() || resume.skills.toolsAndFrameworks.isNotEmpty() || resume.skills.softSkills.isNotEmpty()) {
            drawSectionHeader("Technical & Core Skills")
            if (resume.skills.technical.isNotEmpty()) {
                val techLine = "Core Technologies: " + resume.skills.technical.joinToString(", ")
                drawWrappedText(techLine, bodyPaint, indent = 0f, lineSpacing = 13f)
                currentY += 2f
            }
            if (resume.skills.toolsAndFrameworks.isNotEmpty()) {
                val toolsLine = "Tools & Infrastructure: " + resume.skills.toolsAndFrameworks.joinToString(", ")
                drawWrappedText(toolsLine, bodyPaint, indent = 0f, lineSpacing = 13f)
                currentY += 2f
            }
            if (resume.skills.softSkills.isNotEmpty()) {
                val softLine = "Professional & Soft Skills: " + resume.skills.softSkills.joinToString(", ")
                drawWrappedText(softLine, bodyPaint, indent = 0f, lineSpacing = 13f)
                currentY += 2f
            }
            currentY += 4f
        }

        // 6. Education
        if (resume.education.isNotEmpty()) {
            drawSectionHeader("Education")
            for (edu in resume.education) {
                checkPageBreak(30f)
                val degreeTitle = if (edu.fieldOfStudy.isNotBlank()) "${edu.degree} in ${edu.fieldOfStudy}" else edu.degree
                val degreeSchool = "$degreeTitle  —  ${edu.institution}"
                canvas.drawText(degreeSchool, MARGIN, currentY, subheaderPaint)

                val details = listOfNotNull(
                    edu.graduationDate.takeIf { it.isNotBlank() },
                    edu.gpa.takeIf { it.isNotBlank() }
                ).joinToString(" | ")
                if (details.isNotBlank()) {
                    val detWidth = subheaderDatePaint.measureText(details)
                    val detX = (PDF_WIDTH - MARGIN - detWidth).coerceAtLeast(MARGIN + subheaderPaint.measureText(degreeSchool) + 10f)
                    canvas.drawText(details, detX, currentY, subheaderDatePaint)
                }
                currentY += 13f
            }
        }

        document.finishPage(page)

        FileOutputStream(outputFile).use { out ->
            document.writeTo(out)
        }
        document.close()

        return outputFile
    }

    /**
     * Pure Kotlin standard PDF 1.4 generator.
     * Guaranteed to work across all environments including local JVM/Robolectric tests.
     */
    fun exportWithStandardPdf(context: Context, resume: Resume, targetJob: JobTarget): File {
        val fileName = "${getBaseFileName(resume, targetJob)}.pdf"
        val outputFile = File(getResumesDir(context), fileName)

        fun escapePdf(text: String): String {
            return text
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("\r", "")
                .replace("\n", " ")
        }

        // Accumulate page streams
        val pages = mutableListOf<StringBuilder>()
        var currentStream = StringBuilder()
        var currentY = 750f // Coordinates start from bottom-left (0,0) to top-right (612, 792)

        fun newPage() {
            if (currentStream.isNotEmpty()) {
                pages.add(currentStream)
            }
            currentStream = StringBuilder()
            currentY = 750f
        }

        fun checkPageBreak(neededHeight: Float) {
            if (currentY - neededHeight < 40f) {
                newPage()
            }
        }

        fun drawText(text: String, font: String, size: Float, x: Float, y: Float) {
            currentStream.append("BT\n")
            currentStream.append("/$font $size Tf\n")
            currentStream.append("1 0 0 1 $x $y Tm\n")
            currentStream.append("(${escapePdf(text)}) Tj\n")
            currentStream.append("ET\n")
        }

        fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float) {
            currentStream.append("0.5 w\n")
            currentStream.append("0.8 0.8 0.8 RG\n")
            currentStream.append("$x1 $y1 m $x2 $y2 l S\n")
        }

        fun drawWrappedText(text: String, font: String, size: Float, indent: Float = 0f, lineSpacing: Float = 13f) {
            val maxChars = ((532f - indent) / (size * 0.52f)).toInt().coerceAtLeast(30)
            val words = text.split(" ")
            var currentLine = StringBuilder()

            for (word in words) {
                if (currentLine.length + word.length + 1 > maxChars && currentLine.isNotEmpty()) {
                    checkPageBreak(lineSpacing)
                    drawText(currentLine.toString(), font, size, 40f + indent, currentY)
                    currentY -= lineSpacing
                    currentLine = StringBuilder(word)
                } else {
                    if (currentLine.isNotEmpty()) currentLine.append(" ")
                    currentLine.append(word)
                }
            }

            if (currentLine.isNotEmpty()) {
                checkPageBreak(lineSpacing)
                drawText(currentLine.toString(), font, size, 40f + indent, currentY)
                currentY -= lineSpacing
            }
        }

        fun drawSectionHeader(title: String) {
            checkPageBreak(35f)
            currentY -= 12f
            drawText(title.uppercase(), "F1", 12f, 40f, currentY)
            currentY -= 4f
            drawLine(40f, currentY, 572f, currentY)
            currentY -= 12f
        }

        // 1. Name & Contact
        drawText(resume.fullName.ifEmpty { "Candidate Resume" }, "F1", 18f, 40f, currentY)
        currentY -= 18f

        val contactItems = listOfNotNull(
            resume.email.takeIf { it.isNotBlank() },
            resume.phone.takeIf { it.isNotBlank() },
            resume.location.takeIf { it.isNotBlank() },
            resume.linkedinUrl.takeIf { it.isNotBlank() },
            resume.githubUrl.takeIf { it.isNotBlank() }
        )
        val contactStr = contactItems.joinToString("   |   ")
        if (contactStr.isNotBlank()) {
            drawWrappedText(contactStr, "F2", 9.5f, indent = 0f, lineSpacing = 12f)
        }
        currentY -= 4f
        drawLine(40f, currentY, 572f, currentY)
        currentY -= 6f

        // 2. Summary
        if (resume.summary.isNotBlank()) {
            drawSectionHeader("Professional Summary")
            drawWrappedText(resume.summary, "F2", 9.5f, indent = 0f, lineSpacing = 13f)
        }

        // 3. Work Experience
        if (resume.experiences.isNotEmpty()) {
            drawSectionHeader("Work Experience")
            for (exp in resume.experiences) {
                checkPageBreak(35f)
                val roleCompany = "${exp.role}  —  ${exp.company}"
                drawText(roleCompany, "F1", 10.5f, 40f, currentY)

                val dateRange = formatExperienceDate(exp.startDate, exp.endDate, exp.isCurrent)
                val dateLocParts = listOfNotNull(dateRange.takeIf { it.isNotBlank() }, exp.location.takeIf { it.isNotBlank() })
                val dateLoc = dateLocParts.joinToString(" | ")
                if (dateLoc.isNotBlank()) {
                    drawText(dateLoc, "F3", 9f, 420f, currentY)
                }
                currentY -= 14f

                for (bullet in exp.bullets) {
                    checkPageBreak(18f)
                    drawText("-", "F2", 9.5f, 44f, currentY)
                    drawWrappedText(bullet, "F2", 9.5f, indent = 16f, lineSpacing = 12.5f)
                    currentY -= 2f
                }
                currentY -= 6f
            }
        }

        // 4. Projects
        if (resume.projects.isNotEmpty()) {
            drawSectionHeader("Key Projects")
            for (proj in resume.projects) {
                checkPageBreak(30f)
                val projTitle = if (proj.technologies.isNotEmpty()) "${proj.name}  (${proj.technologies.joinToString(", ")})" else proj.name
                drawText(projTitle, "F1", 10.5f, 40f, currentY)
                currentY -= 14f

                for (bullet in proj.bullets) {
                    checkPageBreak(18f)
                    drawText("-", "F2", 9.5f, 44f, currentY)
                    drawWrappedText(bullet, "F2", 9.5f, indent = 16f, lineSpacing = 12.5f)
                    currentY -= 2f
                }
                currentY -= 6f
            }
        }

        // 5. Skills
        if (resume.skills.technical.isNotEmpty() || resume.skills.toolsAndFrameworks.isNotEmpty() || resume.skills.softSkills.isNotEmpty()) {
            drawSectionHeader("Technical & Core Skills")
            if (resume.skills.technical.isNotEmpty()) {
                drawWrappedText("Core Technologies: " + resume.skills.technical.joinToString(", "), "F2", 9.5f, indent = 0f, lineSpacing = 13f)
                currentY -= 2f
            }
            if (resume.skills.toolsAndFrameworks.isNotEmpty()) {
                drawWrappedText("Tools & Platforms: " + resume.skills.toolsAndFrameworks.joinToString(", "), "F2", 9.5f, indent = 0f, lineSpacing = 13f)
                currentY -= 2f
            }
            if (resume.skills.softSkills.isNotEmpty()) {
                drawWrappedText("Professional: " + resume.skills.softSkills.joinToString(", "), "F2", 9.5f, indent = 0f, lineSpacing = 13f)
                currentY -= 2f
            }
        }

        // 6. Education
        if (resume.education.isNotEmpty()) {
            drawSectionHeader("Education")
            for (edu in resume.education) {
                checkPageBreak(25f)
                val degreeTitle = if (edu.fieldOfStudy.isNotBlank()) "${edu.degree} in ${edu.fieldOfStudy}" else edu.degree
                drawText("$degreeTitle  —  ${edu.institution}", "F1", 10.5f, 40f, currentY)

                val details = listOfNotNull(edu.graduationDate.takeIf { it.isNotBlank() }, edu.gpa.takeIf { it.isNotBlank() }).joinToString(" | ")
                if (details.isNotBlank()) {
                    drawText(details, "F3", 9f, 420f, currentY)
                }
                currentY -= 14f
            }
        }

        if (currentStream.isNotEmpty()) {
            pages.add(currentStream)
        }
        if (pages.isEmpty()) {
            pages.add(StringBuilder("BT /F1 14 Tf 40 750 Td (Empty Resume) Tj ET\n"))
        }

        // Assemble PDF 1.4 binary output
        val baos = ByteArrayOutputStream()
        val offsets = mutableListOf<Long>()

        fun writeString(s: String) {
            baos.write(s.toByteArray(StandardCharsets.US_ASCII))
        }

        writeString("%PDF-1.4\n")

        val totalPages = pages.size
        // Object IDs:
        // 1: Catalog
        // 2: Pages root
        // 3: Font F1 (Helvetica-Bold)
        // 4: Font F2 (Helvetica)
        // 5: Font F3 (Helvetica-Oblique)
        // For each page i (0-based):
        //   Page object id = 6 + i*2
        //   Content object id = 7 + i*2
        val pageObjIds = (0 until totalPages).map { 6 + it * 2 }
        val contentObjIds = (0 until totalPages).map { 7 + it * 2 }

        // Obj 1: Catalog
        offsets.add(baos.size().toLong())
        writeString("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n")

        // Obj 2: Pages
        offsets.add(baos.size().toLong())
        val kidsStr = pageObjIds.joinToString(" ") { "$it 0 R" }
        writeString("2 0 obj\n<< /Type /Pages /Kids [$kidsStr] /Count $totalPages >>\nendobj\n")

        // Obj 3: Font F1 (Helvetica-Bold)
        offsets.add(baos.size().toLong())
        writeString("3 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>\nendobj\n")

        // Obj 4: Font F2 (Helvetica)
        offsets.add(baos.size().toLong())
        writeString("4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n")

        // Obj 5: Font F3 (Helvetica-Oblique)
        offsets.add(baos.size().toLong())
        writeString("5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Oblique >>\nendobj\n")

        for (i in 0 until totalPages) {
            val pageObjId = pageObjIds[i]
            val contentObjId = contentObjIds[i]
            val streamBytes = pages[i].toString().toByteArray(StandardCharsets.US_ASCII)

            // Page Object
            offsets.add(baos.size().toLong())
            writeString("$pageObjId 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 $PDF_WIDTH $PDF_HEIGHT] /Resources << /Font << /F1 3 0 R /F2 4 0 R /F3 5 0 R >> >> /Contents $contentObjId 0 R >>\nendobj\n")

            // Content Object
            offsets.add(baos.size().toLong())
            writeString("$contentObjId 0 obj\n<< /Length ${streamBytes.size} >>\nstream\n")
            baos.write(streamBytes)
            writeString("\nendstream\nendobj\n")
        }

        // XRef table
        val startXref = baos.size().toLong()
        val totalObjects = 5 + totalPages * 2
        writeString("xref\n0 ${totalObjects + 1}\n0000000000 65535 f \n")
        for (offset in offsets) {
            writeString(String.format("%010d 00000 n \n", offset))
        }

        writeString("trailer\n<< /Size ${totalObjects + 1} /Root 1 0 R >>\nstartxref\n$startXref\n%%EOF\n")

        FileOutputStream(outputFile).use { fos ->
            baos.writeTo(fos)
        }

        return outputFile
    }

    /**
     * Exports the given resume to a standard Microsoft Word OpenXML (.docx) file.
     * Compatible with Microsoft Word, Google Docs, Apple Pages, and LibreOffice.
     */
    fun exportToDocx(context: Context, resume: Resume, targetJob: JobTarget): File {
        val fileName = "${getBaseFileName(resume, targetJob)}.docx"
        val outputFile = File(getResumesDir(context), fileName)

        fun escapeXml(text: String): String {
            return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;")
        }

        val contentTypesXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
  <Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>
</Types>""".trimIndent()

        val relsXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>""".trimIndent()

        val docRelsXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>""".trimIndent()

        val stylesXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:style w:type="paragraph" w:default="1" w:styleId="Normal">
    <w:name w:val="Normal"/>
    <w:qFormat/>
    <w:rPr>
      <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri" w:cs="Calibri"/>
      <w:sz w:val="21"/>
      <w:color w:val="2D3748"/>
    </w:rPr>
  </w:style>
  <w:style w:type="paragraph" w:styleId="Heading1">
    <w:name w:val="heading 1"/>
    <w:qFormat/>
    <w:pPr>
      <w:spacing w:before="240" w:after="80"/>
      <w:pBdr>
        <w:bottom w:val="single" w:sz="6" w:space="4" w:color="CBD5E0"/>
      </w:pBdr>
    </w:pPr>
    <w:rPr>
      <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri"/>
      <w:b/>
      <w:caps/>
      <w:sz w:val="24"/>
      <w:color w:val="1E3A8A"/>
    </w:rPr>
  </w:style>
</w:styles>""".trimIndent()

        val docBody = StringBuilder()

        // Name Header
        val name = escapeXml(resume.fullName.ifEmpty { "Candidate Resume" })
        docBody.append("""
          <w:p>
            <w:pPr><w:spacing w:after="60"/></w:pPr>
            <w:r>
              <w:rPr><w:b/><w:sz w:val="40"/><w:color w:val="111827"/></w:rPr>
              <w:t>$name</w:t>
            </w:r>
          </w:p>
        """.trimIndent())

        // Contact Line
        val contactItems = mutableListOf<String>()
        if (resume.email.isNotBlank()) contactItems.add(resume.email)
        if (resume.phone.isNotBlank()) contactItems.add(resume.phone)
        if (resume.location.isNotBlank()) contactItems.add(resume.location)
        if (resume.linkedinUrl.isNotBlank()) contactItems.add(resume.linkedinUrl)
        if (resume.githubUrl.isNotBlank()) contactItems.add(resume.githubUrl)
        val contactStr = escapeXml(contactItems.joinToString("  |  "))
        if (contactStr.isNotBlank()) {
            docBody.append("""
              <w:p>
                <w:pPr>
                  <w:spacing w:after="160"/>
                  <w:pBdr><w:bottom w:val="single" w:sz="4" w:space="6" w:color="E2E8F0"/></w:pBdr>
                </w:pPr>
                <w:r>
                  <w:rPr><w:sz w:val="19"/><w:color w:val="4A5568"/></w:rPr>
                  <w:t>$contactStr</w:t>
                </w:r>
              </w:p>
            """.trimIndent())
        }

        fun appendSectionHeading(title: String) {
            docBody.append("""
              <w:p>
                <w:pPr><w:pStyle w:val="Heading1"/></w:pPr>
                <w:r><w:t>${escapeXml(title)}</w:t></w:r>
              </w:p>
            """.trimIndent())
        }

        // Summary
        if (resume.summary.isNotBlank()) {
            appendSectionHeading("Professional Summary")
            docBody.append("""
              <w:p>
                <w:pPr><w:spacing w:after="140"/><w:jc w:val="both"/></w:pPr>
                <w:r><w:t>${escapeXml(resume.summary)}</w:t></w:r>
              </w:p>
            """.trimIndent())
        }

        // Experience
        if (resume.experiences.isNotEmpty()) {
            appendSectionHeading("Work Experience")
            for (exp in resume.experiences) {
                val roleCompany = escapeXml("${exp.role}  —  ${exp.company}")
                val dateRange = formatExperienceDate(exp.startDate, exp.endDate, exp.isCurrent)
                val dateLocParts = listOfNotNull(
                    dateRange.takeIf { it.isNotBlank() },
                    exp.location.takeIf { it.isNotBlank() }
                )
                val dateLoc = escapeXml(dateLocParts.joinToString(" | "))

                docBody.append("""
                  <w:p>
                    <w:pPr><w:spacing w:before="120" w:after="40"/></w:pPr>
                    <w:r>
                      <w:rPr><w:b/><w:sz w:val="22"/><w:color w:val="1A202C"/></w:rPr>
                      <w:t>$roleCompany</w:t>
                    </w:r>
                    <w:r>
                      <w:rPr><w:i/><w:sz w:val="19"/><w:color w:val="718096"/></w:rPr>
                      <w:t>    $dateLoc</w:t>
                    </w:r>
                  </w:p>
                """.trimIndent())

                for (bullet in exp.bullets) {
                    docBody.append("""
                      <w:p>
                        <w:pPr>
                          <w:ind w:left="360" w:hanging="240"/>
                          <w:spacing w:after="40"/>
                        </w:pPr>
                        <w:r><w:t>• </w:t></w:r>
                        <w:r><w:t>${escapeXml(bullet)}</w:t></w:r>
                      </w:p>
                    """.trimIndent())
                }
            }
        }

        // Projects
        if (resume.projects.isNotEmpty()) {
            appendSectionHeading("Key Projects")
            for (proj in resume.projects) {
                val techDetails = if (proj.technologies.isNotEmpty()) {
                    proj.technologies.joinToString(", ")
                } else {
                    proj.role
                }
                val nameTech = escapeXml(if (techDetails.isNotBlank()) "${proj.name}  —  $techDetails" else proj.name)
                docBody.append("""
                  <w:p>
                    <w:pPr><w:spacing w:before="100" w:after="40"/></w:pPr>
                    <w:r>
                      <w:rPr><w:b/><w:sz w:val="22"/></w:rPr>
                      <w:t>$nameTech</w:t>
                    </w:r>
                  </w:p>
                """.trimIndent())

                for (bullet in proj.bullets) {
                    docBody.append("""
                      <w:p>
                        <w:pPr>
                          <w:ind w:left="360" w:hanging="240"/>
                          <w:spacing w:after="40"/>
                        </w:pPr>
                        <w:r><w:t>• </w:t></w:r>
                        <w:r><w:t>${escapeXml(bullet)}</w:t></w:r>
                      </w:p>
                    """.trimIndent())
                }
            }
        }

        // Technical Skills
        if (resume.skills.technical.isNotEmpty() || resume.skills.toolsAndFrameworks.isNotEmpty() || resume.skills.softSkills.isNotEmpty()) {
            appendSectionHeading("Technical & Core Skills")
            if (resume.skills.technical.isNotEmpty()) {
                docBody.append("""
                  <w:p>
                    <w:pPr><w:spacing w:after="40"/></w:pPr>
                    <w:r><w:rPr><w:b/></w:rPr><w:t>Core Technologies: </w:t></w:r>
                    <w:r><w:t>${escapeXml(resume.skills.technical.joinToString(", "))}</w:t></w:r>
                  </w:p>
                """.trimIndent())
            }
            if (resume.skills.toolsAndFrameworks.isNotEmpty()) {
                docBody.append("""
                  <w:p>
                    <w:pPr><w:spacing w:after="40"/></w:pPr>
                    <w:r><w:rPr><w:b/></w:rPr><w:t>Tools &amp; Infrastructure: </w:t></w:r>
                    <w:r><w:t>${escapeXml(resume.skills.toolsAndFrameworks.joinToString(", "))}</w:t></w:r>
                  </w:p>
                """.trimIndent())
            }
            if (resume.skills.softSkills.isNotEmpty()) {
                docBody.append("""
                  <w:p>
                    <w:pPr><w:spacing w:after="40"/></w:pPr>
                    <w:r><w:rPr><w:b/></w:rPr><w:t>Leadership &amp; Professional: </w:t></w:r>
                    <w:r><w:t>${escapeXml(resume.skills.softSkills.joinToString(", "))}</w:t></w:r>
                  </w:p>
                """.trimIndent())
            }
        }

        // Education
        if (resume.education.isNotEmpty()) {
            appendSectionHeading("Education")
            for (edu in resume.education) {
                val degreeTitle = if (edu.fieldOfStudy.isNotBlank()) "${edu.degree} in ${edu.fieldOfStudy}" else edu.degree
                val degreeSchool = escapeXml("$degreeTitle  —  ${edu.institution}")
                val details = escapeXml(listOfNotNull(
                    edu.graduationDate.takeIf { it.isNotBlank() },
                    edu.gpa.takeIf { it.isNotBlank() }
                ).joinToString(" | "))

                docBody.append("""
                  <w:p>
                    <w:pPr><w:spacing w:before="80" w:after="40"/></w:pPr>
                    <w:r>
                      <w:rPr><w:b/><w:sz w:val="21"/></w:rPr>
                      <w:t>$degreeSchool</w:t>
                    </w:r>
                    <w:r>
                      <w:rPr><w:i/><w:sz w:val="19"/><w:color w:val="718096"/></w:rPr>
                      <w:t>    $details</w:t>
                    </w:r>
                  </w:p>
                """.trimIndent())
            }
        }

        val documentXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:body>
    $docBody
    <w:sectPr>
      <w:pgSz w:w="12240" w:h="15840"/>
      <w:pgMar w:top="1152" w:right="1152" w:bottom="1152" w:left="1152"/>
    </w:sectPr>
  </w:body>
</w:document>""".trimIndent()

        FileOutputStream(outputFile).use { fos ->
            ZipOutputStream(fos).use { zos ->
                fun addZipEntry(path: String, content: String) {
                    val entry = ZipEntry(path)
                    zos.putNextEntry(entry)
                    val bytes = content.toByteArray(StandardCharsets.UTF_8)
                    zos.write(bytes, 0, bytes.size)
                    zos.closeEntry()
                }

                addZipEntry("[Content_Types].xml", contentTypesXml)
                addZipEntry("_rels/.rels", relsXml)
                addZipEntry("word/_rels/document.xml.rels", docRelsXml)
                addZipEntry("word/styles.xml", stylesXml)
                addZipEntry("word/document.xml", documentXml)
            }
        }

        return outputFile
    }

    /**
     * Launch system share sheet for the exported file using FileProvider.
     */
    fun shareFile(context: Context, file: File, mimeType: String, title: String = "Share Resume") {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, file.nameWithoutExtension)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    /**
     * Attempt to open the exported file in an external viewer app (e.g. PDF viewer or Office).
     */
    fun openFile(context: Context, file: File, mimeType: String): Boolean {
        return try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }
}
