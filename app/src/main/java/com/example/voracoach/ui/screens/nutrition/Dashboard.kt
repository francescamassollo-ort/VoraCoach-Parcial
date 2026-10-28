package com.example.voracoach.ui.screens.nutrition


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.ui.components.InsightBanner
import com.example.voracoach.ui.components.SectionHeader
import com.example.voracoach.ui.screens.nutrition.components.dashboard.Consume
import com.example.voracoach.ui.screens.nutrition.components.dashboard.Meal
import com.example.voracoach.ui.screens.nutrition.components.dashboard.TitleDate
import com.example.voracoach.ui.screens.nutrition.components.dashboard.Vora
import com.example.voracoach.ui.theme.VoraCoachTheme

@Composable
fun Dashboard(){
  VoraCoachTheme {
    Scaffold { innerPadding ->
      Column(
        modifier = Modifier
          .padding(innerPadding)
          .padding(horizontal = 16.dp)
      ) {
        TitleDate()
        Consume()
        Spacer(
          modifier = Modifier.padding(12.dp)
        )
        InsightBanner(
          message = "You're on track. A protein-rich dinner would help you reach your daily target.",
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(
          modifier = Modifier.padding(8.dp)
        )
        SectionHeader(
          title = "Today's Meals",
          actionText = "View history",
          onActionClick = {},
        )
        Meal(
          calories = "428",
          mealType = "Breakfast",
          food = "Chicken"
        )
        Meal(
          calories = "428",
          mealType = "Breakfast",
          food = "Chicken"
        )
        Meal(
          calories = "428",
          mealType = "Breakfast",
          food = "Chicken"
        )
        Vora()

      }
    }
  }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewDashboard(){
  Dashboard()
}