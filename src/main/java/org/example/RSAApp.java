package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RSAApp extends JFrame {
    private final JTextField keySizeField = new JTextField("1024");
    private final JTextArea publicKeyArea = new JTextArea();
    private final JTextArea privateKeyArea = new JTextArea();
    private final JTextArea inputArea = new JTextArea();
    private final JTextArea cipherArea = new JTextArea();
    private final JLabel statusLabel = new JLabel("Готово до роботи.");

    private final RSAService rsaService = new RSAService();
    private RSAKeyPair currentKeyPair;

    public RSAApp() {
        super("RSA лабораторна робота №2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildStatusPanel(), BorderLayout.SOUTH);

        publicKeyArea.setEditable(false);
        privateKeyArea.setEditable(false);
        cipherArea.setLineWrap(true);
        inputArea.setLineWrap(true);
    }

    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel keyLabel = new JLabel("Розмір ключа (біти):");
        JButton generateButton = new JButton("Згенерувати ключі");
        generateButton.addActionListener(e -> generateKeys());

        panel.add(keyLabel);
        keySizeField.setColumns(5);
        panel.add(keySizeField);
        panel.add(generateButton);
        return panel;
    }

    private JSplitPane buildCenterPanel() {
        JPanel keysPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        keysPanel.setBorder(new EmptyBorder(10, 10, 10, 5));
        keysPanel.add(wrapWithTitledScroll(publicKeyArea, "Відкритий ключ (n, e)"));
        keysPanel.add(wrapWithTitledScroll(privateKeyArea, "Закритий ключ (n, d)"));

        JPanel actionsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        actionsPanel.setBorder(new EmptyBorder(10, 5, 10, 10));

        JPanel encryptPanel = new JPanel(new BorderLayout(5, 5));
        encryptPanel.add(wrapWithTitledScroll(inputArea, "Вхідне повідомлення"), BorderLayout.CENTER);
        JButton encryptButton = new JButton("Зашифрувати →");
        encryptButton.addActionListener(e -> encrypt());
        encryptPanel.add(encryptButton, BorderLayout.SOUTH);

        JPanel decryptPanel = new JPanel(new BorderLayout(5, 5));
        decryptPanel.add(wrapWithTitledScroll(cipherArea, "Шифртекст (Base64)"), BorderLayout.CENTER);
        JButton decryptButton = new JButton("← Розшифрувати");
        decryptButton.addActionListener(e -> decrypt());
        decryptPanel.add(decryptButton, BorderLayout.SOUTH);

        actionsPanel.add(encryptPanel);
        actionsPanel.add(decryptPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, keysPanel, actionsPanel);
        splitPane.setResizeWeight(0.45);
        return splitPane;
    }

    private JPanel buildStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(8, 10, 8, 10));
        statusLabel.setForeground(new Color(0, 128, 0));
        panel.add(statusLabel, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane wrapWithTitledScroll(JTextArea area, String title) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(BorderFactory.createTitledBorder(title));
        return scrollPane;
    }

    private void generateKeys() {
        try {
            int bitLength = Integer.parseInt(keySizeField.getText().trim());
            currentKeyPair = rsaService.generateKeyPair(bitLength);
            publicKeyArea.setText("n = \n" + currentKeyPair.modulus() + "\n\ne = \n" + currentKeyPair.publicExponent());
            privateKeyArea.setText("n = \n" + currentKeyPair.modulus() + "\n\nd = \n" + currentKeyPair.privateExponent());
            updateStatus("Нові ключі згенеровані.", true);
        } catch (Exception ex) {
            updateStatus("Помилка генерації ключів: " + ex.getMessage(), false);
        }
    }

    private void encrypt() {
        if (currentKeyPair == null) {
            updateStatus("Спочатку згенеруйте ключі.", false);
            return;
        }
        try {
            String cipherText = rsaService.encrypt(inputArea.getText(), currentKeyPair);
            cipherArea.setText(cipherText);
            updateStatus("Повідомлення зашифровано.", true);
        } catch (Exception ex) {
            updateStatus("Помилка шифрування: " + ex.getMessage(), false);
        }
    }

    private void decrypt() {
        if (currentKeyPair == null) {
            updateStatus("Спочатку згенеруйте ключі.", false);
            return;
        }
        try {
            String plainText = rsaService.decrypt(cipherArea.getText().trim(), currentKeyPair);
            inputArea.setText(plainText);
            updateStatus("Шифртекст розшифровано.", true);
        } catch (Exception ex) {
            updateStatus("Помилка розшифрування: " + ex.getMessage(), false);
        }
    }

    private void updateStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setForeground(success ? new Color(0, 128, 0) : Color.RED);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RSAApp app = new RSAApp();
            app.setVisible(true);
        });
    }
}
