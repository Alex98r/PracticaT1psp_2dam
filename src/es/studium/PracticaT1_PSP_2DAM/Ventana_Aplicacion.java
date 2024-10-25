package es.studium.PracticaT1_PSP_2DAM;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Ventana_Aplicacion extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField textField;
    private JTextArea textArea;
    private String[] fileLines; // Array para almacenar las líneas de texto
    private String selectedFilePath; // Variable para almacenar la ruta del archivo seleccionado

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Ventana_Aplicacion frame = new Ventana_Aplicacion();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Ventana_Aplicacion() {
        setTitle("Mis ficheros");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 462, 355);
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(panel);
        panel.setLayout(null);

        textArea = new JTextArea();
        JScrollPane scrollTextArea = new JScrollPane(textArea);
        scrollTextArea.setBounds(52, 25, 346, 194);
        panel.add(scrollTextArea);

        textField = new JTextField();
        textField.setBounds(109, 230, 232, 20);
        panel.add(textField);
        textField.setColumns(10);

        JButton btnNewButton = new JButton("Buscar");
        btnNewButton.setBounds(165, 260, 120, 20);
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String result = listar(textField.getText());
                textArea.setText(result);
                fileLines = result.split("\n"); // Guarda las líneas en el array
            }
        });
        panel.add(btnNewButton);

        textArea.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int caretPosition = textArea.getCaretPosition();
                    int lineNumber = 0;
                    try {
                        lineNumber = textArea.getLineOfOffset(caretPosition);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (lineNumber >= 0 && lineNumber < fileLines.length) {
                        selectedFilePath = fileLines[lineNumber]; // Guarda el texto de la línea seleccionada
                        if (selectedFilePath.endsWith(".exe")) {
                            try {
                                File file = new File(selectedFilePath);
                                if (file.exists()) {
                                    ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start", "\"\"", selectedFilePath);
                                    pb.start();
                                } else {
                                    System.out.println("El archivo no existe: " + selectedFilePath);
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }

    public String listar(String extension) {
        StringBuilder res = new StringBuilder();
        File[] roots = File.listRoots();

        for (File root : roots) {
            int[] count = {0}; // Contador específico para cada unidad
            buscarEnDirectorio(root, extension, res, count);
        }

        return res.toString();
    }

    private void buscarEnDirectorio(File dir, String extension, StringBuilder res, int[] count) {
        if (count[0] >= 1000) {
            return;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (count[0] >= 1000) {
                    break;
                }
                if (file.isDirectory()) {
                    buscarEnDirectorio(file, extension, res, count);
                } else if (file.getName().endsWith(extension)) {
                    res.append(file.getAbsolutePath()).append("\n");
                    count[0]++;
                }
            }
        }
    }
}
