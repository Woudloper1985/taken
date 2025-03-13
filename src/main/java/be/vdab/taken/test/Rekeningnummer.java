package be.vdab.taken.test;

final class Rekeningnummer {

    private Rekeningnummer() {
    } // final en abstract gaan niet tegelijk; hier voor final gekozen en private constructor om instanties te voorkomen.

    static String valideer(String rekeningnummer) {
        var foutmeldingen = new StringBuilder();

        if (rekeningnummer.length() != 16) {
            foutmeldingen.append("Het rekeningnummer moet precies 16 tekens bevatten.\n");
        }
        if (rekeningnummer.length() == 16) {
            if (!rekeningnummer.startsWith("BE")) {
                foutmeldingen.append("Het rekeningnummer moet beginnen met 'BE'.\n");
            }

            int controlegetal;
            try {
                controlegetal = Integer.parseInt(rekeningnummer.substring(2, 4));
                if (controlegetal < 2 || controlegetal > 98) {
                    foutmeldingen.append("Het controlegetal moet tussen 2 en 98 liggen.\n");
                }
            } catch (NumberFormatException e) {
                foutmeldingen.append("Het controlegetal moet een numerieke waarde (geheel getal) zijn.\n");
            }

            String comboGetal = rekeningnummer.substring(4) + "1114" + rekeningnummer.substring(2, 4);
            try {
                long getal = Long.parseLong(comboGetal);
                if (getal % 97 != 1) {
                    foutmeldingen.append("De uitkomst van het samengestelde getal gedeeld door 97 moet exact 1 zijn.\n");
                }
            } catch (NumberFormatException e) {
                foutmeldingen.append("Het samengestelde getal is ongeldig (bevat bv. niet-gehele getallen).\n");
            }
        }
        // Gooi een exception met alle foutmeldingen als er fouten zijn
        if (!foutmeldingen.isEmpty()) {
            throw new IllegalArgumentException(foutmeldingen.toString());
        }
        return rekeningnummer;
    }
}