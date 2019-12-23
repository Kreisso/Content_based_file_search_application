package main.entity;

import com.google.common.collect.Multimap;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class DirectoryTree {
    static TreeItem<String> root;
    static List<String> filesType;

    public static void createNewTree(String rootName, List<String> types, Multimap<String, String> multimap) {
        //Node rootIcon =  new ImageView(new Image(DirectoryTree.class.getResourceAsStream("res/img/iconfinder_icon-folder_211608.png")));
        //root  = new TreeItem<String>(rootName, rootIcon);
        root = new TreeItem<String>(rootName);
        root.setExpanded(true);
        filesType = types;

        File rootFile = new File(rootName);
        File[] fileList = rootFile.listFiles();

        // create tree
        fileList = getFiles(fileList, multimap);
        for (File file : fileList) {
            DirectoryTree.createTree(file, DirectoryTree.getTreeItem(), multimap);
        }

    }

    public static void addChildren(String childrenName) {
        root.getChildren().add(new TreeItem<String>(childrenName));
    }

    public static TreeItem<String> getTreeItem() {
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
        Optional<String> typeFile = getExtensionByStringHandling(file.getPath());
        if (!typeFile.isPresent()) return false;
        return multimap.containsKey(file.getPath());
//        for (String fileType : filesType) {
//            if (typeFile.get().toLowerCase().equals(fileType.toLowerCase())) {
//                return true;
//            }
//        }
    }

    private static Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
}
