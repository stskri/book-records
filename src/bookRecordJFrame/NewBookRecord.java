package bookRecordJFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.formdev.flatlaf.FlatLightLaf;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

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
    private JLabel titleCharCountLabel;
    private JLabel authorCharCountLabel;
    private JLabel reviewCharCountLabel;

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
    	setTitle("新規作成画面");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 550);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 255, 255));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // 戻るボタン
        JButton backButton = new JButton("一覧画面に戻る");
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
        scrollPane.setBounds(185, 235, 600, 180);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 2));
        contentPane.add(scrollPane);

        reviewErrorLabel = new JLabel();
        reviewErrorLabel.setBounds(185, 415, 600, 20);
        contentPane.add(reviewErrorLabel);

        // 保存ボタン
        saveButton = new JButton("保存");
        saveButton.setBounds(350, 445, 200, 40);
        contentPane.add(saveButton);

        // バリデーションとイベントリスナーのセットアップ
        setupValidation();
        setupSaveButton();
        
        // タイトル文字数カウントラベルの追加
        titleCharCountLabel = new JLabel("0/30", SwingConstants.RIGHT);
        titleCharCountLabel.setBounds(735, 115, 50, 20);
        titleCharCountLabel.setForeground(Color.GRAY);
        contentPane.add(titleCharCountLabel);

        // 作者文字数カウントラベルの追加
        authorCharCountLabel = new JLabel("0/15", SwingConstants.RIGHT);
        authorCharCountLabel.setBounds(735, 165, 50, 20);
        authorCharCountLabel.setForeground(Color.GRAY);
        contentPane.add(authorCharCountLabel);

        // 感想文字数カウントラベルの追加
        reviewCharCountLabel = new JLabel("0/400", SwingConstants.RIGHT);
        reviewCharCountLabel.setBounds(735, 415, 50, 20);
        reviewCharCountLabel.setForeground(Color.GRAY);
        contentPane.add(reviewCharCountLabel);

        // 文字数カウントのセットアップ
        setupCharacterCountDisplay();
    }

    private void setupBackButton(JButton backButton) {
        backButton.setForeground(new Color(200, 200, 200));
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.setBackground(new Color(252, 252, 252));
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
            stars[i] = new JLabel("☆");
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
    
    // 文字数カウント表示のセットアップ
    private void setupCharacterCountDisplay() {
        // タイトル文字数カウント
        titleField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTitleCharCount();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTitleCharCount();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTitleCharCount();
            }
        });

        // 作者文字数カウント
        authorField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateAuthorCharCount();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateAuthorCharCount();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateAuthorCharCount();
            }
        });

        // 感想文字数カウント
        reviewTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateReviewCharCount();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateReviewCharCount();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateReviewCharCount();
            }
        });
    }

    // タイトルの文字数カウント更新メソッド
    private void updateTitleCharCount() {
        int currentLength = titleField.getText().trim().length();
        titleCharCountLabel.setText(currentLength + "/30");
        
        // 文字数に応じて色を変更
        if (currentLength > 30) {
            titleCharCountLabel.setForeground(Color.RED);
        } else {
            titleCharCountLabel.setForeground(Color.GRAY);
        }
    }

    // 作者の文字数カウント更新メソッド
    private void updateAuthorCharCount() {
        int currentLength = authorField.getText().trim().length();
        authorCharCountLabel.setText(currentLength + "/15");
        
        // 文字数に応じて色を変更
        if (currentLength > 15) {
            authorCharCountLabel.setForeground(Color.RED);
        } else {
            authorCharCountLabel.setForeground(Color.GRAY);
        }
    }

    // 感想の文字数カウント更新メソッド
    private void updateReviewCharCount() {
        int currentLength = reviewTextArea.getText().trim().length();
        reviewCharCountLabel.setText(currentLength + "/400");
        
        // 文字数に応じて色を変更
        if (currentLength > 400) {
            reviewCharCountLabel.setForeground(Color.RED);
        } else {
            reviewCharCountLabel.setForeground(Color.GRAY);
        }
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
        
        // 入力が有効な場合、ボタンのスタイルを変更
        if (isTitleValid && isAuthorValid && isReviewValid) {
        	saveButton.setForeground(new Color(0, 180, 0));
            saveButton.setBorder(BorderFactory.createLineBorder(new Color(0, 180, 0), 2)); // 濃い緑色のボーダー
        } else {
            // デフォルトのスタイルに戻す
        	saveButton.setForeground(Color.BLACK);
            saveButton.setBackground(null); // デフォルトの背景色
            saveButton.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 2)); // グレーのボーダー
        }
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
        saveButton.setBackground(new Color(245, 245, 245)); // 初期状態のグレー背景
        saveButton.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 2)); // グレーのボーダー
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

    private void setReviewStars(int count) {
        selectedReview = count;
        for (int i = 0; i < stars.length; i++) {
            if (i < count) {
                stars[i].setText("★");
                stars[i].setForeground(new Color(255, 200, 0));
            } else {
                stars[i].setText("☆");
                stars[i].setForeground(Color.GRAY);
            }
        }
    }

    private void highlightStars(int count) {
        for (int i = 0; i < stars.length; i++) {
            if (i < count) {
                stars[i].setText("★");
                stars[i].setForeground(new Color(255, 200, 0));
            } else {
                stars[i].setText("☆");
                stars[i].setForeground(Color.GRAY);
            }
        }
    }

    private void saveBookRecord(String title, String author, int review, String reviewText) {
        try {
            // 次のIDを取得
            int nextId = getNextId();
            String id = String.format("%08d", nextId);

            // 現在時刻の取得
            LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = currentTime.format(formatter);

            // CSVに書き込む文字列配列を準備
            String[] record = {
                id, 
                formattedTime, 
                title, 
                author, 
                String.valueOf(review), 
                reviewText
            };

            // CSVにレコードを追加（シフトJISを使用）
            try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream("book_records.csv", true), "MS932"))) {
                writer.writeNext(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getNextId() {
        try (CSVReader reader = new CSVReader(new FileReader("book_records.csv"))) {
            List<String[]> records = reader.readAll();
            
            // ファイルが空の場合は1を返す
            if (records.isEmpty()) {
                return 1;
            }
            
            // 最後のレコードのIDを取得して+1
            String[] lastRecord = records.get(records.size() - 1);
            return Integer.parseInt(lastRecord[0]) + 1;
        } catch (IOException | CsvException e) {
            // ファイルが存在しない、または読み取れない場合は1を返す
            return 1;
        }
    }
}