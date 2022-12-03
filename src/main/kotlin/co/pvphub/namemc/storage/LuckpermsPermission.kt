package co.pvphub.namemc.storage

import co.pvphub.namemc.NameMCVelocity
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.node.Node
import java.util.*
import kotlin.jvm.optionals.getOrNull

class LuckpermsPermission : StorageAdapter() {
    override fun playerRewarded(uuid: UUID) {
        LuckPermsProvider.get().userManager.getUser(uuid)?.data()?.add(Node.builder("namemc.rewarded").build())
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun isPlayerRewarded(uuid: UUID): Boolean {
        return NameMCVelocity.get().server.getPlayer(uuid).getOrNull()?.hasPermission("namemc.rewarded") ?: false
    }

    override fun id() = "lp_permission"
}