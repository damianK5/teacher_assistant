package com.example.teacher_assistant

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.teacher_assistant.database.AppDatabase
import kotlinx.coroutines.launch

class DataFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_data, container, false)

        view.findViewById<Button>(R.id.button_wipe_data).setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())

            with(builder) {
                setTitle("Czy chcesz usunąć wszystkie dane?")
                setPositiveButton("Tak") { _, _ ->
                    lifecycleScope.launch {
                        val db = AppDatabase.getInstance(requireContext())

                        db.lessonInScheduleDao().deleteAll()
                        db.customTableRowDao().deleteAll()
                        db.studentLessonDao().deleteAll()
                        db.markDao().deleteAll()
                        db.lessonDao().deleteAll()
                        db.studentDao().deleteAll()

                        db.lessonInScheduleDao().resetId()
                        db.customTableRowDao().resetId()
                        db.studentLessonDao().resetId()
                        db.markDao().resetId()
                        db.lessonDao().resetId()
                        db.studentDao().resetId()
                    }
                    Toast.makeText(
                        requireContext(),
                        "Dane usunięte",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                setNegativeButton("Nie", null)
                show()
            }
        }

        return view
    }
}