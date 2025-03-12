package be.vdab.taken.test;

public class Rekening {
    private final String rekeningnummer;

    public Rekening(String rekeningnummer) {
        // Controleer de lengte
        if (rekeningnummer.length() != 16) {
            throw new IllegalArgumentException("Het rekeningnummer moet precies 16 tekens bevatten.");
        }
        // Controleer of het begint met "BE"
        if (!rekeningnummer.startsWith("BE")) {
            throw new IllegalArgumentException("Het rekeningnummer moet beginnen met 'BE'.");
        }
        // Haal het controlegetal op (3e en 4e teken)
        int controlegetal;
        try {
            controlegetal = Integer.parseInt(rekeningnummer.substring(2, 4));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Het controlegetal moet een numerieke waarde (geheel getal) zijn.");
        }
        // Controleer of het controlegetal binnen het bereik ligt
        if (controlegetal < 2 || controlegetal > 98) {
            throw new IllegalArgumentException("Het controlegetal moet tussen 2 en 98 liggen.");
        }
        // Maak het samengestelde getal volgens de specificaties
        String hoofdgetal = rekeningnummer.substring(4) + "1114" + controlegetal;
        // Is het samengesteld getal gedeeld door 97 exact gelijk aan 1?
        try {
            long getal = Long.parseLong(hoofdgetal);
            if (getal % 97 != 1) {
                throw new IllegalArgumentException("Het samengestelde getal gedeeld door 97 moet exact gelijk aan 1 zijn.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Het samengestelde getal is ongeldig (bevat bv. iets anders dan gehele getallen).");
        }
        this.rekeningnummer = rekeningnummer;
    }

    public String getRekeningnummer() {
        return rekeningnummer;
    }
}
