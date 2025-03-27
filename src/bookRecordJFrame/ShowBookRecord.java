package bookRecordJFrame;

import java.awt.Color;
import java.awt.Font;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

import com.formdev.flatlaf.FlatLightLaf;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class ShowBookRecord extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JLabel[] stars = new JLabel[5];
    private String[] bookData;

    public ShowBookRecord(String bookId) {
    	setTitle("詳細画面");
    	
        initializeFrame();
        
        // 本のデータをCSVから読み込む
        bookData = loadBookDataById(bookId);
        if (bookData == null) {
            dispose();
            return;
        }
        
        setupComponents();
    }

    // フレームの初期設定
    private void initializeFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 550);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 255, 255));
        setContentPane(contentPane);
        contentPane.setLayout(null);
    }

    // コンポーネントのセットアップ
    private void setupComponents() {
        // 戻るボタン（一覧画面に戻る）
        JButton backToListButton = new JButton("一覧画面に戻る");
        backToListButton.setForeground(new Color(100, 220, 220));
        backToListButton.setBackground(new Color(252, 252, 252));
        backToListButton.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 0));
        backToListButton.addActionListener(e -> {
            dispose();
            new BookRecords().setVisible(true);
        });
        backToListButton.setBounds(30, 25, 150, 40);
        contentPane.add(backToListButton);

        // IDと登録日表示
        JLabel idLabel = new JLabel("ID: " + bookData[0]);
        idLabel.setBounds(190, 30, 120, 30);
        idLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(idLabel);

        JLabel dateLabel = new JLabel("登録日: " + formatDate(bookData[1]));
        dateLabel.setBounds(320, 30, 200, 30);
        dateLabel.setHorizontalAlignment(SwingConstants.LEFT);
        contentPane.add(dateLabel);
        
        // タイトル
        JLabel titleLabel = new JLabel("Book Title:");
        titleLabel.setBounds(100, 90, 75, 30);
        contentPane.add(titleLabel);

        JLabel titleValueLabel = new JLabel(bookData[2]);
        titleValueLabel.setBounds(185, 90, 600, 30);
        titleValueLabel.setBorder(createTextFieldBorder());
        titleValueLabel.setBackground(new Color(250, 250, 250));
        titleValueLabel.setOpaque(true);
        contentPane.add(titleValueLabel);

        // 著者
        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setBounds(100, 140, 75, 30);
        contentPane.add(authorLabel);

        JLabel authorValueLabel = new JLabel(bookData[3]);
        authorValueLabel.setBounds(185, 140, 600, 30);
        authorValueLabel.setBorder(createTextFieldBorder());
        authorValueLabel.setBackground(new Color(250, 250, 250));
        authorValueLabel.setOpaque(true);
        contentPane.add(authorValueLabel);

        // 星評価のラベル
        JLabel reviewLabel = new JLabel("Review:");
        reviewLabel.setBounds(100, 190, 75, 30);
        contentPane.add(reviewLabel);

        // 星アイコンを配置
        setupStarRating(bookData[4]);

        // 感想セクションラベル
        JLabel thoughtsTextLabel = new JLabel("Review Text:");
        thoughtsTextLabel.setBounds(100, 240, 75, 30);
        contentPane.add(thoughtsTextLabel);

        // 感想エリア
        JTextArea thoughtsArea = new JTextArea(bookData[5]);
        thoughtsArea.setLineWrap(true);
        thoughtsArea.setWrapStyleWord(true);
        thoughtsArea.setEditable(false);
        thoughtsArea.setBackground(new Color(250, 250, 250));
      
        JScrollPane thoughtsScrollPane = new JScrollPane(thoughtsArea);
        thoughtsScrollPane.setBounds(185, 240, 600, 180);
        thoughtsScrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 2));
        thoughtsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentPane.add(thoughtsScrollPane);

        // 編集ボタン
        JButton editButton = new JButton("編集");
        editButton.setBounds(350, 445, 200, 40);
        editButton.setBackground(new Color(255, 255, 255));  // 青系の背景色
        editButton.setForeground(Color.blue);
        editButton.setBorder(BorderFactory.createLineBorder(Color.blue, 2));
        editButton.addActionListener(e -> {
            dispose();
            new EditBookRecord(bookData[0]).setVisible(true);
        });
        contentPane.add(editButton);
    }

    // テキストフィールド風のボーダー作成
    private Border createTextFieldBorder() {
        Border border = BorderFactory.createLineBorder(new Color(230, 230, 230), 2);
        Border margin = BorderFactory.createEmptyBorder(0, 10, 0, 10);
        return BorderFactory.createCompoundBorder(border, margin);
    }

    // スター評価の設定
    private void setupStarRating(String ratingStr) {
        int selectedReview = Integer.parseInt(ratingStr);

        for (int i = 0; i < 5; i++) {
            stars[i] = new JLabel("★");
            stars[i].setFont(new Font("SansSerif", Font.PLAIN, 30));
            stars[i].setBounds(185 + (i * 40), 185, 40, 40);
            stars[i].setForeground(i < selectedReview ? Color.YELLOW : Color.GRAY);
            contentPane.add(stars[i]);
        }
    }

    // 日付のフォーマット
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

    // CSVからデータを読み込むメソッド
    private String[] loadBookDataById(String bookId) {
        try (CSVReader csvReader = new CSVReader(new FileReader("book_records.csv"))) {
            List<String[]> allRecords = csvReader.readAll();
            for (String[] record : allRecords) {
                if (record[0].equals(bookId)) {
                    return record;
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            ShowBookRecord frame = new ShowBookRecord("1");
            frame.setVisible(true);
        });
    }
}