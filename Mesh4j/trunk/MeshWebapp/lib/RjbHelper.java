import java.util.Date;

public class RjbHelper {

  public static Date newDate(String value) {
    return new Date(Long.parseLong(value));
  }

}
