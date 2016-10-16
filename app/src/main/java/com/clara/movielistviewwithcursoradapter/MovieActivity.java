package com.clara.movielistviewwithcursoradapter;

import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MovieActivity extends AppCompatActivity implements MovieCursorAdapter.RatingChangedListener {

	private static final String TAG = "MOVIE ACTIVITY";
	DatabaseManager dbManager;
	MovieCursorAdapter cursorListAdapter;
	TextView mreviewDate;
	//use getDate to update the date of review
	public Date getDate() {
		mDate = new Date();
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	Date mDate = new Date();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie);
		//create Database Manager object
		dbManager = new DatabaseManager(this);

		Button addNew = (Button) findViewById(R.id.add_movie_button);
		final EditText newMovieNameET = (EditText) findViewById(R.id.add_movie_name);
		final RatingBar newMovieRB = (RatingBar) findViewById(R.id.add_movie_rating_bar);
		final EditText newMovieYearET = (EditText) findViewById(R.id.add_movie_year);
		//listview for the list of movies
		final ListView movieList = (ListView) findViewById(R.id.movie_list_view);
		Cursor cursor = dbManager.getAllMovies();
		cursorListAdapter = new MovieCursorAdapter(this, cursor, true);
		movieList.setAdapter(cursorListAdapter);

		//when add button is pressed
		addNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateDate();//updateDate called so review is logged when button is pressed
				String name = newMovieNameET.getText().toString();
				float rating = newMovieRB.getRating();
				String year = newMovieYearET.getText().toString();
				String reviewDate = getDate().toString();
				dbManager.addMovie(name, rating, year, reviewDate);
				cursorListAdapter.changeCursor(dbManager.getAllMovies());
				newMovieNameET.getText().clear();//clear EditText's for year and name for next entry
				newMovieYearET.getText().clear();
				updateDate();

			}
		});
		mreviewDate = (TextView) findViewById(R.id.add_review_date);//display date next to add button
		mreviewDate.setText(getDate().toString());
	}

	public void notifyRatingChanged(int movieID, float rating) {

		// Update DB, and then update the cursor for the ListView if necessary.
		dbManager.updateRating(movieID, rating);

		cursorListAdapter.changeCursor(dbManager.getAllMovies());
	}
	//Don't forget these! Close and re-open DB as Activity pauses/resumes.

	@Override
	protected void onPause(){
		super.onPause();
		dbManager.close();
	}

	@Override
	protected void onResume(){
		super.onResume();
		dbManager = new DatabaseManager(this);
	}//call when necessary to update the date
	private void updateDate() {
		mreviewDate.setText(getDate().toString());
	}
}


