package com.example.teacher_assistant

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.teacher_assistant.database.AppDatabase
import com.example.teacher_assistant.database.Lesson
import com.example.teacher_assistant.database.Student
import com.example.teacher_assistant.database.StudentLesson
import kotlinx.coroutines.launch

class AddStudentsToLessonFragment : Fragment() {
    private lateinit var students: List<Student>
    private lateinit var studentsLesson: List<StudentLesson>
    private val checkedStates = mutableMapOf<Int, Boolean>()
    private val args: AddStudentsToLessonFragmentArgs by navArgs()
    private lateinit var lesson: Lesson

    // list adapter
    private val adapter = object: BaseAdapter() {
        override fun getCount(): Int = students.size

        override fun getItem(index: Int): Any = students[index]

        override fun getItemId(index: Int): Long = index.toLong()

        override fun getView(index: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(
                R.layout.checkbox_list_item, viewGroup, false
            )
            val item = getItem(index) as Student

            view.findViewById<TextView>(R.id.item_text).text = buildString {
                append(item.firstName)
                append(" ")
                append(item.lastName)
            }

            val checkbox = view.findViewById<CheckBox>(R.id.item_checkbox)

            checkbox.isChecked = false

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                checkedStates[item.idStudent] = isChecked
            }

            for (studentLesson in studentsLesson) {
                if (studentLesson.idStudent == item.idStudent) {
                    checkbox.isChecked = true
                    checkedStates[item.idStudent] = true
                    break
                }
            }

            return view
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_students_to_lesson, container, false)

        val lessonId = args.lessonId
        lifecycleScope.launch {
            lesson = AppDatabase.getInstance(requireContext()).lessonDao().getById(lessonId)!!
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            studentsLesson = db.studentLessonDao().getAll()
            students = db.studentDao().getAll()

            val studentsListView = view.findViewById<ListView>(R.id.add_students_to_lesson_listview)
            studentsListView.adapter = adapter
        }

        view.findViewById<Button>(R.id.button_save_students_to_lesson).setOnClickListener {
            lifecycleScope.launch {
                val db = AppDatabase.getInstance(requireContext())

                // Clear existing records
                db.studentLessonDao().deleteAll()

                // Save checked students to the database
                for ((idStudent, isChecked) in checkedStates) {
                    if (isChecked) {
                        val studentLesson = StudentLesson(
                            idStudentLesson = 0,
                            idStudent = idStudent,
                            idLesson = lesson.idLesson
                        )
                        db.studentLessonDao().insert(studentLesson)
                    }
                }
                Navigation.findNavController(view).popBackStack()
                Toast.makeText(requireContext(), "Studenci zostali zapisani", Toast.LENGTH_SHORT).show()
            }
        }
    }

}