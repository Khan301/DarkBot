package com.github.khan301.darkbot.modules;

import com.github.khan301.darkbot.Main;
import com.github.khan301.darkbot.config.Config;
import com.github.khan301.darkbot.core.entities.Npc;
import com.github.khan301.darkbot.core.entities.Ship;
import com.github.khan301.darkbot.core.itf.Module;
import com.github.khan301.darkbot.core.manager.HeroManager;
import com.github.khan301.darkbot.core.manager.MapManager;
import com.github.khan301.darkbot.core.utils.Drive;
import com.github.khan301.darkbot.core.utils.Location;
import com.github.khan301.darkbot.modules.utils.NpcAttacker;
import com.github.khan301.darkbot.modules.utils.SafetyFinder;

import java.util.Comparator;
import java.util.List;

import static java.lang.Double.max;
import static java.lang.Double.min;
import static java.lang.Math.random;

public class LootModule implements Module {

    private Main main;

    private List<Ship> ships;
    private List<Npc> npcs;

    private HeroManager hero;
    private Drive drive;

    private Config config;

    NpcAttacker attack;
    private long refreshing;
    private SafetyFinder safety;

    @Override
    public void install(Main main) {
        this.main = main;
        this.attack = new NpcAttacker(main);
        this.safety = new SafetyFinder(main);

        this.hero = main.hero;
        this.drive = main.hero.drive;

        this.ships = main.mapManager.entities.ships;
        this.npcs = main.mapManager.entities.npcs;
        this.config = main.config;
    }

    @Override
    public String status() {
        return safety.state() != SafetyFinder.Escaping.NONE ? safety.status() :
                attack.hasTarget() ? attack.status() : "Roaming";
    }

    @Override
    public boolean canRefresh() {
        if (!attack.hasTarget()) refreshing = System.currentTimeMillis() + 10000;
        return !attack.hasTarget() && safety.state() == SafetyFinder.Escaping.WAITING;
    }

    @Override
    public void tick() {
        if (checkDangerousAndCurrentMap()) {
            main.guiManager.pet.setEnabled(true);

            if (findTarget()) {
                moveToAnSafePosition();
                ignoreInvalidTarget();
                attack.doKillTargetTick();
            } else {
                hero.roamMode();
                if (!drive.isMoving()) drive.moveRandom();
            }
        }
    }

    boolean checkDangerousAndCurrentMap() {
        safety.setRefreshing(System.currentTimeMillis() <= refreshing);
        return safety.tick() && checkMap();
    }

    private boolean checkMap() {
        if (this.config.GENERAL.WORKING_MAP != this.hero.map.id && !main.mapManager.entities.portals.isEmpty()) {
            this.main.setModule(new MapModule())
                    .setTarget(this.main.starManager.byId(this.main.config.GENERAL.WORKING_MAP));
            return false;
        }
        return true;
    }

    boolean findTarget() {
        return (attack.target = closestNpc(hero.locationInfo.now)) != null;
    }

    void ignoreInvalidTarget() {
        double closestDist = drive.closestDistance(attack.target.locationInfo.now);
        if (!main.mapManager.isTarget(attack.target)) {
            if (closestDist > 600) {
                attack.target.setTimerTo(5000);
                hero.setTarget(attack.target = null);
            }
        } else if (!(attack.target.npcInfo.ignoreOwnership || main.mapManager.isCurrentTargetOwned())
                || (hero.locationInfo.distance(attack.target) > config.LOOT.NPC_DISTANCE_IGNORE) // Too far away from ship
                || (closestDist > 650 && attack.target.health.hpPercent() > 0.90)   // Too far into obstacle and full hp
                || (closestDist > 500 && !attack.target.locationInfo.isMoving() // Inside obstacle, waiting & and regen shields
                        && (attack.target.health.shIncreasedIn(1000) || attack.target.health.shieldPercent() > 0.99))) {
            attack.target.setTimerTo(5000);
            hero.setTarget(attack.target = null);
        }
    }

    void moveToAnSafePosition() {
        Npc target = attack.target;
        Location direction = drive.movingTo();
        Location heroLoc = hero.locationInfo.now;
        Location targetLoc = target.locationInfo.destinationInTime(400);

        double distance = heroLoc.distance(target.locationInfo.now);
        double angle = targetLoc.angle(heroLoc);
        double radius = target.npcInfo.radius;

        if (target != hero.target || attack.castingAbility()) radius = Math.min(500, radius);
        if (!target.locationInfo.isMoving() && target.health.hpPercent() < 0.25) radius = Math.min(radius, 600);

        if (target.npcInfo.noCircle) {
            if (targetLoc.distance(direction) <= radius) return;
            distance = 100 + random() * (radius - 110);
            angle += (random() * 0.1) - 0.05;
        } else {
            double maxRadFix = target.npcInfo.radius / 2,
                    radiusFix = (int) Math.max(Math.min(radius - distance, maxRadFix), -maxRadFix);
            distance = (radius += radiusFix);
            // Moved distance + speed - distance to chosen radius same angle, divided by radius
            angle += Math.max((hero.shipInfo.speed * 0.625) + (min(200, target.locationInfo.speed) * 0.625)
                    - heroLoc.distance(Location.of(targetLoc, angle, radius)), 0) / radius;
        }
        direction = Location.of(targetLoc, angle, distance);

        while (!drive.canMove(direction) && distance < 10000)
            direction.toAngle(targetLoc, angle += 0.3, distance += 2);
        if (distance >= 10000) direction.toAngle(targetLoc, angle, 500);

        setConfig(direction);

        drive.move(direction);
    }

    public void setConfig(Location direction) {
        if (!attack.hasTarget()) hero.roamMode();
        else if (config.LOOT.RUN_CONFIG_IN_CIRCLE
                && attack.target.health.hpPercent() < 0.25
                && hero.locationInfo.now.distance(direction) > attack.target.npcInfo.radius * 2) hero.runMode();
        else if (hero.locationInfo.now.distance(direction) > attack.target.npcInfo.radius * 3) hero.roamMode();
        else hero.attackMode();
    }

    private boolean isAttackedByOthers(Npc npc) {
        for (Ship ship : this.ships) {
            if (ship.address == hero.address || ship.address == hero.pet.address
                    || !ship.isAttacking(npc)) continue;
            npc.setTimerTo(20_000);
            return true;
        }
        return npc.isInTimer();
    }

    private Npc closestNpc(Location location) {
        int extraPriority = attack.hasTarget() &&
                (hero.target == attack.target || hero.locationInfo.distance(attack.target) < 600)
                ? 20 - (int)(attack.target.health.hpPercent() * 10) : 0;
        return this.npcs.stream()
                .filter(n -> (n == attack.target && hero.isAttacking(attack.target)) ||
                        ((!config.GENERAL.ROAMING.ONLY_KILL_PREFERRED || main.mapManager.preferred.contains(n.locationInfo.now))
                                && drive.closestDistance(location) < 450
                                && shouldKill(n)))
                .min(Comparator.<Npc>comparingInt(n -> n.npcInfo.priority - (n == attack.target ? extraPriority : 0))
                        .thenComparing(n -> n.locationInfo.now.distance(location))).orElse(null);
    }

    private boolean shouldKill(Npc n) {
        boolean attacked = this.isAttackedByOthers(n);
        return n.npcInfo.kill &&
                (n.npcInfo.ignoreAttacked || !attacked) && // Either ignore attacked, or not being attacked
                (!n.npcInfo.attackSecond || attacked) &&   // Either don't want to attack second, or being attacked
                (!n.npcInfo.passive || n.isAttacking(hero));
    }

}
