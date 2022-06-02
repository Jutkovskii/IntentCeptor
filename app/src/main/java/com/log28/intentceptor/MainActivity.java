package com.log28.intentceptor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Intent savedIntent;
    String actionName;
    String uriName;
    String categoryName ;
    String typeName ;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView action_name= findViewById(R.id.actionName);
        TextView uri_name= findViewById(R.id.uriName);
        TextView category_name= findViewById(R.id.categoryName);
        TextView type_name= findViewById(R.id.typeName);
        savedIntent = getIntent();
        if(savedIntent.getAction() != "android.intent.action.MAIN") {
            try {
                actionName = savedIntent.getAction();
                //uriName =savedIntent.getData()==null?"null":savedIntent.getData().toString();
                uri=getUriFromIntent(savedIntent);
                uriName = uri == null ? "null" : uri.toString();
                categoryName = getCategories(savedIntent);
                typeName = savedIntent.getType() == null ? "null" : savedIntent.getType();

                action_name.setText(actionName);
                uri_name.setText(uriName);
                category_name.setText(categoryName);
                type_name.setText(typeName);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public Uri getUriFromIntent(Intent intent){
        Uri uri=null;

        if(intent.getType() == null){
            uri=intent.getData();
        }
        else {
            Bundle bundle=intent.getExtras();
           Set<String> keys= bundle.keySet();
            uri=(Uri) intent.getParcelableExtra((String) keys.toArray()[0]);
            if(uri==null)
                uri=intent.getData();
        }

        return uri;
    }

    public Intent checkFileUri(Intent intent){


        try {
            Intent newIntent = new Intent();
            newIntent.setAction(intent.getAction());
            if (uri.toString().contains("file:///")) {

                File tempFile;
                //File tempFile = new File(uri.getPath());
                tempFile = new File(uri.getPath().substring(uri.getPath().lastIndexOf("//") + 1));


                Uri localUri = FileProvider.getUriForFile(
                        MainActivity.this,
                        "com.log28.intentceptor", //(use your app signature + ".provider" )
                        tempFile);

                if (intent.getAction() == Intent.ACTION_SEND) {//||intent.getAction()==Intent.ACTION_VIEW){

                    newIntent.putExtra(Intent.EXTRA_STREAM, localUri);
                    newIntent.setType(typeName);
                } else {
   /*           String val="";
        String key="";
        try{
                  Set<String> keys = savedIntent.getExtras().keySet();
            Object o = savedIntent.getExtras().get((String) keys.toArray()[0]);
            val=o.toString();
            key=(String) keys.toArray()[0];
        }
        catch (Exception e){
            e.printStackTrace();
        }*/
              /*  newIntent.putExtra(key,val);
                newIntent.setDataAndType(localUri,typeName);*/


                    newIntent.setDataAndType(localUri, typeName);
                    newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //newIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:/" + uri.getEncodedPath()));

                }
                intent=newIntent;
            }

        }

        catch (Exception e) {
            e.printStackTrace();
        }




    return intent;
    }
    public String getCategories(Intent intent){
        if(intent.getCategories()==null) return "null";
        Set<String> categoryList =  intent.getCategories();
        String categories="";
        for(String current:categoryList){
            categories+=current+" ";
        }
        return categories;
    }
    public void onResendIntent(View v){
       /* String val="";
        String key="";
        try{
            Set<String> keys = savedIntent.getExtras().keySet();
            Object o = savedIntent.getExtras().get((String) keys.toArray()[0]);
            val=o.toString();
            key=(String) keys.toArray()[0];
        }
        catch (Exception e){

        }*/
        try {


            //Intent newIntent = new Intent(Intent.ACTION_SEND);
          /*   Intent newIntent = new Intent(actionName);
          if(savedIntent.getAction()==Intent.ACTION_SEND)
            {newIntent.putExtra(Intent.EXTRA_STREAM,checkFileUri(uri)); newIntent.setType(typeName);}
            else
            {
               // newIntent.putExtra(key,val);
                //newIntent.setDataAndType(uri,typeName);
                newIntent=(Intent) savedIntent.clone();
                newIntent.setComponent(null);
            }
*/

         Intent   newIntent=(Intent) savedIntent.clone();
            newIntent=checkFileUri(newIntent);
            newIntent.setComponent(null);
newIntent.addCategory(Intent.CATEGORY_DEFAULT);



            {
                startActivity(newIntent);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"Пересылка не удалась",Toast.LENGTH_LONG);
        }
    }
}