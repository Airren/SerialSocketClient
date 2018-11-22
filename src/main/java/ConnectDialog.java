import org.ecust.client.SocketClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class ConnectDialog {
    private JPanel ConnectPanel;
    private JTextField IPTextField;
    private JTextField PortTextField;
    private JButton ConnectButton;
    private JButton ExitButton;
    private static JFrame connectFrame;

    public JPanel getConnectPanel() {
        return ConnectPanel;
    }

    public JTextField getIPTextField() {
        return IPTextField;
    }

    public JTextField getPortTextField() {
        return PortTextField;
    }

    public JButton getConnectButton() {
        return ConnectButton;
    }

    public JButton getExitButton() {
        return ExitButton;
    }



    public ConnectDialog() {
//        String lnfName="Nimbus";
//        try {
//            UIManager.setLookAndFeel(lnfName);
//            Component fName;
//            SwingUtilities.updateComponentTreeUI(ExitButton);
//        }catch (UnsupportedLookAndFeelException ex1) {
//            System.err.println("Unsupported LookAndFeel: " + lnfName);
//        }catch (ClassNotFoundException ex2) {
//            System.err.println("LookAndFeel class not found: " + lnfName);
//        }catch (InstantiationException ex3) {
//            System.err.println("Could not load LookAndFeel: " + lnfName);
//        }catch (IllegalAccessException ex4) {
//            System.err.println("Cannot use LookAndFeel: " + lnfName);}

        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(10);
        RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardOldestPolicy();

        final ThreadPoolExecutor connectpool = new ThreadPoolExecutor(5, 20, 60, TimeUnit.SECONDS, queue, handler);
        final ThreadPoolExecutor desktoppool = new ThreadPoolExecutor(100, 100, 60, TimeUnit.SECONDS, queue, handler);

         IPTextField.setText("192.168.1.118");
         PortTextField.setText("8000");


        ConnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String ip = IPTextField.getText();
                String porttext = PortTextField.getText();
                if (!ip.equals("") || !porttext.equals("")) {
                    int port = Integer.parseInt(porttext);
                    SocketClient socketClient = new SocketClient("ConnectTest", ip, port);

                    connectpool.execute(socketClient);
                    if (socketClient != null) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        String receivedData = socketClient.getReceivedResult().toString();
                        System.out.println(receivedData);

                        //if (receivedData != null && receivedData.equals("ConnectSuccess")) {

                        String[] ports = receivedData.split(",");
                        List<String> serialPorts = Arrays.asList(ports);
                        if (receivedData != null && receivedData.startsWith("COM")) {
                            //if (receivedData != null && receivedData.startsWith("COM")) {
                            JFrame desktopFrame = new JFrame("DekstopDialog");
                            desktopFrame.addWindowListener(new WindowAdapter() {
                                @Override
                                public void windowClosing(WindowEvent e) {
                                    super.windowClosing(e);
                                    desktoppool.shutdown();
                                }
                            });
                            desktopFrame.setLocation(500, 300);
                            desktopFrame.setContentPane(new DesktopDialog(desktoppool, ip, port, serialPorts).getMainPanel());
                            desktopFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            desktopFrame.pack();
                            desktopFrame.setVisible(true);
                            connectFrame.setVisible(false);
                        } else {
                            Alert dialog = new Alert("与服务端连接失败，请检查地址和端口！");
                            dialog.setLocation(500, 300);
                            dialog.pack();
                            dialog.setVisible(true);

                        }
                    }
                } else {
                    Alert dialog = new Alert("服务端地址和端口不可为空");
                    dialog.setLocation(500, 300);
                    dialog.pack();
                    dialog.setVisible(true);
                }
            }
        });

        ExitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
                connectpool.shutdown();
            }
        });
    }

    public static void main(String[] args) {

        connectFrame = new JFrame("ConnectDialog");
        connectFrame.setLocation(500, 300);
        connectFrame.setContentPane(new ConnectDialog().ConnectPanel);
        connectFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        connectFrame.pack();
        connectFrame.setVisible(true);
    }
}
