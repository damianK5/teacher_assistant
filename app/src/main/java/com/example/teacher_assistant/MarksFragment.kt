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
import androidx.navigation.fragment.navArgs
import com.example.teacher_assistant.database.AppDatabase
import com.example.teacher_assistant.database.Lesson
import com.example.teacher_assistant.database.Mark
import com.example.teacher_assistant.database.Student
import kotlinx.coroutines.launch

class MarksFragment : Fragment() {
    private lateinit var marks: List<Mark>
    private val args: MarksFragmentArgs by navArgs()
    private var studentId: Int = 0
    private var lessonId: Int = 0

    // list adapter
    private val adapter = object: BaseAdapter() {
        override fun getCount(): Int = marks.size

        override fun getItem(index: Int): Any = marks[index]

        override fun getItemId(index: Int): Long = index.toLong()

        override fun getView(index: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(
                R.layout.mark_list_item, viewGroup, false
            )
            val item = getItem(index) as Mark

            view.findViewById<TextView>(R.id.textView_value).text = item.value.toString()
            view.findViewById<TextView>(R.id.textView_type).text = item.type

            view.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                val dialogLayout = layoutInflater.inflate(R.layout.dialog_mark_details, null)

                dialogLayout.findViewById<TextView>(R.id.textView_mark).text = item.value.toString()
                dialogLayout.findViewById<TextView>(R.id.textView_type).text = item.type
                dialogLayout.findViewById<TextView>(R.id.textView_description).text = item.description

                with(builder) {
                    setTitle("Informacje o ocenie")
                    setPositiveButton("Ok", null)
                    setView(dialogLayout)
                    show()
                }
            }

            val buttonEdit = view.findViewById<Button>(R.id.btn_edit)
            val buttonDelete = view.findViewById<Button>(R.id.btn_delete)

            buttonEdit.setOnClickListener {
                addMark(true, item.idMark, item.value, item.type, item.description)
            }

            buttonDelete.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())

                with(builder) {
                    setTitle("Czy chcesz usunąć?")
                    setPositiveButton("Tak") { _, _ ->
                        lifecycleScope.launch {
                            val db = AppDatabase.getInstance(requireContext())
                            db.markDao().delete(item)
                            refreshList()
                        }
                        Toast.makeText(
                            requireContext(),
                            "Ocena została usunięta",
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // set the appbar
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.appbar_marks, menu)
            }

            // appbar buttons action
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.btn_add_mark -> {
                        addMark(false, null, null, null, null)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_marks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            studentId = args.studentId
            lessonId = args.lessonId

            marks = AppDatabase.getInstance(requireContext())
                .markDao()
                .getByStudentAndLessonId(idStudent = studentId, idLesson = lessonId)

            val marksListView = view.findViewById<ListView>(R.id.listview_marks)
            marksListView.adapter = adapter
        }
    }

    private fun refreshList() {
        lifecycleScope.launch {
            marks = AppDatabase.getInstance(requireContext())
                .markDao()
                .getByStudentAndLessonId(idStudent = studentId, idLesson = lessonId)
            adapter.notifyDataSetChanged()
        }
    }

    private fun addMark(editMode: Boolean, markId: Int?, markVal: Double?, markTyp: String?, markDesc: String?) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_mark_add, null)

        val editTextMarkValue = dialogLayout.findViewById<EditText>(R.id.editText_mark)
        val editTextMarkType = dialogLayout.findViewById<EditText>(R.id.editText_mark_type)
        val editTextMarkDescription = dialogLayout.findViewById<EditText>(R.id.editText_description)

        if (editMode) {
            editTextMarkValue.setText(markVal.toString())
            editTextMarkType.setText(markTyp)
            editTextMarkDescription.setText(markDesc)
        }

        with(builder) {
            setTitle("Podaj informacje o ocenie")
            setPositiveButton(if (editMode) "Edytuj" else "Dodaj") { _, _ ->

                val markValue = editTextMarkValue.text.toString()
                val markType = editTextMarkType.text.toString()
                val markDescription = editTextMarkDescription.text.toString()

                if (markValue.isNotBlank() && markType.isNotBlank() && markDescription.isNotBlank()) {
                    val mark = Mark(
                        idMark = if (editMode) markId!! else 0,
                        idStudent = studentId,
                        idLesson = lessonId,
                        value = markValue.toDouble(),
                        type = markType,
                        description = markDescription
                    )

                    lifecycleScope.launch {
                        AppDatabase.getInstance(requireContext())
                            .markDao()
                            .insert(mark)
                        Toast.makeText(
                            requireContext(),
                            "Ocena zapisana",
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