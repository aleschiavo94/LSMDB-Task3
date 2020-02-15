package task3;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class FXMLFilmDetailsController implements Initializable{
	//objects for retrieving logged user and searched film informations
	@FXML private Film current_film;
	@FXML private User current_user;
	
		private List<Film> list;
		
	//objects for visualizing film's informations
	@FXML private Label title_label;
	@FXML private Label genre_label;
	@FXML private Label rating_label;
	@FXML private Label budget_label;
	@FXML private Label revenue_label;
	@FXML private Label country_label;
	@FXML private Label date_label;
	@FXML private Label language_label;
	@FXML private Label runtime_label;
	@FXML private Label company_label;
	@FXML private TextArea plot_area;
	@FXML private TextField vote_field;
	@FXML private Label vote_label;
	
	//objects for rating the film
	@FXML private ComboBox<String> vote_comboBox;
		private ObservableList<String> vote_list;
	@FXML private Button submit_button;
	
	@FXML private Label list_label;
	@FXML private ListView<String> rate_list;
		  private ObservableList<String> vote;
	
	@FXML private Rectangle rectangle;

	//inizializing with the current film's informations
	public void setInfo(Film f, List<Film> list, User u) {
		this.current_user = new User(u); 
		
		this.current_film = new Film(f);
		this.list = list;
		
		title_label.setText(f.getTitle());
		genre_label.setText(f.getGenre());
		date_label.setText(f.getReleaseDate());
		budget_label.setText(Integer.toString(f.getBudget()));
		revenue_label.setText(Integer.toString(f.getRevenue()));
		country_label.setText(f.getProductionCountry());
		language_label.setText(f.getLanguage());
		runtime_label.setText(Integer.toString(f.getRuntime()) + " minutes");
		company_label.setText(f.getProductionCompany());
		rating_label.setText(f.getVoteAvg()+"/10 out of " + f.getVoteCount() + " votes");
		plot_area.setText(f.getPlot());
		
		if(!current_user.getUsername().equals("admin")) {
			rate_list.setVisible(false);
			list_label.setVisible(false);
			
			//if the current user has already rated the current film, the button is disabled
			boolean rated = UserEntityManager.searchRating(current_user, current_film);
			
			if(rated == true) {
				submit_button.setDisable(true);
			}
		}else {
			rectangle.setVisible(false);
			vote_comboBox.setVisible(false);
			submit_button.setVisible(false);
			
			vote_label.setVisible(false);
			
			rate_list.setVisible(true);
			list_label.setVisible(true);
			
			ObservableList<String> vote = FXCollections.observableArrayList();
			List<String> listVote = new ArrayList<String>(UserEntityManager.getAllRatingByFilm(current_film));
			
			for(int i = 0; i < listVote.size(); i++) {
				vote.add(listVote.get(i));
			}
			rate_list.setItems(vote);
		}
	}
	
	
	//getting the rate from the comboBox
	public void getRadioButtonVote() {
			Rating r;
			
			//getting the value from the comboBoz
			String selected = vote_comboBox.getSelectionModel().getSelectedItem().toString();
			double value = Double.parseDouble(selected);
			
			//adding the new rate
			r = new Rating(current_user, current_film, LocalDate.now(), value);
		
			UserEntityManager.insertRating(r);
			
			List<String> avg_count = new ArrayList<String>();
			avg_count.addAll(UserEntityManager.updateMovieRate(current_film, value));
			
			current_film.setVoteAvg(Double.parseDouble(avg_count.get(1)));
			current_film.setVoteCount(Integer.parseInt(avg_count.get(0)));
			
			rating_label.setText(current_film.getVoteAvg()+"/10 out of " + current_film.getVoteCount() + " votes");
			
			//disabling the button to not rate again
			submit_button.setDisable(true);
	}
	
	public void initialize(URL url, ResourceBundle rb) {
		vote_list = FXCollections.observableArrayList("1", 
				"2", "3", "4", "5", "6", "7", "8", "9", "10");
		vote_comboBox.setItems(vote_list);
    }
	
	  
}
