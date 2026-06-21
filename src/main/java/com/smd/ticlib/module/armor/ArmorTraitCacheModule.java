package com.smd.ticlib.module.armor;

import com.smd.ticlib.api.TicTraits;
import com.smd.ticlib.core.nbt.TicNbt;
import com.smd.ticlib.core.target.TicTargets;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unchecked")
public final class ArmorTraitCacheModule {

    public static final ArmorTraitCacheModule INSTANCE = new ArmorTraitCacheModule();

    private static final EntityEquipmentSlot[] ARMOR_SLOTS = {
            EntityEquipmentSlot.HEAD,
            EntityEquipmentSlot.CHEST,
            EntityEquipmentSlot.LEGS,
            EntityEquipmentSlot.FEET
    };

    private final Map<UUID, ArmorTraitSnapshot> cache = new HashMap<>();

    private ArmorTraitCacheModule() {
    }

    public String[] getTraits(EntityPlayer player) {
        return getSnapshot(player).copyAllTraits();
    }

    public String[] getSlotTraits(EntityPlayer player, String slotName) {
        return getSlotTraits(player, TicTargets.parseArmorSlot(slotName));
    }

    public String[] getSlotTraits(EntityPlayer player, EntityEquipmentSlot slot) {
        int index = TicTargets.armorSlotIndex(slot);
        return index < 0 ? TicNbt.EMPTY_STRINGS : getSnapshot(player).copySlotTraits(index);
    }

    public boolean hasTrait(EntityPlayer player, String traitId) {
        return traitId != null && !traitId.trim().isEmpty() && getSnapshot(player).allTraitSet.contains(traitId);
    }

    public boolean hasSlotTrait(EntityPlayer player, String slotName, String traitId) {
        if (traitId == null || traitId.trim().isEmpty()) {
            return false;
        }
        int index = TicTargets.armorSlotIndex(TicTargets.parseArmorSlot(slotName));
        return index >= 0 && getSnapshot(player).slotTraitSets[index].contains(traitId);
    }

    public boolean refresh(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        cache.put(player.getUniqueID(), buildSnapshot(player));
        return true;
    }

    public void clear(EntityPlayer player) {
        if (player != null) {
            cache.remove(player.getUniqueID());
        }
    }

    @SubscribeEvent
    public void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer && TicTargets.armorSlotIndex(event.getSlot()) >= 0) {
            refresh((EntityPlayer) event.getEntityLiving());
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        refresh(event.player);
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        clear(event.player);
    }

    @SubscribeEvent
    public void onPlayerClone(Clone event) {
        clear(event.getOriginal());
        refresh(event.getEntityPlayer());
    }

    private ArmorTraitSnapshot getSnapshot(EntityPlayer player) {
        if (player == null) {
            return ArmorTraitSnapshot.EMPTY;
        }
        ArmorTraitSnapshot snapshot = cache.get(player.getUniqueID());
        if (snapshot == null) {
            snapshot = buildSnapshot(player);
            cache.put(player.getUniqueID(), snapshot);
        }
        return snapshot;
    }

    private ArmorTraitSnapshot buildSnapshot(EntityPlayer player) {
        String[][] slotTraits = new String[ARMOR_SLOTS.length][];
        Set<String>[] slotTraitSets = new Set[ARMOR_SLOTS.length];
        Set<String> allTraitSet = new HashSet<>();
        int allCount = 0;

        for (int i = 0; i < ARMOR_SLOTS.length; i++) {
            ItemStack stack = player.getItemStackFromSlot(ARMOR_SLOTS[i]);
            String[] traits = TicTargets.isArmor(stack) ? TicTraits.getTraits(stack) : TicNbt.EMPTY_STRINGS;
            slotTraits[i] = traits;
            slotTraitSets[i] = toSet(traits);
            allTraitSet.addAll(slotTraitSets[i]);
            allCount += traits.length;
        }

        String[] allTraits = new String[allCount];
        int offset = 0;
        for (String[] traits : slotTraits) {
            System.arraycopy(traits, 0, allTraits, offset, traits.length);
            offset += traits.length;
        }

        return new ArmorTraitSnapshot(slotTraits, allTraits, allTraitSet, slotTraitSets);
    }

    private static Set<String> toSet(String[] traits) {
        if (traits == null || traits.length == 0) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(traits)));
    }

    private static final class ArmorTraitSnapshot {
        static final ArmorTraitSnapshot EMPTY = new ArmorTraitSnapshot(
                new String[][]{TicNbt.EMPTY_STRINGS, TicNbt.EMPTY_STRINGS, TicNbt.EMPTY_STRINGS, TicNbt.EMPTY_STRINGS},
                TicNbt.EMPTY_STRINGS,
                Collections.emptySet(),
                new Set[]{Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet()}
        );

        private final String[][] slotTraits;
        private final String[] allTraits;
        private final Set<String> allTraitSet;
        private final Set<String>[] slotTraitSets;

        private ArmorTraitSnapshot(String[][] slotTraits, String[] allTraits, Set<String> allTraitSet, Set<String>[] slotTraitSets) {
            this.slotTraits = slotTraits;
            this.allTraits = allTraits;
            this.allTraitSet = Collections.unmodifiableSet(new HashSet<>(allTraitSet));
            this.slotTraitSets = slotTraitSets;
        }

        private String[] copyAllTraits() {
            return allTraits.length == 0 ? TicNbt.EMPTY_STRINGS : Arrays.copyOf(allTraits, allTraits.length);
        }

        private String[] copySlotTraits(int index) {
            String[] traits = slotTraits[index];
            return traits.length == 0 ? TicNbt.EMPTY_STRINGS : Arrays.copyOf(traits, traits.length);
        }
    }
}
