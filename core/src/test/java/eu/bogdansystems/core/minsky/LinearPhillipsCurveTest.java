package eu.bogdansystems.core.minsky;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LinearPhillipsCurveTest {

	/**
	 * How far apart two doubles may be and still count as equal: 1e-12 = 0.000000000001 (one trillionth).
	 * <p>
	 * Needed because a {@code double} stores numbers in binary, and most decimals (0.96, 0.90, ...) have no exact
	 * binary form, just as 1/3 has no exact decimal form. So the computer gets tiny rounding errors:
	 * <pre>
	 * 0.96 - 0.90         = 0.05999999999999994    (not 0.06)
	 * 0.5 * (0.96 - 0.90) = 0.02999999999999997    (not 0.03, off by about 0.00000000000000003)
	 * </pre>
	 * An exact {@code assertEquals(0.03, ...)} would fail on that last digit. With the tolerance the test passes
	 * whenever the result is within one trillionth of the expected value. That is far smaller than anything
	 * meaningful here: a wage growth of 0.000000000001 per year is zero for any economic purpose, yet still tens of
	 * thousands of times larger than the rounding error, so real mistakes are still caught.
	 */
	private static final double TOLERANCE = 1e-12;

	// Phi(a) = 0.5 * (a - 0.90)
	private final PhillipsCurve curve = new LinearPhillipsCurve(0.5, 0.90);

	@Test
	void wagesStandStillAtNeutralEmployment() {
		// 0.5 * (0.90 - 0.90) = 0
		assertEquals(0.0, curve.wageGrowthRate(0.90), TOLERANCE);
	}

	@Test
	void wagesRiseWhenEmploymentIsAboveNeutral() {
		// 0.5 * (0.96 - 0.90) = 0.5 * 0.06 = 0.03 -> +3% per year
		assertEquals(0.03, curve.wageGrowthRate(0.96), TOLERANCE);
	}

	@Test
	void wagesFallWhenEmploymentIsBelowNeutral() {
		// 0.5 * (0.80 - 0.90) = 0.5 * -0.10 = -0.05 -> -5% per year
		assertEquals(-0.05, curve.wageGrowthRate(0.80), TOLERANCE);
	}

	@Test
	void rejectsSlopeThatIsNotPositive() {
		// a Phillips curve must rise with employment
		assertThrows(IllegalArgumentException.class, () -> new LinearPhillipsCurve(0.0, 0.90));
		assertThrows(IllegalArgumentException.class, () -> new LinearPhillipsCurve(-0.5, 0.90));
	}

	@Test
	void rejectsNonFiniteParameters() {
		assertThrows(IllegalArgumentException.class, () -> new LinearPhillipsCurve(Double.NaN, 0.90));
		assertThrows(IllegalArgumentException.class, () -> new LinearPhillipsCurve(0.5, Double.POSITIVE_INFINITY));
	}

}
