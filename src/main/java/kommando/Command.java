package kommando;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author markus
 */
public class Command {

    private String name;
    private String[] args;
    private Collection<String> options = new LinkedList<String>();
    private boolean debug = false;

    private static final Logger log = Logger.getLogger("kommando.Command");

    public Command(String name, String... args) {

        this.name = name;
        this.args = args;
    }

    /**Runs this command.
     *
     * @param host Host this command shall run on.
     * @return Return code of the command.
     * @throws IOException
     */
    public int run(String host) throws IOException {

        Socket socket = null;
        try {
            socket = new Socket(host, 59128);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            writeOptions(out);
            out.writeUTF(name);
            out.writeInt(args.length);
            for (String arg: args) {
                out.writeUTF(arg);
            }
            return new DataInputStream(socket.getInputStream()).readInt();
        } catch (UnknownHostException e) {
            log.log(Level.SEVERE, "Unknown host: " + e.getMessage());
            throw new RuntimeException("Unknown host: " + e.getMessage());
        } finally {
            if (socket != null) {
                try { socket.close(); } catch (IOException e) { }
            }
        }
    }

    /**Same as run, but throws no exception.
     *
     * @param host Host this command shall run on.
     * @return Return code of the command or -1 if it failed due to an exception.
     */
    public int runIfPossible(String host) {

        try {
            return run(host);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Command failed: ", e);
        }
        return -1;
    }

    /**Run on localhost.
     *
     * @return
     * @throws IOException
     */
    public int run() throws IOException {

        return run("localhost");
    }

    /**Run if possible on localhost.
     *
     * @return
     */
    public int runIfPossible() {

        return runIfPossible("localhost");
    }

    protected void writeOptions(DataOutputStream out) throws IOException {

        out.writeInt(options.size());
        for (String opt: options) {
            out.writeUTF(opt);
        }
    }

    protected void setOption(String name, String value) {

        String opt = name + "=" + value;
        Iterator<String> opts = options.iterator();
        while (opts.hasNext()) {
            if (opts.next().toLowerCase().startsWith(name.toLowerCase() + "=")) {
                opts.remove(); break;
            }
        }
        options.add(opt);
    }

    /**Directory within which the command will be executed.
     *
     * @param directory the directory to set
     */
    public void setDirectory(String directory) {

        setOption("dir", directory);
    }

    /**
     * @return the debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
