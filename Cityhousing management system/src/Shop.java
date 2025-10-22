public class Shop{
   private String id;
   private double price;
   private Shapetype shape;
   private Plottype type;
   private boolean available;
   
   Shop(String id,Shapetype shape,Plottype type){
       this.id=id;
       this.price=price;
       this.available=true;
       this.shape=shape;
       this.type=type;
       this.price=type.getprice();
   }

   public boolean book(){
       if(available){
           available=false;
           return true;
       }
       else
           return false;
   }

    public boolean cancel(){
       if(!available){
           available=true;
           return true;
       }
       else
           return false;
   }

   public boolean getavailable(){
       return available;
   }

   public String toString(){
       String status=available?"available":"booked";
       return String.format("Shop %s | Status: %s | Price: %.2f |Shape: %s |Shoptype: %s",id,status,this.price,shape,type);
      //return id + " | " + " | " +status+"Price: "+this.price;
       
   }
}