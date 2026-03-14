package app;

public class LGA3B {
   private String rank;
   private String name;
   private int count;
   private String code;
   private int counto;
   private String similarity;
   private double perc;

   public LGA3B(String rank, String name, String code, int count, int counto) {
    this.rank = rank;
    this.count = count;
    this.name = name;
    this.code = code;
    this.counto = counto;
 }

   public String getRank() {
      return rank;
   }

   public String getLGANAME() {
      return name;
   }


   public String getLGA() {
      return code;
   }


   public int getCount() {
      return count;
   }


   public String getSimilarity() {

      if (count > counto){
         perc = (double)counto/count;
      }
      else{perc = (double)count/counto;}

      perc = perc * 100;
      String formattedValue = String.format("%.2f", perc);
      similarity = formattedValue + "%";

      return similarity;
   }
}
