package morales.david.desktop.utils;

import java.io.*;
import java.net.Socket;

public class FileTransferProcessor {

    private Socket socket;
    private InputStream is;
    private FileOutputStream fos;
    private BufferedOutputStream bos;
    private int bufferSize;

    public FileTransferProcessor(Socket client) {
        socket = client;
        is = null;
        fos = null;
        bos = null;
        bufferSize = 0;
    }

    public void receiveFile(String fileName) {

        try {
            is = socket.getInputStream();
            bufferSize = socket.getReceiveBufferSize();
            fos = new FileOutputStream(fileName);
            bos = new BufferedOutputStream(fos);
            byte[] bytes = new byte[bufferSize];
            int count;
            while ((count = is.read(bytes)) >= 0) {
                bos.write(bytes, 0, count);
            }
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendFile(File file) {

        FileInputStream fis;
        BufferedInputStream bis;
        BufferedOutputStream out;
        byte[] buffer = new byte[8192];
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            out = new BufferedOutputStream(socket.getOutputStream());
            int count;
            while ((count = bis.read(buffer)) > 0) {
                out.write(buffer, 0, count);

            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}