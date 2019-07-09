package sample;

import com.google.common.collect.Multimap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

import static com.google.common.base.Preconditions.checkNotNull;

public class FileScanner implements Runnable {

    private BlockingQueue<File> queue;
    private String sample;
    private Multimap<String, String> multimap;
    private List<String> fileType;

    public FileScanner(BlockingQueue<File> queue, String sample, Multimap<String, String> multimap, List<String> fileType) {
        this.queue = queue;
        this.sample = sample;
        this.multimap = multimap;
        this.fileType = fileType;
    }

    public FileScanner(BlockingQueue<File> queue, String sample, Multimap<String, String> multimap) {

        this.queue = queue;
        this.sample = sample;
        this.multimap = multimap;

    }

    public Multimap<String, String> getMultimap() {
        return multimap;
    }

    public void FindWord(File file) throws FileNotFoundException {

        Scanner reader = new Scanner(new BufferedReader(new FileReader(file)));

        int lineNumber = 0;

        while (reader.hasNext()) {
            lineNumber++;

            if (reader.nextLine().contains(sample))
                System.out.println("Szukane słowo znajduje się w pliku: "+file.getPath()+" w lini "+lineNumber);
                multimap.put(file.getPath(), String.valueOf(lineNumber));
        }

        reader.close();
    }

    public void run() {
        boolean interrupt = false;

        while (!interrupt) {

            try {
                if(fileType.isEmpty()) return;
                File currentFile;
                currentFile = queue.take();
                Optional<String> typeFile = getExtensionByStringHandling(currentFile.getPath());
                System.out.println("typeFile :"+typeFile);
//                if(currentFile.getFile)
                if (currentFile.equals(new File("Empty"))) {
                    interrupt = true;
                    queue.put(currentFile);
                } else {
//                    FindWord(currentFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getFileExtension(String fullName) {
        checkNotNull(fullName);
        String fileName = new File(fullName).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    public Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

}

