package com.example.teacher_assistant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.teacher_assistant.database.AppDatabase
import com.example.teacher_assistant.database.Lesson
import com.example.teacher_assistant.database.Student
import com.example.teacher_assistant.database.StudentLesson
import kotlinx.coroutines.launch

class StudentDetailedFragment : Fragment() {

    private val args: StudentDetailedFragmentArgs by navArgs()
    private lateinit var student: Student
    private lateinit var studentsLesson : List<StudentLesson>

    private val adapter = object : BaseAdapter() {
        override fun getCount(): Int = studentsLesson.size

        override fun getItem(index: Int): Any = studentsLesson[index]

        override fun getItemId(index: Int): Long = index.toLong()

        override fun getView(index: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(
                R.layout.simple_text_item, viewGroup, false
            )
            val item = getItem(index) as StudentLesson

            lifecycleScope.launch {
                val lesson = AppDatabase
                    .getInstance(requireContext())
                    .lessonDao()
                    .getById(item.idLesson)

                view.findViewById<TextView>(R.id.item_text).text = buildString {
                    append(lesson!!.groupName)
                    append(" ")
                    append(lesson.groupNumber)
                }
            }

            view.setOnClickListener {
                val action = StudentDetailedFragmentDirections.actionStudentDetailedFragmentToMarksFragment(item.idStudent, item.idLesson)
                Navigation.findNavController(view).navigate(action)
            }

            return view
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_detailed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val studentId = args.studentId

            student = AppDatabase
                .getInstance(requireContext())
                .studentDao()
                .getById(studentId)!!

            view.findViewById<TextView>(R.id.firstName).text = student.firstName
            view.findViewById<TextView>(R.id.lastName).text = student.lastName
            view.findViewById<TextView>(R.id.studentNumber).text = student.studentNumber

            studentsLesson = AppDatabase
                .getInstance(requireContext())
                .studentLessonDao()
                .getByStudentId(studentId)

            val studentsListView = view.findViewById<ListView>(R.id.listview_lessons_for_student)
            studentsListView.adapter = adapter
        }
    }
}