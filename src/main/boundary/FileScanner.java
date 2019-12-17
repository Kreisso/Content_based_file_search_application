package main.boundary;

import com.google.common.collect.Multimap;
import main.entity.FindeFile;
import main.entity.LCS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class FileScanner implements Runnable {

    private File emptyFile = new File("Empty");
    private BlockingQueue<File> queue;
    private String sample;
    private Multimap<String, String> filePathToLineNumber;
    private List<String> fileType;

    public FileScanner(BlockingQueue<File> queue, String sample, Multimap<String, String> multimap, List<String> fileType) {
        this.queue = queue;
        this.sample = sample;
        this.filePathToLineNumber = multimap;
        this.fileType = fileType;
    }

    private void FindWord(File file) throws FileNotFoundException {

        Scanner reader = new Scanner(new BufferedReader(new FileReader(file)));
        FindeFile findeFile = new FindeFile();

        int lineNumber = 0;

        while (reader.hasNext()) {
            lineNumber++;
            String line = reader.nextLine();
            List<String> temporaryList = List.of(line.split(" "));
            Set<String> splitString = new HashSet<>(temporaryList);
            splitString = splitString.stream().filter(s -> LCS.computeMatching(s, sample)).collect(Collectors.toSet());
            System.out.println("rozmiar seta: " + splitString.size());
            if (splitString.size() > 0)
                System.out.println("Szukane słowo znajduje się w pliku: " + file.getPath() + " w lini " + lineNumber);
            filePathToLineNumber.put(file.getPath(), String.valueOf(lineNumber));
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

