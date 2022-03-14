import java.io.*;
import java.net.Socket;

public class FileShareClient {
    //public static int PORT = 9443;
    //public static int BUFFER_SIZE = 8 * 1024; // ik dacht 4*1024 = 4096kb te verdubbelen naar 8*1024 = 8192kb

    private static Socket sock;
    private static String fileName;
    private static BufferedReader stdin;
    private static PrintStream os;

    public static void main(String[] args) throws IOException {
        String host = args[0];
        Integer port = Integer.parseInt(args[1]);

        while (true) {
            try {
                sock = new Socket(host, port);
                stdin = new BufferedReader(new InputStreamReader(System.in));
            } catch (Exception e) {
                System.err.println("Can't connect to the server, try again later.");
                System.exit(1);
            }

            os = new PrintStream(sock.getOutputStream());

            try {
                switch (Integer.parseInt(selectAction())) {
                case 1:
                    os.println("1");
                    sendFile();
                    continue;
                case 2:
                    os.println("2");
                    System.out.println("Enter filename: ");
                    fileName = stdin.readLine();
                    os.println(fileName);
                    receiveFile(fileName);
                    continue;
                case 3:
                    os.println("3");
                    System.out.println("Enter filename to delete: ");
                    fileName = stdin.readLine();
                    os.println(fileName);
                    deleteFile(fileName);
                case 4:
                    sock.close();
                    System.exit(1);
            }
        } catch(Exception e){
            System.err.println("No valid input");
        }

    }
        }

    public static String selectAction() throws IOException {
        System.out.println("1. Send file.");
        System.out.println("2. Receive file.");
        System.out.println("3. Delete file.");
        System.out.println("4. Exit.");
        System.out.println("\nMake selection: ");

        return stdin.readLine();
}
    public static void sendFile() {
        try {
            System.out.println("Enter file name: ");
            fileName = stdin.readLine();

            File myFile = new File (fileName);
            byte[] mybytearray = new byte[(int) myFile.length()];
            if(!myFile.exists()) {
                System.out.println("File does not exist...");
                return;
                }

            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            //bis.read(mybytearray, 0, mybytearray.length);

            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);

            OutputStream os = sock.getOutputStream();

            //Sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(myFile.getName());
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            System.out.println("File "+fileName+" sent to SushiShareServer.");
        } catch (Exception e) {
            System.err.println("Exception: "+e);
        }
    }

    //Receive file from server
    public static void receiveFile(String fileName) {
        try {
            int bytesRead;
            InputStream in = sock.getInputStream();

            DataInputStream clientData = new DataInputStream(in);

            fileName = clientData.readUTF();
            OutputStream output = new FileOutputStream(fileName);
            long size = clientData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            output.close();
            in.close();

            System.out.println("File "+fileName+" received from SushiShareServer.");
        } catch (IOException ex) {
            System.out.println("Exception: "+ex);
        }
    }

    // Delete file from the server
    public static void deleteFile(String fileName) {
        try {
            deleteFile(fileName);
            System.out.println("File " + fileName + " deleted from SushiShareServer");
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }
}