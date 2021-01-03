package src.swing;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {
    private JTextArea chatText;
    private JButton sendButton;
    private JButton disconnectButton;
    private JTextArea messageField;
    private JList<String> usersList;
    private DefaultListModel<String> usersListModel;
    private GridBagConstraints constraints;

    public ChatPanel(String connectionString) {
        super(new GridBagLayout());
        chatText = new JTextArea(connectionString);
        sendButton = new JButton("Send");
        disconnectButton = new JButton("Disconnect");
        messageField = new JTextArea();
        constraints = new GridBagConstraints();
        usersListModel = new DefaultListModel<>();
        usersList = new JList<>(usersListModel);

        setupGUI();
    }

    private void setupGUI() {
        setBackground(Color.ORANGE);

        //Chat Text
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridheight = 2;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(40, 40, 10, 10);
        chatText.setBackground(Color.WHITE);
        chatText.setEditable(false);
        chatText.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(chatText);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, constraints);

        //user List
        constraints.gridy = 1;
        constraints.gridx = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.insets = new Insets(0, 10, 10, 40);
        constraints.ipadx = 50;
        usersList.setLayoutOrientation(JList.VERTICAL);
        usersList.setBackground(Color.ORANGE);
        add(new JScrollPane(usersList), constraints);

        //user text to send
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.insets = new Insets(10, 40, 40, 10);
        constraints.ipady = 30;
        constraints.ipadx = 0;
        messageField.setBackground(Color.WHITE);
        messageField.setLineWrap(true);
        JScrollPane scrollPane2 = new JScrollPane(messageField);
        scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane2, constraints);

        //send button
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.ipady = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 40, 40);
        constraints.ipadx = 0;
        add(sendButton, constraints);

        //disconnect button
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.insets = new Insets(15, 15, 15, 15);
        add(disconnectButton, constraints);
    }

    public JTextArea getMessageField(){ return messageField; }
    public JTextArea getChatText(){
        return chatText;
    }
    public DefaultListModel getUsersList(){
        return usersListModel;
    }
    public JButton getSendButton(){
        return sendButton;
    }
    public JButton getDisconnectButton(){
        return disconnectButton;
    }
}




