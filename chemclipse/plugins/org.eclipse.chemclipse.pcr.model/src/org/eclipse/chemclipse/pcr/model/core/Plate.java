/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.model.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.chemclipse.model.core.AbstractMeasurementInfo;

public class Plate extends AbstractMeasurementInfo implements IPlate {

	private static final long serialVersionUID = -7209280707411376156L;
	//
	private String name = "";
	private IDetectionFormat detectionFormat = null;
	private List<IDetectionFormat> detectionFormats = new ArrayList<>();
	private TreeSet<IWell> wells = new TreeSet<IWell>();

	@Override
	public List<String> getActiveChannels() {

		List<String> channels = new ArrayList<>();
		if(detectionFormat != null) {
			for(IChannelSpecification channelSpecification : detectionFormat.getChannelSpecifications()) {
				channels.add(channelSpecification.getName());
			}
		}
		return channels;
	}

	@Override
	public void setActiveChannel(int activeChannel) {

		if(activeChannel < 0) {
			for(IWell well : wells) {
				well.clearActiveChannel();
			}
		} else {
			for(IWell well : wells) {
				well.setActiveChannel(activeChannel);
			}
		}
	}

	@Override
	public List<String> getSampleSubsets() {

		Set<String> subsets = new HashSet<>();
		for(IWell well : wells) {
			subsets.add(well.getSampleSubset());
		}
		//
		List<String> sampleSubsets = new ArrayList<>(subsets);
		Collections.sort(sampleSubsets);
		if(sampleSubsets.size() == 0 || !sampleSubsets.get(0).equals(ALL_SUBSETS)) {
			sampleSubsets.set(0, ALL_SUBSETS); // All subsets
		}
		//
		return sampleSubsets;
	}

	@Override
	public void setActiveSubset(String activeSubset) {

		activeSubset = (ALL_SUBSETS.equals(activeSubset)) ? "" : activeSubset;
		//
		for(IWell well : wells) {
			well.setActiveSubset(activeSubset);
		}
	}

	public IDetectionFormat getDetectionFormat() {

		return detectionFormat;
	}

	public void setDetectionFormat(IDetectionFormat detectionFormat) {

		this.detectionFormat = detectionFormat;
		for(IWell well : wells) {
			well.applyDetectionFormat(detectionFormat);
		}
	}

	@Override
	public List<IDetectionFormat> getDetectionFormats() {

		return detectionFormats;
	}

	@Override
	public TreeSet<IWell> getWells() {

		return wells;
	}

	@Override
	public IWell getWell(int id) {

		for(IWell well : wells) {
			if(well.getPosition().getId() == id) {
				return well;
			}
		}
		return null;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public String getName() {

		return name;
	}
}
