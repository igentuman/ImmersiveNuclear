package igentuman.immersivenuclear.api;

import java.util.Locale;

public enum INEnumMetals {
    ZIRCONIUM(INEnumMetals.Type.IE_ALLOY,0.7F),
    THORIUM(1.0F),
    BORON(1.0F),
    URANIUM238(INEnumMetals.Type.IE_ISOTOPE,1.0F),
    URANIUM235(INEnumMetals.Type.IE_ISOTOPE,1.0F),
    URANIUM233(INEnumMetals.Type.IE_ISOTOPE,1.0F),
    PLUTONIUM239(INEnumMetals.Type.IE_ISOTOPE,1.0F),
    PLUTONIUM240(INEnumMetals.Type.IE_ISOTOPE, 1.0F),
    PLUTONIUM241(INEnumMetals.Type.IE_ISOTOPE, 1.0F),
    STAINLESS_STEEL(INEnumMetals.Type.IE_ALLOY, Float.NaN);

    private final Type type;
    public final float smeltingXP;

    private INEnumMetals(Type t, float xp) {
        this.type = t;
        this.smeltingXP = xp;
    }

    private INEnumMetals(float xp) {
        this.smeltingXP = xp;
        this.type = INEnumMetals.Type.IE_PURE;
    }

    public boolean isVanillaMetal() {
        return this.type == INEnumMetals.Type.VANILLA || this.type == INEnumMetals.Type.VANILLA_NO_NUGGET;
    }

    public boolean isAlloy() {
        return this.type == INEnumMetals.Type.IE_ALLOY;
    }

    public boolean isIsotope() {
        return this.type == Type.IE_ISOTOPE;
    }

    public boolean shouldAddOre() {
        return !this.isVanillaMetal() && !this.isAlloy() && !this.isIsotope();
    }

    public boolean shouldAddNugget() {
        return (!this.isVanillaMetal() || this.type == INEnumMetals.Type.VANILLA_NO_NUGGET) && !this.isIsotope();
    }

    public String tagName() {
        return this.name().toLowerCase(Locale.US);
    }

    private static enum Type {
        VANILLA,
        VANILLA_NO_NUGGET,
        IE_PURE,
        IE_ISOTOPE,
        IE_ALLOY;

        private Type() {
        }
    }
}
