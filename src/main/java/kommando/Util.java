package kommando;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

/**
 *
 * @author Markus Kahl
 */
public class Util {

    public static String readFrom(InputStream in) throws IOException {

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void emptyStream(InputStream in) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        while (br.readLine() != null);
    }

    public static String mkString(Collection<String> strings, String delimiter) {

        boolean first = true;
        int length = length(strings);
        StringBuilder sb = new StringBuilder(length + delimiter.length() * (strings.size() - 1));
        for (String string : strings) {
            if (!first) {
                sb.append(delimiter);
            } else {
                first = false;
            }
            sb.append(string);
        }
        return sb.toString();
    }

    public static int length(Collection<String> strings) {

        int length = 0;
        for (String string : strings) {
            length += string.length();
        }
        return length;
    }
}
