package bookRecordJFrame;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

public class CSVUtility {
    private static final String FILE_PATH = "book_records.csv";

    // CSVファイルを読み込んで、書籍データをリストで返す
    public static List<String[]> readBooksFromCSV() {
        List<String[]> books = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            books = reader.readAll();
        } catch (IOException | CsvException e) {
            // ファイルが存在しない、または読み取れない場合は空のリストを返す
            e.printStackTrace();
        }
        return books;
    }

    // 書籍データをCSVファイルに書き込む
    public static void writeBooksToCSV(List<String[]> books) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH))) {
            writer.writeAll(books);
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
            if (book[0].equals(updatedBookData[0])) {
                books.set(i, updatedBookData);
                bookUpdated = true;
                break;
            }
        }

        // 書籍データが更新された場合のみ書き込む
        if (bookUpdated) {
            writeBooksToCSV(books);
        }
    }

    // 特定の書籍データを削除
    public static void deleteBookFromCSV(String bookId) {
        List<String[]> books = readBooksFromCSV();
        boolean bookDeleted = false;
        
        // 書籍データを削除
        for (int i = 0; i < books.size(); i++) {
            String[] book = books.get(i);
            if (book[0].equals(bookId)) {  // IDが一致する書籍を見つけて削除
                books.remove(i);
                bookDeleted = true;
                break;
            }
        }
        
        // 書籍データが削除された場合のみ書き込む
        if (bookDeleted) {
            writeBooksToCSV(books);
        }
    }
}