package sk.demo.notes

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_create_note.view.*
import sk.demo.notes.model.AppNote
import sk.demo.sdk.NotesSdk
import sk.demo.sdk.internal.NotesSdkFactory
import sk.demo.sdk.model.note.Note

class MainActivity : AppCompatActivity() {

    private val disposables: CompositeDisposable by lazy { CompositeDisposable() }
    private val sdk: NotesSdk by lazy { NotesSdkFactory.getInstance(this) }
    private lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fab.setOnClickListener { showCreateUpdateDialog() }
        swipeToRefresh.setOnRefreshListener { pullNotes() }
        pullNotes()
        //region getNote call test
//        disposables.add(
//            sdk.notes()
//                .getNote(1)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    {
//                        Snackbar.make(content, it.toString(), Snackbar.LENGTH_SHORT).show()
//                    },
//                    { t: Throwable? ->
//                        Log.e(this.javaClass.simpleName, t?.message)
//                        showError(t?.message)
//                    }
//                )
//        )
        //endregion
    }

    private fun pullNotes() {
        disposables.add(
            sdk.notes()
                .getNotes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if (::adapter.isInitialized) {
                            adapter.selected = null
                            invalidateOptionsMenu()
                            adapter.data = it.toMutableList()
                            adapter.notifyDataSetChanged()
                            swipeToRefresh.isRefreshing = false
                        } else
                            initAdapter(it)
                        showContent()
                    },
                    { t: Throwable? ->
                        Log.e(this.javaClass.simpleName, t?.message)
                        showError(t?.message)
                    }
                )
        )
    }

    private fun initAdapter(data: List<Note>) {
        adapter = NotesAdapter(data.toMutableList(), { }, { invalidateOptionsMenu() })
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    //region LCE
    private fun showContent() {
        recyclerView.visibility = View.VISIBLE
        errorView.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
    }

    private fun showError(message: String? = null) {
        errorView.text = message ?: "Something went wong."
        recyclerView.visibility = View.INVISIBLE
        errorView.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        errorView.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
    }
    //endregion

    private fun showCreateUpdateDialog(noteToUpdate: AppNote? = null) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_create_note, null)
        noteToUpdate?.let { view.noteEditText.setText(it.note.title) }
        AlertDialog.Builder(this)
            .setView(view)
            .setTitle(if (noteToUpdate == null) R.string.create_note else R.string.edit_note)
            .setPositiveButton(if (noteToUpdate == null) R.string.add else R.string.save) { _, _ ->
                val currentValue = view.noteEditText.text.toString()
                if (noteToUpdate == null)
                    createNote(currentValue)
                else
                    noteToUpdate.note.id?.let { updateNote(it, currentValue, noteToUpdate.position) }
            }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
            .setCancelable(false)
            .show()
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.remove_dialog_message)
            .setTitle(R.string.remove_note)
            .setPositiveButton(R.string.remove) { _, _ ->
                adapter.selectedNote?.let { note ->
                    note.note.id?.let { id ->
                        removeNote(id, note.position)
                    }
                }
            }
            .setNegativeButton(R.string.cancel) { d, _ -> d.cancel() }
            .setCancelable(false)
            .show()
    }

    //region CRUD
    private fun createNote(title: String) {
        disposables.add(
            sdk.notes()
                .createNote(Note(title = title))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        adapter.data.add(it)
                        adapter.notifyItemInserted(adapter.data.size - 1)
                    },
                    { t: Throwable? ->
                        Log.e(this.javaClass.simpleName, t?.message)
                        Snackbar.make(content, t?.message ?: "Something went wrong", Snackbar.LENGTH_SHORT).show()
                    }
                )
        )
    }

    private fun updateNote(id: Int, newTitle: String, position: Int) {
        disposables.add(
            sdk.notes()
                .updateNote(id, Note(title = newTitle))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        adapter.data[position] = it
                        adapter.notifyItemChanged(position)
                    },
                    { t: Throwable? ->
                        Log.e(this.javaClass.simpleName, t?.message)
                        Snackbar.make(content, t?.message ?: "Something went wrong", Snackbar.LENGTH_SHORT).show()
                    }
                )
        )
    }

    private fun removeNote(id: Int, position: Int) {
        disposables.add(
            sdk.notes()
                .removeNote(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        adapter.data.removeAt(position)
                        adapter.notifyItemRemoved(position)
                    },
                    { t: Throwable? ->
                        Log.e(this.javaClass.simpleName, t?.message)
                        Snackbar.make(content, t?.message ?: "Something went wrong", Snackbar.LENGTH_SHORT).show()
                    }
                )
        )
    }
    //endregion

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        if (::adapter.isInitialized)
            menu?.setGroupVisible(R.id.actions, adapter.selected != null)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_edit_action -> {
                showCreateUpdateDialog(adapter.selectedNote)
                true
            }
            R.id.menu_remove_action -> {
                showDeleteDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}
