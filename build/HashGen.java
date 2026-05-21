import org.mindrot.jbcrypt.BCrypt;
public class HashGen {
  public static void main(String[] args) {
    for (String pass : args) {
      System.out.println(pass + "=" + BCrypt.hashpw(pass, BCrypt.gensalt()));
    }
  }
}
