package eu.bogdansystems.core.minsky;

/**
 * Wage bargaining: how fast the wage rate grows, given how tight the labour market is.
 * <p>
 * Implementations are interchangeable curve shapes (linear, rational, exponential, ...).
 * The engine only depends on this interface.
 */
@FunctionalInterface
public interface PhillipsCurve {

	/**
	 * @param employmentRate employed / labour force, e.g. 0.94
	 * @return growth rate of the wage rate per year, e.g. 0.03 for 3%/year (negative = wages fall)
	 */
	double wageGrowthRate(double employmentRate);

}
