package il.co.rtcohen.rt;

import il.co.rtcohen.rt.app.MainUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static eu.hurion.vaadin.heroku.VaadinForHeroku.forApplication;
import static eu.hurion.vaadin.heroku.VaadinForHeroku.herokuServer;

@SpringBootApplication()
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		herokuServer(forApplication(MainUI.class)).start();
/* https://rt2019.herokuapp.com/
 git push heroku master*/

/*
		heroku deploy:jar target/rt-2.2.jar --app hana2018
*/
	}

}
