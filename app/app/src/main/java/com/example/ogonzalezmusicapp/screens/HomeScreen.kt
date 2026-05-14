package com.example.ogonzalezmusicapp.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
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
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    onAlbumSelected: (Album) -> Unit
) {
    var albums by remember { mutableStateOf<List<Album>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.instance.getAlbums()
            }
            albums = response
        } catch (e: Exception) {
            Log.e("API_ERROR", "Fallo al descargar: ${e.message}")
            errorMessage = "No se pudieron cargar los álbumes. Verifica tu conexión."
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF7B42F6), Color(0xFFF0E6FF), Color(0xFFF0E6FF)),
                    startY = 0f,
                    endY = 600f
                )
            )
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
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFF7B42F6),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* podría reintentar */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B42F6))
                    ) {
                        Text("Reintentar", color = Color.White)
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    item { HomeHeader() }

                    item {
                        SectionTitle(title = "Albums", onSeeMore = { })
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(albums) { album ->
                                AlbumLargeCard(album = album, onClick = {
                                    onAlbumSelected(album)
                                    onNavigateToDetail(album.id)
                                })
                            }
                        }
                    }

                    item {
                        SectionTitle(title = "Recently Played", onSeeMore = { })
                    }

                    items(albums) { album ->
                        RecentlyPlayedCard(album = album, onClick = {
                            onAlbumSelected(album)
                            onNavigateToDetail(album.id)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF9B59F6), Color(0xFF5A1BE8))
                )
            )
    ) {
        Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text("Good Morning!", color = Color(0xFFD5B9FF), fontSize = 14.sp)
            Text(
                "Victor Chavez",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SectionTitle(title: String, onSeeMore: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2A2A2A)
        )
        Text(
            text = "See more",
            color = Color(0xFF7B42F6),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onSeeMore() }
        )
    }
}

@Composable
fun AlbumLargeCard(album: Album, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(album.image)
                    .crossfade(true)
                    .setHeader("User-Agent", "Mozilla/5.0")
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onState = { state ->
                    if (state is coil.compose.AsyncImagePainter.State.Error) {
                        Log.e("COIL_ERROR", "Error cargando imagen: ${state.result.throwable}")
                    }
                }
            )
            // Overlay oscuro degradado en la parte inferior
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xEE220934))
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            album.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            album.artist,
                            color = Color(0xFFB39DDB),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .padding(4.dp)
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color(0xFF220934),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentlyPlayedCard(album: Album, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(album.image)
                    .crossfade(true)
                    .setHeader("User-Agent", "Mozilla/5.0")
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = album.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${album.artist} • Popular Song",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.Gray)
        }
    }
}
