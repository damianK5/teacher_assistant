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
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle

class ScheduleFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_schedule, container, false)
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
                    R.id.action_row_add -> {
                        val builder = AlertDialog.Builder(requireContext())
                        val dialogLayout = layoutInflater.inflate(R.layout.dialog_table_row_add, null)

                        with(builder) {
                            setTitle("Podaj godziny")
                            setPositiveButton("Dodaj") { _, _ ->

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

        /*
        val tableLayout = view.findViewById<TableLayout>(R.id.tableLayout)

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val number = numberEditText.text.toString()
            val email = emailEditText.text.toString()

            val tableRow = LayoutInflater.from(requireContext()).inflate(R.layout.table_row, null) as TableRow
            tableRow.findViewById<TextView>(R.id.nameTextView).text = number
            tableRow.findViewById<TextView>(R.id.numberTextView).text = name
            tableRow.findViewById<TextView>(R.id.emailTextView).text = email

            val removeButton = tableRow.findViewById<TableRow>(R.id.removeButton)

            removeButton.setOnClickListener {
                tableLayout.removeView(tableRow)
            }

            tableLayout.addView(tableRow)

        }

         */
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ScheduleFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}