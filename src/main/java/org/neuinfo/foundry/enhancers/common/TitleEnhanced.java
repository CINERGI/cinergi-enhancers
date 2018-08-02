package org.neuinfo.foundry.enhancers.common;

 public class TitleEnhanced {
    private String title ;
     private String org;
     private String originalTitle;

     @Override
     public String toString() {
         final StringBuilder sb = new StringBuilder("Title{");
         sb.append("title='").append(getTitle()).append('\'');
         sb.append('}');
         return sb.toString();
     }

     public String getTitle() {
         return title;
     }

     public void setTitle(String title) {
         this.title = title;
     }

     public String getOrg() {
         return org;
     }

     public void setOrg(String org) {
         this.org = org;
     }

     public String getOriginalTitle() {
         return originalTitle;
     }

     public void setOriginalTitle(String originalTitle) {
         this.originalTitle = originalTitle;
     }
 }
