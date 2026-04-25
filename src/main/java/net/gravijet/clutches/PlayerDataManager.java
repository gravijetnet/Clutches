package net.gravijet.clutches;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class PlayerDataManager {
    private final Map<UUID, PlayerData> dataMap = new HashMap<>();

    public PlayerData getData(Player p) {
        return dataMap.computeIfAbsent(p.getUniqueId(), k -> new PlayerData());
    }

    public void removeData(Player p) {
        dataMap.remove(p.getUniqueId());
    }
}
