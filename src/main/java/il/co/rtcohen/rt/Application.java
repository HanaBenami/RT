package il.co.rtcohen.rt;

import il.co.rtcohen.rt.service.hashavshevet.HashavshevetSync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication()
public class Application {

	@Autowired
	HashavshevetSync hashavshevetSync;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Scheduled(fixedDelayString="${settings.hashSync.scheduleSyncFixedDelayMiliseconds}", initialDelayString="${settings.hashSync.scheduleSyncInitialDelayMiliseconds}")
	private void HashavshevetSync() {
		hashavshevetSync.syncData();
	}
}
