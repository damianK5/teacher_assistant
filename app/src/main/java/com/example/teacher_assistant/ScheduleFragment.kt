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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
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
import com.example.teacher_assistant.database.LessonInSchedule
import com.example.teacher_assistant.database.Student
import kotlinx.coroutines.launch
import java.util.Calendar

class ScheduleFragment : Fragment() {
    private lateinit var customTableRows: List<CustomTableRow>
    private lateinit var lessonsInSchedule: List<LessonInSchedule>
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
                                        val db = AppDatabase.getInstance(requireContext())
                                        db.customTableRowDao().deleteAll()
                                        db.lessonInScheduleDao().deleteAll()
                                        tableLayout.removeAllViews()
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
                                            "Godzina zajęć zapisana",
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

                    R.id.action_lesson_add -> {
                        addLessonToSchedule()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun createTable() {
        lifecycleScope.launch {
            tableLayout.removeAllViews()

            val db = AppDatabase.getInstance(requireContext())
            customTableRows = db
                .customTableRowDao()
                .getAll()
            lessonsInSchedule = db
                .lessonInScheduleDao()
                .getAll()

            for (customTableRow in customTableRows) {
                val newRow = layoutInflater.inflate(R.layout.table_row, tableLayout, false) as TableRow

                val hourTextView = newRow.findViewById<TextView>(R.id.hourTextView)
                hourTextView.text =
                    "${customTableRow.lessonNumber}.\n ${customTableRow.startHour} - ${customTableRow.endHour}"

                for (lessonInSchedule in lessonsInSchedule) {
                    if (lessonInSchedule.rowIndex == customTableRow.lessonNumber) {
                        //getting the id for current cell textView
                        var textViewId = -1
                        when (lessonInSchedule.columnIndex) {
                            0 -> textViewId = R.id.ponTextView
                            1 -> textViewId = R.id.wtTextView
                            2 -> textViewId = R.id.srTextView
                            3 -> textViewId = R.id.czwTextView
                            4 -> textViewId = R.id.ptTextView
                        }
                        val textView = newRow.findViewById<TextView>(textViewId)

                        // getting the lesson for current table cell
                        val currentLesson = AppDatabase
                            .getInstance(requireContext())
                            .lessonDao()
                            .getById(lessonInSchedule.idLesson)

                        // setting the text in table cell
                        if (currentLesson!!.groupName.length > 3)
                            textView.text = "${currentLesson.groupName.substring(0, 3).uppercase()} \nGr. ${currentLesson.groupNumber}"
                        else
                            textView.text = "${currentLesson.groupName.uppercase()} \nGr. ${currentLesson.groupNumber}"

                        // setting the click function for table cell
                        textView.setOnClickListener {
                            val builder = AlertDialog.Builder(requireContext())
                            val dialogLayout = layoutInflater.inflate(R.layout.dialog_lesson_details, null)

                            dialogLayout.findViewById<TextView>(R.id.lesson_details_name).text = currentLesson.groupName
                            dialogLayout.findViewById<TextView>(R.id.lesson_details_group).text = currentLesson.groupNumber
                            dialogLayout.findViewById<TextView>(R.id.lesson_details_room).text = currentLesson.roomNumber

                            with(builder) {
                                setTitle("Informacje o zajęciach")
                                setPositiveButton("Ok", null)
                                setView(dialogLayout)
                                show()
                            }
                        }
                    }
                }

                tableLayout.addView(newRow)
            }
        }
    }

    private fun addLessonToSchedule() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_table_cell_add, null)

        var selectedDay: String = ""
        var selectedLessonNumber: Int = -1
        var selectedLessonId: Int = -1
        var isEveryValueSelected = true

        // Dropdown list of week days
        val daysSpinner = dialogLayout.findViewById<Spinner>(R.id.days_spinner)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.days_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            daysSpinner.adapter = adapter
        }
        daysSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedDay = parent.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), "Selected: $selectedDay", Toast.LENGTH_SHORT)
                    .show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                isEveryValueSelected = false
            }
        }

        // Dropdown list of lesson numbers
        val lessonNumbers = customTableRows.map { it.lessonNumber }
        val lessonNumbersSpinner = dialogLayout.findViewById<Spinner>(R.id.lesson_numbers_spinner)
        val lessonNumbersSpinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            lessonNumbers
        )
        lessonNumbersSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        lessonNumbersSpinner.adapter = lessonNumbersSpinnerAdapter
        lessonNumbersSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedLessonNumber = lessonNumbers[position]
                Toast.makeText(requireContext(), "Selected: $selectedLessonNumber", Toast.LENGTH_SHORT)
                    .show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                isEveryValueSelected = false
            }
        }

        // Dropdown list of lesson numbers
        val lessonNames = lessons.map { "${it.groupName}, Gr. ${it.groupNumber}" }
        val lessonNamesSpinner = dialogLayout.findViewById<Spinner>(R.id.lessons_spinner)
        val lessonNamesSpinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            lessonNames
        )
        lessonNamesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        lessonNamesSpinner.adapter = lessonNamesSpinnerAdapter
        lessonNamesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedLessonId = lessons[position].idLesson
                Toast.makeText(requireContext(), "Selected: $selectedLessonId", Toast.LENGTH_SHORT)
                    .show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                isEveryValueSelected = false
            }
        }


        with(builder) {
            setTitle("Przypisz zajęcia do planu")
            setPositiveButton("Dodaj") { _, _ ->

                val dayOfWeek = when (selectedDay) {
                    "Poniedziałek" -> 0
                    "Wtorek" -> 1
                    "Środa" -> 2
                    "Czwartek" -> 3
                    "Piątek" -> 4

                    else -> -1
                }

                if (isEveryValueSelected) {
                    val lessonInSchedule = LessonInSchedule(
                        idLessonInSchedule = 0,
                        idLesson = selectedLessonId,
                        columnIndex = dayOfWeek,
                        rowIndex = selectedLessonNumber
                    )

                    lifecycleScope.launch {
                        AppDatabase.getInstance(requireContext())
                            .lessonInScheduleDao()
                            .insert(lessonInSchedule)
                        Toast.makeText(
                            requireContext(),
                            "zajęcia dodane do planu",
                            Toast.LENGTH_SHORT
                        ).show()
                        createTable()
                    }

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Wypełnij wszystkie pola",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            setNegativeButton("Anuluj", null)
            setView(dialogLayout)
            show()
        }
    }
}