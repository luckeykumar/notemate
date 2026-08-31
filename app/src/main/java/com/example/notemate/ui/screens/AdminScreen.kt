package com.example.notemate.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notemate.data.model.ActivityEntity
import com.example.notemate.data.model.BookingEntity
import com.example.notemate.data.model.ContactEntity
import com.example.notemate.data.model.UserEntity
import com.example.notemate.ui.theme.*
import com.example.notemate.ui.viewmodel.NoteMateViewModel

@Composable
fun AdminScreen(
    viewModel: NoteMateViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()

    val userCount by viewModel.userCount.collectAsState()
    val bookingCount by viewModel.bookingCount.collectAsState()
    val contactCount by viewModel.contactCount.collectAsState()
    val activityCount by viewModel.activityCount.collectAsState()

    val allBookings by viewModel.allBookings.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allContacts by viewModel.allContacts.collectAsState()
    val allActivities by viewModel.allActivities.collectAsState()

    var selectedTab by remember { mutableStateOf("dashboard") } // "dashboard", "bookings", "users", "contacts", "activity", "agent"
    var adminSearchQuery by remember { mutableStateOf("") }

    if (!isAdminLoggedIn) {
        // Locked state
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = AccentGold.copy(alpha = 0.15f),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = AccentGold,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Coordinator Portal Locked",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Access student submissions, live pipeline, and system logs with your admin credentials.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 6.dp, bottom = 20.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Button(
                    onClick = { viewModel.showAdminAuthDialog.value = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.Default.VpnKey, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unlock with Master Key", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isLaptop = maxWidth >= 840.dp
        val isTablet = maxWidth in 600.dp..839.dp
        val horizontalPad = if (isLaptop) 40.dp else if (isTablet) 28.dp else 20.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 1180.dp)
            ) {
                // Admin Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPad),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = AccentGold, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Admin Management",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(text = "Live NoteMate Control Panel & Analytics", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }

                    IconButton(
                        onClick = { viewModel.adminLogout() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout Admin", tint = AccentRose)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Admin Navigation Tabs
                LazyRow(
                    contentPadding = PaddingValues(horizontal = horizontalPad),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabs = listOf(
                        "dashboard" to "Dashboard",
                        "bookings" to "Bookings ($bookingCount)",
                        "users" to "Users ($userCount)",
                        "contacts" to "Contacts ($contactCount)",
                        "activity" to "Activity ($activityCount)",
                        "agent" to "Email Agent"
                    )
                    items(tabs) { (key, label) ->
                        val isSelected = selectedTab == key
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) AccentGold else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedTab = key }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    "dashboard" -> {
                        LazyColumn(
                            contentPadding = PaddingValues(start = horizontalPad, end = horizontalPad, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                    item {
                        // Responsive KPI Grid
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            if (maxWidth >= 600.dp) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    KpiCard(title = "Users", count = "$userCount", icon = Icons.Default.People, tint = AccentBlue, modifier = Modifier.weight(1f))
                                    KpiCard(title = "Bookings", count = "$bookingCount", icon = Icons.Default.Assignment, tint = AccentGold, modifier = Modifier.weight(1f))
                                    KpiCard(title = "Inquiries", count = "$contactCount", icon = Icons.Default.Mail, tint = AccentEmerald, modifier = Modifier.weight(1f))
                                    KpiCard(title = "Logins", count = "$activityCount", icon = Icons.Default.History, tint = AccentPurple, modifier = Modifier.weight(1f))
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        KpiCard(title = "Users", count = "$userCount", icon = Icons.Default.People, tint = AccentBlue, modifier = Modifier.weight(1f))
                                        KpiCard(title = "Bookings", count = "$bookingCount", icon = Icons.Default.Assignment, tint = AccentGold, modifier = Modifier.weight(1f))
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        KpiCard(title = "Inquiries", count = "$contactCount", icon = Icons.Default.Mail, tint = AccentEmerald, modifier = Modifier.weight(1f))
                                        KpiCard(title = "Logins", count = "$activityCount", icon = Icons.Default.History, tint = AccentPurple, modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    item {
                        // Export Action Bar
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Export Data (CSV)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text(text = "Copy formatted table to clipboard", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                }
                                Button(
                                    onClick = {
                                        val csv = viewModel.exportCsvData("bookings")
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("NoteMate Bookings CSV", csv))
                                        viewModel.showToast("Bookings CSV copied to clipboard! 📋")
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black)
                                ) {
                                    Text("Copy CSV")
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Recent Booking Slots",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(allBookings.take(5)) { booking ->
                        AdminBookingItem(
                            booking = booking,
                            onToggleStatus = { newStatus -> viewModel.updateBookingStatus(booking.id, newStatus) },
                            onDelete = { viewModel.deleteBooking(booking.id) }
                        )
                    }
                }
            }

            "bookings" -> {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                    OutlinedTextField(
                        value = adminSearchQuery,
                        onValueChange = { adminSearchQuery = it },
                        placeholder = { Text("Filter bookings by student, subject, course...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AccentGold) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val filtered = allBookings.filter {
                        adminSearchQuery.isBlank() ||
                                it.name.contains(adminSearchQuery, ignoreCase = true) ||
                                it.subject.contains(adminSearchQuery, ignoreCase = true) ||
                                it.course.contains(adminSearchQuery, ignoreCase = true)
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filtered, key = { it.id }) { booking ->
                            AdminBookingItem(
                                booking = booking,
                                onToggleStatus = { newStatus -> viewModel.updateBookingStatus(booking.id, newStatus) },
                                onDelete = { viewModel.deleteBooking(booking.id) }
                            )
                        }
                    }
                }
            }

            "users" -> {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(allUsers, key = { it.id }) { user ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 4.dp,
                            modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = user.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text(text = user.course, style = MaterialTheme.typography.labelSmall, color = AccentGold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                if (user.phone.isNotBlank()) {
                                    Text(text = "Phone: ${user.phone}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                }
                            }
                        }
                    }
                }
            }

            "contacts" -> {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(allContacts, key = { it.id }) { contact ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 4.dp,
                            modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = contact.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    IconButton(
                                        onClick = { viewModel.deleteContact(contact.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AccentRose, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Text(text = "${contact.email} • ${contact.course}", style = MaterialTheme.typography.labelSmall, color = AccentGold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = contact.message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            "activity" -> {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(allActivities, key = { it.id }) { act ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = act.name.ifBlank { act.email }, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text(text = act.email, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                }
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (act.action == "login") AccentBlue.copy(alpha = 0.2f) else AccentEmerald.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = act.action.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (act.action == "login") AccentBlue else AccentEmerald,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            "agent" -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(AccentEmerald))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Email Agent Webhook Active",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = AccentEmerald
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Webhook URL: https://luckkyy.app.n8n.cloud/webhook/notemate-email-agent",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "All student booking slots and custom messages trigger automated assignment routing and notification dispatch.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
}
}

@Composable
private fun KpiCard(
    title: String,
    count: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = count, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text(text = title, style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
        }
    }
}

@Composable
private fun AdminBookingItem(
    booking: BookingEntity,
    onToggleStatus: (String) -> Unit,
    onDelete: () -> Unit
) {
    val isDone = booking.status == "done"
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { onToggleStatus(if (isDone) "pending" else "done") },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDone) AccentEmerald else AccentGold,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = if (isDone) "✓ Done" else "⏳ Pending",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AccentRose, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${booking.course} • ${booking.subject}",
                style = MaterialTheme.typography.bodySmall,
                color = AccentGold
            )
            Text(
                text = "Contact: ${booking.email} (${booking.phone})",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            Text(
                text = "Type: ${booking.type} • Due: ${booking.date}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}
