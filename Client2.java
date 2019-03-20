import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;


public class Client2 {
  public void execute() {
    try {
      Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 5000); 
      ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream()); 
      output.flush();
      ObjectInputStream input = new ObjectInputStream(socket.getInputStream() );
      String mensagem = "miss";
      output.writeObject(mensagem);
      output.flush();
      String code = ""; 
      int range = 0;
      code = (String) input.readObject();
      range = (int) input.readObject();
      boolean newcode = (boolean) input.readObject();
      DecodThread newthread = new DecodThread(code, range, output);
      newthread.run();
      while(!code.equals("finish")) {
        try {
          code = (String) input.readObject();
          range = (int) input.readObject();
          newcode = (boolean) input.readObject();
          if (newcode){
            System.out.println("killing thread");
            newthread.interrupt();
            code = (String) input.readObject();
            range = (int) input.readObject();
            newcode = (boolean) input.readObject();
            newcode = false;
          }
          newthread.setRange(range);
          newthread.setCode(code);
          if (!code.equals("ignore"))
            newthread.run();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } 
      }
      try { input.close(); } catch (IOException e) { e.printStackTrace(); }
      try { output.close(); } catch (IOException e) { e.printStackTrace(); }
      try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
      try { newthread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
    } catch (IOException | ClassNotFoundException e) { 
        e.printStackTrace(); 
    }        
  }
}