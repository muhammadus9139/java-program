public class CommercialMarket{
      private String id;
      private Shop[] shops;


      CommercialMarket(String id,int n){           
          this.id=id;
          this.shops=new Shop[n];
          for(int i=0;i<n;i++){
            shops[i]=new Shop(id+"- Shop:"+(i+1),Shapetype.TRAPEZOID,Plottype.COMM_SHOP);
          }
      }

   public void showallshops(){
     System.out.println("Market:"+id);
      for(int i=0;i<shops.length;i++){
         System.out.println(shops[i]);
     }
  }
}