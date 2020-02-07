package task3;

import java.time.LocalDate;


public class Rating {
	private final int idRating;
	private final LocalDate insertDate;
	private final int vote;
    private Film film;
    private User user;
    
    
		public Rating(int idR, User u, Film f, LocalDate insDate, int vote) {
			this.idRating = idR;
			this.user = new User(u);
			this.film = new Film(f);
			this.insertDate = insDate;
			this.vote = vote;
		}
	
		public Rating(User u, Film f, LocalDate insDate, int vote) {	
			this.idRating = 0; // null value, used when inserting a new vote
			this.user = new User(u);
			this.film = new Film(f);
			this.insertDate = insDate;
			this.vote = vote;
		}

		public int getIdRating() {
			return this.idRating;
		}
		public LocalDate getDate() {
			return this.insertDate;
		}
		public Film getFilm() {
			return this.film;
		}
		public User getUser() {
			return this.user;
		}
		public int getVote() {
			return this.vote;
		}
		
		public Rating() {
			this.idRating = 0;
			this.insertDate = null;
			this.vote = 0;
		}
		
		public void setUser(User u) {
			this.user = u;
		}
		public void setFilm(Film f) {
			this.film = f;
		}
		
}
