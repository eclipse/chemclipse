/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.text.ValueFormat;

public class MassToChargeRatio extends AbstractVariable implements IMassToChargeRatio {

	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0");
	private double mz = 0;

	public static List<MassToChargeRatio> create(List<Double> massToChargeRotatios) {

		List<MassToChargeRatio> massToChargeList = new ArrayList<>();
		for(int i = 0; i < massToChargeRotatios.size(); i++) {
			massToChargeList.add(new MassToChargeRatio(massToChargeRotatios.get(i)));
		}
		return massToChargeList;
	}

	public MassToChargeRatio(Double mz) {

		super();
		this.mz = mz;
		setValue(convertValue());
		setType(IMassToChargeRatio.TYPE);
		setSelected(true);
	}

	public MassToChargeRatio(Double mz, String description) {

		this(mz);
		setDescription(description);
	}

	@Override
	public int compareTo(IVariable o) {

		if(o instanceof IMassToChargeRatio) {
			IMassToChargeRatio mz = (IMassToChargeRatio)o;
			// round internally if not already done by pre-processing
			return Integer.compare((int)Math.round(getMassToChargeRatio()), (int)Math.round(mz.getMassToChargeRatio()));
		}
		return 0;
	}

	private String convertValue() {

		return decimalFormat.format(getMassToChargeRatio());
	}

	@Override
	public double getMassToChargeRatio() {

		return mz;
	}

	@Override
	public void setMassToChargeRatio(double mz) {

		this.mz = mz;
		setValue(convertValue());
	}
}
