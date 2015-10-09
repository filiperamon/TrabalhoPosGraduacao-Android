package fa7.edu.com.avaliacao1fa7;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    static final int REQUEST_SELECT_CONTACT = 1;
    static final int REQUEST_SELECT_CAMERA = 2;

    String contato = "Contato:";
    String NumeroContato;
    Bitmap imgAnexo;
    Uri uriAnexo;

    Button btnContato;
    Button btnCamera;
    Button btnEnviar;
    ImageView imgCamera;
    TextView txtContato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnContato = (Button)    findViewById(R.id.btnContatos);
        btnCamera  = (Button)    findViewById(R.id.btnCamera);
        btnEnviar  = (Button)    findViewById(R.id.btnEnviar);
        txtContato = (TextView)  findViewById(R.id.txtContato);
        imgCamera  = (ImageView) findViewById(R.id.imgCamera);

        btnContato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContact();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCamera();
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeMmsMessage();
            }
        });
    }

    public void selectContact() {
        Intent intentContato = new Intent(Intent.ACTION_PICK);
        intentContato.setType(ContactsContract.Contacts.CONTENT_TYPE);

        if (intentContato.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intentContato, REQUEST_SELECT_CONTACT);
        }
    }

    public void selectCamera(){
        Intent intentCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentCamera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intentCamera, REQUEST_SELECT_CAMERA);
        }
    }

    public void composeMmsMessage(){

        Intent intentMmsMessage = new Intent(Intent.ACTION_SENDTO);
        intentMmsMessage.setType("image/*");
        intentMmsMessage.setData(Uri.parse("mmsto:"+NumeroContato));
        intentMmsMessage.putExtra(Intent.EXTRA_STREAM, uriAnexo);
        startActivity(intentMmsMessage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtContato.setText(contato);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();

            switch (requestCode) {

                case REQUEST_SELECT_CONTACT:
                    getContact(this, uri.getLastPathSegment());
                    txtContato.setText(contato);
                    break;

                case REQUEST_SELECT_CAMERA:
                    imgAnexo = data.getParcelableExtra("data");
                    imgCamera.setImageBitmap(imgAnexo);
                    uriAnexo = uri;
                    break;
            }
        }
    }

    public void getContact(Context context, String contactId) {
        int type = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        String[] whereArgs = new String[] { String.valueOf(type) };

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.TYPE + " = ?",
                whereArgs,
                null);

        if(cursor != null && cursor.getCount()>0) {
            try {
                while (cursor.moveToNext()) {

                    if(contactId.equals(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)))) {
                        this.contato = "Contato: " + cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)) + " - " +
                                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        NumeroContato = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        cursor.moveToLast();
                    }
                }
            } finally {
                cursor.close();
            }
        } else {
            Toast.makeText(this,"Cursor vazio.",Toast.LENGTH_SHORT).show();
        }

    }
}
