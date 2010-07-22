package kommando;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Markus Kahl
 */
public class Server {

    private boolean debug = false;
    private volatile boolean stop = false;
    private volatile boolean running = false;
    private ServerListener listener = new ServerAdapter();
    private static final Logger log = Logger.getLogger("kommando.Server");

    public static void main(String[] args) {

        System.out.println("Kommando Server started");
        new Server().run();
        System.out.println("Server stopped");
    }

    public void start() {

        if (!running) {
            startInBackground();
        }
    }

    public void stop() {

        if (running) {
            stop = true;
            new Command("echo", "bye").runIfPossible("localhost");
        }
    }

    protected void startInBackground() {

        Runnable run = new Runnable() {

            public void run() {
                Server.this.run();
            }
        };
        Thread thread = new Thread(run, "Kommando Server");
        thread.start();
    }

    protected void run() {

        ServerSocket server = null;
        try {
            running = true;
            server = new ServerSocket(59128);
            stop = false;
            listener.serverStarted();
            while (!stop) {
                Socket client = server.accept();
                if (acceptClient(client)) {
                    Thread thread = new Thread(new Runner(client, this),
                            client.getRemoteSocketAddress().toString());
                    thread.setDaemon(true);
                    thread.start();
                } else {
                    try {
                        client.close();
                    } catch (IOException e) {}
                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, null, e);
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                }
            }
            running = false;
            listener.serverStopped();
        }
    }

    protected boolean acceptClient(Socket client) {

        boolean ret = client.getRemoteSocketAddress().toString().indexOf("127.0.0.1") != -1;
        return ret;
    }

    public void setServerListener(ServerListener listener) {

        this.listener = listener;
    }

    public ServerListener getServerListener() {

        return listener;
    }

    public boolean isRunning() {

        return running;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
