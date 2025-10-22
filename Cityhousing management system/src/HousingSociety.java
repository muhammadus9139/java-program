public class HousingSociety{
   private String name;
   private Block[]blocks;
       

   HousingSociety(String name,int n){
         this.name=name;
         blocks=new Block[n];
         for(int i=0;i<n;i++){
         String blockname=name+" Block "+(char)('A'+i);
         blocks[i]=new Block(blockname);
         }
   }

   public void showallblocks(){
        System.out.println("Housing society "+name);
        for(int i=0;i<blocks.length;i++){
              blocks[i].showallplots();
              blocks[i].showallparks();
              blocks[i].showmarket();
              System.out.println();
        }
   }

   public Plot findplotbyId(String id){
       for(int i=0;i<blocks.length;i++){
         Plot p=blocks[i].findplotbyId(id);
         if(p!=null){
            return p;
          }
       }
         
           return null;
   }
}