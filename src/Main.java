import model.AddressJAXB;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Основное задание
 * 1. Написать программу для парсинга xml документа.
 * Программа на вход получает строку к папке, где находится документ.
 * Распарсить нужно только один документ - соответственно,
 * предусмотреть различные проверки, например на то, что в папке нет
 * файлов, или в папке несколько документов формата xml и другие
 * возможные проверки.
 * Необходимо распарсить xml документ и содержимое тегов line записать в
 * другой документ.
 * Название файла для записи должно состоять из значений тегов и имеет
 * вид: <firstName>_<lastName>_<title>.txt
 * Дополнительное задание
 * 2. Создать схему для документа XML.
 * 3. Реализовать работу с XML документов используя JAXB.
 */

public class Main {
    public static void main(String[] args) throws JAXBException, IOException {
        String folderPath = getPathFromConsole();
        Set<Path> paths = getPathsToAllXMLDocumentsInFolder(folderPath);
        System.out.println("Paths with xml extension: " + paths);
        if (!isXMLFilesValid(paths)) {
            return;
        }
        AddressJAXB addressJAXB = unmarshal(paths.iterator().next().toString());
        System.out.println(addressJAXB);
        writeFinalResult(addressJAXB);
    }

    private static String getPathFromConsole() {
        String folderPath = null;
        try (Scanner sc = new Scanner(System.in)) {//чтобы выполнить sc.close() = finally
            System.out.println("Please enter folder path: ");
            if (sc.hasNextLine()) {
                folderPath = sc.nextLine();
            }
        }
        return folderPath;
    }

    private static Set<Path> getPathsToAllXMLDocumentsInFolder(String folderPath) throws IOException {
        Set<Path> requiredFilesPaths;
        String regexPath = ".+\\.(xml)";
        try (Stream<Path> pathsStream = Files.list(Paths.get(folderPath))) {
            requiredFilesPaths = pathsStream
                    .map(Path::toString)//проверить почему String.class::cast не прокатил
                    .filter(path -> Pattern.matches(regexPath, path))
                    .map(Path::of)
                    .collect(Collectors.toSet());
        }
        return requiredFilesPaths;
    }

    private static boolean isXMLFilesValid(Set<Path> paths) throws IOException {
        final int REQUIRED_AMOUNT_OF_FILES = 1;
        if (paths.isEmpty()) {
            return false;
        } else if (Files.readAllLines(paths.iterator().next()).isEmpty()) {
            return false;
        } else return paths.stream().count() <= REQUIRED_AMOUNT_OF_FILES;
    }

    private static AddressJAXB unmarshal(String pathToFile) throws FileNotFoundException, JAXBException {
        JAXBContext context = JAXBContext.newInstance(AddressJAXB.class);
        return (AddressJAXB) context.createUnmarshaller().unmarshal(new FileReader(pathToFile));
    }

    private static void writeFinalResult(AddressJAXB addressJAXB) throws IOException {
        Path filePath = Path.of("src/resources/" + addressJAXB.getStreet() + "_" + addressJAXB.getHouseNumber() + "_" + addressJAXB.getPostCode());
        Iterable<String> lst = List.of(addressJAXB.getStreet(), addressJAXB.getHouseNumber(), addressJAXB.getPostCode()
                , addressJAXB.getCity(), addressJAXB.getCountry());
        Files.write(filePath, lst);
    }

}

