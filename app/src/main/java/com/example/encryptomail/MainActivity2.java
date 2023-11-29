package com.example.encryptomail;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.Manifest;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
//import com.itextpdf.io.IOException;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;


import com.example.encryptomail.databinding.ActivityMain2Binding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMain2Binding binding;
    private EditText toEditText, subjectEditText, messageEditText;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private File filePDF_initial, filePDF_finalOutput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main2);
        toEditText = findViewById(R.id.editTextTo);
        subjectEditText = findViewById(R.id.editTextSubject);
        messageEditText = findViewById(R.id.editTextMultiline);

        Button button=findViewById(R.id.buttonSend);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });


    }
    private void check()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest
                .permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            // Permission already granted, proceed with file creation
            createPdf();
        }
    }
    private void createPdf() {
        String message = messageEditText.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timestamp = sdf.format(new Date());

        // Create a file to save the PDF with timestamp
        String pdfFileName = "password_protected_" + timestamp + ".pdf";
        File pdfFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), pdfFileName);

        try {
            // Initialize Document and PdfWriter with password protection
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            writer.setEncryption("userPassword".getBytes(), "ownerPassword".getBytes(),
                    PdfWriter.ALLOW_COPY, PdfWriter.ENCRYPTION_AES_128);

            // Open the document for writing
            document.open();



            // Add content to the PDF
            document.add(new Paragraph(getencryptedmessage(message)));

            // Close the document
            document.close();
            String pdfPath = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS) + "/";
            pdfPath+=pdfFileName;
            Toast.makeText(this, pdfPath, Toast.LENGTH_LONG).show();


            sendEmail(pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating PDF", Toast.LENGTH_SHORT).show();
        }

        // Now you have a password-protected PDF with text
    }
    private String getencryptedmessage(String message) throws Exception {
        MessageEncryptor encryptor = null;
        try {
            encryptor = new MessageEncryptor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Original message


        // Encrypt the message
        String encryptedMessage = encryptor.encryptMessage(message,getString(R.string.secret123));

        return encryptedMessage;
    }
    private void sendEmail(File attach) {
        String to = toEditText.getText().toString();
        String subject = subjectEditText.getText().toString();


        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("application/pdf");

        // Add email address, subject, and body if needed
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");

        // Attach the PDF file
        Uri fileUri = FileProvider.getUriForFile(
                this,
                "com.example.encryptomail.fileprovider",
                attach);

        // Grant read permission to the receiver app
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Attach the PDF file to the email
        emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);

        // Start the email intent
        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }
}