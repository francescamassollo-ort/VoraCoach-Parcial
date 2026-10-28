package com.example.voracoach.ui.screens.nutrition.components.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.ui.theme.VoraCoachTheme
import org.w3c.dom.Text

@Composable
fun Meal(
  calories: String,
  mealType: String,
  food: String //esto deberia ser una lista
){
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(10.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Column() {
      Text(
        mealType,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight(700)

        )
      Text(
        food,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Text(
      "$calories kcal",
      style = MaterialTheme.typography.titleSmall,
      fontWeight = FontWeight(700)
    )
  }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMeal(){
  VoraCoachTheme {
    Scaffold(
      modifier = Modifier.safeDrawingPadding()
    ) {
      Meal(
        calories = "428",
        mealType = "Breakfast",
        food = "Chicken"
      )
    }
  }
}