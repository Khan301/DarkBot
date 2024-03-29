package com.github.khan301.darkbot.core.manager;

import com.github.khan301.darkbot.config.types.suppliers.OptionList;
import com.github.khan301.darkbot.core.entities.Portal;
import com.github.khan301.darkbot.core.objects.LocationInfo;
import com.github.khan301.darkbot.core.objects.Map;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StarManager {
    private static StarManager INSTANCE;

    private static int INVALID_MAP_ID = -999;

    private final Graph<Map, Portal> starSystem;

    public StarManager() {
        INSTANCE = this;
        String[] HOME_MAPS = new String[]{"1-1", "2-1", "3-1"};

        StarBuilder mapBuild = new StarBuilder();
        mapBuild.addMap(-1, "Loading")
                .addMap(-2, "Home Map").addPortal(0, 0, "1-1").addPortal(0, 0, "2-1").addPortal(0, 0, "3-1");
                // MMO
        mapBuild.addMap(1, "1-1").addPortal(18500, 11500, "1-2").addPortal(10500, 6750, "Experiment Zone 1")
                .addMap(2, "1-2").addPortal(2000, 2000, "1-1").addPortal(18500, 2000, "1-3").addPortal(18500, 11500, "1-4")
                .addMap(3, "1-3").addPortal(2000, 11500, "1-2").addPortal(18500, 11500, "1-4").addPortal(18500, 2000, "2-3")
                .addMap(4, "1-4").addPortal(2000, 2000, "1-2").addPortal(18500, 2000, "1-3").addPortal(19000, 6000, "4-1").addPortal(18500, 11500, "3-4")
                .addMap(17, "1-5").addPortal(19000, 6000, "4-4").addPortal(10000, 12000, "4-5").addPortal(2000, 2000, "1-6").addPortal(2000, 11500, "1-7").addPortal(10500, 6750, "Experiment Zone 2-1")
                .addMap(18, "1-6").addPortal(18500, 11500, "1-5").addPortal(2000, 11500, "1-8")
                .addMap(19, "1-7").addPortal(2000, 2000, "1-8").addPortal(18500, 2000, "1-5")
                .addMap(20, "1-8").addPortal(18500, 2000, "1-6").addPortal(18500, 11500, "1-7").addPortal(11084, 11084, "1BL");
                // EIC
        mapBuild.addMap(5, "2-1").addPortal(2000, 11500, "2-2").addPortal(10500, 6750, "Experiment Zone 1")
                .addMap(6, "2-2").addPortal(2000, 11500, "2-3").addPortal(18500, 11500, "2-4").addPortal(18500, 2000, "2-1")
                .addMap(7, "2-3").addPortal(2000, 11500, "1-3").addPortal(18500, 11500, "2-4").addPortal(18500, 2000, "2-2")
                .addMap(8, "2-4").addPortal(2000, 2000, "2-2").addPortal(18500, 2000, "2-3").addPortal(2000, 11500, "3-3").addPortal(10000, 12000, "4-2")
                .addMap(21, "2-5").addPortal(2000, 11500, "4-4").addPortal(18500, 11500, "4-5").addPortal(2000, 2000, "2-6").addPortal(18500, 2000, "2-7").addPortal(10500, 6750, "Experiment Zone 2-2")
                .addMap(22, "2-6").addPortal(2000, 11500, "2-5").addPortal(18500, 2000, "2-8")
                .addMap(23, "2-7").addPortal(2000, 11500, "2-5").addPortal(18500, 2000, "2-8")
                .addMap(24, "2-8").addPortal(2000, 11500, "2-6").addPortal(18500, 11500, "2-7").addPortal(11084, 11084, "2BL");
                // VRU
        mapBuild.addMap(9, "3-1").addPortal(2000, 2000, "3-2").addPortal(10500, 6750, "Experiment Zone 1")
                .addMap(10, "3-2").addPortal(18500, 2000, "3-3").addPortal(2000, 2000, "3-4").addPortal(18500, 11500, "3-1")
                .addMap(11, "3-3").addPortal(2000, 2000, "2-4").addPortal(2000, 11500, "3-4").addPortal(18500, 11500, "3-2")
                .addMap(12, "3-4").addPortal(2000, 2000, "1-4").addPortal(10000, 1500, "4-3").addPortal(18500, 2000, "3-3").addPortal(18500, 11500, "3-2")
                .addMap(25, "3-5").addPortal(2000, 2000, "4-4").addPortal(16500, 1500, "4-5").addPortal(2000, 11500, "3-6").addPortal(18500, 11500, "3-7").addPortal(10500, 6750, "Experiment Zone 2-3")
                .addMap(26, "3-6").addPortal(2000, 2000, "3-5").addPortal(18500, 11500, "3-8")
                .addMap(27, "3-7").addPortal(2000, 11500, "3-5").addPortal(18500, 11500, "3-8")
                .addMap(28, "3-8").addPortal(2000, 2000, "3-7").addPortal(2000, 11500, "3-6").addPortal(11084, 11084, "3BL");
                // B-MAPS
        mapBuild.addMap(13, "4-1").addPortal(1500, 6000, "1-4").addPortal(18500, 2000, "4-2").addPortal(18500, 11500, "4-3").addPortal(10500, 6750, "4-4")
                .addMap(14, "4-2").addPortal(10000, 1500, "2-4").addPortal(2000, 11500, "4-1").addPortal(18500, 11500, "4-3").addPortal(10500, 6750, "4-4")
                .addMap(15, "4-3").addPortal(19000, 6000, "3-4").addPortal(2000, 2000, "4-2").addPortal(2000, 11500, "4-1").addPortal(10500, 6750, "4-4")
                .addMap(16, "4-4").addPortal(7000, 13500, "1-5").addPortal(28000, 1376, "2-5").addPortal(28000, 25124, "3-5").addPortal(19200, 13500, "4-1").addPortal(21900, 11941, "4-2").addPortal(21900, 14559, "4-3")
                .addMap(29, "4-5").addPortal(7000, 13500, "1-5").addPortal(28000, 1376, "2-5").addPortal(28000, 25624, "3-5").addPortal(12200, 13300, "5-1").addPortal(25000, 6300, "5-1").addPortal(25000, 20700, "5-1");
                // Pirates
        mapBuild.addMap(91, "5-1").addPortal(5200, 6800, "5-2").addPortal(2900, 13500, "5-2").addPortal(5200, 20600, "5-2")
                .addMap(92, "5-2").addPortal(2800, 3600, "5-3").addPortal(1300, 6750, "5-3").addPortal(2800, 10900, "5-3")
                .addMap(93, "5-3").addPortal(2000, 9500, "4-4", 1).addPortal(2000, 13500, "4-4", 2).addPortal(2000, 17500, "4-4", 3);
                // BL
        mapBuild.addMap(306, "1BL").addPortal( 786, 11458, "1-8").addPortal( 7589,  1456, "2BL").addPortal(20072, 11732, "3BL")
                .addMap(307, "2BL").addPortal(9893,   862, "1BL").addPortal(  593,  5884, "2-8").addPortal(20377,  7996, "3BL")
                .addMap(308, "3BL").addPortal(1545, 12210, "1BL").addPortal(19400, 11854, "2BL").addPortal(14027,  3181, "3-8");
                // EX
        mapBuild.addMap(401, "Experiment Zone 1").addPortal(1200, 1100, "Home Map")
                .addMap(402, "Experiment Zone 2-1").addPortal(1200, 1100, "1-5")
                .addMap(403, "Experiment Zone 2-2").addPortal(1200, 1100, "2-5")
                .addMap(404, "Experiment Zone 2-3").addPortal(1200, 1100, "3-5");
                // GG
        mapBuild.addGG(51, "GG α").accessBy(2, HOME_MAPS)
                .addGG(52, "GG β").accessBy(3, HOME_MAPS)
                .addGG(53, "GG γ").accessBy(4, HOME_MAPS)
                .addGG(54, "GG NC") // New Client GG        (No access)
                .addGG(55, "GG δ").accessBy(5, HOME_MAPS)
                .addGG(56, "GG Orb")// The forgotten gate   (No access)
                .addGG(57, "GG Y6").accessBy(16, HOME_MAPS) // Year 6 (Anniversary)
                .addGG(57, "HSG")   // High Score Gate      (No access)
                .addGG(70, "GG ε").accessBy(53, HOME_MAPS)
                .addGG(71, "GG ζ 1").accessOnlyBy(54, HOME_MAPS)
                .addGG(72, "GG ζ 2").accessOnlyBy(54, "GG ζ 1")
                .addGG(73, "GG ζ 3").accessBy(54, "GG ζ 2")
                .addGG(74, "GG κ").accessBy(70, HOME_MAPS)
                .addGG(75, "GG λ").accessBy(71, HOME_MAPS)
                .addGG(76, "GG Kronos").accessBy(72, HOME_MAPS)
                .addGG(77, "GG Cold Wave (Easy)").accessBy(73, "1-4", "2-4", "3-4")
                .addGG(78, "GG Cold Wave (Hard)").accessBy(73, "1-4", "2-4", "3-4")
                .addGG(203, "GG Hades").accessBy(74, HOME_MAPS)
                .addGG(223, "Devolarium Attack")    // (No access), missing type ID (HOME_MAPS)
                .addGG(225, "GG PET Attack (easy)") // (No access)
                .addGG(226, "GG PET Attack (hard)") // (No access)
                .addGG(228, "Permafrost Fissure")   // (No access), missing type ID (HOME_MAPS)
                .addGG(300, "GG ς 1").accessOnlyBy(82, HOME_MAPS)
                .addGG(301, "GG ς 2").accessOnlyBy(82, "GG ς 1")
                .addGG(302, "GG ς 3").accessOnlyBy(82, "GG ς 2")
                .addGG(303, "GG ς 4").accessOnlyBy(82, "GG ς 3")
                .addGG(304, "GG ς 5").accessBy(82, "GG ς 4")
                .addGG(200, "LoW").accessOnlyBy(34, "1-3", "2-3", "3-3")
                .addGG(229, "Quarantine Zone").accessOnlyBy(84, "1-7", "2-7", "3-7")
                .addGG(227, "GG VoT 1").accessOnlyBy(54, "1-4", "2-4", "3-4") // Assume it's the same type as zeta?
                .addGG(230, "GG VoT 2").accessOnlyBy(54, "GG VoT 1")
                .addGG(231, "GG VoT 3").accessOnlyBy(54, "GG VoT 2")
                .addGG(232, "GG VoT 4").accessOnlyBy(54, "GG VoT 3")
                .addGG(233, "GG VoT 5").accessOnlyBy(54, "GG VoT 4")
                .addGG(234, "GG VoT 6").accessOnlyBy(54, "GG VoT 5")
                .addGG(235, "GG VoT 7").accessOnlyBy(54, "GG VoT 6")
                .addGG(236, "GG VoT 8").accessBy(54, "GG VoT 7")
                .addGG(305, "Compromising Invasion") // (No access)
                .addGG(410, "GoP 1").accessOnlyBy(24, "1-8", "2-8", "3-8")
                .addGG(411, "GoP 2").accessOnlyBy(24, "GoP 1")
                .addGG(412, "GoP 3").accessOnlyBy(24, "GoP 2")
                .addGG(413, "GoP 4").accessOnlyBy(24, "GoP 3")
                .addGG(414, "GoP 5").accessOnlyBy(24, "GoP 4")
                .addGG(415, "GoP Final").accessBy(24, "GoP 5");
                // Special (No direct access)
        mapBuild.addMap(42, "???")
                .addMap(61, "MMO Invasion").addMap(62, "EIC Invasion").addMap(63, "VRU Invasion")
                .addMap(64, "MMO Invasion").addMap(65, "EIC Invasion").addMap(66, "VRU Invasion")
                .addMap(67, "MMO Invasion").addMap(68, "EIC Invasion").addMap(69, "VRU Invasion")
                .addMap(81, "TDM I").addMap(82, "TDM II")
                .addMap(101, "JP").addMap(101, "JP").addMap(102, "JP").addMap(103, "JP")
                .addMap(104, "JP").addMap(105, "JP").addMap(106, "JP").addMap(107, "JP")
                .addMap(108, "JP").addMap(109, "JP").addMap(110, "JP").addMap(111, "JP")
                .addMap(112, "UBA").addMap(113, "UBA").addMap(114, "UBA").addMap(115, "UBA").addMap(116, "UBA")
                .addMap(117, "UBA").addMap(118, "UBA").addMap(119, "UBA").addMap(120, "UBA").addMap(121, "UBA")
                .addMap(201, "SC-1").addMap(201, "SC-2") // Sector Control
                .addMap(224, "Custom Tournament")
                .addMap(150, "R-Zone 1").addMap(151, "R-Zone 2")
                .addMap(152, "R-Zone 3").addMap(153, "R-Zone 4")
                .addMap(154, "R-Zone 5").addMap(155, "R-Zone 6")
                .addMap(156, "R-Zone 7").addMap(157, "R-Zone 8")
                .addMap(158, "R-Zone 9").addMap(159, "R-Zone 10")
                .addMap(420, "WarGame 1").addMap(421, "WarGame 2").addMap(422, "WarGame 3")
                .addMap(423, "WarGame 4").addMap(423, "WarGame 5").addMap(423, "WarGame 6");

        starSystem = mapBuild.build();
    }

    public Portal getOrCreate(int id, int type, int x, int y) {
        return starSystem.outgoingEdgesOf(HeroManager.instance.map).stream()
                .filter(p -> p.matches(x, y, type))
                .peek(p -> p.id = id)
                .findAny().orElse(new Portal(id, type, x, y));
    }

    public Portal next(HeroManager hero, Map target) {
        DijkstraShortestPath<Map, Portal> path = new DijkstraShortestPath<>(starSystem);
        return starSystem.outgoingEdgesOf(hero.map).stream().filter(p -> !p.removed).min(
                Comparator.<Portal>comparingDouble(p -> path.getPaths(p.target).getWeight(target))
                        .thenComparing(p -> p.factionId == -1 ? 0 : p.factionId == hero.playerInfo.factionId ? -1 : 1)
                        .thenComparing(p -> hero.locationInfo.distance(p.locationInfo))).orElse(null);
    }

    public Map byName(String name) {
        return starSystem.vertexSet().stream().filter(m -> m.name.equals(name)).findAny()
                .orElseGet(() -> addMap(new Map(--INVALID_MAP_ID, name, false, false)));
    }

    public Stream<Map> mapSet() {
        return starSystem.vertexSet().stream();
    }

    public Map byId(int id) {
        return starSystem.vertexSet().stream().filter(m -> m.id == id).findAny()
                .orElseGet(() -> addMap(new Map(id, "Unknown map " + id, false, false)));
    }

    private Map addMap(Map map) {
        starSystem.addVertex(map);
        return map;
    }

    public List<String> getAccessibleMaps() {
        return starSystem.vertexSet().stream()
                .filter(m -> !m.gg && m.id > 0)
                .filter(m -> starSystem.inDegreeOf(m) > 0)
                .map(m -> m.name).sorted().collect(Collectors.toList());
    }

    public Collection<String> getGGMaps() {
        return starSystem.vertexSet().stream()
                .filter(m -> m.gg)
                .filter(m -> starSystem.inDegreeOf(m) > 0 &&
                        (starSystem.outDegreeOf(m) == 0 || starSystem.containsEdge(m, m)))
                .map(m -> m.name)
                .sorted().collect(Collectors.toList());
    }

    public static Collection<Map> getAllMaps() {
        return INSTANCE.starSystem.vertexSet();
    }

    public static class MapList extends OptionList<Integer> {

        @Override
        public Integer getValue(String text) {
            return INSTANCE.byName(text).id;
        }

        @Override
        public String getText(Integer value) {
            return INSTANCE.byId(value).name;
        }

        @Override
        public List<String> getOptions() {
            return INSTANCE.getAccessibleMaps();
        }
    }

}
