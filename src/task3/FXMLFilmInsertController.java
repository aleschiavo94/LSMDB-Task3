package task3;

import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLFilmInsertController {
	@FXML private TextField inserted_title;
	@FXML private TextField inserted_genre;
	@FXML private DatePicker inserted_pub_year;
	@FXML private TextField inserted_weekly_price;
	@FXML private TextField inserted_production_company;
	@FXML private TextArea inserted_plot;
	@FXML private TextField inserted_budget;
	@FXML private TextField inserted_revenue;
	@FXML private TextField inserted_production_country;
	@FXML private TextField inserted_language;
	@FXML private TextField inserted_runtime;
	
		private List<Film> film_list;
	public void insertNewFilm() {
		
		String title = this.inserted_title.getText();
		String genre = this.inserted_genre.getText();
		String production_company = this.inserted_production_company.getText();
		String plot = this.inserted_plot.getText();
		String release_year = this.inserted_pub_year.getEditor().getText();
		String weekly_price = this.inserted_weekly_price.getText();
		String budget = this.inserted_budget.getText();
		String revenue = this.inserted_revenue.getText();
		String production_country = this.inserted_production_country.getText();
		String language = this.inserted_language.getText();
		String runtime = this.inserted_runtime.getText();
		
		

		int price = 0;
		int bud = 0;
		int rev = 0;
		int run = 0;
		
		if(weekly_price.matches("\\d*")) {
        	price = Integer.parseInt(weekly_price);
		}else {
			inserted_weekly_price.clear();
			inserted_weekly_price.setPromptText("Insert a number");
			inserted_weekly_price.setStyle("-fx-prompt-text-fill: red;");
        	return;
		}
		
		if(budget.matches("\\d*")) {
			bud = Integer.parseInt(weekly_price);
		}else {
			inserted_weekly_price.clear();
			inserted_weekly_price.setPromptText("Insert a number");
			inserted_weekly_price.setStyle("-fx-prompt-text-fill: red;");
        	return;
		}
		
		if(revenue.matches("\\d*")) {
			rev = Integer.parseInt(weekly_price);
		}else {
			inserted_weekly_price.clear();
			inserted_weekly_price.setPromptText("Insert a number");
			inserted_weekly_price.setStyle("-fx-prompt-text-fill: red;");
        	return;
		}
		if(runtime.matches("\\d*")) {
			run = Integer.parseInt(weekly_price);
		}else {
			inserted_weekly_price.clear();
			inserted_weekly_price.setPromptText("Insert a number");
			inserted_weekly_price.setStyle("-fx-prompt-text-fill: red;");
        	return;
		}
		
		if(genre.length() == 0 || title.length() == 0 || production_company.length() == 0 || 
				plot.length() == 0 || release_year.length()==0 ||
				production_country.length()==0 || language.length()==0) {
			inserted_title.clear();
			inserted_title.setPromptText("Fill in all the fields");
			inserted_title.setStyle("-fx-prompt-text-fill: red;");
        	return;
		}
		
		Film f = new Film(title, genre, plot, release_year, price, 
				production_company, bud, rev, production_country, 
				language,run, 0, 0.0);
		
		UserEntityManager.insertFilm(f);
		film_list.clear();
		film_list.addAll(UserEntityManager.getFilms());
		
    	Stage stage = (Stage) inserted_title.getScene().getWindow();
    	stage.close();
		
	}
	
	
	public void initList(List<Film> list) {
		this.film_list = list;
		 
		
		inserted_title.setFocusTraversable(false);
		inserted_genre.setFocusTraversable(false);
		inserted_pub_year.setFocusTraversable(false);
		inserted_weekly_price.setFocusTraversable(false);
		inserted_production_company.setFocusTraversable(false);
		inserted_plot.setFocusTraversable(false); 
		inserted_budget.setFocusTraversable(false);
		inserted_revenue.setFocusTraversable(false);
		inserted_production_country.setFocusTraversable(false);;
		inserted_language.setFocusTraversable(false);
		inserted_runtime.setFocusTraversable(false);
	}
	
}
