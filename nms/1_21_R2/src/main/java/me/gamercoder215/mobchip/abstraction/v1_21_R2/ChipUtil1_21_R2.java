package me.gamercoder215.mobchip.abstraction.v1_21_R2;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Lifecycle;
import me.gamercoder215.mobchip.EntityBody;
import me.gamercoder215.mobchip.abstraction.ChipUtil;
import me.gamercoder215.mobchip.ai.attribute.Attribute;
import me.gamercoder215.mobchip.ai.attribute.AttributeInstance;
import me.gamercoder215.mobchip.ai.behavior.BehaviorResult;
import me.gamercoder215.mobchip.ai.controller.EntityController;
import me.gamercoder215.mobchip.ai.enderdragon.CustomPhase;
import me.gamercoder215.mobchip.ai.enderdragon.DragonPhase;
import me.gamercoder215.mobchip.ai.goal.*;
import me.gamercoder215.mobchip.ai.goal.target.*;
import me.gamercoder215.mobchip.ai.gossip.EntityGossipContainer;
import me.gamercoder215.mobchip.ai.gossip.GossipType;
import me.gamercoder215.mobchip.ai.memories.EntityMemory;
import me.gamercoder215.mobchip.ai.memories.Memory;
import me.gamercoder215.mobchip.ai.memories.MemoryStatus;
import me.gamercoder215.mobchip.ai.memories.Unit;
import me.gamercoder215.mobchip.ai.navigation.EntityNavigation;
import me.gamercoder215.mobchip.ai.schedule.Activity;
import me.gamercoder215.mobchip.ai.schedule.EntityScheduleManager;
import me.gamercoder215.mobchip.ai.schedule.Schedule;
import me.gamercoder215.mobchip.ai.sensing.EntitySenses;
import me.gamercoder215.mobchip.ai.sensing.Sensor;
import me.gamercoder215.mobchip.combat.CombatEntry;
import me.gamercoder215.mobchip.combat.CombatLocation;
import me.gamercoder215.mobchip.combat.EntityCombatTracker;
import me.gamercoder215.mobchip.nbt.EntityNBT;
import me.gamercoder215.mobchip.util.Registration;
import net.minecraft.core.Registry;
import net.minecraft.core.*;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.FallLocation;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.allay.AllayAi;
import net.minecraft.world.entity.animal.axolotl.AxolotlAi;
import net.minecraft.world.entity.animal.camel.CamelAi;
import net.minecraft.world.entity.animal.frog.FrogAi;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.phases.*;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.schedule.ScheduleBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_21_R2.CraftServer;
import org.bukkit.craftbukkit.v1_21_R2.CraftSound;
import org.bukkit.craftbukkit.v1_21_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R2.entity.*;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R2.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_21_R2.util.CraftNamespacedKey;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cod;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.Fox;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Salmon;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Squid;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Turtle;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

import static org.bukkit.entity.Villager.Profession.*;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.*;

@SuppressWarnings({"rawtypes", "unchecked", "deprecation", "UnstableApiUsage"})
final class ChipUtil1_21_R2 implements ChipUtil {

    @Override
    public void addCustomPathfinder(CustomPathfinder p, int priority, boolean target) {
        Mob m = p.getEntity();
        net.minecraft.world.entity.Mob mob = toNMS(m);
        GoalSelector s = target ? mob.targetSelector : mob.goalSelector;
        Goal g = custom(p);
        Set<Goal.Flag> nms = ChipUtil1_21_R2.getFlags(g);

        Pathfinder.PathfinderFlag[] flags = p.getFlags() == null ? new Pathfinder.PathfinderFlag[0] : p.getFlags();
        for (Pathfinder.PathfinderFlag f : flags) {
            EnumSet<Goal.Flag> nmsFlags = nms == null ? EnumSet.allOf(Goal.Flag.class) : EnumSet.copyOf(nms);
            nmsFlags.add(toNMS(f));
            g.setFlags(nmsFlags);
        }

        s.addGoal(priority, g);
    }

    @Override
    public Set<WrappedPathfinder> getGoals(Mob m, boolean target) {
        net.minecraft.world.entity.Mob mob = toNMS(m);
        GoalSelector s = target ? mob.targetSelector : mob.goalSelector;

        Set<WrappedPathfinder> pF = new HashSet<>();
        s.getAvailableGoals().forEach(w -> pF.add(new WrappedPathfinder(fromNMS(w.getGoal()), w.getPriority())));

        return pF;
    }

    @Override
    public Collection<WrappedPathfinder> getRunningGoals(Mob m, boolean target) {
        net.minecraft.world.entity.Mob mob = toNMS(m);
        GoalSelector s = target ? mob.targetSelector : mob.goalSelector;

        Collection<WrappedPathfinder> l = new HashSet<>();
        s.getAvailableGoals().stream().filter(WrappedGoal::isRunning).forEach(w ->
                l.add(new WrappedPathfinder(fromNMS(w.getGoal()), w.getPriority())));

        return l;
    }

    @Override
    public void setFlag(Mob m, Pathfinder.PathfinderFlag flag, boolean target, boolean value) {
        net.minecraft.world.entity.Mob mob = toNMS(m);
        GoalSelector s = target ? mob.targetSelector : mob.goalSelector;
        if (value) s.enableControlFlag(toNMS(flag)); else s.disableControlFlag(toNMS(flag));
    }

    public static final Map<Class<? extends LivingEntity>, Class<? extends net.minecraft.world.entity.LivingEntity>> BUKKIT_NMS_MAP = ImmutableMap.<Class<? extends LivingEntity>, Class<? extends net.minecraft.world.entity.LivingEntity>>builder()
            .put(LivingEntity.class, net.minecraft.world.entity.LivingEntity.class) // EntityLiving
            .put(Mob.class, net.minecraft.world.entity.Mob.class) // EntityInsentient
            .put(Tameable.class, TamableAnimal.class) // EntityTameableAnimal

            // Below are not in the root package (LET'S KEEP THEM ALPHABETICAL!!!)
            .put(AbstractHorse.class, net.minecraft.world.entity.animal.horse.AbstractHorse.class)
            .put(AbstractVillager.class, net.minecraft.world.entity.npc.AbstractVillager.class)
            .put(Animals.class, Animal.class)
            .put(Allay.class, net.minecraft.world.entity.animal.allay.Allay.class)
            .put(Ambient.class, AmbientCreature.class)
            .put(Armadillo.class, net.minecraft.world.entity.animal.armadillo.Armadillo.class)
            .put(Axolotl.class, net.minecraft.world.entity.animal.axolotl.Axolotl.class)
            .put(Bat.class, net.minecraft.world.entity.ambient.Bat.class)
            .put(Bee.class, net.minecraft.world.entity.animal.Bee.class)
            .put(Blaze.class, net.minecraft.world.entity.monster.Blaze.class)
            .put(Bogged.class, net.minecraft.world.entity.monster.Bogged.class)
            .put(Breeze.class, net.minecraft.world.entity.monster.breeze.Breeze.class)
            .put(Cat.class, net.minecraft.world.entity.animal.Cat.class)
            .put(Camel.class, net.minecraft.world.entity.animal.camel.Camel.class)
            .put(CaveSpider.class, net.minecraft.world.entity.monster.CaveSpider.class)
            .put(Chicken.class, net.minecraft.world.entity.animal.Chicken.class)
            .put(Cod.class, net.minecraft.world.entity.animal.Cod.class)
            .put(Cow.class, net.minecraft.world.entity.animal.Cow.class)
            .put(Creaking.class, net.minecraft.world.entity.monster.creaking.Creaking.class)
            .put(CreakingTransient.class, net.minecraft.world.entity.monster.creaking.CreakingTransient.class)
            .put(Creeper.class, net.minecraft.world.entity.monster.Creeper.class)
            .put(Dolphin.class, net.minecraft.world.entity.animal.Dolphin.class)
            .put(Donkey.class, net.minecraft.world.entity.animal.horse.Donkey.class)
            .put(Drowned.class, net.minecraft.world.entity.monster.Drowned.class)
            .put(ElderGuardian.class, net.minecraft.world.entity.monster.ElderGuardian.class)
            .put(EnderDragon.class, net.minecraft.world.entity.boss.enderdragon.EnderDragon.class)
            .put(Enderman.class, EnderMan.class)
            .put(Endermite.class, net.minecraft.world.entity.monster.Endermite.class)
            .put(Evoker.class, net.minecraft.world.entity.monster.Evoker.class)
            .put(Fish.class, AbstractFish.class)
            .put(Fox.class, net.minecraft.world.entity.animal.Fox.class)
            .put(Frog.class, net.minecraft.world.entity.animal.frog.Frog.class)
            .put(Ghast.class, net.minecraft.world.entity.monster.Ghast.class)
            .put(Giant.class, net.minecraft.world.entity.monster.Giant.class)
            .put(Goat.class, net.minecraft.world.entity.animal.goat.Goat.class)
            .put(Golem.class, AbstractGolem.class)
            .put(Guardian.class, net.minecraft.world.entity.monster.Guardian.class)
            .put(Hoglin.class, net.minecraft.world.entity.monster.hoglin.Hoglin.class)
            .put(Horse.class, net.minecraft.world.entity.animal.horse.Horse.class)
            .put(HumanEntity.class, net.minecraft.world.entity.player.Player.class)
            .put(Husk.class, net.minecraft.world.entity.monster.Husk.class)
            .put(Illager.class, AbstractIllager.class)
            .put(Illusioner.class, net.minecraft.world.entity.monster.Illusioner.class)
            .put(IronGolem.class, net.minecraft.world.entity.animal.IronGolem.class)
            .put(Llama.class, net.minecraft.world.entity.animal.horse.Llama.class)
            .put(MagmaCube.class, net.minecraft.world.entity.monster.MagmaCube.class)
            .put(Mule.class, net.minecraft.world.entity.animal.horse.Mule.class)
            .put(MushroomCow.class, net.minecraft.world.entity.animal.MushroomCow.class)
            .put(Ocelot.class, net.minecraft.world.entity.animal.Ocelot.class)
            .put(Panda.class, net.minecraft.world.entity.animal.Panda.class)
            .put(Parrot.class, net.minecraft.world.entity.animal.Parrot.class)
            .put(Phantom.class, net.minecraft.world.entity.monster.Phantom.class)
            .put(Pig.class, net.minecraft.world.entity.animal.Pig.class)
            .put(Piglin.class, net.minecraft.world.entity.monster.piglin.Piglin.class)
            .put(PiglinBrute.class, net.minecraft.world.entity.monster.piglin.PiglinBrute.class)
            .put(PigZombie.class, ZombifiedPiglin.class)
            .put(Pillager.class, net.minecraft.world.entity.monster.Pillager.class)
            .put(Player.class, net.minecraft.world.entity.player.Player.class)
            .put(PolarBear.class, net.minecraft.world.entity.animal.PolarBear.class)
            .put(PufferFish.class, Pufferfish.class)
            .put(Rabbit.class, net.minecraft.world.entity.animal.Rabbit.class)
            .put(Raider.class, net.minecraft.world.entity.raid.Raider.class)
            .put(Ravager.class, net.minecraft.world.entity.monster.Ravager.class)
            .put(Salmon.class, net.minecraft.world.entity.animal.Salmon.class)
            .put(Sheep.class, net.minecraft.world.entity.animal.Sheep.class)
            .put(Shulker.class, net.minecraft.world.entity.monster.Shulker.class)
            .put(Silverfish.class, net.minecraft.world.entity.monster.Silverfish.class)
            .put(Skeleton.class, net.minecraft.world.entity.monster.Skeleton.class)
            .put(SkeletonHorse.class, net.minecraft.world.entity.animal.horse.SkeletonHorse.class)
            .put(Slime.class, net.minecraft.world.entity.monster.Slime.class)
            .put(Sniffer.class, net.minecraft.world.entity.animal.sniffer.Sniffer.class)
            .put(Snowman.class, SnowGolem.class)
            .put(Spider.class, net.minecraft.world.entity.monster.Spider.class)
            .put(Squid.class, net.minecraft.world.entity.animal.Squid.class)
            .put(Stray.class, net.minecraft.world.entity.monster.Stray.class)
            .put(Strider.class, net.minecraft.world.entity.monster.Strider.class)
            .put(Tadpole.class, net.minecraft.world.entity.animal.frog.Tadpole.class)
            .put(TraderLlama.class, net.minecraft.world.entity.animal.horse.TraderLlama.class)
            .put(TropicalFish.class, net.minecraft.world.entity.animal.TropicalFish.class)
            .put(Turtle.class, net.minecraft.world.entity.animal.Turtle.class)
            .put(Vex.class, net.minecraft.world.entity.monster.Vex.class)
            .put(Villager.class, net.minecraft.world.entity.npc.Villager.class)
            .put(Vindicator.class, net.minecraft.world.entity.monster.Vindicator.class)
            .put(WanderingTrader.class, net.minecraft.world.entity.npc.WanderingTrader.class)
            .put(org.bukkit.entity.Warden.class, Warden.class)
            .put(WaterMob.class, WaterAnimal.class)
            .put(Witch.class, net.minecraft.world.entity.monster.Witch.class)
            .put(Wither.class, WitherBoss.class)
            .put(WitherSkeleton.class, net.minecraft.world.entity.monster.WitherSkeleton.class)
            .put(Wolf.class, net.minecraft.world.entity.animal.Wolf.class)
            .put(Zoglin.class, net.minecraft.world.entity.monster.Zoglin.class)
            .put(Zombie.class, net.minecraft.world.entity.monster.Zombie.class)
            .put(ZombieHorse.class, net.minecraft.world.entity.animal.horse.ZombieHorse.class)
            .put(ZombieVillager.class, net.minecraft.world.entity.monster.ZombieVillager.class)
            .build();

    public static Class<? extends net.minecraft.world.entity.LivingEntity> toNMS(Class<? extends LivingEntity> clazz) {
        if (BUKKIT_NMS_MAP.containsKey(clazz)) return BUKKIT_NMS_MAP.get(clazz);

        Class<? extends net.minecraft.world.entity.LivingEntity> nms = null;
        try {
            // Sometimes we can get lucky...
            nms = Class.forName(PathfinderMob.class.getPackageName() + "." + clazz.getSimpleName()).asSubclass(net.minecraft.world.entity.LivingEntity.class);
        } catch (ClassNotFoundException ignored) {
            try {
                // Some Pre-Mojang Mapping Classes start with "Entity"
                if (nms == null) nms = Class.forName(PathfinderMob.class.getPackageName() + ".Entity" + clazz.getSimpleName()).asSubclass(net.minecraft.world.entity.LivingEntity.class);
            } catch (ClassNotFoundException ignored2) {}
        }

        if (nms == null) throw new AssertionError("Could not convert " + clazz.getName() + " to NMS class");

        return nms;
    }

    public static net.minecraft.world.item.ItemStack toNMS(ItemStack i) {
        return CraftItemStack.asNMSCopy(i);
    }

    public static SoundEvent toNMS(Sound s) {
        return CraftSound.bukkitToMinecraft(s);
    }

    public static Goal toNMS(Pathfinder b) {
        Mob mob = b.getEntity();
        net.minecraft.world.entity.Mob m = toNMS(mob);

        String name = b.getInternalName().startsWith("PathfinderGoal") ? b.getInternalName().replace("PathfinderGoal", "") : b.getInternalName();

        return switch (name) {
            case "AvoidTarget" -> {
                PathfinderAvoidEntity p = (PathfinderAvoidEntity) b;
                Predicate<LivingEntity> avoidP = p.getAvoidPredicate() == null ?
                        en -> true :
                        en -> p.getAvoidPredicate().test(en);
                Predicate<LivingEntity> avoidingP = p.getAvoidingPredicate() == null ?
                        en -> true :
                        en -> p.getAvoidingPredicate().test(en);

                yield new AvoidEntityGoal<>((PathfinderMob) m, toNMS(p.getFilter()), en -> avoidP.test(fromNMS(en)), p.getMaxDistance(), p.getSpeedModifier(), p.getSprintModifier(), en -> avoidingP.test(fromNMS(en)));
            }
            case "ArrowAttack" -> {
                PathfinderRangedAttack p = (PathfinderRangedAttack) b;
                yield new RangedAttackGoal((RangedAttackMob) m, p.getSpeedModifier(), p.getMinAttackInterval(), p.getMaxAttackInterval(), p.getRange());
            }
            case "Beg" -> {
                PathfinderBeg p = (PathfinderBeg) b;
                yield new BegGoal((net.minecraft.world.entity.animal.Wolf) m, p.getRange());
            }
            case "BowShoot" -> {
                PathfinderRangedBowAttack p = (PathfinderRangedBowAttack) b;
                yield new RangedBowAttackGoal((net.minecraft.world.entity.monster.Monster) m, p.getSpeedModifier(), p.getInterval(), p.getRange());
            }
            case "BreakDoor" -> {
                PathfinderBreakDoor p = (PathfinderBreakDoor) b;
                yield new BreakDoorGoal(m, p.getBreakTime(), d -> p.getCondition().test(fromNMS(d)));
            }
            case "Breath" -> new BreathAirGoal((PathfinderMob) m);
            case "Breed" -> {
                PathfinderBreed p = (PathfinderBreed) b;
                yield new BreedGoal((Animal) m, p.getSpeedModifier());
            }
            case "CatSitOnBed" -> {
                PathfinderCatOnBed p = (PathfinderCatOnBed) b;
                yield new CatLieOnBedGoal((net.minecraft.world.entity.animal.Cat) m, p.getSpeedModifier(), Math.min((int) p.getRange(), 1));
            }
            case "ClimbOnTopOfPowderSnowGoal" -> new ClimbOnTopOfPowderSnowGoal(m, toNMS(mob.getWorld()));
            case "CrossbowAttack" -> {
                PathfinderRangedCrossbowAttack p = (PathfinderRangedCrossbowAttack) b;
                yield new RangedCrossbowAttackGoal((net.minecraft.world.entity.monster.Monster) m, p.getSpeedModifier(), p.getRange());
            }
            case "DoorOpen" -> {
                PathfinderOpenDoor p = (PathfinderOpenDoor) b;
                yield new OpenDoorGoal(m, p.mustClose());
            }
            case "EatTile" -> new EatBlockGoal(m);
            case "FishSchool" -> new FollowFlockLeaderGoal((AbstractSchoolingFish) m);
            case "FleeSun" -> {
                PathfinderFleeSun p = (PathfinderFleeSun) b;
                yield new FleeSunGoal((PathfinderMob) m, p.getSpeedModifier());
            }
            case "Float" -> new FloatGoal(m);
            case "FollowBoat" -> new FollowBoatGoal((PathfinderMob) m);
            case "FollowEntity" -> {
                PathfinderFollowMob p = (PathfinderFollowMob) b;
                yield new FollowMobGoal(m, p.getSpeedModifier(), p.getStopDistance(), p.getRange());
            }
            case "FollowOwner" -> {
                PathfinderFollowOwner p = (PathfinderFollowOwner) b;
                yield new FollowOwnerGoal((TamableAnimal) m, p.getSpeedModifier(), p.getStartDistance(), p.getStopDistance());
            }
            case "FollowParent" -> {
                PathfinderFollowParent p = (PathfinderFollowParent) b;
                yield new FollowParentGoal((Animal) m, p.getSpeedModifier());
            }
            case "JumpOnBlock" -> {
                PathfinderCatOnBlock p = (PathfinderCatOnBlock) b;
                yield new CatSitOnBlockGoal((net.minecraft.world.entity.animal.Cat) m, p.getSpeedModifier());
            }
            case "LeapAtTarget" -> {
                PathfinderLeapAtTarget p = (PathfinderLeapAtTarget) b;
                yield new LeapAtTargetGoal(m, p.getHeight());
            }
            case "LlamaFollow" -> {
                PathfinderLlamaFollowCaravan p = (PathfinderLlamaFollowCaravan) b;
                yield new LlamaFollowCaravanGoal((net.minecraft.world.entity.animal.horse.Llama) m, p.getSpeedModifier());
            }
            case "LookAtPlayer" -> {
                PathfinderLookAtEntity<?> p = (PathfinderLookAtEntity) b;
                yield new LookAtPlayerGoal(m, toNMS(p.getFilter()), p.getRange(), p.getProbability(), p.isHorizontal());
            }
            case "LookAtTradingPlayer" -> new LookAtTradingPlayerGoal((net.minecraft.world.entity.npc.AbstractVillager) m);
            case "MeleeAttack" -> {
                PathfinderMeleeAttack p = (PathfinderMeleeAttack) b;
                yield new MeleeAttackGoal((PathfinderMob) m, p.getSpeedModifier(), p.mustSee());
            }
            case "MoveThroughVillage" -> {
                PathfinderMoveThroughVillage p = (PathfinderMoveThroughVillage) b;
                yield new MoveThroughVillageGoal((PathfinderMob) m, p.getSpeedModifier(), p.mustBeNight(), p.getMinDistance(), p.canUseDoors());
            }
            case "MoveTowardsRestriction" -> {
                PathfinderMoveTowardsRestriction p = (PathfinderMoveTowardsRestriction) b;
                yield new MoveTowardsRestrictionGoal((PathfinderMob) m, p.getSpeedModifier());
            }
            case "MoveTowardsTarget" -> {
                PathfinderMoveTowardsTarget p = (PathfinderMoveTowardsTarget) b;
                yield new MoveTowardsTargetGoal((PathfinderMob) m, p.getSpeedModifier(), p.getRange());
            }
            case "NearestVillage" -> {
                PathfinderRandomStrollThroughVillage p = (PathfinderRandomStrollThroughVillage) b;
                yield new StrollThroughVillageGoal((PathfinderMob) m, p.getInterval());
            }
            case "OcelotAttack" -> new OcelotAttackGoal(m);
            case "OfferFlower" -> new OfferFlowerGoal((net.minecraft.world.entity.animal.IronGolem) m);
            case "Panic" -> {
                PathfinderPanic p = (PathfinderPanic) b;
                yield new PanicGoal((PathfinderMob) m, p.getSpeedModifier());
            }
            case "Perch" -> new LandOnOwnersShoulderGoal((ShoulderRidingEntity) m);
            case "Raid" -> new PathfindToRaidGoal<>((net.minecraft.world.entity.raid.Raider) m);
            case "RandomFly" -> {
                PathfinderRandomStrollFlying p = (PathfinderRandomStrollFlying) b;
                yield new WaterAvoidingRandomFlyingGoal((PathfinderMob) m, p.getSpeedModifier());
            }
            case "RandomLookaround" -> new RandomLookAroundGoal(m);
            case "RandomStroll" -> {
                PathfinderRandomStroll p = (PathfinderRandomStroll) b;
                yield new RandomStrollGoal((PathfinderMob) m, p.getSpeedModifier(), p.getInterval());
            }
            case "RandomStrollLand" -> {
                PathfinderRandomStrollLand p = (PathfinderRandomStrollLand) b;
                yield new WaterAvoidingRandomStrollGoal((PathfinderMob) m, p.getSpeedModifier(), p.getProbability());
            }
            case "RandomSwim" -> {
                PathfinderRandomSwim p = (PathfinderRandomSwim) b;
                yield new RandomSwimmingGoal((PathfinderMob) m, p.getSpeedModifier(), p.getInterval());
            }
            case "RemoveBlock" -> {
                PathfinderRemoveBlock p = (PathfinderRemoveBlock) b;
                yield new RemoveBlockGoal(CraftMagicNumbers.getBlock(p.getBlock()), (PathfinderMob) m, p.getSpeedModifier(), p.getVerticalSearchRange());
            }
            case "RestrictSun" -> new RestrictSunGoal((PathfinderMob) m);
            case "Sit" -> new SitWhenOrderedToGoal((TamableAnimal) m);
            case "StrollVillage" -> {
                PathfinderRandomStrollToVillage p = (PathfinderRandomStrollToVillage) b;
                yield new MoveBackToVillageGoal((PathfinderMob) m, p.getSpeedModifier(), true);
            }
            case "StrollVillageGolem" -> {
                PathfinderRandomStrollInVillage p = (PathfinderRandomStrollInVillage) b;
                yield new GolemRandomStrollInVillageGoal((PathfinderMob) m, p.getSpeedModifier());
            }
            case "Swell" -> new SwellGoal((net.minecraft.world.entity.monster.Creeper) m);
            case "Tame" -> {
                PathfinderTameHorse p = (PathfinderTameHorse) b;
                yield new RunAroundLikeCrazyGoal((net.minecraft.world.entity.animal.horse.AbstractHorse) m, p.getSpeedModifier());
            }
            case "Tempt" -> {
                PathfinderTempt p = (PathfinderTempt) b;
                yield new TemptGoal((PathfinderMob) m, p.getSpeedModifier(), toNMS(p), true);
            }
            case "TradeWithPlayer" -> new TradeWithPlayerGoal((net.minecraft.world.entity.npc.AbstractVillager) m);
            case "UseItem" -> {
                PathfinderUseItem p = (PathfinderUseItem) b;
                yield new UseItemGoal<>(m, toNMS(p.getItem()), toNMS(p.getFinishSound()), e -> p.getCondition().test(fromNMS(e)));
            }
            case "Water" -> new TryFindWaterGoal((PathfinderMob) m);
            case "WaterJump" -> {
                PathfinderDolphinJump p = (PathfinderDolphinJump) b;
                yield new DolphinJumpGoal((net.minecraft.world.entity.animal.Dolphin) m, p.getInterval());
            }
            case "ZombieAttack" -> {
                PathfinderZombieAttack p = (PathfinderZombieAttack) b;
                yield new ZombieAttackGoal((net.minecraft.world.entity.monster.Zombie) m, p.getSpeedModifier(), p.mustSee());
            }
            case "UniversalAngerReset" -> {
                PathfinderResetAnger p = (PathfinderResetAnger) b;
                yield new ResetUniversalAngerTargetGoal<>((net.minecraft.world.entity.Mob & NeutralMob) m, p.isAlertingOthers());
            }
            case "RandomStandGoal" -> new RandomStandGoal((net.minecraft.world.entity.animal.horse.AbstractHorse) m);

            // Target

            case "DefendVillage" -> new DefendVillageTargetGoal((net.minecraft.world.entity.animal.IronGolem) m);
            case "HurtByTarget" -> {
                PathfinderHurtByTarget p = (PathfinderHurtByTarget) b;
                List<Class<? extends net.minecraft.world.entity.LivingEntity>> classes = new ArrayList<>();
                p.getIgnoring().stream().map(EntityType::getEntityClass).forEach(c -> classes.add(toNMS(c.asSubclass(LivingEntity.class))));

                yield new HurtByTargetGoal((PathfinderMob) m, classes.toArray(new Class[0]));
            }
            case "NearestAttackableTarget" -> {
                PathfinderNearestAttackableTarget p = (PathfinderNearestAttackableTarget) b;
                yield new NearestAttackableTargetGoal<>(m, toNMS(p.getFilter()), p.getInterval(), p.mustSee(), p.mustReach(), (t, l) -> p.getCondition().test(fromNMS(t)));
            }
            case "NearestAttackableTargetWitch" -> {
                PathfinderNearestAttackableTargetRaider p = (PathfinderNearestAttackableTargetRaider) b;
                yield new NearestAttackableWitchTargetGoal<>((net.minecraft.world.entity.raid.Raider) m, toNMS(p.getFilter()), p.getInterval(), p.mustSee(), p.mustReach(), (t, l) -> p.getCondition().test(fromNMS(t)));
            }
            case "NearestHealableRaider" -> {
                PathfinderNearestHealableRaider p = (PathfinderNearestHealableRaider) b;
                yield new NearestHealableRaiderTargetGoal<>((net.minecraft.world.entity.raid.Raider) m, toNMS(p.getFilter()), p.mustSee(), (t, l) -> p.getCondition().test(fromNMS(t)));
            }
            case "OwnerHurtByTarget" -> new OwnerHurtByTargetGoal((TamableAnimal) m);
            case "OwnerHurtTarget" -> new OwnerHurtTargetGoal((TamableAnimal) m);
            case "RandomTargetNonTamed" -> {
                PathfinderWildTarget p = (PathfinderWildTarget) b;
                yield new NonTameRandomTargetGoal<>((TamableAnimal) m, toNMS(p.getFilter()), p.mustSee(), (t, l) -> p.getCondition().test(fromNMS(t)));
            }

            default -> {
                if (b instanceof CustomPathfinder p) yield custom(p);
                else yield null;
            }
        };
    }

    @Override
    public void addPathfinder(Pathfinder b, int priority, boolean target) {
        Mob mob = b.getEntity();
        net.minecraft.world.entity.Mob m = toNMS(mob);
        GoalSelector s = target ? m.targetSelector : m.goalSelector;

        final Goal g = toNMS(b);
        if (g == null) return;
        s.addGoal(priority, g);
    }

    @Override
    public void removePathfinder(Pathfinder b, boolean target) {
        Mob mob = b.getEntity();
        net.minecraft.world.entity.Mob m = toNMS(mob);
        GoalSelector s = target ? m.targetSelector : m.goalSelector;

        final Goal g = toNMS(b);
        if (g == null) return;
        s.removeGoal(g);
    }

    @Override
    public void clearPathfinders(Mob mob, boolean target) {
        net.minecraft.world.entity.Mob m = toNMS(mob);
        GoalSelector s = target ? m.targetSelector : m.goalSelector;

        s.removeAllGoals(g -> true);
    }

    public static BehaviorResult.Status fromNMS(Behavior.Status status) {
        if (status == Behavior.Status.STOPPED) return BehaviorResult.Status.STOPPED;
        return BehaviorResult.Status.RUNNING;
    }

    public static LivingEntity fromNMS(net.minecraft.world.entity.LivingEntity l) {
        return (LivingEntity) l.getBukkitEntity();
    }

    @Override
    public BehaviorResult runBehavior(Mob m, String behaviorName, Object... args) {
        return runBehavior(m, behaviorName, Behavior.class.getPackage().getName(), args);
    }

    @Override
    public BehaviorResult runBehavior(Mob m, String behaviorName, String packageName, Object... args) {
        net.minecraft.world.entity.Mob nms = toNMS(m);

        for (int i = 0; i < args.length; i++) {
            Object o = args[i];
            if (o instanceof Villager.Profession) args[i] = toNMS((Villager.Profession) o);
            if (o instanceof Memory<?>) args[i] = toNMS((Memory<?>) o);

            if (o instanceof Predicate) args[i] = (Predicate) obj -> {
                if (obj instanceof net.minecraft.world.entity.Mob) return ((Predicate<Mob>) o).test(fromNMS((net.minecraft.world.entity.Mob) obj));

                return ((Predicate) o).test(obj);
            };

            if (o instanceof Function) args[i] = (Function) obj -> {
                if (obj instanceof net.minecraft.world.entity.LivingEntity) return ((Function<LivingEntity, ?>) o).apply(fromNMS((net.minecraft.world.entity.LivingEntity) obj));

                return ((Function) o).apply(obj);
            };

            if (o instanceof Sound) args[i] = toNMS((Sound) o);
            if (o instanceof Item) args[i] = toNMS((Item) o);
        }

        try {
            Class<?> bClass = Class.forName(packageName + "." + behaviorName);
            if (Behavior.class.isAssignableFrom(bClass)) {
                Constructor<?> c = bClass.getConstructor(ChipUtil.getArgTypes(args));
                Behavior<? super net.minecraft.world.entity.LivingEntity> b = (Behavior<? super net.minecraft.world.entity.LivingEntity>) c.newInstance(args);
                return new BehaviorResult1_21_R2(b, nms);
            } else {
                Method create = bClass.getDeclaredMethod("a", ChipUtil.getArgTypes(args));
                create.setAccessible(true);
                BehaviorControl<? super net.minecraft.world.entity.LivingEntity> control = (BehaviorControl<? super net.minecraft.world.entity.LivingEntity>) create.invoke(null, args);
                return new BehaviorResult1_21_R2(control, nms);
            }


        } catch (Exception e) {
            ChipUtil.printStackTrace(e);
            return null;
        }
    }


    public static ServerPlayer toNMS(Player p) { return ((CraftPlayer) p).getHandle(); }

    @Override
    public Attribute getDefaultAttribute(String s) {
        return new Attribute1_21_R2((RangedAttribute) BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.parse(s)).get().value());
    }

    public static net.minecraft.world.entity.schedule.Activity toNMS(Activity a) {
        return BuiltInRegistries.ACTIVITY.get(ResourceLocation.parse(a.getKey().getKey())).get().value();
    }

    public static Activity fromNMS(net.minecraft.world.entity.schedule.Activity a) {
        ResourceLocation key = BuiltInRegistries.ACTIVITY.getKey(a);
        if (key == null) return null;
        return Activity.getByKey(NamespacedKey.minecraft(key.getPath()));
    }

    public static Schedule fromNMS(net.minecraft.world.entity.schedule.Schedule s) {
        Schedule.Builder b = Schedule.builder();
        for (int i = 0; i < 24000; i++) {
            if (s.getActivityAt(i) == null) continue;
            Activity a = fromNMS(s.getActivityAt(i));
            b.addActivity(i, a);
        }

        return b.build();
    }

    public static net.minecraft.world.entity.schedule.Schedule toNMS(Schedule s) {
        ScheduleBuilder b = new ScheduleBuilder(new net.minecraft.world.entity.schedule.Schedule());
        for (int i = 0; i < 24000; i++) {
            if (!s.contains(i)) continue;
            net.minecraft.world.entity.schedule.Activity a = toNMS(s.get(i));
            b.changeActivityAt(i, a);
        }

        return b.build();
    }

    public static <T extends net.minecraft.world.entity.LivingEntity> Behavior<T> toNMS(Consumer<Mob> en) {
        return new Behavior<>(Collections.emptyMap()) {
            @Override
            protected void tick(ServerLevel var0, T m, long var2) {
                if (!(m instanceof net.minecraft.world.entity.Mob)) return;
                en.accept(fromNMS((net.minecraft.world.entity.Mob) m));
            }
        };
    }

    @Override
    public Schedule getDefaultSchedule(String key) {
        return fromNMS(BuiltInRegistries.SCHEDULE.get(ResourceLocation.parse(key)).get().value());
    }

    @Override
    public EntityScheduleManager getManager(Mob m) {
        return new EntityScheduleManager1_21_R2(m);
    }

    public static AbstractDragonPhaseInstance toNMS(CustomPhase c) {
        return new AbstractDragonPhaseInstance(toNMS(c.getDragon())) {
            @Override
            public EnderDragonPhase<? extends DragonPhaseInstance> getPhase() {
                try {
                    Method create = EnderDragonPhase.class.getDeclaredMethod("a");
                    create.setAccessible(true);
                    return (EnderDragonPhase<? extends DragonPhaseInstance>) create.invoke(null, this.getClass(), c.getKey().getKey());
                } catch (Exception ignored) {}
                return EnderDragonPhase.HOVERING;
            }

            public void begin() { c.start(); }
            public void end() { c.stop(); }
            public boolean isSitting() { return c.isSitting(); }
            public void doClientTick() { c.clientTick(); }
            public void doServerTick() { c.serverTick(); }
            public void onCrystalDestroyed(EndCrystal crystal, BlockPos pos, DamageSource s, net.minecraft.world.entity.player.Player p) {
                EnderCrystal bCrystal = (EnderCrystal) crystal.getBukkitEntity();
                c.onCrystalDestroyed(bCrystal, fromNMS(s), p == null ? null : Bukkit.getPlayer(p.getUUID()));
            }
            public Vec3 getFlyTargetLocation() {
                Location l = c.getTargetLocation();
                return new Vec3(l.getX(), l.getY(), l.getZ());
            }
            public float getFlySpeed() { return c.getFlyingSpeed(); }
            public float onHurt(DamageSource s, float damage) { return c.onDamage(fromNMS(s), damage); }
        };
    }

    @Override
    public void setCustomPhase(EnderDragon a, CustomPhase c) {
        net.minecraft.world.entity.boss.enderdragon.EnderDragon nmsMob = toNMS(a);
        AbstractDragonPhaseInstance nmsPhase = toNMS(c);
        try {
            new EnderDragonPhaseManager(nmsMob).setPhase(nmsPhase.getPhase());
        } catch (IndexOutOfBoundsException ignored) {}
    }

    @Override
    public void knockback(EnderDragon a, List<Entity> list) {
        net.minecraft.world.entity.boss.enderdragon.EnderDragon nmsMob = toNMS(a);

        try {
            Method m = net.minecraft.world.entity.boss.enderdragon.EnderDragon.class.getDeclaredMethod("a", List.class);
            m.setAccessible(true);
            m.invoke(nmsMob, list.stream().map(ChipUtil1_21_R2::toNMS).collect(Collectors.toList()));
        } catch (Exception e) {
            ChipUtil.printStackTrace(e);
        }
    }

    @Override
    public EntityController getController(Mob m) {
        return new EntityController1_21_R2(m);
    }

    @Override
    public EntityNavigation getNavigation(Mob m) {
        return new EntityNavigation1_21_R2(m);
    }

    @Override
    public EntityBody getBody(Mob m) {
        return new EntityBody1_21_R2(m);
    }

    private static DamageSource fromType(ResourceKey<DamageType> key) {
        return fromType(key, null);
    }

    private static DamageSource fromType(ResourceKey<DamageType> key, net.minecraft.world.entity.Entity cause) {
        Frozen access = ((CraftServer) Bukkit.getServer()).getHandle().getServer().registries().compositeAccess();

        return new DamageSource(access.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key), cause, null);
    }

    public static DamageSource toNMS(EntityDamageEvent.DamageCause c, Entity en) {
        if (en != null) {
            net.minecraft.world.entity.Entity nmsEntity = toNMS(en);

            if (c == EntityDamageEvent.DamageCause.FALLING_BLOCK)
              return fromType(DamageTypes.FALLING_BLOCK, nmsEntity);
        }

        return switch (c) {
            case FIRE -> fromType(DamageTypes.IN_FIRE);
            case LIGHTNING -> fromType(DamageTypes.LIGHTNING_BOLT);
            case FIRE_TICK -> fromType(DamageTypes.ON_FIRE);
            case SUFFOCATION -> fromType(DamageTypes.IN_WALL);
            case LAVA -> fromType(DamageTypes.LAVA);
            case HOT_FLOOR -> fromType(DamageTypes.HOT_FLOOR);
            case CRAMMING -> fromType(DamageTypes.CRAMMING);
            case DROWNING -> fromType(DamageTypes.DROWN);
            case STARVATION -> fromType(DamageTypes.STARVE);
            case CONTACT -> fromType(DamageTypes.CACTUS);
            case MAGIC -> fromType(DamageTypes.MAGIC);
            case FALL -> fromType(DamageTypes.FALL);
            case FLY_INTO_WALL -> fromType(DamageTypes.FLY_INTO_WALL);
            case VOID -> fromType(DamageTypes.FELL_OUT_OF_WORLD);
            case WITHER -> fromType(DamageTypes.WITHER);
            case DRAGON_BREATH -> fromType(DamageTypes.DRAGON_BREATH);
            case FREEZE -> fromType(DamageTypes.FREEZE);
            case DRYOUT -> fromType(DamageTypes.DRY_OUT);
            default -> fromType(DamageTypes.GENERIC);
        };
    }

    public static ItemEntity toNMS(Item i) {
        return ((CraftItem) i).getHandle();
    }

    public static net.minecraft.world.entity.LivingEntity toNMS(LivingEntity en) {
        return ((CraftLivingEntity) en).getHandle();
    }

    public static Object toNMS(String key, Object value) {
        final Object nmsValue;

        switch (value) {
            case Location l -> nmsValue = switch (key) {
                case "nearest_bed", "celebrate_location", "nearest_repellent", "disturbance_location",
                     "breeze_jump_target" -> new BlockPos(l.getBlockX(), l.getBlockY(), l.getBlockZ());
                default ->
                        GlobalPos.of(toNMS(l.getWorld()).dimension(), new BlockPos(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
            };
            case Location[] ls -> {
                List<GlobalPos> p = new ArrayList<>();

                for (Location l : ls) {
                    p.add(GlobalPos.of(toNMS(l.getWorld()).dimension(), new BlockPos(l.getBlockX(), l.getBlockY(), l.getBlockZ())));
                }

                nmsValue = p;
            }
            case Player p -> {
                if (key.equals("liked_player")) nmsValue = p.getUniqueId();
                else nmsValue = toNMS(p);
            }
            case Memory.WalkingTarget t ->
                    nmsValue = new WalkTarget(toNMS(t.getLocation()), (float) t.getSpeedModifier(), t.getDistance());
            case LivingEntity l -> nmsValue = toNMS(l);
            case Entity e -> {
                if (key.equals("angry_at")) nmsValue = e.getUniqueId();
                else nmsValue = toNMS(e);
            }
            case org.bukkit.block.Block[] b -> {
                final Collection<GlobalPos> s;
                if (key.equals("doors_to_close")) s = new HashSet<>();
                else s = new ArrayList<>();

                for (org.bukkit.block.Block bl : b) {
                    Location l = bl.getLocation();
                    s.add(GlobalPos.of(toNMS(l.getWorld()).dimension(), new BlockPos(l.getBlockX(), l.getBlockY(), l.getBlockZ())));
                }
                nmsValue = s;
            }
            case Villager[] vs -> {
                List<net.minecraft.world.entity.LivingEntity> s = new ArrayList<>();
                for (Villager v : vs) s.add(toNMS(v));
                nmsValue = s;
            }
            case Player[] ps -> {
                List<net.minecraft.world.entity.player.Player> s = new ArrayList<>();
                for (Player p : ps) s.add(toNMS(p));
                nmsValue = s;
            }
            case LivingEntity[] ls -> {
                List<net.minecraft.world.entity.LivingEntity> s = new ArrayList<>();
                for (LivingEntity l : ls) s.add(toNMS(l));
                nmsValue = s;
            }
            case EntityDamageEvent.DamageCause c -> nmsValue = toNMS(c, null);
            case Unit ignored -> nmsValue = net.minecraft.util.Unit.INSTANCE;
            case null, default -> nmsValue = value;
        }

        return nmsValue;
    }

    public static Object fromNMS(Mob m, String key, Object nmsValue) {
        Object value = nmsValue;

        if (nmsValue instanceof GlobalPos l) {
            BlockPos pos = l.pos();
            World w = ((CraftServer) Bukkit.getServer()).getHandle().getServer().registries().compositeAccess().lookupOrThrow(Registries.DIMENSION).get(l.dimension()).get().value().getWorld();
            if (w == null) w = Bukkit.getWorlds().getFirst();
            value = new Location(w, pos.getX(), pos.getY(), pos.getZ());
        }
        else if (nmsValue instanceof BlockPos p) {
            value = new Location(m.getWorld(), p.getX(), p.getY(), p.getZ());
        }
        else if (nmsValue instanceof List<?> li) {
            switch (key) {
                case "secondary_job_site", "interactable_doors" -> {
                    List<Location> l = new ArrayList<>();
                    li.forEach(o -> l.add((Location) fromNMS(m, key, o)));
                    value = l.toArray(new Location[0]);
                }
                case "nearest_players" -> {
                    List<Player> l = new ArrayList<>();
                    li.forEach(o -> l.add(Bukkit.getPlayer(((net.minecraft.world.entity.player.Player) o).getUUID())));
                    value = l.toArray(new Player[0]);
                }
                case "visible_villager_babies" -> {
                    List<Villager> l = new ArrayList<>();
                    li.forEach(o -> l.add((Villager) fromNMS((net.minecraft.world.entity.LivingEntity) o)));
                    value = l.toArray(new Villager[0]);
                }
                case "mobs" -> {
                    List<LivingEntity> vl = new ArrayList<>();
                    li.forEach(o -> vl.add(fromNMS((net.minecraft.world.entity.LivingEntity) o)));
                    value = vl.toArray(new LivingEntity[0]);
                }
            }
        }
        else if (nmsValue instanceof net.minecraft.world.entity.player.Player p) value = Bukkit.getPlayer(p.getUUID());
        else if (nmsValue instanceof WalkTarget t) {
            BlockPos p = t.getTarget().currentBlockPosition();
            value = new Memory.WalkingTarget(new Location(m.getWorld(), p.getX(), p.getY(), p.getZ()), t.getSpeedModifier(), t.getCloseEnoughDist());
        }
        else if (nmsValue instanceof net.minecraft.world.entity.LivingEntity l) value = Bukkit.getEntity(l.getUUID());
        else if (nmsValue instanceof Set<?> s) {
            if (key.equals("doors_to_close")) {
                List<org.bukkit.block.Block> l = new ArrayList<>();
                s.forEach(o -> l.add((org.bukkit.block.Block) fromNMS(m, key, o)));
                value = l.toArray(new org.bukkit.block.Block[0]);
            }
        }
        else if (value instanceof DamageSource c) value = fromNMS(c);
        else if (value instanceof net.minecraft.util.Unit) value = Unit.INSTANCE;
        else if (value instanceof Optional<?> o) value = fromNMS(m, key, o.orElse(null));
        else value = nmsValue;

        return value;
    }

    public static EntityDamageEvent.DamageCause fromNMS(DamageSource c) {
        return switch (c.getMsgId()) {
            case "inFire" -> FIRE;
            case "lightningBolt" -> LIGHTNING;
            case "onFire" -> FIRE_TICK;
            case "lava" -> LAVA;
            case "hotFloor" -> HOT_FLOOR;
            case "inWall" -> SUFFOCATION;
            case "cramming" -> CRAMMING;
            case "drown" -> DROWNING;
            case "starve" -> STARVATION;
            case "cactus", "sweetBerryBush", "stalagmite" -> CONTACT;
            case "fall" -> FALL;
            case "flyIntoWall" -> FLY_INTO_WALL;
            case "outOfWorld" -> VOID;
            case "magic" -> MAGIC;
            case "wither" -> WITHER;
            case "anvil", "fallingBlock", "fallingStalactite" -> FALLING_BLOCK;
            case "dragonBreath" -> DRAGON_BREATH;
            case "dryout" -> DRYOUT;
            case "freeze" -> FREEZE;
            default -> CUSTOM;
        };
    }

    @Override
    public MemoryStatus getMemoryStatus(Mob mob, Memory<?> m) {
        net.minecraft.world.entity.Mob nms = toNMS(mob);
        MemoryModuleType<?> nmsM = toNMS(m);

        if (nms.getBrain().checkMemory(nmsM, net.minecraft.world.entity.ai.memory.MemoryStatus.VALUE_PRESENT)) return MemoryStatus.PRESENT;
        if (nms.getBrain().checkMemory(nmsM, net.minecraft.world.entity.ai.memory.MemoryStatus.VALUE_ABSENT)) return MemoryStatus.ABSENT;

        return MemoryStatus.REGISTERED;
    }

    @Override
    public void setMemory(Mob mob, String memoryKey, Object value) {
        net.minecraft.world.entity.Mob nms = toNMS(mob);
        MemoryModuleType type = BuiltInRegistries.MEMORY_MODULE_TYPE.get(ResourceLocation.parse(memoryKey)).get().value();
        Object nmsValue = toNMS(memoryKey, value);

        nms.getBrain().setMemory(type, nmsValue);
    }

    @Override
    public <T> void setMemory(Mob mob, Memory<T> m, T value) {
        if (value == null) {
            removeMemory(mob, m);
            return;
        }

        net.minecraft.world.entity.Mob nms = toNMS(mob);
        MemoryModuleType type = toNMS(m);
        String key = BuiltInRegistries.MEMORY_MODULE_TYPE.getKey(type).getPath();
        Object nmsValue = toNMS(key, value);

        nms.getBrain().setMemory(type, nmsValue);
    }

    @Override
    public <T> void setMemory(Mob mob, Memory<T> m, T value, long durationTicks) {
        if (value == null) {
            removeMemory(mob, m);
            return;
        }

        net.minecraft.world.entity.Mob nms = toNMS(mob);
        MemoryModuleType type = toNMS(m);
        String key = BuiltInRegistries.MEMORY_MODULE_TYPE.getKey(type).getPath();
        Object nmsValue = toNMS(key, value);

        nms.getBrain().setMemoryWithExpiry(type, nmsValue, durationTicks);
    }

    @Override
    public <T> T getMemory(Mob mob, Memory<T> m) {
        net.minecraft.world.entity.Mob nms = toNMS(mob);
        MemoryModuleType type = toNMS(m);
        String key = BuiltInRegistries.MEMORY_MODULE_TYPE.getKey(type).getPath();

        return m.getBukkitClass().cast(fromNMS(mob, key, nms.getBrain().getMemory(type)));
    }

    @Override
    public long getExpiry(Mob mob, Memory<?> m) {
        net.minecraft.world.entity.Mob nms = toNMS(mob);
        MemoryModuleType type = toNMS(m);

        return nms.getBrain().getTimeUntilExpiry(type);
    }

    @Override
    public boolean contains(Mob mob, Memory<?> m) {
        net.minecraft.world.entity.Mob nms = toNMS(mob);
        MemoryModuleType type = toNMS(m);

        return nms.getBrain().hasMemoryValue(type);
    }

    @Override
    public void removeMemory(Mob mob, Memory<?> m) {
        net.minecraft.world.entity.Mob nms = toNMS(mob);
        MemoryModuleType<?> type = toNMS(m);
        nms.getBrain().eraseMemory(type);
    }

    @Override
    public boolean isRestricted(Mob m) {
        net.minecraft.world.entity.Mob nms = toNMS(m);
        return nms.isWithinRestriction();
    }

    @Override
    public void clearRestriction(Mob m) {
        net.minecraft.world.entity.Mob nms = toNMS(m);
        nms.clearRestriction();
    }

    @Override
    public void restrictTo(Mob m, double x, double y, double z, int radius) {
        net.minecraft.world.entity.Mob nms = toNMS(m);

        int bX = (int) x;
        int bY = (int) y;
        int bZ = (int) z;

        nms.restrictTo(new BlockPos(bX, bY, bZ), radius);
    }

    @Override
    public Location getRestriction(Mob m) {
        net.minecraft.world.entity.Mob nms = toNMS(m);
        BlockPos c = nms.getRestrictCenter();
        return new Location(m.getWorld(), c.getX(), c.getY(), c.getZ());
    }

    @Override
    public int getRestrictionRadius(Mob m) {
        net.minecraft.world.entity.Mob nms = toNMS(m);
        return ((int) nms.getRestrictRadius()) < 0 ? Integer.MAX_VALUE : (int) nms.getRestrictRadius();
    }

    @Override
    public boolean hasRestriction(Mob m) {
        net.minecraft.world.entity.Mob nms = toNMS(m);
        return nms.hasRestriction();
    }

    @Override
    public boolean canSee(Mob m, Entity en) {
        net.minecraft.world.entity.Mob nms = toNMS(m);
        return nms.getSensing().hasLineOfSight(toNMS(en));
    }

    public static net.minecraft.world.entity.Entity toNMS(Entity en) {
        return ((CraftEntity) en).getHandle();
    }

    public static VillagerProfession toNMS(Villager.Profession p) {
        // Can't make this a field because the tests will fail. Maybe there's a better way to do this?
        Map<Villager.Profession, VillagerProfession> VILLAGER_PROFESSION_MAP = ImmutableMap.<Villager.Profession,VillagerProfession>builder()
                .put(FARMER, VillagerProfession.FARMER)
                .put(FISHERMAN, VillagerProfession.FISHERMAN)
                .put(LIBRARIAN, VillagerProfession.LIBRARIAN)
                .put(WEAPONSMITH, VillagerProfession.WEAPONSMITH)
                .put(TOOLSMITH, VillagerProfession.TOOLSMITH)
                .put(BUTCHER, VillagerProfession.BUTCHER)
                .put(FLETCHER, VillagerProfession.FLETCHER)
                .put(MASON, VillagerProfession.MASON)
                .put(CLERIC, VillagerProfession.CLERIC)
                .put(ARMORER, VillagerProfession.ARMORER)
                .put(NITWIT, VillagerProfession.NITWIT)
                .put(SHEPHERD, VillagerProfession.SHEPHERD)
                .put(CARTOGRAPHER, VillagerProfession.CARTOGRAPHER)
                .put(LEATHERWORKER, VillagerProfession.LEATHERWORKER)
                .build();
        return VILLAGER_PROFESSION_MAP.getOrDefault(p, VillagerProfession.NONE);
    }

    public static <T extends Entity> Class<? extends T> fromNMS(Class<? extends net.minecraft.world.entity.Entity> clazz, Class<T> cast) {
        try {
            String name = clazz.getSimpleName();
            if (name.contains("Entity")) name = name.replace("Entity", "");

            Class<? extends Entity> bukkit = switch (name) {
                case "" -> Entity.class;
                case "Living" -> LivingEntity.class;
                case "Lightning" -> LightningStrike.class;
                case "Insentient" -> Mob.class;
                case "TameableAnimal" -> Tameable.class;

                case "Animal" -> Animals.class;
                case "FishSchool" -> Fish.class;
                case "HorseAbstract" -> AbstractHorse.class;
                case "HorseMule" -> Mule.class;
                case "HorseSkeleton" -> SkeletonHorse.class;
                case "HorseZombie" -> ZombieHorse.class;
                case "HorseDonkey" -> Donkey.class;
                case "WaterAnimal" -> WaterMob.class;

                case "GiantZombie" -> Giant.class;
                case "GuardianElder" -> ElderGuardian.class;
                case "IllagerIllusioner" -> Illusioner.class;
                case "SkeletonAbstract" -> AbstractSkeleton.class;
                case "SkeletonStray" -> Stray.class;
                case "SkeletonWither" -> WitherSkeleton.class;
                case "ZombieHusk" -> Husk.class;
                case "ZombieVillager" -> ZombieVillager.class;

                case "Villager" -> Villager.class;
                case "VillagerAbstract" -> AbstractVillager.class;
                case "VillagerTrader" -> WanderingTrader.class;

                case "Human" -> HumanEntity.class;
                case "Player" -> Player.class;

                case "FireballFireball" -> SizedFireball.class;
                case "Fireworks" -> Firework.class;
                case "FishingHook" -> FishHook.class;
                case "Potion" -> ThrownPotion.class;
                case "ProjectileThrowable" -> ThrowableProjectile.class;
                case "ThrownTrident" -> Trident.class;

                case "MinecartAbstract" -> Minecart.class;
                case "MinecartChest" -> StorageMinecart.class;
                case "MinecartCommandBlock" -> CommandMinecart.class;
                case "MinecartFurnace" -> PoweredMinecart.class;
                case "MinecartHopper" -> HopperMinecart.class;
                case "MinecartMobSpawner" -> SpawnerMinecart.class;
                case "MinecartTNT" -> ExplosiveMinecart.class;

                default -> Class.forName("org.bukkit.entity." + name).asSubclass(Entity.class);
            };

            return bukkit.asSubclass(cast);
        } catch (ClassNotFoundException e) {
            return cast;
        }
    }

    public static net.minecraft.world.entity.Mob toNMS(Mob m) { return ((CraftMob) m).getHandle(); }

    public static EntityType[] getEntityTypes(Class<?>... nms) {
        List<EntityType> types = new ArrayList<>();
        for (Class<?> c : nms) {

            Class<? extends Entity> bukkit = fromNMS((Class<? extends net.minecraft.world.entity.Entity>) c, Entity.class);
            for (EntityType t : EntityType.values()) {
                if (t.getEntityClass() != null && t.getEntityClass().isAssignableFrom(bukkit)) types.add(t);
            }
        }
        return types.toArray(new EntityType[0]);
    }

    public static Difficulty toNMS(org.bukkit.Difficulty d) {
        return switch (d) {
            case PEACEFUL -> Difficulty.PEACEFUL;
            case EASY -> Difficulty.EASY;
            case NORMAL -> Difficulty.NORMAL;
            case HARD -> Difficulty.HARD;
        };
    }

    public static org.bukkit.Difficulty fromNMS(Difficulty d) {
        return switch (d) {
            case PEACEFUL -> org.bukkit.Difficulty.PEACEFUL;
            case EASY -> org.bukkit.Difficulty.EASY;
            case NORMAL -> org.bukkit.Difficulty.NORMAL;
            case HARD -> org.bukkit.Difficulty.HARD;
        };
    }

    public static PathfinderMob toNMS(Creature c) { return ((CraftCreature) c).getHandle();}

    public static Goal.Flag toNMS(Pathfinder.PathfinderFlag f) {
        return switch (f) {
            case MOVEMENT -> Goal.Flag.MOVE;
            case JUMPING -> Goal.Flag.JUMP;
            case TARGETING -> Goal.Flag.TARGET;
            case LOOKING -> Goal.Flag.LOOK;
        };
    }

    public static Pathfinder.PathfinderFlag fromNMS(Goal.Flag f) {
        return switch (f) {
            case MOVE -> Pathfinder.PathfinderFlag.MOVEMENT;
            case JUMP -> Pathfinder.PathfinderFlag.JUMPING;
            case TARGET -> Pathfinder.PathfinderFlag.TARGETING;
            case LOOK -> Pathfinder.PathfinderFlag.LOOKING;
        };
    }

    public static float getFloat(Goal o, String name) {
        Float obj = getObject(o, name, Float.class);
        return obj == null ? 0 : obj;
    }

    public static double getDouble(Goal o, String name) {
        Double obj = getObject(o, name, Double.class);
        return obj == null ? 0 : obj;
    }

    public static boolean getBoolean(Goal o, String name) {
        Boolean obj = getObject(o, name, Boolean.class);
        return obj != null && obj;
    }

    public static int getInt(Goal o, String name) {
        Integer obj = getObject(o, name, Integer.class);
        return obj == null ? 0 : obj;
    }

    public static <T> T getObject(Goal o, String name, Class<T> cast) {
        try {
            Class<? extends Goal> clazz = o.getClass();

            while (clazz.getSuperclass() != null) {
                try {
                    Field f = clazz.getDeclaredField(name);
                    f.setAccessible(true);
                    return cast.cast(f.get(o));
                } catch (NoSuchFieldException | ClassCastException e) {
                    if (Goal.class.isAssignableFrom(clazz.getSuperclass())) clazz = (Class<? extends Goal>) clazz.getSuperclass();
                    else break;
                }
            }
        } catch (Exception e) {
            ChipUtil.printStackTrace(e);
        }

        return null;
    }

    public static Mob fromNMS(net.minecraft.world.entity.Mob m) { return (Mob) m.getBukkitEntity(); }

    public static World fromNMS(Level l) { return l.getWorld(); }

    public static ServerLevel toNMS(World w) { return ((CraftWorld) w).getHandle(); }

    public static BlockPos toNMS(Location l) { return new BlockPos(l.getBlockX(), l.getBlockY(), l.getBlockZ()); }

    public static List<ItemStack> fromNMS(Ingredient in) { return in.itemStacks().stream().map(CraftItemStack::asBukkitCopy).collect(Collectors.toList()); }

    public static Sound fromNMS(SoundEvent s) { return CraftSound.minecraftToBukkit(s); }

    public static Mob getEntity(Goal g) {
        // For no discernible reason, the Mob field in DoorInteractGoal
        // is not final, unlike the mob fields in every other pathfinder.
        // Since DoorInteractGoal and subclasses each only have one mob field,
        // we can simply ignore the "final" check in this case.
        boolean ignoreNonFinal = g instanceof DoorInteractGoal;
        try {
            Class<? extends Goal> clazz = g.getClass();

            while (clazz.getSuperclass() != null) {
                for (Field f : clazz.getDeclaredFields()) {
                    f.setAccessible(true);
                    if (net.minecraft.world.entity.Mob.class.isAssignableFrom(f.getType()) && (ignoreNonFinal || Modifier.isFinal(f.getModifiers()))) {
                        return fromNMS((net.minecraft.world.entity.Mob) f.get(g));
                    }
                }

                if (Goal.class.isAssignableFrom(clazz.getSuperclass())) clazz = (Class<? extends Goal>) clazz.getSuperclass();
                else break;
            }
            return null;
        } catch (Exception e) {
            ChipUtil.printStackTrace(e);
            return null;
        }
    }

    public static Object invoke(Goal g, String method, Object... args) {
        try {
            Method m = g.getClass().getDeclaredMethod(method);
            m.setAccessible(true);

            return m.invoke(g, args);
        } catch (Exception e) {
            ChipUtil.printStackTrace(e);
            return null;
        }
    }

    public static Set<Goal.Flag> getFlags(long backingSet) {
        Set<Goal.Flag> flags = new HashSet<>();

        for (Goal.Flag flag : Goal.Flag.values())
            if ((backingSet & 1L << flag.ordinal()) != 0L) flags.add(flag);

        return flags;
    }


    /**
     * Paper removes the default Goal#getFlags method for performance reasons, causing NoSuchMethodErrors.
     */
    public static Set<Goal.Flag> getFlags(Goal g) {
        try {
            Method getFlags = Goal.class.getDeclaredMethod("getFlags");
            getFlags.setAccessible(true);

            Object optimizedSmallEnumSet = getFlags.invoke(g);
            Method backingSetM = optimizedSmallEnumSet.getClass().getDeclaredMethod("getBackingSet");
            backingSetM.setAccessible(true);

            long backingSet = (long) backingSetM.invoke(optimizedSmallEnumSet);
            return getFlags(backingSet);
        } catch (NoSuchMethodException ignored) {
            try {
                Method obfGetFlags = Goal.class.getDeclaredMethod("j");
                obfGetFlags.setAccessible(true);
                return (Set<Goal.Flag>) obfGetFlags.invoke(g);
            } catch (NoSuchMethodException e) {
                throw new AssertionError("Could not find flags", e);
            } catch (ReflectiveOperationException e) {
                ChipUtil.printStackTrace(e);
                return null;
            }
        } catch (ReflectiveOperationException e) {
            ChipUtil.printStackTrace(e);
            return null;
        }
    }

    public static Goal custom(CustomPathfinder p) {
        CustomGoal1_21_R2 g = new CustomGoal1_21_R2(p);
        EnumSet<Goal.Flag> set = EnumSet.noneOf(Goal.Flag.class);
        Arrays.stream(p.getFlags()).map(ChipUtil1_21_R2::toNMS).forEach(set::add);
        g.setFlags(set);
        return g;
    }

    /**
     * A "custom" pathfinder is one not known to MobChip,
     * NMS or otherwise. (Private NMS pathfinders will be "custom.")
     */
    public static CustomPathfinder custom(Goal g) {
        if (g instanceof CustomGoal1_21_R2) {
            return ((CustomGoal1_21_R2) g).getPathfinder();
        }
        return new CustomPathfinder(getEntity(g)) {
            @Override
            public @NotNull PathfinderFlag[] getFlags() {
                Set<Goal.Flag> nms = ChipUtil1_21_R2.getFlags(g);

                PathfinderFlag[] flags = new PathfinderFlag[nms.size()];
                int i = 0;
                for (Goal.Flag f : nms) {
                    flags[i] = fromNMS(f);
                    i++;
                }
                return flags;
            }

            @Override
            public boolean canStart() {
                return g.canUse();
            }

            @Override
            public void start() {
                g.start();
            }

            @Override
            public void tick() {
                g.tick();
            }

            @Override
            public boolean canInterrupt() { return g.isInterruptable(); }

            @Override
            public void stop() { g.stop();}

            @Override
            public String getInternalName() {
                return g.getClass().getSimpleName();
            }
        };
    }

    public static Location fromNMS(BlockPos p, World w) { return new Location(w, p.getX(), p.getY(), p.getZ()); }

    public static Location fromNMS(Position p, World w) { return new Location(w, p.x(), p.y(), p.z()); }

    private Pathfinder fromNMS(Goal g) {
        if (g instanceof CustomGoal1_21_R2 custom) {
            return custom.getPathfinder();
        }

        Mob m = getEntity(g);
        String name = g.getClass().getSimpleName();

        return switch (name) {
            case "PathfinderGoalAvoidTarget" -> new PathfinderAvoidEntity<>((Creature) m, fromNMS(getObject(g, "f", Class.class), LivingEntity.class), getFloat(g, "c"), getDouble(g, "i"), getDouble(g, "j"), en -> getObject(g, "g", Predicate.class).test(toNMS(en)), en -> getObject(g, "h", Predicate.class).test(toNMS(en)));
            case "PathfinderGoalArrowAttack" -> new PathfinderRangedAttack(m, getDouble(g, "e"), getFloat(g, "i"), getInt(g, "g"), getInt(g, "h"));
            case "PathfinderGoalBeg" -> new PathfinderBeg((Wolf) m, getFloat(g, "d"));
            case "PathfinderGoalBowShoot" -> new PathfinderRangedBowAttack(m, getDouble(g, "b"), (float) Math.sqrt(getFloat(g, "d")), getInt(g, "c"));
            case "PathfinderGoalBreakDoor" -> new PathfinderBreakDoor(m, getInt(g, "i"), d -> getObject(g, "h", Predicate.class).test(toNMS(d)));
            case "PathfinderGoalBreath" -> new PathfinderBreathAir((Creature) m);
            case "PathfinderGoalBreed" -> new PathfinderBreed((Animals) m, getDouble(g, "g"));
            case "PathfinderGoalCatSitOnBed" -> new PathfinderCatOnBed((Cat) m, getDouble(g, "b"), getInt(g, "l"));
            case "PathfinderGoalCrossbowAttack" -> new PathfinderRangedCrossbowAttack((Pillager) m, getDouble(g, "d"), (float) Math.sqrt(getFloat(g, "e")));
            case "PathfinderGoalDoorOpen" -> new PathfinderOpenDoor(m, getBoolean(g, "a"));
            case "PathfinderGoalWaterJump" -> new PathfinderDolphinJump((Dolphin) m, getInt(g, "c"));
            case "PathfinderGoalEatTile" -> new PathfinderEatTile(m);
            case "PathfinderGoalWater" -> new PathfinderFindWater((Creature) m);
            case "PathfinderGoalFleeSun" -> new PathfinderFleeSun((Creature) m, getDouble(g, "e"));
            case "PathfinderGoalFloat" -> new PathfinderFloat(m);
            case "PathfinderGoalFollowBoat" -> new PathfinderFollowBoat((Creature) m);
            case "PathfinderGoalFollowEntity" -> new PathfinderFollowMob(m, getDouble(g, "d"), getFloat(g, "g"), getFloat(g, "i"));
            case "PathfinderGoalFollowOwner" -> new PathfinderFollowOwner((Tameable) m, getDouble(g, "h"), getFloat(g, "l"), getFloat(g, "k"), getBoolean(g, "n"));
            case "PathfinderGoalFollowParent" -> new PathfinderFollowParent((Animals) m, getDouble(g, "f"));
            case "PathfinderGoalHorseTrap" -> new PathfinderSkeletonTrap((SkeletonHorse) m);
            case "PathfinderGoalLeapAtTarget" -> new PathfinderLeapAtTarget(m, getFloat(g, "c"));
            case "PathfinderGoalJumpOnBlock" -> new PathfinderCatOnBlock((Cat) m, getDouble(g, "b"));
            case "PathfinderGoalLlamaFollow" -> new PathfinderLlamaFollowCaravan((Llama) m, getDouble(g, "b"));
            case "PathfinderGoalLookAtPlayer" -> new PathfinderLookAtEntity<>(m, fromNMS(getObject(g, "f", Class.class), LivingEntity.class), getFloat(g, "d"), getFloat(g, "e"), getBoolean(g, "i"));
            case "PathfinderGoalLookAtTradingPlayer" -> new PathfinderLookAtTradingPlayer((AbstractVillager) m);
            case "PathfinderGoalMeleeAttack" -> new PathfinderMeleeAttack((Creature) m, getDouble(g, "b"), getBoolean(g, "c"));
            case "PathfinderGoalMoveThroughVillage" -> new PathfinderMoveThroughVillage((Creature) m, getObject(g, "b", BooleanSupplier.class), getDouble(g, "b"), getInt(g, "g"), getBoolean(g, "e"));
            case "PathfinderGoalNearestVillage" -> new PathfinderRandomStrollThroughVillage((Creature) m, getInt(g, "b"));
            case "PathfinderGoalGotoTarget" -> new PathfinderMoveToBlock((Creature) m, l -> (boolean) invoke(g, "a", toNMS(l.getWorld()), toNMS(l)), getDouble(g, "b"), getInt(g, "l"), getInt(g, "m"));
            case "PathfinderGoalRaid" -> new PathfinderMoveToRaid((Raider) m);
            case "PathfinderGoalMoveTowardsRestriction" -> new PathfinderMoveTowardsRestriction((Creature) m, getDouble(g, "e"));
            case "PathfinderGoalMoveTowardsTarget" -> new PathfinderMoveTowardsTarget((Creature) m, getDouble(g, "f"), getFloat(g, "g"));
            case "PathfinderGoalOcelotAttack" -> new PathfinderOcelotAttack(m);
            case "PathfinderGoalOfferFlower" -> new PathfinderOfferFlower((IronGolem) m);
            case "PathfinderGoalPanic" -> new PathfinderPanic((Creature) m, getDouble(g, "c"));
            case "PathfinderGoalPerch" -> new PathfinderRideShoulder((Parrot) m);
            case "PathfinderGoalRandomLookaround" -> new PathfinderRandomLook(m);
            case "PathfinderGoalRandomStandGoal" -> new PathfinderRandomStand((AbstractHorse) m);
            case "PathfinderGoalRandomStroll" -> new PathfinderRandomStroll((Creature) m, getDouble(g, "f"), getInt(g, "g"));
            case "PathfinderGoalRandomStrollLand" -> new PathfinderRandomStrollLand((Creature) m, getDouble(g, "f"), getFloat(g, "j"));
            case "PathfinderGoalRandomSwim" -> new PathfinderRandomSwim((Creature) m, getDouble(g, "f"), getInt(g, "g"));
            case "PathfinderGoalRandomFly" -> new PathfinderRandomStrollFlying((Creature) m, getDouble(g, "f"));
            case "PathfinderGoalRemoveBlock" -> new PathfinderRemoveBlock((Creature) m, CraftMagicNumbers.getMaterial(getObject(g, "g", Block.class)), getDouble(g, "b"), getInt(g, "i"));
            case "PathfinderGoalRestrictSun" -> new PathfinderRestrictSun((Creature) m);
            case "PathfinderGoalSit" -> new PathfinderSit((Tameable) m);
            case "PathfinderGoalStrollVillage" -> new PathfinderRandomStrollToVillage((Creature) m, getDouble(g, "f"));
            case "PathfinderGoalStrollVillageGolem" -> new PathfinderRandomStrollInVillage((Creature) m, getDouble(g, "f"));
            case "PathfinderGoalSwell" -> new PathfinderSwellCreeper((Creeper) m);
            case "PathfinderGoalTame" -> new PathfinderTameHorse((AbstractHorse) m);
            case "PathfinderGoalTempt" -> createPathfinderTempt((Creature) m, g);
            case "PathfinderGoalTradeWithPlayer" -> new PathfinderTradePlayer((AbstractVillager) m);
            case "PathfinderGoalUniversalAngerReset" -> new PathfinderResetAnger(m, getBoolean(g, "c"));
            case "PathfinderGoalUseItem" -> new PathfinderUseItem(m, fromNMS(getObject(g, "b", net.minecraft.world.item.ItemStack.class)), en -> getObject(g, "c", Predicate.class).test(toNMS(en)), fromNMS(getObject(g, "d", SoundEvent.class)));
            case "PathfinderGoalZombieAttack" -> new PathfinderZombieAttack((Zombie) m, getDouble(g, "b"), getBoolean(g, "c"));
            case "ClimbOnTopOfPowderSnowGoal" -> new PathfinderClimbPowderedSnow(m, fromNMS(getObject(g, "b", Level.class)));

            // Target
            case "PathfinderGoalNearestAttackableTarget" -> new PathfinderNearestAttackableTarget<>(m, fromNMS(getObject(g, "a", Class.class), LivingEntity.class), getInt(g, "b"), getBoolean(g, "f"), getBoolean(g, "d"));
            case "PathfinderGoalNearestAttackableTargetWitch" -> new PathfinderNearestAttackableTargetRaider<>((Raider) m, fromNMS(getObject(g, "a", Class.class), LivingEntity.class), getInt(g, "b"), true, true, l -> getObject(g, "d", TargetingConditions.Selector.class).test(toNMS(l), null));
            case "PathfinderGoalNearestHealableRaider" -> new PathfinderNearestHealableRaider<>((Raider) m, fromNMS(getObject(g, "a", Class.class), LivingEntity.class), true,  l -> getObject(g, "d", TargetingConditions.Selector.class).test(toNMS(l), null));
            case "PathfinderGoalDefendVillage" -> new PathfinderDefendVillage((IronGolem) m);
            case "PathfinderGoalHurtByTarget" -> new PathfinderHurtByTarget((Creature) m, getEntityTypes(getObject(g, "i", Class[].class)));
            case "PathfinderGoalOwnerHurtByTarget" -> new PathfinderOwnerHurtByTarget((Tameable) m);
            case "PathfinderGoalOwnerHurtTarget" -> new PathfinderOwnerHurtTarget((Tameable) m);
            case "PathfinderGoalRandomTargetNonTamed" -> new PathfinderWildTarget<>((Tameable) m, fromNMS(getObject(g, "a", Class.class), LivingEntity.class), getBoolean(g, "f"), l -> getObject(g, "d", TargetingConditions.Selector.class).test(toNMS(l), null));

            default -> custom(g);
        };
    }

    private Pathfinder createPathfinderTempt(Creature m, Goal g) {
        Predicate<net.minecraft.world.item.ItemStack> predicate = getObject(g, "m", Predicate.class);
        if (predicate instanceof ItemStackPredicateContainer container) {
            return new PathfinderTempt(m, getDouble(g, "e"), container.items());
        }
        return new PathfinderTempt(m, getDouble(g, "e"), fromNMS(predicate));
    }

    /**
     * Wraps an NMS ItemStack predicate for use with Bukkit ItemStacks
     */
    public record ItemStackPredicateWrapper(
            Predicate<net.minecraft.world.item.ItemStack> predicate) implements Predicate<ItemStack> {

        @Override
        public boolean test(ItemStack item) {
            return predicate.test(CraftItemStack.asNMSCopy(item));
        }
    }

    /**
     * Contains a list of items that match a predicate
     */
    public record ItemStackPredicateContainer(
            Collection<ItemStack> items) implements Predicate<net.minecraft.world.item.ItemStack> {

        @Override
        public boolean test(net.minecraft.world.item.ItemStack itemStack) {
            ItemStack bukkitItem = CraftItemStack.asBukkitCopy(itemStack);
            for (ItemStack stack : items) {
                if (stack.isSimilar(bukkitItem)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static Predicate<ItemStack> fromNMS(Predicate<net.minecraft.world.item.ItemStack> p) {
        return new ItemStackPredicateWrapper(p);
    }

    public static Predicate<net.minecraft.world.item.ItemStack> toNMS(PathfinderTempt p) {
        if (p.getPredicate() instanceof ItemStackPredicateWrapper wrapper) {
            return wrapper.predicate();
        }
        return new ItemStackPredicateContainer(p.getItems());
    }

    public static ItemStack fromNMS(net.minecraft.world.item.ItemStack item) { return CraftItemStack.asBukkitCopy(item); }

    public static <T> void changeRegistryLock(Registry<T> r, boolean isLocked) {
        DedicatedServer srv = ((CraftServer) Bukkit.getServer()).getServer();
        MappedRegistry<T> registry = (MappedRegistry<T>) srv.registryAccess().lookupOrThrow(r.key());
        try {
            Field frozen = registry.getClass().getDeclaredField("ca");
            frozen.setAccessible(true);
            frozen.set(registry, isLocked);
        } catch (Exception e) {
            ChipUtil.printStackTrace(e);
        }
    }

    @Override
    public Attribute registerAttribute(NamespacedKey key, double defaultV, double min, double max, boolean client) {
        if (existsAttribute(key)) return null;
        changeRegistryLock(BuiltInRegistries.ATTRIBUTE, false);

        DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WritableRegistry<net.minecraft.world.entity.ai.attributes.Attribute> writable = (WritableRegistry<net.minecraft.world.entity.ai.attributes.Attribute>) server.registryAccess().lookupOrThrow(Registries.ATTRIBUTE);
        ResourceKey<net.minecraft.world.entity.ai.attributes.Attribute> nmsKey = ResourceKey.create(Registries.ATTRIBUTE, toNMS(key));
        Attribute1_21_R2 att = new Attribute1_21_R2(key, defaultV, min, max, client);
        writable.register(nmsKey, att, registration(key));

        changeRegistryLock(BuiltInRegistries.ATTRIBUTE, true);
        return att;
    }

    @Override
    public boolean existsAttribute(NamespacedKey key) {
        return BuiltInRegistries.ATTRIBUTE.containsKey(toNMS(key));
    }

    public static ResourceLocation toNMS(NamespacedKey key) {
        return CraftNamespacedKey.toMinecraft(key);
    }

    @Override
    public Attribute getAttribute(NamespacedKey key) {
        net.minecraft.world.entity.ai.attributes.Attribute a = BuiltInRegistries.ATTRIBUTE.get(toNMS(key)).get().value();
        if (!(a instanceof RangedAttribute)) return null;
        return new Attribute1_21_R2((RangedAttribute) a);
    }

    @NotNull
    private AttributeInstance1_21_R2 getOrCreateInstance(Mob m, Attribute a) {
        net.minecraft.world.entity.Mob nms = toNMS(m);
        AttributeMap map = nms.getAttributes();
        Optional<Holder.Reference<net.minecraft.world.entity.ai.attributes.Attribute>> nmsAH = BuiltInRegistries.ATTRIBUTE.get(toNMS(a.getKey()));
        if (nmsAH.isEmpty()) throw new AssertionError("Missing unregistered attribute: " + a.getKey());

        Holder<net.minecraft.world.entity.ai.attributes.Attribute> nmsA = nmsAH.get();
        net.minecraft.world.entity.ai.attributes.AttributeInstance handle = toNMS(m).getAttribute(nmsA);
        if (handle != null) return new AttributeInstance1_21_R2(a, handle);

        try {
            Field attributesF = AttributeMap.class.getDeclaredField("b");
            attributesF.setAccessible(true);
            Map<Holder<net.minecraft.world.entity.ai.attributes.Attribute>, net.minecraft.world.entity.ai.attributes.AttributeInstance> attributes = (Map<Holder<net.minecraft.world.entity.ai.attributes.Attribute>, net.minecraft.world.entity.ai.attributes.AttributeInstance>) attributesF.get(map);

            handle = new net.minecraft.world.entity.ai.attributes.AttributeInstance(nmsA, ignored -> {});
            attributes.put(nmsA, handle);

            return new AttributeInstance1_21_R2(a, handle);
        } catch (ReflectiveOperationException e) {
            ChipUtil.printStackTrace(e);
        }

        throw new RuntimeException("Failed to create AttributeInstance");
    }

    @Override
    public AttributeInstance getAttributeInstance(Mob m, Attribute a) {
        return getOrCreateInstance(m, a);
    }

    public static net.minecraft.world.entity.ai.gossip.GossipType toNMS(GossipType t) {
        for (net.minecraft.world.entity.ai.gossip.GossipType nms : net.minecraft.world.entity.ai.gossip.GossipType.values()) {
            if (nms.id.equalsIgnoreCase(t.getKey().getKey())) return nms;
        }

        throw new AssertionError("Missing GossipType: " + t.getKey() + "\"");
    }

    public static GossipType fromNMS(net.minecraft.world.entity.ai.gossip.GossipType t) {
        return GossipType.getByKey(NamespacedKey.minecraft(t.id));
    }

    @Override
    public EntityGossipContainer getGossipContainer(Villager v) {
        return new EntityGossipContainer1_21_R2(v);
    }

    public static Entity fromNMS(net.minecraft.world.entity.Entity en) {
        return en.getBukkitEntity();
    }

    public static CombatEntry fromNMS(Mob m, net.minecraft.world.damagesource.CombatEntry en) {
        return new CombatEntry(m, fromNMS(en.source()), 0, 0, en.damage(), en.fallDistance(), en.fallLocation() == null ? null : CombatLocation.getByKey(NamespacedKey.minecraft(en.fallLocation().id())));
    }

    public static net.minecraft.world.damagesource.CombatEntry toNMS(CombatEntry en) {
        return new net.minecraft.world.damagesource.CombatEntry(toNMS(en.getCause(), en.getAttacker()), en.getDamage(), new FallLocation(en.getLocation().getKey().getKey().toLowerCase()), en.getFallDistance());
    }

    @Override
    public EntityCombatTracker getCombatTracker(Mob m) { return new EntityCombatTracker1_21_R2(m); }

    @Override
    public BehaviorResult hearNoteblock(Creature c, Location loc) {
        AllayAi.hearNoteblock(toNMS(c), toNMS(loc));
        return ChipUtil.super.hearNoteblock(c, loc);
    }

    @Override
    public BehaviorResult setDisturbanceLocation(Creature c, Location loc) {
        Warden w = ((CraftWarden) c).getHandle();
        WardenAi.setDisturbanceLocation(w, toNMS(loc));
        return ChipUtil.super.setDisturbanceLocation(c, loc);
    }

    public static net.minecraft.world.entity.boss.enderdragon.EnderDragon toNMS(EnderDragon dragon) {
        return ((CraftEnderDragon) dragon).getHandle();
    }

    @Override
    public DragonPhase fromBukkit(EnderDragon d, EnderDragon.Phase phase) {
        net.minecraft.world.entity.boss.enderdragon.EnderDragon nms = toNMS(d);
        DragonPhaseInstance i = switch (phase) {
            case CIRCLING -> new DragonHoldingPatternPhase(nms);
            case STRAFING -> new DragonStrafePlayerPhase(nms);
            case FLY_TO_PORTAL -> new DragonLandingApproachPhase(nms);
            case LAND_ON_PORTAL -> new DragonLandingPhase(nms);
            case LEAVE_PORTAL -> new DragonTakeoffPhase(nms);
            case BREATH_ATTACK -> new DragonSittingFlamingPhase(nms);
            case SEARCH_FOR_BREATH_ATTACK_TARGET -> new DragonSittingScanningPhase(nms);
            case ROAR_BEFORE_ATTACK -> new DragonSittingAttackingPhase(nms);
            case CHARGE_PLAYER -> new DragonChargePlayerPhase(nms);
            case DYING -> new DragonDeathPhase(nms);
            default -> new DragonHoverPhase(nms);
        };

        return new DragonPhase1_21_R2(d, i);
    }

    @Override
    public DragonPhase getCurrentPhase(EnderDragon dragon) {
        return new DragonPhase1_21_R2(dragon, toNMS(dragon).getPhaseManager().getCurrentPhase());
    }

    @Override
    public void updateActivities(Creature c) {
        PathfinderMob nms = toNMS(c);
        if (c instanceof Allay) AllayAi.updateActivity((net.minecraft.world.entity.animal.allay.Allay) nms);
        else if (c instanceof Axolotl) AxolotlAi.updateActivity((net.minecraft.world.entity.animal.axolotl.Axolotl) nms);
        else if (c instanceof Frog) FrogAi.updateActivity((net.minecraft.world.entity.animal.frog.Frog) nms);
        else if (c instanceof org.bukkit.entity.Warden) WardenAi.updateActivity((Warden) nms);
        else if (c instanceof Camel) CamelAi.updateActivity((net.minecraft.world.entity.animal.camel.Camel) nms);
    }

    public static MemoryModuleType<?> toNMS(Memory<?> mem) {
        return BuiltInRegistries.MEMORY_MODULE_TYPE.get(mem instanceof EntityMemory<?> ? ResourceLocation.parse(mem.getKey().getKey()) : ResourceLocation.fromNamespaceAndPath(mem.getKey().getNamespace(), mem.getKey().getKey())).get().value();
    }

    @Override
    public void registerMemory(Memory<?> m) {
        changeRegistryLock(BuiltInRegistries.MEMORY_MODULE_TYPE, false);
        DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WritableRegistry<MemoryModuleType<?>> writable = (WritableRegistry<MemoryModuleType<?>>) server.registryAccess().lookupOrThrow(Registries.MEMORY_MODULE_TYPE);
        ResourceKey<MemoryModuleType<?>> nmsKey = ResourceKey.create(Registries.MEMORY_MODULE_TYPE, toNMS(m.getKey()));
        writable.register(nmsKey, toNMS(m), registration(m.getKey()));
        changeRegistryLock(BuiltInRegistries.MEMORY_MODULE_TYPE, true);
    }

    @Override
    public boolean existsMemory(Memory<?> m) {
        if (m instanceof EntityMemory<?>) return true;
        return BuiltInRegistries.MEMORY_MODULE_TYPE.containsKey(ResourceLocation.fromNamespaceAndPath(m.getKey().getNamespace(), m.getKey().getKey()));
    }

    @Override
    public EntityNBT getNBTEditor(Mob m) {
        return new EntityNBT1_21_R2(m);
    }

    public static net.minecraft.world.entity.ai.sensing.Sensor<?> toNMS(Sensor<?> s) {
        if (s instanceof SensorDefault1_21_R2) return ((SensorDefault1_21_R2) s).getHandle();
        return new Sensor1_21_R2(s);
    }

    public static SensorType<?> toNMSType(Sensor<?> s) {
        try {
            Constructor<SensorType> c = SensorType.class.getDeclaredConstructor(Supplier.class);
            c.setAccessible(true);

            Supplier<net.minecraft.world.entity.ai.sensing.Sensor<?>> sup = () -> toNMS(s);

            return c.newInstance(sup);
        } catch (ReflectiveOperationException e) {
            Bukkit.getLogger().severe(e.getMessage());
            for (StackTraceElement st : e.getStackTrace()) Bukkit.getLogger().severe(st.toString());
        }

        return null;
    }

    public static Sensor<?> fromNMS(net.minecraft.world.entity.ai.sensing.Sensor<?> type) {
        if (type instanceof Sensor1_21_R2) return ((Sensor1_21_R2) type).getSensor();
        return new SensorDefault1_21_R2(type);
    }

    public static NamespacedKey fromNMS(ResourceLocation loc) {
        return new NamespacedKey(loc.getNamespace(), loc.getPath());
    }

    public static Memory<?> fromNMS(MemoryModuleType<?> memory) {
        return EntityMemory.getByKey(fromNMS(BuiltInRegistries.MEMORY_MODULE_TYPE.getKey(memory)));
    }

    @Override
    public void registerSensor(Sensor<?> s) {
        changeRegistryLock(BuiltInRegistries.SENSOR_TYPE, false);
        DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WritableRegistry<SensorType<?>> writable = (WritableRegistry<SensorType<?>>) server.registryAccess().lookupOrThrow(Registries.SENSOR_TYPE);
        ResourceKey<SensorType<?>> nmsKey = ResourceKey.create(Registries.SENSOR_TYPE, toNMS(s.getKey()));
        writable.register(nmsKey, toNMSType(s), registration(s.getKey()));
        changeRegistryLock(BuiltInRegistries.SENSOR_TYPE, true);
    }

    @Override
    public boolean existsSensor(NamespacedKey key) {
        return BuiltInRegistries.SENSOR_TYPE.containsKey(ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getKey()));
    }

    @Override
    public Sensor<?> getSensor(NamespacedKey key) {
        return fromNMS(BuiltInRegistries.SENSOR_TYPE.get(toNMS(key)).get().value().create());
    }

    @Override
    public EntitySenses getSenses(Mob m) {
        return new EntitySenses1_21_R2(m);
    }

    @Override
    public EnderCrystal getNearestCrystal(EnderDragon d) {
        net.minecraft.world.entity.boss.enderdragon.EnderDragon nms = toNMS(d);
        if (nms.nearestCrystal == null) return null;
        return (EnderCrystal) nms.nearestCrystal.getBukkitEntity();
    }

    public static RegistrationInfo registration(NamespacedKey key) {
        return new RegistrationInfo(Optional.of(new KnownPack(key.getNamespace(), key.getKey(), Registration.getVersion())), Lifecycle.stable());
    }

    public static me.gamercoder215.mobchip.util.Position fromNMS(Node point) {
        return new me.gamercoder215.mobchip.util.Position(point.x, point.y, point.z);
    }

}