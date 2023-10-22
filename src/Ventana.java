import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//50
//100
//500
//1000
//5000
//10000
//50000
public class Ventana extends JFrame {
    private JLabel originalArrayLabel;
    ForkJoinPool forkJoinPool = new ForkJoinPool();
    public static ArrayList<String> files = new ArrayList<String>();

    private JLabel mergeSortTimeLabel;
    private final ExecutorService executor = Executors.newWorkStealingPool(10);
    private JLabel forkJoinTimeLabel;
    private JLabel executorServiceLabel;
    private JTextArea originalArrayTextArea;
    private JTextArea sorterdArrayTextArea;
    private JLabel sortedArrayLabel;

    private JButton mergeSortButton;
    private JButton forkJoinButton;
    private JButton executorButton;
    private JButton createOriginalArrayButton;
    private JButton deleteOriginalArrayButton;
    public static TimerThread timerThread = new TimerThread();

    private JPanel leftPanel;
    private JPanel rightPanel;
    public static int[] numbers;
    public static int[] originalNumbers;

    public Ventana(){
        setTitle("Merge sort");
        setSize(1200,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        createComponents();
        createListeners();
        setVisible(true);
    }
    public void createListeners(){
        mergeSortButton.addActionListener(e -> {
            if (!Ventana.timerThread.contando){
                Ventana.timerThread.startCountingTime();
            }
            try {
                File folder = new File("C:\\Users\\Adrian Llanos\\Desktop\\Escuela\\Archivos-proyecto-paralela\\10,000");
                File[] listOfFiles = folder.listFiles();
                final FileOutputStream fos = new FileOutputStream("secuencial-compressed.zip");
                ZipOutputStream zipOut = new ZipOutputStream(fos);
                compressSecuencial(listOfFiles,fos,zipOut);
                zipOut.close();
                fos.close();
                timerThread.endCount();
                mergeSortTimeLabel.setText(timerThread.getTime() + " mS");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        forkJoinButton.addActionListener(e -> {
            if (!Ventana.timerThread.contando){
                Ventana.timerThread.startCountingTime();
            }
            File folder = new File("C:\\Users\\Adrian Llanos\\Desktop\\Escuela\\Archivos-proyecto-paralela\\10,000");
            File[] listOfFiles = folder.listFiles();
            File outputZipFile = new File("fork-join-compressed.zip");
            FileOutputStream fos;
            ZipOutputStream zipOutputStream;
            try {
                fos = new FileOutputStream(outputZipFile, true);
                zipOutputStream = new ZipOutputStream(fos);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            ArrayList<File> list = new ArrayList<>();
            Collections.addAll(list,listOfFiles);
            ForkJoin compressTask = new ForkJoin(list, outputZipFile, fos, zipOutputStream);
            forkJoinPool.invoke(compressTask);
            try {
                zipOutputStream.close();
                fos.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            files.clear();

            timerThread.endCount();
            forkJoinTimeLabel.setText(timerThread.getTime() + " mS");
        });
        executorButton.addActionListener(e -> {
            if (!Ventana.timerThread.contando){
                Ventana.timerThread.startCountingTime();
            }
            File folder = new File("C:\\Users\\Adrian Llanos\\Desktop\\Escuela\\Archivos-proyecto-paralela\\10,000");
            File[] listOfFiles = folder.listFiles();
            ExecutorServiceMerge.ExecutorMerge(listOfFiles);
            timerThread.endCount();
            executorServiceLabel.setText(timerThread.getTime() + " mS");
        });

        createOriginalArrayButton.addActionListener(e -> {
            String s = JOptionPane.showInputDialog("Ingresa el tamaño del arreglo");
            int lenght = Integer.parseInt(s);
            numbers = new int[lenght];
            for (int i = 0; i < lenght; i++) {
                numbers[i]= (int)(Math.random()*(100+1));
            }
            originalNumbers = numbers.clone();
            printFirstRectangle(originalNumbers);
        });
        deleteOriginalArrayButton.addActionListener(e -> {
            numbers = new int[]{};
            originalNumbers = numbers.clone();
            printFirstRectangle(originalNumbers);
            printSecondRectangle(originalNumbers);
        });
    }
    private void printFirstRectangle(int[] array){
        String arrStr = "";
        for (int i = 0; i < array.length; i++) {
            arrStr+=array[i]+", ";
        }
        originalArrayTextArea.setText(arrStr);
    }
    private void printSecondRectangle(int[] array){
        String arrStr = "";
        for (int i = 0; i < array.length; i++) {
            arrStr+=array[i]+", ";
        }
        sorterdArrayTextArea.setText(arrStr);
    }
    public void createComponents(){
        originalArrayLabel = new JLabel("Arreglo original");
        originalArrayLabel.setFont(new Font("Verdana", Font.BOLD, 20));
        originalArrayLabel.setSize(400,150);
        originalArrayLabel.setHorizontalAlignment(JLabel.CENTER);
        originalArrayLabel.setVerticalAlignment(JLabel.CENTER);
        sortedArrayLabel = new JLabel("Arreglo ordenado");
        sortedArrayLabel.setFont(new Font("Verdana", Font.BOLD, 20));

        mergeSortTimeLabel = new JLabel("0.0 mS");
        mergeSortTimeLabel.setHorizontalAlignment(JLabel.CENTER);

        forkJoinTimeLabel = new JLabel("0.0 mS");
        forkJoinTimeLabel.setHorizontalAlignment(JLabel.CENTER);

        executorServiceLabel = new JLabel("0.0 mS");
        executorServiceLabel.setHorizontalAlignment(JLabel.CENTER);

        sortedArrayLabel.setSize(400,150);
        sortedArrayLabel.setHorizontalAlignment(JLabel.CENTER);
        sortedArrayLabel.setVerticalAlignment(JLabel.CENTER);
        createOriginalArrayButton = new JButton("Crear arreglo");
        deleteOriginalArrayButton = new JButton("Eliminar arreglo");
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(getWidth()-200,getHeight()));
        originalArrayTextArea = new JTextArea();
        originalArrayTextArea.setPreferredSize(new Dimension(leftPanel.getWidth(), 200));
        originalArrayTextArea.setMaximumSize(new Dimension(leftPanel.getWidth(), 450));
        originalArrayTextArea.setLineWrap(true);
        sorterdArrayTextArea = new JTextArea();
        sorterdArrayTextArea.setPreferredSize(new Dimension(leftPanel.getWidth(), 200));
        sorterdArrayTextArea.setMaximumSize(new Dimension(leftPanel.getWidth(), 450));

        sorterdArrayTextArea.setLineWrap(true);

        mergeSortButton = new JButton("Merge Sort");
        mergeSortButton.setFont(new Font("Skia", Font.PLAIN, 20));
        forkJoinButton = new JButton("Fork Join");
        forkJoinButton.setFont(new Font("Skia", Font.PLAIN, 20));
        executorButton = new JButton("Executor Join");
        executorButton.setFont(new Font("Skia", Font.PLAIN, 20));


        JPanel arrayUnsortedPanel = new JPanel();
        arrayUnsortedPanel.setLayout(new BorderLayout());
        arrayUnsortedPanel.add(originalArrayLabel, BorderLayout.NORTH);
        arrayUnsortedPanel.add(originalArrayTextArea, BorderLayout.CENTER);

        JPanel arraySortedPanel = new JPanel();
        arraySortedPanel.setLayout(new BorderLayout());
        arraySortedPanel.add(sortedArrayLabel, BorderLayout.NORTH);
        arraySortedPanel.add(sorterdArrayTextArea, BorderLayout.CENTER);

        JPanel buttonsPannel = new JPanel();
        buttonsPannel.setLayout(new GridLayout(2,3));
        buttonsPannel.add(mergeSortTimeLabel);
        buttonsPannel.add(forkJoinTimeLabel);
        buttonsPannel.add(executorServiceLabel);

        buttonsPannel.add(mergeSortButton);
        buttonsPannel.add(forkJoinButton);
        buttonsPannel.add(executorButton);

        leftPanel.add(arrayUnsortedPanel, BorderLayout.NORTH);
        leftPanel.add(arraySortedPanel, BorderLayout.CENTER);
        leftPanel.add(buttonsPannel, BorderLayout.SOUTH);

        rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(createOriginalArrayButton, BorderLayout.NORTH);
        rightPanel.add(deleteOriginalArrayButton, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }
    public static void compressSecuencial(File[] listOfFiles, FileOutputStream fos, ZipOutputStream zipOut) throws IOException {
        for (File srcFile : listOfFiles) {
            FileInputStream fis = new FileInputStream(srcFile);
            ZipEntry zipEntry = new ZipEntry(srcFile.getName());
            File[] filesToCompare = listOfFiles.clone();
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            for (int i = 0; i < bytes.length/200; i++) {
                FileOutputStream fos2 = new FileOutputStream(srcFile);
                ZipOutputStream zipOutputStream = new ZipOutputStream(fos2);
                zipOutputStream.flush();
            }
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);filesToCompare.clone().toString();

            }
            fis.close();
        }
    }

    public static void addToZip(File[] files, ZipOutputStream zipOut) throws IOException {
        synchronized (zipOut) { // Exclusión mutua para evitar conflictos de escritura
            for (File file: files) {
                FileInputStream fis = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] buffer = new byte[1024];
                for (int i = 0; i < buffer.length/500; i++) {
                    FileOutputStream fos2 = new FileOutputStream(file);
                    ZipOutputStream zipOutputStream = new ZipOutputStream(fos2);
                    zipOutputStream.flush();
                }
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zipOut.write(buffer, 0, length);
                }
                fis.close();
            }

        }
    }
    public static void addToZipSingle(File file, ZipOutputStream zipOut) throws IOException {
        synchronized (zipOut) { // Exclusión mutua para evitar conflictos de escritura
            FileInputStream fis = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zipOut.write(buffer, 0, length);
            }
            fis.close();

        }
    }


}
