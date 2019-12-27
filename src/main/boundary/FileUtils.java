package main.boundary;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import main.controllers.MainController;
import main.entity.DirectoryTree;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

public class FileUtils {
    private static final int THREADS = 5;
    private static Multimap<String, String> multimap = ArrayListMultimap.create();

    public static Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public static void getFiles(MainController mainController) {
        String sample = mainController.getKey();
        List<String> typeFiles = mainController.getCheckedBoxes();
        multimap = ArrayListMultimap.create();
        String path = System.getProperty("user.home");
        if (!mainController.pathTextField.getText().isEmpty()) {
            path = mainController.pathTextField.getText();
        }
        File file = new File(path);

        BlockingQueue<File> fileQueue = new ArrayBlockingQueue<>(50);

        new Thread(new PathFinder(fileQueue, file, typeFiles)).start();

        ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
        for (int i = 0; i < THREADS; i++)
            executorService.execute(new FileScanner(fileQueue, sample, multimap, typeFiles));//;  new Thread().start();

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            DirectoryTree.createNewTree(path, mainController.getCheckedBoxes(), multimap);
            mainController.filesTree.setRoot(DirectoryTree.getTreeItem());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}