package com.example.myhipmi.ui.screen.rapat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhipmi.ui.theme.GreenPrimary
import com.example.myhipmi.ui.theme.White

@Composable
fun ModernTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    placeholder: String = "",
    singleLine: Boolean = true,
    minLines: Int = 1,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
    isRequired: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF374151),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (isRequired) {
                Text(
                    text = " *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        if (onClick != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onClick()
                    }
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = { },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = placeholder.ifEmpty { "Masukkan $label" },
                            color = Color(0xFFD1D5DB)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = singleLine,
                    minLines = minLines,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color(0xFF1F2937),
                        unfocusedTextColor = Color(0xFF1F2937),
                        cursorColor = GreenPrimary,
                        disabledTextColor = Color(0xFF1F2937),
                        disabledBorderColor = Color(0xFFE5E7EB),
                        disabledContainerColor = Color.White
                    )
                )
            }
        } else {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                readOnly = readOnly,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = placeholder.ifEmpty { "Masukkan $label" },
                        color = Color(0xFFD1D5DB)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = singleLine,
                minLines = minLines,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = Color(0xFFE5E7EB),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color(0xFF1F2937),
                    unfocusedTextColor = Color(0xFF1F2937),
                    cursorColor = GreenPrimary
                )
            )
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        },
        containerColor = White,
        shape = RoundedCornerShape(24.dp)
    )
}
