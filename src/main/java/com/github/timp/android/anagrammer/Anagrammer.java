package com.github.timp.android.anagrammer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Anagrammer is a sample application for the usage of the Maven Android Plugin.
 * The code is trivial and not the focus of this example and therefore not really documented.
 *
 * @author Manfred Moser <manfred@simpligility.com>
 */
public class Anagrammer extends Activity {

  com.github.timp.anagram.Anagrammer anagrammer;
  TableLayout table;
  EditText inputBox;
  TextView resultsCount;
  TextView output;
  String input;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    // get all the view components
    table = (TableLayout) findViewById(R.id.Table);
    inputBox = (EditText) findViewById(R.id.anagramInput);
    resultsCount = (TextView) findViewById((R.id.resultsCount));
    output = (TextView) findViewById(R.id.outputText);

    // default the full screen to white
    table.setBackgroundColor(Color.BLACK);
    output.setMovementMethod(new ScrollingMovementMethod());

    inputBox.setOnEditorActionListener(doer());

    try {
      anagrammer = new com.github.timp.anagram.Anagrammer();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  TextView.OnEditorActionListener doer() {
    return new TextView.OnEditorActionListener() {
      public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

        EditText inputBox = (EditText) findViewById(R.id.anagramInput);

        input = inputBox.getText().toString();
        new Doer().execute(new String[]{input});

        return true;
      }

    };
  }

  private class Doer extends AsyncTask<String, Void, ArrayList<String>> {

    @Override
    protected void onPreExecute() {
      InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      mgr.hideSoftInputFromWindow(inputBox.getWindowToken(), 0);
      inputBox.setFocusableInTouchMode(false);
      table.setBackgroundColor(Color.LTGRAY);
      resultsCount.setText("Working...");
      output.setText("");
    }

    @Override
    protected ArrayList<String> doInBackground(String[] args) {
      ArrayList<String> anagrams = null;
      try {
        anagrams = anagrammer.run(input.replace(" ", ""));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return anagrams;
    }

    @Override
    protected void onPostExecute(ArrayList<String> anagrams) {
      inputBox.setText("");
      inputBox.setFocusableInTouchMode(true);
      inputBox.requestFocus();
      table.setBackgroundColor(Color.BLACK);

      String result = "";
      for (String line : anagrams) {
        result += line;
        result += "\n";
      }

      String numberOfAnagrams = ((Integer)anagrams.size()).toString();

      resultsCount.setText(numberOfAnagrams + " for '" + input + "'");
      output.setText(result);
    }


  }
}
