package bookRecordJFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.FlatLightLaf;

public class BookRecords extends JFrame {
    private static final int PAGE_SIZE = 10;
    private int currentPage = 0;
    private ArrayList<String[]> bookItems;
    private ArrayList<String[]> filteredBookItems;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel totalRecordsLabel;
    private JTextField searchField;

    public BookRecords() {
        setTitle("Book Records");
        setBounds(100, 100, 900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        bookItems = new ArrayList<>();
        filteredBookItems = new ArrayList<>();
        loadBooksFromCSV();

        // 登録日時で降順ソート
        bookItems.sort((book1, book2) -> book2[0].compareTo(book1[0]));  // idの降順

        // JTableの列名（IDと感想を除いた4項目）
        String[] columnNames = {"Registration Date", "Title", "Author", "Review"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 日付の列を中央揃え
        table.getColumnModel().getColumn(0).setCellRenderer(new CenteredCellRenderer());

        // レビューの列を中央揃え
        table.getColumnModel().getColumn(3).setCellRenderer(new StarRatingRenderer());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int dataIndex = selectedRow + (currentPage * PAGE_SIZE);
                    String bookId = filteredBookItems.get(dataIndex)[0];
                    openShowBookRecord(bookId);
                }
            }
        });

        getContentPane().setLayout(new BorderLayout());

        // 上部パネルの作成 (New Book Recordボタンと総登録件数の表示)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // 右側に30ピクセルの余白
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        
        // 新規登録ボタン
        JButton newBookButton = new JButton("New Book Record");
        newBookButton.addActionListener(e -> openNewBookRecord());
        topPanel.add(newBookButton);

        // 検索バー
        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterBooks();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterBooks();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterBooks();
            }
        });
        topPanel.add(searchField);

        // 総登録件数の表示
        totalRecordsLabel = new JLabel("Total Records: " + filteredBookItems.size());
        topPanel.add(totalRecordsLabel);

        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
        getContentPane().add(createPaginationPanel(), BorderLayout.SOUTH);

        showPage(currentPage);
    }

    private JPanel createPaginationPanel() {
        JPanel paginationPanel = new JPanel();

        // 1. 全ページ数を計算
        int totalPages = (int) Math.ceil((double) filteredBookItems.size() / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1; // 本がない場合でも最低1ページ

        // 2. 前ページへボタン
        JButton prevButton = new JButton("<<");
        prevButton.addActionListener(e -> showPage(currentPage - 1));
        prevButton.setEnabled(currentPage > 0);
        prevButton.setPreferredSize(new Dimension(50, 30)); // 固定サイズ設定
        paginationPanel.add(prevButton);

        // 3. ページ番号ボタンを表示（Google風に）
        int displayPages = 7; // 9から7に減らす
        int startPage = Math.max(0, Math.min(currentPage - displayPages / 2, totalPages - displayPages));
        int endPage = Math.min(startPage + displayPages, totalPages);

        if (endPage - startPage < displayPages && startPage > 0) {
            startPage = Math.max(0, endPage - displayPages);
        }

        for (int i = startPage; i < endPage; i++) {
            JButton pageButton = new JButton(String.valueOf(i + 1));
            if (i == currentPage) {
                pageButton.setEnabled(false);
                pageButton.setBackground(UIManager.getColor("Button.highlight"));
            }
            pageButton.setPreferredSize(new Dimension(50, 30));
            final int pageNum = i;
            pageButton.addActionListener(e -> showPage(pageNum));
            paginationPanel.add(pageButton);
        }

        // 4. 次ページへボタン
        JButton nextButton = new JButton(">>");
        nextButton.addActionListener(e -> showPage(currentPage + 1));
        nextButton.setEnabled(currentPage < totalPages - 1);
        nextButton.setPreferredSize(new Dimension(50, 30));
        paginationPanel.add(nextButton);

        return paginationPanel;
    }

    private void loadBooksFromCSV() {
        bookItems.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("book_records.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 6) {
                    bookItems.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // idの降順でソート
        bookItems.sort((book1, book2) -> book2[0].compareTo(book1[0]));

        // フィルタリング用にfilteredBookItemsをセット
        filteredBookItems.clear();
        filteredBookItems.addAll(bookItems);
    }

    private void showPage(int page) {
        int totalPages = (int) Math.ceil((double) filteredBookItems.size() / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;

        if (page < 0 || page >= totalPages) return;

        tableModel.setRowCount(0);
        int startIndex = page * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, filteredBookItems.size());
        for (int i = startIndex; i < endIndex; i++) {
            String[] bookData = filteredBookItems.get(i);
            String formattedDate = formatDate(bookData[1]);
            tableModel.addRow(new String[]{formattedDate, bookData[2], bookData[3], bookData[4]});
        }

        table.setRowHeight(400 / PAGE_SIZE);
        currentPage = page;

        // ページネーションパネルを更新
        getContentPane().remove(getContentPane().getComponentCount() - 1);
        getContentPane().add(createPaginationPanel(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private void filterBooks() {
        String searchText = searchField.getText().toLowerCase();
        filteredBookItems.clear();

        for (String[] book : bookItems) {
            String title = book[2].toLowerCase();
            String author = book[3].toLowerCase();
            if (title.contains(searchText) || author.contains(searchText)) {
                filteredBookItems.add(book);
            }
        }

        totalRecordsLabel.setText("Total Records: " + filteredBookItems.size());
        showPage(0);
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = inputFormat.parse(dateStr);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年M月d日");
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }

    private void openNewBookRecord() {
        dispose();
        new NewBookRecord().setVisible(true);
    }

    private void openShowBookRecord(String bookId) {
        dispose();
        new ShowBookRecord(bookId).setVisible(true);
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

    class StarRatingRenderer extends DefaultTableCellRenderer {
        @Override
        protected void setValue(Object value) {
            if (value instanceof String) {
                try {
                    int reviewCount = Integer.parseInt((String) value);
                    StringBuilder stars = new StringBuilder("<html>");
                    for (int i = 0; i < 5; i++) {
                        stars.append(i < reviewCount ? "<font color='yellow'>★</font>" : "<font color='gray'>★</font>");
                    }
                    stars.append("</html>");
                    setText(stars.toString());
                } catch (NumberFormatException e) {
                    setText("Invalid review");
                }
            }
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(CENTER);
            return comp;
        }
    }

    class CenteredCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(CENTER);
            return comp;
        }
    }
}