package securevault;

import securevault.cli.CommandParser;

public class SecureVaultCLI {

    public static final String VERSION = "1.0.0 (2026 Edition)";

    public static void main(String[] args) {
        if (args.length == 0) {
            CommandParser.printHelp();
            return;
        }

        CommandParser.parse(args);
    }
}
