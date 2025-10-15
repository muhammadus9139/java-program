
public class Plot{

      private String id;
      private Type plottype;
      private Shape shapetype;
      private double area;
      private double price;
      private boolean available;
      private double width;   
      private double depth;
     

      public enum Type{
                    RES_5_MARLA,RES_10_MARLA,RES_1_KANAL,COMM_SHOP,COMM_OFFICE,PARKING;
        
     }


     public enum Shape{
           RECTANGLE,TRAPEZOID,LSHAPE;
     }



     Plot(String id,Type plottype,Shape shapetype,double area,boolean available,double width,double depth){
             this.id=id;
             this.plottype=plottype;
             this.shapetype=shapetype;
             this.area=area;
             this.price=price();
             this.available=true;
     }



    public double price(){
          switch(plottype){
          case RES_5_MARLA: 
               return 4000000;
          case RES_10_MARLA:
               return 7500000;
          case RES_1_KANAL:
               return 14000000;
          case COMM_SHOP:
                return 3000000;
         case  COMM_OFFICE:
                return 5000000;
         case PARKING:
                return 200000;
         default:
                return 0;
          }
          
    }


   /*public double area(){
          switch(shapetype){
          case RECTANGLE: 
               return width*depth;
          case TRAPEZOID:
               return ;
          case LSHAPE:
               return ;
         default:
                return 0;
          }
          
    }*/
    



     public String toString(){
          return "id="+id+"  Plottype="+plottype+"  Shapetype="+shapetype+"  Area="+area+"  Price="+this.price+"  Availability="+available;
     }


    
}