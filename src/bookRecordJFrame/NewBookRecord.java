package bookRecordJFrame;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatLightLaf;

public class NewBookRecord extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField titleField;
    private JTextField authorField;
    private JSlider reviewSlider;
    private JLabel reviewLabel;
    private JTextArea reviewTextArea; // 感想用テキストエリア

    public static void main(String[] args) {
        // FlatLaf Look and Feelを設定
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());  // 明るいテーマ（FlatLightLaf）
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // SwingのUIを設定
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    // フレームを作成して表示
                    NewBookRecord frame = new NewBookRecord();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public NewBookRecord() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // タイトル入力フィールド
        JLabel titleLabel = new JLabel("Book Title:");
        titleLabel.setBounds(50, 50, 100, 30);
        contentPane.add(titleLabel);

        titleField = new JTextField();
        titleField.setBounds(150, 50, 200, 30);
        contentPane.add(titleField);
        titleField.setColumns(10);

        // 作者入力フィールド
        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setBounds(50, 100, 100, 30);
        contentPane.add(authorLabel);

        authorField = new JTextField();
        authorField.setBounds(150, 100, 200, 30);
        contentPane.add(authorField);
        authorField.setColumns(10);

        // レビュー入力スライダー
        JLabel reviewSliderLabel = new JLabel("Review (1-5):");
        reviewSliderLabel.setBounds(50, 150, 100, 30);
        contentPane.add(reviewSliderLabel);

        reviewSlider = new JSlider(1, 5, 1);
        reviewSlider.setBounds(150, 150, 200, 50);
        contentPane.add(reviewSlider);

        // レビュー数値表示
        reviewLabel = new JLabel("1");
        reviewLabel.setBounds(360, 150, 30, 30);
        contentPane.add(reviewLabel);

        reviewSlider.addChangeListener(e -> reviewLabel.setText(String.valueOf(reviewSlider.getValue())));

        // 感想入力エリア
        JLabel reviewTextLabel = new JLabel("Review Text:");
        reviewTextLabel.setBounds(50, 200, 100, 30);
        contentPane.add(reviewTextLabel);

        reviewTextArea = new JTextArea();
        reviewTextArea.setBounds(150, 200, 600, 100);
        contentPane.add(reviewTextArea);

        // 保存ボタン
        JButton saveButton = new JButton("Save Record");
        saveButton.setBounds(50, 350, 150, 40);
        contentPane.add(saveButton);
        
     // 戻るボタン
        JButton backButton = new JButton("Back to List");
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setForeground(Color.WHITE);
        backButton.setBounds(255, 350, 150, 40);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();  // 現在のウィンドウを閉じる
                new BookRecords().setVisible(true);  // BookRecords画面に戻る
            }
        });
        contentPane.add(backButton);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String author = authorField.getText();
                int review = reviewSlider.getValue();
                String reviewText = reviewTextArea.getText();

                // 感想が3000文字を超えた場合のチェック
                if (reviewText.length() > 3000) {
                    JOptionPane.showMessageDialog(null, "文字数制限を超過しています。3000文字以内で入力してください。");
                    return; // ここで保存を中止
                }

                if (title.length() > 0 && title.length() <= 15 && author.length() > 0 && author.length() <= 10) {
                    saveToCSV(title, author, review, reviewText);
                    JOptionPane.showMessageDialog(null, "Record saved successfully!");
                    dispose(); // Save and close NewBookRecord
                    new BookRecords().setVisible(true); // Transition to BookRecords screen
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter valid data.");
                }
            }
        });
    }

    // CSVファイルに保存するメソッド
    private void saveToCSV(String title, String author, int review, String reviewText) {
        try {
            // 現在のファイルの内容を読み込んで、IDの次の番号を決定
            int nextId = getNextId();

            // CSVファイルにデータを保存
            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("book_records.csv", true), "UTF-8"))) {
                // CSV形式でデータを保存 (ID, title, author, review, reviewText)
                bufferedWriter.write(nextId + "," + title + "," + author + "," + review + "," + reviewText);
                bufferedWriter.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving the record.");
        }
    }

    // 現在のCSVファイルに書かれているレコード数をカウントして、次のIDを取得するメソッド
    private int getNextId() {
        int nextId = 1;  // 初期IDは1に設定

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("book_records.csv"), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                nextId++;  // レコードが1行読み込まれるたびにIDをインクリメント
            }
        } catch (IOException e) {
            // ファイルが存在しない場合も想定している
            System.out.println("No previous records found, starting from ID = 1.");
        }

        return nextId;
    }
}