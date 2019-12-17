package sample;

import com.google.common.collect.Multimap;
import sample.entity.FindeFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class FileScanner implements Runnable {

    private File emptyFile = new File("Empty");
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

    private void FindWord(File file) throws FileNotFoundException {

        Scanner reader = new Scanner(new BufferedReader(new FileReader(file)));
        FindeFile findeFile = new FindeFile();

        int lineNumber = 0;

        while (reader.hasNext()) {
            lineNumber++;

            if (reader.nextLine().contains(sample))
                //TODO usunąć
                System.out.println("Szukane słowo znajduje się w pliku: " + file.getPath() + " w lini " + lineNumber);
                multimap.put(file.getPath(), String.valueOf(lineNumber));
                //TODO dodać obsługę poprzez entity FindeFile
        }

        reader.close();
    }

    public void run() {
        boolean interrupt = false;

        while (!interrupt) {

            try {
                if (fileType.isEmpty()) {
                    return;
                }
                File currentFile = queue.take();
                if (currentFile.equals(emptyFile)) {
//                    return;
                    interrupt = true;
                    queue.put(currentFile);
                } else {
                    FindWord(currentFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

