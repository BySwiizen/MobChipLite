package me.gamercoder215.mobchip.abstraction.v1_21_R3;

import me.gamercoder215.mobchip.ai.gossip.GossipType;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Difficulty;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TestChipUtil1_21_R3 {

    @BeforeAll
    public static void init() throws Exception {
        Constructor<DetectedVersion> versionC = DetectedVersion.class.getDeclaredConstructor();
        versionC.setAccessible(true);
        DetectedVersion v = versionC.newInstance();
        SharedConstants.setVersion(v);

        Bootstrap.bootStrap();
    }

    @Test
    @DisplayName("Test Bukkit-NMS Conversion")
    public void testNMSConversion() {
        // Classes
        List<EntityType> livingTypes = Arrays.stream(EntityType.values())
                .filter(EntityType::isAlive)
                .filter(e -> Mob.class.isAssignableFrom(e.getEntityClass()))
                .toList();

        for (EntityType t : livingTypes)
            Assertions.assertNotNull(ChipUtil1_21_R3.toNMS(t.getEntityClass().asSubclass(Mob.class)));

        // Other
        for (Difficulty d : Difficulty.values()) Assertions.assertNotNull(ChipUtil1_21_R3.toNMS(d));
        for (GossipType t : GossipType.values()) Assertions.assertNotNull(ChipUtil1_21_R3.toNMS(t));

        Assertions.assertNotNull(ChipUtil1_21_R3.toNMS(m -> m.damage(2)));
    }

    @Test
    @DisplayName("Test NMS-Bukkit Conversion")
    public void testBukkitConversion() {
        // Other
        for (net.minecraft.world.Difficulty d : net.minecraft.world.Difficulty.values()) Assertions.assertNotNull(ChipUtil1_21_R3.fromNMS(d));
        for (net.minecraft.world.entity.ai.gossip.GossipType t : net.minecraft.world.entity.ai.gossip.GossipType.values()) Assertions.assertNotNull(ChipUtil1_21_R3.fromNMS(t));
    }

    @Test
    @DisplayName("Test ChipUtil1_21_R3#getFlags")
    public void testGetFlags() {
        OptimizedSmallEnumSet1_21_R3<Goal.Flag> set = new OptimizedSmallEnumSet1_21_R3<>(Goal.Flag.class);
        set.addUnchecked(Goal.Flag.MOVE);
        set.addUnchecked(Goal.Flag.LOOK);

        Assertions.assertTrue(set.hasElement(Goal.Flag.MOVE));
        Assertions.assertTrue(set.hasElement(Goal.Flag.LOOK));

        Set<Goal.Flag> flags = ChipUtil1_21_R3.getFlags(set.getBackingSet());
        Assertions.assertTrue(flags.contains(Goal.Flag.MOVE));
        Assertions.assertTrue(flags.contains(Goal.Flag.LOOK));
    }

}
