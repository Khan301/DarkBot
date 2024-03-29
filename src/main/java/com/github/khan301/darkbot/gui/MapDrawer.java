package com.github.khan301.darkbot.gui;

import com.github.khan301.darkbot.Main;
import com.github.khan301.darkbot.config.Config;
import com.github.khan301.darkbot.config.SafetyInfo;
import com.github.khan301.darkbot.config.ZoneInfo;
import com.github.khan301.darkbot.core.entities.Box;
import com.github.khan301.darkbot.core.entities.*;
import com.github.khan301.darkbot.core.manager.*;
import com.github.khan301.darkbot.core.objects.Health;
import com.github.khan301.darkbot.core.utils.Drive;
import com.github.khan301.darkbot.core.utils.Location;
import com.github.khan301.darkbot.core.utils.pathfinder.Area;
import com.github.khan301.darkbot.core.utils.pathfinder.PathPoint;
import com.github.khan301.darkbot.gui.trail.Line;
import com.github.khan301.darkbot.gui.utils.UIUtils;
import com.github.khan301.darkbot.utils.Time;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MapDrawer extends JPanel {

    private final DecimalFormat formatter = new DecimalFormat("###,###,###");

    private Color BACKGROUND = Color.decode("#263238");
    private Color TEXT = Color.decode("#F2F2F2");
    protected Color TEXT_DARK = Color.decode("#BBBBBB");
    private Color GOING = Color.decode("#8F9BFF");
    private Color PORTALS = Color.decode("#AEAEAE");
    private Color OWNER = Color.decode("#22CC22");
    private Color[] TRAIL = IntStream.rangeClosed(1, 255).mapToObj(i -> new Color(224, 224, 224, i)).toArray(Color[]::new);
    private Color BOXES = Color.decode("#BBB830");
    private Color ALLIES = Color.decode("#29B6F6");
    private Color ENEMIES = Color.decode("#d50000");
    private Color NPCS = Color.decode("#AA4040");
    private Color TARGET = NPCS.darker();
    private Color PET = Color.decode("#004c8c");
    private Color PET_IN = Color.decode("#c56000");
    private Color HEALTH = Color.decode("#388e3c");
    private Color SHIELD = Color.decode("#0288d1");
    private Color METEROID = Color.decode("#AAAAAA");
    private Color BARRIER = new Color(255, 255, 255, 32);
    private Color BARRIER_BORDER = new Color(255, 255, 255, 128);
    private Color NO_CLOACK = new Color(24, 160, 255, 32);
    private Color PREFER = new Color(0, 255, 128, 32);
    private Color AVOID = new Color(255, 0, 0, 32);
    private Color SAFETY = new Color(16, 96, 255, 48);

    private Color BASES = Color.decode("#00D14E");
    private Color UNKNOWN = Color.decode("#7C05D1");
    private Color STATS_BACKGROUND = new Color(38, 50, 56, 128);

    private Color ACTION_BUTTON = new Color(255, 255, 255, 160);
    private Color DARKEN_BACK = new Color(0, 0, 0, 96);

    protected Font FONT_BIG = new Font("Consolas", Font.PLAIN, 32);
    private Font FONT_MID = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
    private Font FONT_SMALL = new Font("Consolas", Font.PLAIN, 12);
    private Font FONT_TINY = new Font(Font.SANS_SERIF, Font.PLAIN, 9);

    private TreeMap<Long, Line> positions = new TreeMap<>();

    private Main main;
    protected HeroManager hero;
    private Drive drive;
    private MapManager mapManager;
    private GuiManager guiManager;
    private StatsManager statsManager;
    private PingManager pingManager;
    protected Config config;

    private List<Portal> portals;
    private List<Npc> npcs;
    private List<Box> boxes;
    private List<Ship> ships;
    private List<BattleStation> battleStations;
    private List<BasePoint> basePoints;

    private RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    private Location last = new Location(0, 0);

    protected boolean hovering;
    protected int width, height, mid;

    public MapDrawer() {
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                hovering = true;
                repaint();
            }
            public void mouseExited(MouseEvent evt) {
                hovering = false;
                repaint();
            }
        });
    }

    public MapDrawer(Main main) {
        this();
        setBorder(UIUtils.getBorder());
        setup(main);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (main.config.MISCELLANEOUS.MAP_START_STOP && SwingUtilities.isLeftMouseButton(e)) {
                    main.setRunning(!main.isRunning());
                    repaint();
                    return;
                }
                hero.drive.move(undoTranslateX(e.getX()), undoTranslateY(e.getY()));
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (main.config.MISCELLANEOUS.MAP_START_STOP && SwingUtilities.isLeftMouseButton(e)) return;
                hero.drive.move(undoTranslateX(e.getX()), undoTranslateY(e.getY()));
            }
        });
    }

    public void setup(Main main) {
        this.main = main;
        this.hero = main.hero;
        this.drive = main.hero.drive;
        this.mapManager = main.mapManager;
        this.guiManager = main.guiManager;
        this.statsManager = main.statsManager;
        this.pingManager = main.pingManager;
        this.config = main.config;

        this.portals = main.mapManager.entities.portals;
        this.npcs = main.mapManager.entities.npcs;
        this.boxes = main.mapManager.entities.boxes;
        this.ships = main.mapManager.entities.ships;
        this.battleStations = main.mapManager.entities.battleStations;
        this.basePoints = main.mapManager.entities.basePoints;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (main == null) return;
        Graphics2D g2 = setupDraw(g);


        synchronized (Main.UPDATE_LOCKER) {
            drawZones(g2);
            if (config.MISCELLANEOUS.DISPLAY.SHOW_ZONES) drawCustomZones(g2);
            drawInfos(g2);
            drawHealth(g2);
            drawTrail(g2);
            drawStaticEntities(g2);
            drawDynamicEntities(g2);
            drawHero(g2);
        }

        if (config.MISCELLANEOUS.DEV_STUFF) {
            g2.setFont(FONT_TINY);
            g2.setColor(TEXT_DARK);
            synchronized (Main.UPDATE_LOCKER) {
                List<Entity> entities = mapManager.entities.allEntities.stream().flatMap(Collection::stream)
                        .filter(e -> e.id > 150_000_000 && e.id < 160_000_000)
                        .filter(e -> e.locationInfo.isLoaded())
                        .collect(Collectors.toList());

                g2.setColor(STATS_BACKGROUND);
                for (Entity e : entities) {
                    Location loc = e.locationInfo.now;
                    int strWidth = g2.getFontMetrics().stringWidth(e.toString());
                    g2.fillRect(translateX(loc.x) - (strWidth >> 1), translateY(loc.y) - 7, strWidth, 8);
                }
                g2.setColor(TEXT);
                g2.setFont(FONT_TINY);
                for (Entity e : entities) {
                    Location loc = e.locationInfo.now;
                    drawString(g2, e.toString(), translateX(loc.x), translateY(loc.y), Align.MID);
                }
            }
        }

        drawStats(g2,
                "cre/h " + formatter.format(statsManager.earnedCredits()),
                "uri/h " + formatter.format(statsManager.earnedUridium()),
                "exp/h " + formatter.format(statsManager.earnedExperience()),
                "hon/h " + formatter.format(statsManager.earnedHonor()),
                "cargo " + statsManager.deposit + "/" + statsManager.depositTotal,
                "death " + guiManager.deaths + '/' + config.GENERAL.SAFETY.MAX_DEATHS);

        if (hovering && main.config.MISCELLANEOUS.MAP_START_STOP) drawActionButton(g2);
    }

    protected Graphics2D setupDraw(Graphics g) {
        height = getHeight();
        width = getWidth();
        mid = width / 2;

        g.setColor(BACKGROUND);
        g.fillRect(0, 0, width, height);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHints(hints);
        return g2;
    }

    protected void drawZones(Graphics2D g2) {
        for (Barrier barrier : mapManager.entities.barriers) {
            Area area = barrier.getZone();
            g2.setColor(this.BARRIER);
            g2.fillRect(
                    translateX(area.minX), translateY(area.minY),
                    translateX(area.maxX - area.minX), translateY(area.maxY - area.minY));
            g2.setColor(BARRIER_BORDER);
            g2.drawRect(
                    translateX(area.minX), translateY(area.minY),
                    translateX(area.maxX - area.minX), translateY(area.maxY - area.minY));
        }

        g2.setColor(this.NO_CLOACK);
        for (NoCloack noCloack : mapManager.entities.noCloack) {
            Area area = noCloack.getZone();
            g2.fillRect(
                    translateX(area.minX), translateY(area.minY),
                    translateX(area.maxX - area.minX), translateY(area.maxY - area.minY));
        }
    }

    protected void drawCustomZones(Graphics2D g2) {
        g2.setColor(PREFER);
        drawCustomZone(g2, config.PREFERRED.get(hero.map.id));
        if (config.GENERAL.ROAMING.SEQUENTIAL) drawCustomZonePath(g2, config.PREFERRED.get(hero.map.id));
        g2.setColor(AVOID);
        drawCustomZone(g2, config.AVOIDED.get(hero.map.id));
        g2.setColor(SAFETY);
        for (SafetyInfo safety : config.SAFETY.get(hero.map.id)) {
            if (safety.runMode == SafetyInfo.RunMode.NEVER
                    || safety.entity == null || safety.entity.removed) continue;
            drawSafeZone(g2, safety);
        }
    }

    private void drawInfos(Graphics2D g2) {
        g2.setColor(TEXT_DARK);
        String running = (main.isRunning() ? "RUNNING " : "WAITING ") + Time.toString(statsManager.runningTime());
        drawString(g2, running, mid, height / 2 + 35, Align.MID);

        g2.setFont(FONT_SMALL);
        String info = "v" + Main.VERSION + " - Refresh: " +
                (main.isRunning() ? Time.toString(System.currentTimeMillis() - main.lastRefresh) : "00") +
                "/" + Time.toString(config.MISCELLANEOUS.REFRESH_TIME * 60 * 1000);
        drawString(g2, info, 5, 12, Align.LEFT);
        if (main.tickingModule) drawString(g2, main.module.status(), 5, 12 + 15, Align.LEFT);

        drawString(g2, pingManager.ping + " ms ping", width - 5, 12, Align.RIGHT);
        drawString(g2, String.format("%.1f ms tick", main.avgTick), width - 5, 24, Align.RIGHT);
        drawString(g2, "SID: " + main.backpage.sidStatus(), width - 5, 36, Align.RIGHT);

        drawMap(g2);
    }

    protected void drawMap(Graphics2D g2) {
        g2.setColor(TEXT_DARK);
        g2.setFont(FONT_BIG);
        drawString(g2, hero.map.name, mid, (height / 2) - 5, Align.MID);
    }

    private void drawHealth(Graphics2D g2) {
        g2.setColor(TEXT);
        g2.setFont(FONT_MID);
        if (!config.MISCELLANEOUS.DISPLAY.HIDE_NAME)
            drawString(g2, hero.playerInfo.username, 10 + (mid - 20) / 2, height - 40, Align.MID);
        drawHealth(g2, hero.health, 10, this.getHeight() - 34, mid - 20);

        if (hero.target != null && !hero.target.removed) {
            Ship ship = hero.target;

            if (ship instanceof Npc) {
                g2.setColor(ENEMIES);
                g2.setFont(FONT_MID);
                String name = ((Npc) ship).playerInfo.username;
                drawString(g2, name, mid + 10 + (mid - 20) / 2, height - 40, Align.MID);
            }

            drawHealth(g2, hero.target.health, mid + 10, height - 34, mid - 20);
        }
    }

    private void drawTrail(Graphics2D g2) {
        Location heroLocation = hero.locationInfo.now;

        double distance = last.distance(heroLocation);

        if (distance > 500) {
            last = hero.locationInfo.now.copy();
        } else if (distance > 100) {
            positions.put(System.currentTimeMillis(), new Line(last, last = heroLocation.copy()));
        }
        positions.headMap(System.currentTimeMillis() - config.MISCELLANEOUS.DISPLAY.TRAIL_LENGTH * 1000).clear();

        if (positions.isEmpty()) return;

        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        List<List<Location>> paths = Line.getSmoothedPaths(positions.values());
        double max = paths.stream().mapToInt(Collection::size).sum() / 255d, curr = 0;
        for (List<Location> points : paths) {
            Location last = null;
            for (Location point : points) {
                g2.setColor(TRAIL[(int) (curr++ / max)]);
                if (last != null) drawLine(g2, last.x, last.y,point.x, point.y);
                last = point;
            }
        }
        g2.setStroke(new BasicStroke());
    }

    protected void drawStaticEntities(Graphics2D g2) {
        g2.setColor(PORTALS);
        for (Portal portal : portals) {
            Location loc = portal.locationInfo.now;
            g2.drawOval(translateX(loc.x) - 6, translateY(loc.y) - 6, 11, 11);
        }

        for (BattleStation station : this.battleStations) {
            if (station.hullId == 0) g2.setColor(this.METEROID);
            else if (station.info.isEnemy()) g2.setColor(this.ENEMIES);
            else g2.setColor(this.ALLIES);

            Location loc = station.locationInfo.now;
            if (station.hullId >= 0 && station.hullId < 255)
                g2.fillOval(translateX(loc.x) - 5, translateY(loc.y) - 4, 11, 9);
            else drawEntity(g2, loc, false);
        }

        g2.setColor(this.BASES);
        for (BasePoint base : this.basePoints) {
            Location loc = base.locationInfo.now;
            g2.fillOval(this.translateX(loc.x) - 2, this.translateY(loc.y) - 2, 4, 4);
        }
    }

    private void drawDynamicEntities(Graphics2D g2) {
        g2.setColor(BOXES);
        for (Box box : boxes) drawEntity(g2, box.locationInfo.now, box.boxInfo.collect);

        g2.setColor(NPCS);
        for (Npc npc : npcs) drawEntity(g2, npc.locationInfo.now, npc.npcInfo.kill);

        for (Ship ship : ships) {
            Location loc = ship.locationInfo.now;
            g2.setColor(ship.playerInfo.isEnemy() ? ENEMIES : ALLIES);
            drawEntity(g2, ship.locationInfo.now, false);
            if (config.MISCELLANEOUS.DISPLAY.SHOW_NAMES)
                drawString(g2, ship.playerInfo.username, translateX(loc.x), translateY(loc.y) - 5, Align.MID);
        }

        if (hero.target != null && !hero.target.removed) {
            g2.setColor(GOING);
            Location now = hero.target.locationInfo.now, later = hero.target.locationInfo.destinationInTime(200);
            drawLine(g2, now.x, now.y, later.x, later.y);
            g2.setColor(TARGET);
            drawEntity(g2, hero.target.locationInfo.now, true);
        }

        if (!config.MISCELLANEOUS.DEV_STUFF) return;

        g2.setColor(UNKNOWN);
        for (Entity entity : mapManager.entities.unknown) {
            drawEntity(g2, entity.locationInfo.now, false);
        }

        for (PathPoint point : hero.drive.pathFinder.points) {
            g2.fillRect(translateX(point.x), translateY(point.y), 2, 2);
        }
    }

    private void drawHero(Graphics2D g2) {
        g2.setColor(TEXT);
        g2.setFont(FONT_SMALL);
        drawString(g2, hero.config + "C", 12, height - 12, Align.LEFT);

        if (!hero.locationInfo.isLoaded()) return;

        g2.setColor(GOING);
        PathPoint begin = new PathPoint((int) hero.locationInfo.now.x, (int) hero.locationInfo.now.y);
        for (PathPoint path : drive.paths) {
            g2.drawLine(translateX(begin.x), translateY(begin.y),
                    translateX(path.x), translateY((begin = path).y));
        }

        g2.setColor(OWNER);

        Location loc = hero.locationInfo.now;
        g2.fillOval(translateX(loc.x) - 3, translateY(loc.y) - 3, 7, 7);

        g2.setColor(BARRIER_BORDER);
        g2.drawRect(translateX(mapManager.boundX), translateY(mapManager.boundY),
                translateX(mapManager.boundMaxX - mapManager.boundX),
                translateY(mapManager.boundMaxY - mapManager.boundY));

        if (hero.pet.removed || !hero.pet.locationInfo.isLoaded()) return;
        loc = hero.pet.locationInfo.now;

        int x = translateX(loc.x),
                y = translateY(loc.y);

        g2.setColor(PET);
        g2.fillRect(x - 3, y - 3, 6, 6);

        g2.setColor(PET_IN);
        g2.fillRect(x - 2, y - 2, 4, 4);
    }

    private void drawActionButton(Graphics2D g2) {
        g2.setColor(this.DARKEN_BACK);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        g2.setColor(this.ACTION_BUTTON);
        int height2 = this.getHeight() / 2, height3 = this.getHeight() / 3,
                width3 = this.getWidth() / 3, width9 = this.getWidth() / 9;
        if (this.main.isRunning()) {
            g2.fillRect(width3, height3, width9, height3); // Two vertical parallel lines
            g2.fillRect((width3 * 2) - width9, height3, width9, height3);
        } else { // A "play" triangle
            g2.fillPolygon(new int[]{width3, width3 * 2, width3},
                    new int[]{height3, height2, height3 * 2}, 3);
        }
    }

    private void drawStats(Graphics2D g2, String... stats) {
        int top = getHeight() / 2 - (stats.length * 15) / 2,
                width = Arrays.stream(stats).mapToInt(g2.getFontMetrics()::stringWidth).max().orElse(0);
        g2.setColor(STATS_BACKGROUND);
        g2.fillRect(0, top, width + 5, stats.length * 15 + 3);
        g2.setColor(TEXT);
        g2.setFont(FONT_SMALL);
        for (String str : stats) g2.drawString(str, 2, top += 15);
    }

    protected void drawCustomZone(Graphics2D g2, ZoneInfo zoneInfo) {
        if (zoneInfo == null) return;
        for (int x = 0; x < zoneInfo.resolution; x++) {
            for (int y = 0; y < zoneInfo.resolution; y++) {
                if (!zoneInfo.get(x, y)) continue;
                int startX = gridToMapX(x), startY = gridToMapY(y);
                g2.fillRect(startX, startY, gridToMapX(x + 1) - startX, gridToMapY(y + 1) - startY);
            }
        }
    }

    protected void drawCustomZonePath(Graphics2D g2, ZoneInfo zoneInfo) {
        if (zoneInfo == null) return;
        List<ZoneInfo.Zone> zones = zoneInfo.getSortedZones();
        for (int i = 0; i < zones.size(); i++) {
            Location loc1 = zones.get(i).innerPoint(0.5, 0.5, MapManager.internalWidth, MapManager.internalHeight);
            Location loc2 = zones.get((i + 1) % zones.size()).innerPoint(0.5, 0.5,MapManager.internalWidth, MapManager.internalHeight);
            drawLine(g2, loc1.x, loc1.y, loc2.x, loc2.y);
        }
    }

    protected void drawSafeZone(Graphics2D g2, SafetyInfo safetyInfo) {
        if (safetyInfo == null) return;
        int radius = safetyInfo.diameter / 2;
        g2.fillOval(translateX(safetyInfo.x - radius), translateY(safetyInfo.y - radius),
                translateX(safetyInfo.diameter), translateY(safetyInfo.diameter));
    }

    private void drawHealth(Graphics2D g2, Health health, int x, int y, int width) {
        int height = 12;
        g2.setColor(HEALTH.darker());
        g2.fillRect(x, y, width, height);
        g2.setColor(SHIELD.darker());
        g2.fillRect(x, y + height, width, height);
        g2.setColor(HEALTH);
        g2.fillRect(x, y, (int) (health.hpPercent() * width), height);
        g2.setColor(SHIELD);
        g2.fillRect(x, y + height, (int) (health.shieldPercent() * width), height);
        g2.setColor(TEXT);
        g2.setFont(FONT_SMALL);
        drawString(g2, health.hp + "/" + health.maxHp, x + width / 2, y + height - 2, Align.MID);
        drawString(g2, health.shield + "/" + health.maxShield, x + width / 2, y + height + height - 2, Align.MID);
    }

    protected enum Align {
        LEFT, MID, RIGHT
    }
    protected void drawString(Graphics2D g2, String str, int x, int y, Align align) {
        if (str == null || str.isEmpty()) return;
        if (align != Align.LEFT) {
            int strWidth = g2.getFontMetrics().stringWidth(str);
            x -= strWidth >> (align == Align.MID ? 1 : 0);
        }
        g2.drawString(str, x, y);
    }

    private void drawLine(Graphics2D g2, double x1, double y1, double x2, double y2) {
        g2.drawLine(translateX(x1), translateY(y1), translateX(x2), translateY(y2));
    }

    private void drawEntity(Graphics2D g2, Location loc, boolean fill) {
        int x = this.translateX(loc.x) - 1;
        int y = this.translateY(loc.y) - 1;
        if (fill) g2.fillRect(x, y, 4, 4);
        else g2.drawRect(x, y, 3, 3);
    }

    protected int translateX(double x) {
        return (int) ((x / (double) MapManager.internalWidth) * getWidth());
    }

    protected int translateY(double y) {
        return (int) ((y / (double) MapManager.internalHeight) * getHeight());
    }

    protected double undoTranslateX(double x) {
        return ((x / (double) getWidth()) * MapManager.internalWidth);
    }

    protected double undoTranslateY(double y) {
        return ((y / (double) getHeight()) * MapManager.internalHeight);
    }

    protected int gridToMapX(int x) {
        return x * width / config.MISCELLANEOUS.ZONE_RESOLUTION;
    }

    protected int gridToMapY(int y) {
        return y * height / config.MISCELLANEOUS.ZONE_RESOLUTION;
    }

    protected int mapToGridX(int x) {
        return (x + 1) * config.MISCELLANEOUS.ZONE_RESOLUTION / width;
    }

    protected int mapToGridY(int y) {
        return (y + 1) * config.MISCELLANEOUS.ZONE_RESOLUTION / height;
    }
}
