package co.pvphub.namemc.storage

import com.velocitypowered.api.proxy.Player
import java.util.UUID

abstract class StorageAdapter {

    open fun start() {

    }

    open fun playerRewarded(player: Player) = playerRewarded(player.uniqueId)
    abstract fun playerRewarded(uuid: UUID)

    open fun isPlayerRewarded(player: Player) = isPlayerRewarded(player.uniqueId)
    abstract fun isPlayerRewarded(uuid: UUID) : Boolean

    abstract fun id() : String

    open fun end() {

    }

}