package bookRecordJFrame;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.FlatLightLaf;

public class BookRecords extends JFrame {
    private static final int PAGE_SIZE = 10;  // 1ページに表示するアイテム数
    private int currentPage = 0;  // 現在のページ番号

    private ArrayList<String[]> bookItems; // 本のデータリスト（String[]型に変更）
    private DefaultTableModel tableModel;  // JTableのモデル
    private JTable table;  // JTableをインスタンス変数として宣言

    public BookRecords() {
        // JFrameの設定
        setTitle("Book Records");
        setBounds(100, 100, 900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // アイテムリストを作成（CSVから読み込む）
        bookItems = new ArrayList<>();
        loadBooksFromCSV();

        // IDで降順ソート（ゼロ埋めでIDをソートする）
        bookItems.sort((book1, book2) -> {
            String id1 = book1[0];
            String id2 = book2[0];
            
            // IDが数値であることを想定してゼロ埋めを行い、6桁の数値として比較する
            String zeroPaddedId1 = String.format("%06d", Integer.parseInt(id1));
            String zeroPaddedId2 = String.format("%06d", Integer.parseInt(id2));

            // ゼロ埋め後のIDを比較（降順）
            return zeroPaddedId2.compareTo(zeroPaddedId1); // 降順でソート
        });

        // JTableの列名（"Date"列を削除）
        String[] columnNames = {"ID", "Title", "Author", "Review"};

        // JTableのモデルを作成
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);  // tableをインスタンス変数として作成
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // セルレンダラーを設定してレビュー数を☆で表示
        table.getColumnModel().getColumn(3).setCellRenderer(new StarRatingRenderer());

        // 本の選択イベントリスナーを追加
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // 本を選択したときにShowBookRecordページに遷移
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String bookId = (String) table.getValueAt(selectedRow, 0);  // ここでIDを取得
                    openShowBookRecord(bookId);   // IDを渡して詳細ページを開く
                }
            }
        });

        // 「次ページ」と「前ページ」ボタンの作成
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");

        // ボタンのアクション設定
        prevButton.addActionListener(e -> showPage(currentPage - 1));
        nextButton.addActionListener(e -> showPage(currentPage + 1));

        // 新規登録画面への遷移ボタンの作成
        JButton newBookButton = new JButton("New Book Record");
        newBookButton.addActionListener(e -> openNewBookRecord());

        // ボタンを配置
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(newBookButton);  // 新規登録ボタンを追加

        // コンテンツパネルに追加
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // 初期ページを表示
        showPage(currentPage);
    }

    // CSVファイルから本のデータを読み込むメソッド
    private void loadBooksFromCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader("book_records.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {  // データが5つの項目を持つ場合
                    // "Date"の項目を削除（データ配列の4番目の要素は不要）
                    String[] filteredData = new String[4];
                    System.arraycopy(data, 0, filteredData, 0, 4);  // 最初の4つの項目をコピー
                    bookItems.add(filteredData);  // 新しい配列を追加
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPage(int page) {
        if (page < 0 || page * PAGE_SIZE >= bookItems.size()) {
            return;  // 無効なページ番号の場合は何もしない
        }

        // 新しいページのアイテムを設定
        tableModel.setRowCount(0);  // 既存の行を削除
        int startIndex = page * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, bookItems.size());
        for (int i = startIndex; i < endIndex; i++) {
            String[] bookData = bookItems.get(i);
            tableModel.addRow(bookData);  // データを行として追加
        }

        // 行の高さを調整（例えば、テーブル全体の高さが 400px と仮定）
        int rowHeight = 400 / PAGE_SIZE; // 10行の場合の行高
        table.setRowHeight(rowHeight);  // 行高さを設定

        // 現在のページ番号を更新
        currentPage = page;
    }

    // NewBookRecord画面を開くメソッド
    private void openNewBookRecord() {
        dispose();
        new NewBookRecord().setVisible(true);
    }

    // ShowBookRecordページを開くメソッド
    private void openShowBookRecord(String bookId) {
        dispose();  // 現在のページを閉じる
        new ShowBookRecord(bookId).setVisible(true); // IDを渡して新しいページに遷移
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            BookRecords frame = new BookRecords();
            frame.setVisible(true);
        });
    }

    // ★を表示するカスタムセルレンダラー
    class StarRatingRenderer extends DefaultTableCellRenderer {
        @Override
        protected void setValue(Object value) {
            if (value instanceof String) {
                String reviewStr = (String) value;
                try {
                    int reviewCount = Integer.parseInt(reviewStr);

                    // ★を表示するためのHTML形式を作成
                    StringBuilder stars = new StringBuilder("<html>");
                    for (int i = 0; i < 5; i++) {
                        if (i < reviewCount) {
                            stars.append("<font color='yellow'>★</font>");
                        } else {
                            stars.append("<font color='gray'>★</font>");
                        }
                    }
                    stars.append("</html>");

                    // JLabelにHTMLで表示
                    JLabel label = new JLabel(stars.toString());
                    setText(""); // テキストを空にして
                    setIcon(null); // アイコンもクリア
                    setHorizontalAlignment(CENTER); // 中央に配置
                    setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                    setText(label.getText()); // JLabelのHTMLをセルにセット
                } catch (NumberFormatException e) {
                    setText("Invalid review");
                }
            }
        }
    }
}