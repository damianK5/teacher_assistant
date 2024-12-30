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
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.teacher_assistant.database.Student
import com.example.teacher_assistant.database.AppDatabase
import kotlinx.coroutines.launch

class StudentsFragment : Fragment(R.layout.fragment_students) {
    private lateinit var students: List<Student>

    // list adapter
    private val adapter = object: BaseAdapter() {
        override fun getCount(): Int = students.size

        override fun getItem(index: Int): Any = students[index]

        override fun getItemId(index: Int): Long = index.toLong()

        override fun getView(index: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(
                R.layout.student_list_item, viewGroup, false
            )
            val item = getItem(index) as Student

            view.findViewById<TextView>(R.id.listName).text = buildString {
                append(item.firstName)
                append(" ")
                append(item.lastName)
            }

            view.setOnClickListener {
                val action = StudentsFragmentDirections.navigateToStudentDetailedFragment(item.idStudent)
                Navigation.findNavController(view).navigate(action)
            }

            val buttonEdit = view.findViewById<Button>(R.id.btn_edit)
            val buttonDelete = view.findViewById<Button>(R.id.btn_delete)

            buttonEdit.setOnClickListener {
                addStudent(true, item.idStudent, item.firstName, item.lastName, item.studentNumber)
            }

            buttonDelete.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())

                with(builder) {
                    setTitle("Czy chcesz usunąć?")
                    setPositiveButton("Tak") { _, _ ->
                        lifecycleScope.launch {
                            val db = AppDatabase.getInstance(requireContext())
                            db.studentDao().delete(item)
                            refreshList()
                        }
                        Toast.makeText(
                            requireContext(),
                            "Student usunięty",
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
            students = AppDatabase.getInstance(requireContext())
                .studentDao()
                .getAll()

            val studentsListView = view.findViewById<ListView>(R.id.students_listview)
            studentsListView.adapter = adapter

            // set the appbar
            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.appbar_students, menu)
                }

                // appbar buttons action
                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.action_student_add -> {
                            addStudent(false, null, null, null, null)
                            true
                        }
                        else -> false
                    }
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        }
    }

    fun refreshList() {
        lifecycleScope.launch {
            students = AppDatabase.getInstance(requireContext())
                .studentDao()
                .getAll()
            adapter.notifyDataSetChanged()
        }
    }

    fun addStudent(editMode: Boolean, studentId: Int?, fName: String?, lName: String?, num: String?) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_student_add, null)

        val editTextFirstName = dialogLayout.findViewById<EditText>(R.id.editTextFirstName)
        val editTextLastName = dialogLayout.findViewById<EditText>(R.id.editTextLastName)
        val editTextStudentNumber = dialogLayout.findViewById<EditText>(R.id.editTextStudentNumber)

        if (editMode) {
            editTextFirstName.setText(fName)
            editTextLastName.setText(lName)
            editTextStudentNumber.setText(num)
        }

        with(builder) {
            setTitle("Podaj informacje o studencie")
            setPositiveButton(if (editMode) "Edytuj" else "Dodaj") { _, _ ->

                val firstName = editTextFirstName.text.toString()
                val lastName = editTextLastName.text.toString()
                val studentNumber = editTextStudentNumber.text.toString()

                if (firstName.isNotBlank() && lastName.isNotBlank() && studentNumber.isNotBlank()) {
                    val student = Student(
                        idStudent = if (editMode) studentId!! else 0,
                        firstName = firstName,
                        lastName = lastName,
                        studentNumber = studentNumber
                    )

                    lifecycleScope.launch {
                        AppDatabase.getInstance(requireContext())
                            .studentDao()
                            .insert(student)
                        Toast.makeText(
                            requireContext(),
                            "Student zapisany",
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