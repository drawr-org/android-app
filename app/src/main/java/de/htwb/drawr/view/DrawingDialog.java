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
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import de.htwb.drawr.R;
import de.htwb.drawr.util.DrawingUtil;
import de.htwb.drawr.util.PenSettings;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static de.htwb.drawr.util.PenSettings.*;

/**
 * Created by laokoon on 12/12/16.
 */
public class DrawingDialog extends DialogFragment {

    private FragmentTabHost mTabHost;
    private ViewPager viewPager;
    private ColorPickerFragment colorPickerFragment;
    private ToolsFragment toolsFragment;

    private static PenSettings penSettings;

    private OnDialogClosedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_drawing, container);

        getDialog().setTitle(R.string.preferences);

        penSettings = PenSettings.getInstance();

        colorPickerFragment = new ColorPickerFragment();
        toolsFragment = new ToolsFragment();


        mTabHost = (FragmentTabHost)view.findViewById(R.id.tabs);
        mTabHost.setup(getActivity(), getChildFragmentManager());
        mTabHost.addTab(mTabHost.newTabSpec(ColorPickerFragment.TAG).setIndicator(getContext()
                .getString(R.string.dialog_title_color)), ColorPickerFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(ToolsFragment.TAG).setIndicator(getContext()
                .getString(R.string.dialog_title_tools)), ToolsFragment.class, null);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if(ColorPickerFragment.TAG.equals(s)) {
                    showFragment(colorPickerFragment);
                } else if (ToolsFragment.TAG.equals(s)) {
                    showFragment(toolsFragment);
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

        penSettings.setColor(colorPickerFragment.getColor());
        penSettings.setStroke(colorPickerFragment.getStroke());
        penSettings.setTool(toolsFragment.getTool());
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
        private int currentColor;
        private int stroke;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dailog_drawing_pen, container, false);

            ColorPickerView colorPickerView = (ColorPickerView)view.findViewById(R.id.colorpicker);
            setColor(penSettings.getColor());
            colorPickerView.setColor(currentColor);
            colorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
                @Override
                public void onColorChanged(int newColor) {
                    setColor(newColor);
                }
            });

            SeekBar seekBar = (SeekBar)view.findViewById(R.id.stroke_bar);
            setStroke(penSettings.getStroke());
            seekBar.setProgress(stroke);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    stroke = progress+1;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
            return view;
        }

        public int getColor() {
            return currentColor;
        }

        public void setColor(int newColor) {
            currentColor = newColor;
        }

        public int getStroke() {
            return stroke;
        }

        public void setStroke(int newStroke) {
            stroke = newStroke;
        }
    }

    public static class ToolsFragment extends Fragment {
        public static final String TAG = ToolsFragment.class.getName();
        private  ListView list;
        private static String[] tools;
        private PenSettings.Tool currentTool;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            LinearLayout ll = new LinearLayout(getActivity());

            tools = getActivity().getResources().getStringArray(R.array.canvas_pen_tools);
            currentTool = penSettings.getTool();

            list  = new ListView(getActivity());
            list.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            list.setAdapter(new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_single_choice,
                    tools));
            list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

            for(int i = 0; i < tools.length; i++) {
                if (tools[i].toUpperCase().equals(currentTool.name().toUpperCase())) {
                    list.setItemChecked(i, true);
                    break;
                }
            }

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    for (PenSettings.Tool penTool :PenSettings.Tool.values()) {
                        if(tools[position].toUpperCase().equals(penTool.name().toUpperCase())) {
                            currentTool = penTool;
                            break;
                        }
                    }
                }
            });

            ll.addView(list);
            return ll;
        }

        public PenSettings.Tool getTool() {
            return currentTool;
        }
    }
}
