package igentuman.immersivenuclear.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;

import igentuman.immersivenuclear.common.config.IEServerConfig;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;

public class IEOreFeature extends Feature<IEOreFeature.IEOreFeatureConfig> {
    public IEOreFeature() {
        super(IEOreFeature.IEOreFeatureConfig.CODEC);
    }

    public boolean place(FeaturePlaceContext<IEOreFeatureConfig> ctx) {
        IEOreFeatureConfig config = (IEOreFeatureConfig)ctx.config();
        OreConfiguration vanillaConfig = new OreConfiguration(config.targetList, config.getSize(), (float)config.getAirExposure());
        return Feature.ORE.place(new FeaturePlaceContext(Optional.empty(), ctx.level(), ctx.chunkGenerator(), ctx.random(), ctx.origin(), vanillaConfig));
    }

    public static record IEOreFeatureConfig(List<OreConfiguration.TargetBlockState> targetList, IEServerConfig.Ores.VeinType type) implements FeatureConfiguration {
        public static final Codec<IEOreFeatureConfig> CODEC = RecordCodecBuilder.create((app) -> app.group(Codec.list(TargetBlockState.CODEC).fieldOf("targets").forGetter((cfg) -> cfg.targetList), IEServerConfig.Ores.VeinType.CODEC.fieldOf("type").forGetter((cfg) -> cfg.type)).apply(app, IEOreFeatureConfig::new));

        public IEOreFeatureConfig(List<OreConfiguration.TargetBlockState> targetList, IEServerConfig.Ores.VeinType type) {
            this.targetList = targetList;
            this.type = type;
        }

        public int getSize() {
            return (Integer)((IEServerConfig.Ores.OreConfig)IEServerConfig.ORES.ores.get(this.type)).veinSize.get();
        }

        public double getAirExposure() {
            return (Double)((IEServerConfig.Ores.OreConfig)IEServerConfig.ORES.ores.get(this.type)).airExposure.get();
        }

        public List<OreConfiguration.TargetBlockState> targetList() {
            return this.targetList;
        }

        public IEServerConfig.Ores.VeinType type() {
            return this.type;
        }
    }
}
