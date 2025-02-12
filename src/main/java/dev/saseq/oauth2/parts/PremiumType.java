package dev.saseq.oauth2.parts;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PremiumType {

    NONE(0),
    NITRO_CLASSIC(1),
    NITRO(2),
    NITRO_BASIC(3);

    private final int value;

    public static PremiumType fromValue(long value) {
        for (PremiumType premiumType : PremiumType.values()) {
            if (premiumType.value == value) {
                return premiumType;
            }
        }
        return null;
    }
}
