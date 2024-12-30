import java.io.File;
import java.util.Comparator;

public class FilenameComparator implements Comparator<File> {
    @Override
    public int compare(File o1, File o2) {
        return extractNumber(o1) - extractNumber(o2);
    }

    private static int extractNumber(File file) {

        return file.getName().charAt(0);
    }

}
