package igentuman.immersivenuclear.common.world;

import com.mojang.serialization.Codec;
import igentuman.immersivenuclear.common.config.IEServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement;

public class IECountPlacement extends RepeatingPlacement {
    public static final Codec<IECountPlacement> CODEC;
    private final IEServerConfig.Ores.VeinType type;

    public IECountPlacement(IEServerConfig.Ores.VeinType type) {
        this.type = type;
    }

    protected int count(RandomSource p_191913_, BlockPos p_191914_) {
        return (Integer)((IEServerConfig.Ores.OreConfig)IEServerConfig.ORES.ores.get(this.type)).veinsPerChunk.get();
    }

    public PlacementModifierType<?> type() {
        return (PlacementModifierType)IEWorldGen.IE_COUNT_PLACEMENT.get();
    }

    static {
        CODEC = IEServerConfig.Ores.VeinType.CODEC.xmap(IECountPlacement::new, (p) -> p.type);
    }
}
