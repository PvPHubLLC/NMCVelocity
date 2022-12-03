package co.pvphub.namemc.retrieve

import co.pvphub.namemc.NameMCVelocity
import co.pvphub.namemc.util.singleUrl
import java.net.URL
import java.util.*

class SingleMethod : RetrieveMethod() {

    override fun check(uuid: UUID) =
        URL(singleUrl(uuid, NameMCVelocity.get().config!!.getString("server-ip"))).readText() == "true"

    override fun id() = "single"
}