package com.example.notes

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.database.NotesViewModel
import com.example.notes.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import kotlin.properties.Delegates

class HomeFragment : Fragment() {
    private var fbind: FragmentHomeBinding? = null
    private lateinit var notesViewModel: NotesViewModel
    private val sharedPrefKey = "appSettings"
    private val nightModeKey = "NightMode"
    lateinit var appPref: SharedPreferences
    lateinit var sharedPrefsEdit: SharedPreferences.Editor
    var nightModeStatus by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)
        fbind = binding
        notesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        appPref = activity?.getSharedPreferences(sharedPrefKey, 0)!!
        nightModeStatus = appPref.getInt("NightMode", 3)

//        setTheme(nightModeStatus)

        val adapter = activity?.applicationContext?.let { NotesAdapter() }
        binding.noteRecycler.adapter = adapter
        binding.noteRecycler.setHasFixedSize(true)
        binding.noteRecycler.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        notesViewModel.allNotes.observe(viewLifecycleOwner, androidx.lifecycle.Observer { notes ->
            adapter?.submitList(notes)
        })

        binding.newNoteFAB.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_homeFragment_to_noteFragment)
        }

        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val mNote = adapter?.getNote(viewHolder.adapterPosition)
                mNote?.let { notesViewModel.deleteNote(it) }

                if (mNote != null) {
                    Snackbar.make(view, "Заметка удалена", Snackbar.LENGTH_LONG)
                        .setAction("Отмена") {
                            notesViewModel.insertNote(mNote)
                        }.show()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.noteRecycler)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fbind = null
    }
}