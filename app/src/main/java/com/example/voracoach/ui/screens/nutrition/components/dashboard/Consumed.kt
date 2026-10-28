package com.example.voracoach.ui.screens.nutrition.components.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.theme.VoraCoachTheme

@Composable
fun Consume(){
  AppCard {
    Column {
      Row(
        modifier = Modifier
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = "CALORIES CONSUMED",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(Modifier.height(5.dp))

          Text(
            text = "1230",
            style = MaterialTheme.typography.headlineMedium
          )

          Text(
            text = "goal: 1230 cal",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        val percentage = 35
        Box(
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator(
            progress = {percentage/100f},
            strokeWidth = 6.dp,
            trackColor = MaterialTheme.colorScheme.outline,
            strokeCap = StrokeCap.Round,
            modifier = Modifier.size(72.dp)
          )
          Text(
            "$percentage%",
            style = MaterialTheme.typography.titleSmall
          )
        }

      }
      HorizontalDivider(
        modifier = Modifier.padding(vertical = 15.dp)
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ConsumptionProgress(
          modifier = Modifier.weight(1f),
          barProg = 104,
          barTotal = 140,
          color = MaterialTheme.colorScheme.primary,
          text = "Protein",
        )
        ConsumptionProgress(
          modifier = Modifier.weight(1f),
          barProg = 104,
          barTotal = 140,
          color = MaterialTheme.colorScheme.secondary,
          text = "Carbs",
        )
        ConsumptionProgress(
          modifier = Modifier.weight(1f),
          barProg = 104,
          barTotal = 140,
          color = MaterialTheme.colorScheme.primaryContainer,
          text = "Fat",
        )
      }

    }
  }
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewConsume(){
  VoraCoachTheme {
    Scaffold(
      modifier = Modifier.safeDrawingPadding()
    ) {
      Consume()
    }
  }

}
