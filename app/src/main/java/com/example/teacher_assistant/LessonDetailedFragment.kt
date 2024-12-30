package com.example.teacher_assistant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.teacher_assistant.database.AppDatabase
import com.example.teacher_assistant.database.Lesson
import com.example.teacher_assistant.database.Student
import com.example.teacher_assistant.database.StudentLesson
import kotlinx.coroutines.launch

class LessonDetailedFragment : Fragment() {

    private val args: LessonDetailedFragmentArgs by navArgs()
    private lateinit var lesson: Lesson
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
                val student = AppDatabase
                    .getInstance(requireContext())
                    .studentDao()
                    .getById(item.idStudent)

                view.findViewById<TextView>(R.id.item_text).text = buildString {
                    append(student!!.firstName)
                    append(" ")
                    append(student.lastName)
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
        return inflater.inflate(R.layout.fragment_lesson_detailed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val lessonId = args.lessonId
            lesson = AppDatabase
                .getInstance(requireContext())
                .lessonDao()
                .getById(lessonId)!!

            view.findViewById<TextView>(R.id.group_name).text = lesson.groupName
            view.findViewById<TextView>(R.id.group_number).text = lesson.groupNumber
            view.findViewById<TextView>(R.id.room_number).text = lesson.roomNumber

            studentsLesson = AppDatabase
                .getInstance(requireContext())
                .studentLessonDao()
                .getByLessonId(lesson.idLesson)

            val studentsListView = view.findViewById<ListView>(R.id.listview_students_in_lesson)
            studentsListView.adapter = adapter
        }

        // set the appbar
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.appbar_lesson_details, menu)
            }

            // appbar buttons action
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_edit_students -> {
                        val action = LessonDetailedFragmentDirections.navigateToAddStudentsToLessonFragment(lesson.idLesson)
                        Navigation.findNavController(view).navigate(action)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}