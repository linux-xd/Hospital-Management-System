package Secrets;

public class Secrets {
     private static final String url = "jdbc:mariadb://localhost:3306/hospital";
     private static final String username = "nix";
     private static final String password = "userpass";

     public static String getUrl() {
          return url;
     }

     public static String getUsername() {
          return username;
     }

     public static String getPassword() {
          return password;
     }
}
