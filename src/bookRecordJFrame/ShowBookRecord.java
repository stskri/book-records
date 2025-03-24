package bookRecordJFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
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
    private Color buttonColor = new Color(70, 130, 180); // BookRecords と同じボタン色

    public ShowBookRecord(String bookId) {
        setTitle("Book Details");
        setBounds(100, 100, 900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // メインパネル - BorderLayout
        setLayout(new BorderLayout());
        
        // 本のデータをCSVから読み込む
        String[] bookData = loadBookDataById(bookId);
        if (bookData == null) {
            JPanel errorPanel = createErrorPanel();
            add(errorPanel, BorderLayout.CENTER);
            return;
        }

        // コンテンツパネル（中央に配置）
        JPanel contentPanel = createContentPanel(bookData);
        add(contentPanel, BorderLayout.CENTER);
        
        // ボタンパネル（下部に配置）
        JPanel buttonPanel = createButtonPanel(bookData);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createErrorPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel errorLabel = new JLabel("Error: Book not found.");
        errorLabel.setFont(new Font("メイリオ", Font.BOLD, 16));
        errorLabel.setForeground(Color.RED);
        panel.add(errorLabel);
        
        return panel;
    }
    
    private JPanel createContentPanel(String[] bookData) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 0);
        
        // 日付を BookRecords と同じフォーマットに変換
        String formattedDate = formatDate(bookData[1]);
        
        // 日付ラベル
        JLabel dateLabel = new JLabel("Registration Date: " + formattedDate);
        dateLabel.setFont(new Font("メイリオ", Font.PLAIN, 14));
        gbc.insets = new Insets(5, 0, 15, 0);
        panel.add(dateLabel, gbc);
        
        // タイトルラベル
        JLabel titleLabel = new JLabel(bookData[2]);
        titleLabel.setFont(new Font("メイリオ", Font.BOLD, 24));
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(titleLabel, gbc);
        
        // 著者ラベル - "by" を削除
        JLabel authorLabel = new JLabel(bookData[3]);
        authorLabel.setFont(new Font("メイリオ", Font.ITALIC, 16));
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(authorLabel, gbc);
        
        // 評価ラベル - 星表示の修正（JLabelではなくJPanelを使用）
        JPanel ratingPanel = createRatingPanel(bookData[4]);
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(ratingPanel, gbc);
        
        // 思考セクションラベル - "Reader's Notes"を"感想"に変更
        JLabel thoughtsLabel = new JLabel("感想");
        thoughtsLabel.setFont(new Font("メイリオ", Font.BOLD, 16));
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(thoughtsLabel, gbc);
        
        // 思考テキストエリア
        JTextArea thoughtsArea = new JTextArea(bookData[5]);
        thoughtsArea.setWrapStyleWord(true);
        thoughtsArea.setLineWrap(true);
        thoughtsArea.setCaretPosition(0);
        thoughtsArea.setEditable(false);
        thoughtsArea.setFont(new Font("メイリオ", Font.PLAIN, 14));
        thoughtsArea.setBackground(new Color(245, 245, 250));
        thoughtsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(thoughtsArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 220)));
        scrollPane.setPreferredSize(new Dimension(600, 200));
        
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(scrollPane, gbc);
        
        return panel;
    }
    
    // 星評価用のパネルを作成するメソッド
    private JPanel createRatingPanel(String rating) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(Color.WHITE);
        
        try {
            int reviewCount = Integer.parseInt(rating);
            
            // 星のラベルを作成
            for (int i = 0; i < 5; i++) {
                JLabel starLabel = new JLabel();
                if (i < reviewCount) {
                    // 評価分は黄色の星
                    starLabel.setText("★");
                    starLabel.setForeground(Color.YELLOW);
                } else {
                    // 残りは灰色の星
                    starLabel.setText("★");
                    starLabel.setForeground(Color.GRAY);
                }
                starLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
                panel.add(starLabel);
            }
        } catch (NumberFormatException e) {
            JLabel ratingLabel = new JLabel(rating + "/5");
            ratingLabel.setFont(new Font("メイリオ", Font.PLAIN, 16));
            panel.add(ratingLabel);
        }
        
        return panel;
    }
    
    private JPanel createButtonPanel(String[] bookData) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 0, 10, 0)
        ));
        
        // 編集ボタン
        JButton editButton = new JButton("Edit Book Record");
        editButton.setFont(new Font("メイリオ", Font.BOLD, 14));
        editButton.setBackground(buttonColor);
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.addActionListener((ActionEvent e) -> {
            dispose();
            new EditBookRecord(bookData).setVisible(true);
        });
        
        // 戻るボタン
        JButton backButton = new JButton("Back to List");
        backButton.setFont(new Font("メイリオ", Font.BOLD, 14));
        backButton.setBackground(buttonColor);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener((ActionEvent e) -> {
            dispose();
            new BookRecords().setVisible(true);
        });
        
        panel.add(editButton);
        panel.add(backButton);
        
        return panel;
    }

    private String formatDate(String dateStr) {
        try {
            // "yyyy-MM-dd HH:mm:ss" 形式を解析
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = inputFormat.parse(dateStr);

            // "yyyy年M月d日" 形式にフォーマット (BookRecords と同じ)
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年M月d日");
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr; // 変換エラーがあった場合、元の文字列をそのまま返す
        }
    }

    private String[] loadBookDataById(String bookId) {
        try (BufferedReader br = new BufferedReader(new FileReader("book_records.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 6 && data[0].equals(bookId)) {
                    return data;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            ShowBookRecord frame = new ShowBookRecord("1");
            frame.setVisible(true);
        });
    }
}