/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.model;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class WellMapping {

	private String subset;
	private String sample;
	private int[] channels;
	private int cutoff;
	private String positive;
	private String negative;

	public WellMapping(String subset, String sample, int[] channels, int cutoff, String positive, String negative) {

		this.subset = subset;
		this.sample = sample;
		this.channels = channels;
		this.cutoff = cutoff;
		this.positive = positive;
		this.negative = negative;
	}

	public String getSubset() {

		return subset;
	}

	public void setSubset(String subset) {

		this.subset = subset;
	}

	public String getSample() {

		return sample;
	}

	public void setSample(String sample) {

		this.sample = sample;
	}

	public int[] getChannels() {

		return channels;
	}

	public String getChannelString() {

		return StringUtils.join(ArrayUtils.toObject(channels), "+");
	}

	public void setChannels(int[] channels) {

		this.channels = channels;
	}

	public int getCutoff() {

		return cutoff;
	}

	public void setCutoff(int cutoff) {

		this.cutoff = cutoff;
	}

	public String getPositive() {

		return positive;
	}

	public void setPositive(String positive) {

		this.positive = positive;
	}

	public String getNegative() {

		return negative;
	}

	public void setNegative(String negative) {

		this.negative = negative;
	}

	public void copyFrom(WellMapping mapping) {

		if(mapping != null) {
			setSubset(mapping.getSubset());
			setSample(mapping.getSample());
			setChannels(mapping.getChannels());
			setCutoff(mapping.getCutoff());
			setPositive(mapping.getPositive());
			setNegative(mapping.getNegative());
		}
	}

	@Override
	public String toString() {

		return getSample() + " | " + getChannelString() + " | " + getSubset() + " | " + getCutoff() + " | " + getPositive() + " | " + getNegative();
	}
}
