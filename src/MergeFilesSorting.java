import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MergeFilesSorting {
    public static void main(String[] args) {

        if (args.length < 3) {
            System.err.println("Ошибка: Необходимо указать режим сортировки, тип данных и имя выходного файла.");
            return;
        }

        String sortMode = "";
        String dataType;
        String outputFilePath;
        List<String> ins = new ArrayList<>();
        if (args[1].equals("-a") || args[1].equals("-d")) {
            sortMode = args[1];
            dataType = args[0];
            outputFilePath = args[2];
            ins.addAll(Arrays.asList(args).subList(3, args.length));
        } else {
            dataType = args[0];
            ins.addAll(Arrays.asList(args).subList(2, args.length));
            outputFilePath = args[1];
        }

        try {
            File outputFile = new File(outputFilePath);
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            // Открытие входных файлов
            List<BufferedReader> readers = new ArrayList<>();
            for (String inputFile : ins) {
                readers.add(new BufferedReader(new FileReader(inputFile)));
            }

            boolean ascending = sortMode.equals("-a");
            mergeFiles(readers, writer, dataType, ascending);

            // Закрытие всех файлов
            for (BufferedReader reader : readers) {
                reader.close();
            }
            writer.close();

            System.out.println("Слияние и сортировка завершены. Результат в файле: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Ошибка ввода/вывода: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Произошла неожиданная ошибка: " + e.getMessage());
        }
    }

    private static void mergeFiles(List<BufferedReader> readers, BufferedWriter writer,
                                   String dataType, boolean ascending) throws IOException {
        List<String> elements = new ArrayList<>();

        // Заполняем список элементов из файлов
        for (BufferedReader reader : readers) {
            String line;
            while ((line = reader.readLine()) != null) {
                elements.add(line);
            }
        }

        // Выполняем сортировку слиянием
        mergeSort(elements, dataType, ascending);

        // Записываем отсортированные элементы в выходной файл
        for (String element : elements) {
            writer.write(element);
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
            String leftElement = left.get(i);
            String rightElement = right.get(j);

            if (shouldSwap(leftElement, rightElement, dataType, ascending)) {
                result.set(k, leftElement);
                i++;
            } else {
                result.set(k, rightElement);
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
        int cmp = dataType.equals("-i") ? Integer.compare(Integer.parseInt(a), Integer.parseInt(b))
                : a.compareTo(b);
        return ascending ? cmp > 0 : cmp < 0;
    }
}