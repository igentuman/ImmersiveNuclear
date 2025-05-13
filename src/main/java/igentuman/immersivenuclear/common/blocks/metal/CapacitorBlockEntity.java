package igentuman.immersivenuclear.common.blocks.metal;

import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.IEEnums.IOSideConfig;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.api.energy.NullEnergyStorage;
import blusunrize.immersiveengineering.api.energy.WrappingEnergyStorage;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import blusunrize.immersiveengineering.client.utils.TextUtils;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlockEntity;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.ticking.IEServerTickableBE;
import blusunrize.immersiveengineering.common.config.IEClientConfig;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.ResettableCapability;
import blusunrize.immersiveengineering.common.util.Utils;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import igentuman.immersivenuclear.common.config.IEServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class CapacitorBlockEntity extends IEBaseBlockEntity implements IEServerTickableBE, IEBlockInterfaces.IBlockOverlayText, IEBlockInterfaces.IConfigurableSides, IEBlockInterfaces.IComparatorOverride, IEBlockInterfaces.IBlockEntityDrop {
    public EnumMap<Direction, IEEnums.IOSideConfig> sideConfig = new EnumMap(Direction.class);
    private final IEServerConfig.Machines.CapacitorConfig configValues;
    private final IEnergyStorage energyStorage;
    private final Map<Direction, ResettableCapability<IEnergyStorage>> energyCaps = new EnumMap(Direction.class);
    private final ResettableCapability<IEnergyStorage> nullEnergyCap;
    public int comparatorOutput = 0;

    public CapacitorBlockEntity(IEServerConfig.Machines.CapacitorConfig configValues, BlockPos pos, BlockState state) {
        super((BlockEntityType)configValues.tileType.get(), pos, state);
        this.configValues = configValues;
        if (IEServerConfig.CONFIG_SPEC.isLoaded()) {
            this.energyStorage = this.makeMainEnergyStorage();
        } else {
            this.energyStorage = NullEnergyStorage.INSTANCE;
        }

        for(Direction f : DirectionUtils.VALUES) {
            if (f == Direction.UP) {
                this.sideConfig.put(f, IOSideConfig.INPUT);
            } else {
                this.sideConfig.put(f, IOSideConfig.NONE);
            }

            this.energyCaps.put(f, this.registerCapability(new CapacitorEnergyHandler(f, this.sideConfig, this.energyStorage)));
        }

        this.nullEnergyCap = this.registerCapability(new WrappingEnergyStorage(this.energyStorage, false, false));
    }

    public void tickServer() {
        for(Direction f : DirectionUtils.VALUES) {
            this.transferEnergy(f);
        }

        if (this.level.getGameTime() % 32L == (long)((this.getBlockPos().getX() ^ this.getBlockPos().getZ()) & 31)) {
            int i = this.scaleStoredEnergyTo(15);
            if (i != this.comparatorOutput) {
                this.comparatorOutput = i;
                this.level.updateNeighbourForOutputSignal(this.getBlockPos(), this.getBlockState().getBlock());
            }
        }

    }

    public int scaleStoredEnergyTo(int scale) {
        return (int)((float)scale * ((float)this.energyStorage.getEnergyStored() / (float)this.energyStorage.getMaxEnergyStored()));
    }

    protected void transferEnergy(Direction side) {
        if (this.sideConfig.get(side) == IOSideConfig.OUTPUT) {
            BlockPos outPos = this.getBlockPos().relative(side);
            BlockEntity tileEntity = Utils.getExistingTileEntity(this.level, outPos);
            int out = Math.min(this.getMaxOutput(), this.energyStorage.getEnergyStored());
            this.energyStorage.extractEnergy(EnergyHelper.insertFlux(tileEntity, side.getOpposite(), out, false), false);
        }
    }

    public IEEnums.IOSideConfig getSideConfig(Direction side) {
        return (IEEnums.IOSideConfig)this.sideConfig.get(side);
    }

    public boolean toggleSide(Direction side, Player player) {
        this.sideConfig.put(side, IOSideConfig.next((IEEnums.IOSideConfig)this.sideConfig.get(side)));
        this.setChanged();
        this.markContainingBlockForUpdate((BlockState)null);
        this.level.blockEvent(this.getBlockPos(), this.getBlockState().getBlock(), 0, 0);
        return true;
    }

    public boolean triggerEvent(int id, int arg) {
        if (id == 0) {
            this.markContainingBlockForUpdate((BlockState)null);
            return true;
        } else {
            return false;
        }
    }

    public final int getMaxStorage() {
        return this.configValues.storage.getAsInt();
    }

    public final int getMaxInput() {
        return this.configValues.input.getAsInt();
    }

    public final int getMaxOutput() {
        return this.configValues.output.getAsInt();
    }

    public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {
        for(Direction f : DirectionUtils.VALUES) {
            nbt.putInt("sideConfig_" + f.ordinal(), ((IEEnums.IOSideConfig)this.sideConfig.get(f)).ordinal());
        }

        IEnergyStorage var8 = this.energyStorage;
        if (var8 instanceof EnergyStorage forgeStorage) {
            EnergyHelper.serializeTo(forgeStorage, nbt);
        }

    }

    public void readCustomNBT(CompoundTag nbt, boolean descPacket) {
        for(Direction f : DirectionUtils.VALUES) {
            this.sideConfig.put(f, IOSideConfig.values()[nbt.getInt("sideConfig_" + f.ordinal())]);
        }

        IEnergyStorage var8 = this.energyStorage;
        if (var8 instanceof EnergyStorage forgeStorage) {
            EnergyHelper.deserializeFrom(forgeStorage, nbt);
        }

    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.ENERGY ? (side == null ? this.nullEnergyCap : (ResettableCapability)this.energyCaps.get(side)).cast() : super.getCapability(cap, side);
    }

    public Component[] getOverlayText(Player player, HitResult mop, boolean hammer) {
        if (hammer && (Boolean)IEClientConfig.showTextOverlay.get() && mop instanceof BlockHitResult bmop) {
            IEEnums.IOSideConfig here = (IEEnums.IOSideConfig)this.sideConfig.get(bmop.getDirection());
            IEEnums.IOSideConfig opposite = (IEEnums.IOSideConfig)this.sideConfig.get(bmop.getDirection().getOpposite());
            return TextUtils.sideConfigWithOpposite("desc.immersiveengineering.info.blockSide.connectEnergy.", here, opposite);
        } else {
            return null;
        }
    }

    public boolean useNixieFont(Player player, HitResult mop) {
        return false;
    }

    public int getComparatorInputOverride() {
        return this.comparatorOutput;
    }

    public void getBlockEntityDrop(LootContext context, Consumer<ItemStack> drop) {
        ItemStack stack = new ItemStack(this.getBlockState().getBlock(), 1);
        this.writeCustomNBT(stack.getOrCreateTag(), false);
        drop.accept(stack);
    }

    public void onBEPlaced(BlockPlaceContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        if (stack.hasTag()) {
            this.readCustomNBT(stack.getOrCreateTag(), false);
        }

    }

    protected IEnergyStorage makeMainEnergyStorage() {
        return new MutableEnergyStorage(this.getMaxStorage(), this.getMaxInput(), this.getMaxOutput());
    }

    private static record CapacitorEnergyHandler(Direction side, Map<Direction, IEEnums.IOSideConfig> sideConfigs, IEnergyStorage base) implements IEnergyStorage {
        private CapacitorEnergyHandler(Direction side, Map<Direction, IEEnums.IOSideConfig> sideConfigs, IEnergyStorage base) {
            this.side = side;
            this.sideConfigs = sideConfigs;
            this.base = base;
        }

        public int receiveEnergy(int maxReceive, boolean simulate) {
            return this.canReceive() ? this.base.receiveEnergy(maxReceive, simulate) : 0;
        }

        public int extractEnergy(int maxExtract, boolean simulate) {
            return this.canExtract() ? this.base.extractEnergy(maxExtract, simulate) : 0;
        }

        public int getEnergyStored() {
            return this.base.getEnergyStored();
        }

        public int getMaxEnergyStored() {
            return this.base.getMaxEnergyStored();
        }

        public boolean canExtract() {
            return this.sideConfigs.get(this.side) == IOSideConfig.OUTPUT;
        }

        public boolean canReceive() {
            return this.sideConfigs.get(this.side) == IOSideConfig.INPUT;
        }

        public Direction side() {
            return this.side;
        }

        public Map<Direction, IEEnums.IOSideConfig> sideConfigs() {
            return this.sideConfigs;
        }

        public IEnergyStorage base() {
            return this.base;
        }
    }
}
