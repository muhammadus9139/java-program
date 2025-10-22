public class Plot{
      private String id;
      private Plottype type;
      private Shapetype shape;
      private double area;
      private double price;
      private boolean isavailable;
      private double width;
      private double depth;
      private double front;
      private double back;
 
      public Plot(String id,Plottype type,Shapetype shape,double width,double depth,double front,double back){
              this.id=id;
              this.type=type;
              this.shape=shape;
              this.width=width;
              this.depth=depth;
              this.front=front;
              this.back=back; 
              this.isavailable=true;
              this.price=type.getprice();
              this.area=calarea();
             
      }


      public void setprice(double price){
          this.price=price;
      }

       public double calarea(){
          switch(shape){
            case RECTANGLE:
                return area=width*depth;
               

            case TRAPEZOID:
                return area = ((front + back) / 2) * depth;
                 

           case L_SHAPE:
                return area = (width * depth) + (front * back);
                 
           default:
               return 0;
          }
       }

       //Getters
       public boolean isavailable(){
        return isavailable;
      }
 
       public double getarea(){
         return area;
      }

       public double getprice(){
           return price;
       }

       public String getid(){
          return id;
       }

       public Plottype gettype(){
          return type;
       }
   
      //Methods
      public boolean book(){
          if(isavailable){
               isavailable=false;
               return true;
          }
          else
               return false;
       }

     public boolean cancel(){
          if(!isavailable){
               isavailable=true;
               return true;
          }
          else
               return false;
       }

     public double getwidth(){
         return width;
     }

     public double getdepth(){
         return depth;
     }

     
     public double getfront(){
         return front;
     }

      public double getback(){
         return back;
     }

      //toString
      @Override
      public String toString(){
            String status=isavailable?"Available":"Booked";
            return String.format("Id: %s |Type: %s |Shape: %s |Area: %.2f |Price: %.2f |Status: %s",id,type,shape,this.area,this.price,status);
      }
}