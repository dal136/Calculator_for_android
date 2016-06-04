package peng.tt.hit.calculator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    GridLayout gridLayout;

    boolean isResult = false; //是否得出结果
    String[] chars = new String[]
            {
              "7", "8", "9", "/",
                    "4", "5", "6", "*",
                    "1", "2", "3", "-",
                    ".", "0", "=", "+"
            };

    ArrayList<String> stack = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = (GridLayout)findViewById(R.id.root);
        final TextView textView = (TextView)findViewById(R.id.textView);
        Button clear_btn = (Button)findViewById(R.id.clear_btn);
        Button back_btn = (Button)findViewById(R.id.back_btn);
        //清空按钮监听器
        clear_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isResult = false;
                textView.setText("0");
                stack.clear();
            }
        });
        //回退按钮监听器
        back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(isResult){
                    isResult = false;
                    textView.setText("0");
                    return;
                }
                String str = textView.getText().toString();
                int l = str.length();
                if(l>0) {
                    str = str.substring(0, l - 1);
                    textView.setText(str);
                    int size = stack.size();
                    String cell = stack.get(size - 1);
                    if(cell.length() == 1)
                        stack.remove(size-1);
                    else{
                        int length = cell.length();
                        cell = cell.substring(0, length-1);
                        stack.set(size-1,cell);
                    }
                }
            }
        });
        for(int i=0;i<chars.length;i++){
            Button bn = new Button(this);
            bn.setText(chars[i]);
            bn.setTextSize(40);
            GridLayout.Spec rowSpec = GridLayout.spec(i/4+2);
            GridLayout.Spec columnSpec = GridLayout.spec(i%4);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                    rowSpec, columnSpec
            );
            params.setGravity(Gravity.FILL);
            gridLayout.addView(bn, params);

            bn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isResult){
                        isResult = false;
                        textView.setText("");
                    }
                    Button btn = (Button)v;
                    if(!btn.getText().equals("=")){
                        if(textView.getText().toString().trim().equals("0"))
                            textView.setText("");
                        textView.append(btn.getText());
                        char ch = btn.getText().charAt(0);
                        int size = stack.size();

                        if(Character.isDigit(ch) || ch == '.'){
                            if(stack.isEmpty())
                                stack.add(""+ch);
                            else{
                                if(!stack.get(size-1).equals("+") &&
                                        !stack.get(size-1).equals("-") &&
                                        !stack.get(size-1).equals("*") &&
                                        !stack.get(size-1).equals("/") ) {
                                    String fomer = stack.get(size - 1);
                                    fomer = fomer+ch;
                                    stack.set(size-1, fomer);
                                }
                                else
                                    stack.add(""+ch);
                            }
                        }
                        else
                            stack.add(""+ch);
                    }else{ //按下“=”时
                        if(stack.size()%2 == 0 || stack.size() == 1){
                            Toast toast = Toast.makeText(MainActivity.this, "表达式错误！", Toast.LENGTH_SHORT);
                            toast.show();
                            textView.setText("0");
                            stack.clear();
                            return;
                        }
                        double a = 0;
                        double b = 0;
                        for(int i=0;i<stack.size();i++){
                            String ch = stack.get(i).trim();

                            if(i%2==0){
                                if (i==0)
                                    try {
                                        a = Double.valueOf(ch);
                                    }catch(NumberFormatException e){
                                        Toast toast = Toast.makeText(MainActivity.this, "表达式错误！", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                i++;
                                String ch_op = stack.get(i);
                                i++;
                                ch = stack.get(i);
                                try{
                                    b = Double.valueOf(ch);
                            }catch(NumberFormatException e){
                                Toast toast = Toast.makeText(MainActivity.this, "表达式错误！", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                                switch (ch_op){
                                    case "+":
                                        a=a+b;
                                        break;
                                    case "-":
                                        a=a-b;
                                        break;
                                    case "*":
                                        a=a*b;
                                        break;
                                    case "/":
                                        a=a/b;
                                        break;
                                }
                            }else{
                                String ch_op = ch;
                                i++;
                                ch = stack.get(i);
                                try{
                                    b = Double.valueOf(ch);
                            }catch(NumberFormatException e){
                                Toast toast = Toast.makeText(MainActivity.this, "表达式错误！", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                                switch (ch_op){
                                    case "+":
                                        a=a+b;
                                        break;
                                    case "-":
                                        a=a-b;
                                        break;
                                    case "*":
                                        a=a*b;
                                        break;
                                    case "/":
                                        a=a/b;
                                        break;
                            }}

                        }
                        textView.append("\n");
                        textView.append(""+a);
                        stack.clear();
                        isResult = true;
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
