package com.weare2434.sha256;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();
                if (!text.equals("")){
                    textView.setText(text);
                    editText.setText("");

                    byte[] cipher_byte;
                    try {
                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        md.update(text.getBytes());
                        cipher_byte = md.digest();
                        StringBuilder sb = new StringBuilder(2 * cipher_byte.length);
                        for(byte b: cipher_byte) {
                            sb.append(String.format("%02x", b&0xff) );
                        }
                        text = String.valueOf(sb);

                        //クリップボードにテキストを保存
                        ClipboardManager clipboard = (ClipboardManager)
                                getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setPrimaryClip(ClipData.newPlainText("", text));

                        Toast.makeText(view.getContext(), text, Toast.LENGTH_SHORT).show();

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("")
                                .setMessage("ハッシュ値：\n" + text + "\n計算成功！クリップボードにコピーしました。")
                                .setPositiveButton("close", null)
                                .show();
                        text = "計算成功！";
                    } catch (Exception e) {
                        text = "計算失敗！";
                        Toast.makeText(view.getContext(), text, Toast.LENGTH_SHORT).show();

                        new AlertDialog.Builder(view.getContext())
                                .setTitle("")
                                .setMessage(text)
                                .setPositiveButton("close", null)
                                .show();
                    }
                    textView.setText(text);
                }
                else {
                    Toast.makeText(view.getContext(), "入力してください", Toast.LENGTH_SHORT).show();
                    textView.setText("No Input");
                }
            }
        });

        //textViewコピー有効
        textView.setTextIsSelectable(true); // Text Selection をenableにする
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setCustomSelectionActionModeCallback(new ActionMode.Callback2() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                    switch (item.getItemId()) {
                        case android.R.id.copy:
                            int min = 0;
                            int max = textView.getText().length();
                            if (textView.isFocused()) {
                                final int selStart = textView.getSelectionStart();
                                final int selEnd = textView.getSelectionEnd();

                                min = Math.max(0, Math.min(selStart, selEnd));
                                max = Math.max(0, Math.max(selStart, selEnd));
                            }

                            final CharSequence selectedText = textView.getText()
                                    .subSequence(min, max);
                            String text = selectedText.toString();

                            ClipboardManager clipboard = (ClipboardManager)
                                    getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setPrimaryClip(ClipData.newPlainText("", text));

                            // ActionModeの終了
                            mode.finish();
                            return true;
                        case android.R.id.cut:
                            return true;
                        case android.R.id.paste:
                            return true;

                        default:
                            break;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                }
            });
        }
    }
}
