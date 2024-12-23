package com.example.teacher_assistant

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.teacher_assistant.database.AppDatabase
import com.example.teacher_assistant.database.CustomTableRow
import com.example.teacher_assistant.database.Lesson
import kotlinx.coroutines.launch
import java.util.Calendar

class ScheduleFragment : Fragment() {
    private lateinit var customTableRows: List<CustomTableRow>
    private lateinit var lessons: List<Lesson>
    private lateinit var tableLayout: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        tableLayout = view.findViewById(R.id.tableLayout)
        lifecycleScope.launch {
            lessons = AppDatabase.getInstance(requireContext())
                .lessonDao()
                .getAll()
        }
        createTable()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.appbar_schedule, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle menu item clicks
                return when (menuItem.itemId) {
                    R.id.action_table_clear -> {
                        lifecycleScope.launch {
                            val builder = AlertDialog.Builder(requireContext())
                            with(builder) {
                                setTitle("Czy chcesz usunąć cały plan?")
                                setPositiveButton("Tak") { _, _ ->
                                    lifecycleScope.launch {
                                        AppDatabase.getInstance(requireContext())
                                            .customTableRowDao()
                                            .deleteAll()
                                        clearTable()
                                    }
                                    Toast.makeText(
                                        requireContext(),
                                        "Plan zajęć usunięty",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                setNegativeButton("Nie", null)
                                show()
                            }

                        }
                        true
                    }

                    R.id.action_row_add -> {
                        val builder = AlertDialog.Builder(requireContext())
                        val dialogLayout = layoutInflater.inflate(R.layout.dialog_table_row_add, null)

                        val editTextLessonNumber = dialogLayout.findViewById<EditText>(R.id.editTextLessonNumber)
                        val textViewStartHour = dialogLayout.findViewById<TextView>(R.id.start_hour)
                        textViewStartHour.setOnClickListener {
                            val c = Calendar.getInstance()
                            val hour = c.get(Calendar.HOUR_OF_DAY)
                            val minute = c.get(Calendar.MINUTE)

                            val timePickerDialog = TimePickerDialog(
                                requireContext(),
                                { _, hourOfDay, minuteOfDay ->
                                    textViewStartHour.text = String.format("%02d:%02d", hourOfDay, minuteOfDay)
                                },
                                hour,
                                minute,
                                true
                            )

                            timePickerDialog.show()
                        }
                        val textViewEndHour = dialogLayout.findViewById<TextView>(R.id.end_hour)
                        textViewEndHour.setOnClickListener {
                            val c = Calendar.getInstance()
                            val hour = c.get(Calendar.HOUR_OF_DAY)
                            val minute = c.get(Calendar.MINUTE)

                            val timePickerDialog = TimePickerDialog(
                                requireContext(),
                                { _, hourOfDay, minuteOfDay ->
                                    textViewEndHour.text =
                                        String.format("%02d:%02d", hourOfDay, minuteOfDay)
                                },
                                hour,
                                minute,
                                true
                            )

                            timePickerDialog.show()
                        }

                        with(builder) {
                            setTitle("Podaj godziny")
                            setPositiveButton("Dodaj") { _, _ ->
                                if (editTextLessonNumber.toString().isNotBlank()) {
                                    val lessonNumber = editTextLessonNumber.text.toString().toInt()
                                    val startHour = textViewStartHour.text.toString()
                                    val endHour = textViewEndHour.text.toString()

                                    val customTableRow = CustomTableRow(
                                        idCustomTableRow = 0,
                                        lessonNumber = lessonNumber,
                                        startHour = startHour,
                                        endHour = endHour
                                    )

                                    lifecycleScope.launch {
                                        AppDatabase.getInstance(requireContext())
                                            .customTableRowDao()
                                            .insert(customTableRow)
                                        Toast.makeText(
                                            requireContext(),
                                            "Student zapisany",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        // clear the table and create new based on database
                                        createTable()
                                    }

                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Wypełnij wszystkie pola",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                //val newRow = layoutInflater.inflate(R.layout.table_row, tableLayout, false) as TableRow
                                //newRow.findViewById<TextView>(R.id.hourTextView).text = "$lessonNumber. $startHour - $endHour"
                                //tableLayout.addView(newRow)
                            }
                            setNegativeButton("Anuluj") { _, _ -> }
                            setView(dialogLayout)
                            show()
                        }
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun createTable() {
        lifecycleScope.launch {
            clearTable()

            customTableRows = AppDatabase.getInstance(requireContext())
                .customTableRowDao()
                .getAll()

            for (customTableRow in customTableRows) {
                val newRow = layoutInflater.inflate(R.layout.table_row, tableLayout, false) as TableRow

                val hourTextView = newRow.findViewById<TextView>(R.id.hourTextView)
                hourTextView.text =
                    "${customTableRow.lessonNumber}.\n ${customTableRow.startHour} - ${customTableRow.endHour}"
                hourTextView.setOnClickListener {
                    Toast.makeText(
                        requireContext(),
                        "${customTableRow.lessonNumber}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                tableLayout.addView(newRow)
            }
        }
    }

    private fun clearTable() {
        val childCount = tableLayout.childCount
        if (childCount > 1) { // Assume the first row is the header
            tableLayout.removeViews(1, childCount - 1)
        }
    }
}