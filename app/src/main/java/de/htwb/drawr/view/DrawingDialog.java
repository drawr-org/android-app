package de.htwb.drawr.view;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import de.htwb.drawr.R;
import de.htwb.drawr.util.DrawingUtil;

/**
 * Created by laokoon on 12/12/16.
 */
public class DrawingDialog extends DialogFragment {

    public static final String DRAWING_SHARED_PREF_KEY = "drawing";
    public static final String SHARED_PREF_KEY_COLOR = "color";
    public static final String SHARED_PREF_KEY_STROKE = "stroke";
    public static final int SHARED_PREF_DEFAULT_COLOR = 0;
    public static final String SHARED_PREF_DEFAULT_STROKE = "normal";

    private FragmentTabHost mTabHost;
    private ViewPager viewPager;
    private ColorPickerFragment colorPickerFragment;
    private PenStrokeFragment penStrokeFragment;
    private SharedPreferences sharedPreferences;

    private OnDialogClosedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_drawing, container);

        sharedPreferences = getActivity().getSharedPreferences(DRAWING_SHARED_PREF_KEY, Context.MODE_PRIVATE);

        getDialog().setTitle(R.string.preferences);

        colorPickerFragment = new ColorPickerFragment();
        penStrokeFragment = new PenStrokeFragment();

        mTabHost = (FragmentTabHost)view.findViewById(R.id.tabs);
        mTabHost.setup(getActivity(), getChildFragmentManager());
        mTabHost.addTab(mTabHost.newTabSpec(ColorPickerFragment.TAG).setIndicator("Color"), ColorPickerFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(PenStrokeFragment.TAG).setIndicator("Stroke"), PenStrokeFragment.class, null);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if(ColorPickerFragment.TAG.equals(s)) {
                    showFragment(colorPickerFragment);
                } else if(PenStrokeFragment.TAG.equals(s)) {
                    showFragment(penStrokeFragment);
                }
            }
        });

        showFragment(colorPickerFragment);

        return view;
    }

    public void setOnDialogClosedListener(OnDialogClosedListener l) {
        listener = l;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARED_PREF_KEY_COLOR, colorPickerFragment.getColor());
        editor.putString(SHARED_PREF_KEY_STROKE, penStrokeFragment.getStroke());
        editor.commit();
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onDialogClosed();
        }

    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    public static class ColorPickerFragment extends Fragment {

        public static final String TAG = ColorPickerFragment.class.getName();
        private SharedPreferences sharedPreferences;
        private ColorPickerView mColorPicker;
        private int currentColor;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
            sharedPreferences = getActivity().getSharedPreferences(DRAWING_SHARED_PREF_KEY, Context.MODE_PRIVATE);
            setColor(sharedPreferences.getInt(SHARED_PREF_KEY_COLOR, SHARED_PREF_DEFAULT_COLOR));
            LinearLayout ll = new LinearLayout(getActivity());

            mColorPicker = new ColorPickerView(getActivity());
            mColorPicker.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mColorPicker.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
                @Override
                public void onColorChanged(int newColor) {
                    setColor(newColor);
                }
            });
            mColorPicker.setColor(currentColor);
            ll.addView(mColorPicker);
            return ll;
        }

        public int getColor() {
            return currentColor;
        }

        public void setColor(int newColor) {
            currentColor = newColor;
        }
    }

    public static class PenStrokeFragment extends Fragment {
        public static final String TAG = PenStrokeFragment.class.getName();
        private SharedPreferences sharedPreferences;
        private String[] penStrokes;
        private String currentStroke;
        private  ListView list;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            sharedPreferences = getActivity().getSharedPreferences(DRAWING_SHARED_PREF_KEY, Context.MODE_PRIVATE);
            setStroke(sharedPreferences.getString(SHARED_PREF_KEY_STROKE,
                    SHARED_PREF_DEFAULT_STROKE));
            penStrokes = getActivity().getResources().getStringArray(R.array.canvas_pen_stroke);

            LinearLayout ll = new LinearLayout(getActivity());

            list  = new ListView(getActivity());
            list.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            list.setAdapter(new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_single_choice,
                    penStrokes));

            list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            for(int i = 0; i < penStrokes.length; i++) {
                if(currentStroke.equals(penStrokes[i])) {
                    list.setItemChecked(i, true);
                }
            }

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    setStroke(penStrokes[i]);
                }
            });

            ll.addView(list);
            return ll;
        }

        public void setStroke(String newStroke) {
            currentStroke = newStroke;
        }

        public String getStroke() {
            return currentStroke;
        }
    }
}
