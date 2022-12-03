package co.pvphub.namemc.storage

import co.pvphub.namemc.NameMCVelocity
import org.simpleyaml.configuration.file.FileConfiguration
import java.util.*

class YamlStorage : StorageAdapter() {
    private lateinit var file: FileConfiguration

    override fun start() {
        val plugin = NameMCVelocity.get()
        file = plugin.createOrLoadConfig("${plugin.dataDirectory}/data.yml")
    }

    override fun playerRewarded(uuid: UUID) {
        file.set(uuid.toString(), true)
    }

    override fun isPlayerRewarded(uuid: UUID): Boolean {
        return file.getBoolean(uuid.toString(), false)
    }

    override fun end() {
        val plugin = NameMCVelocity.get()
        file.save("${plugin.dataDirectory}/data.yml")
    }

    override fun id() = "yaml"
}