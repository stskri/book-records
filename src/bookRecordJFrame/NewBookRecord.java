package bookRecordJFrame;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

import com.formdev.flatlaf.FlatLightLaf;

public class NewBookRecord extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField titleField;
    private JTextField authorField;
    private JLabel[] stars = new JLabel[5];
    private int selectedReview = 1; // デフォルト評価は1
    private JTextArea reviewTextArea;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            try {
                NewBookRecord frame = new NewBookRecord();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public NewBookRecord() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 500);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 255, 255));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // タイトル入力フィールド
        JLabel titleLabel = new JLabel("Book Title:");
        titleLabel.setBounds(50, 50, 100, 30);
        contentPane.add(titleLabel);

        titleField = new JTextField();
        titleField.setBounds(150, 50, 600, 30);  // 幅をウィンドウに合わせて調整
        Border border = BorderFactory.createLineBorder(new Color(211, 211, 211), 2); // 灰色の太めのボーダー
        Border margin = BorderFactory.createEmptyBorder(0, 10, 0, 10); // 左右に余白
        titleField.setBorder(BorderFactory.createCompoundBorder(border, margin)); // ボーダーと余白を結合
        contentPane.add(titleField);

     // 作者入力フィールド
        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setBounds(50, 100, 100, 30);
        contentPane.add(authorLabel);

        authorField = new JTextField();
        authorField.setBounds(150, 100, 600, 30);  // 幅をウィンドウに合わせて調整
        Border authorBorder = BorderFactory.createLineBorder(new Color(211, 211, 211), 2); // 灰色の太めのボーダー
        Border authorMargin = BorderFactory.createEmptyBorder(0, 10, 0, 10); // 左右に余白
        authorField.setBorder(BorderFactory.createCompoundBorder(authorBorder, authorMargin)); // ボーダーと余白を結合
        contentPane.add(authorField);

        // 星評価のラベル
        JLabel reviewLabel = new JLabel("Review:");
        reviewLabel.setBounds(50, 150, 100, 30);
        contentPane.add(reviewLabel);

        // 星アイコンを配置
        for (int i = 0; i < 5; i++) {
            stars[i] = new JLabel("☆");
            stars[i].setFont(new Font("SansSerif", Font.PLAIN, 30));
            stars[i].setBounds(150 + (i * 40), 140, 40, 40);
            stars[i].setForeground(Color.GRAY); // 初期は灰色
            contentPane.add(stars[i]);

            final int index = i + 1;

            // マウスイベントを追加
            stars[i].addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    setReviewStars(index);
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    highlightStars(index);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    setReviewStars(selectedReview);
                }
            });
        }

     // 感想入力エリア
        JLabel reviewTextLabel = new JLabel("Review Text:");
        reviewTextLabel.setBounds(50, 200, 100, 30);
        contentPane.add(reviewTextLabel);

        reviewTextArea = new JTextArea();
        reviewTextArea.setBounds(150, 200, 600, 100);
        reviewTextArea.setLineWrap(true); // 自動改行を有効にする
        reviewTextArea.setWrapStyleWord(true); // 単語単位で改行
        contentPane.add(reviewTextArea);
        
        // スクロールペインを作成して、JTextAreaをその中に配置
        JScrollPane scrollPane = new JScrollPane(reviewTextArea);
        scrollPane.setBounds(150, 200, 600, 100);
        // スクロールペインのボーダーを削除
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        // ビューのボーダーを設定（JScrollPaneが持つビューのボーダーを設定）
        scrollPane.setViewportBorder(BorderFactory.createLineBorder(new Color(211, 211, 211), 2));
        // スクロールペインをコンテンツパネルに追加
        contentPane.add(scrollPane);
        
        // 保存ボタン
        JButton saveButton = new JButton("Save Record");
        saveButton.setBounds(50, 350, 150, 40);
        contentPane.add(saveButton);

        // 戻るボタン
        JButton backButton = new JButton("Back to List");
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setForeground(Color.WHITE);
        backButton.setBounds(255, 350, 150, 40);
        backButton.addActionListener(e -> {
            dispose();
            new BookRecords().setVisible(true);
        });
        contentPane.add(backButton);

        saveButton.addActionListener(e -> {
            String title = titleField.getText();
            String author = authorField.getText();
            String reviewText = reviewTextArea.getText();

            // 文字数制限チェック
            StringBuilder errorMessage = new StringBuilder();

            // タイトルチェック
            if (title.isEmpty()) {
                errorMessage.append("タイトルを入力してください。\n");
            } else if (title.length() < 1 || title.length() > 30) {
                errorMessage.append("タイトルは1〜30文字にしてください。\n");
            }

            // 著者チェック
            if (author.isEmpty()) {
                errorMessage.append("著者を入力してください。\n");
            } else if (author.length() < 1 || author.length() > 15) {
                errorMessage.append("著者は1〜15文字にしてください。\n");
            }

            // 感想チェック
            if (reviewText.isEmpty()) {
                errorMessage.append("感想を入力してください。\n");
            } else if (reviewText.length() < 1 || reviewText.length() > 400) {
                errorMessage.append("感想は1〜400文字にしてください。\n");
            }

            // エラーチェック
            if (errorMessage.length() > 0) {
                JOptionPane.showMessageDialog(null, errorMessage.toString(), "エラー", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 保存処理
            saveBookRecord(title, author, selectedReview, reviewText);
            JOptionPane.showMessageDialog(null, "Record saved successfully!");
            dispose();
            new BookRecords().setVisible(true);
        });

        // 初期の星の表示を設定
        setReviewStars(selectedReview);
    }

    // 星をハイライトするメソッド
    private void highlightStars(int count) {
        for (int i = 0; i < 5; i++) {
            if (i < count) {
                stars[i].setText("★");
                stars[i].setForeground(Color.YELLOW);
            } else {
                stars[i].setText("☆");
                stars[i].setForeground(Color.GRAY);
            }
        }
    }

    // 星の選択を確定するメソッド
    private void setReviewStars(int count) {
        selectedReview = count;
        highlightStars(count);
    }

    // 書籍データをCSVに保存
    private void saveBookRecord(String title, String author, int review, String reviewText) {
        try {
            int nextId = calculateNextId();
            String formattedId = String.format("%08d", nextId);
            String timestamp = getCurrentTimestamp();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("book_records.csv", true), "UTF-8"))) {
                writer.write(formattedId + "," + timestamp + "," + title + "," + author + "," + review + "," + reviewText);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving the record.");
        }
    }

    // 次のIDを計算するメソッド
    private int calculateNextId() {
        int nextId = 1;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("book_records.csv"), "UTF-8"))) {
            String line;
            String lastId = "";
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length > 0) lastId = data[0];
            }
            if (!lastId.isEmpty()) {
                nextId = Integer.parseInt(lastId) + 1;
            }
        } catch (IOException e) {
            System.out.println("No previous records found, starting from ID = 1.");
        }
        return nextId;
    }

    // 現在の東京時刻を取得
    private String getCurrentTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now(ZoneId.of("Asia/Tokyo")).format(formatter);
    }
}