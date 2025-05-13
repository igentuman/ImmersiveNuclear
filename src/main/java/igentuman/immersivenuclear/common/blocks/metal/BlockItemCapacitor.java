package igentuman.immersivenuclear.common.blocks.metal;

import blusunrize.immersiveengineering.common.blocks.BlockItemIE;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.SimpleCapProvider;
import igentuman.immersivenuclear.common.config.IEServerConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

public class BlockItemCapacitor extends BlockItemIE {
    private final IEServerConfig.Machines.CapacitorConfig configValues;

    public BlockItemCapacitor(Block b, IEServerConfig.Machines.CapacitorConfig configValues) {
        super(b, new Item.Properties());
        this.configValues = configValues;
    }

    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return (ICapabilityProvider)(!stack.isEmpty() ? new SimpleCapProvider(() -> ForgeCapabilities.ENERGY, new EnergyHelper.ItemEnergyStorage(stack, (value) -> this.configValues.storage.getAsInt())) : super.initCapabilities(stack, nbt));
    }
}
