package main.controllers;

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

    private void findPath(File path) throws InterruptedException {
        File[] listFiles = path.listFiles();
        if (listFiles == null) {
            return;
        }
        for (File listFile : listFiles) {
            if (listFile.isDirectory()) {
                findPath(listFile);
            } else if (isCheckedFile(listFile)) {
                queue.put(listFile);
            }
        }

    }

    private boolean isCheckedFile(File file) {
        Optional<String> typeFile = getExtensionByStringHandling(file.getPath());
        if (!typeFile.isPresent()) {
            return false;
        }
        for (String fileType : filesType) {
            if (typeFile.get().toLowerCase().equals(fileType.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private Optional<String> getExtensionByStringHandling(String filename) {
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

