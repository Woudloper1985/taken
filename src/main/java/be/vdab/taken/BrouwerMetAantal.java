package be.vdab.taken;

public record BrouwerMetAantal(String naam, int aantal) {
    @Override
    public String toString() {
        return naam + ", " + aantal;
    }
}
