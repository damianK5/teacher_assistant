package com.example.teacher_assistant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.teacher_assistant.database.AppDatabase
import com.example.teacher_assistant.database.Lesson
import com.example.teacher_assistant.database.Student
import kotlinx.coroutines.launch

class StudentDetailedFragment : Fragment() {

    private val args: StudentDetailedFragmentArgs by navArgs()
    private lateinit var student: Student

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_student_detailed, container, false)

        val studentId = args.studentId
        lifecycleScope.launch {
            student = AppDatabase
                .getInstance(requireContext())
                .studentDao()
                .getById(studentId)!!

            view.findViewById<TextView>(R.id.firstName).text = student.firstName
            view.findViewById<TextView>(R.id.lastName).text = student.lastName
            view.findViewById<TextView>(R.id.studentNumber).text = student.studentNumber
        }

        return view
    }
}