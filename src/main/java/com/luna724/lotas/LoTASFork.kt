package com.luna724.lotas

import net.fabricmc.api.ClientModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class LoTASFork : ClientModInitializer {
	companion object {
		var LOGGER: Logger = LogManager.getLogger(
			"LoTAS-Fork"
		)
	}

	override fun onInitializeClient() {

	}
}