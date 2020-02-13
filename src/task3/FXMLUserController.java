package task3;

import java.io.IOException;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.scene.control.TableView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;


public class FXMLUserController implements Initializable {
	//objects for visualizing all the film in the table
	@FXML private TableView<Film> film_table;
	@FXML private TableColumn<Film, String> title_column;
	@FXML private TableColumn<Film, String> genre_column;
	@FXML private TableColumn<Film, Integer> price_column;
		  private ObservableList<Film> film_in_db;
	
	//objects for visualizing the film in the cart
	@FXML private TableView<Film> cart_table;
	@FXML private TableColumn<Film, String> film_cart_column;
		  private ObservableList<Film> film_in_cart;
	
	//object for retrieving logged user's informations  
	private User current_user;
		  
	//objects for visualizing the current user's rentals
	@FXML private TableView<Rental> rental_table;
	@FXML private TableColumn<Rental, String> rental_title_column;
	@FXML private TableColumn<Rental, LocalDate> rental_startDate_column;
	@FXML private TableColumn<Rental, LocalDate> rental_endDate_column;
		  private ObservableList<Rental> rentals;
	
	//objects for visualizing the films of the current user's rentals
	//@FXML private TableView<Film> rented_film_table;
	//@FXML private TableColumn<Film, String> rented_title_column;
		//  private ObservableList<Film> rented_film;
		
		
	//textfield for live search of a film by title
	@FXML private TextField search_field;	
	@FXML private TextField search_user;	
	
	//objects for user's profile
	@FXML private Label infouser_username_field;
	@FXML private Label infouser_name_field;
	@FXML private Label infouser_surname_field;
	@FXML private Label infouser_credit_field;
	@FXML private TextField infouser_email_field;
	@FXML private PasswordField infouser_newpassword_field;
	@FXML private PasswordField infouser_currentpassword_field;
	@FXML private TextField infouser_deposit_field;
	
	//objects for visualizing follower
	@FXML private TableView<User> followed_table;
	@FXML private TableColumn<User, String> username_followed_column;
		private ObservableList<User> followed;
		
	@FXML private TableView<User> all_user_table;
	@FXML private TableColumn<User, String> all_user_column;
		private ObservableList<User> all_user;
	
	//for most rented films
	@FXML private TableView<Film> most_rent_film;
	@FXML private TableColumn<Film, String> most_rent_column;
		private ObservableList<Film> top_rent_film;
		
	//for most rated films	
	@FXML private TableView<Film> top_rate_film;
	@FXML private TableColumn<Film, String> top_rate_column;
		private ObservableList<Film> top_rate_list;
	
	// film of those followed
	@FXML private TableView<Film> following_film_table;
	@FXML private TableColumn<Film, String> following_film_column;
		private ObservableList<Film> following_film;
		
		
	@FXML private TableView<Film> film_genre_table;
	@FXML private TableColumn<Film, String> film_genre_column;
		private ObservableList<Film> genre_film;
	
	public void initialize(URL url, ResourceBundle rb) {
       //ininizializing of the table 
		title_column.setCellValueFactory(new PropertyValueFactory<Film, String>("title"));
		genre_column.setCellValueFactory(new PropertyValueFactory<Film, String>("genre"));
		price_column.setCellValueFactory(new PropertyValueFactory<Film, Integer>("weeklyPrice"));
		
		
		
		//retrieving all the films from the database and putting them into the table
		film_in_db = FXCollections.observableArrayList();
		film_in_db.addAll(UserEntityManager.getFilms());
		film_table.setItems(film_in_db);
		
		//creating the cart table
		film_cart_column.setCellValueFactory(new PropertyValueFactory<Film, String> ("title"));
		film_in_cart = FXCollections.observableArrayList();
		cart_table.setItems(film_in_cart);
		
		//initializing the rent table
		rental_startDate_column.setCellValueFactory(new PropertyValueFactory<Rental, LocalDate>("startDate"));
		rental_endDate_column.setCellValueFactory(new PropertyValueFactory<Rental, LocalDate>("endDate"));
		rental_title_column.setCellValueFactory(new PropertyValueFactory<Rental, String>("title"));
		rentals = FXCollections.observableArrayList();
		rental_table.setItems(rentals);
		
		//creating the rent table for visualizing the films in the rent
		//rented_title_column.setCellValueFactory(new PropertyValueFactory<Film, String>("title"));
		//rented_film = FXCollections.observableArrayList();
		//rented_film_table.setItems(rented_film);
		
		//initializing follower table
		username_followed_column.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
		followed = FXCollections.observableArrayList();
		followed_table.setItems(followed);
		
		
		film_genre_column.setCellValueFactory(new PropertyValueFactory<Film, String>("title"));
		genre_film = FXCollections.observableArrayList();
		film_genre_table.setItems(genre_film);
		
		all_user_column.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
		all_user = FXCollections.observableArrayList();
		all_user_table.setItems(all_user);
		
		most_rent_column.setCellValueFactory(new PropertyValueFactory<Film, String>("title"));
		top_rent_film = FXCollections.observableArrayList();
		top_rent_film.addAll(UserEntityManager.getTopRentedFilms());
		most_rent_film.setItems(top_rent_film);
		
		top_rate_column.setCellValueFactory(new PropertyValueFactory<Film, String>("title"));
		top_rate_list = FXCollections.observableArrayList();
		top_rate_list.addAll(UserEntityManager.getTopRatedFilms());
		top_rate_film.setItems(top_rate_list);
		
		
		
    }  
	
	
	public void exit(MouseEvent event) throws IOException {
		Stage dialogStage = new Stage();
        Scene scene;
        Node source = (Node) event.getSource();
		 dialogStage = (Stage) source.getScene().getWindow();
         dialogStage.close();
         String resource;
         Parent root;
         
         resource = "FXMLDocument.fxml";
     	FXMLLoader loader = new FXMLLoader();
         loader.setLocation(getClass().getResource(resource));
       //passing user's informations to the new controller FXMLUserController
         root = (Parent) loader.load();
        		
        FXMLDocumentController controller = loader.getController();
        
        scene = new Scene(root);
        dialogStage.setTitle("MovieTime");
        dialogStage.setScene(scene);
        dialogStage.show(); 
	}
	
	//adding the selected film to the cart
	public void addSelectedToCart() {
		Film selectedFilm = film_table.getSelectionModel().getSelectedItem();
		if(selectedFilm == null) {
			Alert windowAlert = new Alert(AlertType.INFORMATION);
			windowAlert.setHeaderText("No movie selected!");
			windowAlert.setContentText("Select a movie to add it to the cart");
			windowAlert.setTitle("Warning");
			windowAlert.showAndWait();
		}else {
		
		if(film_in_cart.contains(selectedFilm)) {
			Alert windowAlert = new Alert(AlertType.INFORMATION);
			windowAlert.setHeaderText("Movie already in cart!");
			windowAlert.setContentText("You can not add to the cart the same movie twice");
			windowAlert.setTitle("Warning");
			windowAlert.showAndWait();
			return;
		}
			
		film_in_cart.add(selectedFilm);
		}
	}
	
	//taking all the elements in the cart and renting them
	public void buyFilmInCart() {
		if(film_in_cart.isEmpty()) {
			Alert windowAlert = new Alert(AlertType.INFORMATION);
			windowAlert.setHeaderText("Empty cart!");
			windowAlert.setContentText("You can not buy an empty cart");
			windowAlert.setTitle("Warning");
			windowAlert.showAndWait();
		}else {
			List<Film> f = new ArrayList<>();
			f = cart_table.getItems();
		
			//calculating the total price
			int tot = 0;
			for(int i = 0; i < f.size(); i++)
				tot += f.get(i).getWeeklyPrice();
		
			//verifying the user's credit
			if(current_user.getCredit()< tot) {
				//opening an alert window and cleaning the cart
				Alert windowAlert = new Alert(AlertType.INFORMATION);
				windowAlert.setHeaderText("Scarce credit!");
				windowAlert.setContentText("Refill your credit in the profile section");
				windowAlert.setTitle("Warning");
				windowAlert.showAndWait();
				film_in_cart.clear();
				return;
			}else {
				//inserting a new rental for the current user
				LocalDate stDate = LocalDate.now();
				LocalDate eDate = stDate.plusDays(7);
				
				for(int i=0; i<f.size(); i++) {
					Rental r = new Rental(current_user.getUsername(), f.get(i).getTitle(), stDate, eDate, tot);
					UserEntityManager.insertRental(r);
				}
				

				//decreasing user's credit and updating the value
				//current_user.setCredit(current_user.getCredit() - tot);
				//UserEntityManager.updateUserInfo(current_user);
		
				film_in_cart.clear();
			}
		}
		
	}	
	
	//loading current user's informations
	public void initUser(User u) {
		this.current_user = new User(u);
		following_film_column.setCellValueFactory(new PropertyValueFactory<Film, String>("title"));
		following_film = FXCollections.observableArrayList();
		following_film.addAll(UserEntityManager.getFollowingFilms(this.current_user));
		following_film_table.setItems(following_film);
		genre_film.addAll(UserEntityManager.getSuggestedGenreFilms(current_user));
	}
	
	
	//loading current user's rentals
	public void initRental() {
		rentals.clear();
		User tmp;
	
        //tmp = new User(UserEntityManager.refreshUser(current_user));
        //current_user = tmp;
        rentals.addAll(UserEntityManager.getRentals(current_user));
	}
	
	//loading current user's followed
	public void initFollowed() {
		followed.clear();
		all_user.clear();
		User tmp;
	
        //tmp = new User(UserEntityManager.refreshUser(current_user));
        //current_user = tmp;
        followed.addAll(UserEntityManager.getFollowed(current_user));
        all_user.addAll(UserEntityManager.getUsersToFollow(current_user));
        
	}
	
	public void follow() {
		User selectedUser = all_user_table.getSelectionModel().getSelectedItem();
		
		UserEntityManager.followUser(current_user, selectedUser);
		initFollowed();
	}
	
	public void setUserSearched() {
		String s = search_user.getText();
		if(s.length() > 0 )
			getUserSearched(s);
		else
			all_user_table.setItems(all_user);
	}
	
	//searching for user beginning with the string got from the field
	public void getUserSearched(String s) {
		ObservableList<User> list = FXCollections.observableArrayList();
		s = s.toLowerCase();
		String tmp;
		for(User u : this.all_user) {
			tmp = u.getUsername();
			if(tmp.startsWith(s))
				list.add(u);
		}
		all_user_table.setItems(list);
	}
	
	//showing film's details
	public void showDetails(MouseEvent event) throws IOException{
		Film film = film_table.getSelectionModel().getSelectedItem();
	    if (event.getClickCount() == 2 && film != null){
	        getFilmDetails(film);
	    }
	}
	
	public void showFollowingFilmDetails(MouseEvent event) throws IOException{
		Film film = following_film_table.getSelectionModel().getSelectedItem();
	    if (event.getClickCount() == 2 && film != null){
	        getFilmDetails(film);
	    }
	}
	
	public void showCategoryFilmDetails(MouseEvent event) throws IOException{
		Film film = film_genre_table.getSelectionModel().getSelectedItem();
	    if (event.getClickCount() == 2 && film != null){
	        getFilmDetails(film);
	    }
	}
	
	public void showTopRatedDetails(MouseEvent event) throws IOException{
		Film film = top_rate_film.getSelectionModel().getSelectedItem();
	    if (event.getClickCount() == 2 && film != null){
	        getFilmDetails(film);
	    }
	}
	
	public void showTopRatingDetails(MouseEvent event) throws IOException{
		Film film = most_rent_film.getSelectionModel().getSelectedItem();
	    if (event.getClickCount() == 2 && film != null){
	        getFilmDetails(film);
	    }
	}
	
	//getting the details for the selected film
	public void getFilmDetails( Film f )throws IOException {
		//opening a new window with a new controller
        Stage dialogStage = new Stage();
        Scene scene;
        
        String resource = "FXMLFilmDetails.fxml";
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(resource));
        
        Parent root = (Parent) loader.load();
        		
        FXMLFilmDetailsController controller = loader.getController();
        
        controller.setInfo(f, film_in_db, current_user);
        
        scene = new Scene(root);
        dialogStage.setTitle(f.getTitle());
        dialogStage.setScene(scene);
        dialogStage.show(); 
	}
	
	//live search for films by title
	public void setFilmSearched() {
		String s = search_field.getText();
		if(s.length() > 0 )
			getFilmSearched(s);
		else
			film_table.setItems(film_in_db);
	}
	
	//searching for films beginning with the string got from the field
	public void getFilmSearched(String s) {
		ObservableList<Film> list = FXCollections.observableArrayList();
		s = s.toLowerCase();
		String tmp;
		for(Film f : this.film_in_db) {
			tmp = f.getTitle().toLowerCase();
			if(tmp.startsWith(s))
				list.add(f);
		}
		film_table.setItems(list);
	}
	
	//inizializing the profile with current user's informations
	public void initUserInfo() {
		
		//refresh of user info in case one or more rental are done in this session and the user want to delete the account 
		//immediately after that
		
		//User tmp = current_user;
		
		//tmp = new User(UserEntityManager.refreshUser(current_user));
		//current_user = tmp;
		
		
		this.infouser_username_field.setText(current_user.getUsername());
		this.infouser_name_field.setText(current_user.getName());
		this.infouser_surname_field.setText(current_user.getSurname());
		this.infouser_credit_field.setText(Integer.toString(current_user.getCredit()));
		this.infouser_email_field.setText(current_user.getEmail());
		this.infouser_currentpassword_field.clear();
		this.infouser_newpassword_field.clear();
		this.infouser_deposit_field.setText(Integer.toString(0));
	}
	
	//updating user's informations
	public void updateUserInfo() {
		//checking the password from the field
		String currpass = this.infouser_currentpassword_field.getText();
		currpass = HashClass.convertToSha(currpass);
		
		if(!current_user.getPassword().equals(currpass)) {
			errorInfoUser();
			return;
		}
		
		//getting the new informations from the fields
		String mail = this.infouser_email_field.getText();
		String newpass = this.infouser_newpassword_field.getText();
		String deposit = this.infouser_deposit_field.getText();
		
		int dep = 0;
		if(deposit.matches("\\d*")) {
			dep = Integer.parseInt(deposit);
		}
		
		if(mail.equals(current_user.getEmail()) && newpass.length() == 0 && dep == 0) {
			return;
		}
		
		if(!mail.equals(current_user.getEmail()))
			current_user.setEmail(mail);
		if(newpass.length() > 0) {
			newpass = HashClass.convertToSha(newpass);
			current_user.setPassword(newpass);
		}
		if(dep > 0)
			current_user.setCredit(current_user.getCredit() + dep);
		
		UserEntityManager.updateUserInfo(current_user);
		initUserInfo();
	}
	
	//message error for wrong password
	public void errorInfoUser() {
		this.infouser_currentpassword_field.clear();
		this.infouser_currentpassword_field.setPromptText("Error, wrong password");
		this.infouser_currentpassword_field.setStyle("-fx-prompt-text-fill: red;");
	}
	
	//deleting the user's account
	public void deleteAccount() {
		//checking the password from the field
		String currpass = this.infouser_currentpassword_field.getText();
		currpass = HashClass.convertToSha(currpass);
		
		if(!current_user.getPassword().equals(currpass)) {
			errorInfoUser();
			return;
		}
		
	
		UserEntityManager.removeUser(current_user);
		
		//closing the window
		 Parent root;
	        try {
	            root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
	            Stage secondStage = new Stage();
	            secondStage.setTitle("");
	            secondStage.setScene(new Scene(root));
	            secondStage.initModality(Modality.WINDOW_MODAL);
	            Stage stage = (Stage) this.infouser_currentpassword_field.getScene().getWindow();
	            stage.close();
	            secondStage.show();
	        }catch (IOException e) {
	            e.printStackTrace();
	        }  
	}
	
	
	
}
