import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import java.io.*;
import java.net.ServerSocket;


public class HttpServer implements Runnable{
    // Use fileinputstreams, possibly a bufferedreader
    public int port = 0;
    public static ServerSocket server;
    public static Socket client;
    public static Boolean sCreated = false;
    public PrintWriter out;

    public static void main(String[] args){
        HttpServer serv = new HttpServer();
            if (args.length != 1 && !sCreated)
            {
                System.out.println("Port set to default, 4433");
                System.out.println("Error: System expected port " +
                    "from command line, too many or few arguments");
                serv.port = 4433;
            }
            else if (!sCreated)
                serv.port = Integer.parseInt(args[0]);
            try {
                if(!sCreated)
                {
                    server = new ServerSocket(serv.port);
                    sCreated = true;
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        while(true)
        {
            try {
                client = server.accept();
            } catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
            serv.run();
        }
    }

    public void run()
    {
        BufferedReader receive;
        try {
            receive = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
            String command = receive.readLine();
            String[] splitStr = command.split(" ");
            if (splitStr[0].equals("GET"))
            {
                System.out.println("Client asks for a file");
                get(splitStr);
            }
            else if (splitStr[0].equals("HEAD"))
            {
                System.out.println("Client asks for the head");
                head(splitStr);
            }
            else if (splitStr[0].equals("OPTIONS") || splitStr[0].equals("POST") || splitStr[0].equals("PUT")
                || splitStr[0].equals("DELETE") || splitStr[0].equals("TRACE") || splitStr[0].equals("CONNECT"))
            {
                System.out.println("The command attempts to access an unsupported function");
                unsupported(splitStr);
                System.exit(-1);
            }
            else 
            {
                System.out.println("There was an issue understanding the command");
                badReq();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            out.close();
        }
        
    }

    /*
        These following methods are for printing the correct server response.
        They should contain: "HTTP/1.1 " + StatusCode + " " + Reason Phrase + "\n"
                             general header line | response-header line | entity header line + "\n"
                             "\n"
                             [Message] Use a FileInputStream
        Where StatusCode is a 3 digit number associated with the reason phrase.
            I.E. 404 Page Not Found, etc.
        And the header lines will hold information for the client. This project MUST have
            ServerName/Version
            lengthOfResource
            typeOfResource

    */
    // This method should print out everything outlines above
    // If the index has a picture referenced, the browser will search for the picture,
    // print it all below the index.html's information, and print the picture byte by byte
    public void get(String[] command) // If website processes that it needs an image, it should submit another get. Maybe make this a loop?
    {
        String fName = command[1];
        File file = new File("public_html", fName);
        Boolean held = locate(file, "public_html");
        if (held)
        {
            System.out.println("The working directory is " + System.getProperty("user.dir"));
            System.out.println("The path is: " + file.getAbsolutePath());
            try {
                String[] type = file.getName().split("\\.");
                byte[] contents = new byte[(int)file.length()];
                ByteBuffer  buf = ByteBuffer.allocate((int)file.length());
                BufferedOutputStream read = new BufferedOutputStream(client.getOutputStream());
                buf.put(contents);
                FileInputStream in = new FileInputStream(file);
                in.read(contents);
                in.close();
                String conType = "Unknown: " + type[type.length-1];
                if(type[type.length-1].equals("html") || type[type.length-1].equals("htm"))
                    conType = "text/html";
                if(type[type.length-1].equals("gif"))
                    conType = "image/gif";
                if(type[type.length-1].equals("jpeg") || type[type.length-1].equals("jpg"))
                    conType = "image/jpeg";
                if(type[type.length-1].equals("pdf"))
                    conType = "application/pdf";
                out.println();
                out.println("HTTP/1.1 200 OK \n");
                out.println("Server: JohnServer/1.0 \n");
                out.println("Content-Length: " + file.length() + " \n");
                out.println("Content-Type: " + conType +  " \n");
                read.write(contents);
                read.close();
                System.out.print("finished writing");
                
            } catch (FileNotFoundException e)
            {
                notFound(command);
            } catch (IOException e)
            {
                System.out.println(e.getMessage());
                badReq();
            }
        }
        else
        {
            System.out.println("Access denied. Reason: Requested file not held within public directory.");
            notFound(command);
        }
        out.flush();
        out.close();
    }

    public Boolean locate(File file, String dir)
    {
        File library = new File(dir);
        for (File f : library.listFiles())
        {
            System.out.println(f.getName());
            if(f.isDirectory())
                locate(file, f.getName());
            else   
                if (f.getName().equals(file.getName()));
                    return true;
        }
        return false;
    }

    // This method should print out only the header, no message
    public void head(String[] command)
    {
        System.out.println("System returns header value");
        out.print("HTTP/1.1 200 OK \n");
        out.print("Server: JohnServer/1.0 \n");
        out.print("Content-Length: N/A \n");
        out.print("Content-Type: N/A \n");
        out.flush();
        out.close();
    }
    // This method is called by the get method whenever a file isn't found in the public_html directory or its children
    public void notFound(String[] command)
    {
        System.out.println("System couldn't find file");
        out.print("HTTP/1.1 404 Not Found \n");
        out.print("Server: JohnServer/1.0 \n");
        out.print("Content-Length: N/A \n");
        out.print("Content-Type: N/A \n");
        out.flush();
        out.close();
    }
    // This method is for when the server cannot understand the command requested
    public void badReq()
    {
        System.out.println("System couldn't process request");
        out.print("HTTP/1.1 400 Bad Request \n");
        out.print("Server: JohnServer/1.0 \n");
        out.print("Content-Length: N/A \n");
        out.print("Content-Type: N/A \n");
        out.flush();
        out.close();
    }
    // This method is called whenever the user calls an unsupported command. Anything other than get or head
    public void unsupported(String[] command)
    {
        System.out.println("System couldn't perform the unimplemented/unsupported request");
        out.print("HTTP/1.1 501 Not Implemented \n");
        out.print("Server: JohnServer/1.0 \n");
        out.print("Content-Length: N/A \n");
        out.print("Content-Type: N/A \n");
        out.flush();
        out.close();
    }
}