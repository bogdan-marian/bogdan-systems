package eu.bogdansystems.core.minsky;

public record MinskyState(
		double employmentRate,
		double wageShare,
		double privateDebtRatio,
		double governmentDebtRatio) {

	public MinskyState {
		throw new UnsupportedOperationException("Please implement this");
	}

}
