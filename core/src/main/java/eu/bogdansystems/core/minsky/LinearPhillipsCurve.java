package eu.bogdansystems.core.minsky;

/**
 * Straight-line Phillips curve: {@code Phi(a) = slope * (a - neutralEmploymentRate)}.
 * <p>
 * Wages stand still at the neutral employment rate, rise above it and fall below it.
 * With this curve the Goodwin model is exactly the Lotka-Volterra predator-prey system.
 */
public record LinearPhillipsCurve(
        double slope,
        double neutralEmploymentRate) implements PhillipsCurve {


    public LinearPhillipsCurve {
        if (!Double.isFinite(slope) || !Double.isFinite(neutralEmploymentRate)) {
            throw new IllegalArgumentException(
                    "parameters must be finite, were slope=" + slope + ", neutralEmploymentRate=" + neutralEmploymentRate);
        }
        if (slope <= 0) {
            throw new IllegalArgumentException("slope must be > 0 (wages rise with employment), was " + slope);
        }
    }

    @Override
    public double wageGrowthRate(double employmentRate) {
        return slope * (employmentRate - neutralEmploymentRate);
    }

}
