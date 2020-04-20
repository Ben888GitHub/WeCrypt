package com.example.WeCrypt;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

public class UsingRsaAlgo extends AppCompatActivity {

    Button navigate_to_aes;
    EditText input_for_encrypt2;
    TextView textView8;
    ClipboardManager clipboardManager;
    Button button_copy_en2;
    Button button_paste;
    Button button_clearing;

    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    private String publicKey = "";
    private String privateKey = "";
    private byte[] encodeData = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_using_rsa_algo);

        navigateToAES();

        copyText();
//        clearInput();

        UsingRsaAlgo rsa = null;
        try {
            Map<String, Object> keyMap = rsa.initKey();
            publicKey = rsa.getPublicKey(keyMap);
            privateKey = rsa.getPrivateKey(keyMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void navigateToAES() {
        navigate_to_aes = (Button) findViewById(R.id.navigate_to_aes);
        navigate_to_aes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void encrypt(View v) {
        input_for_encrypt2 = (EditText) findViewById(R.id.input_for_encrypt2);
        textView8 = (TextView) findViewById(R.id.textView8);

//        String rsaStr = input_for_encrypt2.getText().toString();

        byte[] rsaData = input_for_encrypt2.getText().toString().getBytes();

        UsingRsaAlgo rsa = null;
        try {
            encodeData = rsa.encryptByPublicKey(rsaData, getPublicKey());
            String encodeStr = new BigInteger(1,  encodeData).toString();
            textView8.setText(encodeStr);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @SuppressLint("WrongViewCast")
    public void decrypt(View v) {
//        output_for_decrypt2 = (EditText) findViewById(R.id.output_for_decrypt2);
        input_for_encrypt2 = (EditText) findViewById(R.id.input_for_encrypt2);
        textView8 = (TextView) findViewById(R.id.textView8);

//        String rsaStr = input_for_encrypt2.getText().toString();

        byte[] rsaData = input_for_encrypt2.getText().toString().getBytes();

        UsingRsaAlgo rsa = null;
        try {
            byte[] decodeData = rsa.encryptByPrivateKey(encodeData, getPrivateKey());
            String decodeStr = new String(decodeData);
            textView8.setText(decodeStr);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyText() {
        textView8 = (TextView) findViewById(R.id.textView8);
        button_copy_en2 = (Button) findViewById(R.id.button_copy_en2);
        button_paste = (Button) findViewById(R.id.button_paste);

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        button_copy_en2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textView8.getText().toString();

                if (!text.equals("")) {
                    ClipData clipData = ClipData.newPlainText("text", text);
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(UsingRsaAlgo.this, "Copied", Toast.LENGTH_SHORT).show();
                    button_paste.setEnabled(true);
                }
            }
        });

        button_paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clipData = clipboardManager.getPrimaryClip();
                ClipData.Item item = clipData.getItemAt(0);

                input_for_encrypt2.setText(item.getText().toString());
                Toast.makeText(UsingRsaAlgo.this, "Pasted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void clearInput(View view) {
        input_for_encrypt2 = (EditText) findViewById(R.id.input_for_encrypt2);

        input_for_encrypt2.getText().clear();
    }

    public String getPublicKey() {
        return publicKey;
    }

//    public void setPublicKey(String publicKey) {
//        this.publicKey = publicKey;
//    }

    public String getPrivateKey() {
        return privateKey;
    }

//    public void setPrivateKey(String privateKey) {
//        this.privateKey = privateKey;
//    }

    public void rsa() {

    }

    public static byte[] decryptBASE64(String key) throws Exception {
        return Base64.decode(key, Base64.DEFAULT);

    }

    public static String encryptBASE64(byte[] key) throws Exception {
        return Base64.encodeToString(key, Base64.DEFAULT);
    }

    public static String sign(byte[] data, String privateKey) throws Exception{
        byte[] keyBytes = decryptBASE64(privateKey);

        // PKCS8EncodedKeySpec
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

        // KEY_ALGORITHM
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        // PrivateKey
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // Signature
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(data);

        return encryptBASE64(signature.sign());
    }

    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = decryptBASE64(publicKey);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        PublicKey pubKey = keyFactory.generatePublic(keySpec);

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(data);

        return signature.verify(decryptBASE64(sign));

    }

    public static byte[] decryptByPrivateKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = decryptBASE64(key);

        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());

        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(data);

    }

    public static byte[] decryptByPublicKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = decryptBASE64(key);

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        return cipher.doFinal(data);

    }

    public  static byte[] encryptByPublicKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = decryptBASE64(key);

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }

    public static byte[] encryptByPrivateKey(byte[] data, String key) throws Exception{
        byte[] keyBytes = decryptBASE64(key);

        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

    public  static String getPrivateKey(Map<String, Object> keyMap) throws Exception{
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return encryptBASE64(key.getEncoded());
    }

    public static String getPublicKey(Map<String, Object> keyMap)  throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);

        return encryptBASE64(key.getEncoded());
    }

    public static Map<String, Object> initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);

        KeyPair keyPair = keyPairGen.generateKeyPair();

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, Object> keyMap = new HashMap<String, Object>(2);

        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }
}
