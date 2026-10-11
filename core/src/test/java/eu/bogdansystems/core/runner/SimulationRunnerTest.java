package eu.bogdansystems.core.runner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulationRunnerTest {

	private final SimulationRunner runner = new SimulationRunner();

	@Test
	void processTickReturnsFormattedTickLine() {
		assertEquals("[TICK 1] System state running...", runner.processTick(1));
	}

}
