package com.example.voracoach.ui.screens.nutrition.components.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.ui.theme.VoraCoachTheme


@Composable
fun SecVoraButton(
  text: String,
  //onClick: () -> Unit = {},
  //modifier: Modifier = Modifier,
  leadingIcon: ImageVector? = null,
  textColor: Color, //MaterialTheme.colorScheme.primary,
  backColor: Color //MaterialTheme.colorScheme.onPrimary,
) {

  Button(
    onClick = {},
    //modifier = modifier,
    enabled = true,
    shape = CircleShape,
    colors = ButtonDefaults.buttonColors(
      containerColor = backColor,
      contentColor = textColor
    ),
  ) {
    Row(
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      Column(

      ) {
        Spacer(modifier = Modifier.height(3.dp))
        Icon(
          imageVector = Icons.Outlined.CameraAlt,
          contentDescription = null,
          modifier = Modifier.size(16.dp)

        )
      }

      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text,
        style = MaterialTheme.typography.labelMedium
      )
    }
  }
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = false)
@Composable
fun PreviewSecVoraButton(){
  VoraCoachTheme {
    Scaffold {
      SecVoraButton(
        text = "Photo",
        leadingIcon = Icons.Outlined.CameraAlt,
        textColor = MaterialTheme.colorScheme.onPrimary,
        backColor = MaterialTheme.colorScheme.primary

      )
    }
  }

}