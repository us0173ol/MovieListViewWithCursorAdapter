package com.clara.movielistviewwithcursoradapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

public class DatabaseManager {

	private Context context;
	private SQLHelper helper;
	private SQLiteDatabase db;

	protected static final String DB_NAME = "movies";
	protected static final int DB_VERSION = 1;
	protected static final String DB_TABLE = "ratings";

	protected static final String ID_COL = "_id";
	protected static final String MOVIE_NAME_COL = "name";
	protected static final String MOVIE_RATING_COL = "rating";
	protected static final String YEAR_RELEASED_COL = "year";
	protected static final String DATE_OF_REVIEW_COL = "reviewDate";

	private static final String DB_TAG = "DatabaseManager" ;
	private static final String SQLTAG = "SQLHelper" ;

	public DatabaseManager(Context c) {
		this.context = c;
		helper = new SQLHelper(c);
		this.db = helper.getWritableDatabase();
	}

	public void close() {
		helper.close(); //Closes the database - very important!
	}


	public Cursor getAllMovies() {
		// Fetch all data, sort by movie name
		//SELECT , FROM, GROUP BY, HAVING, ORDER BY
		Cursor cursor = db.query(DB_TABLE, null,null,null,null,null,MOVIE_NAME_COL);
		return cursor;
	}


	//Add a product and quantity to the database.
	// Returns true if movie added, false if movie is already in the database
	public boolean addMovie(String name, float rating, String year, String reviewDate) {
		ContentValues newProduct = new ContentValues();
		newProduct.put(MOVIE_NAME_COL,name);
		newProduct.put(MOVIE_RATING_COL, rating);
		newProduct.put(YEAR_RELEASED_COL, year);
		newProduct.put(DATE_OF_REVIEW_COL, reviewDate);
		try{
			db.insertOrThrow(DB_TABLE, null, newProduct);
			Log.d(DB_TAG, "Added movie: " + name + "to with rating: " + rating +
				"released in " + year + "Date of Review: " + reviewDate);
			return true;
		}catch (SQLiteConstraintException sqlce){
			Log.e(DB_TAG, "error inserting data into table. " +
				"Name: " + name + " rating: " + rating + " year released: " +
				 " Date of Review: " );
			return false;
		}
		//return false; //TODO replace with boolean reflecting success of operation

	}


	public boolean updateRating(int movieID, float rating) {
		// update the rating for a movie, by movie id.
		Log.d(DB_TAG, "About to update rating for " + movieID + " to " + rating);
		ContentValues updateVals = new ContentValues();
		updateVals.put(MOVIE_RATING_COL, rating);
		String where = ID_COL + "=?";
		String[] whereArgs = {Integer.toString(movieID)};
		int rowsMod = db.update(DB_TABLE, updateVals, where, whereArgs);
		Log.d(DB_TAG, "After update for " + movieID + " update " + rowsMod +
		" rows updated (should be 1");
		if (rowsMod == 1){
			return true;
		}else{
			return false;//e.g.if no rows exist
		}
	}


	public class SQLHelper extends SQLiteOpenHelper {
		public SQLHelper(Context c){
			super(c, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String createSQLbase = "CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT UNIQUE, %s FLOAT )";
			String createSQL = String.format(createSQLbase, DB_TABLE, ID_COL, MOVIE_NAME_COL, MOVIE_RATING_COL, YEAR_RELEASED_COL, DATE_OF_REVIEW_COL);
			db.execSQL(createSQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
			onCreate(db);
			Log.w(SQLTAG, "Upgrade table - drop and recreate it");
		}
	}
}
