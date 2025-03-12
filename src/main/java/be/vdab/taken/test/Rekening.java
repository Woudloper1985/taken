package be.vdab.taken.test;

public class Rekening {
    private final String rekeningnummer;

    public Rekening(String rekeningnummer) {
        valideer(rekeningnummer);
        this.rekeningnummer = rekeningnummer;
    }

    public static void valideer(String rekeningnummer) {
            var fouten = new StringBuilder(); // Verzamelt alle fouten

            if (rekeningnummer.length() != 16) {
                fouten.append("Het rekeningnummer moet precies 16 tekens bevatten.\n");
            }
            if (!rekeningnummer.startsWith("BE")) {
                fouten.append("Het rekeningnummer moet beginnen met 'BE'.\n");
            }

            int controlegetal;
            try {
                controlegetal = Integer.parseInt(rekeningnummer.substring(2, 4));
                if (controlegetal < 2 || controlegetal > 98) {
                    fouten.append("Het controlegetal moet tussen 2 en 98 liggen.\n");
                }
            } catch (NumberFormatException e) {
                fouten.append("Het controlegetal moet een numerieke waarde (geheel getal) zijn.\n");
            }

            String comboGetal = rekeningnummer.substring(4) + "1114" + rekeningnummer.substring(2, 4);
            try {
                long getal = Long.parseLong(comboGetal);
                if (getal % 97 != 1) {
                    fouten.append("De uitkomst van het samengestelde getal gedeeld door 97 moet exact 1 zijn.\n");
                }
            } catch (NumberFormatException e) {
                fouten.append("Het samengestelde getal is ongeldig (bevat bv. niet-gehele getallen).\n");
            }

            // Gooi een exception met alle fouten als er fouten zijn
            if (!fouten.isEmpty()) {
                throw new IllegalArgumentException(fouten.toString());
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