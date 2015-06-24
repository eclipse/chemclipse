/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.documents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.msd.model.core.IIntegrationEntryMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.IntegrationEntryMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.msd.model.implementation.PeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.PeakModelMSD;
import org.eclipse.chemclipse.database.documents.AbstractDocument;
import org.eclipse.chemclipse.logging.core.Logger;

public class QuantitationPeakDocument extends AbstractDocument implements IQuantitationPeakDocument {

	/**
	 * Renew this uid on each change.
	 */
	private static final long serialVersionUID = -1984344157933233941L;
	private static final Logger logger = Logger.getLogger(QuantitationPeakDocument.class);

	public QuantitationPeakDocument() {

		super(CLASS_NAME);
	}

	public QuantitationPeakDocument(ODocument document) {

		super(document);
	}

	@Override
	public void setFields() {

		setConcentration(getConcentration());
		setConcentrationUnit(getConcentrationUnit());
		setPeakMSD(getPeakMSD());
	}

	@Override
	public void setConcentration(double concentration) {

		field(FIELD_CONCENTRATION, concentration, OType.DOUBLE);
	}

	@Override
	public double getConcentration() {

		return getDouble(FIELD_CONCENTRATION);
	}

	@Override
	public void setConcentrationUnit(String concentrationUnit) {

		field(FIELD_CONCENTRATION_UNIT, concentrationUnit, OType.STRING);
	}

	@Override
	public String getConcentrationUnit() {

		return getString(FIELD_CONCENTRATION_UNIT);
	}

	@Override
	public IPeakMSD getPeakMSD() {

		IPeakMSD peakMSD = null;
		IPeakIntensityValues peakIntensityValues = getPeakIntensityValues();
		IScanMSD massSpectrum = getMassSpectrum();
		IPeakMassSpectrum peakMassSpectrum = new PeakMassSpectrum(massSpectrum);
		try {
			IPeakModelMSD peakModelMSD = new PeakModelMSD(peakMassSpectrum, peakIntensityValues, 0, 0);
			peakMSD = new PeakMSD(peakModelMSD);
			peakMSD.setIntegratedArea(getPeakIntegrationValues(), "STORED QUANTITATION PEAK");
		} catch(IllegalArgumentException e) {
			logger.warn(e);
		} catch(PeakException e) {
			logger.warn(e);
		}
		return peakMSD;
	}

	@Override
	public void setPeakMSD(IPeakMSD peakMSD) {

		if(peakMSD != null) {
			setMassSpectrum(peakMSD.getPeakModel().getPeakMassSpectrum());
			setPeakIntensityValues(calculatePeakIntensityValues(peakMSD));
			setPeakIntegrationValues(peakMSD.getIntegrationEntries());
		}
	}

	private void setMassSpectrum(IScanMSD massSpectrum) {

		// Extract the values.
		Map<String, String> map = new HashMap<String, String>();
		for(IIon ion : massSpectrum.getIons()) {
			map.put(Double.valueOf(ion.getIon()).toString(), Float.valueOf(ion.getAbundance()).toString());
		}
		// Store the values.
		field(FIELD_IONS, map, OType.EMBEDDEDMAP);
	}

	private IScanMSD getMassSpectrum() {

		IScanMSD massSpectrum = new ScanMSD();
		Object object = getObject(FIELD_IONS);
		if(object != null && object instanceof Map) {
			/*
			 * Retrieve the ions stored in the map.
			 */
			@SuppressWarnings("rawtypes")
			Map map = (Map)object;
			for(Object key : map.keySet()) {
				double mz = OType.DOUBLE.asDouble(key);
				float abundance = OType.FLOAT.asFloat(map.get(key));
				try {
					IIon ion = new Ion(mz, abundance);
					massSpectrum.addIon(ion);
				} catch(AbundanceLimitExceededException e) {
					logger.warn(e);
				} catch(IonLimitExceededException e) {
					logger.warn(e);
				}
			}
		}
		return massSpectrum;
	}

	private void setPeakIntensityValues(IPeakIntensityValues peakIntensityValues) {

		// Extract the values.
		Map<String, String> map = new HashMap<String, String>();
		for(Integer retentionTime : peakIntensityValues.getRetentionTimes()) {
			Map.Entry<Integer, Float> entry = peakIntensityValues.getIntensityValue(retentionTime);
			map.put(Integer.valueOf(entry.getKey()).toString(), Float.valueOf(entry.getValue()).toString());
		}
		// Store the values.
		field(FIELD_INTENSITY_VALUES, map, OType.EMBEDDEDMAP);
	}

	private IPeakIntensityValues getPeakIntensityValues() {

		IPeakIntensityValues peakIntensityValues = new PeakIntensityValues();
		Object object = getObject(FIELD_INTENSITY_VALUES);
		if(object != null && object instanceof Map) {
			@SuppressWarnings("rawtypes")
			Map map = (Map)object;
			for(Object key : map.keySet()) {
				int retentionTime = OType.INTEGER.asInt(key);
				float relativeIntensity = OType.FLOAT.asFloat(map.get(key));
				peakIntensityValues.addIntensityValue(retentionTime, relativeIntensity);
			}
		}
		return peakIntensityValues;
	}

	private void setPeakIntegrationValues(List<? extends IIntegrationEntry> integrationEntries) {

		// Extract the values.
		Map<String, String> map = new HashMap<String, String>();
		for(IIntegrationEntry integrationEntry : integrationEntries) {
			if(integrationEntry instanceof IIntegrationEntryMSD) {
				/*
				 * It must be a MSD integration entry.
				 */
				IIntegrationEntryMSD integrationEntryMSD = (IIntegrationEntryMSD)integrationEntry;
				map.put(Double.valueOf(integrationEntryMSD.getIon()).toString(), Double.valueOf(integrationEntryMSD.getIntegratedArea()).toString());
			}
		}
		// Store the values.
		field(FIELD_INTEGRATION_VALUES, map, OType.EMBEDDEDMAP);
	}

	private List<IIntegrationEntryMSD> getPeakIntegrationValues() {

		List<IIntegrationEntryMSD> integrationEntries = new ArrayList<IIntegrationEntryMSD>();
		Object object = getObject(FIELD_INTEGRATION_VALUES);
		if(object != null && object instanceof Map) {
			@SuppressWarnings("rawtypes")
			Map map = (Map)object;
			for(Object key : map.keySet()) {
				double ion = OType.DOUBLE.asDouble(key);
				double integratedArea = OType.DOUBLE.asDouble(map.get(key));
				IIntegrationEntryMSD integrationEntry = new IntegrationEntryMSD(ion, integratedArea);
				integrationEntries.add(integrationEntry);
			}
		}
		return integrationEntries;
	}

	private IPeakIntensityValues calculatePeakIntensityValues(IPeakMSD peakMSD) {

		IPeakModelMSD peakModel = peakMSD.getPeakModel();
		float maxIntensity = peakModel.getPeakMaximum().getTotalSignal();
		IPeakIntensityValues peakIntensityValues = new PeakIntensityValues();
		for(int retentionTime : peakModel.getRetentionTimes()) {
			float actualIntensity = peakModel.getPeakAbundance(retentionTime);
			float relativeIntensity = calculateRelativeIntensity(maxIntensity, actualIntensity);
			peakIntensityValues.addIntensityValue(retentionTime, relativeIntensity);
		}
		peakIntensityValues.normalize();
		return peakIntensityValues;
	}

	private float calculateRelativeIntensity(float maxIntensity, float actualIntensity) {

		return IPeakIntensityValues.MAX_INTENSITY / maxIntensity * actualIntensity;
	}
}
