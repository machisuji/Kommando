package kommando;

import java.util.List;

/**
 *
 * @author Markus Kahl
 */
public interface ServerListener {

    void serverStarted();
    void serverStopped();
    
    /**
     * 
     * @param cmd
     * @param args
     * @return true if the command shall be executed
     */
    boolean run(String cmd, List<String> args);
}
