import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class Utils {

    static String[] readLines(String fileName) {
        try (Stream<String> stream = Files.lines(Paths.get(Utils.class.getResource(fileName).toURI()))) {
            return stream.toArray(String[]::new);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    public static void main(String[] args) {
    }

}
