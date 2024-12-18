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
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation

class StudentsFragment : Fragment(R.layout.fragment_students) {
    private val studentList = mutableListOf<StudentData>()
    private val adapter = ItemElementAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    // list adapter
    inner class ItemElementAdapter: BaseAdapter() {
        override fun getCount(): Int = studentList.size

        override fun getItem(index: Int): Any = studentList[index]

        override fun getItemId(index: Int): Long = index.toLong()

        override fun getView(index: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val newConvertView = convertView ?: LayoutInflater.from(context).inflate(
                R.layout.student_list_item, viewGroup, false
            )
            val item = getItem(index) as StudentData
            newConvertView.findViewById<TextView>(R.id.listName).text = buildString {
                append(item.firstName)
                append(" ")
                append(item.lastName)
            }
            return newConvertView
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val studentsListView = view.findViewById<ListView>(R.id.students_listview)
        studentsListView.adapter = adapter

        studentsListView.setOnItemClickListener { _, _, i, _ ->
            Navigation.findNavController(view).navigate(R.id.navigateToStudentDetailedFragment)
        }

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
                                studentList.add(
                                    StudentData(
                                        editTextFirstName.text.toString(),
                                        editTextLastName.text.toString(),
                                        editTextStudentNumber.text.toString(),
                                    )
                                )
                                adapter.notifyDataSetChanged()
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


    companion object {

        @JvmStatic
        fun newInstance() =
            StudentsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}