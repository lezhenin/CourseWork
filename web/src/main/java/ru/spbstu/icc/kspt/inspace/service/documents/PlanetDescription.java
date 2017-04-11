package ru.spbstu.icc.kspt.inspace.service.documents;


import ru.spbstu.icc.kspt.inspace.api.APlanet;


public class PlanetDescription {

    private final String name;
    private final Position position;
    private final String url;

    public PlanetDescription(APlanet planet) {
        this.name = planet.getName();
        position = new Position(planet.getPosition());
        url = "/planets/" + planet.getPosition().getNumberOfSystem()
                + "/" + planet.getPosition().getNumberOfPlanet();
    }

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    public String getUrl() {
        return url;
    }
}
