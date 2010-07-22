package kommando;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Markus Kahl
 */
public class Runner implements Runnable {

    private Socket socket;
    private String directory;
    private Server server;

    private static final Logger log = Logger.getLogger("kommando.Runner");

    public Runner(Socket socket, Server server) {

        this.socket = socket;
        this.server = server;
    }

    public void run() {

        try {
            handle(socket);
        } catch (IOException e) {
            log.log(Level.SEVERE, null, e);
        } finally {
            try { socket.close(); } catch (IOException e) { }
        }
    }

    protected void handle(Socket client) throws IOException {

        DataInputStream in = new DataInputStream(client.getInputStream());
        readOptions(in);
        String cmd = in.readUTF();
        LinkedList<String> args = readArguments(in);
        new DataOutputStream(client.getOutputStream()).writeInt(run(cmd, args));
    }

    protected LinkedList<String> readArguments(DataInputStream in) throws IOException {

        int numArgs = in.readInt();
        LinkedList<String> args = new LinkedList<String>();
        for (int i = 0; i < numArgs; ++i) {
            args.add(in.readUTF());
        }
        return args;
    }

    protected void readOptions(DataInputStream in) throws IOException {

        int numOpts = in.readInt();
        for (int i = 0; i < numOpts; ++i) {
            String token = in.readUTF();
            String opt = token.substring(0, token.indexOf("="));
            String value = token.substring(token.indexOf("=") + 1);
            checkOption(opt, value);
        }
    }

    protected void checkOption(String opt, String value) {

        if (opt.equalsIgnoreCase("dir")) {
            this.directory = value;
        }
    }

    protected void applyOptions(ProcessBuilder sb) {

        if (directory != null) {
            sb.directory(new java.io.File(directory));
        }
    }

    protected int run(String cmd, LinkedList<String> args) {

        if (!server.getServerListener().run(cmd, args)) {
            return -2;
        }
        args.addFirst(cmd);
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectErrorStream(true);
        applyOptions(pb);
        int ret = -1;
        try {
            Process process = pb.start();
            Util.emptyStream(process.getInputStream());
            ret = process.waitFor();
        } catch (IOException e) {
            log.log(Level.WARNING, "Command \"" + Util.mkString(args, " ") + "\" could not be executed.", e);
        }  catch (InterruptedException e) {
            log.log(Level.SEVERE, "Wait interrupted. Maybe I should handle this, but I'm too lazy.");
        }
        return ret;
    }

    public void setDirectory(String directory) {

        this.directory = directory;
    }

    public String getDirectory() {

        return directory;
    }
}
