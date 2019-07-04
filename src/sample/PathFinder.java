package sample;

import java.io.File;
import java.util.concurrent.BlockingQueue;

public class PathFinder implements Runnable{

        private BlockingQueue<File> queue;
        private File mainPath;

        public PathFinder(BlockingQueue<File> kolejka, File sciezkaGlowna){

            this.queue = kolejka;
            this.mainPath = sciezkaGlowna;


        }
        public void findPath(File path) throws InterruptedException {
            File[] listFiles = path.listFiles();

            for(int i = 0; i< listFiles.length; i++)
            {
                if(listFiles[i].isDirectory())
                {
                    findPath(listFiles[i]);
                }
                else
                {
                    queue.put(listFiles[i]);

                }
            }
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

