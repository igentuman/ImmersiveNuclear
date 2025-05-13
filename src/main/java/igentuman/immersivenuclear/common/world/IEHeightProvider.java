package igentuman.immersivenuclear.common.world;

import com.mojang.serialization.Codec;
import igentuman.immersivenuclear.common.config.IEServerConfig;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraftforge.common.util.Lazy;

public class IEHeightProvider extends HeightProvider {
    public static final Codec<IEHeightProvider> CODEC;
    private final IEServerConfig.Ores.VeinType type;
    private final Lazy<HeightProvider> internalProvider;

    public IEHeightProvider(IEServerConfig.Ores.VeinType type) {
        this.type = type;
        this.internalProvider = Lazy.of(() -> {
            IEServerConfig.Ores.OreConfig config = (IEServerConfig.Ores.OreConfig)IEServerConfig.ORES.ores.get(type);
            VerticalAnchor vaMin = (pContext) -> (Integer)config.minY.get();
            VerticalAnchor vaMax = (pContext) -> (Integer)config.maxY.get();
            return (HeightProvider)(config.distribution.get() == IEServerConfig.Ores.OreDistribution.TRAPEZOID ? TrapezoidHeight.of(vaMin, vaMax) : UniformHeight.of(vaMin, vaMax));
        });
    }

    public int sample(RandomSource random, WorldGenerationContext context) {
        return ((HeightProvider)this.internalProvider.get()).sample(random, context);
    }

    public HeightProviderType<?> getType() {
        return (HeightProviderType)IEWorldGen.IE_HEIGHT_PROVIDER.get();
    }

    static {
        CODEC = IEServerConfig.Ores.VeinType.CODEC.xmap(IEHeightProvider::new, (p) -> p.type);
    }
}