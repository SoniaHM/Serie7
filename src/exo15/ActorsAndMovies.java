package exo15;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;


public class ActorsAndMovies {

    public static void main(String[] args) {

        ActorsAndMovies actorsAndMovies = new ActorsAndMovies();
        Set<Movie> movies = actorsAndMovies.readMovies();
        
        // Question 1
        
        System.out.println("Question 1:");
        System.out.println("movies.size()) = " + movies.size());
        
        // Question 2
        
        System.out.println("\nQuestion 2:");
        
        long nbActors =
        movies
        	.stream()
        	.flatMap(s->s.actors().stream())
        	.distinct()
        	.count();
       
        System.out.println("Number actors : " + nbActors );
        
        // Question 3
        
        System.out.println("\nQuestion 3:");
        
        long DifferentYear =
        movies
        	.stream()
        	.map(Movie::releaseYear)
        	.distinct()
        	.count();

        System.out.println("Number of referenced year : " + DifferentYear );
        
        // Question 4
        
        System.out.println("\nQuestion 4:");
        
        int oldest =
        movies
        	.stream()
        	.mapToInt(Movie::releaseYear)
        	.min()
        	.orElseThrow();
        
        int youngest =
        movies
        	.stream()
        	.mapToInt(Movie::releaseYear)
        	.max()
        	.orElseThrow();
 
        
        System.out.println("oldest year referenced : " + oldest );
        System.out.println("youngest year referenced : " + youngest );
        
        // Question 5
        
        System.out.println("\nQuestion 5:");
        


		Map<Object, Long> YearNbMovies = movies
            	.stream()
            	.collect(Collectors.groupingBy(
            						s->s.releaseYear(),
            						Collectors.counting()));
		
		Entry<Object, Long> YearMaxMovies = YearNbMovies.entrySet()
			.stream()
			.max(Comparator.comparing(Map.Entry::getValue))
		    .orElseThrow();

		System.out.println("Year with most movies: " +YearMaxMovies.getKey() + " with " + YearMaxMovies.getValue() + " movies");
		
        // Question 6
        
        System.out.println("\nQuestion 6:");
        
        Comparator<Movie> comparators = Comparator.comparing(s->s.actors().size());
		Movie MaxActors = movies
            	.stream()
            	.max(comparators)
            	.orElseThrow();
        
        
        int nbMaxactors = movies
        	.stream()
        	.mapToInt(s->s.actors().size())
        	.max()
        	.orElseThrow();
        
        System.out.println("film with the most actors : " + MaxActors.title() + " with " + nbMaxactors + " actors");
        
        
        // Question 7
        
        System.out.println("\nQuestion 7:");
        
        Map<Actor, Long> map = 
    	        movies.stream()
    				.flatMap(movie -> movie.actors().stream())
    				.collect(
    						Collectors.groupingBy(
    								Function.identity(), 
    								Collectors.counting()
    						)
    				);
            
            Map.Entry<Actor, Long> MostPlayedActor = 
    	        map.entrySet().stream()
    		        .max(Comparator.comparing(Map.Entry::getValue))
    		        .orElseThrow();
            
            System.out.println("Actor who played the most film is " + MostPlayedActor.getKey().firstName + " " + MostPlayedActor.getKey().lastName + " with " + MostPlayedActor.getValue() + " films ");
        
            // Question 8
            
            System.out.println("\nQuestion 8:");
            
            Map.Entry<Actor, Long> maxEntry = 
        	        movies.stream()
        				.flatMap(movie -> movie.actors().stream())
        				.collect(
        						Collectors.groupingBy(
        								Function.identity(), 
        								Collectors.counting()
        						)
        				).entrySet().stream()
        		        .max(Comparator.comparing(Map.Entry::getValue))
        		        .orElseThrow();
                
                Collector<Movie, ?, Entry<Actor, Long>> myAwesomeCollector = 
                		Collectors.collectingAndThen(
        						Collectors.flatMapping(
        								movie -> movie.actors().stream(), 
        								Collectors.groupingBy(
        										Function.identity(), 
        										Collectors.counting()
        								)
        						), 
        						m -> m.entrySet().stream()
        					    .max(Comparator.comparing(Map.Entry::getValue))
        					    .orElseThrow()
        					);
        		Map.Entry<Actor, Long> maxEntry2 = 
                movies.stream().collect(myAwesomeCollector);
                System.out.println(" With Collectors :The actor that played the most movies is " + maxEntry2.getKey().firstName + " " + maxEntry2.getKey().lastName + " with " + maxEntry2.getValue() + " movies.");
                
                
                Map<Integer, Entry<Actor, Long>> mapPerYear = movies.stream()
                	.collect(
                			Collectors.groupingBy(
                					Movie::releaseYear, 
                					myAwesomeCollector
                			)
                	);
                
                Entry<Integer, Entry<Actor, Long>> entry2 = 
                		mapPerYear.entrySet().stream()
        		        	.max(Comparator.comparing(e -> e.getValue().getValue()))
        		        	.orElseThrow();
                System.out.println("\nThe actor that played the most movies in a year is " + entry2.getValue().getKey().firstName + " " + entry2.getValue().getKey().lastName + " during the year " + entry2.getKey() + " with " + entry2.getValue().getValue() + " movies");
        
                //Question 9
                //System.out.println("\nQuestion 9:");
                
        		Comparator<Actor> Names =Comparator.comparing(Actor::lastName).thenComparing(Comparator.comparing(Actor::firstName));

                
                
    }

    public Set<Movie> readMovies() {

        Function<String, Stream<Movie>> toMovie =
                line -> {
                    String[] elements = line.split("/");
                    String title = elements[0].substring(0, elements[0].lastIndexOf("(")).trim();
                    String releaseYear = elements[0].substring(elements[0].lastIndexOf("(") + 1, elements[0].lastIndexOf(")"));
                    if (releaseYear.contains(",")) {
                        // Movies with a coma in their title are discarded
                    	int indexOfcoma = releaseYear.indexOf(",");
                    	releaseYear = releaseYear.substring(0,indexOfcoma);
                        //return Stream.empty();
                    }
                    Movie movie = new Movie(title, Integer.valueOf(releaseYear));


                    for (int i = 1; i < elements.length; i++) {
                        String[] name = elements[i].split(", ");
                        String lastName = name[0].trim();
                        String firstName = "";
                        if (name.length > 1) {
                            firstName = name[1].trim();
                        }

                        Actor actor = new Actor(lastName, firstName);
                        movie.addActor(actor);
                    }
                    return Stream.of(movie);
                };

        try (FileInputStream fis = new FileInputStream("files/movies-mpaa.txt.gz");
             GZIPInputStream gzis = new GZIPInputStream(fis);
             InputStreamReader reader = new InputStreamReader(gzis);
             BufferedReader bufferedReader = new BufferedReader(reader);
             Stream<String> lines = bufferedReader.lines();
        ) {

            return lines.flatMap(toMovie).collect(Collectors.toSet());

        } catch (IOException e) {
            System.out.println("e.getMessage() = " + e.getMessage());
        }

        return Set.of();
    }
}
