package SMPC;

import com.codahale.shamir.Scheme;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
* This class should be the generic class to represent the
* secret function to be computed, i.e. function f.
**/

public class SecretFunctionService {



    /**
     * Compute() should be run by a party collecting all shares and computing on them.
     * Collects each party's share, in this case p1's, p2's, and p3's
     */
    public String compute(boolean kgc) {
       // long startTime = System.nanoTime();
// ... the code being measured ...
        final Map<Integer, byte[]> res = new HashMap();

        int n = 3;
        int k = 2;
        Scheme scheme = Scheme.of(n,k); // n shares of the secret, k required to regenerate the secret

        int counter = 0;
        String[] listOfShares = new String[n];

        // All commands that run to collect shares


        ArrayList<String> commands = new ArrayList<String>();
        if(kgc) {
            commands.add("http://localhost:8081/KGCtoP1");
            commands.add("http://localhost:8082/KGCtoP2");
            commands.add("http://localhost:8083/KGCtoP3");
        } else {
            commands.add("http://localhost:8081/VtoP1");
            commands.add("http://localhost:8082/VtoP2");
            commands.add("http://localhost:8083/VtoP3");
        }
        while(counter < n){
            try {
                String command = commands.get(counter);
                URL url = new URL(command);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                String outputStr = content.toString();
                listOfShares[counter] = outputStr;
                counter++;
            } catch(Exception e){e.printStackTrace();}
        }
        int counter2 = 0;
        while(counter2 < n){
            String string = listOfShares[counter2];
            byte[] decoded = Base64.getMimeDecoder().decode(string.getBytes());
            res.put(counter2+1, decoded);
            counter2++;
        }

        // Reconstruct the secret
        final byte[] recovered = scheme.join(res);

        //long estimatedTime = System.nanoTime() - startTime;
        //System.out.println(estimatedTime);
        return new String(recovered, StandardCharsets.UTF_8);
    }
}
