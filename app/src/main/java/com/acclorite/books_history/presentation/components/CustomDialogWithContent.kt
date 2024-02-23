package com.acclorite.books_history.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.acclorite.books_history.R
import com.acclorite.books_history.ui.ElevationDefaults
import com.acclorite.books_history.ui.elevation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDialogWithContent(
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    drawableIcon: Painter? = null,
    imageVectorIcon: ImageVector? = null,
    backgroundTransparency: Float = 0.3f,
    title: String,
    description: String?,
    actionText: String?,
    isActionEnabled: Boolean?,
    onDismiss: () -> Unit,
    onAction: () -> Unit,
    withDivider: Boolean,
    customContent: @Composable (ColumnScope.() -> Unit) = {}
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        properties = properties,
    ) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(
            backgroundTransparency
        )
        Column(
            modifier = modifier
                .navigationBarsPadding()
                .statusBarsPadding()
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.elevation(ElevationDefaults.Dialog))
                .padding(top = 24.dp, bottom = 12.dp)
        ) {
            if (drawableIcon != null) {
                Icon(
                    painter = drawableIcon,
                    contentDescription = title,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (imageVectorIcon != null) {
                Icon(
                    imageVector = imageVectorIcon,
                    contentDescription = title,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = title,
                modifier = Modifier
                    .align(
                        if (drawableIcon != null || imageVectorIcon != null) Alignment.CenterHorizontally
                        else Alignment.Start
                    )
                    .padding(horizontal = 24.dp),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (description != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
            if (withDivider) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }

            customContent()

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 24.dp)
            ) {
                TextButton(onClick = { onDismiss() }) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (actionText != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(
                        onClick = { onAction() },
                        enabled = isActionEnabled == true
                    ) {
                        Text(
                            text = actionText,
                            style = MaterialTheme.typography.labelLarge,
                            color =
                            if (isActionEnabled == true) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(0.5f)
                        )
                    }
                }
            }
        }
    }
}