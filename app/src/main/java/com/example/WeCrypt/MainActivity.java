package com.example.WeCrypt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    EditText input_for_encrypt;
    TextView textView3;
    Button button_encrypt;
    Button button_decrypt;
    String outputString;
    String outputString2;
    String AES = "AES";
    Button button_to_copy;
    Button button_to_paste;
    Button button_to_clear;
    ClipboardManager clipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        encryptText();
        decryptText();
        copyAndPaste();
    }

    public void navigateToRSA(View view) {
        Intent intent = new Intent(this, UsingRsaAlgo.class);
        startActivity(intent);
    }

    public void encryptText() {
        button_encrypt = (Button) findViewById(R.id.button_encrypt);
        textView3 = (TextView) findViewById(R.id.textView3);
        input_for_encrypt = (EditText) findViewById(R.id.input_for_encrypt);

        button_encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                textView3.setText("Hello World");
                try {
                    outputString = encrypt(input_for_encrypt.getText().toString());
                    textView3.setText(outputString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void decryptText() {
        button_decrypt = (Button) findViewById(R.id.button_decrypt);
        textView3 = (TextView) findViewById(R.id.textView3);
        input_for_encrypt = (EditText) findViewById(R.id.input_for_encrypt);

        button_decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    outputString = decrypt(input_for_encrypt.getText().toString());
                    textView3.setText(outputString);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void copyAndPaste() {
        textView3 = (TextView) findViewById(R.id.textView3);
        button_to_copy = (Button) findViewById(R.id.button_to_copy);
        button_to_paste = (Button) findViewById(R.id.button_to_paste);

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        button_to_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textView3.getText().toString();

                if (!text.equals("")) {
                    ClipData clipData = ClipData.newPlainText("text", text);
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(MainActivity.this, "Copied", Toast.LENGTH_SHORT).show();
                    button_to_paste.setEnabled(true);
                }
            }
        });

        button_to_paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clipData = clipboardManager.getPrimaryClip();
                ClipData.Item item = clipData.getItemAt(0);

                input_for_encrypt.setText(item.getText().toString());
                Toast.makeText(MainActivity.this, "Pasted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clearInputText(View view) {
        input_for_encrypt = (EditText) findViewById(R.id.input_for_encrypt);

        input_for_encrypt.getText().clear();
    }

//    private String decrypt(String toString) {
//    }

    private String decrypt(String toString) throws Exception{
        SecretKeySpec key = generateKey(toString);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.decode(this.outputString, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
//        byte[] decValue = c.doFinal(toString.getBytes());
//        byte[] decodedValue = Base64.decode(decValue, Base64.DEFAULT);


        String decryptedValue = new String(decValue);
//        String decryptedValue = new String(decodedValue);
        return decryptedValue;

    }

    private String encrypt(String toString) throws Exception{
        SecretKeySpec key = generateKey(toString);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(toString.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    private SecretKeySpec generateKey(String toString) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = toString.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }


}
