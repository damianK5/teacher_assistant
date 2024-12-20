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
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.wydarzenieuczestnik.AppDatabase
import kotlinx.coroutines.launch

class StudentsFragment : Fragment(R.layout.fragment_students) {
    private lateinit var students: List<Student>
    private val adapter = ItemElementAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    // list adapter
    inner class ItemElementAdapter: BaseAdapter() {
        override fun getCount(): Int = students.size

        override fun getItem(index: Int): Any = students[index]

        override fun getItemId(index: Int): Long = index.toLong()

        override fun getView(index: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val newConvertView = convertView ?: LayoutInflater.from(context).inflate(
                R.layout.student_list_item, viewGroup, false
            )
            val item = getItem(index) as Student

            newConvertView.findViewById<TextView>(R.id.listName).text = buildString {
                append(item.firstName)
                append(" ")
                append(item.lastName)
            }

            newConvertView.setOnClickListener {
                Navigation.findNavController(newConvertView).navigate(R.id.navigateToStudentDetailedFragment)
            }

            return newConvertView
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

            // Add a MenuProvider for menu handling
            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    // Inflate your menu resource
                    menuInflater.inflate(R.menu.appbar_students, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    // Handle menu item clicks
                    return when (menuItem.itemId) {
                        R.id.action_student_add -> {
                            val builder = AlertDialog.Builder(requireContext())
                            val dialogLayout = layoutInflater.inflate(R.layout.dialog_student_add, null)

                            val editTextFirstName = dialogLayout.findViewById<EditText>(R.id.editTextFirstName)
                            val editTextLastName = dialogLayout.findViewById<EditText>(R.id.editTextLastName)
                            val editTextStudentNumber = dialogLayout.findViewById<EditText>(R.id.editTextStudentNumber)

                            with(builder) {
                                setTitle("Podaj informacje o studencie")
                                setPositiveButton("Dodaj") { _, _ ->

                                    val firstName = editTextFirstName.text.toString()
                                    val lastName = editTextLastName.text.toString()
                                    val studentNumber = editTextStudentNumber.text.toString()

                                    if (firstName.isNotBlank() && lastName.isNotBlank() && studentNumber.isNotBlank()) {
                                        val student = Student(
                                            idStudent = 0,
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
                                            students = AppDatabase.getInstance(requireContext())
                                                .studentDao()
                                                .getAll()
                                            adapter.notifyDataSetChanged()
                                        }
                                    } else {
                                        Toast.makeText(requireContext(), "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
                                    }


                                }
                                setNegativeButton("Anuluj", null)
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


    }


    companion object {

        @JvmStatic
        fun newInstance() =
            StudentsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}