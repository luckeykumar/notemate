package com.example.notemate.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notemate.data.model.Course
import com.example.notemate.data.model.NoteMateData
import com.example.notemate.data.model.Testimonial
import com.example.notemate.ui.theme.*
import com.example.notemate.ui.viewmodel.NoteMateViewModel

@Composable
fun HomeScreen(
    viewModel: NoteMateViewModel,
    onNavigateToCourses: () -> Unit,
    onNavigateToPricing: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val scrollState = rememberScrollState()

    // Contact form state
    var contactName by remember { mutableStateOf(currentUser?.name ?: "") }
    var contactEmail by remember { mutableStateOf(currentUser?.email ?: "") }
    var contactCourse by remember { mutableStateOf("B.Tech") }
    var contactMessage by remember { mutableStateOf("") }

    // Fast Calculator state for Hero on Laptop
    var calcCourseIndex by remember { mutableStateOf(0) }
    var calcPages by remember { mutableStateOf(20f) }
    var calcUrgent by remember { mutableStateOf(false) }

    val sampleCourses = listOf(
        "B.Tech Computer Science" to 249,
        "MBA Management" to 299,
        "BCA Software Lab" to 199,
        "B.Pharma Medicinal Notes" to 279,
        "B.Com Financial Acc" to 179
    )

    val calculatedPrice = remember(calcCourseIndex, calcPages, calcUrgent) {
        val base = sampleCourses[calcCourseIndex].second
        val pageMultiplier = (calcPages / 10f).coerceAtLeast(1f)
        val raw = (base * pageMultiplier).toInt()
        if (calcUrgent) (raw * 1.35).toInt() else raw
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isLaptop = maxWidth >= 840.dp
        val isTablet = maxWidth in 600.dp..839.dp
        val isWide = isLaptop || isTablet

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── HERO SECTION ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(
                        horizontal = if (isLaptop) 48.dp else if (isTablet) 32.dp else 20.dp,
                        vertical = if (isLaptop) 36.dp else 24.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLaptop) {
                    // 2-Column Expansive Laptop Hero
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 1180.dp),
                        horizontalArrangement = Arrangement.spacedBy(36.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Column: Hero Text & Actions
                        Column(
                            modifier = Modifier.weight(1.15f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = AccentGold.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.4f)),
                                modifier = Modifier.padding(bottom = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "✦ India's Premier Academic Writing Partner",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentGold
                                    )
                                }
                            }

                            Text(
                                text = "Ace Your University Exams & Submissions",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontSize = 42.sp,
                                    lineHeight = 48.sp
                                ),
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Get verified handwritten notes, university assignments, lab reports, and major project documentation crafted by Master's & PhD subject experts tailored for your syllabus.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextMuted,
                                lineHeight = 24.sp
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = onNavigateToCourses,
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black),
                                    modifier = Modifier
                                        .height(50.dp)
                                        .testTag("hero_explore_btn")
                                ) {
                                    Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Explore All Courses", fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.selectCourseForBooking(NoteMateData.courses.first())
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .height(50.dp)
                                        .testTag("hero_book_slot_btn")
                                ) {
                                    Icon(Icons.Default.EditCalendar, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Book Writing Slot", fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            // Trust Badges
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⭐", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("4.9/5 Rating (2.4k+ Reviews)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⚡", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("24h Express Turnaround", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        // Right Column: Interactive Quick Quote & Calculator Card
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 6.dp,
                            modifier = Modifier
                                .weight(0.85f)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = AccentGold.copy(alpha = 0.2f),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Calculate, contentDescription = null, tint = AccentGold, modifier = Modifier.size(20.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("Quick Estimator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                            Text("Instant price estimate", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = AccentEmerald.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            "Guaranteed Plagiarism-Free",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = AccentEmerald,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Select Department
                                Text("Select Discipline", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(sampleCourses.size) { index ->
                                        val isSel = calcCourseIndex == index
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSel) AccentGold else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.clickable { calcCourseIndex = index }
                                        ) {
                                            Text(
                                                text = sampleCourses[index].first.split(" ").first(),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSel) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Length Slider
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Assignment Length", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                                    Text("${calcPages.toInt()} pages / modules", style = MaterialTheme.typography.labelMedium, color = AccentGold, fontWeight = FontWeight.Bold)
                                }
                                Slider(
                                    value = calcPages,
                                    onValueChange = { calcPages = it },
                                    valueRange = 5f..50f,
                                    steps = 8,
                                    colors = SliderDefaults.colors(
                                        thumbColor = AccentGold,
                                        activeTrackColor = AccentGold
                                    )
                                )

                                // Urgent toggle
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Bolt, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Urgent Delivery (12-24h)", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Switch(
                                        checked = calcUrgent,
                                        onCheckedChange = { calcUrgent = it }
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Estimated Starting Price", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                        Text(
                                            "₹$calculatedPrice",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Black,
                                            color = AccentGold
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val matched = NoteMateData.courses.firstOrNull { it.title.contains(sampleCourses[calcCourseIndex].first.split(" ").first()) }
                                                ?: NoteMateData.courses.first()
                                            viewModel.selectCourseForBooking(matched)
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black)
                                    ) {
                                        Text("Book This Scope", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Mobile & Tablet Centered Hero
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.widthIn(max = 800.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = AccentGold.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.4f)),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✦ India's Premier Academic Writing Service",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGold
                                )
                            }
                        }

                        Text(
                            text = "Your Academic\nSuccess Partner",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = if (isTablet) 38.sp else 30.sp,
                                lineHeight = if (isTablet) 44.sp else 36.sp
                            ),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Expert handwritten notes, university assignments, lab manuals, and major projects curated for college students across all disciplines.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = TextMuted,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .widthIn(max = 640.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 480.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = onNavigateToCourses,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("hero_explore_btn")
                            ) {
                                Text("Explore Courses", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.selectCourseForBooking(NoteMateData.courses.first())
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("hero_book_slot_btn")
                            ) {
                                Text("Book a Slot")
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Stats row
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 4.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 700.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatItem(number = "98%", label = "Satisfaction")
                                HorizontalDivider(modifier = Modifier.height(32.dp).width(1.dp), color = MaterialTheme.colorScheme.outline)
                                StatItem(number = "500+", label = "Expert Writers")
                                HorizontalDivider(modifier = Modifier.height(32.dp).width(1.dp), color = MaterialTheme.colorScheme.outline)
                                StatItem(number = "15K+", label = "Delivered")
                                if (isTablet) {
                                    HorizontalDivider(modifier = Modifier.height(32.dp).width(1.dp), color = MaterialTheme.colorScheme.outline)
                                    StatItem(number = "24/7", label = "Support")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ── WHY CHOOSE US (FEATURES GRID) ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isLaptop) 48.dp else if (isTablet) 32.dp else 20.dp)
                    .widthIn(max = 1180.dp)
            ) {
                Text(
                    text = "Why Students Trust NoteMate",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Everything you need to excel in university exams, lab practicals, and major projects",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                if (isWide) {
                    // 4 columns on Tablet & Laptop
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        FeatureCard(
                            icon = Icons.Default.Verified,
                            title = "Subject Experts",
                            desc = "Writers with Master's & PhD backgrounds in your exact field.",
                            tint = AccentGold,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        FeatureCard(
                            icon = Icons.Default.Speed,
                            title = "Express Delivery",
                            desc = "24-48h standard turnaround with 12h urgent emergency option.",
                            tint = AccentBlue,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        FeatureCard(
                            icon = Icons.Default.CheckCircle,
                            title = "100% Plagiarism Free",
                            desc = "Original handwritten & digital text adhering to grading rubrics.",
                            tint = AccentEmerald,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        FeatureCard(
                            icon = Icons.Default.CurrencyRupee,
                            title = "Pocket Friendly",
                            desc = "Affordable student rates starting at ₹149 with UPI & POD support.",
                            tint = AccentRose,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FeatureCard(
                            icon = Icons.Default.Verified,
                            title = "Subject Experts",
                            desc = "Writers with Master's & PhD backgrounds in your exact field.",
                            tint = AccentGold,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        FeatureCard(
                            icon = Icons.Default.Speed,
                            title = "Express Delivery",
                            desc = "Turnaround in 24-48h with 12h urgent option.",
                            tint = AccentBlue,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FeatureCard(
                            icon = Icons.Default.CheckCircle,
                            title = "100% Plagiarism Free",
                            desc = "Original research meeting university rubrics.",
                            tint = AccentEmerald,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        FeatureCard(
                            icon = Icons.Default.CurrencyRupee,
                            title = "Pocket Friendly",
                            desc = "Rates starting at ₹149. UPI & POD supported.",
                            tint = AccentRose,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ── POPULAR COURSES CAROUSEL / GRID ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isLaptop) 48.dp else if (isTablet) 32.dp else 20.dp)
                    .widthIn(max = 1180.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Featured Academic Programs",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Browse top modules across Engineering, Management, Pharmacy & Commerce",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = onNavigateToCourses,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("View All 19 →", color = AccentGold, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(NoteMateData.courses.take(10)) { course ->
                        CourseCardHome(
                            course = course,
                            onBookClick = { viewModel.selectCourseForBooking(course) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── STUDENT TESTIMONIALS ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isLaptop) 48.dp else if (isTablet) 32.dp else 20.dp)
                    .widthIn(max = 1180.dp)
            ) {
                Text(
                    text = "What Students Say",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Authentic reviews from university students achieving distinction grades",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (isLaptop) {
                    // 3-Column Reviews on Laptop
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        NoteMateData.testimonials.take(3).forEach { review ->
                            Box(modifier = Modifier.weight(1f)) {
                                ReviewCard(review = review, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(NoteMateData.testimonials) { review ->
                            ReviewCard(review = review)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── QUICK INQUIRY & CONTACT DESK ──
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isLaptop) 48.dp else if (isTablet) 32.dp else 20.dp)
                    .widthIn(max = 1180.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
            ) {
                if (isLaptop) {
                    // 2-Column Contact Desk for Laptop
                    Row(
                        modifier = Modifier.padding(32.dp),
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        // Left Column: Help Desk Info
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = AccentGold.copy(alpha = 0.2f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = AccentGold, modifier = Modifier.size(24.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Academic Coordination Desk", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    Text("Direct access to writers and project guides", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Need custom thesis chapters, IEEE formatted research papers, special PowerPoint presentations, or express 12-hour turnaround?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Phone, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Direct Helpline: +91 98765 43210", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Email, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Official Desk: support@notemate.academic.in", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }

                        // Right Column: Form
                        Column(modifier = Modifier.weight(1.2f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = contactName,
                                    onValueChange = { contactName = it },
                                    label = { Text("Your Name") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = contactEmail,
                                    onValueChange = { contactEmail = it },
                                    label = { Text("Your Email") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = contactCourse,
                                onValueChange = { contactCourse = it },
                                label = { Text("Course / Department (e.g. B.Tech CSE, MBA Finance)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = contactMessage,
                                onValueChange = { contactMessage = it },
                                label = { Text("Message / Assignment Scope Details") },
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (contactName.isNotBlank() && contactEmail.isNotBlank() && contactMessage.isNotBlank()) {
                                        viewModel.submitContact(contactName.trim(), contactEmail.trim(), contactCourse.trim(), contactMessage.trim())
                                        contactMessage = ""
                                    } else {
                                        viewModel.showToast("Please enter Name, Email, and Message.")
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("home_contact_submit_btn")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Send Inquiries to Academic Desk", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // Mobile & Tablet Form
                    Column(modifier = Modifier.padding(22.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = AccentGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Have Custom Questions?",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Reach our academic coordination desk for thesis, custom PPTs, or special requests.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        if (isTablet) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = contactName,
                                    onValueChange = { contactName = it },
                                    label = { Text("Your Name") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = contactEmail,
                                    onValueChange = { contactEmail = it },
                                    label = { Text("Your Email") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        } else {
                            OutlinedTextField(
                                value = contactName,
                                onValueChange = { contactName = it },
                                label = { Text("Your Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = contactEmail,
                                onValueChange = { contactEmail = it },
                                label = { Text("Your Email") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        OutlinedTextField(
                            value = contactCourse,
                            onValueChange = { contactCourse = it },
                            label = { Text("Course / Department") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = contactMessage,
                            onValueChange = { contactMessage = it },
                            label = { Text("Message / Assignment Details") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (contactName.isNotBlank() && contactEmail.isNotBlank() && contactMessage.isNotBlank()) {
                                    viewModel.submitContact(contactName.trim(), contactEmail.trim(), contactCourse.trim(), contactMessage.trim())
                                    contactMessage = ""
                                } else {
                                    viewModel.showToast("Please enter Name, Email, and Message.")
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("home_contact_submit_btn")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Send Message", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun StatItem(number: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = number,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = AccentGold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
    }
}

@Composable
private fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = TextMuted, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun CourseCardHome(
    course: Course,
    onBookClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        modifier = Modifier
            .width(220.dp)
            .height(180.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AccentGold.copy(alpha = 0.15f),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = course.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp
                    )
                }
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = course.full,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 2
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "From ₹${course.price}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold
                )
                Button(
                    onClick = onBookClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Book", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ReviewCard(
    review: Testimonial,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        modifier = modifier
            .then(if (modifier == Modifier) Modifier.width(260.dp) else Modifier)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = AccentGold.copy(alpha = 0.2f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = review.initials,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = review.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Text(text = review.info, style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row {
                repeat(review.rating) {
                    Text("★", color = AccentGold, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = review.quote,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}
