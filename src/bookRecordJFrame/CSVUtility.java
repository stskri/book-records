package bookRecordJFrame;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class CSVUtility {

    private static final String FILE_PATH = "book_records.csv";  // CSVファイルのパスをbook_records.csvに変更

    // CSVファイルを読み込んで、書籍データをリストで返す
    public static List<String[]> readBooksFromCSV() {
        List<String[]> books = new ArrayList<>();
        try {
            // ファイルが存在するか確認し、ない場合は新しく作成する
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            // ファイルを読み込み
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                books.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }

    // 書籍データをCSVファイルに書き込む
    public static void writeBooksToCSV(List<String[]> books) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String[] book : books) {
                writer.write(String.join(",", book));  // 配列の各要素をカンマ区切りで書き込み
                writer.newLine();  // 行を改行
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 特定の書籍データを更新
    public static void updateBookInCSV(String[] updatedBookData) {
        List<String[]> books = readBooksFromCSV();
        boolean bookUpdated = false;

        // 書籍データを更新
        for (int i = 0; i < books.size(); i++) {
            String[] book = books.get(i);
            if (book[0].equals(updatedBookData[0])) {  // タイトルで一致する書籍を見つけて更新
                books.set(i, updatedBookData);
                bookUpdated = true;
                break;
            }
        }

        // 書籍データが更新された場合のみ書き込む
        if (bookUpdated) {
            writeBooksToCSV(books);  // 更新されたデータをCSVに書き込む
        }
    }
}