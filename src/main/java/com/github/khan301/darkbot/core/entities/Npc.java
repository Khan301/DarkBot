package com.github.khan301.darkbot.core.entities;

import com.github.khan301.darkbot.config.ConfigEntity;
import com.github.khan301.darkbot.config.NpcInfo;

import static com.github.khan301.darkbot.Main.API;

public class Npc extends Ship {
    private static final NpcInfo INITIAL_NPC_INFO = new NpcInfo(); // Prevent NPE trying to obtain npc info.

    public NpcInfo npcInfo = INITIAL_NPC_INFO;
    public boolean ish;

    public Npc(int id) {
        super(id);
    }

    @Override
    public void update() {
        String oldName = playerInfo.username;
        super.update();

        ish = API.readMemoryBoolean(API.readMemoryLong(address + 208) + 56);

        if (!oldName.equals(playerInfo.username))
            npcInfo = ConfigEntity.INSTANCE.getOrCreateNpcInfo(playerInfo.username);
    }
}
