package juanmanuelco.facci.com.soschat.BLUETOOTH.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import juanmanuelco.facci.com.soschat.BLUETOOTH.Entities.ChatMessage;
import juanmanuelco.facci.com.soschat.R;

public class MsgArrayAdapter extends ArrayAdapter<ChatMessage> {
    private TextView chatText;
    private ImageView imagen;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;
    private Bitmap bitmap;


    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public MsgArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.tipo_contenido.equals("texto")){
            if (chatMessageObj.left) {
                row = inflater.inflate(R.layout.bt_element_right_msg, parent, false);
            }else{
                row = inflater.inflate(R.layout.bt_element_left_msg, parent, false);
            }
            chatText = (TextView) row.findViewById(R.id.msgr);
            chatText.setText(chatMessageObj.message);
        }else if (chatMessageObj.tipo_contenido.equals("imagen")){
            if (chatMessageObj.left) {
                row = inflater.inflate(R.layout.bt_element_right_img_msg, parent, false);
            }else{
                row = inflater.inflate(R.layout.bt_element_left_img_msg, parent, false);
            }

            imagen = (ImageView)row.findViewById(R.id.imgSelect);
            byte[] decodedString = Base64.decode(chatMessageObj.message, Base64.DEFAULT);
            Glide.with(context)
                    .asBitmap()
                    .load(decodedString)
                    .into(imagen);
        }

        return row;
    }
}
