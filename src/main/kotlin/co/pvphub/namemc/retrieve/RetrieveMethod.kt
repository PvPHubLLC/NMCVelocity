package co.pvphub.namemc.retrieve

import co.pvphub.velocity.plugin.VelocityPlugin
import co.pvphub.velocity.scheduling.async
import java.util.UUID

abstract class RetrieveMethod {

    open fun start() {

    }

    fun checkAsync(plugin: VelocityPlugin, uuid: UUID, cb: (Boolean) -> Unit) = async(plugin) { cb(check(uuid)) }

    abstract fun check(uuid: UUID): Boolean

    abstract fun id(): String
}