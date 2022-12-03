package co.pvphub.namemc.util

import com.velocitypowered.api.proxy.Player
import java.util.UUID

fun arrayUrl(ip: String) = "https://api.namemc.com/server/$ip/likes"
fun singleUrl(uuid: UUID, ip: String) = "https://api.namemc.com/server/$ip/likes?profile=$uuid"