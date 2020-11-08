# MovieTime
The project consists in an online movie rental application. To use the applica-
tion, the user must sign up to the system through the registration form. Once
registered, the user can sign in and visualize the list of available movies, choose
the ones he is interested in and put them in the cart for rental. It is also possible
to search for a movie by its title.
Each rented film remains available to the user for a week, at the end of this
period the film is automatically returned.
If the user credit isn't enough, the system denies the operation thus he needs to
top up his credit. Moreover, users can vote a movie with a score varying from
1 to 10 and see the film's details.
The user can also follow some other users in the specific section.
The system, moreover, provides a list of suggested movies by genre based on
the user's most recent rented movie, the rank composed by rated movies, the
rank composed by rented movies and the most recent rented movies by the users
followed.

## Main actors
1. **Ordinary User**:
A certain user who wants to rent a movie, first must sign up to the system
submitting his info into the registration form. Once registered, the user can sign
in using his credentials and visualize the list of available movies, among these
he can pick the ones he is interested in and put them in the cart for rental.
A customer can even check for the availability of a specific movie searching it
through the system by title. Moreover, the user can visualize the film's details
clicking on its title in the table and a window will show all the specifics.
Each rented film remains available to the user for a week, at the end of this
period the film is automatically returned. A film has a certain cost per week
which is detracted from the customer's credit when the movie is rented: if the
user credit isn't enough the system denies the operation, thus the user needs to
top up his credit providing his credit card first.
Moreover, the system offers the rating feature to its users: a film can be voted
with a score varying from 1 to 10, each of these votes equally contributes to the
average score of the film which is automatically calculated by the application.
The user can also visualize a list of all users registered to the application,
search for them by username and follow some of them clicking the Follow button.
Additionally, on the right of the window, he can visualize the list of followed
users.
In the Suggested movies section, the system provides four lists of movies
proposed considering the most recent rented movies by the followed users, the
most recent rented movie's genre by the user himself, the top rated movies and
the top rented movies.
2. **Administrator user**:
The movie rental system is managed by an administrator user. The admin has
the possibility to perform the necessary operations in order to manage the film
database. In order to access to the system, the admin must provide its privileged
credentials.
In particular, the administrator can visualize all the available movies and
for each of these he can visualize the details. Furthermore, he can search for a
certain movie by title. The admin can even add new movies to the database.
As far as the rentals handling is concerned, the administrator can visualize
the rentals history checking for all rentals occurred in the past. Moreover,
the system provides the admin with the possibility to visualize all the rentals
occurred within a certain time interval: he can specify a starting date and an
ending date. The system computes and displays the number of rentals occurred
in the requested period, and the total expenditure for these.
Regarding the user management, the system provides the administrator with
the features which follow. The system admin can visualize the list of all the user
currently registered to the application, on top of this, he can search for a specific
user by username. Once the admin has found a user, he can visualize his account
details and eventually delete the user.

## Use case
![Use case image](https://github.com/elenaveltroni/Task3/blob/master/UseCase.jpg?raw=true)

## Software architecture
The application data is organized in graph database and interacts with it through
the Java API for Neo4j.
![Software architecture image](https://github.com/elenaveltroni/Task3/blob/master/ArchitectureSchema.png?raw=true)
