import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.homescreen.TaskState
import com.example.myapplication.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calendar(state:TaskState) {
    var selectedDate by remember { mutableIntStateOf(-1) }
    val weekdays = listOf("mon", "tue", "wed", "thu", "fri", "sat", "sun")

    // Bottom sheet state functionality, https://developer.android.com/develop/ui/compose/components/bottom-sheets
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF4E4853))
    ) {
        Image(
            painter = painterResource(id = R.drawable.homescreenshapes),
            contentDescription = "Shape",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 48.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Plan your life",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 84.dp, bottom = 2.dp)
                )
                Text(
                    text = "Add event forward in time",
                    fontSize = 22.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp)
            ) {
                Text(
                    text = "june 2024",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 22.dp, bottom = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    weekdays.forEach { day ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = day,
                                fontSize = 20.sp,
                                color = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }

            val daysInMonth = 30
            val firstDayOffset =
                5 // June 1st is a Saturday, so offset is 5 (0-based index for Saturday)
            for (week in 0 until 6) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    for (day in 0 until 7) {
                        val date = week * 7 + day - firstDayOffset + 1
                        if (date in 1..daysInMonth) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .weight(1f)
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedDate == date) Color(0x8021B6E8) else Color.Transparent)
                                    .border(
                                        width = 2.dp,
                                        color = if (selectedDate != date) Color(0x3B21B6E8) else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selectedDate = date
                                        scope.launch { sheetState.show() }
                                        showBottomSheet = true
                                    }
                            ) {
                                Text(
                                    text = "$date",
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .weight(1f)
                                    .size(48.dp)
                            )
                        }
                    }
                }
            }
        }
        // Bottom Sheet functionality
        if (showBottomSheet) {
            val tasksForSelectedDate = state.task.filter {
                val dayOfMonth = "\\d+".toRegex().find(it.date)?.value?.toIntOrNull()
                dayOfMonth == selectedDate
            }
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(285.dp)  // Adjust height of the bottom sheet
                        .padding(16.dp)
                        .background(Color(0xFF4E4853), shape = RoundedCornerShape(16.dp))
                ) {
                    // Bottom Sheet content for each date
                    LazyColumn(  // Made the content scrollable
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        items(tasksForSelectedDate) { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 16.dp)  // Add more padding to the right of task name
                                ) {
                                    Text(
                                        text = task.name,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(start = 16.dp)  // More padding to the left of task name
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(end = 24.dp)  // Add padding to move text more to the left
                                ) {
                                    Text(
                                        text = task.time,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.White
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