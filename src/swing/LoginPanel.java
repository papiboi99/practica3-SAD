package src.swing;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private JLabel nicknameLabel;
    private JLabel info;
    private JTextField nicknameField;
    private JButton joinButton;
    private GridBagConstraints constraints;

    public LoginPanel() {
        super(new GridBagLayout());
        setupGUI();
    }

    public void setupGUI() {

        setBackground(Color.ORANGE);

        constraints = new GridBagConstraints();

        // Info label
        info = new JLabel("Enter your nickname to join the chat");
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.SOUTHWEST;
        constraints.insets = new Insets(40, 0, 0, 40);
        constraints.gridx = 1;
        constraints.gridy = 0;
        add(info,constraints);

        //Nick Label
        nicknameLabel = new JLabel("NickName:");
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(0, 40, 0, 0);
        constraints.gridx = 0;
        constraints.gridy = 1;
        add(nicknameLabel,constraints);

        //Nick Field
        nicknameField = new JTextField();
        constraints.weightx = 1.0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 0, 0, 40);
        constraints.gridx = 1;
        constraints.gridy = 1;
        add(nicknameField,constraints);

        //Join Button
        joinButton = new JButton("Join Chat");
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.insets = new Insets(15, 0, 40, 40);
        add(joinButton,constraints);
    }

    public JTextField getNicknameField(){
        return nicknameField;
    }

    public JButton getJoinButton(){
        return joinButton;
    }
}
