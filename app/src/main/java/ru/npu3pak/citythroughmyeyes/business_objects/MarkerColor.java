package ru.npu3pak.citythroughmyeyes.business_objects;

public enum MarkerColor {
    RED(0xb71c1c),
    GREEN(0x00695c),
    BLUE(0x01579b),
    YELLOW(0xfb8c00),
    BROWN(0x795548),
    PURPLE(0x4527a0),
    INDIGO(0x283593),
    GREY(0x455a64);

    private int intValue;

    MarkerColor(int intValue) {
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }
}