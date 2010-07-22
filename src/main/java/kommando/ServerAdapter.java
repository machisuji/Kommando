package kommando;

import java.util.List;

/**
 *
 * @author Markus Kahl
 */
public class ServerAdapter implements ServerListener {

    public void serverStarted() { }
    public void serverStopped() { }
    public boolean run(String cmd, List<String> args) { return true; }
}
