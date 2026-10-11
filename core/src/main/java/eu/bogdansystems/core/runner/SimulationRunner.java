package eu.bogdansystems.core.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "simulation.cli.enabled", havingValue = "true")
public class SimulationRunner implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SimulationRunner.class);

	private static final int SAMPLE_TICKS = 5;

	@Override
	public void run(String... args) {
		for (long tick = 1; tick <= SAMPLE_TICKS; tick++) {
			log.info(processTick(tick));
		}
	}

	public String processTick(long tick) {
		return "[TICK " + tick + "] System state running...";
	}

}
