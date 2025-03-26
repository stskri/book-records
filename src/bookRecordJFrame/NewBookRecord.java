package bookRecordJFrame;

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.formdev.flatlaf.FlatLightLaf;

public class NewBookRecord extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField titleField;
    private JTextField authorField;
    private JLabel[] stars = new JLabel[5];
    private int selectedReview = 1; // デフォルト評価は1
    private JTextArea reviewTextArea;
    private JButton saveButton;
    private JLabel titleErrorLabel;
    private JLabel authorErrorLabel;
    private JLabel reviewErrorLabel;

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

        // 戻るボタン
        JButton backButton = new JButton("Back to List");
        setupBackButton(backButton);

        // タイトル入力フィールド
        JLabel titleLabel = new JLabel("Book Title:");
        titleLabel.setBounds(100, 85, 75, 30);
        contentPane.add(titleLabel);

        titleField = new JTextField();
        titleField.setBounds(185, 85, 600, 30);
        setupTextField(titleField);

        titleErrorLabel = new JLabel();
        titleErrorLabel.setBounds(185, 115, 600, 20);
        titleErrorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        contentPane.add(titleErrorLabel);

        // 作者入力フィールド
        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setBounds(100, 135, 75, 30);
        contentPane.add(authorLabel);

        authorField = new JTextField();
        authorField.setBounds(185, 135, 600, 30);
        setupTextField(authorField);

        authorErrorLabel = new JLabel();
        authorErrorLabel.setBounds(185, 165, 600, 20);
        authorErrorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        contentPane.add(authorErrorLabel);

        // 星評価のラベル
        JLabel reviewLabel = new JLabel("Review:");
        reviewLabel.setBounds(100, 185, 75, 30);
        contentPane.add(reviewLabel);

        // 星アイコンを配置
        setupStarRating();

        // 感想入力エリア
        JLabel reviewTextLabel = new JLabel("Review Text:");
        reviewTextLabel.setBounds(100, 235, 75, 30);
        contentPane.add(reviewTextLabel);

        reviewTextArea = new JTextArea();
        reviewTextArea.setLineWrap(true);
        reviewTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(reviewTextArea);
        scrollPane.setBounds(185, 235, 600, 100);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 2));
        contentPane.add(scrollPane);

        reviewErrorLabel = new JLabel();
        reviewErrorLabel.setBounds(185, 335, 600, 20);
        reviewErrorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        contentPane.add(reviewErrorLabel);

        // 保存ボタン
        saveButton = new JButton("Save Record");
        saveButton.setBounds(375, 360, 150, 40);
        contentPane.add(saveButton);

        // バリデーションとイベントリスナーのセットアップ
        setupValidation();
        setupSaveButton();
    }

    private void setupBackButton(JButton backButton) {
        backButton.setForeground(new Color(220, 220, 220));
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.setBackground(new Color(245, 245, 245));
        backButton.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 0));
        backButton.addActionListener(e -> {
            dispose();
            new BookRecords().setVisible(true);
        });
        contentPane.add(backButton);
        backButton.setBounds(30, 25, 150, 40);
    }

    private void setupTextField(JTextField textField) {
        Border border = BorderFactory.createLineBorder(new Color(230, 230, 230), 2);
        Border margin = BorderFactory.createEmptyBorder(0, 10, 0, 10);
        textField.setBorder(BorderFactory.createCompoundBorder(border, margin));
        contentPane.add(textField);
    }

    private void setupStarRating() {
        for (int i = 0; i < 5; i++) {
            stars[i] = new JLabel("★");
            stars[i].setFont(new Font("SansSerif", Font.PLAIN, 30));
            stars[i].setBounds(185 + (i * 40), 180, 40, 40);
            stars[i].setForeground(Color.GRAY);
            contentPane.add(stars[i]);

            final int index = i + 1;
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
        setReviewStars(selectedReview);
    }

    private void setupValidation() {
        setupTitleValidation();
        setupAuthorValidation();
        setupReviewValidation();
    }

    private void setupTitleValidation() {
        titleField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateTitle();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateTitle();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateTitle();
            }

            private void validateTitle() {
                String title = titleField.getText().trim();
                if (title.isEmpty()) {
                    showTitleError("タイトルを入力してください");
                } else if (title.length() < 1 || title.length() > 30) {
                    showTitleError("タイトルは1〜30文字にしてください");
                } else {
                    clearTitleError();
                }
                updateSaveButtonState();
            }
        });
    }

    private void setupAuthorValidation() {
        authorField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateAuthor();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateAuthor();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateAuthor();
            }

            private void validateAuthor() {
                String author = authorField.getText().trim();
                if (author.isEmpty()) {
                    showAuthorError("著者を入力してください");
                } else if (author.length() < 1 || author.length() > 15) {
                    showAuthorError("著者は1〜15文字にしてください");
                } else {
                    clearAuthorError();
                }
                updateSaveButtonState();
            }
        });
    }

    private void setupReviewValidation() {
        reviewTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateReview();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateReview();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateReview();
            }

            private void validateReview() {
                String reviewText = reviewTextArea.getText().trim();
                if (reviewText.isEmpty()) {
                    showReviewError("感想を入力してください");
                } else if (reviewText.length() < 1 || reviewText.length() > 400) {
                    showReviewError("感想は1〜400文字にしてください");
                } else {
                    clearReviewError();
                }
                updateSaveButtonState();
            }
        });
    }

    private void showTitleError(String message) {
        titleErrorLabel.setText(message);
        titleErrorLabel.setForeground(Color.RED);
    }

    private void clearTitleError() {
        titleErrorLabel.setText("");
    }

    private void showAuthorError(String message) {
        authorErrorLabel.setText(message);
        authorErrorLabel.setForeground(Color.RED);
    }

    private void clearAuthorError() {
        authorErrorLabel.setText("");
    }

    private void showReviewError(String message) {
        reviewErrorLabel.setText(message);
        reviewErrorLabel.setForeground(Color.RED);
    }

    private void clearReviewError() {
        reviewErrorLabel.setText("");
    }

    private void updateSaveButtonState() {
        boolean isTitleValid = isTitleValid();
        boolean isAuthorValid = isAuthorValid();
        boolean isReviewValid = isReviewValid();

        saveButton.setEnabled(isTitleValid && isAuthorValid && isReviewValid);
    }

    private boolean isTitleValid() {
        String title = titleField.getText().trim();
        return title.length() >= 1 && title.length() <= 30;
    }

    private boolean isAuthorValid() {
        String author = authorField.getText().trim();
        return author.length() >= 1 && author.length() <= 15;
    }

    private boolean isReviewValid() {
        String reviewText = reviewTextArea.getText().trim();
        return reviewText.length() >= 1 && reviewText.length() <= 400;
    }

    private void setupSaveButton() {
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String reviewText = reviewTextArea.getText().trim();

            saveBookRecord(title, author, selectedReview, reviewText);
            JOptionPane.showMessageDialog(null, "登録が完了しました！");
            dispose();
            new BookRecords().setVisible(true);
        });
    }

    private void setReviewStars(int rating) {
        for (int i = 0; i < 5; i++) {
            stars[i].setForeground(i < rating ? Color.YELLOW : Color.GRAY);
        }
        selectedReview = rating;
    }

    private void highlightStars(int rating) {
        for (int i = 0; i < 5; i++) {
            stars[i].setForeground(i < rating ? Color.YELLOW : Color.GRAY);
        }
    }

    private void saveBookRecord(String title, String author, int review, String reviewText) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("book_records.csv", true), "UTF-8"))) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("book_records.csv"), "UTF-8"));
            String lastLine = null;
            String line;
            while ((line = reader.readLine()) != null) {
                lastLine = line;
            }
            reader.close();

            int nextId = 1;
            if (lastLine != null) {
                String[] lastRecord = lastLine.split(",");
                nextId = Integer.parseInt(lastRecord[0].trim()) + 1;
            }

            String id = String.format("%08d", nextId);

            LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = currentTime.format(formatter);

            String escapedTitle = escapeCSV(title);
            String escapedAuthor = escapeCSV(author);
            String escapedReviewText = escapeCSV(reviewText);

            writer.write(String.format("%s,%s,%s,%s,%d,%s%n", 
                id, 
                formattedTime, 
                escapedTitle, 
                escapedAuthor, 
                review, 
                escapedReviewText
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String escapeCSV(String input) {
        if (input == null) {
            return "";
        }

        input = input.replace("\"", "\"\"");
        input = input.replace("\n", "\\n").replace("\r", "\\r");

        if (input.contains(",") || input.contains("\"") || input.contains("\\n") || input.contains("\\r")) {
            input = "\"" + input + "\"";
        }

        return input;
    }
}