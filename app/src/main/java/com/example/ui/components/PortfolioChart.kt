package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PortfolioSnapshotEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PortfolioChart(
    snapshots: List<PortfolioSnapshotEntity>,
    selectedInterval: String,
    onIntervalSelected: (String) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val intervals = listOf("1 saat", "24 saat", "7 gün", "30 gün", "3 ay", "1 il")
    var selectedPointIndex by remember(snapshots) { mutableStateOf<Int?>(null) }

    val highlightedSnapshot = selectedPointIndex?.let { idx ->
        if (idx in snapshots.indices) snapshots[idx] else snapshots.lastOrNull()
    } ?: snapshots.lastOrNull()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, VeyraNavyBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header stats above chart
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Portfel Qrafiki",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = VeyraTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    if (highlightedSnapshot != null) {
                        val balAz = highlightedSnapshot.totalBalanceCents / 100.0
                        Text(
                            text = String.format(Locale.US, "%.2f AZN", balAz),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = VeyraTextPrimary
                            )
                        )
                    } else {
                        Text(
                            text = "0.00 AZN",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = VeyraTextPrimary
                            )
                        )
                    }
                }

                if (highlightedSnapshot != null) {
                    val dateFormatted = SimpleDateFormat("dd MMM, HH:mm", Locale("az")).format(Date(highlightedSnapshot.timestampMillis))
                    val profitAz = highlightedSnapshot.profitLossCents / 100.0
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = String.format(Locale.US, "+%.2f AZN Gəlir", profitAz),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VeyraEmerald
                        )
                        Text(
                            text = dateFormatted,
                            fontSize = 11.sp,
                            color = VeyraTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = VeyraEmerald, modifier = Modifier.size(28.dp))
                    }
                } else if (snapshots.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Qrafik məlumatı hazırlanır...",
                            color = VeyraTextMuted,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    val values = snapshots.map { it.totalBalanceCents.toFloat() }
                    val minVal = values.minOrNull() ?: 0f
                    val maxVal = values.maxOrNull() ?: 1f
                    val range = if (maxVal - minVal == 0f) 1f else (maxVal - minVal)

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(snapshots) {
                                detectTapGestures { offset ->
                                    val count = snapshots.size
                                    if (count > 1) {
                                        val step = size.width / (count - 1)
                                        val index = (offset.x / step).toInt().coerceIn(0, count - 1)
                                        selectedPointIndex = index
                                    }
                                }
                            }
                            .pointerInput(snapshots) {
                                detectDragGestures(
                                    onDrag = { change, _ ->
                                        change.consume()
                                        val count = snapshots.size
                                        if (count > 1) {
                                            val step = size.width / (count - 1)
                                            val index = (change.position.x / step).toInt().coerceIn(0, count - 1)
                                            selectedPointIndex = index
                                        }
                                    },
                                    onDragEnd = {
                                        selectedPointIndex = null
                                    }
                                )
                            }
                    ) {
                        val width = size.width
                        val height = size.height
                        val paddingBottom = 16f
                        val paddingTop = 16f
                        val drawHeight = height - paddingTop - paddingBottom
                        val count = snapshots.size

                        if (count < 2) return@Canvas

                        val stepX = width / (count - 1)
                        val points = mutableListOf<Offset>()

                        for (i in snapshots.indices) {
                            val x = i * stepX
                            val normalized = (values[i] - minVal) / range
                            val y = paddingTop + (1f - normalized) * drawHeight
                            points.add(Offset(x, y))
                        }

                        // Build smooth path
                        val strokePath = Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (i in 0 until points.size - 1) {
                                val p0 = points[i]
                                val p1 = points[i + 1]
                                val controlX = (p0.x + p1.x) / 2f
                                cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                            }
                        }

                        // Gradient Area Path
                        val fillPath = Path().apply {
                            addPath(strokePath)
                            lineTo(points.last().x, height)
                            lineTo(points.first().x, height)
                            close()
                        }

                        // Draw Gradient Fill
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    VeyraEmerald.copy(alpha = 0.35f),
                                    VeyraEmerald.copy(alpha = 0.0f)
                                ),
                                startY = paddingTop,
                                endY = height
                            )
                        )

                        // Draw Grid lines
                        for (i in 1..3) {
                            val lineY = paddingTop + (drawHeight / 4f) * i
                            drawLine(
                                color = VeyraNavyBorder.copy(alpha = 0.5f),
                                start = Offset(0f, lineY),
                                end = Offset(width, lineY),
                                strokeWidth = 1f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                            )
                        }

                        // Draw Line Stroke
                        drawPath(
                            path = strokePath,
                            color = VeyraEmerald,
                            style = Stroke(width = 2.8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )

                        // Draw selected point scrubber
                        selectedPointIndex?.let { idx ->
                            if (idx in points.indices) {
                                val pt = points[idx]
                                // Vertical highlight guideline
                                drawLine(
                                    color = VeyraEmerald.copy(alpha = 0.6f),
                                    start = Offset(pt.x, paddingTop),
                                    end = Offset(pt.x, height),
                                    strokeWidth = 1.5f,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                                )
                                // Outer glow circle
                                drawCircle(
                                    color = VeyraEmerald.copy(alpha = 0.3f),
                                    radius = 12f,
                                    center = pt
                                )
                                // Inner solid circle
                                drawCircle(
                                    color = VeyraEmerald,
                                    radius = 5.5f,
                                    center = pt
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Time interval selector chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VeyraNavyElevated, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                intervals.forEach { interval ->
                    val isSelected = interval == selectedInterval
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) VeyraEmerald else Color.Transparent)
                            .clickable { onIntervalSelected(interval) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = interval,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF042017) else VeyraTextSecondary
                        )
                    }
                }
            }
        }
    }
}
