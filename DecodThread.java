import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class DecodThread extends Thread{
  private String code;
  private int range;
  private ObjectOutputStream output;
  public DecodThread(String code, int range, ObjectOutputStream output){
    this.code = code;
    this.range = range;
    this.output = output;
  }
  public void setRange(int newrange){
    range = newrange; 
  }
  public void setCode(String newcode){
    code = newcode;
  }
  public void run() {
    try {
      String mensagem = "";
      System.out.println("Codigo recebido do servidor: " + code);
      System.out.println("Faixa recebida do servidor: " + range);
      mensagem = math(code, range);
      output.writeObject(mensagem);
      output.flush();
    } catch (NoSuchAlgorithmException | IOException e) {
      e.printStackTrace();
    }
  }

  public static String math(String codigo, int faixa) throws NoSuchAlgorithmException{
    for (int i = faixa; i <= (faixa+199999); i++) {
      //Formata i com 7 casas (ex.: 0000000)
      String numero = String.format("%07d", i);
      //Calcula o MD5 desse número
      String md5 = md5(numero);

      //Verifica se o código produzido é igual ao do arquivo
      if (md5.equals(codigo)) {
        System.out.println("Match found for code");
        return numero;
      }
    }
    return "miss";
  }

  public static String md5(String entrada) throws NoSuchAlgorithmException {
    MessageDigest sha1 = MessageDigest.getInstance("MD5");
    byte[] saida = sha1.digest(entrada.getBytes());
    StringBuilder saidaStr = new StringBuilder();
    for (byte b : saida)
        saidaStr.append(String.format("%02x", b));
    return saidaStr.toString();
  }
}