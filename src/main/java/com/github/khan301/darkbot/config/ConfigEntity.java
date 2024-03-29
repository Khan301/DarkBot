package com.github.khan301.darkbot.config;

import com.github.khan301.darkbot.core.entities.Entity;
import com.github.khan301.darkbot.core.manager.MapManager;

import java.util.HashSet;
import java.util.Set;

public class ConfigEntity {

    public static ConfigEntity INSTANCE;

    private final Config config;

    public ConfigEntity(Config config) {
        this.config = config;
        INSTANCE = this;
    }

    public NpcInfo getOrCreateNpcInfo(String name) {
        int mapId = MapManager.id;
        NpcInfo info = config.LOOT.NPC_INFOS.get(name);
        if (info == null) {
            info = new NpcInfo();

            info.radius = 500;
            info.mapList.add(mapId);

            if (!name.equals("ERROR") && !name.isEmpty()) {
                config.LOOT.NPC_INFOS.put(name, info);
                config.LOOT.MODIFIED_NPC.send(name);

                config.changed = true;
            }
        } else if (info.mapList.add(mapId)) {
            config.changed = true;
            config.LOOT.MODIFIED_NPC.send(name);
        }
        return info;
    }

    public BoxInfo getOrCreateBoxInfo(String name) {
        BoxInfo info = config.COLLECT.BOX_INFOS.get(name);
        if (info == null) {
            info = new BoxInfo();
            if (!name.equals("ERROR") && !name.isEmpty()) {
                config.COLLECT.BOX_INFOS.put(name, info);
                config.COLLECT.ADDED_BOX.send(name);

                config.changed = true;
            }
        }
        return info;
    }

    public void updateSafetyFor(Entity entity) {
        if (!entity.locationInfo.isLoaded()) return;
        SafetyInfo.Type type = SafetyInfo.Type.of(entity);
        if (type == null) return;

        Set<SafetyInfo> safetyInfos = getOrCreateSafeties();

        config.ADDED_SAFETY.send(safetyInfos.stream().filter(info -> info.type == type
                && info.x == (int) entity.locationInfo.now.x
                && info.y == (int) entity.locationInfo.now.y)
                .peek(info -> info.entity = entity)
                .findFirst()
                .orElseGet(() -> {
                    SafetyInfo s = new SafetyInfo(type, (int) entity.locationInfo.now.x, (int) entity.locationInfo.now.y, entity);
                    safetyInfos.add(s);
                    config.changed = true;
                    return s;
                }));
    }

    public ZoneInfo getOrCreatePreferred() {
        return config.PREFERRED.computeIfAbsent(MapManager.id, id -> new ZoneInfo(config.MISCELLANEOUS.ZONE_RESOLUTION));
    }

    public ZoneInfo getOrCreateAvoided() {
        return config.AVOIDED.computeIfAbsent(MapManager.id, id -> new ZoneInfo(config.MISCELLANEOUS.ZONE_RESOLUTION));
    }

    public Set<SafetyInfo> getOrCreateSafeties() {
        return config.SAFETY.computeIfAbsent(MapManager.id, id -> new HashSet<>());
    }

    public static void changed() {
        INSTANCE.config.changed = true;
    }

    public Config getConfig() {
        return config;
    }

}
