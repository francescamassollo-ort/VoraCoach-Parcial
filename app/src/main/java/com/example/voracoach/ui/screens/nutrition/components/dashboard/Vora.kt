package com.example.voracoach.ui.screens.nutrition.components.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.ui.components.PrimaryButton
import com.example.voracoach.ui.components.SectionHeader
import com.example.voracoach.ui.components.VoraOrb
import com.example.voracoach.ui.theme.VoraCoachTheme

@Composable
fun Vora(){
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(20.dp))
      .background(MaterialTheme.colorScheme.background)
  ) {
    Column(
      modifier = Modifier
        .background(MaterialTheme.colorScheme.primary)
        .padding(18.dp)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row {
        VoraOrb(
          diameter = 36.dp,
          modifier = Modifier.padding(end = 5.dp)
        )
        Column() {
          Spacer(
            modifier = Modifier.padding(top = 7.dp)
          )
          SectionHeader(
            title = "Tell Vora what you ate",
            titleColor = MaterialTheme.colorScheme.secondary
          )
        }

      }
      Text(
        "Vora will automatically estimate the proportions, ingredients, and log the nutrition details",
        color = MaterialTheme.colorScheme.background
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        PrimaryButton(
          onClick = {},
          leadingIcon = Icons.Outlined.Mic,
          text = "Voice",
        )

        SecVoraButton(
          text = "Photo",
          leadingIcon = Icons.Outlined.CameraAlt,
          textColor = MaterialTheme.colorScheme.onPrimary,
          backColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SecVoraButton(
          text = "Search",
          leadingIcon = Icons.Outlined.Search,
          textColor = MaterialTheme.colorScheme.onPrimary,
          backColor = MaterialTheme.colorScheme.onSurfaceVariant
        )

      }
    }
  }
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewVora(
){
  VoraCoachTheme {
    Scaffold(
      modifier = Modifier
        .safeDrawingPadding()
        .padding(12.dp)
    ){
      Vora()
    }
  }
}