package sk.demo.sdk

interface NotesSdk {

    /**
     * @return NoteManager interface which provides api calls for notes.
     */
    fun notes(): NoteManager

}