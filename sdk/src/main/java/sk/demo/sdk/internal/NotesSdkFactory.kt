package sk.demo.sdk.internal

import android.content.Context
import sk.demo.sdk.NotesSdk
import sk.demo.sdk.internal.backend.Env

class NotesSdkFactory {
    companion object {
        private var instance: NotesSdkImpl? = null

        @JvmStatic
        fun getInstance(context: Context): NotesSdk {
            instance.let {
                return if (it != null)
                    it
                else {
                    instance = NotesSdkImpl(context, Env.NOTES)
                    getInstance(context)
                }
            }
        }
    }
}