package hust.trinhnd.myappstore.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import hust.trinhnd.myappstore.R;

/**
 * Created by Trinh on 11/12/2017.
 */

public class FragmentDialogError extends android.app.DialogFragment{
    private Button btnCancle;
    private TextView contentError;
    private OnDismissListener onDismissListener;


    public static FragmentDialogError newInstance(String errorContent) {
        FragmentDialogError frag = new FragmentDialogError();
        Bundle args = new Bundle();
        args.putString("errorContent", errorContent);
        frag.setArguments(args);
        return frag;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener){
        this.onDismissListener= onDismissListener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dialog_erorr, container, false);
        setStyle(STYLE_NO_TITLE, 0);

        btnCancle = (Button) rootView.findViewById(R.id.btn_close_dialog);
        btnCancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        contentError = (TextView) rootView.findViewById(R.id.content_error);
        contentError.setText(this.getArguments().getString("errorContent"));

        return rootView;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(onDismissListener!=null){
            onDismissListener.handleDialogClose();
        }
    }

    public interface OnDismissListener{
        void handleDialogClose();
    }
}
