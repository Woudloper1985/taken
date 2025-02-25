package be.vdab.taken;

public class Soort {
    private final long id;
    private final String naam;

    public Soort(long id, String naam) {
        this.id = id;
        this.naam = naam;
    }

    @Override
    public String toString() {
        return "id: " + id + ", naam: " + naam;
    }
}
