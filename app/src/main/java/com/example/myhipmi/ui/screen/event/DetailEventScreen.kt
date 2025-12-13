package com.example.myhipmi.ui.screen.event

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myhipmi.R
import com.example.myhipmi.data.remote.response.EventItemResponse
import com.example.myhipmi.data.remote.retrofit.ApiConfig
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailEventScreen(navController: NavController, eventId: Int) {
    var eventDetail by remember { mutableStateOf<EventItemResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val apiService = remember { ApiConfig.getApiService() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(eventId) {
        if (eventId > 0) {
            isLoading = true
            errorMessage = null
            try {
                val response = apiService.getEventById(eventId)
                if (response.isSuccessful) {
                    eventDetail = response.body()?.event
                } else {
                    errorMessage = "Gagal memuat detail event (${response.code()})."
                }
            } catch (e: Exception) {
                errorMessage = "Koneksi gagal: ${e.localizedMessage ?: "Terjadi kesalahan jaringan"}"
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
            errorMessage = "ID Event tidak valid."
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            MyHipmiTopBar(
                title = "Detail Event",
                onBackClick = { navController.popBackStack() },
                onMenuClick = { }
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF3B643A),
                                strokeWidth = 3.dp
                            )
                        }
                    }
                    errorMessage != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = Color(0xFFB65B5B),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = errorMessage!!,
                                    color = Color(0xFF666666),
                                    textAlign = TextAlign.Center,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    eventDetail != null -> {
                        EventDetailContent(event = eventDetail!!)
                    }
                }
            }
        }
    )
}

@Composable
fun EventDetailContent(event: EventItemResponse) {
    val formattedDate = try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val date = inputFormat.parse(event.tanggal)
        date?.let { outputFormat.format(it) } ?: event.tanggal
    } catch (e: Exception) {
        event.tanggal
    }

    val formattedTime = try {
        event.waktu.substring(0, 5) + " WIB"
    } catch (e: Exception) {
        event.waktu
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header dengan gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            // Poster Image
            AsyncImage(
                model = event.posterUrl,
                contentDescription = "Poster Event",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.myhipmi_logo),
                error = painterResource(R.drawable.myhipmi_logo)
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF000000).copy(alpha = 0.7f)
                            ),
                            startY = 100f
                        )
                    )
            )

            // Event Title di atas poster
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Text(
                    text = event.namaEvent,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    lineHeight = 32.sp
                )
            }
        }

        // Content Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Quick Info Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickInfoCard(
                    icon = Icons.Default.CalendarToday,
                    label = "Tanggal",
                    value = formattedDate,
                    color = Color(0xFFE8F5E9),
                    iconColor = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                QuickInfoCard(
                    icon = Icons.Default.Schedule,
                    label = "Waktu",
                    value = formattedTime,
                    color = Color(0xFFE3F2FD),
                    iconColor = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detail Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    SectionTitle(text = "Informasi Event")

                    Spacer(modifier = Modifier.height(16.dp))

                    DetailInfoRow(
                        icon = Icons.Default.Place,
                        label = "Lokasi",
                        value = event.tempat,
                        iconColor = Color(0xFFE91E63)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailInfoRow(
                        icon = Icons.Default.Checkroom,
                        label = "Dress Code",
                        value = event.dresscode ?: "Tidak Ada",
                        iconColor = Color(0xFF9C27B0)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailInfoRow(
                        icon = Icons.Default.Person,
                        label = "Penyelenggara",
                        value = event.penyelenggara,
                        iconColor = Color(0xFF3B643A)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailInfoRow(
                        icon = Icons.Default.Call,
                        label = "Contact Person",
                        value = event.contactPerson ?: "Tidak Ada",
                        iconColor = Color(0xFFFF9800)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    SectionTitle(text = "Deskripsi Event")

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = event.deskripsi ?: "Tidak ada deskripsi.",
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QuickInfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF666666),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontSize = 12.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun DetailInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF999999),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF333333)
    )
}