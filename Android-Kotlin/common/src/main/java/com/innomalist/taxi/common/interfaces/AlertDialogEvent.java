package com.chatanoga.cab.common.interfaces;

import com.chatanoga.cab.common.utils.AlertDialogBuilder;

public interface AlertDialogEvent {
    void onAnswerDialog(AlertDialogBuilder.DialogResult result);
}