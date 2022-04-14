package igentuman.immersivenuclear.common;

import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.common.blocks.tileentities.DistillationTowerTileEntity;
import igentuman.immersivenuclear.common.blocks.tileentities.GasGeneratorTileEntity;
import igentuman.immersivenuclear.common.blocks.tileentities.HydrotreaterTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class IPTileTypes{
	public static final DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ImmersiveNuclear.MODID);
	
	// Multiblocks
	public static final RegistryObject<TileEntityType<DistillationTowerTileEntity>> TOWER = register("distillationtower", DistillationTowerTileEntity::new, IPContent.Multiblock.distillationtower);
	public static final RegistryObject<TileEntityType<HydrotreaterTileEntity>> TREATER = register("hydrotreater", HydrotreaterTileEntity::new, IPContent.Multiblock.hydrotreater);
	
	// Normal Blocks
	public static final RegistryObject<TileEntityType<GasGeneratorTileEntity>> GENERATOR = register("gasgenerator", GasGeneratorTileEntity::new, IPContent.Blocks.gas_generator);

	private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<T> factory, Block... valid){
		return REGISTER.register(name, () -> new TileEntityType<>(factory, ImmutableSet.copyOf(valid), null));
	}
}
