/*
 * This file is part of the GeMTC software for MTC model generation and
 * analysis. GeMTC is distributed from http://drugis.org/gemtc.
 * Copyright (C) 2009-2012 Gert van Valkenhoef.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.mtc.presentation;

import java.util.List;

import org.apache.commons.collections15.BidiMap;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.model.Treatment;

public class SimulationInconsistencyWrapper<TreatmentType> extends AbstractMTCSimulationWrapper<TreatmentType, InconsistencyModel> implements InconsistencyWrapper<TreatmentType> {

	public SimulationInconsistencyWrapper(InconsistencyModel model, BidiMap<TreatmentType, Treatment> treatmentMap) {
		super(model, "Inconsistency Model", treatmentMap);
	}

	@Override
	public List<Parameter> getInconsistencyFactors() {	
		return d_nested.getInconsistencyFactors();
	}

	@Override
	public Parameter getInconsistencyVariance() {
		return d_nested.getInconsistencyStandardDeviation();
	}
}
