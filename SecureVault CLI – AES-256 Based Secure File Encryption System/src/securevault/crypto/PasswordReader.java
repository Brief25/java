package securevault.crypto;

import java.io.Console;
import java.util.Scanner;

public class PasswordReader {

    public static char[] readPassword(String prompt) {
        Console console = System.console();

        if (console != null) {
            return console.readPassword(prompt);
        } else {
            // Fallback (IDE support)
            System.out.print(prompt);
            Scanner sc = new Scanner(System.in);
            return sc.nextLine().toCharArray();
        }
    }
}
