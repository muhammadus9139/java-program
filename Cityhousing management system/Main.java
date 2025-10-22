public class Main{
  public static void main(String args[]){
     //Plot p1=new Plot("003",Plottype.RES_5_MARLA,Shapetype.RECTANGLE,5,3,8,5);
     //System.out.println(p1);

     //CornerPlot p2=new CornerPlot("003",Plottype.RES_5_MARLA,Shapetype.RECTANGLE,5,3,8,5,1);
     //System.out.println(p2.toString());

    //Block b1=new Block("LDA AVENUE-1");
    //b1.showallplots();

    //Plot p=b1.findplotbyId("1-001");
    //System.out.println(p);
    //boolean p4=b1.bookplot("1-001");
    //System.out.println(p4);
     

     //CommercialMarket m1=new CommercialMarket("Market 1",15);
     //m1.showallshops();

    //Park pr1=new Park("Park 1",Shapetype.TRAPEZOID,5,4,3,2);
    //System.out.println(pr1);

     //Shop s1=new Shop("1",Shapetype.TRAPEZOID,Plottype.COMM_SHOP);
     //System.out.println(s1);

     //HousingSociety h1=new HousingSociety("Lda avenue 1",3);
     //h1.showallblocks();
     //Plot pl=h1.findplotbyId("1-001");
      //System.out.println(pl);


      //CityHousing c1=new CityHousing("Lahore",2);
     // c1.showcityhousing();
     // Plot p=c1.findplotbyId("1-001");
      //System.out.println(p);
  
    // Display all housing socities with blocks, plots etc
    CityHousing ch=new CityHousing("Lahore",2);
    ch.showcityhousing();
    
     //Find plot by id
    Plot p=ch.findplotbyId("1-001");
    System.out.println(p);

     }

    
}