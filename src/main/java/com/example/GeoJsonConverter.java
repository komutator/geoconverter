// src/main/java/com/example/GeoJsonConverter.java
package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class GeoJsonConverter extends JFrame {

    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private GeoJsonConverterService converterService;

    public GeoJsonConverter() {
        setTitle("GeoJSON Converter");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        converterService = new GeoJsonConverterServiceImpl();

        inputTextArea = new JTextArea();
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(inputTextArea),
                new JScrollPane(outputTextArea));
        splitPane.setDividerLocation(400);
        add(splitPane, BorderLayout.CENTER);

        JButton convertButton = new JButton("Convert");
        convertButton.addActionListener(new ConvertButtonListener());

        JButton pasteButton = new JButton("Paste from Clipboard");
        pasteButton.addActionListener(new PasteButtonListener());

        JToolBar toolBar = new JToolBar();
        toolBar.add(convertButton);
        toolBar.add(pasteButton);
        add(toolBar, BorderLayout.NORTH);
    }

    private class ConvertButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String geoJsonText = inputTextArea.getText();
            String convertedText = converterService.convert(geoJsonText);
            outputTextArea.setText(convertedText);
        }
    }

    private class PasteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Получаем текст из буфера обмена
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                String clipboardText = (String) clipboard.getData(DataFlavor.stringFlavor);

                // Вставляем текст в левое окно
                inputTextArea.setText(clipboardText);
            } catch (UnsupportedFlavorException | IOException ex) {
                JOptionPane.showMessageDialog(GeoJsonConverter.this,
                    "Failed to paste from clipboard: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GeoJsonConverter app = new GeoJsonConverter();
            app.setVisible(true);
        });
    }
}
