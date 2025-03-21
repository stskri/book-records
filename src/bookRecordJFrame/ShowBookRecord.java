package bookRecordJFrame;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatLightLaf;

public class ShowBookRecord extends JFrame {

    private static final long serialVersionUID = 1L;

    public ShowBookRecord(String bookId) {
        setTitle("Book Details");
        setBounds(100, 100, 900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());  // 明るいテーマ（FlatLightLaf）
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(245, 245, 245));
        getContentPane().add(panel);

        // 本のデータをCSVから読み込む
        String[] bookData = loadBookDataById(bookId);
        if (bookData == null) {
            JLabel errorLabel = new JLabel("Error: Book not found.");
            errorLabel.setFont(new Font("メイリオ", Font.BOLD, 16));
            errorLabel.setBounds(20, 20, 550, 30);
            panel.add(errorLabel);
            return;
        }

        // データをラベルとして表示
        JLabel idLabel = new JLabel("ID: " + bookData[0]);
        idLabel.setFont(new Font("メイリオ", Font.BOLD, 16));
        idLabel.setBounds(20, 20, 550, 30);
        panel.add(idLabel);

        JLabel titleLabel = new JLabel("Title: " + bookData[1]);
        titleLabel.setFont(new Font("メイリオ", Font.BOLD, 20));
        titleLabel.setBounds(20, 70, 550, 30);
        panel.add(titleLabel);

        JLabel authorLabel = new JLabel("Author: " + bookData[2]);
        authorLabel.setFont(new Font("メイリオ", Font.PLAIN, 16));
        authorLabel.setBounds(20, 110, 550, 25);
        panel.add(authorLabel);

        JLabel reviewLabel = new JLabel("Review: " + bookData[3] + "/5");
        reviewLabel.setFont(new Font("メイリオ", Font.PLAIN, 16));
        reviewLabel.setBounds(20, 150, 550, 25);
        panel.add(reviewLabel);

        JLabel thoughtsLabel = new JLabel("Thoughts:");
        thoughtsLabel.setFont(new Font("メイリオ", Font.BOLD, 16));
        thoughtsLabel.setBounds(20, 190, 550, 25);
        panel.add(thoughtsLabel);

        JTextArea thoughtsArea = new JTextArea();
        thoughtsArea.setText(bookData[4]);
        thoughtsArea.setWrapStyleWord(true);
        thoughtsArea.setLineWrap(true);
        thoughtsArea.setCaretPosition(0);
        thoughtsArea.setEditable(false);
        thoughtsArea.setFont(new Font("メイリオ", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(thoughtsArea);
        scrollPane.setBounds(20, 220, 550, 150);
        panel.add(scrollPane);

        // 編集ボタン
        JButton editButton = new JButton("Edit Book Record");
        editButton.setFont(new Font("メイリオ", Font.BOLD, 14));
        editButton.setBackground(new Color(70, 130, 180));
        editButton.setForeground(Color.WHITE);
        editButton.setBounds(20, 380, 200, 40);
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();  // 現在の画面を閉じる
                new EditBookRecord(bookData).setVisible(true);  // EditBookRecord画面を開く
            }
        });
        panel.add(editButton);

        JButton backButton = new JButton("Back to List");
        backButton.setFont(new Font("メイリオ", Font.BOLD, 14));
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setForeground(Color.WHITE);
        backButton.setBounds(240, 380, 150, 40);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();  // 現在のウィンドウを閉じる
                new BookRecords().setVisible(true);  // BookRecords画面に戻る
            }
        });
        panel.add(backButton);
    }

    private String[] loadBookDataById(String bookId) {
        try (BufferedReader br = new BufferedReader(new FileReader("book_records.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5 && data[0].equals(bookId)) {
                    return data;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String bookId = "1";  // ダミーID
        SwingUtilities.invokeLater(() -> {
            ShowBookRecord frame = new ShowBookRecord(bookId);
            frame.setVisible(true);
        });
    }
}