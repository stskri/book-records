package bookRecordJFrame;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

public class CSVUtility {
    private static final String FILE_PATH = "book_records.csv";

    // CSVファイルを読み込んで、書籍データをリストで返す（Shift_JIS対応）
    public static List<String[]> readBooksFromCSV() {
        List<String[]> books = new ArrayList<>();
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(new FileInputStream(FILE_PATH), "Shift_JIS"))) {
            books = reader.readAll();
        } catch (IOException | CsvException e) {
            // ファイルが存在しない、または読み取れない場合は空のリストを返す
            e.printStackTrace();
        }
        return books;
    }

    // 書籍データをCSVファイルに書き込む（Shift_JIS対応）
    public static void writeBooksToCSV(List<String[]> books) {
        try (CSVWriter writer = new CSVWriter(
                new OutputStreamWriter(new FileOutputStream(FILE_PATH), "Shift_JIS"),
                CSVWriter.DEFAULT_SEPARATOR,        // デフォルトのカンマ区切り
                CSVWriter.DEFAULT_QUOTE_CHARACTER,  // デフォルトの引用符
                CSVWriter.DEFAULT_ESCAPE_CHARACTER, // デフォルトのエスケープ文字
                CSVWriter.DEFAULT_LINE_END          // デフォルトの改行コード
            )) {
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