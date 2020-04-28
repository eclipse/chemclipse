/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add the PeakPostion interface, extract Classifiable interface, implement ISignal, add getName
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.quantitation.IInternalStandard;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.support.IIntegrationConstraints;

public interface IPeak extends ITargetSupplier, PeakPosition, Classifiable, ISignal {

	/**
	 * This comparator compares peaks based on the RT at the maximum of the intensity of the peak model
	 */
	public static final Comparator<IPeak> COMPARATOR_RT_MAX = (o1, o2) -> Integer.compare(o1.getPeakModel().getRetentionTimeAtPeakMaximum(), o2.getPeakModel().getRetentionTimeAtPeakMaximum());
	/**
	 * This comparator compares peaks based on the RT at the start of the intensity of the peak model
	 */
	public static final Comparator<IPeak> COMPARATOR_RT_START = (o1, o2) -> Integer.compare(o1.getPeakModel().getStartRetentionTime(), o2.getPeakModel().getStartRetentionTime());

	IPeakModel getPeakModel();

	/**
	 * Returns the modelDescription of the peak.<br/>
	 * The modelDescription is given as:<br/>
	 * TIC - if all ions are considered<br/>
	 * XIC - if some ions are subtracted<br/>
	 * <br/>
	 * E.g.:<br/>
	 * "TIC"<br/>
	 * "+199-70"<br/>
	 * 
	 * @return String
	 */
	String getModelDescription();

	/**
	 * Sets a description of the peak model.<br/>
	 * See also getModel();
	 * 
	 * @param modelDescription
	 */
	void setModelDescription(String modelDescription);

	/**
	 * Returns the peak type.<br/>
	 * It is a value which defines the type of the peak start and end point.<br/>
	 * BB - baseline baseline<br/>
	 * BV - baseline valley<br/>
	 * ... <br/>
	 * 
	 * @return PeakType
	 */
	@Override
	PeakType getPeakType();

	/**
	 * Sets the peak type.<br/>
	 * 
	 * @param peakType
	 */
	void setPeakType(PeakType peakType);

	/**
	 * Returns the number of assumed hidden peaks.
	 * If there's no hint, 0 will be returned.
	 * 
	 * @return int
	 */
	int getSuggestedNumberOfComponents();

	/**
	 * Sets the number of suggested hidden peaks.
	 * 
	 * @param suggestedNumberOfComponents
	 */
	void setSuggestedNumberOfComponents(int suggestedNumberOfComponents);

	/**
	 * Returns the integrator description.
	 * 
	 * @return String
	 */
	String getIntegratorDescription();

	/**
	 * Sets the integrator description.
	 * 
	 * @param integratorDescription
	 */
	void setIntegratorDescription(String integratorDescription);

	/**
	 * Returns the peak detector description.
	 * 
	 * @return String
	 */
	String getDetectorDescription();

	/**
	 * Sets the peak detector description.
	 * 
	 * @param detectorDescription
	 */
	void setDetectorDescription(String detectorDescription);

	/**
	 * Returns the integration constraints object.<br/>
	 * Several constraints on integration mode can be stored in the object.<br/>
	 * Think of a peak where a shoulder has been detected. Now you don't want
	 * the integrator to use another baseline for the shoulder.<br/>
	 * What can you do? Tell the integrator to not set another baseline or
	 * correction. If the peak is marked with the IntegrationConstraint
	 * "LEAVE_PEAK_AS_IT_IS" it will be integrated as it is, if the integrator
	 * has implemented it.
	 * 
	 * @return {@link IIntegrationConstraints}
	 */
	IIntegrationConstraints getIntegrationConstraints();

	/**
	 * Returns the integrated area of the actual peak.
	 * 
	 * @return double
	 */
	double getIntegratedArea();

	/**
	 * Sets the integration results.
	 * 
	 * @param integratorDescription
	 * @param integrationEntries
	 */
	void setIntegratedArea(List<? extends IIntegrationEntry> integrationEntries, String integratorDescription);

	/**
	 * Returns the list of integration entries.
	 * 
	 * @return List<IIntegrationEntry>
	 */
	List<IIntegrationEntry> getIntegrationEntries();

	void addAllIntegrationEntries(Collection<? extends IIntegrationEntry> integrationEntries);

	void addAllIntegrationEntries(IIntegrationEntry... integrationEntries);

	/**
	 * Returns the peak quantifier description.
	 * 
	 * @return String
	 */
	String getQuantifierDescription();

	/**
	 * Sets the peak quantifier description.
	 * 
	 * @param quantifierDescription
	 */
	void setQuantifierDescription(String quantifierDescription);

	/**
	 * Adds a quantitation entry to the list.
	 * 
	 * @param quantitationEntry
	 */
	void addQuantitationEntry(IQuantitationEntry quantitationEntry);

	void addAllQuantitationEntries(Collection<? extends IQuantitationEntry> quantitationEntries);

	void addAllQuantitationEntries(IQuantitationEntry... quantitationEntries);

	/**
	 * Removes the quantitation entry from the list.
	 * 
	 * @param quantitationEntry
	 */
	void removeQuantitationEntry(IQuantitationEntry quantitationEntry);

	/**
	 * Removes the quantitation entries from the list.
	 * 
	 * @param List
	 *            <IQuantitationEntry>
	 */
	void removeQuantitationEntries(List<IQuantitationEntry> quantitationEntriesToRemove);

	/**
	 * Get the quantitation entries.
	 * 
	 * @return List<IQuantitationEntry>
	 */
	List<IQuantitationEntry> getQuantitationEntries();

	/**
	 * Removes all quantitation entries.
	 */
	void removeAllQuantitationEntries();

	/**
	 * Removes all integration entries.
	 */
	void removeAllIntegrationEntries();

	/**
	 * Returns if the peak is active for analysis.
	 * 
	 * @return boolean
	 */
	boolean isActiveForAnalysis();

	/**
	 * Sets the peak active/inactive.
	 * 
	 * @param active
	 */
	void setActiveForAnalysis(boolean activeForAnalysis);

	List<IInternalStandard> getInternalStandards();

	void addInternalStandard(IInternalStandard internalStandard);

	void addInternalStandards(List<IInternalStandard> internalStandards);

	void removeInternalStandards();

	void removeInternalStandard(IInternalStandard internalStandard);

	List<String> getQuantitationReferences();

	void addQuantitationReference(String quantitationReference);

	void addQuantitationReferences(List<String> quantitationReferences);

	void removeQuantitationReference(String quantitationReference);

	/**
	 * Returns the name of a peak that might be used to identify the peak to the user e.g. in plots or lists
	 * 
	 * @return the name of the peak or <code>null</code> if none is available
	 */
	default String getName() {

		IIdentificationTarget target = IIdentificationTarget.getBestIdentificationTarget(getTargets());
		if(target != null) {
			ILibraryInformation libraryInformation = target.getLibraryInformation();
			if(libraryInformation != null) {
				String name = libraryInformation.getName();
				if(name != null && !name.isEmpty()) {
					return name;
				}
			}
		}
		return null;
	}

	@Override
	default int getPeakEnd() {

		IPeakModel peakModel = getPeakModel();
		return peakModel.getPeakScan(peakModel.getStopRetentionTime()).getScanNumber() - 1;
	}

	@Override
	default int getPeakStart() {

		IPeakModel peakModel = getPeakModel();
		return peakModel.getPeakScan(peakModel.getStartRetentionTime()).getScanNumber() - 1;
	}

	@Override
	default int getPeakMaximum() {

		IPeakModel peakModel = getPeakModel();
		return peakModel.getPeakMaximum().getScanNumber() - 1;
	}

	@Override
	default double getX() {

		return getPeakModel().getRetentionTimeAtPeakMaximum();
	}

	@Override
	default double getY() {

		IPeakModel peakModel = getPeakModel();
		return peakModel.getPeakAbundance() + peakModel.getBackgroundAbundance();
	}
}
