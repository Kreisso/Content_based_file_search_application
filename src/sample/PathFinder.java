package sample;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class PathFinder implements Runnable {

    private BlockingQueue<File> queue;
    private File mainPath;
    private List<String> filesType;

    public PathFinder(BlockingQueue<File> queue, File mainPath, List<String> filesType) {
        this.queue = queue;
        this.mainPath = mainPath;
        this.filesType = filesType;
    }

    public PathFinder(BlockingQueue<File> kolejka, File sciezkaGlowna) {

        this.queue = kolejka;
        this.mainPath = sciezkaGlowna;
    }

    public void findPath(File path) throws InterruptedException {
        File[] listFiles = path.listFiles();
        try {
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].isDirectory()) {
                    findPath(listFiles[i]);
                } else if (isCheckedFile(listFiles[i])) {
//                    System.out.println("finde file :" + listFiles[i]);
                    queue.put(listFiles[i]);
                }
                System.out.println(listFiles[i]);
            }
        } catch (NullPointerException e) {
            System.out.println("List lenght: "+listFiles.length);
        }
    }

    boolean isCheckedFile(File file) {
        Optional<String> typeFile = getExtensionByStringHandling(file.getPath());
        if (!typeFile.isPresent()) return false;
        for (String fileType : filesType) {
            if (typeFile.get().toLowerCase().equals(fileType.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public void run() {
        try {
            findPath(mainPath);
            queue.put(new File("Empty"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

