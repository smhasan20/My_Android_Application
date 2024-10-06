package com.example.myandroidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.myandroidapplication.ml.NoteNewModel;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DetectionActivity extends AppCompatActivity {
    TextView result, confidence;
    ImageView imageView;
    Button picture, generatePdfButton;
    int imageSize = 224;
    float tenTakaPercentage = 0, twentyTakaPercentage = 0, fiftyTakaPercentage = 0, hundredTakaPercentage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);

        result = findViewById(R.id.result);
        confidence = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.button);
        generatePdfButton = findViewById(R.id.generate_pdf_button); // Assuming you have a button for generating PDFs

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        generatePdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generatePdf();
            }
        });
    }

    public void classifyImage(Bitmap image) {
        try {
            NoteNewModel model = NoteNewModel.newInstance(getApplicationContext());

            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);
            NoteNewModel.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;

            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Ten taka", "Twenty taka", "Fifty taka", "One Hundred taka"};
            result.setText(classes[maxPos]);

            tenTakaPercentage = confidences[0] * 100;
            twentyTakaPercentage = confidences[1] * 100;
            fiftyTakaPercentage = confidences[2] * 100;
            hundredTakaPercentage = confidences[3] * 100;

            String s = "";
            for (int i = 0; i < classes.length; i++) {
                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);
            }
            confidence.setText(s);

            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void generatePdf() {
        Document document = new Document();
        String pdfFileName = "CurrencyDetection_" + System.currentTimeMillis() + ".pdf"; // Unique filename with timestamp

        File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), pdfFileName);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            // Add centered content to the PDF
            Paragraph title = new Paragraph("Currency Detection Results");
            title.setAlignment(Element.ALIGN_CENTER); // Center the title
            document.add(title);

            // Adding a new line after "Detected Note:" and centering it
            Paragraph detectedNote = new Paragraph("Detected Note: " + result.getText().toString());
            detectedNote.setAlignment(Element.ALIGN_CENTER); // Center the detected note line
            document.add(detectedNote);

            // Add a new line after the second line
            document.add(new Paragraph("\n"));

            // Adding percentages in a table
            PdfPTable table = new PdfPTable(2); // 2 columns
            table.addCell("Note Type");
            table.addCell("Percentage");

            table.addCell("Ten Taka");
            table.addCell(String.format("%.1f%%", tenTakaPercentage));

            table.addCell("Twenty Taka");
            table.addCell(String.format("%.1f%%", twentyTakaPercentage));

            table.addCell("Fifty Taka");
            table.addCell(String.format("%.1f%%", fiftyTakaPercentage));

            table.addCell("One Hundred Taka");
            table.addCell(String.format("%.1f%%", hundredTakaPercentage));

            document.add(table);

            Toast.makeText(this, "PDF Generated Successfully", Toast.LENGTH_SHORT).show();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF", Toast.LENGTH_SHORT).show();
        } finally {
            document.close();
        }
    }

}
