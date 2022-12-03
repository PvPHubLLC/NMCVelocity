package co.pvphub.namemc.storage

import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.node.types.MetaNode
import java.util.*

class LuckpermsMeta : StorageAdapter() {
    override fun playerRewarded(uuid: UUID) {
        val user = LuckPermsProvider.get().userManager.getUser(uuid)
        val node = MetaNode.builder("namemc", true.toString()).build()
        user?.data()?.add(node)
    }

    override fun isPlayerRewarded(uuid: UUID): Boolean {
        val user = LuckPermsProvider.get().userManager.getUser(uuid)
        return user?.cachedData?.metaData?.getMetaValue("namemc", String::toBooleanStrictOrNull)?.orElse(false) ?: false
    }

    override fun id() = "lp_meta"
}