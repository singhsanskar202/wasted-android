package com.wasted.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppPickerScreen(
    vm: OnboardingViewModel = viewModel(),
    onSelected: (Set<String>, Map<String, String>) -> Unit
) {
    val apps = remember { vm.installedApps() }
    var selected by remember { mutableStateOf(emptySet<String>()) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(60.dp))
        Text(
            text = "which apps\nwaste your time?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            lineHeight = 36.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Select all that apply.",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.35f),
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(apps, key = { it.packageName }) { app ->
                val isSelected = app.packageName in selected
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(
                            color = if (isSelected) Color(0xFF1A1A1A) else Color(0xFF0D0D0D),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = if (isSelected) 1.dp else 0.dp,
                            color = if (isSelected) Color.White.copy(alpha = 0.25f) else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            selected = if (isSelected) {
                                selected - app.packageName
                            } else {
                                selected + app.packageName
                            }
                        }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            bitmap = app.icon.toBitmap(48, 48).asImageBitmap(),
                            contentDescription = app.label,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = app.label,
                            fontSize = 10.sp,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                            maxLines = 2,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        val count = selected.size
        val plural = if (count != 1) "s" else ""
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val names = apps
                    .filter { it.packageName in selected }
                    .associate { it.packageName to it.label }
                onSelected(selected, names)
            },
            enabled = selected.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = "Track $count app$plural",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(Modifier.height(40.dp))
    }
}
