package ru.spbstu.icc.kspt.inspace.model;



import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;
import ru.spbstu.icc.kspt.inspace.api.*;
import ru.spbstu.icc.kspt.inspace.model.buildings.BuildingType;
import ru.spbstu.icc.kspt.inspace.model.exception.*;
import ru.spbstu.icc.kspt.inspace.model.fleet.ShipType;
import ru.spbstu.icc.kspt.inspace.model.fleet.missions.Attack;
import ru.spbstu.icc.kspt.inspace.model.fleet.missions.Comeback;
import ru.spbstu.icc.kspt.inspace.model.fleet.missions.MissionType;
import ru.spbstu.icc.kspt.inspace.model.research.ResearchType;
import ru.spbstu.icc.kspt.inspace.model.resources.Resources;
import ru.spbstu.icc.kspt.inspace.model.utils.Time;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;


@RunWith(PowerMockRunner.class)
@PrepareForTest(Time.class)
public class PlanetTest {

    private APlanet planet;
    private APlanet anotherPlanet;

    public PlanetTest() throws PlanetDoesntExist {
        Galaxy.getInstance().addPlanet(new Position(2, 4), "Nibiru");
        Galaxy.getInstance().addPlanet(new Position(3, 5), "Another");
        planet = Galaxy.getInstance().getPlanet(2, 4);
        anotherPlanet = Galaxy.getInstance().getPlanet(3, 5);
    }

    @Test
    public void testUpdate() {

        assertEquals(planet.getResources(), new Resources(3000, 2000, 1000));

        PowerMockito.mockStatic(Time.class);
        when(Time.now()).thenReturn(LocalDateTime.now().plus(Duration.ofMinutes(531)));

        assertEquals(planet.getResources(), new Resources(3266, 2221, 1177));
    }

    @Test
    public void testGetBuildings() {
        PowerMockito.mockStatic(Time.class);
        when(Time.now()).thenReturn(LocalDateTime.now() .plus(Duration.ofMinutes(531)));
        System.out.println("Planet: " + planet.getName());
        System.out.println("Resources: " + planet.getResources());
        System.out.println("Energy level: " + planet.getEnergyLevel());
        System.out.println(planet.getNumberOfEmptyFields() + " fields are empty");
        System.out.println("Buildings:");
        System.out.println();
        for(Map.Entry<BuildingType, ? extends ABuilding> entry: planet.getBuildings().entrySet()) {
            ABuilding building = entry.getValue();
            System.out.println(entry.getKey());
            System.out.println("Level: " + building.getLevel());
            System.out.println("Cost: " + building.getUpgradeCost());
            System.out.println("Duration: " + building.getUpgradeDuration());
            System.out.println();
        }
    }

    @Test
    public void testResearchUpgrade() throws UpgradeException {
        PowerMockito.mockStatic(Time.class);
        when(Time.now()).thenReturn(LocalDateTime.now().plus(Duration.ofMinutes(531)));

        AResearch research = planet.getResearch(ResearchType.ENERGY);
        assertTrue(research.canBeUpgraded());
        assertEquals(research.getLevel(), 0);
        research.startUpgrade();

        PowerMockito.mockStatic(Time.class);
        when(Time.now()).thenReturn(LocalDateTime
                .now()
                .plus(Duration.ofMinutes(531))
                .plus(research.getUpgradeDuration()));
        planet.update();
        assertEquals(research.getLevel(), 1);
        assertEquals(planet.getResources(), new Resources(3226, 2160, 1130));
    }

    @Test
    public void testBuildingUpgrade() throws UpgradeException {

        PowerMockito.mockStatic(Time.class);
        when(Time.now()).thenReturn(LocalDateTime.now().plus(Duration.ofMinutes(531)));

        ABuilding building = planet.getBuilding(BuildingType.FACTORY);
        assertTrue(building.canBeUpgraded());
        assertEquals(building.getLevel(), 0);
        building.startUpgrade();

        PowerMockito.mockStatic(Time.class);
        when(Time.now()).thenReturn(LocalDateTime
                .now()
                .plus(Duration.ofMinutes(531))
                .plus(building.getUpgradeDuration()));
        planet.update();
        assertEquals(building.getLevel(), 1);
        assertEquals(planet.getResources(), new Resources(3207, 2172, 1178));
    }

    @Test
    public void testEnergySystem() throws UpgradeException {

        assertEquals(planet.getEnergyLevel(), 0);

        PowerMockito.mockStatic(Time.class);
        when(Time.now()).thenReturn(LocalDateTime.now().plus(Duration.ofMinutes(531)));
        planet.getBuilding(BuildingType.POWER_STATION).startUpgrade();

        when(Time.now()).thenReturn(LocalDateTime.now().plus(Duration.ofMinutes(540)));
        assertEquals(planet.getEnergyLevel(), 100);
        assertEquals(planet.getEnergyProduction(), 100);
        assertEquals(planet.getEnergyConsumption(), 0);
        planet.getBuilding(BuildingType.DEUTERIUM_MINE).startUpgrade();

        when(Time.now()).thenReturn(LocalDateTime.now().plus(Duration.ofMinutes(550)));
        assertEquals(planet.getEnergyLevel(), 37);
        assertEquals(planet.getEnergyProduction(), 100);
        assertEquals(planet.getEnergyConsumption(), 63);
        planet.getBuilding(BuildingType.CRYSTAL_MINE).startUpgrade();

        when(Time.now()).thenReturn(LocalDateTime.now().plus(Duration.ofMinutes(560)));
        assertEquals(planet.getEnergyLevel(), 0);
        assertEquals(planet.getEnergyProduction(), 100);
        assertEquals(planet.getEnergyConsumption(), 100);
        assertTrue(planet.getProductionPower() < 1);

        planet.update();
        assertEquals(planet.getEnergyLevel(), 0);
        assertEquals(planet.getEnergyConsumption(), planet.getEnergyProduction());
        assertEquals(planet.getEnergyConsumption(), 100);


    }

//    @Test
//    public void testFleets() throws ConstructException {
//
//        planet.getResources().putResources(new Resources(100000, 100000, 100000));
//        anotherPlanet.getResources().putResources(new Resources(100000, 100000, 100000));
//
//        Iterator<Map.Entry<ShipType, ShipModel>> iterator = planet.getShips().entrySet().iterator();
//        iterator.next().getValue().startConstruction(15);
//
//        iterator = anotherPlanet.getShips().entrySet().iterator();
//        iterator.next().getValue().startConstruction(14);
//
//        PowerMockito.mockStatic(Time.class);
//        when(Time.now()).thenReturn(LocalDateTime.now().plus(Duration.ofMinutes(531)));
//
//        Fleet fleet1 = planet.getFleetOnPlanet().detachFleet();
//        assertEquals(15, fleet1.getNumberOfShips());
//        assertEquals(0, planet.getFleetOnPlanet().getNumberOfShips());
//
//        Fleet fleet2 = anotherPlanet.getFleetOnPlanet().detachFleet();
//        assertEquals(14, fleet2.getNumberOfShips());
//        assertEquals(0, anotherPlanet.getFleetOnPlanet().getNumberOfShips());
//
//        fleet1.attack(fleet2);
//        assertEquals(13, fleet1.getNumberOfShips());
//        assertEquals(0, fleet2.getNumberOfShips());
//
//        planet.getFleetOnPlanet().attachFleet(fleet1);
//        anotherPlanet.getFleetOnPlanet().attachFleet(fleet2);
//        assertEquals(13, planet.getFleetOnPlanet().getNumberOfShips());
//        assertEquals(0, anotherPlanet.getFleetOnPlanet().getNumberOfShips());
//
//    }

    @Test
    public void testMissions() throws ConstructException, FleetDetachException, PlanetDoesntExist, CapacityExcessException {

        PowerMockito.mockStatic(Time.class);
        when(Time.now()).thenReturn(LocalDateTime.now().plus(Duration.ofMinutes(5310)));

        Iterator<? extends Map.Entry<ShipType, ? extends AShipModel>> iterator;
        iterator = planet.getShips().entrySet().iterator();
        iterator.next().getValue().startConstruction(15);
        iterator = anotherPlanet.getShips().entrySet().iterator();
        iterator.next().getValue().startConstruction(14);

        when(Time.now()).thenReturn(LocalDateTime.now().plus(Duration.ofMinutes(531000)));

        planet.update();
        anotherPlanet.update();

        //TODO empty fleets
        planet.startMission(MissionType.ATTACK, anotherPlanet.getPosition(), planet.getFleetOnPlanet().getNumbersOfShips(),
                0, 0 ,0);

        assertTrue(!planet.getMissions().isEmpty());
        AMission currentMission = planet.getMissions().get(0);
        assertTrue(currentMission == anotherPlanet.getExternalMissions().get(0));
        assertTrue(currentMission.getClass().equals(Attack.class));
        assertTrue(anotherPlanet == currentMission.getDestination());
        assertTrue(planet == currentMission.getSource());
        assertEquals(25, currentMission.getFleet().getNumberOfShips());

        when(Time.now()).thenReturn(currentMission.getTime().plus(Duration.ofMinutes(1)));

        currentMission = planet.getMissions().get(0);
        assertTrue(anotherPlanet.getExternalMissions().isEmpty());
        assertTrue(planet.getExternalMissions().isEmpty());
        assertTrue(currentMission.getClass().equals(Comeback.class));
        assertTrue(anotherPlanet == currentMission.getSource());
        assertTrue(planet == currentMission.getDestination());
        assertEquals(18, currentMission.getFleet().getNumberOfShips());

    }
}
