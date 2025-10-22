public class CornerPlot extends Plot{
      private double extrawidth;
      private final double premiumrate=0.08;
      private double extraprice;
      private double baseprice;

  CornerPlot(String id,Plottype type,Shapetype shape,double width,double depth,double front,double back,double extrawidth){
              super(id,type,shape,width,depth,front,back);
              this.extrawidth=extrawidth;
              applyCornerPrice();
              this.baseprice=type.getprice();
      }

   public void applyCornerPrice() {
       
       extraprice=getprice()*premiumrate*extrawidth;;
       double newprice=extraprice+getprice();
       setprice(newprice);


        //double newPrice = getprice() + (getprice() * premiumrate);
        //setprice(newPrice); // Update parent class price
    }

  /*public String toString(){
       return String.format("%s %.2f %.2f Final price=%.2f Rs",super.toString(),premiumrate * 100,extraprice,getprice());
  }*/

   @Override
     public String toString() {
       return String.format("%s |Premium rate: %.2f |Base price: %.2f ",super.toString(),premiumrate*100,this.baseprice);
}

}