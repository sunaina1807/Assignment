
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Test {    //main class of the program

    private Frame myFrame;
    private Label topLabel;
    private Label bottomLabel1;
    private Label bottomlabel2;
    private Label errorMessage;
    private Label message;
    private Panel panel1;
    private Panel panel2;


    public Test(){   //constructor of the test class
        makeGui();
    }

    public void makeGui(){    //definition of GUI function

        myFrame = new Frame("Assn");
        myFrame.setBackground(Color.CYAN);
        myFrame.setSize(600,500);
        myFrame.setLayout(new GridLayout(8,1));

        myFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){ //closes the window on pressing the cross button
                System.exit(0);
            }
        });

        topLabel = new Label();
        topLabel.setAlignment(Label.CENTER);
        message = new Label();
        message.setAlignment(Label.CENTER);
        errorMessage = new Label();
        errorMessage.setAlignment(Label.CENTER);
        bottomLabel1 = new Label();
        bottomLabel1.setAlignment(Label.CENTER);
        bottomlabel2 = new Label();
        bottomlabel2.setAlignment(Label.CENTER);

        panel1 = new Panel();
        panel1.setLayout(new FlowLayout());
        panel2 = new Panel();
        panel2.setLayout(new FlowLayout());

        myFrame.add(topLabel);
        myFrame.add(panel1);
        myFrame.add(bottomLabel1);
        myFrame.add(bottomlabel2);
        myFrame.add(message);
        myFrame.add(errorMessage);
        myFrame.add(panel2);
        myFrame.setVisible(true);
        topLabel.setText("click to browse files");
    }

    public static String checkOS() {  //check for the OS type
        String store = "";
        String OS = System.getProperty("os.name").toLowerCase();
        if(OS.indexOf("win") >= 0){
            store = "/root/Desktop/";
        } else if(OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 ){
            store = "/home/";
        } else  if(OS.indexOf("mac") >= 0){
            store = "/home/";
        } else{
            return null;
        }
        return store;
    }
    public static String getExtension(String fileName){  //return the filename selected by the user

                    String extension = "";
                int i = fileName.lastIndexOf('.');
                if (i > 0) {
                    extension = fileName.substring(i+1);// to get part of string substring() is used. Here (i+1) is the begining index of the substring.
                }
                return extension;
    }
    private void action() {

        Button button1 = new Button("BROWSE");  //creation of button1
        myFrame.add(button1);
        panel1.add(button1);
        Button button2 = new Button("SAVED FILES");
        myFrame.add(button2);
        panel2.add(button2);

        FileDialog fileDialog = new FileDialog(myFrame);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                fileDialog.setVisible(true);

                //storing path of selected file in fileName
                String fileName = fileDialog.getDirectory() + fileDialog.getFile();

                //splitting of files
                try {
                              split(fileName);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                File file = new File(fileName);
                bottomLabel1.setText("You have selected the file:-> " + fileDialog.getFile()
                        + " , from Directory :->    " + fileDialog.getDirectory() );
                bottomlabel2.setText( "The length of file is "+ file.length() + "  Bytes"
                        + " ( " + (file.length()/1024) + " kB ) and the extension (type of file) is " + getExtension(fileName));

                //Here first, the write protection of directory is checked and the the file is saved to that location
                if(file.canWrite()){
                    Path sourceFile = Paths.get(fileName);
                    Path targetFile = Paths.get(checkOS() + "CopyOf-"+fileDialog.getFile()+"." + getExtension(fileName));//accepts sitable path from checkOS function

                    try {
                        Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        errorMessage.setText("I/O Error when copying file. Check write protections.");
                    }
                }
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileDialog.setDirectory(checkOS());
                fileDialog.setVisible(true);

            }
        });
    }

    public static void split(String fileName) throws IOException {

        RandomAccessFile raf = new RandomAccessFile(fileName, "r");
        long numSplits = 10;
        long sourceSize = raf.length();
        long bytesPerSplit = sourceSize / numSplits;
        long remainingBytes = sourceSize % numSplits;
        int maxReadBufferSize = 8 * 1024; //8KB
        for (int destIx = 1; destIx <= numSplits; destIx++) {
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(checkOS() + "split." + destIx + "." + getExtension(fileName)));//getting path by checkOS()
            if (bytesPerSplit > maxReadBufferSize) {
                long numReads = bytesPerSplit / maxReadBufferSize;
                long numRemainingRead = bytesPerSplit % maxReadBufferSize;
                for (int i = 0; i < numReads; i++) {
                    readWrite(raf, bw, maxReadBufferSize);
                }
                if (numRemainingRead > 0) {
                    readWrite(raf, bw, numRemainingRead);
                }
            } else {
                readWrite(raf, bw, bytesPerSplit);
            }
            bw.close();
        }
        if (remainingBytes > 0)
        {
            //BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(checkOS() + "RemainingSplit." + (numSplits + 1)));
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(checkOS() + "RemainingSplit." + getExtension(fileName)));
            readWrite(raf, bw, remainingBytes);
            bw.close();
        }
        raf.close();
    }
    public static void readWrite(RandomAccessFile raf, BufferedOutputStream bw, long numBytes) throws IOException {
        byte[] buf = new byte[(int) numBytes];
        int val = raf.read(buf);
        if (val != -1) {
            bw.write(buf);
        }
    }
      public static void main(String[] args) throws IOException {
        Test test = new Test();   //obect of the Test class
       // awt.checkOS();
        test.action();
    }

}

