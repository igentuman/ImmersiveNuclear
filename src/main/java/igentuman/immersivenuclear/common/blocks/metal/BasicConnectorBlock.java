package igentuman.immersivenuclear.common.blocks.metal;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.common.blocks.generic.ConnectorBlock;
import blusunrize.immersiveengineering.common.blocks.metal.EnergyConnectorBlockEntity;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import com.mojang.datafixers.util.Pair;
import java.util.Locale;

import igentuman.immersivenuclear.common.register.INBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.RegistryObject;

public class BasicConnectorBlock<T extends BlockEntity & IImmersiveConnectable> extends ConnectorBlock<T> {
    public BasicConnectorBlock(BlockBehaviour.Properties props, RegistryObject<BlockEntityType<T>> type) {
        super(props, type);
    }

    public static INBlocks.BlockEntry<BasicConnectorBlock<?>> forPower(String voltage, boolean relay) {
        return new INBlocks.BlockEntry("connector_" + voltage.toLowerCase(Locale.US) + (relay ? "_relay" : ""), PROPERTIES, (p) -> new BasicConnectorBlock((Properties) p, (RegistryObject) EnergyConnectorBlockEntity.SPEC_TO_TYPE.get(Pair.of(voltage, relay))));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(new Property[]{IEProperties.FACING_ALL, BlockStateProperties.WATERLOGGED});
    }
}