package eu.bogdansystems.core.minsky;

/**
 * Straight-line Phillips curve: {@code Phi(a) = slope * (a - neutralEmploymentRate)}.
 * <p>
 * Wages stand still at the neutral employment rate, rise above it and fall below it.
 * With this curve the Goodwin model is exactly the Lotka-Volterra predator-prey system.
 */
public record LinearPhillipsCurve(double slope, double neutralEmploymentRate) implements PhillipsCurve {

	public LinearPhillipsCurve {
		throw new UnsupportedOperationException("Please implement this");
	}

	@Override
	public double wageGrowthRate(double employmentRate) {
		throw new UnsupportedOperationException("Please implement this");
	}

}
