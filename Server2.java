import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

public class Server2 {
  private List<String> codes;
  private List<ObjectOutputStream> clients;
  private List<String> donehashes;
  private Integer range = 0;
  private boolean newcode = false;

  public void execute() throws IOException {
    codes = Files.readAllLines(Paths.get("hashes.txt"));
    clients = new ArrayList<>();
    donehashes = new ArrayList<>();
    ServerSocket server = new ServerSocket(5000, 10);
    while(true){
      Socket socket = server.accept();
      System.out.println("Conexão nova realizada");
      serverClient(socket);
    }
  }
  public void serverClient(Socket socket) throws IOException{
    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
    output.flush();
    ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
    Thread socketThread = new Thread() {
      public void run(){
        try {
          synchronized(clients) {
            clients.add(output);
          }
          String code = "";
          while(true) {
            String mensagem = (String) input.readObject();
            System.out.println("Mensagem recebida de Cliente "+mensagem);
            if (mensagem.equals("miss")){
              synchronized (range) {
                if (range >= 100000000){
                  range = 0;
                  synchronized (codes) {
                    codes.remove(0);
                  }
                  if (codes.size() == 0){
                    for(ObjectOutputStream all:clients){
                      String finish = "finish";
                      all.writeObject(finish);
                      all.writeObject(range);
                      all.writeObject(newcode);
                      all.flush();
                      System.out.println("No match found");
                    }
                    break;
                  }
                }
              }
            } else {
                donehashes.add(mensagem);
                synchronized(range) {
                  range = 0;
                  synchronized(codes){
                    codes.remove(0);
                  }
                }
                if (codes.size() == 0) {
                  PrintWriter writer = new PrintWriter("donehashes.txt", "UTF-8");
                  for(String end : donehashes){
                    writer.println(end);
                  }
                  writer.close();
                  for(ObjectOutputStream all : clients){
                    String finish = "finish";
                    output.writeObject(finish);
                    output.writeObject(range);
                    output.writeObject(newcode);
                    output.flush();
                  }
                  System.out.println("\nFinished\n");
                  break;
                } else {
                  for (ObjectOutputStream all : clients) {
                    if(all != output){
                      newcode = true;
                      all.writeObject("ignore");
                      all.writeObject(0);
                      all.writeObject(newcode);
                      all.flush();
                    }
                  }
                  System.out.println("\nNew code\n");
                } 
              }
            newcode = false;
            if(codes.size() != 0){
              code = codes.get(0);
              System.out.println("Sending code "+code+" and range "+range);
              output.writeObject(code);
              output.writeObject(range);
              output.writeObject(newcode);
              output.flush();
              synchronized(range) {
                range += 200000;
              } 
            } else {
              break;
            }
          }
        } catch (IOException | ClassNotFoundException e) {
          e.printStackTrace();
        }
        try { input.close(); } catch (IOException e) { e.printStackTrace(); }
        try { output.close(); } catch (IOException e) { e.printStackTrace(); }
        try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
      }
    };      
    socketThread.start();
  }
}
