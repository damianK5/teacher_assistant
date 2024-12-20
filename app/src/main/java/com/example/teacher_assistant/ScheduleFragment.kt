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
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import java.util.Calendar

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

        val tableLayout = view.findViewById<TableLayout>(R.id.tableLayout)

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

                        val startHour = dialogLayout.findViewById<TextView>(R.id.start_hour)
                        startHour.setOnClickListener {
                            val c = Calendar.getInstance()
                            val hour = c.get(Calendar.HOUR_OF_DAY)
                            val minute = c.get(Calendar.MINUTE)

                            val timePickerDialog = TimePickerDialog(
                                requireContext(),
                                { _, hourOfDay, minuteOfDay ->
                                    startHour.text = String.format("%02d:%02d", hourOfDay, minuteOfDay)
                                },
                                hour,
                                minute,
                                true
                            )

                            timePickerDialog.show()
                        }
                        val endHour = dialogLayout.findViewById<TextView>(R.id.end_hour)
                        endHour.setOnClickListener {
                            val c = Calendar.getInstance()
                            val hour = c.get(Calendar.HOUR_OF_DAY)
                            val minute = c.get(Calendar.MINUTE)

                            val timePickerDialog = TimePickerDialog(
                                requireContext(),
                                { _, hourOfDay, minuteOfDay ->
                                    endHour.text = String.format("%02d:%02d", hourOfDay, minuteOfDay)
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
                                val newRow = layoutInflater.inflate(R.layout.table_row, tableLayout, false) as TableRow

                                newRow.findViewById<TextView>(R.id.hourTextView).text = "${startHour.text} - ${endHour.text}"

                                tableLayout.addView(newRow)
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
            ScheduleFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}