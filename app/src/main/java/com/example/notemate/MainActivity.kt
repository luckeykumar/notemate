package com.example.notemate

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notemate.ui.dialogs.*
import com.example.notemate.ui.screens.*
import com.example.notemate.ui.theme.*
import com.example.notemate.ui.viewmodel.NoteMateViewModel
import com.example.notemate.ui.viewmodel.NoteMateViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: NoteMateViewModel by viewModels {
        val app = application as NoteMateApplication
        NoteMateViewModelFactory(app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            NoteMateTheme(darkTheme = isDarkMode) {
                NoteMateResponsiveApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteMateResponsiveApp(viewModel: NoteMateViewModel) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    var currentScreen by remember { mutableStateOf("home") } // "home", "courses", "bookings", "pricing", "admin", "profile"

    // Dialog state collections
    val showBookingDialog by viewModel.showBookingDialog.collectAsState()
    val showAuthDialog by viewModel.showAuthDialog.collectAsState()
    val showPaymentDialog by viewModel.showPaymentDialog.collectAsState()
    val showAdminAuthDialog by viewModel.showAdminAuthDialog.collectAsState()
    val showHelpDialog by viewModel.showHelpDialog.collectAsState()
    val showTermsDialog by viewModel.showTermsDialog.collectAsState()
    val selectedBookingDetail by viewModel.selectedBookingDetail.collectAsState()
    val selectedCourseForBooking by viewModel.selectedCourseForBooking.collectAsState()
    val selectedPlanForPayment by viewModel.selectedPlanForPayment.collectAsState()

    // Handle Toast messages
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Predictive / System Back Handling
    val isAnyDialogShowing = showAuthDialog || showBookingDialog || (selectedBookingDetail != null) ||
            showPaymentDialog || showAdminAuthDialog || showHelpDialog || showTermsDialog

    BackHandler(enabled = isAnyDialogShowing || currentScreen != "home") {
        when {
            showAuthDialog -> viewModel.showAuthDialog.value = false
            showBookingDialog -> viewModel.showBookingDialog.value = false
            selectedBookingDetail != null -> viewModel.selectBookingDetail(null)
            showPaymentDialog -> viewModel.showPaymentDialog.value = false
            showAdminAuthDialog -> viewModel.showAdminAuthDialog.value = false
            showHelpDialog -> viewModel.showHelpDialog.value = false
            showTermsDialog -> viewModel.showTermsDialog.value = false
            currentScreen != "home" -> currentScreen = "home"
        }
    }

    // Measure window constraints natively (Adapts directly to Phone, Tablet, Laptop/Desktop)
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isCompact = maxWidth < 600.dp
        val isMedium = maxWidth in 600.dp..840.dp
        val isExpanded = maxWidth > 840.dp
        val showSideNav = !isCompact

        Row(modifier = Modifier.fillMaxSize()) {
            // ── SIDE NAVIGATION (For Tablet & Laptop/Desktop) ──
            if (showSideNav) {
                if (isExpanded) {
                    // Wide Navigation Drawer for Laptops & Desktops
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .width(240.dp)
                            .fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                // Brand Header
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { currentScreen = "home" }
                                        .padding(bottom = 20.dp, top = 8.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_notemate_logo),
                                        contentDescription = "NoteMate Logo",
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "NOTEMATE",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Academic Portal",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AccentGold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(16.dp))

                                // Nav Items (Full Width with Labels)
                                DrawerNavItem(
                                    label = "Home",
                                    icon = if (currentScreen == "home") Icons.Filled.Home else Icons.Outlined.Home,
                                    selected = currentScreen == "home",
                                    onClick = { currentScreen = "home" },
                                    testTag = "drawer_home"
                                )
                                DrawerNavItem(
                                    label = "Courses & Degrees",
                                    icon = if (currentScreen == "courses") Icons.Filled.School else Icons.Outlined.School,
                                    selected = currentScreen == "courses",
                                    onClick = { currentScreen = "courses" },
                                    testTag = "drawer_courses"
                                )
                                DrawerNavItem(
                                    label = "My Booked Slots",
                                    icon = if (currentScreen == "bookings") Icons.Filled.Assignment else Icons.Outlined.Assignment,
                                    selected = currentScreen == "bookings",
                                    onClick = { currentScreen = "bookings" },
                                    testTag = "drawer_bookings"
                                )
                                DrawerNavItem(
                                    label = "Pricing Plans",
                                    icon = if (currentScreen == "pricing") Icons.Filled.CreditCard else Icons.Outlined.CreditCard,
                                    selected = currentScreen == "pricing",
                                    onClick = { currentScreen = "pricing" },
                                    testTag = "drawer_pricing"
                                )
                                DrawerNavItem(
                                    label = "Student Profile",
                                    icon = if (currentScreen == "profile") Icons.Filled.Person else Icons.Outlined.Person,
                                    selected = currentScreen == "profile",
                                    onClick = { currentScreen = "profile" },
                                    testTag = "drawer_profile"
                                )
                                DrawerNavItem(
                                    label = "Admin Portal",
                                    icon = if (currentScreen == "admin") Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                                    selected = currentScreen == "admin",
                                    onClick = {
                                        if (viewModel.isAdminLoggedIn.value) {
                                            currentScreen = "admin"
                                        } else {
                                            viewModel.showAdminAuthDialog.value = true
                                        }
                                    },
                                    testTag = "drawer_admin"
                                )
                            }

                            // Footer with Dark Mode & User Profile
                            Column {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { viewModel.toggleDarkMode() }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                            contentDescription = "Theme",
                                            tint = AccentGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = if (isDarkMode) "Light Mode" else "Dark Mode",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (currentUser == null) {
                                                viewModel.showAuthDialog.value = true
                                            } else {
                                                currentScreen = "profile"
                                            }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = AccentGold.copy(alpha = 0.25f),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = currentUser?.name?.firstOrNull()?.uppercase() ?: "G",
                                                    fontWeight = FontWeight.Bold,
                                                    color = AccentGold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = currentUser?.name ?: "Sign In",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = if (currentUser != null) (currentUser?.course ?: "Student") else "Tap to sync",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextMuted,
                                                fontSize = 10.sp,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Compact Navigation Rail for Tablets
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        header = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 12.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_notemate_logo),
                                    contentDescription = "NoteMate Logo",
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "NOTEMATE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        NavigationRailItem(
                            selected = currentScreen == "home",
                            onClick = { currentScreen = "home" },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen == "home") Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text("Home") },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                indicatorColor = AccentGold
                            ),
                            modifier = Modifier.testTag("rail_home")
                        )

                        NavigationRailItem(
                            selected = currentScreen == "courses",
                            onClick = { currentScreen = "courses" },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen == "courses") Icons.Filled.School else Icons.Outlined.School,
                                    contentDescription = "Courses"
                                )
                            },
                            label = { Text("Courses") },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                indicatorColor = AccentGold
                            ),
                            modifier = Modifier.testTag("rail_courses")
                        )

                        NavigationRailItem(
                            selected = currentScreen == "bookings",
                            onClick = { currentScreen = "bookings" },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen == "bookings") Icons.Filled.Assignment else Icons.Outlined.Assignment,
                                    contentDescription = "My Slots"
                                )
                            },
                            label = { Text("My Slots") },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                indicatorColor = AccentGold
                            ),
                            modifier = Modifier.testTag("rail_bookings")
                        )

                        NavigationRailItem(
                            selected = currentScreen == "pricing",
                            onClick = { currentScreen = "pricing" },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen == "pricing") Icons.Filled.CreditCard else Icons.Outlined.CreditCard,
                                    contentDescription = "Plans"
                                )
                            },
                            label = { Text("Plans") },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                indicatorColor = AccentGold
                            ),
                            modifier = Modifier.testTag("rail_pricing")
                        )

                        NavigationRailItem(
                            selected = currentScreen == "profile",
                            onClick = { currentScreen = "profile" },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen == "profile") Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Profile"
                                )
                            },
                            label = { Text("Profile") },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                indicatorColor = AccentGold
                            ),
                            modifier = Modifier.testTag("rail_profile")
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Dark mode toggle on rail
                        IconButton(
                            onClick = { viewModel.toggleDarkMode() },
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme",
                                tint = AccentGold
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = AccentGold.copy(alpha = 0.2f),
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .size(38.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (currentUser == null) {
                                        viewModel.showAuthDialog.value = true
                                    } else {
                                        currentScreen = "profile"
                                    }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = currentUser?.name?.firstOrNull()?.uppercase() ?: "?",
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // ── MAIN CONTENT SCAFFOLD ──
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { currentScreen = "home" }
                            ) {
                                if (isCompact) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_notemate_logo),
                                        contentDescription = "NoteMate Logo",
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                }
                                Column {
                                    Text(
                                        text = "NOTEMATE",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = when {
                                            isExpanded -> "Academic Excellence Desk"
                                            isMedium -> "Academic Partner • Tablet"
                                            else -> "Academic Partner"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AccentGold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        },
                        actions = {
                            if (currentUser == null) {
                                Button(
                                    onClick = { viewModel.showAuthDialog.value = true },
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .height(36.dp)
                                        .testTag("topbar_signin_btn")
                                ) {
                                    Text("Sign In", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Surface(
                                    shape = CircleShape,
                                    color = AccentGold.copy(alpha = 0.2f),
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .clickable { currentScreen = "profile" }
                                        .testTag("topbar_avatar_btn")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = currentUser?.name?.firstOrNull()?.uppercase() ?: "U",
                                            fontWeight = FontWeight.Bold,
                                            color = AccentGold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                },
                bottomBar = {
                    // Show bottom navigation bar only on mobile/compact screens
                    if (isCompact) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = currentScreen == "home",
                                onClick = { currentScreen = "home" },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == "home") Icons.Filled.Home else Icons.Outlined.Home,
                                        contentDescription = "Home"
                                    )
                                },
                                label = { Text("Home") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    indicatorColor = AccentGold
                                ),
                                modifier = Modifier.testTag("nav_home")
                            )

                            NavigationBarItem(
                                selected = currentScreen == "courses",
                                onClick = { currentScreen = "courses" },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == "courses") Icons.Filled.School else Icons.Outlined.School,
                                        contentDescription = "Courses"
                                    )
                                },
                                label = { Text("Courses") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    indicatorColor = AccentGold
                                ),
                                modifier = Modifier.testTag("nav_courses")
                            )

                            NavigationBarItem(
                                selected = currentScreen == "bookings",
                                onClick = { currentScreen = "bookings" },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == "bookings") Icons.Filled.Assignment else Icons.Outlined.Assignment,
                                        contentDescription = "My Slots"
                                    )
                                },
                                label = { Text("My Slots") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    indicatorColor = AccentGold
                                ),
                                modifier = Modifier.testTag("nav_bookings")
                            )

                            NavigationBarItem(
                                selected = currentScreen == "pricing",
                                onClick = { currentScreen = "pricing" },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == "pricing") Icons.Filled.CreditCard else Icons.Outlined.CreditCard,
                                        contentDescription = "Plans"
                                    )
                                },
                                label = { Text("Plans") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    indicatorColor = AccentGold
                                ),
                                modifier = Modifier.testTag("nav_pricing")
                            )

                            NavigationBarItem(
                                selected = currentScreen == "admin" || currentScreen == "profile",
                                onClick = { currentScreen = "profile" },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == "profile") Icons.Filled.Person else Icons.Outlined.Person,
                                        contentDescription = "Profile"
                                    )
                                },
                                label = { Text("Profile") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    indicatorColor = AccentGold
                                ),
                                modifier = Modifier.testTag("nav_profile")
                            )
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (currentScreen) {
                        "home" -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToCourses = { currentScreen = "courses" },
                            onNavigateToPricing = { currentScreen = "pricing" }
                        )
                        "courses" -> CoursesScreen(viewModel = viewModel)
                        "bookings" -> MyBookingsScreen(viewModel = viewModel)
                        "pricing" -> PricingScreen(viewModel = viewModel)
                        "admin" -> AdminScreen(viewModel = viewModel)
                        "profile" -> ProfileSettingsScreen(
                            viewModel = viewModel,
                            onNavigateToAdmin = { currentScreen = "admin" }
                        )
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (showAuthDialog) {
        AuthDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showAuthDialog.value = false }
        )
    }

    if (showBookingDialog) {
        BookingDialog(
            course = selectedCourseForBooking,
            viewModel = viewModel,
            onDismiss = { viewModel.showBookingDialog.value = false }
        )
    }

    if (selectedBookingDetail != null) {
        BookingDetailDialog(
            booking = selectedBookingDetail!!,
            onDismiss = { viewModel.selectBookingDetail(null) },
            onStatusToggle = { newStatus ->
                selectedBookingDetail?.let {
                    viewModel.updateBookingStatus(it.id, newStatus)
                    viewModel.selectBookingDetail(null)
                }
            }
        )
    }

    if (showPaymentDialog) {
        PaymentDialog(
            plan = selectedPlanForPayment,
            viewModel = viewModel,
            onDismiss = { viewModel.showPaymentDialog.value = false }
        )
    }

    if (showAdminAuthDialog) {
        AdminAuthDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showAdminAuthDialog.value = false },
            onSuccess = { currentScreen = "admin" }
        )
    }

    if (showHelpDialog) {
        HelpFaqDialog(
            onDismiss = { viewModel.showHelpDialog.value = false }
        )
    }

    if (showTermsDialog) {
        TermsDialog(
            onDismiss = { viewModel.showTermsDialog.value = false }
        )
    }
}

@Composable
private fun DrawerNavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (selected) AccentGold.copy(alpha = 0.2f) else Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) AccentGold else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
