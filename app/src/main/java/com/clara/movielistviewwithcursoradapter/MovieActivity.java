package com.clara.movielistviewwithcursoradapter;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Date;

public class MovieActivity extends AppCompatActivity implements MovieCursorAdapter.RatingChangedListener {

	private static final String TAG = "MOVIE ACTIVITY";
	DatabaseManager dbManager;
	MovieCursorAdapter cursorListAdapter;
	TextView mreviewDate;
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
		final TextView newMovieYear = (TextView) findViewById(R.id.add_movie_year);
		final TextView newMovieReviewDate = (TextView) findViewById(R.id.add_review_date);

		final ListView movieList = (ListView) findViewById(R.id.movie_list_view);
		Cursor cursor = dbManager.getAllMovies();
		cursorListAdapter = new MovieCursorAdapter(this, cursor, true);
		movieList.setAdapter(cursorListAdapter);


		addNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = newMovieNameET.getText().toString();
				float rating = newMovieRB.getRating();
				String year = newMovieYear.getText().toString();
				String reviewDate = newMovieReviewDate.getText().toString();
				dbManager.addMovie(name, rating, year, reviewDate);
				cursorListAdapter.changeCursor(dbManager.getAllMovies());
				newMovieNameET.getText().clear();


			}
		});

		mreviewDate = (TextView) findViewById(R.id.add_review_date);
		mreviewDate.setText(mDate.toString() );

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
	}
}
