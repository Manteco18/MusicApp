package com.example.ogonzalezmusicapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ogonzalezmusicapp.models.Album
import com.example.ogonzalezmusicapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DetailScreen(
    id: String,
    onNavigateBack: () -> Unit
) {
    var album by remember { mutableStateOf<Album?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(id) {
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.instance.getAlbumById(id)
            }
            album = response
        } catch (e: Exception) {
            errorMessage = "No se pudo cargar el álbum. Verifica tu conexión."
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0E6FF))
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF7B42F6)
                )
            }
            errorMessage != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = { onNavigateBack() },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF7B42F6))
                    }
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFF7B42F6),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            else -> {
                album?.let { currentAlbum ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        // Header con imagen full-width y scrim morado
                        item {
                            Box(modifier = Modifier.fillMaxWidth().height(380.dp)) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(currentAlbum.image)
                                        .crossfade(true)
                                        .setHeader("User-Agent", "Mozilla/5.0")
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                // Scrim morado oscuro degradado
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0x661E0A3C),
                                                    Color.Transparent,
                                                    Color(0xFF1E0A3C)
                                                ),
                                                startY = 0f,
                                                endY = Float.POSITIVE_INFINITY
                                            )
                                        )
                                )

                                // Fila superior: botón back y favorito
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp, start = 8.dp, end = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    IconButton(onClick = { onNavigateBack() }) {
                                        Icon(
                                            Icons.Default.ArrowBack,
                                            contentDescription = "Back",
                                            tint = Color.White
                                        )
                                    }
                                    IconButton(onClick = { }) {
                                        Icon(
                                            Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorite",
                                            tint = Color.White
                                        )
                                    }
                                }

                                // Título, artista y botones de acción en la parte inferior
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(24.dp)
                                ) {
                                    Text(
                                        text = currentAlbum.title,
                                        color = Color.White,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = currentAlbum.artist,
                                        color = Color(0xFFB39DDB),
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        // Botón Play morado
                                        IconButton(
                                            onClick = { },
                                            modifier = Modifier
                                                .background(Color(0xFF7B42F6), CircleShape)
                                                .size(56.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.PlayArrow,
                                                contentDescription = "Play",
                                                tint = Color.White
                                            )
                                        }
                                        // Botón Shuffle blanco
                                        IconButton(
                                            onClick = { },
                                            modifier = Modifier
                                                .background(Color.White, CircleShape)
                                                .size(56.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Shuffle,
                                                contentDescription = "Shuffle",
                                                tint = Color(0xFF220934)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Card "About this album"
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "About this album",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF2A2A2A)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = currentAlbum.description
                                            ?: "Un álbum extraordinario que combina sonidos únicos con letras profundas, creando una experiencia musical inolvidable.",
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }

                        // Chip "Artist"
                        item {
                            Surface(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(24.dp),
                                color = Color.White,
                                shadowElevation = 2.dp
                            ) {
                                Text(
                                    text = "Artist: ${currentAlbum.artist}",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    fontSize = 14.sp,
                                    color = Color(0xFF7B42F6),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(12.dp)) }

                        // 10 canciones ficticias
                        items(10) { index ->
                            TrackItem(album = currentAlbum, trackNumber = index + 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackItem(album: Album, trackNumber: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Número de pista
            Text(
                text = "$trackNumber",
                color = Color(0xFFB39DDB),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(album.image)
                    .crossfade(true)
                    .setHeader("User-Agent", "Mozilla/5.0")
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${album.title} • Track $trackNumber",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = album.artist,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.Gray)
        }
    }
}
