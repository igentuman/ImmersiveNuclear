package igentuman.immersivenuclear.api;

import blusunrize.immersiveengineering.api.utils.TagUtils;
import com.google.common.base.Preconditions;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.common.Tags.Items;

public class INTags {
    private static final Map<TagKey<Block>, TagKey<Item>> toItemTag = new HashMap();
    private static final Map<INEnumMetals, MetalTags> metals = new EnumMap(INEnumMetals.class);
    public static final TagKey<Item> steelRod;
    public static final TagKey<Item> metalRods;
    public static final TagKey<Item> steelWire;
    public static final TagKey<Item> allWires;
    public static final TagKey<Item> plates;
    public static final TagKey<Item> forbiddenInCrates;
    public static final TagKey<Item> circuitPCB;
    public static final TagKey<Item> circuitLogic;
    public static final TagKey<Item> circuitSolder;
    public static final TagKey<Item> toolboxTools;
    public static final TagKey<Item> toolboxFood;
    public static final TagKey<Item> toolboxWiring;
    public static final TagKey<Item> connectorInsulator;
    public static final TagKey<Block> hammerHarvestable;
    public static final TagKey<Block> wirecutterHarvestable;
    public static final TagKey<Block> drillHarvestable;
    public static final TagKey<Item> tools;
    public static final TagKey<Item> pickaxes;
    public static final TagKey<Item> shovels;
    public static final TagKey<Item> axes;
    public static final TagKey<Item> hoes;
    public static final TagKey<Item> swords;
    public static final TagKey<Item> powerpackForbidAttach;
    public static final TagKey<Item> recyclingIgnoredComponents;
    public static final TagKey<Item> recyclingWhitelist;
    public static final TagKey<Item> recyclingBlacklist;
    public static final TagKey<Fluid> fluidSteam;
    public static final TagKey<Fluid> fluidPotion;

    public INTags() {

    }

    public static TagKey<Item> getItemTag(TagKey<Block> blockTag) {
        Preconditions.checkArgument(toItemTag.containsKey(blockTag));
        return (TagKey)toItemTag.get(blockTag);
    }

    public static MetalTags getTagsFor(INEnumMetals metal) {
        return (MetalTags)metals.get(metal);
    }

    private static TagKey<Block> createBlockTag(ResourceLocation name) {
        TagKey<Block> blockTag = TagUtils.createBlockWrapper(name);
        toItemTag.put(blockTag, TagUtils.createItemWrapper(name));
        return blockTag;
    }

    public static void forAllBlocktags(BiConsumer<TagKey<Block>, TagKey<Item>> out) {
        for(Map.Entry<TagKey<Block>, TagKey<Item>> entry : toItemTag.entrySet()) {
            out.accept((TagKey)entry.getKey(), (TagKey)entry.getValue());
        }

    }

    private static ResourceLocation forgeLoc(String path) {
        return new ResourceLocation("forge", path);
    }

    public static ResourceLocation getOre(String type) {
        return forgeLoc("ores/" + type);
    }

    public static ResourceLocation getRawOre(String type) {
        return forgeLoc("raw_materials/" + type);
    }

    public static ResourceLocation getNugget(String type) {
        return forgeLoc("nuggets/" + type);
    }

    public static ResourceLocation getIngot(String type) {
        return forgeLoc("ingots/" + type);
    }

    public static ResourceLocation getGem(String type) {
        return forgeLoc("gems/" + type);
    }

    public static ResourceLocation getStorageBlock(String type) {
        return forgeLoc("storage_blocks/" + type);
    }

    public static ResourceLocation getRawBlock(String type) {
        return getStorageBlock("raw_" + type);
    }

    public static ResourceLocation getDust(String type) {
        return forgeLoc("dusts/" + type);
    }

    public static ResourceLocation getPlate(String type) {
        return forgeLoc("plates/" + type);
    }

    public static ResourceLocation getRod(String type) {
        return forgeLoc("rods/" + type);
    }

    public static ResourceLocation getGear(String type) {
        return forgeLoc("gears/" + type);
    }

    public static ResourceLocation getWire(String type) {
        return forgeLoc("wires/" + type);
    }

    public static ResourceLocation getSheetmetalBlock(String type) {
        return forgeLoc("sheetmetals/" + type);
    }

    private static ResourceLocation rl(String path) {
        return new ResourceLocation("immersiveengineering", path);
    }

    static {
        toItemTag.put(Blocks.STORAGE_BLOCKS, Items.STORAGE_BLOCKS);
        toItemTag.put(Blocks.ORES, Items.ORES);
        toItemTag.put(Blocks.ORES_IN_GROUND_STONE, Items.ORES_IN_GROUND_STONE);
        toItemTag.put(Blocks.ORES_IN_GROUND_DEEPSLATE, Items.ORES_IN_GROUND_DEEPSLATE);
        toItemTag.put(Blocks.ORE_RATES_SINGULAR, Items.ORE_RATES_SINGULAR);
        steelRod = TagUtils.createItemWrapper(getRod("steel"));
        metalRods = TagUtils.createItemWrapper(getRod("all_metal"));
        steelWire = TagUtils.createItemWrapper(getWire("steel"));
        allWires = TagUtils.createItemWrapper(forgeLoc("wires"));
        plates = TagUtils.createItemWrapper(forgeLoc("plates"));
        forbiddenInCrates = TagUtils.createItemWrapper(rl("forbidden_in_crates"));
        circuitPCB = TagUtils.createItemWrapper(rl("circuits/pcb"));
        circuitLogic = TagUtils.createItemWrapper(rl("circuits/logic"));
        circuitSolder = TagUtils.createItemWrapper(rl("circuits/solder"));
        toolboxTools = TagUtils.createItemWrapper(rl("toolbox/tools"));
        toolboxFood = TagUtils.createItemWrapper(rl("toolbox/food"));
        toolboxWiring = TagUtils.createItemWrapper(rl("toolbox/wiring"));
        connectorInsulator = TagUtils.createItemWrapper(rl("connector_insulator"));
        hammerHarvestable = TagUtils.createBlockWrapper(rl("mineable/hammer"));
        wirecutterHarvestable = TagUtils.createBlockWrapper(rl("mineable/wirecutter"));
        drillHarvestable = TagUtils.createBlockWrapper(rl("mineable/drill"));
        tools = TagUtils.createItemWrapper(forgeLoc("tools"));
        pickaxes = TagUtils.createItemWrapper(forgeLoc("tools/pickaxes"));
        shovels = TagUtils.createItemWrapper(forgeLoc("tools/shovels"));
        axes = TagUtils.createItemWrapper(forgeLoc("tools/axes"));
        hoes = TagUtils.createItemWrapper(forgeLoc("tools/hoes"));
        swords = TagUtils.createItemWrapper(forgeLoc("tools/swords"));
        powerpackForbidAttach = TagUtils.createItemWrapper(rl("powerpack/forbid_attach"));
        recyclingIgnoredComponents = TagUtils.createItemWrapper(rl("recycling/ignored_components"));
        recyclingWhitelist = TagUtils.createItemWrapper(rl("recycling/whitelist"));
        recyclingBlacklist = TagUtils.createItemWrapper(rl("recycling/blacklist"));
        fluidPotion = TagUtils.createFluidWrapper(forgeLoc("potion"));
        fluidSteam = TagUtils.createFluidWrapper(forgeLoc("creosote"));
        for(INEnumMetals m : INEnumMetals.values()) {
            metals.put(m, new MetalTags(m));
        }

    }

    public static class MetalTags {
        public final TagKey<Item> ingot;
        public final TagKey<Item> nugget;
        @Nullable
        public final TagKey<Item> rawOre;
        public final TagKey<Item> plate;
        public final TagKey<Item> dust;
        public final TagKey<Block> storage;
        @Nullable
        public final TagKey<Block> ore;
        @Nullable
        public final TagKey<Block> rawBlock;

        private MetalTags(INEnumMetals m) {
            String name = m.tagName();
            TagKey<Block> ore = null;
            TagKey<Item> rawOre = null;
            TagKey<Block> rawBlock = null;
            if (m.shouldAddOre()) {
                ore = INTags.createBlockTag(INTags.getOre(name));
                rawOre = TagUtils.createItemWrapper(INTags.getRawOre(name));
                rawBlock = INTags.createBlockTag(INTags.getRawBlock(name));
            }

            if (!m.isVanillaMetal()) {
                this.storage = INTags.createBlockTag(INTags.getStorageBlock(name));
            } else {
                this.storage = null;
            }

            if(!m.isIsotope()) {
                this.plate = TagUtils.createItemWrapper(INTags.getPlate(name));
                this.dust = TagUtils.createItemWrapper(INTags.getDust(name));
            } else {
                this.plate = null;
                this.dust = null;
            }

            this.nugget = TagUtils.createItemWrapper(INTags.getNugget(name));
            this.ingot = TagUtils.createItemWrapper(INTags.getIngot(name));
            this.ore = ore;
            this.rawOre = rawOre;
            this.rawBlock = rawBlock;
        }
    }
}