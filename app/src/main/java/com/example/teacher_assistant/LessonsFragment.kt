package com.example.teacher_assistant

import android.app.AlertDialog
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
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.teacher_assistant.database.Lesson
import com.example.teacher_assistant.database.AppDatabase
import com.example.teacher_assistant.database.Student
import kotlinx.coroutines.launch

class LessonsFragment : Fragment(R.layout.fragment_lessons) {
    private lateinit var lessons: List<Lesson>

    private val adapter = object : BaseAdapter() {
        override fun getCount(): Int = lessons.size

        override fun getItem(index: Int): Any = lessons[index]

        override fun getItemId(index: Int): Long = index.toLong()

        override fun getView(index: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(
                R.layout.student_list_item, viewGroup, false
            )
            val item = getItem(index) as Lesson

            view.findViewById<TextView>(R.id.listName).text = item.groupName

            return view
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            lessons = AppDatabase.getInstance(requireContext())
                .lessonDao()
                .getAll()

            val lessonsListView = view.findViewById<ListView>(R.id.lessons_listview)
            lessonsListView.adapter = adapter

            // set the appbar
            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.appbar_students, menu)
                }

                // appbar buttons action
                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.action_student_add -> {
                            addLesson(false, null)
                            true
                        }
                        else -> false
                    }
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        }
    }

    private fun refreshList() {
        lifecycleScope.launch {
            lessons = AppDatabase.getInstance(requireContext())
                .lessonDao()
                .getAll()
            adapter.notifyDataSetChanged()
        }
    }

    private fun addLesson(editMode: Boolean, lessonId: Int?) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_lesson_add, null)

        val editTextGroupName = dialogLayout.findViewById<EditText>(R.id.editTextGroupName)
        val editTextGroupNumber = dialogLayout.findViewById<EditText>(R.id.editTextGroupNumber)
        val editTextRoomNumber = dialogLayout.findViewById<EditText>(R.id.editTextRoomNumber)

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
                val selectedDay = parent.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), "Selected: $selectedDay", Toast.LENGTH_SHORT)
                    .show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        if (editMode) {
            //editTextGroupName.setText()
            //editTextGroupNumber.setText()
            //editTextRoomNumber.setText()
        }

        with(builder) {
            setTitle("Podaj informacje o zajęciach")
            setPositiveButton(if (editMode) "Edytuj" else "Dodaj") { _, _ ->

                val groupName = editTextGroupName.text.toString()
                val groupNumber = editTextGroupNumber.text.toString()
                val roomNumber = editTextRoomNumber.text.toString()

                if (groupName.isNotBlank() && groupNumber.isNotBlank() && roomNumber.isNotBlank()) {
                    val lesson = Lesson(
                        idLesson = if (editMode) lessonId!! else 0,
                        groupName = groupName,
                        groupNumber = groupNumber,
                        roomNumber = roomNumber
                    )

                    lifecycleScope.launch {
                        AppDatabase.getInstance(requireContext())
                            .lessonDao()
                            .insert(lesson)
                        Toast.makeText(
                            requireContext(),
                            "Zajęcia zapisane",
                            Toast.LENGTH_SHORT
                        ).show()
                        refreshList()
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

    companion object {

        @JvmStatic
        fun newInstance() =
            LessonsFragment().apply {
                arguments = Bundle().apply { }
            }
    }
}