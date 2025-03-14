package be.vdab.taken;

import java.time.LocalDate;
//test
public class Bier {
    private final long id;
    private final String naam;
    private final long brouwerId;
    private final long soortId;
    private final double alcohol;
    private final LocalDate sinds;

    public Bier(long id, String naam, long brouwerId, long soortId, double alcohol, LocalDate sinds) {
        this.id = id;
        this.naam = naam;
        this.brouwerId = brouwerId;
        this.soortId = soortId;
        this.alcohol = alcohol;
        this.sinds = sinds;
    }

    public String getNaam() {
        return naam;
    }

    public LocalDate getSinds() {
        return sinds;
    }

    @Override
    public String toString() {
        return "Bier{" +
                "id=" + id +
                ", naam='" + naam + '\'' +
                ", brouwId=" + brouwerId +
                ", soortId=" + soortId +
                ", alcohol=" + alcohol +
                ", sinds=" + sinds +
                '}';
    }
}
