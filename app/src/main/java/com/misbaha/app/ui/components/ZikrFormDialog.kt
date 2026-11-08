package com.misbaha.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.misbaha.app.R
import com.misbaha.app.domain.validation.ZikrTextValidation
import com.misbaha.app.domain.validation.ZikrTextValidator
import com.misbaha.app.ui.theme.Sizes

@Composable
fun ZikrFormDialog(
    title: String,
    initialText: String,
    existingTexts: List<String>,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by rememberSaveable(initialText) { mutableStateOf(initialText) }
    var errorResource by rememberSaveable { mutableStateOf<Int?>(null) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    fun submit() {
        when (val validation = ZikrTextValidator.validate(text, existingTexts)) {
            ZikrTextValidation.Empty -> errorResource = R.string.zikr_error_empty
            ZikrTextValidation.Duplicate -> errorResource = R.string.zikr_error_duplicate
            is ZikrTextValidation.Valid -> {
                errorResource = null
                onSave(validation.text)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { value ->
                    text = value
                    errorResource = null
                },
                label = { Text(stringResource(R.string.zikr_text_label)) },
                minLines = Sizes.DialogTextMinLines,
                maxLines = Sizes.DialogTextMaxLines,
                isError = errorResource != null,
                supportingText = {
                    val error = errorResource
                    if (error != null) {
                        Text(stringResource(error))
                    }
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { submit() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
        },
        confirmButton = {
            TextButton(onClick = { submit() }) {
                Text(
                    text = stringResource(R.string.save),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface
    )
}
