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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class FXMLFilmDetailsController implements Initializable{
	//objects for retrieving logged user and searched film informations
	@FXML private Film current_film;
	@FXML private User current_user;
	
		private List<Film> list;
		
	//objects for visualizing film's informations
	@FXML private Label title_label;
	@FXML private Label genre_label;
	@FXML private Label year_label;
	@FXML private Label director_label;
	@FXML private TextArea plot_area;
	@FXML private Label rating_label;
	@FXML private TextField vote_field;
	@FXML private Label vote_label;
	
	//objects for rating the film
	@FXML private ToggleGroup group;
	@FXML private RadioButton radiobutton1;
	@FXML private RadioButton radiobutton2;
	@FXML private RadioButton radiobutton3;
	@FXML private RadioButton radiobutton4;
	@FXML private RadioButton radiobutton5;
	@FXML private Button submit_button;
	
	@FXML private Label list_label;
	@FXML private ListView<String> rate_list;
		  private ObservableList<String> vote;
	

	//inizializing with the current film's informations
	public void setInfo(Film f, List<Film> list, User u) {
		
		this.current_user = new User(u); 
		
		this.current_film = new Film(f);
		this.list = list;
		
		
		title_label.setText(f.getTitle());
		genre_label.setText(f.getGenre());
		year_label.setText(Integer.toString(f.getReleaseYear()));
		director_label.setText(f.getDirector());
		plot_area.setText(f.getPlot());
		
		group = new ToggleGroup();
		
		//getting the mean rate for the current film
		Integer rating_value = getRating(f);
		rating_label.setText(rating_value.toString());
		
		//searching for all the rating for the current film
		Rating r;
		List<Rating> rating_film = new ArrayList<>(f.getRatingList());
		
		if(!current_user.getUsername().equals("admin")) {
			rate_list.setVisible(false);
			list_label.setVisible(false);
			
			radiobutton1.setToggleGroup(group);
			radiobutton2.setToggleGroup(group);
			radiobutton3.setToggleGroup(group);
			radiobutton4.setToggleGroup(group);
			radiobutton5.setToggleGroup(group);
			
			if(rating_film.size() > 0) { //if the current film has at least one rate
			
				//getting all the rates for the current film
				for (Iterator<Rating> it = rating_film.iterator(); it.hasNext(); ) {
					r = it.next();
				
					//if the current user has already rated the current film, the button is disabled
					if(r.getUser().getUsername().equals(current_user.getUsername())) {
						submit_button.setDisable(true);
					}
				}
			}
		}else {
			radiobutton1.setVisible(false);
			radiobutton2.setVisible(false);
			radiobutton3.setVisible(false);
			radiobutton4.setVisible(false);
			radiobutton5.setVisible(false);
			
			submit_button.setVisible(false);
			
			vote_label.setVisible(false);
			
			rate_list.setVisible(true);
			list_label.setVisible(true);
			
			ObservableList<String> vote = FXCollections.observableArrayList();
			for (Iterator<Rating> it = f.getRatingList().iterator(); it.hasNext(); ) {
	        	Rating rate = it.next();
	        	
	        	String s = rate.getUser().getUsername()+", "+rate.getVote();
	        	vote.add(s);
			}
			rate_list.setItems(vote);
		}
	}
	
	
	//getting the rate from the radiobutton
	public void getRadioButtonVote() {
			Rating r;

		
			//getting the value from the selected radiobutton 
			RadioButton selected = (RadioButton)group.getSelectedToggle();
			Integer value = Integer.parseInt(selected.getText());
			
			//adding the new rate
			r = new Rating(current_user, current_film, LocalDate.now(), value);
		
			UserEntityManager.insertRating(r);
			
			//updating film's informations
			current_film = UserEntityManager.refreshFilm(current_film);
			
			//calculating the new mean rate and changing the value in the label
			Integer mean = getRating(current_film);
			rating_label.setText(mean.toString());
			
			//disabling the button to not rate again
			submit_button.setDisable(true);
			list.clear();
			list.addAll(UserEntityManager.getFilms());
	}
	
	public void initialize(URL url, ResourceBundle rb) {
		
    }
	
	   //calculating and retrieving the rating given a film
	   public Integer getRating(Film f) {
		   Integer avg=0; //mean
		   Integer sum=0; //sum of rates
		   Integer n=0; //number of rates
		   
		   try {
			  for (Iterator<Rating> it = f.getRatingList().iterator(); it.hasNext(); ) {
		        	Rating r = it.next();
		            sum+=r.getVote();
		            n+=1;
		        }
			   if(n !=0) {
				   avg=sum/n;
			   }else {
				   avg=0;
			   }
		       
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("A problem occurred in retrieving the rate!");

			} 
		   return avg;
	   }
}
