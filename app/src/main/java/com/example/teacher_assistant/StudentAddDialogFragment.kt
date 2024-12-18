package com.example.teacher_assistant

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class StudentAddDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Custom Dialog")
            .setMessage("This is a custom dialog.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
    }
}