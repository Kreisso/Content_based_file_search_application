package main.entity;

import com.google.common.collect.Multimap;
import javafx.scene.control.TreeItem;
import main.boundary.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class DirectoryTree {
    static TreeItem<String> root;
    static List<String> filesType;

    public static void createNewTree(String rootName, List<String> types, Multimap<String, String> multimap) {
        root = new TreeItem<String>(rootName);
        root.setExpanded(true);
        filesType = types;

        File rootFile = new File(rootName);
        File[] fileList = rootFile.listFiles();

        fileList = getFiles(fileList, multimap);
        for (File file : fileList) {
            DirectoryTree.createTree(file, DirectoryTree.getTreeRoot(), multimap);
        }

    }

    public static TreeItem<String> getTreeRoot() {
        return root;
    }

    public static void createTree(File file, TreeItem<String> parent, Multimap<String, String> multimap) {
        if (file.isDirectory()) {
            TreeItem<String> treeItem = new TreeItem<>(file.getName());

            parent.getChildren().add(treeItem);
            for (File f : getFiles(file.listFiles(), multimap)) {
                createTree(f, treeItem, multimap);
            }
        } else {
            parent.getChildren().add(new TreeItem<>(file.getName()));
        }
    }

    private static File[] getFiles(File[] files, Multimap<String, String> multimap) {
        return Arrays.stream(files).filter(getFilePredicate(multimap))
                .toArray(File[]::new);
    }

    private static Predicate<File> getFilePredicate(Multimap<String, String> multimap) {
        return f -> f.isDirectory() || (isCheckedFile(f, multimap));
    }


    private static boolean isCheckedFile(File file, Multimap<String, String> multimap) {
        Optional<String> typeFile = FileUtils.getExtensionByStringHandling(file.getPath());
        if (!typeFile.isPresent()) return false;
        return multimap.containsKey(file.getPath());
    }
}
