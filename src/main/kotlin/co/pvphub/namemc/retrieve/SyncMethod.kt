package co.pvphub.namemc.retrieve

import co.pvphub.namemc.NameMCVelocity
import co.pvphub.namemc.PlayerLikedEvent
import co.pvphub.namemc.util.arrayUrl
import co.pvphub.velocity.scheduling.AsyncTask
import co.pvphub.velocity.scheduling.asyncRepeat
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.velocitypowered.api.scheduler.ScheduledTask
import java.net.URL
import java.util.*

class SyncMethod : RetrieveMethod() {
    private var task: AsyncTask? = null
    private var liked = listOf<String>()

    override fun start() {
        task?.cancel()
        asyncRepeat(NameMCVelocity.get(), NameMCVelocity.get().config!!.getLong("sync-settings.refresh-period")) {
            task = it

            val str = URL(arrayUrl(NameMCVelocity.get().config!!.getString("server-ip"))).readText()
            val jsonArray = JsonParser.parseString(str).asJsonArray
            liked = jsonArray.map { i -> i.toString() }
            checkAll()
        }
    }

    fun checkAll() {
        val plugin = NameMCVelocity.get()
        plugin.server.allPlayers
            .filter { liked.contains(it.uniqueId.toString()) }
            .filter { !NameMCVelocity.storage().isPlayerRewarded(it) }
            .forEach {
                plugin.server.eventManager.fire(PlayerLikedEvent(it))
            }
    }

    override fun check(uuid: UUID) = liked.contains(uuid.toString())

    override fun id() = "sync"
}