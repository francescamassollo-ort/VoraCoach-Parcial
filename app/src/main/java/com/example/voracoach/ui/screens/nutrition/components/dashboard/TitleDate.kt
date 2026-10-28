package com.example.voracoach.ui.screens.nutrition.components.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.ui.components.CircleIconButton
import com.example.voracoach.ui.components.CircleIconButtonStyle

@Composable
fun TitleDate(){
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 20.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Column {
      Text(
        text = "Fecha de hoy",
        style = MaterialTheme.typography.labelSmall
      )
      Text(
        text = "Nutrition",
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight(700)
        )
      )
    }
    CircleIconButton(
      contentDescription = "Calendar",
      icon = Icons.Outlined.CalendarToday,
      onClick = {},
      style = CircleIconButtonStyle.Light
    )
  }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTitleDate(){
  TitleDate()
}
