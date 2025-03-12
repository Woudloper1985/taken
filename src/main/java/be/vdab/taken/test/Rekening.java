package be.vdab.taken.test;

public class Rekening {
    private final String rekeningnummer;

    public Rekening(String rekeningnummer) {
        valideer(rekeningnummer);
        this.rekeningnummer = rekeningnummer;
    }

    public static void valideer(String rekeningnummer) {
        if (rekeningnummer.length() != 16) {
            throw new IllegalArgumentException("Het rekeningnummer moet precies 16 tekens bevatten.");
        }
        if (!rekeningnummer.startsWith("BE")) {
            throw new IllegalArgumentException("Het rekeningnummer moet beginnen met 'BE'.");
        }

        int controlegetal;
        try {
            controlegetal = Integer.parseInt(rekeningnummer.substring(2, 4));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Het controlegetal moet een numerieke waarde (geheel getal) zijn.");
        }

        if (controlegetal < 2 || controlegetal > 98) {
            throw new IllegalArgumentException("Het controlegetal moet tussen 2 en 98 liggen.");
        }

        String comboGetal = rekeningnummer.substring(4) + "1114" + controlegetal;
        try {
            long getal = Long.parseLong(comboGetal);
            if (getal % 97 != 1) {
                throw new IllegalArgumentException("De uitkomst van het samengestelde getal gedeeld door 97 moet exact 1 zijn.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Het samengestelde getal is ongeldig (bevat bv. niet-gehele getallen).");
        }
    }

    public String getRekeningnummer() {
        return rekeningnummer;
    }

    @Override
    public String toString() {
        return "Rekeningnummer: " + rekeningnummer;
    }
}
