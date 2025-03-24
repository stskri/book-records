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
        setBounds(100, 100, 900, 465);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 255, 255));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // 戻るボタン（左上に配置）
        JButton backButton = new JButton("Back to List");
        backButton.setForeground(new Color(220, 220, 220)); // 淡い黒色
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14)); // 太字、少し大きい
        backButton.setBounds(30, 25, 150, 40); // 左上に配置
        backButton.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 0)); // ボーダー追加
        backButton.setBackground(null); // 背景色を削除



        backButton.addActionListener(e -> {
            dispose();
            new BookRecords().setVisible(true);
        });
        contentPane.add(backButton);

        // タイトル入力フィールド
        JLabel titleLabel = new JLabel("Book Title:");
        titleLabel.setBounds(100, 85, 75, 30);
        contentPane.add(titleLabel);

        titleField = new JTextField();
        titleField.setBounds(185, 85, 600, 30);  // 幅をウィンドウに合わせて調整
        Border border = BorderFactory.createLineBorder(new Color(230, 230, 230), 2); // 灰色の太めのボーダー
        Border margin = BorderFactory.createEmptyBorder(0, 10, 0, 10); // 左右に余白
        titleField.setBorder(BorderFactory.createCompoundBorder(border, margin)); // ボーダーと余白を結合
        contentPane.add(titleField);

        // 作者入力フィールド
        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setBounds(100, 135, 75, 30);
        contentPane.add(authorLabel);

        authorField = new JTextField();
        authorField.setBounds(185, 135, 600, 30);  // 幅をウィンドウに合わせて調整
        Border authorBorder = BorderFactory.createLineBorder(new Color(230, 230, 230), 2); // 灰色の太めのボーダー
        Border authorMargin = BorderFactory.createEmptyBorder(0, 10, 0, 10); // 左右に余白
        authorField.setBorder(BorderFactory.createCompoundBorder(authorBorder, authorMargin)); // ボーダーと余白を結合
        contentPane.add(authorField);

        // 星評価のラベル
        JLabel reviewLabel = new JLabel("Review:");
        reviewLabel.setBounds(100, 185, 75, 30);
        contentPane.add(reviewLabel);

        // 星アイコンを配置
        for (int i = 0; i < 5; i++) {
            stars[i] = new JLabel("★");
            stars[i].setFont(new Font("SansSerif", Font.PLAIN, 30));
            stars[i].setBounds(185 + (i * 40), 180, 40, 40);
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
        reviewTextLabel.setBounds(100, 235, 75, 30);
        contentPane.add(reviewTextLabel);

        // JTextAreaのボーダー内に余白を設定
        reviewTextArea = new JTextArea();
        reviewTextArea.setBounds(150, 250, 600, 100);
        reviewTextArea.setLineWrap(true); // 自動改行を有効にする
        reviewTextArea.setWrapStyleWord(true); // 単語単位で改行

        // ボーダーと余白を設定
        Border reviewTextBorder = BorderFactory.createLineBorder(new Color(255, 255, 255), 0); // ボーダー
        Border reviewTextMargin = BorderFactory.createEmptyBorder(5, 10, 5, 10); // 上下、左右に均等な余白
        reviewTextArea.setBorder(BorderFactory.createCompoundBorder(reviewTextBorder, reviewTextMargin)); // ボーダーと余白を結合

        // スクロールペインを作成して、JTextAreaをその中に配置
        JScrollPane scrollPane = new JScrollPane(reviewTextArea);
        scrollPane.setBounds(185, 235, 600, 100);  // 幅をウィンドウに合わせて調整
        scrollPane.setBorder(BorderFactory.createEmptyBorder());  // スクロールペインのボーダーを削除
        scrollPane.setViewportBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 2));  // ビューのボーダーを設定
        contentPane.add(scrollPane);

        // 保存ボタン（中央配置）
        JButton saveButton = new JButton("Save Record");
        saveButton.setBounds(375, 360, 150, 40); // 中央配置
        contentPane.add(saveButton);

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

    private void setReviewStars(int rating) {
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars[i].setForeground(Color.YELLOW); // 黄色に変更
            } else {
                stars[i].setForeground(Color.GRAY); // 灰色に変更
            }
        }
        selectedReview = rating;
    }

    private void highlightStars(int rating) {
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars[i].setForeground(Color.YELLOW); // 黄色に変更
            } else {
                stars[i].setForeground(Color.GRAY); // 灰色に変更
            }
        }
    }

    private void saveBookRecord(String title, String author, int review, String reviewText) {
        // CSV保存処理（仮）
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("book_records.csv", true), "UTF-8"))) {
            // CSVファイルの内容を読み込んで最新のIDを取得
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("book_records.csv"), "UTF-8"));
            String lastLine = null;
            String line;
            while ((line = reader.readLine()) != null) {
                lastLine = line;
            }
            reader.close();

            // 最新のIDを抽出し、次のIDを計算
            int nextId = 1;
            if (lastLine != null) {
                String[] lastRecord = lastLine.split(",");
                nextId = Integer.parseInt(lastRecord[0].trim()) + 1;
            }

            // IDを8桁で0詰め
            String id = String.format("%08d", nextId);

            // 現在の時間を取得
            LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = currentTime.format(formatter);

            // CSVに書き込む（半角スペースを削除）
            writer.write(String.format("%s,%s,%s,%s,%d,%s%n", id, formattedTime, title, author, review, reviewText));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}