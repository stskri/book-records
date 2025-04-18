package bookRecordJFrame;


import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.formdev.flatlaf.FlatLightLaf;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class EditBookRecord extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
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
    private JLabel titleCharCountLabel;
    private JLabel authorCharCountLabel;
    private JLabel thoughtsCharCountLabel;
    
    // コンストラクタ（文字列配列を受け取る）
    public EditBookRecord(String[] bookData) {
        initializeFrame();
        setupComponents(bookData);
    }

    // 新しいコンストラクタ（IDのみを受け取る）
    /**
     * @wbp.parser.constructor
     */
    public EditBookRecord(String bookId) {
    	setTitle("編集画面");
    	
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
        
        // 初期状態で全ての入力が有効であることを確認
        SwingUtilities.invokeLater(() -> {
            updateSaveButtonState();
        });
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
    private void setupComponents(String[] bookData) {
        // 戻るボタン（詳細画面に戻る）
        JButton backToListButton = new JButton("詳細画面に戻る");
        backToListButton.setForeground(new Color(200, 200, 200));
        backToListButton.setBackground(new Color(252, 252, 252));
        backToListButton.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 0));
        backToListButton.addActionListener(e -> {
            dispose();
            new ShowBookRecord(bookData[0]).setVisible(true);
        });
        backToListButton.setBounds(30, 25, 150, 40);
        contentPane.add(backToListButton);

        // IDと登録日表示
        JLabel idLabel = new JLabel("ID: " + bookData[0]);
        idLabel.setBounds(190, 30, 120, 30);
        idLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(idLabel);

        JLabel dateLabel = new JLabel("Registration Date: " + bookData[1]);
        dateLabel.setBounds(320, 30, 230, 30);
        dateLabel.setHorizontalAlignment(SwingConstants.LEFT);
        contentPane.add(dateLabel);
        
        // 削除ボタン
        JButton deleteButton = new JButton("削除");
        deleteButton.setBounds(710, 25, 150, 40);
        deleteButton.setForeground(Color.red);
        deleteButton.setBackground(Color.WHITE);  // 背景色を明示的にwhiteに設定
        deleteButton.setBorder(BorderFactory.createLineBorder(Color.red, 2));
        deleteButton.setContentAreaFilled(true);  // ボタンの塗りつぶしを有効化
        deleteButton.addActionListener(e -> confirmAndDeleteBook(bookData[0]));
        contentPane.add(deleteButton);
        
        // タイトル入力フィールド
        JLabel titleLabel = new JLabel("Book Title:");
        titleLabel.setBounds(100, 90, 75, 30);  // Y座標を90に変更
        contentPane.add(titleLabel);

        titleField = new JTextField(bookData[2]);
        titleField.setBounds(185, 90, 600, 30);  // Y座標を90に変更
        setupTextField(titleField);

        titleErrorLabel = new JLabel();
        titleErrorLabel.setBounds(185, 120, 500, 20);  // Y座標を調整
        contentPane.add(titleErrorLabel);

        // 著者入力フィールド
        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setBounds(100, 140, 75, 30);  // Y座標を140に変更
        contentPane.add(authorLabel);

        authorField = new JTextField(bookData[3]);
        authorField.setBounds(185, 140, 600, 30);  // Y座標を140に変更
        setupTextField(authorField);

        authorErrorLabel = new JLabel();
        authorErrorLabel.setBounds(185, 170, 500, 20);  // Y座標を調整
        contentPane.add(authorErrorLabel);

        // 星評価のラベル
        JLabel reviewLabel = new JLabel("Review:");
        reviewLabel.setBounds(100, 190, 75, 30);
        contentPane.add(reviewLabel);

        // 星アイコンを配置
        setupStarRating(bookData);

        // 感想入力エリア
        JLabel thoughtsTextLabel = new JLabel("Review Text:");
        thoughtsTextLabel.setBounds(100, 240, 75, 30);  // Y座標を240に変更
        contentPane.add(thoughtsTextLabel);

        thoughtsArea = new JTextArea(bookData[5]);
        thoughtsArea.setLineWrap(true);
        thoughtsArea.setWrapStyleWord(true);
        thoughtsArea.setRows(10);  // 初期行数を設定

        thoughtsScrollPane = new JScrollPane(thoughtsArea);
        thoughtsScrollPane.setBounds(185, 240, 600, 180);  // Y座標を240に変更
        thoughtsScrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 2));
        thoughtsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentPane.add(thoughtsScrollPane);

        thoughtsErrorLabel = new JLabel();
        thoughtsErrorLabel.setBounds(185, 420, 500, 20);  // Y座標を調整
        contentPane.add(thoughtsErrorLabel);
        
        // タイトル文字数カウントラベルの追加
        titleCharCountLabel = new JLabel("0/30", SwingConstants.CENTER);
        titleCharCountLabel.setBounds(735, 120, 50, 20);
        titleCharCountLabel.setForeground(Color.GRAY);
        contentPane.add(titleCharCountLabel);

        // 作者文字数カウントラベルの追加
        authorCharCountLabel = new JLabel("0/15", SwingConstants.CENTER);
        authorCharCountLabel.setBounds(735, 170, 50, 20);
        authorCharCountLabel.setForeground(Color.GRAY);
        contentPane.add(authorCharCountLabel);

        // 感想文字数カウントラベルの追加
        thoughtsCharCountLabel = new JLabel("0/400", SwingConstants.CENTER);
        thoughtsCharCountLabel.setBounds(735, 420, 50, 20);
        thoughtsCharCountLabel.setForeground(Color.GRAY);
        contentPane.add(thoughtsCharCountLabel);

        // 保存ボタン
        saveButton = new JButton("変更を保存");
        saveButton.setBounds(350, 445, 200, 40);
        saveButton.setBackground(new Color(50, 205, 50));  // Green color like NewBookRecord
        saveButton.setForeground(Color.WHITE);
        contentPane.add(saveButton);

        // バリデーションとイベントリスナーのセットアップ
        setupValidation();
        setupSaveButton(bookData);
        
        setupCharacterCountDisplay();
        
        preventBackslashInput();
        
        SwingUtilities.invokeLater(() -> {
            updateTitleCharCount();
            updateAuthorCharCount();
            updateThoughtsCharCount();
        });
    }

    // CSVからデータを読み込むメソッド
    private String[] loadBookDataById(String bookId) {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream("book_records.csv"), "Shift_JIS"))) {
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
    
    private void setupTextField(JTextField textField) {
        Border border = BorderFactory.createLineBorder(new Color(230, 230, 230), 2);
        Border margin = BorderFactory.createEmptyBorder(0, 10, 0, 10);
        textField.setBorder(BorderFactory.createCompoundBorder(border, margin));
        contentPane.add(textField);
    }
    
    private void setupStarRating(String[] bookData) {
        selectedReview = Integer.parseInt(bookData[4]); // 現在の評価を取得

        for (int i = 0; i < 5; i++) {
            stars[i] = new JLabel(i < selectedReview ? "★" : "☆");
            stars[i].setFont(new Font("SansSerif", Font.PLAIN, 30));
            stars[i].setBounds(185 + (i * 40), 185, 40, 40); // Y座標を190に変更
            stars[i].setForeground(i < selectedReview ? new Color(255, 200, 0) : Color.GRAY);
            contentPane.add(stars[i]);

            final int index = i + 1;
            stars[i].addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    selectedReview = index;
                    updateStars(selectedReview);
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
        Object[] options = {"はい", "いいえ"}; // ボタンのラベルを明示的に設定
        int confirm = JOptionPane.showOptionDialog(
            null, 
            "本当に削除してよろしいですか？", 
            "削除確認", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            options, 
            options[1] // デフォルト選択
        );

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
    
 // 空白文字のみかどうかをチェックするヘルパーメソッド
    private boolean isOnlyWhitespace(String text) {
        String processed = text.replaceAll("　", " ").trim();
        return processed.isEmpty();
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
                String title = titleField.getText();
                if (title.contains("\\")) {
                    // Remove backslashes
                    title = title.replace("\\", "");
                    titleField.setText(title);
                }
                
                // 空白文字のみのチェックを追加
                if (isOnlyWhitespace(title)) {
                    showTitleError("タイトルを入力してください");
                } else if (title.replaceAll("　", " ").trim().length() < 1 || 
                          title.length() > 30) {
                    showTitleError("タイトルは1〜30文字にしてください");
                } else {
                    clearTitleError();
                }
                updateTitleCharCount();
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
                String author = authorField.getText();
                if (author.contains("\\")) {
                    // Remove backslashes
                    author = author.replace("\\", "");
                    authorField.setText(author);
                }
                
                // 空白文字のみのチェックを追加
                if (isOnlyWhitespace(author)) {
                    showAuthorError("著者を入力してください");
                } else if (author.replaceAll("　", " ").trim().length() < 1 || 
                          author.length() > 15) {
                    showAuthorError("著者は1〜15文字にしてください");
                } else {
                    clearAuthorError();
                }
                updateAuthorCharCount();
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
                String reviewText = thoughtsArea.getText();
                if (reviewText.contains("\\")) {
                    // Remove backslashes
                    reviewText = reviewText.replace("\\", "");
                    thoughtsArea.setText(reviewText);
                }
                
                // 空白文字のみのチェックを追加
                if (isOnlyWhitespace(reviewText)) {
                    showThoughtsError("感想を入力してください");
                } else if (reviewText.replaceAll("　", " ").trim().length() < 1 || 
                          reviewText.length() > 400) {
                    showThoughtsError("感想は1〜400文字にしてください");
                } else {
                    clearThoughtsError();
                }
                updateThoughtsCharCount();
                updateSaveButtonState();
            }
        });
    }

    // 保存ボタンのセットアップ
    private void setupSaveButton(String[] bookData) {
        // 初期状態で保存ボタンを有効にする
        saveButton.setEnabled(true);
        
        // スタイルも初期状態で有効な見た目に設定
        saveButton.setForeground(new Color(0, 180, 0)); // 濃い緑色のテキスト
        saveButton.setBackground(new Color(255, 255, 255));
        saveButton.setBorder(BorderFactory.createLineBorder(new Color(0, 180, 0), 2)); // 濃い緑色のボーダー

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
                stars[i].setForeground(new Color(255, 200, 0));
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
        thoughtsScrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.RED, 0));
    }

    // 感想エラーのクリア
    private void clearThoughtsError() {
        thoughtsErrorLabel.setText("");
        thoughtsScrollPane.setViewportBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 0));
    }
    
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
        thoughtsArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateThoughtsCharCount();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateThoughtsCharCount();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateThoughtsCharCount();
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
    

private void preventBackslashInput() {
    // タイトルフィールドのキー入力制限
    titleField.addKeyListener(new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
            if (e.getKeyChar() == '\\') {
                e.consume(); // バックスラッシュの入力をブロック
            }
        }
    });

    // 作者フィールドのキー入力制限
    authorField.addKeyListener(new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
            if (e.getKeyChar() == '\\') {
                e.consume(); // バックスラッシュの入力をブロック
            }
        }
    });

    // 感想テキストエリアのキー入力制限
    thoughtsArea.addKeyListener(new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
            if (e.getKeyChar() == '\\') {
                e.consume(); // バックスラッシュの入力をブロック
            }
        }
    });
}

    // 感想の文字数カウント更新メソッド
    private void updateThoughtsCharCount() {
        int currentLength = thoughtsArea.getText().trim().length();
        thoughtsCharCountLabel.setText(currentLength + "/400");
        
        // 文字数に応じて色を変更
        if (currentLength > 400) {
            thoughtsCharCountLabel.setForeground(Color.RED);
        } else {
            thoughtsCharCountLabel.setForeground(Color.GRAY);
        }
    }

    // 保存ボタンの状態更新
    private void updateSaveButtonState() {
        boolean isTitleValid = isTitleValid();
        boolean isAuthorValid = isAuthorValid();
        boolean isThoughtsValid = isThoughtsValid();

        saveButton.setEnabled(isTitleValid && isAuthorValid && isThoughtsValid);
        
        // 入力が有効な場合、ボタンのスタイルを変更
        if (isTitleValid && isAuthorValid && isThoughtsValid) {
            saveButton.setForeground(new Color(0, 180, 0)); // 濃い緑色のテキスト
            saveButton.setBackground(new Color(255, 255, 255));
            saveButton.setBorder(BorderFactory.createLineBorder(new Color(0, 180, 0), 2)); // 濃い緑色のボーダー
        } else {
            // デフォルトのスタイルに戻す
            saveButton.setForeground(Color.WHITE);
            saveButton.setBackground(null);
            saveButton.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 2)); // グレーのボーダー
        }
    }

    private boolean isTitleValid() {
        String title = titleField.getText();
        return !isOnlyWhitespace(title) && 
               title.replaceAll("　", " ").trim().length() >= 1 && 
               title.length() <= 30;
    }

    private boolean isAuthorValid() {
        String author = authorField.getText();
        return !isOnlyWhitespace(author) && 
               author.replaceAll("　", " ").trim().length() >= 1 && 
               author.length() <= 15;
    }

    private boolean isThoughtsValid() {
        String reviewText = thoughtsArea.getText();
        return !isOnlyWhitespace(reviewText) && 
               reviewText.replaceAll("　", " ").trim().length() >= 1 && 
               reviewText.length() <= 400;
    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        
        String[] sampleData = {"1", "2025-03-24", "Java Programming", "John Doe", "4", "Great book for Java!"};
        SwingUtilities.invokeLater(() -> {
            EditBookRecord frame = new EditBookRecord(sampleData);
            frame.setVisible(true);
        });
    }
}