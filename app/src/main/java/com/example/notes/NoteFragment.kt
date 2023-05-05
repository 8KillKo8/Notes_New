package com.example.notes

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.notes.database.NoteSaveViewModel
import com.example.notes.database.Notes
import com.example.notes.databinding.FragmentNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class NoteFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    private lateinit var notesSaveViewModel: NoteSaveViewModel
    private var fbind: FragmentNoteBinding? = null
    private var note: Notes? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentNoteBinding.bind(view)
        fbind = binding
        notesSaveViewModel = ViewModelProvider(this).get(NoteSaveViewModel::class.java)

        arguments?.let {
            note = NoteFragmentArgs.fromBundle(it).updateNote
            binding.titleEdittext.setText(note?.title)
            binding.descEditText.setText(note?.description)
            if (note != null) {
                binding.lastEditText.text = setDate(note!!.date)
            }

        }
        //if (note == null)
        //binding.lastEditText.text="Edited " +SimpleDateFormat("MMM dd,yyyy HH:mm").format(Date(System.currentTimeMillis()))


        binding.saveFAB.setOnClickListener {
            val title = binding.titleEdittext.text.toString()
            val desc = binding.descEditText.text.toString()
            if (title.isBlank() and desc.isBlank()) {
                Toast.makeText(activity, "Введите заголовок и текст", Toast.LENGTH_SHORT).show()
            } else {
                val mNote = Notes(title = title, description = desc)
                if (note == null) {
                    notesSaveViewModel.insertNote(mNote)
                    Toast.makeText(activity, "Заметка сохранена", Toast.LENGTH_SHORT).show()
                } else {
                    mNote.noteId = note!!.noteId
                    mNote.date = System.currentTimeMillis()

                    notesSaveViewModel.updateNote(mNote)
                    Toast.makeText(activity, "Заметка обновлена", Toast.LENGTH_SHORT).show()
                }
                activity?.let { it1 -> hideKeyboard(it1) }
                Navigation.findNavController(view).navigateUp()

            }
        }
    }

    private fun setDate(date: Long): CharSequence? {
        val currentMilli = System.currentTimeMillis()
        val result:String
        val sdf = SimpleDateFormat("MMM dd")
        val actualDate = Date(date)
        val currentDate = Date(currentMilli)
        if (sdf.format(currentDate).toString() != sdf.format(actualDate).toString())
            result ="Отредактировано: ".plus(sdf.format(actualDate))
        else
            result ="Отредактировано: ".plus(SimpleDateFormat("hh:mm a").format(actualDate))


        return result
    }

    private fun hideKeyboard(activity: Activity) {
        val inputManager = activity
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // check if no view has focus:
        val currentFocusedView = activity.currentFocus
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(
                currentFocusedView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fbind = null
    }
}