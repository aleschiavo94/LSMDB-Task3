package task3;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.property.*;



public class Film {
	private final int idFilm;
	private final String title;
	private final String genre;
	private final String plot;
	private final String release_year;
	private final int weekly_price;
	private final String production_company;
	private final int budget;
	private Set<Rental> rentalList;
	private Set<Rating> ratings;
	
	
	public Film() {
		this.idFilm=0;
		this.production_company="";
		this.genre=null;
		this.title = "";
		this.release_year= null;
		this.weekly_price = 0;
		this.plot = "";
		this.budget=0;
	}
	
	public Film(int idFilm, String title, String genre, String plot, String releaseYear, int weeklyPrice, String production_company,int budget) {

		this.idFilm = idFilm;
        this.title = title;
		this.genre = genre;
		this.plot = plot;
		this.release_year = releaseYear;
		this.weekly_price = weeklyPrice;
		this.production_company = production_company;
		this.budget = budget;
	}
	
	public Film( String title, String genre, String plot, String releaseYear, int weeklyPrice, String production_company, int budget) {

		this.idFilm = 0;
        this.title = title;
		this.genre = genre;
		this.plot = plot;
		this.release_year = releaseYear;
		this.weekly_price = weeklyPrice;
		this.production_company = production_company;
		this.budget = budget;
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
	public String getReleaseYear(){
		return this.release_year;
	}
	public int getWeeklyPrice() {
		return this.weekly_price;
	}
	public String getProductionCompany() {
		return this.production_company;
	}
	public int getBudget() {
		return this.budget;
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
		this.production_company = f.getProductionCompany();
		this.budget = f.getBudget();
		
		this.ratings = new HashSet<Rating>();
		ratings.addAll(f.getRatingList());
	}

	
	
}
