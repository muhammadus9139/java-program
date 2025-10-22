public class CityHousing{
  private String cityname;
  private HousingSociety[] societies;

  CityHousing(String cityname,int n){
     this.cityname=cityname;
     societies=new HousingSociety[n];
     for(int i=0;i<n;i++){
     societies[i]=new HousingSociety("LDA AVENUE "+(i+1),3);
     }
  }

   public void showcityhousing(){
     for(int i=0;i<societies.length;i++){
       societies[i].showallblocks();
     }
   }

   public Plot findplotbyId(String id){
       for(int i=0;i<societies.length;i++){
        Plot p=societies[i].findplotbyId(id);
        if(p!=null){
           return p;
        }
     } 
            return null;
   }
}