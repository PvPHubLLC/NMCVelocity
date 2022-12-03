package co.pvphub.namemc

import co.pvphub.namemc.retrieve.RetrieveMethod
import co.pvphub.namemc.retrieve.SingleMethod
import co.pvphub.namemc.retrieve.SyncMethod
import co.pvphub.namemc.storage.LuckpermsMeta
import co.pvphub.namemc.storage.LuckpermsPermission
import co.pvphub.namemc.storage.StorageAdapter
import co.pvphub.namemc.storage.YamlStorage
import co.pvphub.velocity.dsl.event
import co.pvphub.velocity.dsl.simpleCommand
import co.pvphub.velocity.plugin.VelocityPlugin
import co.pvphub.velocity.scheduling.async
import co.pvphub.velocity.util.colored
import com.google.inject.Inject
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.event.ClickEvent
import java.nio.file.Path
import java.util.logging.Logger

class NameMCVelocity @Inject constructor(
    server: ProxyServer,
    logger: Logger,
    @DataDirectory
    dataDirectory: Path
) : VelocityPlugin(server, logger, dataDirectory) {

    private val storageMethods = listOf(
        LuckpermsMeta(),
        LuckpermsPermission(),
        YamlStorage()
    )

    private val retrieveMethods = listOf(
        SingleMethod(),
        SyncMethod()
    )

    @Subscribe
    fun init(e: ProxyInitializeEvent) {
        instance = this
        saveDefaultConfig()

        storageMethod = storageMethods.firstOrNull { it.id() == config!!.getString("storage").lowercase() } ?: YamlStorage()
        storageMethod.start()
        event<ProxyShutdownEvent> { storageMethod.end() }

        retrieveMethod = retrieveMethods.firstOrNull { it.id() == config!!.getString("method").lowercase() } ?: SingleMethod()
        retrieveMethod.start()

        event<PlayerLikedEvent> {
            config!!.getStringList("messages.broadcast")
                .map { it.colored(player) }
                .forEach { server.sendMessage(it) }
            config!!.getStringList("rewards.commands")
                .forEach { server.commandManager.executeImmediatelyAsync(server.consoleCommandSource, it) }
        }

        simpleCommand {
            name = "namemc"
            permission = "namemc.command"
            suggestSubCommands = true

            executes { source, args, alias ->
                // Simply send the user the link
                config!!.getStringList("messages.link")
                    .map { it.colored().clickEvent(ClickEvent.openUrl("https://namemc.com/server/${config!!.getString("server-ip")}")) }
                    .forEach { l -> source.sendMessage(l) }
            }

            // We only want to have this sub command IF it is in "SINGLE" mode
            if (config!!.getString("method").lowercase() == "SINGLE") {
                subCommands += simpleCommand {
                    name = "verify"
                    permission = "namemc.command.verify"
                    val cooldowns = hashMapOf<CommandSource, Long>()

                    executes { source, args, alias ->
                        if (source !is Player) {
                            source.sendMessage("&cPlayer-Only command!".colored())
                            return@executes
                        }

                        // Check is player is already verified
                        if (storageMethod.isPlayerRewarded(source)) {
                            source.sendMessage(config!!.getString("messages.already-verified").colored())
                            return@executes
                        }

                        // Check if the player is on command cooldown
                        if (cooldowns.containsKey(source) && cooldowns[source]!! >= System.currentTimeMillis()) {
                            source.sendMessage(config!!.getString("single-settings.cooldown-message").colored())
                            return@executes
                        }

                        cooldowns[source] = System.currentTimeMillis() + config!!.getLong("single-settings.command-cooldown")
                        retrieveMethod.checkAsync(this@NameMCVelocity, source.uniqueId) {
                            if (it) {
                                // Player has verified, send out an event
                                server.eventManager.fire(PlayerLikedEvent(source))
                            } else {
                                // Player isn't verified
                                source.sendMessage(config!!.getString("messages.not-verified").colored())
                            }
                        }
                    }
                }
            }
        }.register(this)
    }

    companion object {
        private lateinit var instance: NameMCVelocity
        private lateinit var storageMethod: StorageAdapter
        private lateinit var retrieveMethod: RetrieveMethod
        val get = { instance }
        val storage = { storageMethod }
    }

}