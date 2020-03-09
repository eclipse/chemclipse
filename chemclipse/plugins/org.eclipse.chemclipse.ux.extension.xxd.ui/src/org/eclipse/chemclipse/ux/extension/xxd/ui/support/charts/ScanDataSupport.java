/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.SignalType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swtchart.extensions.core.IChartSettings;

public class ScanDataSupport {

	public static final String[] DATA_TYPES_DEFAULT = new String[]{DataType.AUTO_DETECT.toString()};
	public static final String[] DATA_TYPES_MSD = new String[]{DataType.AUTO_DETECT.toString(), DataType.MSD_NOMINAL.toString(), DataType.MSD_TANDEM.toString(), DataType.MSD_HIGHRES.toString()};
	public static final String[] DATA_TYPES_CSD = new String[]{DataType.AUTO_DETECT.toString(), DataType.CSD.toString()};
	public static final String[] DATA_TYPES_WSD = new String[]{DataType.AUTO_DETECT.toString(), DataType.WSD.toString()};
	//
	public static final String[] SIGNAL_TYPES_DEFAULT = new String[]{SignalType.AUTO_DETECT.toString()};
	public static final String[] SIGNAL_TYPES_MSD = new String[]{SignalType.AUTO_DETECT.toString(), SignalType.CENTROID.toString(), SignalType.PROFILE.toString()};
	public static final String[] SIGNAL_TYPES_CSD = new String[]{SignalType.AUTO_DETECT.toString(), SignalType.CENTROID.toString()};
	public static final String[] SIGNAL_TYPES_WSD = new String[]{SignalType.AUTO_DETECT.toString(), SignalType.CENTROID.toString(), SignalType.PROFILE.toString()};
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0##");
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public String extractTracesText(IScanMSD scanMSD, int maxCopyTraces) {

		List<Integer> traces = extractTracesList(scanMSD, maxCopyTraces);
		Iterator<Integer> iterator = traces.iterator();
		StringBuilder builder = new StringBuilder();
		//
		while(iterator.hasNext()) {
			builder.append(iterator.next());
			if(iterator.hasNext()) {
				builder.append(" ");
			}
		}
		//
		return builder.toString();
	}

	public List<Integer> extractTracesList(IScanMSD scanMSD, int maxCopyTraces) {

		List<Integer> traces = new ArrayList<>();
		if(scanMSD != null) {
			IScanMSD massSpectrum = scanMSD.getOptimizedMassSpectrum() != null ? scanMSD.getOptimizedMassSpectrum() : scanMSD;
			List<IIon> ions = new ArrayList<>(massSpectrum.getIons());
			Collections.sort(ions, (i1, i2) -> Float.compare(i2.getAbundance(), i1.getAbundance()));
			//
			exitloop:
			for(IIon ion : ions) {
				/*
				 * Add the trace.
				 */
				int trace = AbstractIon.getIon(ion.getIon());
				if(!traces.contains(trace)) {
					traces.add(trace);
				}
				//
				if(traces.size() >= maxCopyTraces) {
					break exitloop;
				}
			}
		}
		/*
		 * Sort the traces ascending.
		 */
		Collections.sort(traces);
		return traces;
	}

	public String getRetentionTime(IScan scan) {

		if(scan != null) {
			return decimalFormat.format(scan.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR);
		} else {
			return "";
		}
	}

	public String getScanLabel(IScan scan) {

		StringBuilder builder = new StringBuilder();
		if(scan != null) {
			if(scan instanceof IPeakMassSpectrum) {
				builder.append("Peak Scan");
				builder.append(" | ");
			} else {
				builder.append("Scan: ");
				builder.append(scan.getScanNumber());
				builder.append(" | ");
			}
			//
			builder.append("RT: ");
			builder.append(decimalFormat.format(scan.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
			builder.append(" | ");
			builder.append("RI: ");
			if(PreferenceSupplier.showRetentionIndexWithoutDecimals()) {
				builder.append(Integer.toString((int)scan.getRetentionIndex()));
			} else {
				builder.append(decimalFormat.format(scan.getRetentionIndex()));
			}
			//
			if(scan instanceof IRegularMassSpectrum) {
				IRegularMassSpectrum massSpectrum = (IRegularMassSpectrum)scan;
				builder.append(" | ");
				builder.append("Detector: MS");
				builder.append(massSpectrum.getMassSpectrometer());
				builder.append(" | ");
				builder.append("Type: ");
				builder.append(massSpectrum.getMassSpectrumTypeDescription());
			}
			//
			builder.append(" | ");
			builder.append("Signal: ");
			builder.append((int)scan.getTotalSignal());
			//
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
				if(optimizedMassSpectrum != null) {
					builder.append(" | ");
					builder.append("optimized");
				}
			}
		} else {
			builder.append("No scan has been selected yet.");
		}
		return builder.toString();
	}

	public String getMassSpectrumLabel(IScanMSD scanMSD, String prefix, String title, String postfix) {

		StringBuilder builder = new StringBuilder();
		builder.append(prefix);
		builder.append(" ");
		builder.append(title);
		builder.append(" = ");
		//
		if(scanMSD != null) {
			if(scanMSD instanceof IRegularLibraryMassSpectrum) {
				IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)scanMSD;
				ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
				builder.append("NAME: ");
				builder.append(libraryInformation.getName());
				builder.append(" | ");
				builder.append("CAS: ");
				builder.append(libraryInformation.getCasNumber());
				builder.append(" | ");
			}
			builder.append("RT: ");
			builder.append(decimalFormat.format(scanMSD.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
			builder.append(" | ");
			builder.append("RI: ");
			if(PreferenceSupplier.showRetentionIndexWithoutDecimals()) {
				builder.append(Integer.toString((int)scanMSD.getRetentionIndex()));
			} else {
				builder.append(decimalFormat.format(scanMSD.getRetentionIndex()));
			}
			//
			IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
			if(optimizedMassSpectrum != null) {
				builder.append(" | ");
				builder.append("optimized");
			}
			//
			if(!"".equals(postfix)) {
				builder.append(" | ");
				builder.append(postfix);
				builder.append("*");
			}
		} else {
			builder.append("No mass spectrum has been selected yet.");
		}
		//
		return builder.toString();
	}

	public boolean containsOptimizedScan(IScan scan) {

		boolean containsOptimizedScan = false;
		//
		if(scan instanceof IScanMSD) {
			IScanMSD scanMSD = (IScanMSD)scan;
			IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
			if(optimizedMassSpectrum != null) {
				containsOptimizedScan = true;
			}
		}
		//
		return containsOptimizedScan;
	}

	public void setDataTypeMSD(IChartSettings chartSettings) {

		String titleX = preferenceStore.getString(PreferenceConstants.P_TITLE_X_AXIS_MZ);
		String titleY = preferenceStore.getString(PreferenceConstants.P_TITLE_Y_AXIS_INTENSITY);
		String titleY1 = preferenceStore.getString(PreferenceConstants.P_TITLE_Y_AXIS_RELATIVE_INTENSITY);
		//
		ChartSupport.setPrimaryAxisSet(chartSettings, titleX, true, titleY);
		ChartSupport.clearSecondaryAxes(chartSettings);
		ChartSupport.addSecondaryAxisY(chartSettings, titleY1);
	}

	public void setDataTypeCSD(IChartSettings chartSettings) {

		String titleX = preferenceStore.getString(PreferenceConstants.P_TITLE_X_AXIS_MILLISECONDS);
		String titleX1 = preferenceStore.getString(PreferenceConstants.P_TITLE_X_AXIS_MINUTES);
		String titleY = preferenceStore.getString(PreferenceConstants.P_TITLE_Y_AXIS_INTENSITY);
		String titleY1 = preferenceStore.getString(PreferenceConstants.P_TITLE_Y_AXIS_RELATIVE_INTENSITY);
		//
		ChartSupport.setPrimaryAxisSet(chartSettings, titleX, false, titleY);
		ChartSupport.clearSecondaryAxes(chartSettings);
		ChartSupport.addSecondaryAxisX(chartSettings, titleX1);
		ChartSupport.addSecondaryAxisY(chartSettings, titleY1);
	}

	public void setDataTypeWSD(IChartSettings chartSettings) {

		String titleX = preferenceStore.getString(PreferenceConstants.P_TITLE_X_AXIS_WAVELENGTH);
		String titleY = preferenceStore.getString(PreferenceConstants.P_TITLE_Y_AXIS_INTENSITY);
		String titleY1 = preferenceStore.getString(PreferenceConstants.P_TITLE_Y_AXIS_RELATIVE_INTENSITY);
		//
		ChartSupport.setPrimaryAxisSet(chartSettings, titleX, true, titleY);
		ChartSupport.clearSecondaryAxes(chartSettings);
		ChartSupport.addSecondaryAxisY(chartSettings, titleY1);
	}
}
