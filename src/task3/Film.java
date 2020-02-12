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
	private final String release_date;
	private final int weekly_price;
	private final String production_company;
	private final int budget;
	private final int revenue;
	private final String production_country;
	private final String language;
	private final String runtime;
	private final int vote_count;
	private double vote_avg;
	
	
	public Film() {
		this.idFilm=0;
		this.production_company = "";
		this.genre = null;
		this.title = "";
		this.release_date = null;
		this.weekly_price = 0;
		this.plot = "";
		this.budget = 0;
		this.revenue = 0;
		this.production_country = "";
		this.language = "";
		this.runtime = "";
		this.vote_count = 0;
		this.vote_avg=0.0;
	}
	
	public Film(int idFilm, String title, String genre, String plot, String releaseYear, int weeklyPrice, String production_company,int budget) {

		this.idFilm = idFilm;
        this.title = title;
		this.genre = genre;
		this.plot = plot;
		this.release_date = releaseYear;
		this.weekly_price = weeklyPrice;
		this.production_company = production_company;
		this.budget = budget;
		this.revenue = 0;
		this.production_country = "";
		this.language = "";
		this.runtime = "";
		this.vote_count = 0;

	}
	
	public Film( String title, String genre, String plot, String releaseYear, int weeklyPrice, String production_company, int budget) {

		this.idFilm = 0;
        this.title = title;
		this.genre = genre;
		this.plot = plot;
		this.release_date = releaseYear;
		this.weekly_price = weeklyPrice;
		this.production_company = production_company;
		this.budget = budget;
		this.revenue = 0;
		this.production_country = "";
		this.language = "";
		this.runtime = "";
		this.vote_count = 0;
	}
	
	public Film(int idFilm, String title, String genre, String plot, String releaseYear, int weeklyPrice, String production_company,int budget, double vote_avg) {

		this.idFilm = idFilm;
        this.title = title;
		this.genre = genre;
		this.plot = plot;
		this.release_date = releaseYear;
		this.weekly_price = weeklyPrice;
		this.production_company = production_company;
		this.budget = budget;
		this.vote_avg = vote_avg;
		this.revenue = 0;
		this.production_country = "";
		this.language = "";
		this.runtime = "";
		this.vote_count = 0;
	}
	

	public Film(int idFilm, String title, String genre, String plot, String releaseYear, int weeklyPrice, String production_company,int budget, int revenue,String production_country, String language, String runtime, int vote_count, double vote_avg) {

		this.idFilm = idFilm;
        this.title = title;
		this.genre = genre;
		this.plot = plot;
		this.release_date = releaseYear;
		this.weekly_price = weeklyPrice;
		this.production_company = production_company;
		this.budget = budget;
		this.revenue = revenue;
		this.production_country = production_country;
		this.language = language;
		this.runtime = runtime;
		this.vote_count = vote_count;
		this.vote_avg = vote_avg;
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
	public String getReleaseDate(){
		return this.release_date;
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
	public double getVoteAvg() {
		return this.vote_avg;
	}
	public int getRevenue(){
		return this.revenue;
	}
	public String getProductionCountry() {
		return this.production_country;
	}
	public String getLanguage() {
		return this.language;
	}
	public String getRuntime() {
		return this.runtime;
	}
	public int getVoteCount() {
		return this.vote_count;
	}
	public void setVoteAvg(double vote) {
		this.vote_avg = vote;
	}

	
	public Film(Film f){
		this.idFilm = f.getIdFilm();
        this.title = f.getTitle();
		this.genre = f.getGenre();
		this.plot = f.getPlot();
		this.release_date = f.getReleaseDate();
		this.weekly_price = f.getWeeklyPrice();
		this.production_company = f.getProductionCompany();
		this.budget = f.getBudget();
		this.revenue = f.getRevenue();
		this.production_country = f.getProductionCountry();
		this.language = f.getLanguage();
		this.runtime = f.getRuntime();
		this.vote_count = f.getVoteCount();
		this.vote_avg = f.getVoteAvg();

	}

	
	
}
