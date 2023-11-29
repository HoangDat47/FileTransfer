package chat;

import javax.swing.*;
import java.awt.*;

public class loginForm extends JFrame {
    public loginForm() {
        super("Trang đăng nhập");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container con = getContentPane();

        //main panel
        JPanel mainpanel = new JPanel();
        mainpanel.setLayout(new BorderLayout());
        mainpanel.setBackground(new Color(247,247,247));
        mainpanel.setBorder(BorderFactory.createCompoundBorder(
                mainpanel.getBorder(),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        //tiêu đề
        JPanel jpntitle = new JPanel();
        jpntitle.setBackground(new Color(247,247,247));
        JLabel pnLgTitle = new JLabel("LOGIN");
        pnLgTitle.setForeground(new Color(150,207,36));
        pnLgTitle.setFont(new Font("NewellsHand", Font.BOLD, 23));

        jpntitle.add(pnLgTitle);
        jpntitle.setBorder(BorderFactory.createCompoundBorder(
                jpntitle.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        mainpanel.add(jpntitle, BorderLayout.NORTH);
        con.add(mainpanel);

        //tk+mk
        JPanel jpnLeft = new JPanel();
        jpnLeft.setLayout(new GridLayout(4,0));
        jpnLeft.setBackground(new Color(247,247,247));

        JLabel pnCenTk = new JLabel("Tài khoản:", SwingConstants.LEFT);
        JLabel pnCenWt = new JLabel("", SwingConstants.LEFT);
        JLabel pnCenMk = new JLabel("Mật khẩu:", SwingConstants.LEFT);
        jpnLeft.add(pnCenTk);
        jpnLeft.add(pnCenWt);
        jpnLeft.add(pnCenMk);

        jpnLeft.setBorder(BorderFactory.createCompoundBorder(
                jpnLeft.getBorder(),
                BorderFactory.createEmptyBorder(8, 30, 8, 15)));
        mainpanel.add(jpnLeft, BorderLayout.WEST);

        //nhập tk+mk
        JPanel jpnCenter = new JPanel();
        jpnCenter.setLayout(new GridLayout(4,0));
        jpnCenter.setBackground(new Color(247,247,247));

        JTextField jtfTk = new JTextField(10);
        JTextField jtfWt = new JTextField(10);
        jtfWt.setVisible(false);
        JPasswordField jpkMk = new JPasswordField(10);
        //JTextField jtfMk = new JTextField(10);
        JCheckBox jcb = new JCheckBox("Đăng nhập nhanh");
        jcb.setFocusPainted(false);
        jcb.setBackground(new Color(247,247,247));

        jpnCenter.add(jtfTk);
        jpnCenter.add(jtfWt);
        jpnCenter.add(jpkMk);
        jpnCenter.add(jcb);
        jpnCenter.setBorder(BorderFactory.createCompoundBorder(
                jpnCenter.getBorder(),
                BorderFactory.createEmptyBorder(11, 15, 11, 30)));
        mainpanel.add(jpnCenter, BorderLayout.CENTER);

        //putton
        JPanel jpnBt = new JPanel();
        jpnBt.setLayout(new GridLayout(0,2));
        jpnBt.setBackground(new Color(247,247,247));

        JButton ptLogin = new JButton("Login");
        ptLogin.setFocusPainted(false);
        ptLogin.setBackground(new Color(248,248,255));
        JButton ptReset = new JButton("Reset");
        ptReset.setFocusPainted(false);
        ptReset.setBackground(new Color(248,248,255));

        jpnBt.add(ptLogin);
        jpnBt.add(ptReset);
        jpnBt.setBorder(BorderFactory.createCompoundBorder(
                jpnBt.getBorder(),
                BorderFactory.createEmptyBorder(1, 60, 9, 60)));
        mainpanel.add(jpnBt, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        loginForm lg = new loginForm();
        lg.setSize(400, 300);
        lg.setVisible(true);
    }
}
