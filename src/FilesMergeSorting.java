import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilesMergeSorting {
    static int BLOCK_SIZE = 10000;

    public static void main(String[] args) {
        String sortMode = "-a";
        String dataType;
        String outputFilePath = "";
        List<String> inputFiles = new ArrayList<>();

        if (args.length < 2) {
            System.err.println("Ошибка: Недостаточное количество аргументов. " +
                    "Необходимо указать тип данных, имя выходного файла и имя файла с исходными данными.");
            return;
        }


        if (args.length >= 3 && (args[0].equals("-a") || args[0].equals("-d"))) {
            sortMode = args[0];
            dataType = args[1];
            outputFilePath = args[2];
            inputFiles.addAll(Arrays.asList(args).subList(3, args.length));


        } else if ((args.length >= 3 && (args[1].equals("-a") || args[1].equals("-d")))) {
            dataType = args[0];
            sortMode = args[1];
            outputFilePath = args[2];
            inputFiles.addAll(Arrays.asList(args).subList(3, args.length));

        } else {
            dataType = args[0];
            outputFilePath = args[1];
            inputFiles.addAll(Arrays.asList(args).subList(2, args.length));

        }

        try {
            File outputFile = new File(outputFilePath);
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            boolean ascending = sortMode.equals("-a");
            mergeFiles(inputFiles, writer, dataType, ascending);

            writer.close();

            System.out.println("Слияние и сортировка завершены. Результат в файле: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Ошибка ввода/вывода: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Произошла неожиданная ошибка: " + e.getMessage());
        }
    }

    private static void mergeFiles(List<String> inputFiles, BufferedWriter writer,
                                   String dataType, boolean ascending) throws IOException {
        List<String> mergedData = new ArrayList<>();
        List<BufferedReader> readers = new ArrayList<>();
        for (String inputFile : inputFiles) {
            readers.add(new BufferedReader(new FileReader(inputFile)));
        }

        boolean dataAvailable = true;
        while (dataAvailable) {
            dataAvailable = false;
            for (BufferedReader reader : readers) {
                List<String> block = readBlock(reader, BLOCK_SIZE, dataType);
                if (!block.isEmpty()) {
                    dataAvailable = true;
                    mergedData.addAll(block);
                }
            }
            if (dataAvailable) {
                mergeSort(mergedData, dataType, ascending);
                writeBlock(writer, mergedData);
                mergedData.clear();
            }
        }
        for (BufferedReader reader : readers) {
            reader.close();
        }
    }

    private static List<String> readBlock(BufferedReader reader, int blockSize,
                                          String dataType) throws IOException {
        List<String> block = new ArrayList<>();
        String line;
        int count = 0;
        while ((line = reader.readLine()) != null && count < blockSize) {
            if (line.isEmpty() || line.equals(" ")) {
                System.err.println("Среди исходных данных была некорректная строка");
                line = reader.readLine();
            }
            if (dataType.equals("-i") && !(line.matches("-?\\d+"))) {
                System.err.println("Несоответствие типа данных из аргументов программы и исходных данных. " + count
                        + " строка пропущена." );
                line = reader.readLine();
            }
            if (dataType.equals("-s") && !(line.matches(" "))) {
                System.err.println("Пробел в строке " + count
                        + " . Строка пропущена как содержащая некорректные данные.");
                line = reader.readLine();
            }
            block.add(line);
            count++;
        }

        return block;
    }

    private static void writeBlock(BufferedWriter writer, List<String> block) throws IOException {
        for (String line : block) {
            writer.write(line);
            writer.newLine();
        }
    }

    private static void mergeSort(List<String> array, String dataType, boolean ascending) {
        if (array.size() <= 1) {
            return;
        }

        int mid = array.size() / 2;
        List<String> left = new ArrayList<>(array.subList(0, mid));
        List<String> right = new ArrayList<>(array.subList(mid, array.size()));

        mergeSort(left, dataType, ascending);
        mergeSort(right, dataType, ascending);

        merge(array, left, right, dataType, ascending);
    }

    private static void merge(List<String> result, List<String> left, List<String> right, String dataType, boolean ascending) {
        int i = 0, j = 0, k = 0;

        while (i < left.size() && j < right.size()) {
            if (shouldSwap(left.get(i), right.get(j), dataType, ascending)) {
                result.set(k, left.get(i));
                i++;
            } else {
                result.set(k, right.get(j));
                j++;
            }
            k++;
        }

        while (i < left.size()) {
            result.set(k, left.get(i));
            i++;
            k++;
        }

        while (j < right.size()) {
            result.set(k, right.get(j));
            j++;
            k++;
        }
    }

    private static boolean shouldSwap(String a, String b, String dataType, boolean ascending) {
        int res = dataType.equals("-i") ? Integer.compare(Integer.parseInt(a), Integer.parseInt(b))
                : a.compareTo(b);
        return ascending ? res < 0 : res > 0;
    }
}
