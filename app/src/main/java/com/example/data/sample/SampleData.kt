package com.example.data.sample

import com.example.data.model.*

object SampleData {

    val sampleResumeSoftware = Resume(
        id = "sample_alex_chen",
        title = "Alex Chen - Software Engineer",
        rawText = """
ALEX CHEN
San Francisco, CA | alex.chen@example.com | (555) 234-5678 | linkedin.com/in/alexchen-dev | github.com/alexchen-code

EDUCATION
University of California, Berkeley — B.S. in Computer Science (GPA: 3.82)
Graduation: May 2025

TECHNICAL SKILLS
Languages: Kotlin, Java, Python, TypeScript, SQL, C++
Frameworks & Libraries: Jetpack Compose, Android SDK, React, Node.js, Spring Boot, Room DB, Coroutines
Developer Tools: Git, Docker, Gradle, Android Studio, Postman, CI/CD (GitHub Actions), Firebase

EXPERIENCE
NexTech Innovations — Software Engineering Intern (June 2024 – August 2024)
- Worked on the core Android client team developing customer checkout features.
- Implemented responsive Jetpack Compose screens for product catalog navigation.
- Fixed network latency issues by caching API data with Room Database and Kotlin Coroutines.
- Collaborated with QA and design teams in bi-weekly Agile sprints.

Campus Tech Solutions — Mobile App Developer (September 2023 – May 2024)
- Built an internal student shuttle tracking Android application used by 3,500+ active students.
- Integrated Google Maps SDK for real-time bus location updates and schedule ETA calculation.
- Refactored legacy Java code to modern Kotlin with MVVM architecture.

PROJECTS
CareerFit Job Tracker — Open Source Project (January 2024 – Present)
- Developed an offline-first Android application to manage job applications and interview schedules.
- Utilized Room, StateFlow, and Material Design 3 for dynamic theming and search filtering.
- Received 180+ stars on GitHub and contributions from community members.

SmartCampus Dining Assistant — Hackathon Winner (October 2023)
- Created a nutritional dining hall recommender in Kotlin with Firebase backend.
- Won 1st Place out of 45 teams at CalHacks for best mobile UX and accessibility.

CERTIFICATIONS & AWARDS
- Dean's Honors List (6 consecutive semesters)
- Meta Certified Android Developer Associate
        """.trimIndent(),
        fullName = "Alex Chen",
        email = "alex.chen@example.com",
        phone = "(555) 234-5678",
        location = "San Francisco, CA",
        linkedinUrl = "linkedin.com/in/alexchen-dev",
        githubUrl = "github.com/alexchen-code",
        summary = "Computer Science graduate with hands-on Android engineering internship experience. Proficient in Kotlin, Jetpack Compose, and modern architecture, eager to build high-scale consumer applications.",
        education = listOf(
            EducationItem(
                id = "edu_1",
                institution = "University of California, Berkeley",
                degree = "B.S. in Computer Science",
                fieldOfStudy = "Computer Science",
                graduationDate = "May 2025",
                gpa = "3.82 / 4.0"
            )
        ),
        skills = ResumeSkills(
            technical = listOf("Kotlin", "Java", "Python", "TypeScript", "SQL", "C++"),
            toolsAndFrameworks = listOf("Jetpack Compose", "Android SDK", "Room DB", "Coroutines", "Git", "Docker", "Gradle", "Firebase", "CI/CD"),
            softSkills = listOf("Cross-functional Collaboration", "Agile Sprints", "Problem Solving", "Code Reviews")
        ),
        experiences = listOf(
            WorkExperience(
                id = "exp_1",
                company = "NexTech Innovations",
                role = "Software Engineering Intern",
                location = "San Francisco, CA",
                startDate = "June 2024",
                endDate = "August 2024",
                isCurrent = false,
                bullets = listOf(
                    "Worked on the core Android client team developing customer checkout features.",
                    "Implemented responsive Jetpack Compose screens for product catalog navigation.",
                    "Fixed network latency issues by caching API data with Room Database and Kotlin Coroutines.",
                    "Collaborated with QA and design teams in bi-weekly Agile sprints."
                )
            ),
            WorkExperience(
                id = "exp_2",
                company = "Campus Tech Solutions",
                role = "Mobile App Developer",
                location = "Berkeley, CA",
                startDate = "September 2023",
                endDate = "May 2024",
                isCurrent = false,
                bullets = listOf(
                    "Built an internal student shuttle tracking Android application used by 3,500+ active students.",
                    "Integrated Google Maps SDK for real-time bus location updates and schedule ETA calculation.",
                    "Refactored legacy Java code to modern Kotlin with MVVM architecture."
                )
            )
        ),
        projects = listOf(
            ProjectItem(
                id = "proj_1",
                name = "CareerFit Job Tracker",
                role = "Lead Developer",
                description = "Offline-first Android application to manage job applications and interview schedules.",
                bullets = listOf(
                    "Built offline-first data caching with Room, Kotlin StateFlow, and Material Design 3.",
                    "Earned 180+ GitHub stars with active open-source community contributions."
                ),
                technologies = listOf("Kotlin", "Jetpack Compose", "Room", "Coroutines", "M3")
            ),
            ProjectItem(
                id = "proj_2",
                name = "SmartCampus Dining Assistant",
                role = "Hackathon Developer",
                description = "Nutritional dining hall recommender mobile application.",
                bullets = listOf(
                    "Developed personalized dining meal suggestions using Kotlin and Firebase.",
                    "Won 1st Place out of 45 teams at CalHacks for accessible UI design."
                ),
                technologies = listOf("Kotlin", "Firebase", "Android SDK")
            )
        ),
        certifications = listOf(
            "Meta Certified Android Developer Associate",
            "Dean's Honors List (6 consecutive semesters)"
        ),
        achievements = listOf(
            "CalHacks 1st Place Mobile Division Award",
            "Open Source Contributor on Android Tooling"
        )
    )

    val sampleJobGoogle = JobTarget(
        companyName = "Google",
        jobTitle = "Software Engineer, Android (Early Career)",
        experienceLevel = "Entry-level / New Grad",
        location = "Mountain View, CA / Remote",
        jobDescription = """
About the role:
As an Early Career Software Engineer on the Android Platform team at Google, you will build user-facing experiences and performant systems that reach billions of active devices worldwide.

Responsibilities:
- Design, develop, test, deploy, maintain, and enhance Android software applications and SDK components.
- Write clean, maintainable, and high-performance Kotlin and Java code adhering to strict engineering excellence guidelines.
- Collaborate with product managers, UX designers, and backend infrastructure teams to deliver intuitive and responsive mobile interfaces.
- Diagnose performance bottlenecks, optimize memory allocation, and improve cold app startup times.
- Implement robust unit, integration, and automated UI testing.

Minimum qualifications:
- Bachelor's degree in Computer Science, Computer Engineering, or equivalent practical experience.
- Experience with Android development using Kotlin or Java.
- Experience with data structures, algorithms, and software design principles.

Preferred qualifications:
- Experience building UI with Jetpack Compose and modern Android architecture components (ViewModel, Flow/Coroutines).
- Experience with offline storage, caching strategies (such as Room or SQLite), and RESTful API integration.
- Familiarity with CI/CD pipelines, Git version control, and code review standards.
- Demonstrated passion for mobile app accessibility and performance optimization.
        """.trimIndent(),
        additionalInstructions = "Highlight performance optimizations, Jetpack Compose, and system reliability without fabricating unverified metrics."
    )

    val sampleJobStripe = JobTarget(
        companyName = "Stripe",
        jobTitle = "Mobile Software Engineer - Payments Experience",
        experienceLevel = "Entry to Mid-Level",
        location = "San Francisco, CA / Seattle, WA",
        jobDescription = """
Stripe is looking for Mobile Software Engineers to help build the future of global economic infrastructure.

Responsibilities:
- Build delightful, secure, and rock-solid checkout experiences on Android for millions of businesses worldwide.
- Partner with engineers across the stack to build resilient offline-ready APIs and client caching mechanisms.
- Drive adoption of modern Android standards: Kotlin Coroutines, Jetpack Compose, and clean modular code.
- Champion code quality through automated testing, architectural reviews, and instrumentation telemetry.

Requirements:
- Strong proficiency in Kotlin and modern Android SDK development.
- Deep understanding of asynchronous programming, concurrency, and client-side storage.
- Passion for crafting polished UI interactions and accessible designs.
- Clear communication skills and ability to collaborate across remote teams.
        """.trimIndent()
    )

    val sampleResumeSneha = Resume(
        id = "sample_sneha_verma",
        title = "Sneha Verma - Software Developer",
        rawText = """
Sneha Verma
Bengaluru, India | sneha@example.com | +91 98765 43210

EDUCATION
XYZ University — B.Tech in Computer Science (2021 - 2025)

PROJECTS
StudentHub — A web platform for college communities
- Developed a web application for student collaboration and resource sharing.
- Built backend services using Node.js and Express with MongoDB integration.

FocusMate — Productivity tracker using AI nudges
- Developed a task management app using React and Node.js.
- Implemented state tracking and interactive productivity widgets.

SKILLS
Python, JavaScript, React, Node.js, SQL
        """.trimIndent(),
        fullName = "Sneha Verma",
        email = "sneha@example.com",
        phone = "+91 98765 43210",
        location = "Bengaluru, India",
        summary = "Computer Science undergraduate passionate about full-stack engineering. Experienced in building responsive web applications using React, Node.js, and modern databases.",
        education = listOf(
            EducationItem(
                id = "edu_sneha_1",
                institution = "XYZ University",
                degree = "B.Tech in Computer Science",
                fieldOfStudy = "Computer Science",
                graduationDate = "2025"
            )
        ),
        experiences = listOf(
            WorkExperience(
                id = "exp_sneha_1",
                company = "TechStart Labs",
                role = "Software Developer Intern",
                location = "Bengaluru, India",
                startDate = "Jan 2024",
                endDate = "June 2024",
                bullets = listOf(
                    "Built reusable UI components in React and optimized REST API endpoints.",
                    "Collaborated with senior engineers to implement user authentication flows."
                )
            )
        ),
        projects = listOf(
            ProjectItem(
                id = "proj_sneha_1",
                name = "StudentHub",
                role = "Full-Stack Developer",
                description = "A web platform for college communities.",
                bullets = listOf(
                    "Developed a collaborative platform connecting 1,200+ campus students.",
                    "Implemented RESTful endpoints with Node.js and MongoDB."
                ),
                technologies = listOf("React", "Node.js", "MongoDB", "JavaScript")
            ),
            ProjectItem(
                id = "proj_sneha_2",
                name = "FocusMate",
                role = "Frontend & Backend Developer",
                description = "Task Management App using React and Node.js.",
                bullets = listOf(
                    "Developed a task management app using React and Node.js.",
                    "Implemented interactive task timers and milestone tracking."
                ),
                technologies = listOf("React", "Node.js", "JavaScript")
            )
        ),
        skills = ResumeSkills(
            technical = listOf("Python", "JavaScript", "React", "Node.js", "SQL"),
            toolsAndFrameworks = listOf("Git", "MongoDB", "Express", "REST APIs"),
            softSkills = listOf("Problem Solving", "Collaboration", "Rapid Prototyping")
        )
    )
}
