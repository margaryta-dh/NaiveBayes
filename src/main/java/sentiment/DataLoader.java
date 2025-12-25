package sentiment;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DataLoader {
    public static List<String> readLinesFromClasspath(String resourcePath) throws IOException {/*List<String> - jede Zeile als String(rückgabewert)*/
        InputStream in = DataLoader.class.getResourceAsStream(resourcePath);
        if (in == null) return Collections.emptyList();
        /*Lädt eine Datei aus dem Klassenpfad.
          getResourceAsStream(resourcePath)
          → öffnet eine Datei aus den Resource-Ordnern.
          Beispiel: "/data/train/pos.txt" → sucht in src/main/resources/data/train/pos.txt.
          Falls Datei nicht existiert: gibt einfach eine leere Liste zurück (verhindert Absturz).*/

        List<String> lines = new ArrayList<String>();//sammelt alle gelesenen Zeilen
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            /*ImputStream -Bytes; InputStreamReader -Bytes-Zeichen. BufferedReader - effizientesZeilenweises Lesen(readLine())*/
            String line;
            while ((line = br.readLine()) != null) {//readline() liest die Zeile bis dem Zeilenumbruch
                String t = line.trim();
                if (!t.isEmpty()) lines.add(t);//prüft ob nach dem trim() noch irgendwas drinsteht. wenn die Zeile leer ist, wird sie übersprungen.
                /*Nur nicht leere gesäuberte Zeileln werden indie Ergebnisliste lines aufgenommen*/
            }
            /*Klassische Read-Loop bis zum Ende. trim() entfernt am Anfang und am Ende Leerzeichen.
            * leere Zeilen werden ignoriert. */
        } finally {
            if (br != null) try {
                br.close(); //gibt die Ressource frei
            } catch (IOException ignore) {}/*close() wirft selbst wieder eine IOException.
            Um zu verhindern, dass unser Programm beim Schließen crasht, fangen wir die Exception mit catch(IOException) ignore.*/
            try { in.close(); //
            } catch (IOException ignore) {}
        }/*alles im finally wird garantiert ausgeführt, auch wenn in try ein return oer exception war.*/
        return lines;
    }
/*fürs bessere Verständnis:
InputStream = Wasserhahn aufdrehen.

BufferedReader = Glas unter den Hahn halten.

Lesen = Wasser ins Glas laufen lassen.

close() = Hahn wieder zudrehen.

Wenn du den Hahn nicht zudrehst, läuft das Wasser weiter, auch wenn du das Glas nicht mehr benutzt.
=>Darum immer close*/
    public static Map<String, List<String>> loadLabeledFromClasspath(String posRes, String negRes) throws IOException {
        Map<String, List<String>> data = new HashMap<String, List<String>>();//erstellt eine leere Hash-Map. Typ:Key =String, Value =List<String>
        data.put("pos", readLinesFromClasspath(posRes));//liest die Datei aus resoursen train zB. und gibt eine List<Stirng> zurück ->jede Zeile ein Satz.
        //in die Map kommt ein neuer Eintrag. Schlüssel: "pos",Wert=Liste der positiven Sätze.
        data.put("neg", readLinesFromClasspath(negRes));//
        return data;//gibt die fertige Map zurück
    }
}
/*Zusammenfassung
Liest Textdateien aus den Ressourcen

Dateien liegen unter src/main/resources

Geht Zeile für Zeile durch

readLine() liest eine Zeile bis zum Zeilenumbruch

trim() entfernt nur führende oder abschließende Leerzeichen, Tabs, Zeilenumbrüche

Leere Zeilen werden ignoriert

Speichert die Sätze in einer Liste

Jede nichtleere und gesäuberte Zeile ist ein Element in List<String>

Verpackt diese Listen in eine Map

Schlüssel "pos" → Liste mit allen positiven Beispielen

Schlüssel "neg" → Liste mit allen negativen Beispielen

Rückgabe: Map<String, List<String>>
 */
