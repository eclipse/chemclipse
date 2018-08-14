/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.quantitation.IInternalStandard;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.support.IIntegrationConstraints;
import org.eclipse.chemclipse.model.support.IntegrationConstraints;
import org.eclipse.chemclipse.model.targets.AbstractPeakTargets;

public abstract class AbstractPeak extends AbstractPeakTargets implements IPeak {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -491170278267735090L;
	//
	private static final String CLASSIFIER_DELIMITER = " ";
	//
	private String modelDescription = "";
	private PeakType peakType = PeakType.DEFAULT;
	private int suggestedNumberOfComponents = 0; // 0 is the default
	private String integratorDescription = "";
	private String detectorDescription = "";
	private String quantifierDescription = "";
	private boolean activeForAnalysis = true;
	private List<IIntegrationEntry> integrationEntries = new ArrayList<IIntegrationEntry>();
	private IIntegrationConstraints integrationConstraints = new IntegrationConstraints();
	private Set<IQuantitationEntry> quantitationEntries = new HashSet<IQuantitationEntry>();
	private List<IInternalStandard> internalStandards = new ArrayList<IInternalStandard>();
	private List<String> quantitationReferences = new ArrayList<String>(); // Used to quantify against certain ISTDs or ESTDs
	private String classifier = "";

	@Override
	public String getModelDescription() {

		return modelDescription;
	}

	@Override
	public void setModelDescription(String modelDescription) {

		if(modelDescription != null) {
			this.modelDescription = modelDescription;
		}
	}

	@Override
	public PeakType getPeakType() {

		return peakType;
	}

	@Override
	public void setPeakType(PeakType peakType) {

		if(peakType != null) {
			this.peakType = peakType;
		}
	}

	@Override
	public int getSuggestedNumberOfComponents() {

		return suggestedNumberOfComponents;
	}

	@Override
	public void setSuggestedNumberOfComponents(int suggestedNumberOfComponents) {

		this.suggestedNumberOfComponents = suggestedNumberOfComponents;
	}

	@Override
	public String getIntegratorDescription() {

		return integratorDescription;
	}

	@Override
	public void setIntegratorDescription(String integratorDescription) {

		if(integratorDescription != null) {
			this.integratorDescription = integratorDescription;
		}
	}

	@Override
	public String getDetectorDescription() {

		return detectorDescription;
	}

	@Override
	public void setDetectorDescription(String detectorDescription) {

		if(detectorDescription != null) {
			this.detectorDescription = detectorDescription;
		}
	}

	@Override
	public IIntegrationConstraints getIntegrationConstraints() {

		return integrationConstraints;
	}

	@Override
	public double getIntegratedArea() {

		double integratedArea = 0.0d;
		if(integrationEntries.size() > 0) {
			for(IIntegrationEntry integrationEntry : integrationEntries) {
				integratedArea += integrationEntry.getIntegratedArea();
			}
		}
		return integratedArea;
	}

	@Override
	public List<IIntegrationEntry> getIntegrationEntries() {

		return Collections.unmodifiableList(integrationEntries);
	}

	@Override
	public void addAllIntegrationEntries(Collection<? extends IIntegrationEntry> integrationEntries) {

		this.integrationEntries.addAll(integrationEntries);
	}

	@Override
	public void addAllIntegrationEntries(IIntegrationEntry... integrationEntries) {

		this.addAllIntegrationEntries(Arrays.asList(integrationEntries));
	}

	@Override
	public void setIntegratedArea(List<? extends IIntegrationEntry> integrationEntries, String integratorDescription) {

		if(integrationEntries != null) {
			setIntegratorDescription(integratorDescription);
			this.integrationEntries = new ArrayList<>(integrationEntries);
		}
	}

	@Override
	public String getQuantifierDescription() {

		return quantifierDescription;
	}

	@Override
	public void setQuantifierDescription(String quantifierDescription) {

		this.quantifierDescription = quantifierDescription;
	}

	@Override
	public void addQuantitationEntry(IQuantitationEntry quantitationEntry) {

		quantitationEntries.add(quantitationEntry);
	}

	@Override
	public void addAllQuantitationEntries(IQuantitationEntry... quantitationEntries) {

		this.addAllQuantitationEntries(Arrays.asList(quantitationEntries));
	}

	@Override
	public void addAllQuantitationEntries(Collection<? extends IQuantitationEntry> quantitationEntries) {

		this.quantitationEntries.addAll(quantitationEntries);
	}

	@Override
	public void removeQuantitationEntry(IQuantitationEntry quantitationEntry) {

		quantitationEntries.remove(quantitationEntry);
	}

	@Override
	public void removeQuantitationEntries(List<IQuantitationEntry> quantitationEntriesToRemove) {

		quantitationEntries.removeAll(quantitationEntriesToRemove);
	}

	@Override
	public List<IQuantitationEntry> getQuantitationEntries() {

		return Collections.unmodifiableList(new ArrayList<IQuantitationEntry>(quantitationEntries));
	}

	@Override
	public void removeAllQuantitationEntries() {

		quantitationEntries.clear();
	}

	@Override
	public void removeAllIntegrationEntries() {

		integrationEntries.clear();
	}

	@Override
	public boolean isActiveForAnalysis() {

		return activeForAnalysis;
	}

	@Override
	public void setActiveForAnalysis(boolean activeForAnalysis) {

		this.activeForAnalysis = activeForAnalysis;
	}

	/**
	 * If the peak model is null, forget about creating a peak and skip its
	 * instantiation.<br/>
	 * Throw a IllegalArgumentException instead.<br/>
	 * Do not allow that parentChromatogram could be null.
	 */
	protected void validatePeakModel(IPeakModel peakModel) throws IllegalArgumentException {

		if(peakModel == null) {
			throw new IllegalArgumentException("The peak model must not be null");
		}
	}

	@SuppressWarnings("rawtypes")
	protected void validateChromatogram(IChromatogram chromatogram) throws IllegalArgumentException {

		/*
		 * Do not allow that the parentChromatogram is null.
		 */
		if(chromatogram == null) {
			throw new IllegalArgumentException("The parent chromatogram must not be null");
		}
	}

	/**
	 * Check that the peak model is within the chromatogram retention time
	 * borders.
	 */
	@SuppressWarnings("rawtypes")
	protected void validateRetentionTimes(IChromatogram chromatogram, IPeakModel peakModel) throws PeakException {

		int start = chromatogram.getStartRetentionTime();
		int stop = chromatogram.getStopRetentionTime();
		int startPeakModel = peakModel.getStartRetentionTime();
		int stopPeakModel = peakModel.getStopRetentionTime();
		if(startPeakModel < start || stopPeakModel > stop) {
			throw new PeakException("The start and stop retention times of the peak must be within the chromatogram retention time borders.");
		}
	}

	@Override
	public List<IInternalStandard> getInternalStandards() {

		return Collections.unmodifiableList(internalStandards);
	}

	@Override
	public void addInternalStandard(IInternalStandard internalStandard) {

		internalStandards.add(internalStandard);
	}

	@Override
	public void addInternalStandards(List<IInternalStandard> internalStandards) {

		this.internalStandards.addAll(internalStandards);
	}

	@Override
	public void removeInternalStandard(IInternalStandard internalStandard) {

		internalStandards.remove(internalStandard);
	}

	@Override
	public List<String> getQuantitationReferences() {

		return Collections.unmodifiableList(quantitationReferences);
	}

	@Override
	public void addQuantitationReference(String quantitationReference) {

		quantitationReferences.add(quantitationReference);
	}

	@Override
	public void addQuantitationReferences(List<String> quantitationReferences) {

		this.quantitationReferences.addAll(quantitationReferences);
	}

	@Override
	public void removeQuantitationReference(String quantitationReference) {

		quantitationReferences.remove(quantitationReference);
	}

	@Override
	public String getClassifier() {

		return classifier;
	}

	@Override
	public void setClassifier(String classifier) {

		this.classifier = classifier;
	}

	@Override
	public void addClassifier(String classifier) {

		if(classifier != null && !"".equals(classifier)) {
			if(!this.classifier.contains(classifier)) {
				this.classifier = this.classifier + CLASSIFIER_DELIMITER + classifier;
			}
		}
	}

	@Override
	public int compareTo(IPeak peak) {

		/*
		 * This method is used to store the peaks in
		 * AbstractChromatogram<T extends IPeak> in a sorted set.
		 */
		return Integer.compare(getPeakModel().getRetentionTimeAtPeakMaximum(), peak.getPeakModel().getRetentionTimeAtPeakMaximum());
	}
}
