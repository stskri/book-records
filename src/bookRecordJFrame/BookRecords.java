package bookRecordJFrame;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.border.Border;
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
        setBounds(100, 100, 900, 575);
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
        
        // テーブルヘッダー用のカスタムレンダラー
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                                                           boolean isSelected, boolean hasFocus, 
                                                           int row, int column) {
                // デフォルトのヘッダーレンダラーを使用
                Component comp = super.getTableCellRendererComponent(table, value, 
                                                                     isSelected, hasFocus, 
                                                                     row, column);
                
                // ヘッダーの背景色を固定
                comp.setBackground(UIManager.getColor("TableHeader.background"));
                setHorizontalAlignment(CENTER);
                
                return comp;
            }
        });
        
        // 列の幅を調整
        table.getColumnModel().getColumn(0).setPreferredWidth(200); // Registration Date列
        table.getColumnModel().getColumn(1).setPreferredWidth(300); // Title列
        table.getColumnModel().getColumn(2).setPreferredWidth(200); // Author列
        table.getColumnModel().getColumn(3).setPreferredWidth(200); // Review列
        
        // RGB値(240, 240, 240)で色を指定
        Color tableHedderColor = new Color(240, 240, 240);

        // ヘッダー部分に下線を追加
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, tableHedderColor));
        
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
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 10, 20));

        // New Book Recordボタンを左寄せ
        JButton newBookButton = new JButton("New Book Record");
        newBookButton.addActionListener(e -> openNewBookRecord());
        newBookButton.setBackground(Color.white);

        Border newBookBorder = BorderFactory.createLineBorder(new Color(230, 230, 230), 2);
        Border newBookMargin = BorderFactory.createEmptyBorder(0, 10, 0, 10);
        newBookButton.setBorder(BorderFactory.createCompoundBorder(newBookBorder, newBookMargin)); // ボーダーと余白を結合
        newBookButton.setPreferredSize(new Dimension(150, 40)); // サイズを150x40に設定
        topPanel.add(newBookButton, BorderLayout.WEST);

        // 検索バーと総登録件数を右寄せ
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(20);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
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
        
        rightPanel.add(searchField);

        totalRecordsLabel = new JLabel(filteredBookItems.size()  + "件");
        rightPanel.add(totalRecordsLabel);

        topPanel.add(rightPanel, BorderLayout.EAST);

        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
        getContentPane().add(createPaginationPanel(), BorderLayout.SOUTH);

        showPage(currentPage);// マウスホバー効果を追加
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // 選択されている行でない場合にマウスホバーエフェクトを追加
                if (!isSelected) {
                    comp.setBackground(table.getMousePosition() != null && 
                                       table.rowAtPoint(table.getMousePosition()) == row 
                                       ? new Color(240, 240, 240)  // ホバー時の背景色（薄いグレー）
                                       : Color.WHITE);  // デフォルトの背景色
                }
                return comp;
            }
        });

        // マウスモーションリスナーを追加してテーブルを再描画
        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                table.repaint();
            }
        });
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
        int displayPages = 7;
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
                    // 各要素をアンエスケープ
                    for (int i = 0; i < data.length; i++) {
                        data[i] = unescapeCSV(data[i]);
                    }
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

    private String unescapeCSV(String input) {
        if (input == null) {
            return "";
        }
        // 前後のダブルクォーテーションを削除
        if (input.startsWith("\"") && input.endsWith("\"")) {
            input = input.substring(1, input.length() - 1);
        }
        // エスケープされた改行文字を実際の改行文字に戻す
        input = input.replace("\\r\\n", "\r\n")
                     .replace("\\n", "\n")
                     .replace("\\r", "\r")
                     .replace("\"\"", "\"");
        return input;
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
            
            // 選択されていない場合にのみ背景色を変更
            if (!isSelected) {
                comp.setBackground(table.getMousePosition() != null && 
                                   table.rowAtPoint(table.getMousePosition()) == row 
                                   ? new Color(240, 240, 240)  // ホバー時の背景色（薄いグレー）
                                   : Color.WHITE);  // デフォルトの背景色
            }
            
            setHorizontalAlignment(CENTER);
            return comp;
        }
    }

    class CenteredCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // 選択されていない場合にのみ背景色を変更
            if (!isSelected) {
                comp.setBackground(table.getMousePosition() != null && 
                                   table.rowAtPoint(table.getMousePosition()) == row 
                                   ? new Color(240, 240, 240)  // ホバー時の背景色（薄いグレー）
                                   : Color.WHITE);  // デフォルトの背景色
            }
            
            setHorizontalAlignment(CENTER);
            return comp;
        }
    }
}