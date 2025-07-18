package com.kpmq.keepLostItems

import org.bukkit.GameRule
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.java.JavaPlugin

class KeepLostItemsPlugin : JavaPlugin(), Listener, TabCompleter {
    // 플러그인 활성 상태 (config.yml 의 enabled)
    private var pluginEnabled = true
    private val allowedLangs = listOf("English", "Korean", "Japanese")

    override fun onEnable() {
        // 기본 config.yml 복사 & 로드
        saveDefaultConfig()
        pluginEnabled = config.getBoolean("enabled", true)

        // 이벤트 리스너 & 명령어 등록
        server.pluginManager.registerEvents(this, this)
        getCommand("keeplostitems")?.run {
            setExecutor(this@KeepLostItemsPlugin)
            tabCompleter = this@KeepLostItemsPlugin
        }

        logger.info("KeepLostItems is successfully enabled! (lang: ${config.getString("language")})")
    }

    override fun onDisable() {
        logger.info("KeepLostItems is diabled.")
    }

    // 커맨드 처리: toggle / lang
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage(getMsg("usage.general"))
            return true
        }

        when (args[0].lowercase()) {
            "toggle" -> {
                // /keeplostitems toggle  vs  /keeplostitems toggle on/off
                pluginEnabled = when {
                    args.size == 1 -> !pluginEnabled
                    args[1].equals("on", true) -> true
                    args[1].equals("off", true) -> false
                    else -> {
                        sender.sendMessage(getMsg("usage.toggle"))
                        return true
                    }
                }
                config.set("enabled", pluginEnabled)
                saveConfig()
                sender.sendMessage(getMsg(if (pluginEnabled) "enabled" else "disabled"))
                return true
            }
            "lang" -> {
                if (args.size < 2) {
                    sender.sendMessage(getMsg("usage.lang"))
                    return true
                }
                // 대소문자 구분 없이, 첫 글자만 대문자로 맞춤
                val langArg = args[1].lowercase().replaceFirstChar { it.uppercase() }
                if (!allowedLangs.contains(langArg)) {
                    sender.sendMessage(getMsg("usage.lang"))
                    return true
                }
                config.set("language", langArg)
                saveConfig()
                sender.sendMessage(getMsg("langSet"))
                return true
            }
            else -> {
                sender.sendMessage(getMsg("usage.general"))
                return true
            }
        }
    }

    // 사망 시 아이템 잠금 + 경험치 유지
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (!pluginEnabled) return
        val player = event.entity
        if (player.world.getGameRuleValue(GameRule.KEEP_INVENTORY) == true) return

        // 경험치 유지
        event.keepLevel = true
        event.droppedExp = 0

        // 아이템 드롭 잠금
        val deathLoc: Location = player.location
        val drops = event.drops.toList()
        event.drops.clear()
        for (stack in drops) {
            val dropped: Item = player.world.dropItemNaturally(deathLoc, stack)
            dropped.pickupDelay = 0
            dropped.setMetadata("dropOwner", FixedMetadataValue(this, player.uniqueId.toString()))
        }
    }

    // 픽업 시 소유자 검사
    @EventHandler
    fun onPickup(event: EntityPickupItemEvent) {
        if (!pluginEnabled) return
        val picker = event.entity as? Player ?: return
        val itemEntity: Item = event.item
        if (!itemEntity.hasMetadata("dropOwner")) return

        val ownerUuid = itemEntity.getMetadata("dropOwner").first().asString()
        if (picker.uniqueId.toString() != ownerUuid) {
            event.isCancelled = true
        }
    }

    // TabCompletion 지원
    override fun onTabComplete(
        sender: CommandSender, command: Command, alias: String, args: Array<String>
    ): List<String>? {
        return when (args.size) {
            1 -> listOf("toggle", "lang").filter { it.startsWith(args[0], true) }
            2 -> when (args[0].lowercase()) {
                "toggle" -> listOf("on", "off").filter { it.startsWith(args[1], true) }
                "lang"   -> allowedLangs.filter { it.startsWith(args[1], true) }
                else     -> emptyList()
            }
            else -> emptyList()
        }
    }

    // config.yml 에 정의된 메시지 가져오는 헬퍼
    private fun getMsg(path: String): String {
        val lang = config.getString("language", "English")
        return config.getString("messages.$path.$lang")
            ?: "Message not found: $path.$lang"
    }
}
