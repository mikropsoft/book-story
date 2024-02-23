package com.acclorite.books_history.presentation.screens.book_info.components

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.acclorite.books_history.R
import com.acclorite.books_history.presentation.screens.book_info.data.BookInfoEvent
import com.acclorite.books_history.presentation.screens.book_info.data.BookInfoViewModel
import com.acclorite.books_history.ui.ElevationDefaults
import com.acclorite.books_history.ui.elevation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookInfoDetailsBottomSheet(
    viewModel: BookInfoViewModel
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val pattern = SimpleDateFormat("HH:mm dd MMM", Locale.getDefault())
    val lastOpened = pattern.format(Date(state.book.lastOpened ?: 0))

    val sizeBytes = state.book.file?.length() ?: 0

    val fileSizeKB = if (sizeBytes > 0) sizeBytes.toDouble() / 1024.0 else 0.0
    val fileSizeMB = if (sizeBytes > 0) fileSizeKB / 1024.0 else 0.0

    val fileSize =
        if (fileSizeMB >= 1.0) "%.2f MB".format(fileSizeMB)
        else if (fileSizeMB > 0.0) "%.2f KB".format(fileSizeKB)
        else ""


    ModalBottomSheet(
        onDismissRequest = {
            viewModel.onEvent(BookInfoEvent.OnShowHideDetailsBottomSheet)
        },
        windowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.elevation(ElevationDefaults.BottomSheet)
    ) {
        BookInfoDetailsBottomSheetItem(
            title = stringResource(id = R.string.file_name),
            description = state.book.filePath.substringAfterLast("/").trim()
        ) {
            viewModel.onEvent(BookInfoEvent.OnCopyToClipboard(
                context,
                state.book.filePath.substringAfterLast("/").trim(),
                success = {
                    Toast.makeText(
                        context,
                        context.getString(R.string.copied),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            ))
        }
        BookInfoDetailsBottomSheetItem(
            title = stringResource(id = R.string.file_path),
            description = state.book.filePath.trim()
        ) {
            viewModel.onEvent(BookInfoEvent.OnCopyToClipboard(
                context,
                state.book.filePath.trim(),
                success = {
                    Toast.makeText(
                        context,
                        context.getString(R.string.copied),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            ))
        }
        BookInfoDetailsBottomSheetItem(
            title = stringResource(id = R.string.file_last_opened),
            description = if (state.book.lastOpened != null) lastOpened
            else stringResource(id = R.string.never)
        ) {
            if (state.book.lastOpened != null) {
                viewModel.onEvent(BookInfoEvent.OnCopyToClipboard(
                    context,
                    lastOpened,
                    success = {
                        Toast.makeText(
                            context,
                            context.getString(R.string.copied),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ))
            }
        }
        BookInfoDetailsBottomSheetItem(
            title = stringResource(id = R.string.file_size),
            description = fileSize.ifBlank { stringResource(id = R.string.unknown) }
        ) {
            if (fileSize.isNotBlank()) {
                viewModel.onEvent(BookInfoEvent.OnCopyToClipboard(
                    context,
                    fileSize,
                    success = {
                        Toast.makeText(
                            context,
                            context.getString(R.string.copied),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ))
            }
        }


        Spacer(modifier = Modifier.height(48.dp))
    }
}