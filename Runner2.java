import java.io.IOException;
import java.security.NoSuchAlgorithmException;
public class Runner2
{
    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException
    {
        if (args.length != 1) {
            System.out.println("Use ChatRunner [server|client]");
            return;
        }

        if (args[0].equals("server"))
        {
            Server2 server = new Server2();
            server.execute();
        }
        else
        {
            Client2 client = new Client2();
            client.execute();
        }
    }
}
