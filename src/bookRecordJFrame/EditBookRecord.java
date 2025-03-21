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
    private JTextField reviewField;
    private JTextArea thoughtsArea;

    public EditBookRecord(String[] bookData) {
        setTitle("Edit Book Details");
        setBounds(100, 100, 900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // FlatLaf Look and Feelを設定
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());  // 明るいテーマ（FlatLightLaf）
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // メインパネルの設定
        JPanel panel = new JPanel();
        panel.setLayout(null);  // レイアウトをnullにして、自由に配置
        panel.setBackground(new Color(245, 245, 245));
        getContentPane().add(panel);

        // タイトル入力欄
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setFont(new Font("メイリオ", Font.BOLD, 16)); 
        titleLabel.setBounds(20, 20, 100, 30);
        panel.add(titleLabel);

        titleField = new JTextField(bookData[1]);  // 既存のタイトルを設定
        titleField.setBounds(120, 20, 550, 30);
        panel.add(titleField);

        // 著者入力欄
        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setFont(new Font("メイリオ", Font.BOLD, 16));
        authorLabel.setBounds(20, 70, 100, 30);
        panel.add(authorLabel);

        authorField = new JTextField(bookData[2]);  // 既存の著者名を設定
        authorField.setBounds(120, 70, 550, 30);
        panel.add(authorField);

        // レビュー入力欄
        JLabel reviewLabel = new JLabel("Review:");
        reviewLabel.setFont(new Font("メイリオ", Font.BOLD, 16));
        reviewLabel.setBounds(20, 120, 100, 30);
        panel.add(reviewLabel);

        reviewField = new JTextField(bookData[3]);  // 既存のレビューを設定
        reviewField.setBounds(120, 120, 550, 30);
        panel.add(reviewField);

        // 感想入力欄
        JLabel thoughtsLabel = new JLabel("Thoughts:");
        thoughtsLabel.setFont(new Font("メイリオ", Font.BOLD, 16)); 
        thoughtsLabel.setBounds(20, 170, 100, 30);
        panel.add(thoughtsLabel);

        thoughtsArea = new JTextArea(bookData[4]);  // 既存の感想を設定
        thoughtsArea.setWrapStyleWord(true);
        thoughtsArea.setLineWrap(true);
        thoughtsArea.setFont(new Font("メイリオ", Font.PLAIN, 14)); 
        JScrollPane scrollPane = new JScrollPane(thoughtsArea);
        scrollPane.setBounds(120, 170, 550, 150);
        panel.add(scrollPane);

        // 保存ボタン
        JButton saveButton = new JButton("Save Changes");
        saveButton.setFont(new Font("メイリオ", Font.BOLD, 14)); 
        saveButton.setBackground(new Color(34, 139, 34));  // 緑色
        saveButton.setForeground(Color.WHITE);
        saveButton.setBounds(120, 350, 150, 40);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 編集内容を取得
                String[] updatedBookData = new String[5];
                updatedBookData[0] = bookData[0];  // ID（変更なし）
                updatedBookData[1] = titleField.getText();  // タイトル
                updatedBookData[2] = authorField.getText();  // 著者
                updatedBookData[3] = reviewField.getText();  // レビュー
                updatedBookData[4] = thoughtsArea.getText();  // 感想

                // CSVファイルに保存
                CSVUtility.updateBookInCSV(updatedBookData);
                JOptionPane.showMessageDialog(null, "Changes saved successfully!");

                // 編集ウィンドウを閉じる
                dispose();
                new ShowBookRecord(updatedBookData[0]).setVisible(true);  // 詳細画面に戻る
            }
        });
        panel.add(saveButton);

        // 戻るボタン
        JButton backButton = new JButton("Back to Details");
        backButton.setFont(new Font("メイリオ", Font.BOLD, 14)); 
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setForeground(Color.WHITE);
        backButton.setBounds(300, 350, 150, 40);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();  // 編集ウィンドウを閉じる
                new ShowBookRecord(bookData[0]).setVisible(true);  // 元の詳細画面に戻る
            }
        });
        panel.add(backButton);
    }

    public static void main(String[] args) {
        // EditBookRecord画面を表示するためのダミーデータ
        String[] sampleData = {"1", "Java Programming", "John Doe", "4", "This is a great book for learning Java!"};

        SwingUtilities.invokeLater(() -> {
            EditBookRecord frame = new EditBookRecord(sampleData);
            frame.setVisible(true);
        });
    }
}