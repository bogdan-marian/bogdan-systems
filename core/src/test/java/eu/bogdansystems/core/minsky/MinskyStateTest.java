package eu.bogdansystems.core.minsky;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MinskyStateTest {

	@Test
	void exposesAllFourStateVariables() {
		MinskyState state = new MinskyState(0.96, 0.65, 1.2, 0.8);

		assertEquals(0.96, state.employmentRate());
		assertEquals(0.65, state.wageShare());
		assertEquals(1.2, state.privateDebtRatio());
		assertEquals(0.8, state.governmentDebtRatio());
	}

	@Test
	void statesWithSameValuesAreEqual() {
		assertEquals(new MinskyState(0.96, 0.65, 1.2, 0.8), new MinskyState(0.96, 0.65, 1.2, 0.8));
	}

	@Test
	void rejectsNegativeEmploymentRate() {
		assertThrows(IllegalArgumentException.class, () -> new MinskyState(-0.01, 0.65, 1.2, 0.8));
	}

	@Test
	void rejectsNegativeWageShare() {
		assertThrows(IllegalArgumentException.class, () -> new MinskyState(0.96, -0.01, 1.2, 0.8));
	}

	@Test
	void allowsNegativeDebtRatiosForNetCreditorSectors() {
		assertDoesNotThrow(() -> new MinskyState(0.96, 0.65, -0.3, -0.5));
	}

	@Test
	void rejectsNonFiniteValues() {
		assertThrows(IllegalArgumentException.class, () -> new MinskyState(Double.NaN, 0.65, 1.2, 0.8));
		assertThrows(IllegalArgumentException.class, () -> new MinskyState(0.96, 0.65, Double.POSITIVE_INFINITY, 0.8));
	}

}
