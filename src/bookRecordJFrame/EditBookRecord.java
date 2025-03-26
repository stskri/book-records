package bookRecordJFrame;

import java.awt.Color;
import java.awt.Font;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class EditBookRecord extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField titleField;
    private JTextField authorField;
    private JTextArea thoughtsArea;
    private JLabel[] stars = new JLabel[5];
    private int selectedReview;
    private JButton saveButton;
    private JLabel titleErrorLabel;
    private JLabel authorErrorLabel;
    private JLabel thoughtsErrorLabel;
    private JScrollPane thoughtsScrollPane;

    // コンストラクタ（文字列配列を受け取る）
    public EditBookRecord(String[] bookData) {
        initializeFrame();
        setupComponents(bookData);
    }

    // 新しいコンストラクタ（IDのみを受け取る）
    public EditBookRecord(String bookId) {
        // CSVからbookIdに対応するデータを読み込む
        String[] bookData = loadBookDataById(bookId);
        
        if (bookData == null) {
            // データが見つからない場合のエラーハンドリング
            JOptionPane.showMessageDialog(null, "書籍データが見つかりませんでした。", "エラー", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        initializeFrame();
        setupComponents(bookData);
    }

    // フレームの初期設定
    private void initializeFrame() {
        setTitle("書籍情報編集");
        setBounds(100, 100, 900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    // コンポーネントのセットアップ
    private void setupComponents(String[] bookData) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 255, 255));
        getContentPane().add(panel);

        // IDと登録日表示
        JLabel idLabel = new JLabel("ID: " + bookData[0]);
        idLabel.setBounds(20, 20, 100, 30);
        panel.add(idLabel);

        JLabel dateLabel = new JLabel("登録日: " + bookData[1]);
        dateLabel.setBounds(130, 20, 200, 30);
        panel.add(dateLabel);

        // タイトル入力欄
        JLabel titleLabel = new JLabel("タイトル:");
        titleLabel.setBounds(20, 70, 100, 30);
        panel.add(titleLabel);

        titleField = new JTextField(bookData[2]);
        titleField.setBounds(120, 70, 550, 30);
        setupTextField(panel, titleField);

        titleErrorLabel = new JLabel();
        titleErrorLabel.setBounds(120, 100, 550, 20);
        titleErrorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(titleErrorLabel);

        // 著者入力欄
        JLabel authorLabel = new JLabel("著者:");
        authorLabel.setBounds(20, 120, 100, 30);
        panel.add(authorLabel);

        authorField = new JTextField(bookData[3]);
        authorField.setBounds(120, 120, 550, 30);
        setupTextField(panel, authorField);

        authorErrorLabel = new JLabel();
        authorErrorLabel.setBounds(120, 150, 550, 20);
        authorErrorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(authorErrorLabel);

        // 星評価入力欄
        setupStarRating(panel, bookData);

        // 感想入力欄
        JLabel thoughtsLabel = new JLabel("感想:");
        thoughtsLabel.setBounds(20, 220, 100, 30);
        panel.add(thoughtsLabel);

        thoughtsArea = new JTextArea(bookData[5]);
        thoughtsArea.setWrapStyleWord(true);
        thoughtsArea.setLineWrap(true);
        thoughtsScrollPane = new JScrollPane(thoughtsArea);
        thoughtsScrollPane.setBounds(120, 220, 550, 150);
        panel.add(thoughtsScrollPane);

        thoughtsErrorLabel = new JLabel();
        thoughtsErrorLabel.setBounds(120, 370, 550, 20);
        thoughtsErrorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(thoughtsErrorLabel);

        // 保存ボタン
        saveButton = new JButton("変更を保存");
        saveButton.setBounds(120, 397, 150, 40);
        saveButton.setBackground(new Color(0, 204, 0));  // 緑
        saveButton.setForeground(Color.WHITE);
        panel.add(saveButton);

        // 戻るボタン
        JButton backButton = new JButton("戻る");
        backButton.setBounds(320, 397, 150, 40);
        backButton.setBackground(new Color(100, 149, 237));  // 青
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> {
            dispose();
            new ShowBookRecord(bookData[0]).setVisible(true);
        });
        panel.add(backButton);

        // 削除ボタン
        JButton deleteButton = new JButton("書籍を削除");
        deleteButton.setBounds(520, 397, 150, 40);
        deleteButton.setBackground(new Color(255, 0, 0));  // 赤
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> confirmAndDeleteBook(bookData[0]));
        panel.add(deleteButton);

        // バリデーションとイベントリスナーのセットアップ
        setupValidation();
        setupSaveButton(bookData);
    }

    // CSVからデータを読み込むメソッド
    private String[] loadBookDataById(String bookId) {
        try (CSVReader csvReader = new CSVReader(new FileReader("book_records.csv"))) {
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                // bookIdが一致するデータを探す
                if (nextRecord[0].equals(bookId)) {
                    return nextRecord;
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // テキストフィールドのセットアップ
    private void setupTextField(JPanel panel, JTextField textField) {
        Border border = BorderFactory.createLineBorder(new Color(230, 230, 230), 2);
        Border margin = BorderFactory.createEmptyBorder(0, 10, 0, 10);
        textField.setBorder(BorderFactory.createCompoundBorder(border, margin));
        panel.add(textField);
    }

    // 星評価のセットアップ
    private void setupStarRating(JPanel panel, String[] bookData) {
        JLabel reviewLabel = new JLabel("レビュー:");
        reviewLabel.setBounds(20, 170, 150, 30);
        panel.add(reviewLabel);

        selectedReview = Integer.parseInt(bookData[4]); // 現在の評価を取得

        int starXPosition = 120;
        for (int i = 0; i < 5; i++) {
            stars[i] = new JLabel("★");
            stars[i].setFont(new Font("SansSerif", Font.PLAIN, 30));
            stars[i].setBounds(starXPosition + (i * 40), 160, 40, 40);
            stars[i].setForeground(i < selectedReview ? Color.YELLOW : Color.GRAY);
            panel.add(stars[i]);

            final int index = i + 1;

            stars[i].addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    selectedReview = index;
                    updateStars(selectedReview);
                    updateSaveButtonState();
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    updateStars(index);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    updateStars(selectedReview);
                }
            });
        }
    }

    // 書籍削除の確認
    private void confirmAndDeleteBook(String bookId) {
        int confirm = JOptionPane.showConfirmDialog(null, 
            "本当に削除してよろしいですか？", 
            "削除確認", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            CSVUtility.deleteBookFromCSV(bookId);
            JOptionPane.showMessageDialog(null, "データが削除されました!");
            dispose();
            new BookRecords().setVisible(true);
        }
    }

    // バリデーションのセットアップ
    private void setupValidation() {
        setupTitleValidation();
        setupAuthorValidation();
        setupThoughtsValidation();
    }

    // タイトルバリデーション
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

    // 著者バリデーション
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

    // 感想バリデーション
    private void setupThoughtsValidation() {
        thoughtsArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateThoughts();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateThoughts();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateThoughts();
            }

            private void validateThoughts() {
                String thoughts = thoughtsArea.getText().trim();
                if (thoughts.isEmpty()) {
                    showThoughtsError("感想を入力してください");
                } else if (thoughts.length() < 1 || thoughts.length() > 400) {
                    showThoughtsError("感想は1〜400文字にしてください");
                } else {
                    clearThoughtsError();
                }
                updateSaveButtonState();
            }
        });
    }

    // 保存ボタンのセットアップ
    private void setupSaveButton(String[] bookData) {
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> {
            // 更新後の書籍情報を配列に格納
            String[] updatedBookData = new String[6];
            updatedBookData[0] = bookData[0];  // ID
            updatedBookData[1] = bookData[1];  // 日付（変更なし）
            updatedBookData[2] = titleField.getText().trim();
            updatedBookData[3] = authorField.getText().trim();
            updatedBookData[4] = String.valueOf(selectedReview);  // 星評価を保存
            updatedBookData[5] = thoughtsArea.getText().trim();

            CSVUtility.updateBookInCSV(updatedBookData);
            JOptionPane.showMessageDialog(null, "変更が保存されました！");
            dispose();
            new ShowBookRecord(updatedBookData[0]).setVisible(true);
        });
    }

    // スター表示の更新
    private void updateStars(int review) {
        for (int i = 0; i < 5; i++) {
            if (i < review) {
                stars[i].setText("★");
                stars[i].setForeground(Color.YELLOW);
            } else {
                stars[i].setText("☆");
                stars[i].setForeground(Color.GRAY);
            }
        }
    }

    // タイトルエラーメッセージの表示
    private void showTitleError(String message) {
        titleErrorLabel.setText(message);
        titleErrorLabel.setForeground(Color.RED);
    }

    // タイトルエラーのクリア
    private void clearTitleError() {
        titleErrorLabel.setText("");
    }

    // 著者エラーメッセージの表示
    private void showAuthorError(String message) {
        authorErrorLabel.setText(message);
        authorErrorLabel.setForeground(Color.RED);
    }

    // 著者エラーのクリア
    private void clearAuthorError() {
        authorErrorLabel.setText("");
    }

    // 感想エラーメッセージの表示
    private void showThoughtsError(String message) {
        thoughtsErrorLabel.setText(message);
        thoughtsErrorLabel.setForeground(Color.RED);
        thoughtsScrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.RED, 2));
    }

    // 感想エラーのクリア
    private void clearThoughtsError() {
        thoughtsErrorLabel.setText("");
        thoughtsScrollPane.setViewportBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 2));
    }

    // 保存ボタンの状態更新
    private void updateSaveButtonState() {
        boolean isTitleValid = isTitleValid();
        boolean isAuthorValid = isAuthorValid();
        boolean isThoughtsValid = isThoughtsValid();

        saveButton.setEnabled(isTitleValid && isAuthorValid && isThoughtsValid);
    }

    // タイトルの有効性チェック
    private boolean isTitleValid() {
        String title = titleField.getText().trim();
        return title.length() >= 1 && title.length() <= 30;
    }

    // 著者の有効性チェック
    private boolean isAuthorValid() {
        String author = authorField.getText().trim();
        return author.length() >= 1 && author.length() <= 15;
    }

    // 感想の有効性チェック
    private boolean isThoughtsValid() {
        String thoughts = thoughtsArea.getText().trim();
        return thoughts.length() >= 1 && thoughts.length() <= 400;
    }

    public static void main(String[] args) {
        String[] sampleData = {"1", "2025-03-24", "Java Programming", "John Doe", "4", "Great book for Java!"};
        SwingUtilities.invokeLater(() -> {
            EditBookRecord frame = new EditBookRecord(sampleData);
            frame.setVisible(true);
        });
    }
}