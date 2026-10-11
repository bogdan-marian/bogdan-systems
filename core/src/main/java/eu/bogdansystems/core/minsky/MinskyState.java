package eu.bogdansystems.core.minsky;

public record MinskyState(
		double employmentRate,
		double wageShare,
		double privateDebtRatio,
		double governmentDebtRatio) {

	public MinskyState {
		requireFinite("employmentRate", employmentRate);
		requireFinite("wageShare", wageShare);
		requireFinite("privateDebtRatio", privateDebtRatio);
		requireFinite("governmentDebtRatio", governmentDebtRatio);
		requireNonNegative("employmentRate", employmentRate);
		requireNonNegative("wageShare", wageShare);
	}

	private static void requireFinite(String name, double value) {
		if (!Double.isFinite(value)) {
			throw new IllegalArgumentException(name + " must be finite, was " + value);
		}
	}

	private static void requireNonNegative(String name, double value) {
		if (value < 0) {
			throw new IllegalArgumentException(name + " must be >= 0, was " + value);
		}
	}

}
