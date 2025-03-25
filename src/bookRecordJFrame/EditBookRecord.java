package bookRecordJFrame;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

public class EditBookRecord extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField titleField;
    private JTextField authorField;
    private JTextArea thoughtsArea;
    private JLabel[] stars = new JLabel[5];
    private int selectedReview;

    public EditBookRecord(String[] bookData) {
        setTitle("書籍情報編集");
        setBounds(100, 100, 900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

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
        panel.add(titleField);

        // 著者入力欄
        JLabel authorLabel = new JLabel("著者:");
        authorLabel.setBounds(20, 120, 100, 30);
        panel.add(authorLabel);

        authorField = new JTextField(bookData[3]);
        authorField.setBounds(120, 120, 550, 30);
        panel.add(authorField);

        // 星評価入力欄
        JLabel reviewLabel = new JLabel("レビュー:");
        reviewLabel.setBounds(20, 170, 150, 30);
        panel.add(reviewLabel);

        selectedReview = Integer.parseInt(bookData[4]); // 現在の評価を取得

        // 星評価を表示するラベルの配列
        int starXPosition = 120;  // 星評価のX位置
        for (int i = 0; i < 5; i++) {
            stars[i] = new JLabel("★");
            stars[i].setFont(new Font("SansSerif", Font.PLAIN, 30));  // 星のサイズ
            stars[i].setBounds(starXPosition + (i * 40), 160, 40, 40);  // 星のX位置を変更
            stars[i].setForeground(i < selectedReview ? Color.YELLOW : Color.GRAY);
            panel.add(stars[i]);

            final int index = i + 1; // 星の番号（1〜5）

            // 星にマウスクリックイベントを追加
            stars[i].addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    // 評価を変更
                    selectedReview = index;
                    updateStars(stars, selectedReview);  // 星を更新
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    // マウスが上に乗ったときに星をハイライト
                    updateStars(stars, index);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    // マウスが外れたときに選択した評価を保持
                    updateStars(stars, selectedReview);
                }
            });
        }

        // 感想入力欄
        JLabel thoughtsLabel = new JLabel("感想:");
        thoughtsLabel.setBounds(20, 220, 100, 30);
        panel.add(thoughtsLabel);

        thoughtsArea = new JTextArea(bookData[5]);
        thoughtsArea.setWrapStyleWord(true);
        thoughtsArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(thoughtsArea);
        scrollPane.setBounds(120, 220, 550, 150);
        panel.add(scrollPane);

        // 保存ボタン
        JButton saveButton = new JButton("変更を保存");
        saveButton.setBounds(120, 397, 150, 40);
        saveButton.setBackground(new Color(0, 204, 0));  // 緑
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String author = authorField.getText();
                String thoughts = thoughtsArea.getText();
                int review = selectedReview; // 星評価

                StringBuilder errorMessage = new StringBuilder();

                // 文字数制限チェック
                if (title.isEmpty()) {
                    errorMessage.append("タイトルを入力してください。\n");
                } else if (title.length() < 1 || title.length() > 30) {
                    errorMessage.append("タイトルは1〜30文字にしてください。\n");
                }

                if (author.isEmpty()) {
                    errorMessage.append("著者を入力してください。\n");
                } else if (author.length() < 1 || author.length() > 15) {
                    errorMessage.append("著者は1〜15文字にしてください。\n");
                }

                if (thoughts.isEmpty()) {
                    errorMessage.append("感想を入力してください。\n");
                } else if (thoughts.length() < 1 || thoughts.length() > 400) {
                    errorMessage.append("感想は1〜400文字にしてください。\n");
                }

                if (errorMessage.length() > 0) {
                    // エラーメッセージをポップアップで表示
                    JOptionPane.showMessageDialog(null, errorMessage.toString(), "エラー", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 更新後の書籍情報を配列に格納
                String[] updatedBookData = new String[6];
                updatedBookData[0] = bookData[0];  // ID
                updatedBookData[1] = bookData[1];  // 日付（変更なし）
                updatedBookData[2] = authorField.getText();
                updatedBookData[3] = titleField.getText();
                updatedBookData[4] = String.valueOf(review);  // 星評価を保存
                updatedBookData[5] = thoughtsArea.getText();

                CSVUtility.updateBookInCSV(updatedBookData);
                JOptionPane.showMessageDialog(null, "変更が保存されました!");
                dispose();
                new ShowBookRecord(updatedBookData[0]).setVisible(true);
            }
        });
        panel.add(saveButton);

        // 戻るボタン
        JButton backButton = new JButton("戻る");
        backButton.setBounds(320, 397, 150, 40);
        backButton.setBackground(new Color(100, 149, 237));  // 青
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new ShowBookRecord(bookData[0]).setVisible(true);  // 同一IDの詳細ページを開く
            }
        });
        panel.add(backButton);

        // 削除ボタン
        JButton deleteButton = new JButton("書籍を削除");
        deleteButton.setBounds(520, 397, 150, 40);
        deleteButton.setBackground(new Color(255, 0, 0));  // 赤
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ユーザーに確認
                int confirm = JOptionPane.showConfirmDialog(null, 
                    "本当に削除してよろしいですか？", 
                    "削除確認", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // CSVから削除
                    CSVUtility.deleteBookFromCSV(bookData[0]);
                    JOptionPane.showMessageDialog(null, "書籍が削除されました!");

                    // 編集ウィンドウを閉じる
                    dispose();

                    // 削除後、書籍一覧画面（BookRecords）に戻る
                    new BookRecords().setVisible(true);  // BookRecordsの画面に遷移
                }
            }
        });
        panel.add(deleteButton);
    }

    // 星の色を更新するメソッド
    private void updateStars(JLabel[] stars, int review) {
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

    public static void main(String[] args) {
        String[] sampleData = {"1", "2025-03-24", "Java Programming", "John Doe", "4", "Great book for Java!"};
        SwingUtilities.invokeLater(() -> {
            EditBookRecord frame = new EditBookRecord(sampleData);
            frame.setVisible(true);
        });
    }
}