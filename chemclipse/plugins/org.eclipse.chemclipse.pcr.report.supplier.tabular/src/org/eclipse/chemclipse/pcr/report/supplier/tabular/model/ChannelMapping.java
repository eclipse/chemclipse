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

public class ChannelMapping {

	private String subset;
	private int channel;
	private String label;

	public ChannelMapping(String subset, int channel, String label) {

		this.subset = subset;
		this.channel = channel;
		this.label = label;
	}

	public int getChannel() {

		return channel;
	}

	public void setChannels(int channel) {

		this.channel = channel;
	}

	public String getSubset() {

		return subset;
	}

	public void setSubset(String subset) {

		this.subset = subset;
	}

	public String getLabel() {

		return label;
	}

	public void setLabel(String label) {

		this.label = label;
	}

	public void copyFrom(ChannelMapping mapping) {

		if(mapping != null) {
			setChannels(mapping.getChannel());
			setSubset(mapping.getSubset());
			setLabel(mapping.getLabel());
		}
	}
}
