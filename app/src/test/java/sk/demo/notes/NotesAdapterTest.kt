package sk.demo.notes

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import sk.demo.sdk.model.note.Note

class NotesAdapterTest {

    lateinit var adapter: NotesAdapter
    lateinit var data: MutableList<Note>

    @Before
    fun setUp(){
        data = mutableListOf()
        for (i in 0..10){
            data.add(Note(i, i.toString()))
        }
        adapter = NotesAdapter(data, {}, {})
    }

    @Test
    fun getItemCountTest(){
        Assert.assertEquals(adapter.itemCount, data.size)
    }

}