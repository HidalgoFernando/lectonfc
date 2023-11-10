package com.example.lectonfc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            // El dispositivo no soporta NFC
            Toast.makeText(this, "Este dispositivo no tiene soporte para NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            // El NFC no está habilitado
            Toast.makeText(this, "Habilita el NFC en la configuración del dispositivo", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        nfcAdapter.enableForegroundDispatch(
                this,
                pendingIntent,
                null,
                null
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();
        Log.d("NFC", "Acción del intent: " + action);

        byte[] uidBytes = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);

        if (uidBytes != null) {
            String uid = ByteArrayToHexString(uidBytes);
            Toast.makeText(this, "UID de la tarjeta: " + uid, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No se encontraron datos de la tarjeta", Toast.LENGTH_LONG).show();
        }
    }

    private String ByteArrayToHexString(byte[] array) {
        if (array == null) {
            return "No hay datos";
        }

        StringBuilder sb = new StringBuilder(array.length * 2);
        for (byte b : array) {
            int v = b & 0xFF;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }
}