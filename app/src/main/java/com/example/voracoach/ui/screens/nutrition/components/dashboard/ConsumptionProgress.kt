package com.example.voracoach.ui.screens.nutrition.components.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.ui.theme.VoraCoachTheme


@Composable
fun ConsumptionProgress(
  text: String,
  barProg: Int,
  barTotal: Int,
  color: Color,
  modifier: Modifier
){
  Column(
    modifier = modifier,

  ) {
    Text(
      text,
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Row {
      Text(
        "${barProg}g",
        style = MaterialTheme.typography.titleSmall,
        //color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        "/${barTotal}g",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    val progress = if (barTotal>0) barProg/barTotal.toFloat() else 0f
    LinearProgressIndicator(
      drawStopIndicator = {},
      progress = {progress},
      color = color,
      trackColor = MaterialTheme.colorScheme.outline,
      strokeCap = StrokeCap.Round,
      gapSize = 0.dp
      )
  }
}

/*
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewConPro(){
  VoraCoachTheme {
    Scaffold(
      modifier = Modifier
        .safeDrawingPadding()

    ) {
      ConsumptionProgress(
        barProg = 104,
        barTotal = 140,
        color = MaterialTheme.colorScheme.primary,
      )
    }
  }
}

 */

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewConProRow(){
  VoraCoachTheme {
    Scaffold(
      modifier = Modifier
        .safeDrawingPadding()

    ) {
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