package il.co.rtcohen.rt;

import il.co.rtcohen.rt.dal.bl.hashavshevet.HashavshevetSync;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Scheduled(fixedDelay = 60 * 60 * 1000, initialDelay = 1000) // every hour? TODO: configuration parameters
	private void HashavshevetSync() {
//		hashavshevetSync.syncData(true, null); // TODO
//		hashavshevetSync.syncData(false, 21333);
//		hashavshevetSync.syncData(false, 2305);
		hashavshevetSync.syncData(false, 2716, true);
		hashavshevetSync.syncData(true, 2326, true);
		hashavshevetSync.syncData(false, 20637, true);
		hashavshevetSync.syncData(true, 20637, true);
//		hashavshevetSync.syncData(false, 2979);
//		hashavshevetSync.syncData(false, 20823);
	}
}
