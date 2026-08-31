package com.example.notemate.data.model

data class Course(
    val id: String,
    val title: String,
    val full: String,
    val category: String, // "engineering", "technology", "management", "science", "commerce", "arts"
    val emoji: String,
    val badge: String, // "popular", "premium", "new"
    val tags: List<String>,
    val price: String
)

data class Testimonial(
    val id: String,
    val name: String,
    val info: String,
    val initials: String,
    val rating: Int,
    val quote: String
)

data class PlanItem(
    val id: String,
    val title: String,
    val price: String,
    val duration: String,
    val features: List<String>,
    val badge: String? = null,
    val isRecommended: Boolean = false
)

data class FAQItem(
    val question: String,
    val answer: String,
    val category: String
)

object NoteMateData {
    val courses = listOf(
        // Engineering
        Course("btech", "B.Tech", "Bachelor of Technology", "engineering", "⚙️", "popular", listOf("CSE", "ECE", "Mech", "Civil", "IT"), "299"),
        Course("be", "BE", "Bachelor of Engineering", "engineering", "🔩", "premium", listOf("EEE", "Aerospace", "Chemical"), "299"),
        Course("mtech", "M.Tech", "Master of Technology", "engineering", "🛠️", "premium", listOf("AI/ML", "VLSI", "Power Systems", "Robotics"), "399"),
        Course("diploma", "Diploma Engg.", "Diploma in Engineering", "engineering", "📐", "new", listOf("Mechanical", "Civil", "Electrical"), "199"),

        // Technology
        Course("bca", "BCA", "Bachelor of Computer Applications", "technology", "💻", "popular", listOf("Programming", "DBMS", "Networking", "Web"), "249"),
        Course("mca", "MCA", "Master of Computer Applications", "technology", "🖥️", "popular", listOf("Software Engg.", "AI", "Data Science"), "349"),
        Course("bsc-cs", "B.Sc (CS / IT)", "Bachelor of Science in CS/IT", "technology", "🔬", "new", listOf("OS", "Algorithms", "Cybersecurity"), "229"),

        // Management
        Course("mba", "MBA", "Master of Business Administration", "management", "👔", "popular", listOf("Finance", "HR", "Marketing", "Operations"), "399"),
        Course("bba", "BBA", "Bachelor of Business Administration", "management", "📈", "popular", listOf("Entrepreneurship", "Accounting", "BRM"), "249"),
        Course("mba-exe", "MBA (Executive)", "Executive MBA", "management", "🏆", "premium", listOf("Corporate", "Leadership", "Global Mgmt."), "499"),

        // Science
        Course("bsc", "B.Sc", "Bachelor of Science", "science", "🧪", "popular", listOf("Physics", "Chemistry", "Maths", "Bio"), "199"),
        Course("msc", "M.Sc", "Master of Science", "science", "🔭", "premium", listOf("Biotech", "Environmental", "Microbiology"), "299"),
        Course("bpharm", "B.Pharm / M.Pharm", "Bachelor / Master of Pharmacy", "science", "💊", "new", listOf("Pharmacognosy", "Medicinal Chem"), "299"),

        // Commerce
        Course("bcom", "B.Com", "Bachelor of Commerce", "commerce", "💰", "popular", listOf("Accounts", "Taxation", "Finance"), "199"),
        Course("mcom", "M.Com", "Master of Commerce", "commerce", "📉", "premium", listOf("Economics", "Auditing", "Business Law"), "249"),
        Course("bba-fin", "BBA (Finance)", "BBA in Financial Markets", "commerce", "📊", "new", listOf("Stock Market", "Banking", "Insurance"), "249"),

        // Arts
        Course("ba", "BA", "Bachelor of Arts", "arts", "🎨", "popular", listOf("History", "Political Sci.", "Sociology", "English"), "149"),
        Course("ma", "MA", "Master of Arts", "arts", "📚", "premium", listOf("Psychology", "Economics", "Philosophy"), "199"),
        Course("llb", "LLB / LLM", "Bachelor / Master of Laws", "arts", "⚖️", "popular", listOf("Criminal Law", "Corporate Law", "IPR"), "399")
    )

    val testimonials = listOf(
        Testimonial("1", "Aarav Mehta", "B.Tech CSE, VIT Pune", "AM", 5, "NOTEMATE completely transformed how I handle assignments. The quality of their notes is outstanding — precise, well-structured, and right on syllabus. Saved me during my exam prep!"),
        Testimonial("2", "Priya Sharma", "MBA Marketing, NMIMS Mumbai", "PS", 5, "The team truly understands what college students need. My MBA project was delivered 2 days ahead of schedule and my professor was very impressed with the research depth."),
        Testimonial("3", "Rohan Gupta", "MCA, Delhi University", "RG", 5, "Fantastic service! Their experts know the exact format required for Delhi University. Revisions were done super fast. Worth every rupee of my Pro plan subscription."),
        Testimonial("4", "Ananya Iyer", "B.Sc Biotech, Bangalore Univ.", "AI", 5, "I was stressed about my practical lab manuals. NOTEMATE sorted everything perfectly, with all the diagrams and observations accurately documented. Highly recommended!"),
        Testimonial("5", "Karan Patel", "B.Com, Gujarat University", "KP", 5, "Very affordable pricing for a student on a budget. The Starter plan gave me exactly what I needed for 3 months. Now upgraded to Pro — no looking back!"),
        Testimonial("6", "Sneha Reddy", "LLB, Osmania University", "SR", 5, "Legal assignments can be very complex, but NOTEMATE has writers who understand case laws and legal frameworks. My moot court preparation notes were exceptional!")
    )

    val plans = listOf(
        PlanItem(
            id = "starter",
            title = "Starter Plan",
            price = "₹199",
            duration = "per assignment / mo",
            features = listOf(
                "Up to 2 Assignment Solutions",
                "Handwritten / Typed PDF notes",
                "Standard 48-Hour Delivery",
                "1 Free Revision",
                "Plagiarism Free Guarantee"
            ),
            badge = "Essential",
            isRecommended = false
        ),
        PlanItem(
            id = "pro",
            title = "Pro Scholar",
            price = "₹499",
            duration = "per month",
            features = listOf(
                "Up to 6 Assignments & Projects",
                "Priority 24-Hour Delivery",
                "Full Lab Manuals & Diagrams",
                "Unlimited Revisions",
                "Direct Subject Expert Support",
                "Custom PPT & Formatted Reports"
            ),
            badge = "Most Popular",
            isRecommended = true
        ),
        PlanItem(
            id = "semester",
            title = "Semester VIP Pass",
            price = "₹899",
            duration = "full semester",
            features = listOf(
                "Unlimited Course Material & Notes",
                "Major & Minor Project Solutions",
                "Express 12-Hour Urgent Delivery",
                "Dedicated Academic Mentor",
                "Complete Exam Revision Guides",
                "24/7 WhatsApp VIP Helpline"
            ),
            badge = "Best Value",
            isRecommended = false
        )
    )

    val faqs = listOf(
        FAQItem(
            "How does NoteMate ensure academic quality?",
            "Every assignment, note set, or project is prepared by verified subject experts and verified against university syllabus standards before delivery. We ensure 100% plagiarism-free, properly formatted work.",
            "Quality"
        ),
        FAQItem(
            "How fast can I get my assignment done?",
            "Standard turnaround is 24 to 48 hours. Urgent delivery options (12 hours) are available for Pro and Semester VIP Pass members.",
            "Delivery"
        ),
        FAQItem(
            "Can I request revisions if my professor asks for changes?",
            "Yes! Starter plans include 1 free revision, while Pro and VIP plans include unlimited free revisions within 7 days of submission.",
            "Revisions"
        ),
        FAQItem(
            "What payment methods are supported?",
            "We support all UPI apps (GPay, PhonePe, Paytm), NetBanking, Debit/Credit Cards via secure Razorpay checkout, and Cash on Delivery (COD) upon assignment handover.",
            "Payment"
        ),
        FAQItem(
            "Is my personal details and college name confidential?",
            "Absolutely. NoteMate adheres to strict student privacy. Your identity, college name, and assignments are fully encrypted and never shared with third parties.",
            "Privacy"
        )
    )
}
