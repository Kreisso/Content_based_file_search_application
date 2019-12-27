package main.boundary;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import main.controllers.FileScanner;
import main.controllers.PathFinder;
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

    public static void getFiles(DoctorFinderProvider doctorFinderProvider) {
        String sample = doctorFinderProvider.getKey();
        List<String> typeFiles = doctorFinderProvider.getCheckedBoxes();
        multimap = ArrayListMultimap.create();
        String path = System.getProperty("user.home");
        if (!doctorFinderProvider.pathTextField.getText().isEmpty()) {
            path = doctorFinderProvider.pathTextField.getText();
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
            DirectoryTree.createNewTree(path, doctorFinderProvider.getCheckedBoxes(), multimap);
            doctorFinderProvider.filesTree.setRoot(DirectoryTree.getTreeItem());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}