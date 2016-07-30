/*******************************************************************************
 * Copyright (c) 2013, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.ArrayList;
import java.util.List;

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
	private String modelDescription = "";
	private PeakType peakType = PeakType.DEFAULT;
	private int suggestedNumberOfComponents = 0; // 0 is the default
	private String integratorDescription = "";
	private String detectorDescription = "";
	private String quantifierDescription = "";
	private boolean activeForAnalysis = true;
	private List<? extends IIntegrationEntry> integrationEntries;
	private IIntegrationConstraints integrationConstraints;
	private List<IQuantitationEntry> quantitationEntries;
	private List<IInternalStandard> internalStandards;

	public AbstractPeak() {
		/*
		 * Create new default instances.
		 */
		integrationConstraints = new IntegrationConstraints();
		integrationEntries = new ArrayList<IIntegrationEntry>();
		quantitationEntries = new ArrayList<IQuantitationEntry>();
		internalStandards = new ArrayList<IInternalStandard>();
	}

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
	public List<? extends IIntegrationEntry> getIntegrationEntries() {

		return integrationEntries;
	}

	@Override
	public void setIntegratedArea(List<? extends IIntegrationEntry> integrationEntries, String integratorDescription) {

		if(integrationEntries != null) {
			setIntegratorDescription(integratorDescription);
			this.integrationEntries = integrationEntries;
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
	public void removeQuantitationEntry(IQuantitationEntry quantitationEntry) {

		quantitationEntries.remove(quantitationEntry);
	}

	@Override
	public void removeQuantitationEntries(List<IQuantitationEntry> quantitationEntriesToRemove) {

		quantitationEntries.removeAll(quantitationEntriesToRemove);
	}

	@Override
	public List<IQuantitationEntry> getQuantitationEntries() {

		return quantitationEntries;
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
	protected void validateRetentionTimes(IChromatogram chromatogram, IPeakModel peakModel) throws PeakException {

		int start = chromatogram.getStartRetentionTime();
		int stop = chromatogram.getStopRetentionTime();
		if(peakModel.getStartRetentionTime() < start || peakModel.getStopRetentionTime() > stop) {
			throw new PeakException("The start and stop retention times of the peak must be within the chromatogram retention time borders.");
		}
	}

	@Override
	public List<IInternalStandard> getInternalStandards() {

		return internalStandards;
	}

	@Override
	public void addInternalStandard(IInternalStandard internalStandard) {

		internalStandards.add(internalStandard);
	}

	@Override
	public void removeInternalStandard(IInternalStandard internalStandard) {

		internalStandards.remove(internalStandard);
	}
}
