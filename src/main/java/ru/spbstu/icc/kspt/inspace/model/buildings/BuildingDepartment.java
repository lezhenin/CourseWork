package ru.spbstu.icc.kspt.inspace.model.buildings;

import ru.spbstu.icc.kspt.inspace.model.Planet;
import ru.spbstu.icc.kspt.inspace.model.research.Research;
import ru.spbstu.icc.kspt.inspace.model.research.ResearchType;
import ru.spbstu.icc.kspt.inspace.model.utils.Time;

import java.util.*;

public class BuildingDepartment {

    private Planet planet;
    private BuildingUpgrade upgrading;
    private int occupiedFields = 0;

    private Map<BuildingType, Building> buildings = new EnumMap<>(BuildingType.class);
    private List<Mine> mines = new ArrayList<>();

    public BuildingDepartment(Planet planet) {
        this.planet = planet;

        buildings.put(BuildingType.FACTORY, new Factory(this));
        buildings.put(BuildingType.CRYSTAL_MINE, new CrystalMine(this));
        buildings.put(BuildingType.DEUTERIUM_MINE, new DeuteriumMine(this));
        buildings.put(BuildingType.METAL_MINE, new MetalMine(this));
        buildings.put(BuildingType.POWER_STATION, new PowerStation(this));

        mines.add((Mine)buildings.get(BuildingType.METAL_MINE));
        mines.add((Mine)buildings.get(BuildingType.CRYSTAL_MINE));
        mines.add((Mine)buildings.get(BuildingType.DEUTERIUM_MINE));
    }

    public void updateDependencies() {
        buildings.values().forEach(Building::updateDependencies);
    }

    boolean checkUpgradability(Building building) {
        return (planet.getResources().isEnough(building.getUpgradeCost()) &&
                upgrading == null && planet.getSize() - occupiedFields > 0);
    }

    void startUpgrade(BuildingUpgrade upgrading) {

        Building building = upgrading.getUpgradable();

        if (!checkUpgradability(building)){
            //TODO exception
            return;
        }

        planet.getResources().takeResources(building.getUpgradeCost());
        this.upgrading = upgrading;
    }

    public BuildingUpgrade getCurrentUpgrade() {
        updateBuildings();
        return upgrading;
    }

    public int getFields() {
        updateBuildings();
        return occupiedFields;
    }

    Research getResearch(ResearchType type) {
      return planet.getResearch(type);
    }

    public Building getBuilding(BuildingType type) {
        updateBuildings();
        return buildings.get(type);
    }

    public Set<Map.Entry<BuildingType, Building>> getBuildings() {
        updateBuildings();
        return buildings.entrySet();
    }

    public List<Mine> getMines() {
        updateBuildings();
        return mines;
    }

   public void updateBuildings() {
        if (upgrading != null && upgrading.getTime().compareTo(Time.now()) <= 0 ){
            upgrading.execute(Time.now());
            upgrading = null;
            occupiedFields++;
        }
    }
}
