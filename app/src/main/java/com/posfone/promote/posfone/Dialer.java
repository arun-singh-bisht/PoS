package com.posfone.promote.posfone;

import android.app.Notification;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.posfone.promote.posfone.fragment.ContactFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

import static com.posfone.promote.posfone.PoSFoneApplicationClass.CHANNEL_1_ID;
public class Dialer extends AppCompatActivity {

    Drawable drawable_u,drawable_p;
    @BindView(R.id.input)
    EditText input;
    @BindView(R.id.match)
    TextView cont_name;
    @BindView(R.id.country_check)
    TextView country_check;
    Animation animation;
    Button buttonplus;
    boolean number_flag;
    private NotificationManagerCompat notificationManager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);
        final float density=getResources().getDisplayMetrics().density;
        drawable_u=getResources().getDrawable(R.drawable.backspace_arrow);
        final int width=Math.round(24*density);
        final int height=Math.round(24*density);
        input=findViewById(R.id.input);
        input.setShowSoftInputOnFocus(false);



        drawable_u.setBounds(0,0,width,height);
        if(input.isInTouchMode()){
            input.setCursorVisible(true);
        }
        notificationManager = NotificationManagerCompat.from(this);
        buttonplus=findViewById(R.id.buttonZero);
        buttonplus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onButtonClick(input ,"+" );
                return true;
            }
        });
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String number =String.valueOf(charSequence);
                String name=getName(number);
                    cont_name.setText(name);
                input.setSelection(input.getText().toString().length());
                if(input.getText().toString().length()>2){
                    String size_max=input.getText().toString();
                    String size_med=input.getText().toString().substring(0,2);
                    String size_min=input.getText().toString().substring(0,1);
                    if(size_max.contains("+44") || size_max.contains("+91") || size_med.contains("+1") || size_med.contains("44") || size_med.contains("91")|| size_min.contains("1"))
                    {
                        number_flag=true;
                        country_check.setVisibility(View.INVISIBLE);
                    }
                    else {
                        number_flag=false;
                        country_check.setVisibility(View.VISIBLE);
                    }

                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ButterKnife.bind(this);
        onview(this);
    }


    public void onview(Dialer v) {
        final LinearLayout linearLayout=findViewById(R.id.mylayout);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        linearLayout.startAnimation(animation);
        String number=getIntent().getStringExtra("number");
        if(number!=null && number.length()>1)
            input.setText(number);
        linearLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                linearLayout.setVisibility(View.VISIBLE);
            }
        },1000);
    }

    @OnClick(R.id.buttonOne)
    public void one() {
        onButtonClick(input ,"1" );
    }
    @OnClick(R.id.buttonTwo)
    public void two() {
        onButtonClick(input ,"2" );
    }
    @OnClick(R.id.buttonThree)
    public void three() {
        onButtonClick(input ,"3" );
    }
    @OnClick(R.id.buttonFour)
    public void four() {
        onButtonClick(input ,"4" );
    }
    @OnClick(R.id.buttonFive)
    public void five() {
        onButtonClick(input ,"5" );
    }
    @OnClick(R.id.buttonSix)
    public void six() {
        onButtonClick(input ,"6" );
    }
    @OnClick(R.id.buttonSeven)
    public void seven() {
        onButtonClick(input ,"7" );
    }
    @OnClick(R.id.buttonEight)
    public void eight() {
        onButtonClick(input ,"8" );
    }
    @OnClick(R.id.buttonNine)
    public void nine() {
        onButtonClick(input ,"9" );
    }
    @OnClick(R.id.buttonZero)
    public void zero() {
        onButtonClick(input ,"0" );
    }
    @OnClick(R.id.buttonStar)
    public void star() {
        onButtonClick(input ,"*" );
    }
    @OnClick(R.id.buttonHash)
    public void hash() {
        onButtonClick(input ,"#" );
    }


    @OnClick(R.id.buttonDelete)
    public void delete(){
        String newScreen="";
        int selection_start=input.getSelectionStart();
        int selection_end=input.getSelectionEnd();
        String selectedText = input.getText().toString().substring(0, input.getSelectionEnd());
        System.out.println("text is ------ "+selectedText+" "+input.getSelectionStart()+" "+input.getSelectionEnd());

        if(selectedText.length()>0){
             newScreen=input.getText().toString().substring(0,input.getSelectionEnd()-1);
             String prefix=input.getText().toString().substring(input.getSelectionEnd(),input.getText().toString().length());
             input.setText(newScreen+prefix);
             newScreen="";
             input.setSelection(selection_end-1);
        }else{}
    }

    @OnClick(R.id.buttonDial)
    public void call(){
        if(number_flag){
        Intent intent = new Intent(this,VoiceActivity2.class);
        intent.setAction(VoiceActivity2.ACTION_OUTGOING_CALL);
        intent.putExtra("from_number","16617480240");
        intent.putExtra("to_number",input.getText().toString());
        intent.putExtra("to_name","No Name");
        startActivity(intent);
        }
    }



    public void onButtonClick( EditText inputNumber, String number) {

        String newScreen="";
        String prefix="";
        int selection_end=input.getSelectionEnd();
        String selectedText = input.getText().toString().substring(0, input.getSelectionEnd());
        if(selectedText.length()>0){
            newScreen=input.getText().toString().substring(0,input.getSelectionEnd());
            prefix=input.getText().toString().substring(selection_end,input.getText().toString().length());
            input.setText(newScreen+number+prefix);
            newScreen="";
            input.setSelection(selection_end+1);
        }
        else{
            inputNumber.setText(number+input.getText().toString());
            input.setSelection(selection_end+1);
        }
    }

    //Search contacts



    public String getName(String number) {
        String name="";

        for (int i = 0; i< ContactFragment.searchContacts.size(); i++){
           // System.out.println(ContactFragment.searchContacts.get(i).getContactNumber()+"----"+ContactFragment.searchContacts.get(i).getContactName());
            if(PhoneNumberUtils.compare(number, ContactFragment.searchContacts.get(i).getContactNumber())){
                System.out.println("found Match --------"+ContactFragment.searchContacts.get(i).getContactName());
                name=ContactFragment.searchContacts.get(i).getContactName();
                return name;
            }

        }
        return name;
    }
}
