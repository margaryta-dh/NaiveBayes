package sentiment;
import java.util.*;
public class NaiveBayes {
    private final Set<String>vocabulary = new HashSet<>();//menge aller Tockens, die im Training überhaupt vorkommen.Wird für Smoothing gebraucht
    private final Map<String,Integer> docCountPerClass= new HashMap<>();//für jede Klasse zB neg, pos wie viele Doks(Zeilen/Sätze) im Training sind
    private final Map<String,Integer> tokenCountPerClass= new HashMap<>();//für jede Klasse die gesamtanzahl aller Tocken mit wiederholung
    private final Map<String,Map<String,Integer>> tokenFreqsPerClass = new HashMap<>();
    private int totalDocs =0;
    private final double alpha;
    public NaiveBayes(double alpha){
        this.alpha=alpha;
    }
    public void fit(Map<String,List<String>>labeledTexts){
        docCountPerClass.clear();
        tokenCountPerClass.clear();
        tokenFreqsPerClass.clear();
        vocabulary.clear();
        totalDocs=0;

        for (Map.Entry<String,List<String>> e: labeledTexts.entrySet()){
            String clazz = e.getKey();
            List<String>texts =e.getValue();
            docCountPerClass.put(clazz, Integer.valueOf(texts.size()));
            totalDocs +=texts.size();
            Map<String,Integer>freq = tokenFreqsPerClass.get(clazz);
            if (freq==null){
                freq = new HashMap<String,Integer>();
                tokenFreqsPerClass.put(clazz,freq);
            }
            int tokenSum =0;
            for(String text:texts){
                List<String>tokens =TextUtils.tokensize(text);
                for(String tok:tokens){
                    vocabulary.add(tok);

                    Integer oldVal = freq.get(tok);
                    if (oldVal ==null){
                        oldVal =Integer.valueOf(0);
                    }
                    freq.put(tok,Integer.valueOf(oldVal.intValue()+1));
                    tokenSum+=1;
                }
            }
           tokenCountPerClass.put(clazz,Integer.valueOf(tokenSum));
        }
    }
    private double logPrior(String clazz){
        Integer dcObj = docCountPerClass.get(clazz);
        int dc = (dcObj==null)?0:dcObj.intValue();
        if(dc==0||totalDocs==0){
            return Double.NEGATIVE_INFINITY;
        }
        return Math.log((double)dc/(double)totalDocs);
    }
    private double logLikelihoodToken(String clazz, String token) {
        Map<String, Integer> freq = tokenFreqsPerClass.get(clazz);
        if (freq == null) freq = Collections.<String, Integer>emptyMap();

        Integer fObj = freq.get(token);
        int tokenFreq = (fObj == null) ? 0 : fObj.intValue();

        Integer sumObj = tokenCountPerClass.get(clazz);
        int tokenSum = (sumObj == null) ? 0 : sumObj.intValue();

        int V = Math.max(vocabulary.size(), 1);

        double num = tokenFreq + alpha;
        double den = tokenSum + alpha * V;
        return Math.log(num / den);
    }

    /** Wahrscheinlichkeiten pro Klasse */
    public Map<String, Double> predictProba(String text) {
        List<String> tokens = TextUtils.tokensize(text);

        // log-Scores sammeln
        Map<String, Double> logScore = new HashMap<String, Double>();
        for (String clazz : docCountPerClass.keySet()) {
            double s = logPrior(clazz);
            for (String tok : tokens) {
                s += logLikelihoodToken(clazz, tok);
            }
            logScore.put(clazz, Double.valueOf(s));
        }

        // numerisch stabile Softmax über logScores
        double max = Double.NEGATIVE_INFINITY;
        for (Double v : logScore.values()) {
            if (v.doubleValue() > max) max = v.doubleValue();
        }

        double sumExp = 0.0;
        for (Double v : logScore.values()) {
            sumExp += Math.exp(v.doubleValue() - max);
        }

        Map<String, Double> proba = new HashMap<String, Double>();
        for (Map.Entry<String, Double> e : logScore.entrySet()) {
            double p = Math.exp(e.getValue().doubleValue() - max) / sumExp;
            proba.put(e.getKey(), Double.valueOf(p));
        }
        return proba;
    }

    /** Bestes Label */
    public String predict(String text) {
        Map<String, Double> proba = predictProba(text);
        String best = null;
        double bestP = -1.0;
        for (Map.Entry<String, Double> e : proba.entrySet()) {
            if (e.getValue().doubleValue() > bestP) {
                bestP = e.getValue().doubleValue();
                best = e.getKey();
            }
        }
        return (best == null) ? "unknown" : best;
    }

    public Set<String> getVocabulary() {
        return Collections.unmodifiableSet(vocabulary);
    }
}
