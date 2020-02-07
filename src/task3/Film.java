package task3;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.property.*;



public class Film {
	private final int idFilm;
	private final String title;
	private final String genre;
	private final String plot;
	private final int release_year;
	private final int weekly_price;
	private final String director;
	private Set<Rental> rentalList;
	private Set<Rating> ratings;
	
	
	public Film() {
		this.idFilm=0;
		this.director="";
		this.genre="";
		this.title = "";
		this.release_year= 0;
		this.weekly_price = 0;
		this.plot = "";
	}
	
	public Film(int idFilm, String title, String genre, String plot, int releaseYear, int weeklyPrice, String director) {

		this.idFilm = idFilm;
        this.title = title;
		this.genre = genre;
		this.plot = plot;
		this.release_year = releaseYear;
		this.weekly_price = weeklyPrice;
		this.director = director;
	}
	
	public Film( String title, String genre, String plot, int releaseYear, int weeklyPrice, String director/*, List<Rental> rental, List<Rating> rating*/) {

		this.idFilm = 0;
        this.title = title;
		this.genre = genre;
		this.plot = plot;
		this.release_year = releaseYear;
		this.weekly_price = weeklyPrice;
		this.director = director;
	}

	public int getIdFilm(){
		return this.idFilm;
	}
	public String getTitle(){
		return this.title;
	}
	public String getGenre(){
		return this.genre;
	}
	public String getPlot(){
		return this.plot;
	}
	public int getReleaseYear(){
		return this.release_year;
	}
	public int getWeeklyPrice() {
		return this.weekly_price;
	}
	public String getDirector() {
		return this.director;
	}
	
	
	public Set<Rating> getRatingList(){
		return this.ratings;
	}
	
	public Set<Rental> getRentalList(){
		return this.rentalList;
	}
	

	public Film(Film f){
		this.idFilm = f.getIdFilm();
        this.title = f.getTitle();
		this.genre = f.getGenre();
		this.plot = f.getPlot();
		this.release_year = f.getReleaseYear();
		this.weekly_price = f.getWeeklyPrice();
		this.director = f.getDirector();
		
		this.ratings = new HashSet<Rating>();
		ratings.addAll(f.getRatingList());
	}

	
	
}
