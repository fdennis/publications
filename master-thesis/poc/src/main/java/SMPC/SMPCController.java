package SMPC;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class SMPCController {
    private SecretFunctionService secretFunctionService = new SecretFunctionService();
    //long startTime;
    // Each party's share. share[0] = p1, share[1] = p2, share[2] = p3.
    private static String[] shares = {"jLCIilyD", "ltSumReB", "aQFFYS52"};

    /**
     * Command from I to KGC that kick starts everything. Curls two commands.
     * 1. curl http://localhost:8084/CLC
     * to kgc for doing the CLC parts
     * 2. curl http://localhost:8085/g
     * to g if the CLC went OK.
     * @return
     */
    @RequestMapping(value = "/ItoKGC", method = RequestMethod.GET)
    public String ItoKGC() {
        long startTime = System.nanoTime();
        String res = "";
        try {
            CLC clc = new CLC();
            String ClcAnswer = CLC.run();

            if(ClcAnswer.equals("Verified")){
                String command = "http://localhost:8081/KGCtoG";

                URL url = new URL(command);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));

                String line = "";
                StringBuffer content = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    content.append(line + "\n");
                }
                String outputStr = content.toString();
                res = outputStr;
            }
        } catch (Exception e) {e.printStackTrace();}
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println(estimatedTime/1000000);
        return res;
    }

    /**
     * Command from KGC to G. Executes one curl command
     * 1. curl http://localhost:8086/GtoV
     * to order V to collect all shares
     * @return
     */
    @RequestMapping(value = "/KGCtoG", method = RequestMethod.GET)
    public String KGCtoG() {
        String res = "";
        try {
            // G sends request to V to recover the secret
            String command = "http://localhost:8082/GtoV";

            URL url = new URL(command);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String line = "";
            StringBuffer content = new StringBuffer();
            while ((line = in.readLine()) != null) {
                content.append(line + "\n");
            }
            String recoveredSecret = content.toString();

            if(recoveredSecret.equals("secret\n"))


            // G sends the recovered secret to E
            command = "http://localhost:8083/GtoE/" + recoveredSecret;
            //System.out.println("111");

            url = new URL(command);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            line = "";
            content = new StringBuffer();
            while ((line = in.readLine()) != null) {
                content.append(line + "\n");
            }
            String outputStr = content.toString();
            res = outputStr;
        } catch (Exception e) { e.printStackTrace();}
        return res;

    }

    /**
     * Command from G to V.
     * Executes the compute function in secretFunctionService to recover the secret
     * @return
     */
    @RequestMapping(value = "/GtoV", method = RequestMethod.GET)
    public String GtoV() {
        String recoveredSecret = secretFunctionService.compute(false);
        return recoveredSecret;
    }

    @RequestMapping(value = "/GtoE/{string}", method = RequestMethod.GET)
    @ResponseBody
    public String GtoE(
            @PathVariable("string") String string) {

        if(string.equals("secret")){
            return "Accepted";
        }
        return "Denied";
    }

    @RequestMapping(value = "/P1toKGC", method = RequestMethod.GET)
    public void P1toKGC() {
    }

    @RequestMapping(value = "/KGCtoP1", method = RequestMethod.GET)
    public ResponseEntity<String> p1() {
        try {
            String command = "http://localhost:8080/P1toKGC";

            URL url = new URL(command);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String line = "";
            StringBuffer content = new StringBuffer();
            while ((line = in.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch(Exception e){e.printStackTrace();}
        String res = shares[0];
        return new ResponseEntity<String>(res, HttpStatus.OK);
    }

    @RequestMapping(value = "/P2toKGC", method = RequestMethod.GET)
    public void P2toKGC() {
    }

    @RequestMapping(value = "/KGCtoP2", method = RequestMethod.GET)
    public ResponseEntity<String> p2() {
        try {
            String command = "http://localhost:8080/P2toKGC";

            URL url = new URL(command);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String line = "";
            StringBuffer content = new StringBuffer();
            while ((line = in.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch(Exception e){e.printStackTrace();}
        String res = shares[1];
        return new ResponseEntity<String>(res, HttpStatus.OK);
    }

    @RequestMapping(value = "/P3toKGC", method = RequestMethod.GET)
    public void P3toKGC() {
    }

    @RequestMapping(value = "/KGCtoP3", method = RequestMethod.GET)
    public ResponseEntity<String> p3() {
        try {
            String command = "http://localhost:8080/P3toKGC";

            URL url = new URL(command);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String line = "";
            StringBuffer content = new StringBuffer();
            while ((line = in.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch(Exception e){e.printStackTrace();}
        String res = shares[2];
        return new ResponseEntity<String>(res, HttpStatus.OK);
    }

    @RequestMapping(value = "/P1toV", method = RequestMethod.GET)
    public void P1toV() {
    }

    @RequestMapping(value = "/VtoP1", method = RequestMethod.GET)
    public ResponseEntity<String> VtoP1() {
        try {
            String command = "http://localhost:8080/P1toV";

            URL url = new URL(command);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String line = "";
            StringBuffer content = new StringBuffer();
            while ((line = in.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch(Exception e){e.printStackTrace();}
        String res = shares[0];
        return new ResponseEntity<String>(res, HttpStatus.OK);
    }
    @RequestMapping(value = "/P2toV", method = RequestMethod.GET)
    public void P2toV() {
    }

    @RequestMapping(value = "/VtoP2", method = RequestMethod.GET)
    public ResponseEntity<String> VtoP2() {
        try {
            String command = "http://localhost:8080/P2toV";

            URL url = new URL(command);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String line = "";
            StringBuffer content = new StringBuffer();
            while ((line = in.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch(Exception e){e.printStackTrace();}
        String res = shares[1];
        return new ResponseEntity<String>(res, HttpStatus.OK);
    }

    @RequestMapping(value = "/P3toV", method = RequestMethod.GET)
    public void P3toV() {
    }

    @RequestMapping(value = "/VtoP3", method = RequestMethod.GET)
    public ResponseEntity<String> VtoP3() {
        try {
            String command = "http://localhost:8080/P3toV";

            URL url = new URL(command);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String line = "";
            StringBuffer content = new StringBuffer();
            while ((line = in.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch(Exception e){e.printStackTrace();}
        String res = shares[2];
        return new ResponseEntity<String>(res, HttpStatus.OK);
    }


    // For extra parties below

    @RequestMapping(value = "/P4toKGC", method = RequestMethod.GET)
    public void P4toKGC() {
    }

    @RequestMapping(value = "/KGCtoP4", method = RequestMethod.GET)
    public ResponseEntity<String> p4() {
        try {
            String command = "http://localhost:8080/P4toKGC";

            URL url = new URL(command);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String line = "";
            StringBuffer content = new StringBuffer();
            while ((line = in.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch(Exception e){e.printStackTrace();}
        String res = shares[0];
        return new ResponseEntity<String>(res, HttpStatus.OK);
    }

    @RequestMapping(value = "/P5toKGC", method = RequestMethod.GET)
    public void P5toKGC() {
    }

    @RequestMapping(value = "/KGCtoP5", method = RequestMethod.GET)
    public ResponseEntity<String> p5() {
        try {
            String command = "http://localhost:8080/P5toKGC";

            URL url = new URL(command);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String line = "";
            StringBuffer content = new StringBuffer();
            while ((line = in.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch(Exception e){e.printStackTrace();}
        String res = shares[1];
        return new ResponseEntity<String>(res, HttpStatus.OK);
    }

    @RequestMapping(value = "/P6toKGC", method = RequestMethod.GET)
    public void P6toKGC() {
    }

    @RequestMapping(value = "/KGCtoP6", method = RequestMethod.GET)
    public ResponseEntity<String> p6() {
        try {
            String command = "http://localhost:8080/P6toKGC";

            URL url = new URL(command);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String line = "";
            StringBuffer content = new StringBuffer();
            while ((line = in.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch(Exception e){e.printStackTrace();}
        String res = shares[2];
        return new ResponseEntity<String>(res, HttpStatus.OK);
    }

    //*****************************************************************************
    //*****************************************************************************
    @RequestMapping(value = "/P4toV", method = RequestMethod.GET)
    public void P4toV() {
    }

    @RequestMapping(value = "/VtoP4", method = RequestMethod.GET)
    public ResponseEntity<String> VtoP4() {
        try {
            String command = "http://localhost:8080/P4toV";

            URL url = new URL(command);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String line = "";
            StringBuffer content = new StringBuffer();
            while ((line = in.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch(Exception e){e.printStackTrace();}
        String res = shares[0];
        return new ResponseEntity<String>(res, HttpStatus.OK);
    }
    @RequestMapping(value = "/P5toV", method = RequestMethod.GET)
    public void P5toV() {
    }

    @RequestMapping(value = "/VtoP5", method = RequestMethod.GET)
    public ResponseEntity<String> VtoP5() {
        try {
            String command = "http://localhost:8080/P5toV";

            URL url = new URL(command);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String line = "";
            StringBuffer content = new StringBuffer();
            while ((line = in.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch(Exception e){e.printStackTrace();}
        String res = shares[1];
        return new ResponseEntity<String>(res, HttpStatus.OK);
    }

    @RequestMapping(value = "/P6toV", method = RequestMethod.GET)
    public void P6toV() {
    }

    @RequestMapping(value = "/VtoP6", method = RequestMethod.GET)
    public ResponseEntity<String> VtoP6() {
        try {
            String command = "http://localhost:8080/P6toV";

            URL url = new URL(command);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String line = "";
            StringBuffer content = new StringBuffer();
            while ((line = in.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch(Exception e){e.printStackTrace();}
        String res = shares[2];
        return new ResponseEntity<String>(res, HttpStatus.OK);
    }



}
