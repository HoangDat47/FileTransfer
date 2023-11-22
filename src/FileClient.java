import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class FileClient {
    public static void main(String[] args) {
        final File[] fileToSend = new File[1];

        JFrame frame = new JFrame("File Client");
        frame.setSize(450, 450);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel jlTitle = new JLabel("File Client");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel jlFile = new JLabel("File to send:");
        jlFile.setFont(new Font("Arial", Font.BOLD, 20));
        jlFile.setBorder(BorderFactory.createEmptyBorder(50, 0, 10, 0));
        jlFile.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jpFile = new JPanel();
        jpFile.setBorder(new EmptyBorder(75, 0, 10, 0));

        JButton jbSendFile = new JButton("Send File");
        jbSendFile.setPreferredSize(new Dimension(150, 75));
        jbSendFile.setFont(new Font("Arial", Font.BOLD, 20));

        JButton jbChooseFile = new JButton("Choose File");
        jbChooseFile.setPreferredSize(new Dimension(150, 75));
        jbChooseFile.setFont(new Font("Arial", Font.BOLD, 20));

        jpFile.add(jbChooseFile);
        jpFile.add(jbSendFile);

        jbChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Choose a file to send");

                if(jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    fileToSend[0] = jFileChooser.getSelectedFile();
                    jlFile.setText("The file you chose is: " + fileToSend[0].getName());
                }
            }
        });

        jbSendFile.addActionListener(e -> {
            if(fileToSend[0] == null) {
                jlFile.setText("Please choose a file to send");
            } else  {
                try {
                    FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
                    Socket socket = new Socket("localhost", 1234);

                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    String fileName = fileToSend[0].getName();
                    byte[] fileNameBytes = fileName.getBytes();

                    byte[] fileContentBytes = new byte[(int) fileToSend[0].length()];
                    fileInputStream.read(fileContentBytes);

                    dataOutputStream.writeInt(fileNameBytes.length);
                    dataOutputStream.write(fileNameBytes);

                    dataOutputStream.writeInt(fileContentBytes.length);
                    dataOutputStream.write(fileContentBytes);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        frame.add(jlTitle);
        frame.add(jlFile);
        frame.add(jpFile);
        frame.setVisible(true);

    }
}
