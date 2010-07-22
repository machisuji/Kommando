package kommando;

import java.util.List;

/**This shows how Kommando is used.
 * Usually Server and Client will run in two separate Java processes,
 * hence the socket communication.
 *
 * @author Markus Kahl
 */
public class Example {

    public static void main(String[] args) {

        Server server = new Server();
        server.setServerListener(new ServerAdapter() {

            public void serverStarted() {

                System.out.println("Server started.");
            }

            public void serverStopped() {

                System.out.println("Server stopped.");
            }

            public boolean run(String cmd, List<String> args) {

                System.out.println("Running " + cmd + " " + Util.mkString(args, " "));
                return true;
            }
        });
        server.start();
        sleep(500);
        int ret = new Command("wget", "-O", "~/Desktop/logo.png",
                "http://any-good.com/images/logo.png").runIfPossible();
        System.out.println("Command returned: " + ret);
        server.stop();
    }

    public static void sleep(long ms) {

        try { Thread.sleep(ms); } catch (InterruptedException e) { }
    }
}
