package SMPC;


import it.unisa.dia.gas.jpbc.*;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import java.security.MessageDigest;
import java.math.BigInteger;


/**
 * A class implementing certificateless cryptography from
 * Chapter 8.4 http://www.isg.rhul.ac.uk/~kp/theses/SARthesis.pdf
 */
public class CLC {
    private static Pairing pairing;
    private static Field g1;
    private static Field zr;
    private static BigInteger q;
    private static BigInteger v;
    private static BigInteger s;
    private static BigInteger xA;
    private static Element p;
    private static Element sA;
    private static Element X_A;
    private static Element Y_A;
    private static Element p_zero;
    private static Element u;
    private static String idA;

    /**
     * Sets up variables and keys needed for signing and verifying.
     */
    public static void setup(){
        // Change path to the correct properties file in the jpbc directory.
        pairing = PairingFactory.getPairing("C:\\path\\a.properties");
        PairingFactory.getInstance().setUsePBCWhenPossible(true);

        g1 = pairing.getG1();
        zr = pairing.getZr();

        // q is copied from the a.properties file
        q = new BigInteger("8780710799663312522437781984754049815806883199414208211028653399266475630880222957078625179422662221423155858769582317459277713367317481324925129998224791");

        // set master key s
        s = zr.newRandomElement().toBigInteger().mod(q); // s in z
        // set an arbitrary generator in g_1 p
        p = g1.newRandomElement();

        // Sets public and private keys if they have not been set earlier
        setPrivateKey();
        setPublicKey();

        // MPC protocol for regenerating private and public keys
        SecretFunctionService secretFunctionService = new SecretFunctionService();
        String recoveredSecret = secretFunctionService.compute(true);
    }

    /**
     * Sets the private key for user A. (Should be done before CLC by IT department)
     */
    public static void setPrivateKey(){
        // Set A's private key sA
        idA = "1"; // A's ID
        BigInteger digest1 = new BigInteger("1");
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            md.update(idA.getBytes());
            digest1 = new BigInteger(1, md.digest());
            digest1 = digest1.mod(q);


        } catch (Exception e) {e.printStackTrace();}
        Element randFromg1 = g1.newRandomElement(); // Used to make qA a point in g_1
        Element qA = randFromg1.mul(digest1); // qA is now a point in g_1
        Element dA = qA.mul(s);
        xA = zr.newRandomElement().toBigInteger().mod(q); // A's secret value
        sA = dA.mul(xA); // A's private key
    }

    /**
     * Sets the public key for user A. (Should be done before CLC by IT department)
     */
    public static void setPublicKey(){
        // Set A's public key P_A = (X_A, Y_A)
        X_A = p.mul(xA); // X_A = xa * P
        p_zero = p.mul(s); // P_0 = s * P
        Y_A = p_zero.mul(xA); // Y_A = xa * P_0
    }

    /**
     * Signs a message
     */
    public static void sign(){


        // Step 1 and 2
        BigInteger a = zr.newRandomElement().toBigInteger().mod(q);

        Element pMultA = p.mul(a);

        Element rPair = pairing.pairing(pMultA, p);
        BigInteger r = rPair.toBigInteger().mod(q);


        // Step 3 (concatenate message with r. mod q)

        String message = "Hello";
        BigInteger biMessage = new BigInteger(message.getBytes()).mod(q);

        String mr = biMessage + "" + r;

        v = new BigInteger("1");
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            md.update(mr.getBytes());
            BigInteger digest2 = new BigInteger(1, md.digest());
            v = digest2.mod(q);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Step 4
        u = sA.mul(v).add(pMultA);
    }

    /**
     * Verifies that a message has been signed.
     */
    public static void verify(){

        // Step 1
        Element lhs = pairing.pairing(X_A, p_zero);
        Element rhs = pairing.pairing(Y_A, p);
        if(!lhs.isEqual(rhs)) {
            return;
        }

        // Step 2
        Element lhs2 = pairing.pairing(u,p);
        Y_A = Y_A.negate();

        // Set A's private key

        String idA2 = "1"; // A's ID
        BigInteger digest12 = new BigInteger("1");
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            md.update(idA.getBytes());
            digest12 = new BigInteger(1, md.digest());
            digest12 = digest12.mod(q);


        } catch (Exception e) {e.printStackTrace();}
        Element randFromg1 = g1.newRandomElement(); // Used to make qA a point in g_1
        Element qA2 = randFromg1.mul(digest12); // qA is now a point in g_1

        Element rhs2 = pairing.pairing(qA2,Y_A.mul(v)); // calc e(qA*v,-Y_A) instead of e(qA,-Y_A)^v
        BigInteger r2 = lhs2.mul(rhs2).toBigInteger().mod(q);


        // Step 3 (concatenate message2 with r2. mod q)

        String message2 = "Hello";
        BigInteger biMessage2 = new BigInteger(message2.getBytes()).mod(q);

        String mr2 = biMessage2 + "" + r2;

        BigInteger v2 = new BigInteger("1");
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            md.update(mr2.getBytes());
            BigInteger digest3 = new BigInteger(1, md.digest());
            v2 = digest3.mod(q);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs the whole CLC-algorithm
     */
    public static String run(){
        //long startTime = System.nanoTime();
// ... the code being measured ...
        setup();

        sign();
        verify();

        //long estimatedTime = System.nanoTime() - startTime;
        //System.out.println(estimatedTime/1000000);
        return "Verified";



    }
}
