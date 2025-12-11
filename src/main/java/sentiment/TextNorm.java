package sentiment;
import java.text.Normalizer;
import java.util.*;
public class TextNorm{
    private static final Set <String> STOPWORDS = new HashSet<>(Arrays.asList(
            "der","die","das","und","oder","ein","eine","einer","einem","einen","den",
            "ist","sind","war","waren","sein","mit","auf","zu","von","für","an",
            "im","in","am","ich","du","er","sie","es","wir","ihr","man","auch",
            "sehr","viel","mehr","wenig","aber","nur","so","noch","wie","als"
    ));//Arrays.asList macht aus literalen eine Liste, die direkt ins Set kopiert wird
    public static List<String> tokensize(String text){
        if (text==null) {
            return Collections.emptyList();//null rein = leere liste raus, anstatt NullPointerException ergeben
        }
        String norm = Normalizer.normalize(text.toLowerCase(Locale.ROOT),Normalizer.Form.NFKC);
        //Struktur die alle buchstaben gleich kelin macht, dann bringt das in eine Form Normalisierungsform NFC bringt, die Z.B ä gleich a+`` macht usw.
        //String lower = text.toLowerCase(Locale.ROOT);
        //String norm = Normalizer.normalize(lower, Normalizer.Form.NFKC); =>ist ja dasselbe.
        norm = norm.replaceAll("[^\\p{L}\\p{Nd}]+", " ").trim();//alles, was nicht ^, Buchstaben oder Dezimalziffer ist wird zu Leerzeichen ersetzt.
        //Bsp: „Science-Fiction!!!“ → „Science Fiction“(entfernt emojis,symbole etc)
        if (norm.isEmpty()) {
            return Collections.emptyList();
        }//falls nichts übrig bleibt, dann ergibt sich leere liste
        String[] raw = norm.split("\\s+");
        //zerlegt ein String in Teile durch ein Trennmuster. Z.B. "ich liebe diesen Film"=>[ich, liebe, diesen, film]
        List<String>tokens = new ArrayList<>(raw.length);
        for (String w:raw){
            if (w.length()<=1&& !Character.isDigit(w.charAt(0)))continue;//länge <=1 weg (a,1,x...)
            if (STOPWORDS.contains(w))continue;//(stopwörter weg)
            tokens.add(w); //den rest in die Liste aufnehmen
            //Der Film war großartig, aber etwas lang!!! 10/10=>["film","großartig","etwas","lang","10","10"]
        }
        return tokens;

    }
}
