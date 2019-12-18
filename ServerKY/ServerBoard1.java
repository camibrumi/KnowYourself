import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.net.ServerSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class ServerBoard1 {

    public static void main(String[] args) {

	try {
            ServerSocket s = new ServerSocket(0);
			System.out.println("listening on port: " + s.getLocalPort());
        }
        catch (IOException e) {
            System.out.println("Exception " + "Finding an open port failed: " + e.toString());
        }
/*

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get the message from the text view
                String messageText = message.getText();
                // add message to the message area
                messagesArea.append("\n" + messageText);
                // send the message to the client
                mServer.sendMessage(messageText);////////////////////////ce o sa avem nevoie ca sa trimitem inapoi la telefon mesajul
                // clear text
                message.setText("");
            }
        }); */
	
	//we start the server

        TCPServer mServer = new TCPServer(new TCPServer.OnMessageReceived() {
                    @Override
                    //this method declared in the interface from TCPServer class is implemented here
                    //this method is actually a callback method, because it will run every time when it will be called from
                    //TCPServer class (at while)
                    public void messageReceived(String message) {
						BufferedWriter bwCurrent = null;
						FileWriter fwCurrent = null;
						BufferedWriter bwFuture = null;
						FileWriter fwFuture = null;

						
						/*int varPerField = 5 * 8; // number of vars * number of fields
							String patt = "[1-9] [a-zA-Z]*";
							String copyPatt = " [-+]?[0-9]*\\.[0-9]*";
							for (int i = 0; i < varPerField; ++i) {
								patt = patt + copyPatt;			
							} */
							String[] currentAndFutureFiles = message.split("SPLIT");
							System.out.println("SPLIT in (# parti): " + currentAndFutureFiles.length);

							String[] current = currentAndFutureFiles[0].split(" final");
							String[] future = currentAndFutureFiles[1].split(" final");
							//Pattern p = Pattern.compile(patt);
							//Matcher m = p.matcher(message);

							String currentData = "";
							String futureData = "";
							//List<String> lines = new ArrayList<String>();
							//while (m.find()) {
								//System.out.println("Found: " + m.group());
							//	realData = realData + m.group() + System.getProperty("line.separator");
								//lines.add(m.group());
							//}

							for (int i = 0; i < current.length; ++i) {
								currentData = currentData + current[i] + System.getProperty("line.separator");
							}
							for (int i = 0; i < future.length; ++i) {
								futureData = futureData + future[i] + System.getProperty("line.separator");
							}
							try {


								fwCurrent = new FileWriter("sampleForModel.txt");
								bwCurrent = new BufferedWriter(fwCurrent);
								bwCurrent.write(currentData);

								fwFuture = new FileWriter("sampleForPrediction.txt");
								bwFuture = new BufferedWriter(fwFuture);
								bwFuture.write(futureData);

								System.out.println("Done");

							} catch (IOException e) {

								e.printStackTrace();

							} finally {

								try {

									if (bwCurrent != null)
										bwCurrent.close();

									if (bwFuture != null)
										bwFuture.close();

									if (fwCurrent != null)
										fwCurrent.close();

									if (fwFuture != null)
										fwFuture.close();

								} catch (IOException ex) {

									ex.printStackTrace();

								}

							}

					
                    }
	});
       	mServer.start();
    }
}
