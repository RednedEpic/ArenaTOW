package io.github.TcFoxy.ArenaTOW.BattleArena.controllers.joining.scoreboard;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * @author alkarin
 */
class OfflinePlayerTeams {
//    static LoadingCache<String, OfflinePlayer> map = CacheBuilder.newBuilder()
//            .expireAfterAccess(10, TimeUnit.MINUTES)
//            .build(
//                    new CacheLoader<String, OfflinePlayer>() {
//                        @Override
//                        public OfflinePlayer load(String key) { // no checked exception
//                            return Bukkit.getOfflinePlayer(key);
//                        }
//                    });
//

    static HashMap<String, OfflinePlayer> map = new HashMap<String, OfflinePlayer>();

    static OfflinePlayer getOfflinePlayer(String name) {
        OfflinePlayer op = map.get(name);
        if (op == null) {
            op = Bukkit.getOfflinePlayer(name);
            map.put(name, op);
        }
        return op;
    }

}
