public class Block{

  private String blockname;
  private Plot[][]plots;
  private Park[] parks;
  private CommercialMarket market;
  //private int streetcount;

 private static final int[] Streetlengths={10,11,12,13,14};


  Block(String blockname){
       this.blockname=blockname;
       buildplots();
  }

   public void buildplots(){
           int[]streetarr={10,11,12,13,14};
       plots=new Plot[streetarr.length][];
       for(int i=0;i<streetarr.length;i++){
          plots[i]=new Plot[streetarr[i]];

           for(int j=0;j<streetarr[i];j++){
             String id=(i+1)+"-"+String.format("%03d",(j+1));
             
             Plottype type;
             Shapetype shape;

             switch(i) {
                    case 0:
                    type = Plottype.RES_5_MARLA; shape = Shapetype.RECTANGLE; 
                    break;
                    case 1:
                    type = Plottype.RES_10_MARLA; shape = Shapetype.RECTANGLE;  
                     break;
                    case 2: 
                    type = Plottype.RES_1_KANAL; shape = Shapetype.TRAPEZOID; 
                     break;
                    case 3:
                    type = Plottype.COMM_SHOP; shape = Shapetype.RECTANGLE; 
                    break;
                    default:
                    type = Plottype.COMM_OFFICE; shape = Shapetype.RECTANGLE; 
                }
             if((j+1)%5==0)
                 type=Plottype.PARKING;

            if(i<=2&&(j+1)%4==0)
                 plots[i][j]=new CornerPlot(id,type,shape,5,4,2,6,1);

            else
                 plots[i][j]=new Plot(id,type,shape,5,4,2,6);
            }
        }

        this.parks=new Park[2];
        for(int i=0;i<parks.length;i++){
           parks[i]=new Park("Park-"+(i+1),Shapetype.RECTANGLE,3,4,2,6);
        }

        this.market=new CommercialMarket("Market-"+blockname,15);
   }
  
   public void showallplots(){
     System.out.println();
     System.out.println("All plots in block "+blockname);
      System.out.println();
      for(int i=0;i<plots.length;i++){
        for(int j=0;j<plots[i].length;j++) {
             System.out.println(plots[i][j]);
         }
      }
   }
 
  public void showallparks(){
      System.out.println();
      System.out.println("All parks in block "+blockname);
      System.out.println();
      for(int i=0;i<parks.length;i++){
           System.out.println();
         System.out.println(parks[i]);
      }
  }

    public void showmarket(){
      System.out.println();
      System.out.println("All shops in block "+blockname);
       System.out.println();
       System.out.println();
       market.showallshops();
    }
  

   public Plot findplotbyId(String id){
      for(int i=0;i<plots.length;i++){
        for(int j=0;j<plots[i].length;j++){
             Plot p1=plots[i][j];
           if(p1.getid().equals(id)){
             return p1;
               }
        }
      }
     return null;
   }

   public boolean bookplot(String id){
    Plot p=findplotbyId(id);
    if(p!=null){
          return p.book();
    }
    else
         return false;
    }

    public boolean cancelplot(String id){
    Plot p=findplotbyId(id);
    if(p!=null){
          return p.cancel();
     }
    else
         return false;
    }

   /* public double calculateBlockValue() {
    double sum = 0;
    for (int i = 0; i < plots.length; i++) {
        for (int j = 0; j < plots[i].length; j++) {
            sum += plots[i][j].getprice();
        }
    }
    return sum;
   }*/


}