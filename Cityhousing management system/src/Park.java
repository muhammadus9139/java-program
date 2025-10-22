public class Park{
   private String id;
   private Shapetype shape;
   private double width;
   private double depth;
   private double front;
   private double back;
 

   Park(String id,Shapetype shape,double width,double depth,double front,double back){
      this.id=id;
      this.shape=shape;
      this.width=width;
      this.depth=depth;
      this.back=back;
      this.front=front;
      
   }

   public String toString(){
       return String.format("Park: %s , Shape: %s , Dimensions:W:%.2f,D:%.2f,F:%.2f, B:%.2f",id,shape,width,depth,front,back);
   }
}  