package com.example.encryptomail;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity3 extends AppCompatActivity {
    private EditText  messageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        messageEditText = findViewById(R.id.editTextMultiline2);

        Button button=findViewById(R.id.b3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MessageEncryptor encryptor = new MessageEncryptor();
                    String message = messageEditText.getText().toString();
                    String decryptedMessage = encryptor.decryptMessage(message,getString(R.string.secret123));
                    messageEditText.setText(decryptedMessage);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


            }
        });

    }
}