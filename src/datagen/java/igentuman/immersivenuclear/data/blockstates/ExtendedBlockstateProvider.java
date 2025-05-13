package igentuman.immersivenuclear.data.blockstates;

import blusunrize.immersiveengineering.api.IEProperties;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.data.DataGenUtils;
import igentuman.immersivenuclear.data.models.*;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static igentuman.immersivenuclear.api.Lib.MODID;

public abstract class ExtendedBlockstateProvider extends BlockStateProvider {
    protected static final List<Vec3i> COLUMN_THREE;
    protected static final Map<ResourceLocation, String> generatedParticleTextures;
    protected final ExistingFileHelper existingFileHelper;
    protected final NongeneratedModels innerModels;

    public ExtendedBlockstateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MODID, exFileHelper);
        this.existingFileHelper = exFileHelper;
        this.innerModels = new NongeneratedModels(output, this.existingFileHelper);
    }

    protected String name(Supplier<? extends Block> b) {
        return this.name((Block)b.get());
    }

    protected String name(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b).getPath();
    }

    public void simpleBlockAndItem(Supplier<? extends Block> b, ModelFile model) {
        this.simpleBlockAndItem(b, new ConfiguredModel(model));
    }

    protected void simpleBlockAndItem(Supplier<? extends Block> b, ConfiguredModel model) {
        this.simpleBlock((Block)b.get(), new ConfiguredModel[]{model});
        this.itemModel(b, model.model);
    }

    public void multiBlockAndItem(Supplier<? extends Block> b, ModelFile... models) {
        this.simpleBlock((Block)b.get(), (ConfiguredModel[])Stream.of(models).map(ConfiguredModel::new).toArray((x$0) -> new ConfiguredModel[x$0]));
        this.itemModel(b, (new ConfiguredModel(models[0])).model);
    }

    protected void cubeSideVertical(Supplier<? extends Block> b, ResourceLocation side, ResourceLocation vertical) {
        this.simpleBlockAndItem(b, (ModelFile)this.models().cubeBottomTop(this.name(b), side, vertical, vertical));
    }

    protected void cubeAll(Supplier<? extends Block> b, ResourceLocation texture) {
        this.cubeAll(b, texture, (RenderType)null);
    }

    protected void cubeAll(Supplier<? extends Block> b, ResourceLocation texture, @Nullable RenderType layer) {
        BlockModelBuilder model = (BlockModelBuilder)this.models().cubeAll(this.name(b), texture);
        this.setRenderType(layer, model);
        this.simpleBlockAndItem(b, (ModelFile)model);
    }

    protected void multiCubeAll(Supplier<? extends Block> b, ResourceLocation... textures) {
        this.multiCubeAll(b, (RenderType)null, textures);
    }

    protected void multiCubeAll(Supplier<? extends Block> b, @Nullable RenderType layer, ResourceLocation... textures) {
        BlockModelBuilder[] models = new BlockModelBuilder[textures.length];

        for(int i = 0; i < textures.length; ++i) {
            models[i] = (BlockModelBuilder)this.models().cubeAll(this.name(b) + i, textures[i]);
            this.setRenderType(layer, models[i]);
        }

        this.multiBlockAndItem(b, models);
    }

    protected void multiEightCubeAll(Supplier<? extends Block> b, ResourceLocation texture) {
        ResourceLocation[] textures = new ResourceLocation[8];

        for(int i = 0; i < 8; ++i) {
            String var10004 = texture.toString();
            textures[i] = new ResourceLocation(var10004 + i);
        }

        this.multiCubeAll(b, textures);
    }

    protected void scaffold(Supplier<? extends Block> b, ResourceLocation others, ResourceLocation top) {
        this.simpleBlockAndItem(b, (ModelFile)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(this.name(b), this.modLoc("block/ie_scaffolding"))).texture("side", others)).texture("bottom", others)).texture("top", top)).renderType(ModelProviderUtils.getName(RenderType.cutout())));
    }

    public void slabBlock(SlabBlock block, ModelFile[] bottom, ModelFile[] top, ModelFile[] doubleslab) {
        this.getVariantBuilder(block).partialState().with(SlabBlock.TYPE, SlabType.BOTTOM).addModels((ConfiguredModel[])Stream.of(bottom).map(ConfiguredModel::new).toArray((x$0) -> new ConfiguredModel[x$0])).partialState().with(SlabBlock.TYPE, SlabType.TOP).addModels((ConfiguredModel[])Stream.of(top).map(ConfiguredModel::new).toArray((x$0) -> new ConfiguredModel[x$0])).partialState().with(SlabBlock.TYPE, SlabType.DOUBLE).addModels((ConfiguredModel[])Stream.of(doubleslab).map(ConfiguredModel::new).toArray((x$0) -> new ConfiguredModel[x$0]));
    }

    public void wallBlock(WallBlock block, ModelFile[] posts, ModelFile[] sides, ModelFile[] sidesTall) {
        for(int i = 0; i < posts.length; ++i) {
            ModelFile side = sides[i];
            ModelFile sideTall = sidesTall[i];
            MultiPartBlockStateBuilder builder = ((MultiPartBlockStateBuilder.PartBuilder)this.getMultipartBuilder(block).part().modelFile(posts[i]).addModel()).condition(WallBlock.UP, new Boolean[]{true}).end();
            WALL_PROPS.entrySet().stream().filter((e) -> ((Direction)e.getKey()).getAxis().isHorizontal()).forEach((e) -> {
                this.wallSidePart(builder, side, e, WallSide.LOW);
                this.wallSidePart(builder, sideTall, e, WallSide.TALL);
            });
        }

    }

    private void wallSidePart(MultiPartBlockStateBuilder builder, ModelFile model, Map.Entry<Direction, Property<WallSide>> entry, WallSide height) {
        ((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile(model).rotationY(((int)((Direction)entry.getKey()).toYRot() + 180) % 360).uvLock(true).addModel()).condition((Property)entry.getValue(), new WallSide[]{height});
    }

    protected void setRenderType(@Nullable RenderType type, ModelBuilder<?>... builders) {
        if (type != null) {
            String typeName = ModelProviderUtils.getName(type);

            for(ModelBuilder<?> model : builders) {
                model.renderType(typeName);
            }
        }

    }

    protected ResourceLocation forgeLoc(String path) {
        return new ResourceLocation("forge", path);
    }

    protected ResourceLocation addModelsPrefix(ResourceLocation in) {
        return new ResourceLocation(in.getNamespace(), "models/" + in.getPath());
    }

    protected void itemModel(Supplier<? extends Block> block, ModelFile model) {
        ((ItemModelBuilder)this.itemModels().getBuilder(this.name(block))).parent(model);
    }

    protected BlockModelBuilder wallModelTopped(String name, String type, ResourceLocation bottom, ResourceLocation side, ResourceLocation top) {
        return (BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name, ImmersiveNuclear.rl("block/" + type))).texture("wall_bottom", bottom)).texture("wall_side", side)).texture("wall_top", top);
    }

    protected BlockModelBuilder wallModelToppedInventory(String name, ResourceLocation bottom, ResourceLocation side, ResourceLocation top) {
        return (BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name, ImmersiveNuclear.rl("block/wall_inventory_topped"))).texture("wall_bottom", bottom)).texture("wall_side", side)).texture("wall_top", top);
    }

    protected NongeneratedModels.NongeneratedModel innerObj(String loc, @Nullable RenderType layer) {
        Preconditions.checkArgument(loc.endsWith(".obj"));
        NongeneratedModels.NongeneratedModel result = (NongeneratedModels.NongeneratedModel)this.obj(loc.substring(0, loc.length() - 4), this.modLoc(loc), this.innerModels);
        this.setRenderType(layer, result);
        return result;
    }

    protected NongeneratedModels.NongeneratedModel innerObj(String loc) {
        return this.innerObj(loc, (RenderType)null);
    }

    protected BlockModelBuilder obj(String loc) {
        return this.obj(loc, (RenderType)null);
    }

    protected BlockModelBuilder obj(String loc, @Nullable RenderType layer) {
        BlockModelBuilder model = (BlockModelBuilder)this.obj(loc, (ModelProvider)this.models());
        this.setRenderType(layer, model);
        return model;
    }

    protected <T extends ModelBuilder<T>> T obj(String loc, ModelProvider<T> modelProvider) {
        Preconditions.checkArgument(loc.endsWith(".obj"));
        return (T)this.obj(loc.substring(0, loc.length() - 4), this.modLoc(loc), modelProvider);
    }

    protected <T extends ModelBuilder<T>> T obj(String name, ResourceLocation model, ModelProvider<T> provider) {
        return (T)this.obj(name, model, ImmutableMap.of(), provider);
    }

    protected <T extends ModelBuilder<T>> T obj(String name, ResourceLocation model, Map<String, ResourceLocation> textures, ModelProvider<T> provider) {
        return (T)this.obj(provider.withExistingParent(name, this.mcLoc("block")), model, textures);
    }

    protected <T extends ModelBuilder<T>> T obj(T base, ResourceLocation model, Map<String, ResourceLocation> textures) {
        this.assertModelExists(model);
        T ret = (T) ((ObjModelBuilder)base.customLoader(ObjModelBuilder::begin)).automaticCulling(false).modelLocation(this.addModelsPrefix(model)).flipV(true).end();
        String particleTex = DataGenUtils.getTextureFromObj(model, this.existingFileHelper);
        if (particleTex.charAt(0) == '#') {
            particleTex = ((ResourceLocation)textures.get(particleTex.substring(1))).toString();
        }

        ret.texture("particle", particleTex);
        generatedParticleTextures.put(ret.getLocation(), particleTex);

        for(Map.Entry<String, ResourceLocation> e : textures.entrySet()) {
            ret.texture((String)e.getKey(), (ResourceLocation)e.getValue());
        }

        return ret;
    }

    protected BlockModelBuilder splitModel(String name, NongeneratedModels.NongeneratedModel model, List<Vec3i> parts, boolean dynamic) {
        BlockModelBuilder result = (BlockModelBuilder)((SplitModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name, this.mcLoc("block"))).customLoader(SplitModelBuilder::begin)).innerModel(model).parts(parts).dynamic(dynamic).end();
        this.addParticleTextureFrom(result, model);
        return result;
    }

    protected ModelFile split(NongeneratedModels.NongeneratedModel baseModel, List<Vec3i> parts, boolean dynamic) {
        return this.splitModel(baseModel.getLocation().getPath() + "_split", baseModel, parts, dynamic);
    }

    protected ModelFile split(NongeneratedModels.NongeneratedModel baseModel, List<Vec3i> parts) {
        return this.split(baseModel, parts, false);
    }

    protected ModelFile splitDynamic(NongeneratedModels.NongeneratedModel baseModel, List<Vec3i> parts) {
        return this.split(baseModel, parts, true);
    }

    protected void addParticleTextureFrom(BlockModelBuilder result, ModelFile model) {
        String particles = (String)generatedParticleTextures.get(model.getLocation());
        if (particles != null) {
            result.texture("particle", particles);
            generatedParticleTextures.put(result.getLocation(), particles);
        }

    }

    protected ConfiguredModel emptyWithParticles(String name, String particleTexture) {
        ModelFile model = ((BlockModelBuilder)this.models().withExistingParent(name, this.modLoc("block/ie_empty"))).texture("particle", particleTexture);
        generatedParticleTextures.put(this.modLoc(name), particleTexture);
        return new ConfiguredModel(model);
    }

    public void assertModelExists(ResourceLocation name) {
        String suffix = name.getPath().contains(".") ? "" : ".json";
        Preconditions.checkState(this.existingFileHelper.exists(name, PackType.CLIENT_RESOURCES, suffix, "models"), "Model \"" + name + "\" does not exist");
    }

    protected IEOBJBuilder<BlockModelBuilder> ieObjBuilder(String loc) {
        return this.ieObjBuilder(getAutoNameIEOBJ(loc), this.modLoc(loc));
    }

    protected IEOBJBuilder<BlockModelBuilder> ieObjBuilder(String name, ResourceLocation model) {
        return this.<BlockModelBuilder>ieObjBuilder(name, model, this.models());
    }

    protected <T extends ModelBuilder<T>> IEOBJBuilder<T> ieObjBuilder(String loc, ModelProvider<T> modelProvider) {
        return this.<T>ieObjBuilder(getAutoNameIEOBJ(loc), this.modLoc(loc), modelProvider);
    }

    private static String getAutoNameIEOBJ(String loc) {
        Preconditions.checkArgument(loc.endsWith(".obj.ie"));
        return loc.substring(0, loc.length() - 7);
    }

    protected <T extends ModelBuilder<T>> IEOBJBuilder<T> ieObjBuilder(String name, ResourceLocation model, ModelProvider<T> modelProvider) {
        String particle = DataGenUtils.getTextureFromObj(model, this.existingFileHelper);
        generatedParticleTextures.put(this.modLoc(name), particle);
        return ((IEOBJBuilder)modelProvider.withExistingParent(name, this.mcLoc("block")).texture("particle", particle).customLoader(IEOBJBuilder::begin)).modelLocation(this.addModelsPrefix(model));
    }

    protected <T extends ModelBuilder<T>> T mirror(NongeneratedModels.NongeneratedModel inner, ModelProvider<T> provider) {
        return (T)((MirroredModelBuilder)provider.getBuilder(inner.getLocation().getPath() + "_mirrored").customLoader(MirroredModelBuilder::begin)).inner(inner).end();
    }

    protected int getAngle(Direction dir, int offset) {
        return (int)((dir.toYRot() + (float)offset) % 360.0F);
    }

    protected void createHorizontalRotatedBlock(Supplier<? extends Block> block, ModelFile model) {
        this.createHorizontalRotatedBlock(block, ($) -> model, List.of());
    }

    protected void createHorizontalRotatedBlock(Supplier<? extends Block> block, ModelFile model, int offsetRotY) {
        this.createRotatedBlock(block, (Function)(($) -> model), IEProperties.FACING_HORIZONTAL, List.of(), 0, offsetRotY);
    }

    protected void createHorizontalRotatedBlock(Supplier<? extends Block> block, Function<VariantBlockStateBuilder.PartialBlockstate, ModelFile> model, List<Property<?>> additionalProps) {
        this.createRotatedBlock(block, (Function)model, IEProperties.FACING_HORIZONTAL, additionalProps, 0, 180);
    }

    protected void createAllRotatedBlock(Supplier<? extends Block> block, ModelFile model) {
        this.createAllRotatedBlock(block, ($) -> model, List.of());
    }

    protected void createAllRotatedBlock(Supplier<? extends Block> block, Function<VariantBlockStateBuilder.PartialBlockstate, ModelFile> model, List<Property<?>> additionalProps) {
        this.createRotatedBlock(block, (Function)model, IEProperties.FACING_ALL, additionalProps, 90, 0);
    }

    protected void createRotatedBlock(Supplier<? extends Block> block, ModelFile model, Property<Direction> facing, List<Property<?>> additionalProps, int offsetRotX, int offsetRotY) {
        this.createRotatedBlock(block, (Function)(($) -> model), facing, additionalProps, offsetRotX, offsetRotY);
    }

    protected void createRotatedBlock(Supplier<? extends Block> block, Function<VariantBlockStateBuilder.PartialBlockstate, ModelFile> model, Property<Direction> facing, List<Property<?>> additionalProps, int offsetRotX, int offsetRotY) {
        VariantBlockStateBuilder stateBuilder = this.getVariantBuilder((Block)block.get());
        forEachState(stateBuilder.partialState(), additionalProps, (state) -> {
            ModelFile modelLoc = (ModelFile)model.apply(state);

            for(Direction d : facing.getPossibleValues()) {
                int x;
                int y;
                switch (d) {
                    case UP:
                        x = 90;
                        y = 0;
                        break;
                    case DOWN:
                        x = -90;
                        y = 0;
                        break;
                    default:
                        y = this.getAngle(d, offsetRotY);
                        x = 0;
                }

                state.with(facing, d).setModels(new ConfiguredModel[]{new ConfiguredModel(modelLoc, x + offsetRotX, y, false)});
            }

        });
    }

    protected static String getName(RenderStateShard state) {
        try {
            Field f = RenderStateShard.class.getDeclaredField("name");
            f.setAccessible(true);
            return (String)f.get(state);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Comparable<T>> void forEach(VariantBlockStateBuilder.PartialBlockstate base, Property<T> prop, List<Property<?>> remaining, Consumer<VariantBlockStateBuilder.PartialBlockstate> out) {
        for(T value : prop.getPossibleValues()) {
            forEachState(base, remaining, (map) -> {
                map = map.with(prop, value);
                out.accept(map);
            });
        }

    }

    public static void forEachState(VariantBlockStateBuilder.PartialBlockstate base, List<Property<?>> props, Consumer<VariantBlockStateBuilder.PartialBlockstate> out) {
        if (props.size() > 0) {
            List<Property<?>> remaining = props.subList(1, props.size());
            Property<?> main = (Property)props.get(0);
            forEach(base, main, remaining, out);
        } else {
            out.accept(base);
        }

    }

    static {
        COLUMN_THREE = ImmutableList.of(BlockPos.ZERO, BlockPos.ZERO.above(), BlockPos.ZERO.above(2));
        generatedParticleTextures = new HashMap();
    }
}
