package com.example.notea;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    EditText dateEt;
    EditText timeEt;
    EditText titleEt;
    EditText descriptionEt;
    RelativeLayout noNotes;
    boolean floatOpen = false;
    boolean emptyEt = false;
    TextToSpeech tts;
    Spinner dropdown;
    String languageSelected;
    Locale locSpanish = new Locale("spa", "MEX");
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        languageSelected = "English";

        dropdown = view.findViewById(R.id.lang_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                view.getContext(),
                R.array.language_array,
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String choice = adapterView.getItemAtPosition(i).toString();
                languageSelected = choice;
                updateFragmentView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        noNotes = view.findViewById(R.id.no_notes);

        viewNotes(view);

        TextView welcomeUser = view.findViewById(R.id.welcome_back_user);
        welcomeUser.setText("Welcome back, " + AccountDatabase.name + "!");
        RelativeLayout addNoteContainer = view.findViewById(R.id.add_note_container);

        FloatingActionButton addBtn = view.findViewById(R.id.add_float);
        FloatingActionButton writeNote = view.findViewById(R.id.write_note);
        FloatingActionButton voiceNote = view.findViewById(R.id.voice_note);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!floatOpen){
                    addBtn.setRotation(45);
                    writeNote.setVisibility(View.VISIBLE);
                    voiceNote.setVisibility(View.VISIBLE);
                    floatOpen = true;
                } else {
                    addBtn.setRotation(0);
                    writeNote.setVisibility(View.INVISIBLE);
                    voiceNote.setVisibility(View.INVISIBLE);
                    floatOpen = false;
                }
            }
        });

        writeNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNoteContainer.setVisibility(View.VISIBLE);
            }
        });

        voiceNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Voice commands coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView back = view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleEt = (EditText) getView().findViewById(R.id.et_title);
                dateEt = (EditText) getView().findViewById(R.id.et_date);
                timeEt = (EditText) getView().findViewById(R.id.et_time);
                descriptionEt = (EditText) getView().findViewById(R.id.et_description);

                titleEt.getText().clear();
                dateEt.getText().clear();
                timeEt.getText().clear();
                descriptionEt.getText().clear();
                addNoteContainer.setVisibility(View.GONE);
            }
        });

        dateEt = view.findViewById(R.id.et_date);

        dateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        view.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                dateEt.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });

        timeEt = view.findViewById(R.id.et_time);
        timeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        view.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                            String minutesStr = String.valueOf(minutes);
                            String meridiem = "AM";
                            if (minutes == 0) {
                                minutesStr = "00";
                            }
                            if (hourOfDay >= 12){
                                hourOfDay -= 12;
                                meridiem = "PM";
                            }
                            timeEt.setText(hourOfDay + ":" + minutesStr + " " + meridiem);
                        }
                }, 0, 0, false);
                timePickerDialog.show();
            }
        });


        ImageView speakDesc = view.findViewById(R.id.speak_desc);
        speakDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                descriptionEt = (EditText) getView().findViewById(R.id.et_description);
                Intent speakIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speakIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speakIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                speakIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start speaking");

                try {
                    startActivityForResult(speakIntent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e){
                    Toast.makeText(getContext(), "Device not compatible with Google speech recognition", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView addNoteBtn = view.findViewById(R.id.addNoteBtn);
        addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNoteToDb(view);
                if (emptyEt){
                    return;
                }

                titleEt = (EditText) getView().findViewById(R.id.et_title);
                dateEt = (EditText) getView().findViewById(R.id.et_date);
                timeEt = (EditText) getView().findViewById(R.id.et_time);
                descriptionEt = (EditText) getView().findViewById(R.id.et_description);

                titleEt.getText().clear();
                dateEt.getText().clear();
                timeEt.getText().clear();
                descriptionEt.getText().clear();

                Toast.makeText(getContext(), "Successfully added new note!", Toast.LENGTH_SHORT).show();
                updateFragmentView();
                addNoteContainer.setVisibility(View.GONE);
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT){
            if(resultCode == Activity.RESULT_OK && data != null){
                Toast.makeText(getContext(), "Voice recognized", Toast.LENGTH_SHORT).show();
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                //String result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
                descriptionEt.setText(result.get(0));
            }
        }
    }

    private void viewNotes(View view) {
        AccountDatabase accountDatabase = new AccountDatabase(getContext());
        LinearLayout notesContainer = view.findViewById(R.id.note_parent_container);

        ArrayList<List<Object>> notesList = accountDatabase.getNotes();

        if (!notesList.isEmpty()){
            noNotes.setVisibility(View.GONE);
        }

        for(List<Object> notesInfo : notesList){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View noteInfoLayout = inflater.inflate(R.layout.notelayout, null);

            int noteId;

            TextView title = noteInfoLayout.findViewById(R.id.note_title);
            TextView sched = noteInfoLayout.findViewById(R.id.note_sched);
            TextView desc = noteInfoLayout.findViewById(R.id.note_info);

            noteId = (int) notesInfo.get(0);

            String titleStr = notesInfo.get(1).toString();
            String dateStr = notesInfo.get(2).toString();
            String timeStr = notesInfo.get(3).toString();
            String descStr = notesInfo.get(4).toString();
            String schedStr = "";
            String divisor = " | ";

            title.setText(titleStr);
            if(!dateStr.isEmpty())
                schedStr = dateStr;
            if (!dateStr.isEmpty() && !timeStr.isEmpty())
                schedStr = schedStr + divisor;
            if(!timeStr.isEmpty())
                schedStr = schedStr + timeStr;
            sched.setText(schedStr);

            desc.setText(descStr);

            ImageButton megaphone = noteInfoLayout.findViewById(R.id.megaphone);

            tts = new TextToSpeech(noteInfoLayout.getContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if (i != TextToSpeech.ERROR){
                        if (languageSelected.equals("English"))
                            tts.setLanguage(Locale.ENGLISH);
                        else if (languageSelected.equals("Japanese"))
                            tts.setLanguage(Locale.JAPANESE);
                        else if (languageSelected.equals("Chinese"))
                            tts.setLanguage(Locale.CHINESE);
                        else if (languageSelected.equals("French"))
                            tts.setLanguage(Locale.FRENCH);
                        else if (languageSelected.equals("German"))
                            tts.setLanguage(Locale.GERMAN);
                        else if (languageSelected.equals("Italian"))
                            tts.setLanguage(Locale.ITALIAN);
                        else if (languageSelected.equals("Korean"))
                            tts.setLanguage(Locale.KOREAN);
                        else if (languageSelected.equals("Taiwanese"))
                            tts.setLanguage(Locale.TAIWAN);
                        else if (languageSelected.equals("Spanish"))
                            tts.setLanguage(locSpanish);
                    }
                }
            });

            megaphone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String text = "Title: " + titleStr;

                    String sched = "";
                    if (!dateStr.isEmpty())
                        sched = sched + " on " + dateStr;
                    if (!timeStr.isEmpty())
                        sched = sched + " every " + timeStr;
                    String desc = "Description: " + descStr;

                    try {
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                        tts.speak(sched, TextToSpeech.QUEUE_ADD, null);
                        tts.speak(desc, TextToSpeech.QUEUE_ADD, null);
                    } catch (Exception e){
                        Toast.makeText(getContext(), "Device not applicable with Text to Speech", Toast.LENGTH_SHORT).show();
                    }
                }
            });



            TextView removeNote = noteInfoLayout.findViewById(R.id.remove_button);

            removeNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    accountDatabase.removeNote(noteId);
                    Toast.makeText(getContext(), "Successfully removed note!", Toast.LENGTH_SHORT).show();
                    updateFragmentView();
                }
            });

            notesContainer.addView(noteInfoLayout);
        }

    }

    public void addNoteToDb(View view) {
        titleEt = (EditText) getView().findViewById(R.id.et_title);
        dateEt = (EditText) getView().findViewById(R.id.et_date);
        timeEt = (EditText) getView().findViewById(R.id.et_time);
        descriptionEt = (EditText) getView().findViewById(R.id.et_description);

        if (TextUtils.isEmpty(titleEt.getText())){
            titleEt.setError("Type is required.");
            emptyEt = true;
        }
        if (TextUtils.isEmpty(descriptionEt.getText())){
            descriptionEt.setError("Type is required.");
            emptyEt = true;
        }
        else {
            emptyEt = false;
        }

        if (emptyEt){
            return;
        }

        AccountDatabase accountDatabase = new AccountDatabase(getContext());

        String noteTitle = titleEt.getText().toString();
        String noteDate = dateEt.getText().toString();
        String noteTime = timeEt.getText().toString();
        String noteDesc = descriptionEt.getText().toString();

        SQLiteDatabase db = accountDatabase.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(AccountDatabase.COLUMN_NTITLE, noteTitle);
        values.put(AccountDatabase.COLUMN_NDATE, noteDate);
        values.put(AccountDatabase.COLUMN_NTIME, noteTime);
        values.put(AccountDatabase.COLUMN_NINFO, noteDesc);

        db.insert("notes_" + accountDatabase.getEmail(), null, values);

        db.close();
    }

    private void updateFragmentView(){
        LinearLayout notesContainer = getView().findViewById(R.id.note_parent_container);
        notesContainer.removeAllViews();
        viewNotes(getView());
    }
}