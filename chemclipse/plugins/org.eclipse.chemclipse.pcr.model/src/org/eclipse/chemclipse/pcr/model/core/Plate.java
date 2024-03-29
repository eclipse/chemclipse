/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - expose the active channel
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.model.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.chemclipse.model.core.AbstractMeasurementInfo;
import org.eclipse.chemclipse.pcr.model.l10n.Messages;

public class Plate extends AbstractMeasurementInfo implements IPlate {

	private static final long serialVersionUID = -7209280707411376156L;
	//
	private String name = "";
	private IDetectionFormat detectionFormat = null;
	private List<IDetectionFormat> detectionFormats = new ArrayList<>();
	private TreeSet<IWell> wells = new TreeSet<>();
	private int activeChannel;

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
	public int getActiveChannel() {

		return activeChannel;
	}

	@Override
	public void setActiveChannel(int activeChannel) {

		this.activeChannel = activeChannel;
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
		if(sampleSubsets.isEmpty() || !sampleSubsets.get(0).equals(Messages.allSubsets)) {
			sampleSubsets.set(0, Messages.allSubsets); // All subsets
		}
		//
		return sampleSubsets;
	}

	@Override
	public void setActiveSubset(String activeSubset) {

		activeSubset = (Messages.allSubsets.equals(activeSubset)) ? "" : activeSubset;
		//
		for(IWell well : wells) {
			well.setActiveSubset(activeSubset);
		}
	}

	@Override
	public IDetectionFormat getDetectionFormat() {

		return detectionFormat;
	}

	@Override
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

	@Override
	public IPlate makeDeepCopy() {

		IPlate plate = new Plate();
		for(IWell well : getWells()) {
			plate.getWells().add(well.makeDeepCopy());
		}
		return plate;
	}
}
