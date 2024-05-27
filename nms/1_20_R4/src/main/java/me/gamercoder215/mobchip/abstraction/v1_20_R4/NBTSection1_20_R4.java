package me.gamercoder215.mobchip.abstraction.v1_20_R4;

import me.gamercoder215.mobchip.abstraction.ChipUtil;
import me.gamercoder215.mobchip.nbt.NBTSection;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.*;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.craftbukkit.v1_20_R4.CraftRegistry;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
class NBTSection1_20_R4 implements NBTSection {

    private final CompoundTag tag;
    private final Runnable saveFunc;

    private final String currentPath;

    public NBTSection1_20_R4(CompoundTag tag, Runnable saveFunc, String path) {
        this.tag = tag;
        this.saveFunc = saveFunc;
        this.currentPath = path;
    }

    public NBTSection1_20_R4(Mob m) {
        this.tag = new CompoundTag();
        this.currentPath = "";
        ChipUtil1_20_R4.toNMS(m).saveWithoutId(tag);
        this.saveFunc = () -> ChipUtil1_20_R4.toNMS(m).load(tag);
    }

    static Tag serialize(Object v) {
        if (v.getClass().isArray()) {
            ListTag tag = new ListTag();
            for (int i = 0; i < Array.getLength(v); i++) tag.add(i, serialize(Array.get(v, i)));
            return tag;
        }

        switch (v) {
            case Collection<?> objects -> {
                List<?> collection = new ArrayList<>(objects);
                CompoundTag coll = new CompoundTag();

                try {
                    coll.putString(ChipUtil.CLASS_TAG, Collection.class.getName());
                    ListTag tag = new ListTag();
                    for (int i = 0; i < collection.size(); i++) tag.add(i, serialize(collection.get(i)));
                    coll.put("values", tag);

                    Field idF = ListTag.class.getDeclaredField("w");
                    idF.setAccessible(true);

                    coll.putByte("id", idF.getByte(tag));
                } catch (ReflectiveOperationException e) {
                    Bukkit.getLogger().severe("Failed to serialize collection: " + e.getMessage());
                    for (StackTraceElement ste : e.getStackTrace()) Bukkit.getLogger().severe(ste.toString());
                }

                return coll;
            }
            case Map<?, ?> map -> {
                CompoundTag tag = new CompoundTag();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    tag.put(entry.getKey().toString(), serialize(entry.getValue()));
                }
                return tag;
            }
            case Enum<?> enumeration -> {
                CompoundTag tag = new CompoundTag();
                tag.putString(ChipUtil.CLASS_TAG, enumeration.getClass().getName());
                tag.putString("value", ((Enum<?>) v).name());
                return tag;
            }
            case ConfigurationSerializable serializable -> {
                CompoundTag tag = new CompoundTag();
                tag.putString(ChipUtil.CLASS_TAG, serializable.getClass().getName());
                tag.put("value", serialize(serializable.serialize()));
                return tag;
            }
            default -> {
            }
        }

        return switch (v) {
            case Short s -> ShortTag.valueOf(s);
            case Float f -> FloatTag.valueOf(f);
            case Long l -> LongTag.valueOf(l);
            case Byte b -> ByteTag.valueOf(b);
            case Integer i -> IntTag.valueOf(i);
            case Double d -> DoubleTag.valueOf(d);
            case UUID uid -> {
                CompoundTag uuid = new CompoundTag();
                uuid.putString(ChipUtil.CLASS_TAG, uid.getClass().getName());
                uuid.putLong("least", uid.getLeastSignificantBits());
                uuid.putLong("most", uid.getMostSignificantBits());
                yield uuid;
            }
            case NamespacedKey key -> {
                CompoundTag nmsKey = new CompoundTag();
                nmsKey.putString(ChipUtil.CLASS_TAG, key.getClass().getName());
                nmsKey.putString("namespace", key.getNamespace());
                nmsKey.putString("key", key.getKey());
                yield nmsKey;
            }
            case ItemStack item -> {
                CompoundTag stack = new CompoundTag();
                stack.putString(ChipUtil.CLASS_TAG, item.getClass().getName());
                stack.put("item", CraftItemStack.asNMSCopy(item).saveOptional(CraftRegistry.getMinecraftRegistry()));

                yield stack;
            }
            case OfflinePlayer p -> {
                CompoundTag player = new CompoundTag();
                player.putString(ChipUtil.CLASS_TAG, OfflinePlayer.class.getName());
                player.putString("id", p.getUniqueId().toString());

                yield player;
            }
            case Location l -> {
                CompoundTag loc = new CompoundTag();
                loc.putString(ChipUtil.CLASS_TAG, Location.class.getName());
                loc.putDouble("x", l.getX());
                loc.putDouble("y", l.getY());
                loc.putDouble("z", l.getZ());
                loc.putFloat("yaw", l.getYaw());
                loc.putFloat("pitch", l.getPitch());
                loc.putString("world", l.getWorld().getName());
                yield loc;
            }
            case Vector vec -> {
                CompoundTag vector = new CompoundTag();
                vector.putString(ChipUtil.CLASS_TAG, Vector.class.getName());
                vector.putDouble("x", vec.getX());
                vector.putDouble("y", vec.getY());
                vector.putDouble("z", vec.getZ());
                yield vector;
            }
            case Color color -> {
                CompoundTag clr = new CompoundTag();
                clr.putString(ChipUtil.CLASS_TAG, Color.class.getName());
                clr.putInt("rgb", color.asRGB());
                yield clr;
            }
            case EulerAngle angle -> {
                CompoundTag euler = new CompoundTag();
                euler.putString(ChipUtil.CLASS_TAG, angle.getClass().getName());
                euler.putDouble("x", angle.getX());
                euler.putDouble("y", angle.getY());
                euler.putDouble("z", angle.getZ());
                yield euler;
            }
            default -> StringTag.valueOf(v.toString());
        };
    }

    static Object deserialize(Tag v) {
        if (v instanceof ListTag list) {
            List<Object> l = new ArrayList<>();
            list.stream().map(NBTSection1_20_R4::deserialize).forEach(l::add);
            return l.toArray();
        }

        if (v instanceof CompoundTag cmp) {
            boolean isClass = cmp.get(ChipUtil.CLASS_TAG) != null && cmp.get(ChipUtil.CLASS_TAG) instanceof StringTag && !cmp.getString(ChipUtil.CLASS_TAG).isEmpty();

            if (isClass) {
                String className = cmp.getString(ChipUtil.CLASS_TAG);
                try {
                    Class<?> clazz = Class.forName(className);

                    if (clazz.isEnum()) return Enum.valueOf(clazz.asSubclass(Enum.class), cmp.getString("value"));

                    if (ConfigurationSerializable.class.isAssignableFrom(clazz)) {
                        try {
                            Method deserialize = clazz.getDeclaredMethod("deserialize", Map.class);
                            deserialize.setAccessible(true);
                            return clazz.cast(deserialize.invoke(null, deserialize(cmp.getCompound("value"))));
                        } catch (NoSuchMethodException e) {
                            Bukkit.getLogger().severe("Class does not have deserialize method: " + className);
                            for (StackTraceElement ste : e.getStackTrace()) Bukkit.getLogger().severe(ste.toString());
                        } catch (InvocationTargetException e) {
                            Bukkit.getLogger().severe("Failed to deserialize class: " + className);
                            for (StackTraceElement ste : e.getStackTrace()) Bukkit.getLogger().severe(ste.toString());
                        } catch (ReflectiveOperationException e) {
                            Bukkit.getLogger().severe(e.getMessage());
                            for (StackTraceElement ste : e.getStackTrace()) Bukkit.getLogger().severe(ste.toString());
                        }
                    }

                    return switch (clazz.getSimpleName()) {
                        case "map" -> {
                            Map<String, Object> map = new HashMap<>();
                            for (String key : cmp.getAllKeys()) map.put(key, deserialize(cmp.get(key)));
                            yield map;
                        }
                        case "collection" -> {
                            int id = cmp.getInt("id");
                            ListTag list = cmp.getList("values", id);

                            List<? super Object> l = new ArrayList<>();
                            list.stream().map(NBTSection1_20_R4::deserialize).forEach(l::add);
                            yield new ArrayList<>(l);
                        }
                        case "uuid" -> {
                            long most = cmp.getLong("most");
                            long least = cmp.getLong("least");
                            yield new UUID(most, least);
                        }
                        case "offlineplayer" -> {
                            UUID uid = UUID.fromString(cmp.getString("id"));
                            yield Bukkit.getOfflinePlayer(uid);
                        }
                        case "namespacedkey" -> {
                            String namespace = cmp.getString("namespace");
                            String key = cmp.getString("key");
                            yield new NamespacedKey(namespace, key);
                        }
                        case "itemstack" -> {
                            CompoundTag item = cmp.getCompound("item");
                            Optional<ItemStack> stack = net.minecraft.world.item.ItemStack.parse(CraftRegistry.getMinecraftRegistry(), item)
                                    .map(CraftItemStack::asBukkitCopy);
                            yield stack.orElse(null);
                        }
                        case "location" -> {
                            String world = cmp.getString("world");
                            World w = Bukkit.getWorld(world);
                            if (w == null) throw new IllegalArgumentException("World not found: " + world);
                            double x = cmp.getDouble("x");
                            double y = cmp.getDouble("y");
                            double z = cmp.getDouble("z");
                            float yaw = cmp.getFloat("yaw");
                            float pitch = cmp.getFloat("pitch");
                            yield new Location(w, x, y, z, yaw, pitch);
                        }
                        case "vector" -> {
                            double x = cmp.getDouble("x");
                            double y = cmp.getDouble("y");
                            double z = cmp.getDouble("z");
                            yield new Vector(x, y, z);
                        }
                        case "color" -> {
                            int rgb = cmp.getInt("rgb");
                            yield Color.fromRGB(rgb);
                        }
                        case "eulerangle" -> {
                            double x = cmp.getDouble("x");
                            double y = cmp.getDouble("y");
                            double z = cmp.getDouble("z");
                            yield new EulerAngle(x, y, z);
                        }
                        default -> throw new AssertionError("Unknown Class: " + clazz.getSimpleName());
                    };
                } catch (ClassNotFoundException e) {
                    throw new AssertionError("Unknown Class: " + className);
                }
            } else {
                Map<String, Object> map = new HashMap<>();
                for (String key : cmp.getAllKeys()) map.put(key, deserialize(cmp.get(key)));
                return map;
            }
        }

        return switch (v.getId()) {
            case 1 -> ((ByteTag) v).getAsByte();
            case 2 -> ((ShortTag) v).getAsShort();
            case 3 -> ((IntTag) v).getAsInt();
            case 4 -> ((LongTag) v).getAsLong();
            case 5 -> ((FloatTag) v).getAsFloat();
            case 6 -> ((DoubleTag) v).getAsDouble();
            case 7 -> ((ByteArrayTag) v).getAsByteArray();
            default -> v.getAsString();
        };
    }

    private void save() {
        saveFunc.run();
    }

    @Override
    public @NotNull String getCurrentPath() {
        return currentPath;
    }

    @Override
    public @NotNull Map<String, Object> getValues(boolean deep) {
        Map<String, Object> map = tag.getAllKeys().stream().filter(k -> !(tag.get(k) instanceof CompoundTag)).collect(Collectors.toMap(Function.identity(), k -> deserialize(tag.get(k))));
        if (!deep) return map;

        tag.getAllKeys().stream().filter(k -> (tag.get(k) instanceof CompoundTag)).forEach(s -> {
            NBTSection sec = getSection(s);
            sec.getValues(true).forEach((k, v) -> map.put(s + "." + k, v));
        });

        return map;
    }

    @Override
    public void set(@Nullable String key, @Nullable Object value) {
        if (key == null) return;
        if (key.equals(ChipUtil.CLASS_TAG)) return;

        if (value == null) tag.remove(key);
        else tag.put(key, serialize(value));
        save();
    }

    @Override
    public boolean isSet(@Nullable String key) {
        return tag.contains(key);
    }

    @Override
    public void remove(@Nullable String key) {
        tag.remove(key);
        save();
    }

    private <T> T get(String key) {
        if (key == null) return null;
        return (T) getValues(true).getOrDefault(key, null);
    }

    private boolean contains(String key) {
        if (key == null) return false;
        return getKeys(true).contains(key);
    }

    @Override
    public double getDouble(@Nullable String key) {
        return get(key);
    }

    @Override
    public double getDouble(@Nullable String key, double def) {
        return contains(key) ? get(key) : def;
    }

    @Override
    public boolean isDouble(@Nullable String key) {
        return contains(key) && (get(key) instanceof Double || isInt(key));
    }

    @Override
    public int getInteger(@Nullable String key) {
        return get(key);
    }

    @Override
    public int getInteger(@Nullable String key, int def) {
        return contains(key) ? get(key) : def;
    }

    @Override
    public boolean isInt(@Nullable String key) {
        return contains(key) && get(key) instanceof Integer;
    }

    @Override
    public boolean getBoolean(@Nullable String key) {
        return tag.getBoolean(key);
    }

    @Override
    public boolean getBoolean(@Nullable String key, boolean def) {
        return contains(key) ? get(key) : def;
    }

    @Override
    public boolean isBoolean(@Nullable String key) {
        return contains(key) && get(key) instanceof Boolean;
    }

    @Override
    public float getFloat(@Nullable String key) {
        return get(key);
    }

    @Override
    public float getFloat(@Nullable String key, float def) {
        return contains(key) ? get(key) : def;
    }

    @Override
    public boolean isFloat(@Nullable String key) {
        return contains(key) && (get(key) instanceof Float || isInt(key));
    }

    @Override
    public long getLong(@Nullable String key) {
        return get(key);
    }

    @Override
    public long getLong(@Nullable String key, long def) {
        return contains(key) ? get(key) : def;
    }

    @Override
    public boolean isLong(@Nullable String key) {
        return contains(key) && get(key) instanceof Long;
    }

    @Override
    public byte getByte(@Nullable String key) {
        return get(key);
    }

    @Override
    public byte getByte(@Nullable String key, byte def) {
        return contains(key) ? get(key) : def;
    }

    @Override
    public boolean isByte(@Nullable String key) {
        return contains(key) && get(key) instanceof Byte;
    }

    @Override
    public @Nullable String getString(@Nullable String key) {
        return contains(key) ? null : get(key);
    }

    @Override
    public @Nullable String getString(@Nullable String key, @Nullable String def) {
        return contains(key) ? get(key) : def;
    }

    @Override
    public boolean isString(@Nullable String key) {
        return contains(key) && get(key) instanceof String;
    }

    @Override
    public @Nullable NamespacedKey getNamespacedKey(@Nullable String key) {
        return get(key);
    }

    @Override
    public @Nullable NamespacedKey getNamespacedKey(@Nullable String key, @Nullable NamespacedKey def) {
        return tag.get(key) == null ? def : get(key);
    }

    @Override
    public boolean isNamespacedKey(@Nullable String key) {
        return contains(key) && get(key) instanceof NamespacedKey;
    }

    @Override
    public @Nullable UUID getUUID(@Nullable String key) {
        return get(key);
    }

    @Override
    public @Nullable UUID getUUID(@Nullable String key, @Nullable UUID def) {
        return contains(key) ? get(key) : def;
    }

    @Override
    public boolean isUUID(@Nullable String key) {
        return contains(key) && get(key) instanceof UUID;
    }

    @Override
    public @Nullable OfflinePlayer getOfflinePlayer(@Nullable String key) {
        return get(key);
    }

    @Override
    public @Nullable OfflinePlayer getOfflinePlayer(@Nullable String key, @Nullable OfflinePlayer def) {
        return contains(key) ? get(key) : def;
    }

    @Override
    public boolean isOfflinePlayer(@Nullable String key) {
        return contains(key) && get(key) instanceof OfflinePlayer;
    }

    @Override
    public <T extends Enum<T>> @Nullable T getEnum(@Nullable String key, Class<T> enumClass) {
        return enumClass.cast(get(key));
    }

    @Override
    public <T extends Enum<T>> @Nullable T getEnum(@Nullable String key, Class<T> enumClass, @Nullable T def) {
        return contains(key) ? def : enumClass.cast(get(key));
    }

    @Override
    public boolean isEnum(@Nullable String key) {
        return contains(key) && get(key) instanceof Enum;
    }

    @Override
    public <T extends Enum<T>> boolean isEnum(@Nullable String key, Class<T> enumClass) throws IllegalArgumentException {
        return contains(key) && get(key) instanceof Enum && enumClass.isAssignableFrom(get(key).getClass());
    }

    @Override
    public @Nullable Location getLocation(@Nullable String key) {
        return get(key);
    }

    @Override
    public @Nullable Location getLocation(@Nullable String key, @Nullable Location def) {
        return contains(key) ? get(key) : def;
    }

    @Override
    public boolean isLocation(@Nullable String key) {
        return contains(key) && get(key) instanceof Location;
    }

    @Override
    public @Nullable Vector getVector(@Nullable String key) {
        return get(key);
    }

    @Override
    public @Nullable Vector getVector(@Nullable String key, @Nullable Vector def) {
        return contains(key) ? get(key) : def;
    }

    @Override
    public boolean isVector(@Nullable String key) {
        return contains(key) && get(key) instanceof Vector;
    }

    @Override
    public @Nullable ItemStack getItemStack(@Nullable String key) {
        return get(key);
    }

    @Override
    public @Nullable ItemStack getItemStack(@Nullable String key, @Nullable ItemStack def) {
        return contains(key) ? get(key) : def;
    }

    @Override
    public boolean isItemStack(@Nullable String key) {
        return contains(key) && get(key) instanceof ItemStack;
    }

    @Override
    public <T extends ConfigurationSerializable> @Nullable T getObject(@Nullable String key, @NotNull Class<T> clazz) {
        return clazz.cast(get(key));
    }

    @Override
    public <T extends ConfigurationSerializable> @Nullable T getObject(@Nullable String key, @NotNull Class<T> clazz, @Nullable T def) {
        return contains(key) ? def : clazz.cast(get(key));
    }

    @Override
    public @Nullable Color getColor(@Nullable String path) {
        return get(path);
    }

    @Override
    public @Nullable Color getColor(@Nullable String path, @Nullable Color def) {
        return contains(path) ? def : get(path);
    }

    @Override
    public boolean isColor(@Nullable String path) {
        return contains(path) && get(path) instanceof Color;
    }

    @Override
    public @Nullable NBTSection getSection(@Nullable String key) {
        return tag.get(key) == null ? null : new NBTSection1_20_R4(tag.getCompound(key), this::save, currentPath + "." + key);
    }

    @Override
    public @Nullable NBTSection getSection(@Nullable String key, @Nullable NBTSection def) {
        return tag.get(key) == null ? def : new NBTSection1_20_R4(tag.getCompound(key), this::save, currentPath + "." + key);
    }

    @Override
    public @NotNull NBTSection getOrCreateSection(@NotNull String key) throws IllegalArgumentException {
        return getSection(key, new NBTSection1_20_R4(new CompoundTag(), this::save, currentPath + "." + key));
    }

    @Override
    public @NotNull NBTSection getOrCreateSection(@NotNull String key, Map<String, Object> map) throws IllegalArgumentException {
        NBTSection sec = getOrCreateSection(key);
        map.forEach(sec::set);
        return sec;
    }

    @Override
    public boolean isSection(@Nullable String key) {
        return isSet(key) && tag.get(key) instanceof CompoundTag && tag.getCompound(key).getString(ChipUtil.CLASS_TAG).isEmpty();
    }

    @Override
    public @NotNull List<?> getList(@Nullable String key) {
        return get(key);
    }

    @Override
    public @Nullable List<?> getList(@Nullable String key, @Nullable List<?> def) {
        return contains(key) ? get(key) : def;
    }

    @Override
    public boolean isList(@Nullable String key) {
        return contains(key) && get(key) instanceof List;
    }

    @Override
    public @NotNull Map<String, Object> getMap(@Nullable String key) {
        return get(key);
    }

    @Override
    public @Nullable Map<String, Object> getMap(@Nullable String key, @Nullable Map<String, Object> def) {
        return contains(key) ? get(key) : def;
    }

    @Override
    public boolean isMap(@Nullable String key) {
        return contains(key) && get(key) instanceof Map<?, ?>;
    }

    @Override
    public @Nullable EulerAngle getEulerAngle(@Nullable String path) {
        return get(path);
    }

    @Override
    public @Nullable EulerAngle getEulerAngle(@Nullable String path, @Nullable EulerAngle def) {
        return contains(path) ? def : get(path);
    }

    @Override
    public boolean isEulerAngle(@Nullable String path) {
        return contains(path) && get(path) instanceof EulerAngle;
    }

}
