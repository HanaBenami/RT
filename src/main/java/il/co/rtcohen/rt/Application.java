package il.co.rtcohen.rt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import static eu.hurion.vaadin.heroku.VaadinForHeroku.forApplication;
//import static eu.hurion.vaadin.heroku.VaadinForHeroku.herokuServer;

@SpringBootApplication()
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		//herokuServer(forApplication(MainUI.class)).start();
/* https://rt2019.herokuapp.com/
git commit -am "heroku"
git push heroku master*/

	}

}
