package org.drugis.mtc.parameterization;

import org.drugis.mtc.model.Treatment;

/**
 * Represents a relative effect parameter that is 'basic' to the parameterization. 
 */
public class BasicParameter implements NetworkParameter, Comparable<BasicParameter> {
	private final Treatment d_base;
	private final Treatment d_subj;

	/**
	 * Basic parameter representing the effect of 'subj' as compared to 'base'.
	 * @param base The baseline (comparator) treatment.
	 * @param subj The subject of the comparison.
	 */
	public BasicParameter(Treatment base, Treatment subj) {
		d_base = base;
		d_subj = subj;	
	}

	@Override
	public String getName() {
		return "d." + d_base.getId() + "." + d_subj.getId();
	}
	
	public Treatment getBaseline() {
		return d_base;
	}
	
	public Treatment getSubject() {
		return d_subj;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof BasicParameter) {
			BasicParameter other = (BasicParameter) o;
			return d_base.equals(other.d_base) && d_subj.equals(other.d_subj);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return 31 * d_base.hashCode() + d_subj.hashCode();
	}

	@Override
	public int compareTo(BasicParameter other) {
		int c1 = TreatmentComparator.INSTANCE.compare(d_base, other.d_base);
		return c1 == 0 ? TreatmentComparator.INSTANCE.compare(d_subj, other.d_subj) : c1;
	}
}