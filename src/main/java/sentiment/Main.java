package sentiment;
import java.nio.file.*;
import java.util.*;
public class Main {
 private static void evaluate(NaiveBayes nb, Map<String,List<String>>test){
     int correct=0,total=0;
     for(Map.Entry<String,List<String>> e:test.entrySet()){
         String gold = e.getKey();
         for(String txt :e.getValue()){
             String pred = nb.predict(txt);
             if(gold.equals(pred)){
                 correct++;
             }
             total++;
         }
     }
     if (total>0){
         double acc = (100.0 *correct)/total;
         System.out.printf("Evaluation:%d/%d korrekt (%.2f%%)\n",correct,total,acc);
     }else{
         System.out.println("Keine Testdaten gefunden-überspringe Evaluation.");
     }
 }
 private static String joinArgs(String[]args) {
     if (args == null || args.length == 0) {
         return "";
     }
     StringBuilder sb = new StringBuilder();
     for (int i = 0; i < args.length; i++) {
         if (i > 0) {
             sb.append(' ');
         }
         sb.append(args[i]);
     }
     return sb.toString();
 }

    public static void main(String[] args) throws Exception {
     Path base = Paths.get("").toAbsolutePath();
     Path trainPos = base.resolve("data/train/pos.txt");
     Path trainNeg = base.resolve("data/train/neg.txt");
     Path testPos = base.resolve("data/test/pos.txt");
     Path testNeg = base.resolve("data/test/neg.txt");

        System.out.println("Lade Trainingsdaten...");
        Map<String, List<String>> train = DataLoader.loadLabeledFromClasspath("/data/train/pos.txt",
                "/data/train/neg.txt");

        System.out.println("Trainiere Naive Bayes...");
        NaiveBayes nb = new NaiveBayes(1.0); // Laplace alpha = 1.0
        nb.fit(train);
        System.out.printf("Vokabulargröße: %d\n", nb.getVocabulary().size());

        System.out.println("Evaluieren (falls Testdaten vorhanden)...");
        Map<String, List<String>> test = DataLoader.loadLabeledFromClasspath(
                "/data/test/pos.txt",
                "/data/test/neg.txt");
        evaluate(nb, test);

        // CLI Nutzung:
        // 1) Wenn Argumente übergeben -> zusammenhängender Text
        // 2) Sonst interaktiver Prompt (StdIn)
        String userText;
        if (args!=null && args.length > 0) {
            userText = joinArgs(args);
        } else {
            System.out.println("\nGib einen Satz ein (Enter zum Klassifizieren, leere Eingabe beendet):");
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.print("> ");
                userText = sc.nextLine();
                if (userText == null || userText.trim().isEmpty()) break;
                String pred = nb.predict(userText);
                Map<String,Double> proba = nb.predictProba(userText);
                double pPos = getOrDefault(proba, "pos", 0.0);
                double pNeg = getOrDefault(proba, "neg", 0.0);
                System.out.printf("Vorhersage: %s  |  P(pos)=%.3f  P(neg)=%.3f%n", pred, pPos, pNeg);
            }
            System.out.println("Fertig.");
            return;
        }

        String pred = nb.predict(userText);
        Map<String,Double> proba = nb.predictProba(userText);
        double pPos = getOrDefault(proba, "pos", 0.0);
        double pNeg = getOrDefault(proba, "neg", 0.0);
        System.out.printf("%nText: %s%nVorhersage: %s  |  P(pos)=%.3f  P(neg)=%.3f%n", userText, pred, pPos, pNeg);
    }

    private static double getOrDefault(Map<String, Double> m, String k, double def) {
        Double v = m.get(k);
        return (v == null) ? def : v.doubleValue();
    }

}
