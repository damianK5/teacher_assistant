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
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
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

            view.setOnClickListener {
                val action = LessonsFragmentDirections.navigateToLessonDetailedFragment(item.idLesson)
                Navigation.findNavController(view).navigate(action)
            }

            val buttonEdit = view.findViewById<Button>(R.id.btn_edit)
            val buttonDelete = view.findViewById<Button>(R.id.btn_delete)

            buttonEdit.setOnClickListener {
                addLesson(true, item.idLesson, item.groupName, item.groupNumber, item.roomNumber)
            }

            buttonDelete.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())

                with(builder) {
                    setTitle("Czy chcesz usunąć?")
                    setPositiveButton("Tak") { _, _ ->
                        lifecycleScope.launch {
                            val db = AppDatabase.getInstance(requireContext())
                            db.lessonDao().delete(item)
                            refreshList()
                        }
                        Toast.makeText(
                            requireContext(),
                            "Zajęcia usunięte",
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
                            addLesson(false, null, null, null, null)
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

    private fun addLesson(editMode: Boolean, lessonId: Int?, groupNam: String?, groupNum: String?, roomNum: String?) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_lesson_add, null)

        val editTextGroupName = dialogLayout.findViewById<EditText>(R.id.editTextGroupName)
        val editTextGroupNumber = dialogLayout.findViewById<EditText>(R.id.editTextGroupNumber)
        val editTextRoomNumber = dialogLayout.findViewById<EditText>(R.id.editTextRoomNumber)

        if (editMode) {
            editTextGroupName.setText(groupNam)
            editTextGroupNumber.setText(groupNum)
            editTextRoomNumber.setText(roomNum)
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
}