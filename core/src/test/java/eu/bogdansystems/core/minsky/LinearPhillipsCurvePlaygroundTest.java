package eu.bogdansystems.core.minsky;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Playground: the Phillips curve in action, told as a story in three parts.
 * <ol>
 *   <li>One worker's salary: what the curve means for a single pay cheque.</li>
 *   <li>The workers' slice of the pie (wage share): raises compared with productivity growth.</li>
 *   <li>The feedback loop: why the economy needs the curve at all.</li>
 * </ol>
 * All examples use the same curve: {@code Phi(a) = 0.5 * (a - 0.90)}.
 * Background: {@code my-notes/001-bogdan-systems/000-knoledge/001-philips-curve.md}.
 */
class LinearPhillipsCurvePlaygroundTest {

	/**
	 * Two doubles count as equal when they differ by less than 1e-12 = 0.000000000001 (one trillionth).
	 * Doubles carry tiny binary rounding errors (0.96 - 0.90 = 0.05999999999999994), so exact equality would fail.
	 * Full explanation on {@code TOLERANCE} in {@link LinearPhillipsCurveTest}.
	 */
	private static final double TOLERANCE = 1e-12;

	private static final double PRODUCTIVITY_GROWTH = 0.02;   // alpha: each worker produces 2% more per year
	private static final double LABOUR_FORCE_GROWTH = 0.01;   // beta: 1% more people available to work each year
	private static final double DEPRECIATION = 0.05;          // delta: 5% of machines wear out each year
	private static final double CAPITAL_OUTPUT_RATIO = 3.0;   // nu: 3 euros of machines make 1 euro of output a year

	// Equilibrium employment: the rate where raises exactly match productivity, 0.5 * (a* - 0.90) = 0.02 -> a* = 0.94
	private static final double EQUILIBRIUM_EMPLOYMENT = 0.94;

	private final PhillipsCurve curve = new LinearPhillipsCurve(0.5, 0.90);

	// ---------------------------------------------------------------------------------------------------------------
	// Part 1: one worker's salary
	// A worker earns 1000 euros per month. Next year's salary = 1000 * (1 + Phi(a)).
	// ---------------------------------------------------------------------------------------------------------------

	@Test
	void tightLabourMarketGivesTheWorkerARaise() {
		// 96% employed: employers compete for the few free workers. Phi = 0.5 * 0.06 = +3%
		assertEquals(1030.0, salaryNextYear(1000.0, 0.96), TOLERANCE);
	}

	@Test
	void recessionCutsTheWorkersSalary() {
		// 80% employed: many people want the job. Phi = 0.5 * -0.10 = -5%
		assertEquals(950.0, salaryNextYear(1000.0, 0.80), TOLERANCE);
	}

	@Test
	void neutralEmploymentKeepsTheSalaryFlat() {
		// 90% employed: neither side has the upper hand. Phi = 0
		assertEquals(1000.0, salaryNextYear(1000.0, 0.90), TOLERANCE);
	}

	@ParameterizedTest(name = "employment {0} -> wage growth {1} per year")
	@CsvSource({
			// employment, wage growth per year
			"0.80,        -0.050",   // deep recession: wages fall 5%
			"0.85,        -0.025",
			"0.90,         0.000",   // neutral
			"0.94,         0.020",   // equilibrium: raises = productivity growth
			"0.96,         0.030",
			"1.00,         0.050",   // everyone employed: +5% (a straight line, so no explosion here)
	})
	void tighterLabourMarketAlwaysMeansBiggerRaises(double employmentRate, double expectedWageGrowth) {
		assertEquals(expectedWageGrowth, curve.wageGrowthRate(employmentRate), TOLERANCE);
	}

	// ---------------------------------------------------------------------------------------------------------------
	// Part 2: the workers' slice of the pie (wage share w = wages / output)
	// Workers get a bigger slice only if their raise beats productivity growth (2%/year):
	// w_next_year = w + w * (Phi(a) - 0.02)
	// ---------------------------------------------------------------------------------------------------------------

	@Test
	void wageShareGrowsWhenRaisesBeatProductivity() {
		// 96% employed: raise 3%, productivity 2% -> slice grows 1%: 0.60 * 1.01 = 0.606
		assertEquals(0.606, wageShareNextYear(0.60, 0.96), TOLERANCE);
	}

	@Test
	void wageShareStaysPutAtEquilibriumEmployment() {
		// 94% employed: raise 2%, productivity 2% -> slice unchanged
		assertEquals(0.60, wageShareNextYear(0.60, EQUILIBRIUM_EMPLOYMENT), TOLERANCE);
	}

	@Test
	void wageShareShrinksWhenRaisesLagProductivity() {
		// 90% employed: raise 0%, productivity 2% -> slice shrinks 2%: 0.60 * 0.98 = 0.588
		assertEquals(0.588, wageShareNextYear(0.60, 0.90), TOLERANCE);
	}

	// ---------------------------------------------------------------------------------------------------------------
	// Part 3: the feedback loop (a preview of the Goodwin model, no debt yet)
	// Profits (1 - w) are invested; investment drives growth; growth drives employment:
	//   employment growth = (1 - w) / nu - alpha - beta - delta
	//   wage share growth = Phi(a) - alpha
	// ---------------------------------------------------------------------------------------------------------------

	@Test
	void withoutPhillipsCurveEmploymentRunsPast100Percent() {
		// Wages ignore the labour market: they always grow exactly with productivity (a lambda is a PhillipsCurve too).
		// Profits are high (w = 0.70), so firms keep investing and hiring, and nothing ever pushes back.
		PhillipsCurve wagesIgnoreJobs = employmentRate -> PRODUCTIVITY_GROWTH;

		Economy economy = simulate(new Economy(0.96, 0.70), wagesIgnoreJobs, 3.0);

		// After 3 years more than 100% of workers are employed: impossible, the model has broken down.
		assertTrue(economy.employmentRate() > 1.0, "employment " + economy.employmentRate() + " should exceed 1.0");
	}

	@Test
	void withPhillipsCurveEmploymentCyclesAroundEquilibrium() {
		// Same economy, but now wages react to employment. Start just above equilibrium (a = 0.96, w = 0.76).
		Economy economy = new Economy(0.96, 0.76);
		double lowestEmployment = economy.employmentRate();
		double highestEmployment = economy.employmentRate();
		int boomsStarted = 0; // times employment rose back above equilibrium

		System.out.println(" year | employment | wage share | wage growth Phi(a)");
		for (int year = 0; year <= 60; year++) {
			if (year % 3 == 0) {
				double a = economy.employmentRate();
				double phi = curve.wageGrowthRate(a);
				System.out.printf(" %4d |   %.4f   |   %.4f   |   %+.4f%n", year, a, economy.wageShare(), phi);
			}
			Economy next = simulate(economy, curve, 1.0);
			if (economy.employmentRate() < EQUILIBRIUM_EMPLOYMENT && next.employmentRate() >= EQUILIBRIUM_EMPLOYMENT) {
				boomsStarted++;
			}
			lowestEmployment = Math.min(lowestEmployment, next.employmentRate());
			highestEmployment = Math.max(highestEmployment, next.employmentRate());
			economy = next;
		}

		// The curve is a restoring force: employment swings between about 92% and 96%, never runs away,
		// and a new boom starts roughly every 18 years.
		assertTrue(lowestEmployment > 0.91 && lowestEmployment < 0.93, "lowest employment " + lowestEmployment);
		assertTrue(highestEmployment > 0.95 && highestEmployment < 0.97, "highest employment " + highestEmployment);
		assertEquals(3, boomsStarted);
	}

	@Test
	void linearCurveHasNoHardWallAt100PercentEmployment() {
		// Known weakness of the straight line: start far from equilibrium (w = 0.70, very profitable) and the boom
		// overshoots. Wages only rise +5%/year even at 100% employment, too weak to stop hiring in time.
		// A convex curve (wages exploding near a = 1) would prevent this; that is a later TDD cycle.
		Economy economy = new Economy(0.96, 0.70);
		double highestEmployment = economy.employmentRate();
		for (int year = 0; year < 10; year++) {
			economy = simulate(economy, curve, 1.0);
			highestEmployment = Math.max(highestEmployment, economy.employmentRate());
		}

		assertTrue(highestEmployment > 1.0, "highest employment " + highestEmployment + " should briefly exceed 1.0");
	}

	// ---------------------------------------------------------------------------------------------------------------
	// Helpers
	// ---------------------------------------------------------------------------------------------------------------

	private double salaryNextYear(double salary, double employmentRate) {
		return salary * (1 + curve.wageGrowthRate(employmentRate));
	}

	private double wageShareNextYear(double wageShare, double employmentRate) {
		return wageShare + wageShare * (curve.wageGrowthRate(employmentRate) - PRODUCTIVITY_GROWTH);
	}

	/** Employment rate and wage share: the two variables of the Goodwin model (no debt, no government). */
	private record Economy(double employmentRate, double wageShare) {
	}

	/** Advances the economy by {@code years}, in small Euler steps of 0.01 year (about 3.65 days). */
	private static Economy simulate(Economy start, PhillipsCurve phillipsCurve, double years) {
		double dt = 0.01;
		double a = start.employmentRate();
		double w = start.wageShare();
		for (int step = 0; step < Math.round(years / dt); step++) {
			double employmentGrowth = (1 - w) / CAPITAL_OUTPUT_RATIO
					- PRODUCTIVITY_GROWTH - LABOUR_FORCE_GROWTH - DEPRECIATION;
			double wageShareGrowth = phillipsCurve.wageGrowthRate(a) - PRODUCTIVITY_GROWTH;
			a += a * employmentGrowth * dt;
			w += w * wageShareGrowth * dt;
		}
		return new Economy(a, w);
	}

}
