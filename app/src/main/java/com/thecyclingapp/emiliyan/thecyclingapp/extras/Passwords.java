package com.thecyclingapp.emiliyan.thecyclingapp.extras;


import com.google.api.client.util.Base64;

import org.jasypt.salt.RandomSaltGenerator;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;

import java.security.SecureRandom;

/**
 * Created by Emiliyan on 3/21/2016.
 *
 * Purpose: encrypt passwords with random salt and check if existing passwords match user input
 *
 * algorithm used: SHA-256
 * way hash function easy to encrypt - believed to be computationally infeasible to break
 * provides 128 bit security against collision attacks.
 *
 * although the algorithm is secure it is still vulnerable against birthday attacks and dictionary attacks
 * first generate random salt(String of random characters)
 * append raw password from user input after the salt
 * encrypt salt + password together 1 time
 * store encrypted result and salt on their own in database
 * to detect if password is correct pull encrypted password and salt from database
 * repeat encryption algorithm and check results
 *
 * more info on how passwords should be stored here: http://www.jasypt.org/howtoencryptuserpasswords.html
 * date link accessed: 21/March/2016
 */
public class Passwords {
    private static final String ALGORITHM ="SHA-256"; //hash function also known as one way function
    private static final int MAX_SALT_SIZE= 47;//maximum length of salt(String)  generated
    private static final int MIN_SALT_SIZE = 5;

    private static final int HASH_FUNCTION_ITERATIONS = 10;
    private RandomSaltGenerator rsg;
    private SecureRandom sr;
    private ConfigurablePasswordEncryptor digester;
    public Passwords(){
        rsg = new RandomSaltGenerator();
        sr  = new SecureRandom();
        /*make customised Encryptor
        * no salt will be generated as salt should be the same for checking passwords
        * */
        digester = new ConfigurablePasswordEncryptor();
        digester.setAlgorithm(ALGORITHM);   //set the algorithm
        digester.setPlainDigest(true);//disable salt generation and algorithm iterations
    }


    /*generate random salt - return String containing Base64 characters
    **/
    public String generateRandomSalt(){

        int saltSize = sr.nextInt(MAX_SALT_SIZE);
        if(saltSize<MIN_SALT_SIZE) saltSize= saltSize + MIN_SALT_SIZE;//make sure saltSize is bigger than 4

        //System.out.println("saltSize: " + saltSize);
        String encodedSalt = Base64.encodeBase64String(rsg.generateSalt(saltSize));

        if(encodedSalt.length()>MAX_SALT_SIZE) return encodedSalt.substring(0,MAX_SALT_SIZE);
        return encodedSalt;
    }

    //append password after salt and encrypt string together
    public String encryptPassword(String rawPassword,String salt){
        return digester.encryptPassword(salt + rawPassword);
    }

    /*check plain password from user input with database encrypted password with added salt*/
    /*if password does not match return false*/
    public boolean checkPassword(String rawPassword, String dbEncryptedPass, String dbSalt){

        String digest = encryptPassword(rawPassword, dbSalt);
//        System.out.println("check raw pass+salt: "+dbSalt + rawPassword);
//        System.out.println("check encrypted res: "+digest);
//        System.out.println("check db   password: "+dbEncryptedPass);
        if (digest.equals(dbEncryptedPass)) return true;//if password matches
        return false;
    }

}
