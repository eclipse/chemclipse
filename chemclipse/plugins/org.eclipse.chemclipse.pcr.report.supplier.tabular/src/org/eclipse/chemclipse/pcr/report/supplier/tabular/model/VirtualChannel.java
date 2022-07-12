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

public class VirtualChannel {

	private String subset;
	private String sample;
	private int[] sourceChannels;
	private LogicalOperator logicalOperator;
	private int targetChannel;

	public VirtualChannel(String subset, String sample, int[] sourceChannels, LogicalOperator logicalOperator, int targetChannel) {

		super();
		this.subset = subset;
		this.sample = sample;
		this.sourceChannels = sourceChannels;
		this.logicalOperator = logicalOperator;
		this.targetChannel = targetChannel;
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

	public int[] getSourceChannels() {

		return sourceChannels;
	}

	public void setSourceChannels(int[] sourceChannels) {

		this.sourceChannels = sourceChannels;
	}

	public String getSourceChannelString() {

		if(logicalOperator == LogicalOperator.AND) {
			return StringUtils.join(ArrayUtils.toObject(sourceChannels), "&");
		} else if(logicalOperator == LogicalOperator.OR) {
			return StringUtils.join(ArrayUtils.toObject(sourceChannels), "/");
		} else {
			return StringUtils.join(ArrayUtils.toObject(sourceChannels), "+");
		}
	}

	public LogicalOperator getLogicalOperator() {

		return logicalOperator;
	}

	public void setLogicalOperator(LogicalOperator logicalOperator) {

		this.logicalOperator = logicalOperator;
	}

	public int getTargetChannel() {

		return targetChannel;
	}

	public void setTargetChannel(int targetChannel) {

		this.targetChannel = targetChannel;
	}

	public void copyFrom(VirtualChannel channel) {

		if(channel != null) {
			setSubset(channel.getSubset());
			setSample(channel.getSample());
			setSourceChannels(channel.getSourceChannels());
			setLogicalOperator(channel.getLogicalOperator());
			setTargetChannel(channel.getTargetChannel());
		}
	}
}
