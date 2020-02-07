package task3;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.beans.property.*;


public class Rental {
    private final int idRental;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int totalPrice;
    private User user;
    private Set<Film> listFilm;
    
    public Rental( int idN, User u, List<Film> list, LocalDate stDate, LocalDate eDate, int totp){
    	
        this.idRental = idN ;
        this.user = new User(u);
        //this.listFilm = new ArrayList<>();
        listFilm = new HashSet<Film>();
        this.listFilm.addAll(list);
       
        this.startDate = (stDate);
        this.endDate = (eDate);
        this.totalPrice = (totp); 
        
    }
    
    public Rental() {
    	this.idRental = 0;
    	this.startDate = LocalDate.now();
    	this.endDate = LocalDate.now();
    	this.totalPrice = 0;
    	
    }
    
    public Rental(User u, List<Film> list, LocalDate stDate, LocalDate eDate, int totp) {
    	this.idRental = (0);
    	this.user = new User(u);
    	listFilm = new HashSet<Film>(list);
       
        this.startDate = (stDate);
        this.endDate = (eDate);
        this.totalPrice = (totp);
        
        
    }
    
    public Rental(Rental n){
    	
        this.idRental = (n.getIdRental());
        this.user = n.getUser();
       // this.listFilm = new ArrayList<>();
        this.listFilm = new HashSet<Film>();
        this.listFilm.addAll(n.getFilmList());
        this.startDate = (n.getStartDate());
        this.endDate = (n.getEndDate());
        this.totalPrice = (n.getTotalPrice());
        
    }
    

    public int getIdRental(){
        return this.idRental;
    }
    
    
    public Set<Film> getFilmList(){
    	
        return this.listFilm;
    }
    public LocalDate getStartDate(){
        return this.startDate;
    }
    public LocalDate getEndDate(){
        return this.endDate;
    }
    public int getTotalPrice(){
        return this.totalPrice;
    }
    
    
    public User getUser() {
    	return this.user;
    }
    
}
