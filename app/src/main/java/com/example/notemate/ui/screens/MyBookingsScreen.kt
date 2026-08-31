package com.example.notemate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notemate.data.model.BookingEntity
import com.example.notemate.ui.theme.*
import com.example.notemate.ui.viewmodel.NoteMateViewModel

@Composable
fun MyBookingsScreen(
    viewModel: NoteMateViewModel,
    modifier: Modifier = Modifier
) {
    val bookings by viewModel.myBookings.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var statusFilter by remember { mutableStateOf("all") } // "all", "pending", "in_progress", "done"

    val filteredBookings = remember(bookings, statusFilter) {
        if (statusFilter == "all") bookings
        else bookings.filter { it.status.equals(statusFilter, ignoreCase = true) }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isLaptop = maxWidth >= 840.dp
        val isTablet = maxWidth in 600.dp..839.dp
        val isWide = isLaptop || isTablet
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
                // Title row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPad),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "My Booked Slots",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (currentUser != null) "Logged in as ${currentUser?.name}" else "Tracking your assignment requests",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }

                    IconButton(
                        onClick = { viewModel.showBookingDialog.value = true },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AccentGold)
                            .testTag("fab_new_booking")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Booking",
                            tint = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Status Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = horizontalPad),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf(
                        "all" to "All Requests (${bookings.size})",
                        "pending" to "Pending ⏳ (${bookings.count { it.status == "pending" }})",
                        "in_progress" to "In Progress 🚀 (${bookings.count { it.status == "in_progress" }})",
                        "done" to "Completed ✓ (${bookings.count { it.status == "done" }})"
                    )
                    items(filters) { (key, label) ->
                        val isSelected = statusFilter == key
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) AccentGold else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { statusFilter = key }
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

                if (filteredBookings.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "📝", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No booking slots found",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Ready for your next assignment? Tap '+' to book a dedicated academic writing slot.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted,
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )
                            Button(
                                onClick = { viewModel.showBookingDialog.value = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black)
                            ) {
                                Text("Book First Slot", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else if (isWide) {
                    // Responsive Grid for Tablet (2 col) & Laptop (3 col)
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 340.dp),
                        contentPadding = PaddingValues(start = horizontalPad, end = horizontalPad, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredBookings, key = { it.id }) { booking ->
                            BookingCardItem(
                                booking = booking,
                                onClick = { viewModel.selectBookingDetail(booking) }
                            )
                        }
                    }
                } else {
                    // Standard 1-column list on mobile
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredBookings, key = { it.id }) { booking ->
                            BookingCardItem(
                                booking = booking,
                                onClick = { viewModel.selectBookingDetail(booking) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingCardItem(
    booking: BookingEntity,
    onClick: () -> Unit
) {
    val statusColor = when (booking.status.lowercase()) {
        "done" -> AccentEmerald
        "in_progress" -> AccentBlue
        else -> AccentGold
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(statusColor))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = booking.status.replace("_", " ").uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }

                Text(
                    text = "Due: ${booking.date}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = booking.subject,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "${booking.course} • ${booking.type}",
                style = MaterialTheme.typography.bodySmall,
                color = AccentGold,
                modifier = Modifier.padding(top = 2.dp)
            )

            if (booking.college.isNotBlank()) {
                Text(
                    text = "🏫 ${booking.college}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "Plan: ${booking.plan}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "View Details",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
