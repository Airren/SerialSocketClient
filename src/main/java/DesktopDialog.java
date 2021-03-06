import org.ecust.client.ClientHandler;
import org.ecust.client.SocketClient;

import javax.swing.*;
import javax.swing.UIManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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


    private String SerialPortName;


    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    private String encoding;


    public String getSerialPortName() {
        return SerialPortName;
    }

    public void setSerialPortName(String serialPortName) {
        SerialPortName = serialPortName;
    }


    public DesktopDialog(final ThreadPoolExecutor pool, final String ip, final int port, final List<String> serailPorts) {


        // 获得来自Server 端的串口列表信息，并更新Serialport的值
        Iterator<String> it = serailPorts.iterator();
        if (!serailPorts.isEmpty()) {
            while (it.hasNext()) {
                SerialPort.addItem(it.next().toString().trim());
            }
        } else {
            SerialPort.addItem("None Serial Ports");
        }

        ASCIIRadioButton.setSelected(true);
        if (ASCIIRadioButton.isSelected()){
            HEXRadioButton.setSelected(false);
        }

        if (HEXRadioButton.isSelected()){
            ASCIIRadioButton.setSelected(true);
        }



        // 发送按键
        SendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (OpenButton.getText().equals("关闭")) {
                    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
                    String msg = CommandField.getText();

                    // 新建Socket 连接
                    SocketClient socketClient = new SocketClient(msg, ip, port);
                    pool.execute(socketClient);

                    Date adate = new Date();
                    InfoArea.append(dateformat.format(adate) + ":\n" + msg + "\n");

                    // 发送完成后命令窗口置空
                    CommandField.setText("");

                    String receiveData = "";
                    try {
                        Thread.sleep(3000);
                        Object ss = socketClient.getReceivedResult();
                        if (ss != null) {
                            receiveData = ss.toString();
                        }
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    if (!receiveData.equals("")) {
                        Date bdate = new Date();
                        String test = ClientHandler.msgs;
                        InfoArea.append(dateformat.format(bdate) + ":\n" + ClientHandler.msgs + "\n");
                        String l = ClientHandler.msgs;
                        ClientHandler.msgs = "";
                    } else {
                        Date bdate = new Date();
                        InfoArea.append(dateformat.format(bdate) + ":\n" + "Nothing" + "\n");
                    }
                } else {
                    InfoArea.append("请选择串口参数，连接串口！！！\n");

                }
            }
        });

        OpenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (OpenButton.getText().equals("连接")) {
                    // 获取需要连接的串口的信息
                    String msg = "";
                    msg = SerialPort.getSelectedItem() + ",";
                    msg += BaudRate.getSelectedItem() + ",";
                    msg += Databit.getSelectedItem() + ",";
                    msg += StopBit.getSelectedItem() + ",";
                    msg += CheckBit.getSelectedItem() + ",";
                    msg += FlowControl.getSelectedItem() + ",";
                    SocketClient socketClient = new SocketClient(msg, ip, port);
                    pool.execute(socketClient);



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

                    if (receiveData.equals("SUCCESS")) {
                        Date bdate = new Date();
                        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
                        InfoArea.append(dateformat.format(bdate) + ":\n" + "串口连接成功" + "\n" + msg + "\n");
                        ClientHandler.msgs = "";
                        OpenButton.setText("关闭");
                    }

                } else {
                    OpenButton.setText("连接");
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
