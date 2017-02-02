package ru.spbstu.icc.kspt.inspace.model.resources;


import ru.spbstu.icc.kspt.inspace.api.AResources;

public class Resources implements AResources {

    private int metal;
    private int crystals;
    private int deuterium;

    public Resources(int metal, int crystals, int deuterium) {
        this.metal = metal;
        this.crystals = crystals;
        this.deuterium = deuterium;
    }

    public void putResources(Resources resources) {
        metal += resources.metal;
        crystals += resources.crystals;
        deuterium += resources.deuterium;
        resources.metal = 0;
        resources.crystals = 0;
        resources.deuterium = 0;
    }

    @Override
    public int getMetal() {
        return metal;
    }

    @Override
    public int getCrystals() {
        return crystals;
    }

    @Override
    public int getDeuterium() {
        return deuterium;
    }

    public Resources takeAllResources() {
        return takeResources(metal, crystals, deuterium);
    }

    public Resources takeResources(Resources resources) {
        return takeResources(resources.metal, resources.crystals, resources.deuterium);
    }

    public Resources takeResources(int metal, int crystals, int deuterium) {

        int takenMetal = (metal <= this.metal) ? metal : this.metal;
        int takenCrystals = (crystals <= this.metal) ? crystals : this.metal;
        int takenDeuterium = (deuterium <= this.metal) ? deuterium : this.metal;

        this.metal -= takenMetal;
        this.crystals -= takenCrystals;
        this.deuterium -= takenDeuterium;

        return new Resources(takenMetal, takenCrystals, takenDeuterium);
    }

    @Override
    public int getAmount() {
        return metal + crystals + deuterium;
    }

    @Override
    public int compareTo(Resources o) {
        if(metal == o.metal && crystals == o.crystals && deuterium == o.deuterium) {
            return 0;
        } else if (metal >= o.metal && crystals >= o.crystals && deuterium >= o.deuterium) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "Resources{" +
                "Metal:" + metal +
                ", Crystals: " + crystals +
                ", Deuterium: " + deuterium +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resources resources = (Resources) o;

        return metal == resources.metal &&
                crystals == resources.crystals &&
                deuterium == resources.deuterium;

    }

    @Override
    public int hashCode() {
        int result = metal;
        result = 31 * result + crystals;
        result = 31 * result + deuterium;
        return result;
    }
}
