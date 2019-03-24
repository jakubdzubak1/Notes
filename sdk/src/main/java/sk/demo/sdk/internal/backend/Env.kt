package sk.demo.sdk.internal.backend

enum class Env(private val backendUrl: String): Environment {
    NOTES("https://private-anon-298be78c7d-note10.apiary-mock.com/");

    override fun getUrl(): String = backendUrl

}
