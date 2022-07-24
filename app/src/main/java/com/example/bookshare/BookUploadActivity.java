package com.example.bookshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookshare.model.Book;
import com.example.bookshare.model.Category;
import com.example.bookshare.model.GlobalData;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookUploadActivity extends AppCompatActivity {

    private EditText etTitle, etPrice, etAuthorName;
    private RadioGroup rgType;
    private int typeCheckId, categoryId, purpose=1, conditionId, status=0;
    private String bookTitle, bookPrice, authorName, bookPicture;
    private ProgressBar pbBookUpload;

    private TextView txtTypeError;
    private Button btnUpload, btnUpdate;

    private List<String> categories = new ArrayList<String>();

    private String[] conditions = {
            "New", "Used"
    };

    private Spinner spCategory, spCondition;
    private ArrayAdapter<String> adapterCategory, adapterCondition;
    private ImageButton btnCloseScreen;
    private ImageView ivImage;

    private LinearLayout switchSection, purposeSection;
    private Switch activeSwitch;

    Integer REQUEST_CAMERA=1, SELECT_FILE=0;

    private SharedPreferences preferences;
    private static final String FILE_NAME="preferenceFile";

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";

    private Boolean switchState;
    private Book editBook;
    private Bitmap bitmap;

    private String encodedImage="";
    private int imageChanged = 0;

    private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_STORAGE = 2;

    String[] permissions = {"android.permission.CAMERA","android.permission.READ_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_upload);

        if(!isConnected(getApplicationContext())){
            Intent loadAgain = new Intent(getApplicationContext(),SplashActivity.class);
            startActivity(loadAgain);
        }

        Intent i = getIntent();
        String intentPurpose = i.getStringExtra("for");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions,12);
        }

        etTitle = (EditText) findViewById(R.id.et_book_title);
        etPrice = (EditText) findViewById(R.id.et_book_price);
        etAuthorName = (EditText) findViewById(R.id.et_author_name);
        txtTypeError = (TextView) findViewById(R.id.txtTypeError);

        switchSection = (LinearLayout) findViewById(R.id.switchSection);
        purposeSection = (LinearLayout) findViewById(R.id.purposeSection);
        purposeSection.setVisibility(View.GONE);

        pbBookUpload = (ProgressBar) findViewById(R.id.pb_bookUpload);
        pbBookUpload.setVisibility(View.GONE);

        activeSwitch = (Switch) findViewById(R.id.activeSwitch);
        switchState = activeSwitch.isChecked();

        activeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    purposeSection.setVisibility(View.VISIBLE);
                    switchState = true;
                }else{
                    purposeSection.setVisibility(View.GONE);
                    switchState = false;
                }
            }
        });

        for(Category c:GlobalData.getInstance().categoryList){
            //System.out.println("c name="+c.categoryName+";");
            categories.add(c.categoryName);
        }

        spCategory = findViewById(R.id.sp_book_category);
        adapterCategory = new ArrayAdapter<String>(this, R.layout.spinner_sample_view, R.id.sp_text_item, categories);
        spCategory.setAdapter(adapterCategory);
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryId = GlobalData.getInstance().categoryList.get(position).categoryId;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categoryId = GlobalData.getInstance().categoryList.get(0).categoryId;
            }
        });

        spCondition = findViewById(R.id.sp_book_condition);
        adapterCondition = new ArrayAdapter<String>(this, R.layout.spinner_sample_view, R.id.sp_text_item, conditions);
        spCondition.setAdapter(adapterCondition);
        spCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                conditionId = position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                conditionId = 1;
            }
        });

        rgType = (RadioGroup) findViewById(R.id.select_purpose);
        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                txtTypeError.setVisibility(View.GONE);
                switch (checkedId){
                    case R.id.purpose_sell:
                        purpose = 1;
                        break;

                    case R.id.purpose_rent:
                        purpose = 2;
                        break;
                }
            }
        });

        btnCloseScreen = (ImageButton) findViewById(R.id.btn_close_screen);
        btnCloseScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivImage = (ImageView) findViewById(R.id.ivImage);
        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        CharSequence[] items={"Camera","Gallery", "Cancel"};
                        String message = "";
                        selectImage(items, message);
                    }else{
                        CharSequence[] items={"Camera","Cancel"};
                        String message = "";
                        Toast.makeText(getApplicationContext(), "Accept STORAGE permission from your phone settings to select pictures from Gallery!",Toast.LENGTH_LONG).show();
                        selectImage(items, message);
                    }
                }else if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    CharSequence[] items={"Gallery", "Cancel"};
                    String message = "";
                    Toast.makeText(getApplicationContext(), "Accept CAMERA permission from your phone settings to take picture using Camera!",Toast.LENGTH_LONG).show();
                    selectImage(items, message);
                }else{
                    CharSequence[] items={"Close"};
                    String message = "Accept CAMERA & STORAGE permission from your phone settings to select or click pictures!";
                    selectImage(items, message);
                    //Toast.makeText(getApplicationContext(), "Accept CAMERA & STORAGE permission from your phone settings to select or click pictures!",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);


        if(intentPurpose.equals("add")){
            btnUpload.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.GONE);
        }else if(intentPurpose.equals("edit")){
            btnUpload.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.VISIBLE);

            editBook = (Book) i.getSerializableExtra("book");
            bookPicture = editBook.getPicture();

            etTitle.setText(editBook.getTitle());
            etPrice.setText(editBook.getPrice()+"");
            etAuthorName.setText(editBook.getAuthorName());
            int indx = 0;
            for(Category c : GlobalData.getInstance().categoryList){
                if(editBook.getIdCategory()==c.categoryId){
                    spCategory.setSelection(adapterCategory.getPosition(c.categoryName));
                }
            }

            if(editBook.getIdCondition()==1){
                spCondition.setSelection(0);
            }else if(editBook.getIdCondition()==2){
                spCondition.setSelection(1);
            }

            if(editBook.getUserBookStatus()==0){
                activeSwitch.setChecked(false);
            }else if(editBook.getUserBookStatus()==1){
                activeSwitch.setChecked(true);
            }

            if(editBook.getPurpose()==1){
                rgType.check(R.id.purpose_sell);
            }else if(editBook.getPurpose()==2){
                rgType.check(R.id.purpose_rent);
            }

            if(editBook.picture.isEmpty() || editBook.picture.equals("") || editBook.picture.equals("null")){
                ivImage.setImageResource(R.drawable.single_book);
            }else{
                String url = GlobalData.getInstance().url+editBook.picture;
                //bookImage.setImageResource(LoadImageFromWebOperations(b.picture).picture);
                Picasso.get().load(url).into(ivImage);
            }
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbBookUpload.setVisibility(View.VISIBLE);
                int flag = formValidation();
                if(flag==1){
                    uploadBook();
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = formValidation();
                if(flag==1){
                    updateBook();
                }
            }
        });
    }


    private void getFormValues(){
        bookTitle = etTitle.getText().toString();
        bookPrice = etPrice.getText().toString();
        authorName = etAuthorName.getText().toString();
        typeCheckId = rgType.getCheckedRadioButtonId();
        //System.out.println("-----------------Gender ID="+genderCheckId);
    }

    private int formValidation(){
        getFormValues();
        txtTypeError.setVisibility(View.GONE);
        //pbSignup.setVisibility(View.GONE);

        if(TextUtils.isEmpty(bookTitle)){
            etTitle.setError("Please enter book title");
            return 0;
        }

        if(TextUtils.isEmpty(bookPrice)){
            etPrice.setError("Please enter price");
            return 0;
        }

        if(TextUtils.isEmpty(authorName)){
            etAuthorName.setError("Please enter author name");
            return 0;
        }

        if(switchState==true){
            if(rgType.getCheckedRadioButtonId()==-1){
                txtTypeError.setVisibility(View.VISIBLE);
                return 0;
            }
        }

        return 1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

    private void selectImage(final CharSequence[] items, String message){

        //final CharSequence[] items={"Camera","Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(BookUploadActivity.this);
        builder.setTitle("Add Image");
        if(!message.equals("")) {
            builder.setMessage(message);
        }

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    imageChanged = 1;

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else if (items[i].equals("Gallery")) {
                    imageChanged = 1;

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    //startActivityForResult(intent.createChooser(intent, "Select File"), SELECT_FILE);
                    startActivityForResult(intent, SELECT_FILE);

                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK){
            encodedImage = "";

            if(requestCode==REQUEST_CAMERA){

                Bundle bundle = data.getExtras();
                bitmap = (Bitmap) bundle.get("data");
                ivImage.setImageBitmap(bitmap);
                encodedImage = imageToString();

            }else if(requestCode==SELECT_FILE){

                Uri filePath = data.getData();
                ivImage.setImageURI(filePath);

                try {
                    InputStream inputStream = getContentResolver().openInputStream(filePath);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    encodedImage = imageToString();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private String imageToString(){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        return encodedImage;
    }

    public void uploadBook(){
        //pbSignup.setVisibility(View.VISIBLE);
        apiName = "api_bookUpload.php";
        action = action+"addBook";
        url = url+apiName+action;
        //System.out.println("----------URL= "+url);
        if(switchState){
            status = 1;
        }else{
            status = 0;
            purpose = 0;
        }

        if(bitmap==null){
            Toast.makeText(getApplicationContext(),"Select book image first",Toast.LENGTH_LONG).show();
            ivImage.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.alert));
        }else {

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String[] s = response.split("-");
                    if (s[0].equals("yes")) {
                        //Intent i = new Intent(getApplicationContext(),OrderActivity.class);
                        //startActivity(i);
                        Toast.makeText(getApplicationContext(), "Book uploaded successfully", Toast.LENGTH_LONG).show();
                        //pb.setVisibility(View.GONE);
                        //Intent goLogin = new Intent(BookUploadActivity.this,LoginActivity.class);
                        //startActivity(goLogin);
                        finish();

                    } else if (response.toString().equals("tokenFail")) {
                        Toast.makeText(getApplicationContext(), "Wrong token, try again", Toast.LENGTH_SHORT).show();
                        //pb.setVisibility(View.GONE);
                        Intent reload = new Intent(getApplicationContext(), BookUploadActivity.class);
                        reload.putExtra("for", "add");
                        startActivity(reload);
                        finish();
                    } else {
                        //msg.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        Intent reload = new Intent(getApplicationContext(), BookUploadActivity.class);
                        reload.putExtra("for", "add");
                        startActivity(reload);
                        finish();
                        System.out.println("................................$$$"+response+"$$$");
                        //pb.setVisibility(View.GONE);
                    }
                    //System.out.println(">>>>>>>>>>>>>>>>>>>>"+response);
                    //pbSignup.setVisibility(View.GONE);
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error occurred!", Toast.LENGTH_SHORT).show();
                    //pb.setVisibility(View.GONE);
                    System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<" + "ERRoR");
                    System.out.println(".......................$$$"+error+"$$$");
                    Intent reload = new Intent(getApplicationContext(), BookUploadActivity.class);
                    reload.putExtra("for", "add");
                    startActivity(reload);
                    finish();
                    //pbSignup.setVisibility(View.GONE);
                }
            }) {
                @Override
                public Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("myToken", "786");
                    params.put("userId", GlobalData.getInstance().userId + "");
                    params.put("bookTitle", bookTitle);
                    params.put("bookPrice", bookPrice);
                    params.put("authorName", authorName);
                    params.put("conditionId", conditionId + "");
                    params.put("categoryId", categoryId + "");
                    params.put("purpose", purpose + "");
                    params.put("status", status + "");
                    params.put("image", encodedImage);
                    System.out.println("----------------------file size: "+encodedImage.length());
                    return params;
                }
            };


            //System.out.println("edUsername= "+GlobalData.getInstance().username);
            //System.out.println(GlobalData.getInstance());
            GlobalData.getInstance().addToRequestQueue(request);
        }
    }

    public void updateBook(){
        //pbSignup.setVisibility(View.VISIBLE);
        apiName = "api_bookUpload.php";
        action = action+"updateBook";
        url = url+apiName+action;
        //System.out.println("----------URL= "+url);
        if(switchState){
            status = 1;
        }else{
            status = 0;
            purpose = 0;
        }

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pbBookUpload.setVisibility(View.GONE);
                if(response.equals("yes")){
                    //Intent i = new Intent(getApplicationContext(),OrderActivity.class);
                    //startActivity(i);
                    Toast.makeText(getApplicationContext(),"Book updated successfully",Toast.LENGTH_LONG).show();
                    //pb.setVisibility(View.GONE);
                    //Intent goLogin = new Intent(BookUploadActivity.this,LoginActivity.class);
                    //startActivity(goLogin);
                    finish();

                } else if(response.toString().equals("tokenFail")){
                    Toast.makeText(getApplicationContext(),"Wrong token, try again",Toast.LENGTH_SHORT).show();
                    //pb.setVisibility(View.GONE);
                    Intent reload = new Intent(getApplicationContext(),BookUploadActivity.class);
                    reload.putExtra("for", "edit");
                    reload.putExtra("book", editBook);
                    startActivity(reload);
                    finish();
                } else{
                    //msg.setVisibility(View.VISIBLE);
                    //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                    Intent reload = new Intent(getApplicationContext(),BookUploadActivity.class);
                    reload.putExtra("for", "edit");
                    reload.putExtra("book", editBook);
                    startActivity(reload);
                    finish();
                    System.out.println("---------------------------------$$$"+response+"$$$");
                    //pb.setVisibility(View.GONE);
                }
                //System.out.println(">>>>>>>>>>>>>>>>>>>>"+response);
                //pbSignup.setVisibility(View.GONE);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error occured!",Toast.LENGTH_SHORT).show();
                //pb.setVisibility(View.GONE);
                System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<"+"ERRoR");
                //pbSignup.setVisibility(View.GONE);
            }
        }){
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("myToken", "786");
                params.put("bookId",editBook.getId()+"");
                params.put("userId",GlobalData.getInstance().userId+"");
                params.put("bookTitle",bookTitle);
                params.put("bookPrice",bookPrice);
                params.put("bookPicture",bookPicture);
                params.put("authorName",authorName);
                params.put("conditionId", conditionId+"");
                params.put("categoryId",categoryId+"");
                params.put("purpose",purpose+"");
                params.put("status",status+"");
                params.put("imageChanged", imageChanged+"");
                params.put("image", encodedImage);
                return params;
            }
        };


        //System.out.println("edUsername= "+GlobalData.getInstance().username);
        //System.out.println(GlobalData.getInstance());
        GlobalData.getInstance().addToRequestQueue(request);
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        }
        else return false;
    }
}