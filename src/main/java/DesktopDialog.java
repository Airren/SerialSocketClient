import org.ecust.client.SocketClient;

import javax.swing.*;
import javax.swing.UIManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

public class DesktopDialog {
    private JTextField CommandField;
    private JButton SendButton;
    private JPanel MainPanel;
    private JTextArea InfoArea;
    private JComboBox SerialPort;
    private JComboBox BaudRate;
    private JComboBox Databit;
    private JComboBox CheckBit;
    private JComboBox StopBit;
    private JComboBox FlowControl;
    private JRadioButton ASCIIRadioButton;
    private JRadioButton HEXRadioButton;
    private JButton OpenButton;




    public DesktopDialog(final ThreadPoolExecutor pool, final String ip, final int port) {
        SerialPort.addItem("COM1");
        SerialPort.addItem("COM2");






        SendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
                String msg = CommandField.getText();
                SocketClient socketClient = new SocketClient(msg, ip, port);
                pool.execute(socketClient);
                Date adate = new Date();
                InfoArea.append(format.format(adate) + ":\n" + msg + "\n");
                CommandField.setText("");
                String receiveData = "";
                try {
                    Thread.sleep(1000);
                    Object ss = socketClient.getReceivedResult();
                    if (ss != null) {
                        receiveData = ss.toString();
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                if (!receiveData.equals("")) {
                    Date bdate = new Date();
                    InfoArea.append(format.format(bdate) + ":\n" + receiveData + "\n");
                }
            }
        });




    }


    public void setMainPanel(JPanel mainPanel) {
        MainPanel = mainPanel;
    }

    public JPanel getMainPanel() {
        return MainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
