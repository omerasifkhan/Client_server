import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;

public class Server
{
    public static int clientCount;
    public static ServerSocket ss;

    public static void main(String[] args) throws IOException
    {
        // server is listening on port 5056
        ss = new ServerSocket( 5056);

        // running infinite loop for getting
        // client request
        Socket s = null;
        clientCount = 0;
        while (true)
        {
            try
            {
                //part 1 - Idle Server
                if (s == null){
                    System.out.println("Waiting for clients on port = " + ss.getLocalPort());
                }


                s = ss.accept();
                //part 3 -- No of Clients
                clientCount++;
                System.out.println("Active Connections = " + clientCount);

                //part 2 - Client connected
                System.out.println("Got connection from = " + s.getInetAddress() + ":" + s.getLocalPort());

                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                Thread t = new ClientHandler(s, dis, dos);

                t.start();

            }

            catch (Exception e){
                s.close();
                clientCount--;
                e.printStackTrace();
            }
        }
    }
}

// ClientHandler class
class ClientHandler extends Thread
{
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;

    // Constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos)
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run()
    {
        String received,received2;
        String toreturn;
        while (true)
        {
            try {
                System.out.println("Welcome to the server = " + Server.ss.getInetAddress() + ":" + Server.ss.getLocalPort());

                // Ask user what he wants
                dos.writeUTF("What do you want?[Save | Read]..\n"+
                        "Type Exit to terminate connection.");
                dos.flush();
                // receive the answer from client
                received = dis.readUTF();

                if(received.equals("Save")){
                    StringBuffer filename = new StringBuffer();
                    filename.append(s.getInetAddress().toString());
                    filename.deleteCharAt(0);
                    filename.append(".txt");
                    System.out.println("file name:  " + filename);
                    String file_Name = "G:\\software\\javaProjects\\17L-4324 HW-3\\src\\" + filename;
                    File file = new File(file_Name);
                    if (file.exists())
                    {
                        System.out.println("file already exists. ");
                        dos.writeUTF("Enter data you want to write: ");
                        dos.flush();

                        received = dis.readUTF();

                        try{
                            FileWriter myWriter = new FileWriter(file_Name, true);
                            myWriter.append(received.toString());
                            myWriter.close();
                            System.out.println("Information saved for client: " + s.getInetAddress().toString());

                        }
                        catch (IOException e) {
                            System.out.println("An error occurred.");
                            e.printStackTrace();
                        }

                    }
                    else
                    {
                        Boolean result = file.createNewFile();

                        if(!result)
                        {
                            System.out.println("Error while creating file !! ");
                        }

                        dos.writeUTF("Enter data you want to write: ");
                        dos.flush();

                        received2 = dis.readUTF();

                        try{
                            FileWriter myWriter = new FileWriter(file_Name);
                            myWriter.write(received2);
                            myWriter.close();
                            System.out.println("Information saved for client: " + s.getInetAddress().toString());

                        }
                        catch (IOException e) {
                            System.out.println("An error occurred.");
                            e.printStackTrace();
                        }


                    }
                }
                if(received.equals("Read")) {
                    StringBuffer filename = new StringBuffer();
                    filename.append(s.getInetAddress().toString());
                    filename.deleteCharAt(0);
                    filename.append(".txt");
                    System.out.println("file name:  " + filename);
                    String file_Name = "G:\\software\\javaProjects\\17L-4324 HW-3\\src\\" + filename;
                    File file = new File(file_Name);
                    if (file.exists()) {
                        System.out.println("Information for client : " + s.getInetAddress().toString());

                        try {
                            File myObj = new File(file_Name);
                            Scanner myReader = new Scanner(myObj);
                            while (myReader.hasNextLine()) {
                                String data = myReader.nextLine();
                                System.out.println(data);
                            }
                            myReader.close();
                        } catch (FileNotFoundException e) {
                            System.out.println("An error occurred.");
                            e.printStackTrace();
                        }

                    }
                    else {
                        System.out.println("Error file does not exists !! ");

                    }

                }

                    if(received.equals("Exit")){
                    System.out.println("Client " + this.s + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.s.close();
                    Server.clientCount--;
                    System.out.println("Connection closed");
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}