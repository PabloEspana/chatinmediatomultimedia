package juanmanuelco.facci.com.soschat.BLUETOOTH.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import juanmanuelco.facci.com.soschat.R;

public class FragmentChats extends Fragment{

    CardView cv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.bt_fragment_mensajes, container, false);
        cv = (CardView) vista.findViewById(R.id.cv2);
        //listarConversaciones();
        //adaptarListado(vista);
        return vista;
    }
}
