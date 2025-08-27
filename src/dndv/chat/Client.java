package dndv.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.Locale;
import java.util.ResourceBundle;


public class Client {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton connectButton;
    private JButton settingsButton;
    private JButton sendButton;
    private JLabel statusLabel;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    
 
    private String serverAddress = "example.com";
    private int serverPort = 8190;
    private String username = "User";
    private String language = "en";
    private boolean isConnected = false;
    
 
    private final String APP_DATA_DIR;
    private final String SETTINGS_FILE;
    private final String MESSAGES_DIR;
    
  
    private ResourceBundle messages;

    public Client() {
       
        String os = System.getProperty("os.name").toLowerCase();
        String appDataPath;
        
        if (os.contains("win")) {
     
            appDataPath = System.getenv("APPDATA");
        } else if (os.contains("mac")) {
            
            appDataPath = System.getProperty("user.home") + "/Library/Application Support";
        } else {
            
            appDataPath = System.getProperty("user.home") + "/.config";
        }
        
        APP_DATA_DIR = appDataPath + "/.dndvclient";
        SETTINGS_FILE = APP_DATA_DIR + "/client_settings.properties";
        MESSAGES_DIR = APP_DATA_DIR + "/messages";
        

        createAppDataDirs();
        
       
        copyDefaultMessageFiles();
    }

    public static void main(String[] args) {
		 String osprint = System.getProperty("os.name").toLowerCase();
		 System.out.println("OS:" + osprint);
		 System.out.println("Launched!");
        SwingUtilities.invokeLater(() -> {
            try {
                new Client().createAndShowGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void createAppDataDirs() {
        File appDataDir = new File(APP_DATA_DIR);
        File messagesDir = new File(MESSAGES_DIR);
        
        if (!appDataDir.exists()) {
            if (appDataDir.mkdirs()) {
       
            } else {
       
            }
        }
        
        if (!messagesDir.exists()) {
            if (messagesDir.mkdirs()) {
              
            } else {
      
            }
        }
    }

    private void copyDefaultMessageFiles() {
        copyResourceToFile("/messages_ru.properties", MESSAGES_DIR + "/messages_ru.properties");
        copyResourceToFile("/messages_en.properties", MESSAGES_DIR + "/messages_en.properties");
    }

    private void copyResourceToFile(String resourcePath, String destinationPath) {
        File destFile = new File(destinationPath);
        if (destFile.exists()) {
            return; 
        }
        
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath);
             OutputStream outputStream = new FileOutputStream(destFile)) {
            
            if (inputStream == null) {
                
                createDefaultMessageFile(destinationPath, resourcePath.contains("_ru") ? "ru" : "en");
                return;
            }
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            
            
        } catch (IOException e) {
           
            createDefaultMessageFile(destinationPath, resourcePath.contains("_ru") ? "ru" : "en");
        }
    }

    private void createDefaultMessageFile(String filePath, String lang) {
        Properties props = new Properties();
        
        if (lang.equals("ru")) {
props.setProperty("title", "Чат");
            props.setProperty("connect", "Подключиться");
            props.setProperty("disconnect", "Отключиться");
            props.setProperty("settings", "Настройки");
            props.setProperty("send", "Отправить");
            props.setProperty("status", "Статус:");
            props.setProperty("disconnected", "Отключено");
            props.setProperty("connecting", "Подключение...");
            props.setProperty("connected", "Подключено");
            props.setProperty("error", "Ошибка");
            props.setProperty("serverAddress", "Адрес сервера");
            props.setProperty("serverPort", "Порт сервера");
            props.setProperty("username", "Имя пользователя");
            props.setProperty("save", "Сохранить");
            props.setProperty("cancel", "Отмена");
            props.setProperty("language", "Язык");
            props.setProperty("welcome", "Добро пожаловать в Чат");
            props.setProperty("currentSettings", "Текущие настройки");
            props.setProperty("server", "Сервер");
            props.setProperty("user", "Пользователь");
            props.setProperty("connectHint", "Для подключения нажмите 'Подключиться'");
            props.setProperty("settingsHint", "Для изменения настроек нажмите 'Настройки'");
            props.setProperty("connectedTo", "Подключено к серверу");
            props.setProperty("loggedInAs", "Вы вошли как");
            props.setProperty("connectionLost", "Соединение с сервером разорвано");
            props.setProperty("tryReconnect", "Попробуйте переподключиться");
            props.setProperty("disconnectedFrom", "Отключено от сервера");
            props.setProperty("settingsSaved", "Настройки сохранены");
            props.setProperty("portNumber", "Порт должен быть числом");
            props.setProperty("portRange", "Порт должен быть в диапазоне 1-65535");
            props.setProperty("usernameEmpty", "Имя пользователя не может быть пустым");
            props.setProperty("serverNotFound", "Сервер не найден");
            props.setProperty("connectionFailed", "Не удалось подключиться к серверу");
            props.setProperty("unknownError", "Неизвестная ошибка");
            props.setProperty("disconnectFirst", "Сначала отключитесь от сервера для изменения настроек");
            props.setProperty("enterMessage", "Введите сообщение и нажмите Enter");
            props.setProperty("you", "Вы");
            props.setProperty("joined", "присоединился к чату.");
            props.setProperty("left", "покинул чат.");
            props.setProperty("configDir", "Директория настроек");
            props.setProperty("configPath", "Путь к настройкам");
            props.setProperty("failedToLoad", "Не удалось загрузить настройки");
            props.setProperty("failedToSave", "Не удалось сохранить настройки");
            props.setProperty("restartToApply", "Перезапустите для применения изменений");
        } else {
props.setProperty("title", "Chat");
            props.setProperty("connect", "Connect");
            props.setProperty("disconnect", "Disconnect");
            props.setProperty("settings", "Settings");
            props.setProperty("send", "Send");
            props.setProperty("status", "Status:");
            props.setProperty("disconnected", "Disconnected");
            props.setProperty("connecting", "Connecting...");
            props.setProperty("connected", "Connected");
            props.setProperty("error", "Error");
            props.setProperty("serverAddress", "Server Address");
            props.setProperty("serverPort", "Server Port");
            props.setProperty("username", "Username");
            props.setProperty("save", "Save");
            props.setProperty("cancel", "Cancel");
            props.setProperty("language", "Language");
            props.setProperty("welcome", "Welcome to Chat");
            props.setProperty("currentSettings", "Current settings");
            props.setProperty("server", "Server");
            props.setProperty("user", "User");
            props.setProperty("connectHint", "Press 'Connect' to connect");
            props.setProperty("settingsHint", "Press 'Settings' to change settings");
            props.setProperty("connectedTo", "Connected to server");
            props.setProperty("loggedInAs", "Logged in as");
            props.setProperty("connectionLost", "Connection to server lost");
            props.setProperty("tryReconnect", "Try to reconnect");
            props.setProperty("disconnectedFrom", "Disconnected from server");
            props.setProperty("settingsSaved", "Settings saved");
            props.setProperty("portNumber", "Port must be a number");
            props.setProperty("portRange", "Port must be in range 1-65535");
            props.setProperty("usernameEmpty", "Username cannot be empty");
            props.setProperty("serverNotFound", "Server not found");
            props.setProperty("connectionFailed", "Failed to connect to server");
            props.setProperty("unknownError", "Unknown error");
            props.setProperty("disconnectFirst", "Disconnect first to change settings");
            props.setProperty("enterMessage", "Enter message and press Enter");
            props.setProperty("you", "You");
            props.setProperty("joined", "joined the chat.");
            props.setProperty("left", "left the chat.");
            props.setProperty("configDir", "Configuration directory");
            props.setProperty("configPath", "Configuration path");
            props.setProperty("failedToLoad", "Failed to load settings");
            props.setProperty("failedToSave", "Failed to save settings");
            props.setProperty("restartToApply", "Restart to apply changes");
        }
        
        try (OutputStream output = new FileOutputStream(filePath)) {
            props.store(output, "Default messages for " + lang);
            
        } catch (IOException e) {
          
        }
    }

    private void createAndShowGUI() {
     
        loadSettings();
        

        loadLanguageBundle();
        
        frame = new JFrame(messages.getString("title"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 550);
        frame.setLayout(new BorderLayout());

     
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        connectButton = new JButton(messages.getString("connect"));
        settingsButton = new JButton(messages.getString("settings"));

        
        toolBar.add(connectButton);
        toolBar.add(settingsButton);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(new JLabel(messages.getString("status")));
        statusLabel = new JLabel(messages.getString("disconnected"));
        statusLabel.setForeground(Color.RED);
        toolBar.add(statusLabel);

    
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);

       
        JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        messageField.setToolTipText(messages.getString("enterMessage"));
        
        sendButton = new JButton(messages.getString("send"));
        sendButton.setEnabled(false);

        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

       
        frame.add(toolBar, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(messagePanel, BorderLayout.SOUTH);

      
        connectButton.addActionListener(e -> {
            if (!isConnected) {
                connectToServer();
            } else {
                disconnectFromServer();
            }
        });

        settingsButton.addActionListener(e -> showSettingsDialog());
        
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

   
        chatArea.append("=== " + messages.getString("welcome") + " ===\n");
		chatArea.append("Support, Поддержка: dndv_dev@hotmail.com");


        frame.setVisible(true);
    }

    private void showSettingsDialog() {
        if (isConnected) {
            JOptionPane.showMessageDialog(frame, 
                messages.getString("disconnectFirst"), 
                messages.getString("error"), 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog settingsDialog = new JDialog(frame, messages.getString("settings"), true);
        settingsDialog.setSize(400, 300);
        settingsDialog.setLayout(new GridLayout(6, 2, 10, 10));
        settingsDialog.setLocationRelativeTo(frame);

        JLabel serverLabel = new JLabel(messages.getString("serverAddress"));
        JTextField serverField = new JTextField(serverAddress);
        
        JLabel portLabel = new JLabel(messages.getString("serverPort"));
        JTextField portField = new JTextField(String.valueOf(serverPort));
        
        JLabel userLabel = new JLabel(messages.getString("username"));
        JTextField userField = new JTextField(username);
        
		
        JLabel langLabel = new JLabel(messages.getString("language"));
        JComboBox<String> langCombo = new JComboBox<>(new String[]{"Русский", "English"});
        langCombo.setSelectedIndex(language.equals("ru") ? 0 : 1);
        
        JButton saveButton = new JButton(messages.getString("save"));
        JButton cancelButton = new JButton(messages.getString("cancel"));

        settingsDialog.add(serverLabel);
        settingsDialog.add(serverField);
        settingsDialog.add(portLabel);
        settingsDialog.add(portField);
        settingsDialog.add(userLabel);
        settingsDialog.add(userField);
        settingsDialog.add(langLabel);
        settingsDialog.add(langCombo);
        settingsDialog.add(new JLabel());
        settingsDialog.add(new JLabel());
        settingsDialog.add(saveButton);
        settingsDialog.add(cancelButton);
		

        saveButton.addActionListener(e -> {
            try {
                String newServerAddress = serverField.getText().trim();
                int newServerPort = Integer.parseInt(portField.getText().trim());
                String newUsername = userField.getText().trim();
                String newLanguage = langCombo.getSelectedIndex() == 0 ? "ru" : "en";
                
                if (newUsername.isEmpty()) {
                    JOptionPane.showMessageDialog(settingsDialog, 
                        messages.getString("usernameEmpty"), 
                        messages.getString("error"), 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (newServerPort <= 0 || newServerPort > 65535) {
                    JOptionPane.showMessageDialog(settingsDialog, 
                        messages.getString("portRange"), 
                        messages.getString("error"), 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                serverAddress = newServerAddress;
                serverPort = newServerPort;
                username = newUsername;
                
                if (!language.equals(newLanguage)) {
                    language = newLanguage;
                    loadLanguageBundle();
                    updateUIAfterLanguageChange();
                }
                
                saveSettings();
                settingsDialog.dispose();
                
                chatArea.append(messages.getString("settingsSaved") + ":\n");
                chatArea.append("• " + messages.getString("server") + ": " + serverAddress + ":" + serverPort + "\n");
                chatArea.append("• " + messages.getString("user") + ": " + username + "\n");
                chatArea.append("• " + messages.getString("language") + ": " + 
                    (language.equals("ru") ? "Русский" : "English") + "\n\n");
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(settingsDialog, 
                    messages.getString("portNumber"), 
                    messages.getString("error"), 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> settingsDialog.dispose());

        settingsDialog.setVisible(true);
    }

    private void updateUIAfterLanguageChange() {
        frame.setTitle(messages.getString("title"));
        connectButton.setText(isConnected ? messages.getString("disconnect") : messages.getString("connect"));
        settingsButton.setText(messages.getString("settings"));
        sendButton.setText(messages.getString("send"));
        statusLabel.setText(isConnected ? messages.getString("connected") : messages.getString("disconnected"));
        messageField.setToolTipText(messages.getString("enterMessage"));
    }

    private void connectToServer() {
        try {
            updateStatus(messages.getString("connecting"), Color.ORANGE);
            
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            
            out.println(username);
            
            isConnected = true;
            connectButton.setText(messages.getString("disconnect"));
            settingsButton.setEnabled(false);
            sendButton.setEnabled(true);
            messageField.requestFocus();
            
            
            new Thread(this::readMessages).start();
            
            updateStatus(messages.getString("connected"), Color.GREEN);
            chatArea.append("? " + messages.getString("connectedTo") + " " + serverAddress + ":" + serverPort + "\n");
            chatArea.append(messages.getString("loggedInAs") + ": " + username + "\n\n");
            
        } catch (UnknownHostException e) {
            updateStatus(messages.getString("error"), Color.RED);
            JOptionPane.showMessageDialog(frame, 
                messages.getString("serverNotFound") + ": " + e.getMessage(), 
                messages.getString("error"), 
                JOptionPane.ERROR_MESSAGE);
        } catch (ConnectException e) {
            updateStatus(messages.getString("error"), Color.RED);
            JOptionPane.showMessageDialog(frame, 
                messages.getString("connectionFailed"), 
                messages.getString("error"), 
                JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            updateStatus(messages.getString("error"), Color.RED);
            JOptionPane.showMessageDialog(frame, 
                messages.getString("connectionFailed") + ": " + e.getMessage(), 
                messages.getString("error"), 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            updateStatus(messages.getString("error"), Color.RED);
            JOptionPane.showMessageDialog(frame, 
                messages.getString("unknownError") + ": " + e.getMessage(), 
                messages.getString("error"), 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void disconnectFromServer() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при отключении: " + e.getMessage());
        }
        
        isConnected = false;
        connectButton.setText(messages.getString("connect"));
        settingsButton.setEnabled(true);
        sendButton.setEnabled(false);
        
        updateStatus(messages.getString("disconnected"), Color.RED);
        chatArea.append("? " + messages.getString("disconnectedFrom") + "\n\n");
    }

    private void updateStatus(String text, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(text);
            statusLabel.setForeground(color);
        });
    }

    private void readMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                final String finalMessage = message;
                SwingUtilities.invokeLater(() -> {
                    chatArea.append(finalMessage + "\n");
                    chatArea.setCaretPosition(chatArea.getDocument().getLength());
                });
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                chatArea.append("\n? " + messages.getString("connectionLost") + "\n");
                chatArea.append(messages.getString("tryReconnect") + "\n\n");
                disconnectFromServer();
            });
        }
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && out != null) {
            out.println(message);
            chatArea.append("[" + messages.getString("you") + "]: " + message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            messageField.setText("");
        }
    }

    private void loadLanguageBundle() {
        try {
           
            File messagesDir = new File(MESSAGES_DIR);
            File messagesFile = new File(messagesDir, "messages_" + language + ".properties");
            
            if (messagesFile.exists()) {
                Properties props = new Properties();
                try (InputStream input = new FileInputStream(messagesFile)) {
                    props.load(input);
                    messages = new ResourceBundle() {
                        @Override
                        protected Object handleGetObject(String key) {
                            return props.getProperty(key);
                        }
                        @Override
                        public java.util.Enumeration<String> getKeys() {
                            return java.util.Collections.enumeration(props.stringPropertyNames());
                        }
                    };
                }
            } else {
           
                messages = ResourceBundle.getBundle("messages", new Locale(language));
            }
        } catch (Exception e) {
           
            try {
                messages = ResourceBundle.getBundle("messages", new Locale("ru"));
            } catch (Exception ex) {
            
                messages = new ResourceBundle() {
                    private java.util.Map<String, String> fallback = java.util.Map.of(
                        "title", "Simple Messenger",
                        "connect", "Connect",
                        "disconnect", "Disconnect",
                        "settings", "Settings",
                        "send", "Send",
                        "status", "Status:"
                    );
                    @Override
                    protected Object handleGetObject(String key) {
                        return fallback.getOrDefault(key, key);
                    }
                    @Override
                    public java.util.Enumeration<String> getKeys() {
                        return java.util.Collections.enumeration(fallback.keySet());
                    }
                };
            }
        }
    }

    private void loadSettings() {
        Properties props = new Properties();
        File settingsFile = new File(SETTINGS_FILE);
        
        if (!settingsFile.exists()) {
   
            saveSettings();
            return;
        }
        
        try (InputStream input = new FileInputStream(SETTINGS_FILE)) {
            props.load(input);
            serverAddress = props.getProperty("server.address", "localhost");
            serverPort = Integer.parseInt(props.getProperty("server.port", "12345"));
            username = props.getProperty("username", "User");
            language = props.getProperty("language", "ru");
        } catch (IOException | NumberFormatException e) {
          
        }
    }

    private void saveSettings() {
        Properties props = new Properties();
        props.setProperty("server.address", serverAddress);
        props.setProperty("server.port", String.valueOf(serverPort));
        props.setProperty("username", username);
        props.setProperty("language", language);
        
        try (OutputStream output = new FileOutputStream(SETTINGS_FILE)) {
            props.store(output, "Client Settings");
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, 
                "Не удалось сохранить настройки: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}