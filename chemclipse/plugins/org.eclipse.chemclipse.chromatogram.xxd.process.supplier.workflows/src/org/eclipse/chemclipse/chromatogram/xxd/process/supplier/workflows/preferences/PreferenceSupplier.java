/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.preferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.ChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilterSupplier;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilterSupport;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.ChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.IChromatogramFilterSupplierMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.IChromatogramFilterSupportMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.IMassSpectrumIdentifierSupport;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.MassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.IPeakIdentifierSupportMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.PeakIdentifierMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.PeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.IPeakDetectorSupplier;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.IPeakDetectorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.BaselineDetector;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.IBaselineDetectorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.IBaselineDetectorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.chromatogram.ChromatogramCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.chromatogram.IChromatogramCalculatorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.chromatogram.IChromatogramCalculatorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.IPeakIntegratorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.IPeakIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.PeakIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.Activator;
import org.eclipse.chemclipse.model.identifier.core.ISupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.BaselineDetectorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramCalculatorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramFilterTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramFilterTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakDetectorTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIdentifierTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIntegratorTypeSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_EVALUATION_CHROMATOGRAM_MSD_FILTER = ChromatogramFilterTypeSupplierMSD.CATEGORY;
	public static final String DEF_EVALUATION_CHROMATOGRAM_MSD_FILTER = "";
	public static final String P_EVALUATION_CHROMATOGRAM_FILTER = ChromatogramFilterTypeSupplier.CATEGORY;
	public static final String DEF_EVALUATION_CHROMATOGRAM_FILTER = "";
	public static final String P_EVALUATION_BASELINE_DETECTOR = BaselineDetectorTypeSupplier.CATEGORY;
	public static final String DEF_EVALUATION_BASELINE_DETECTOR = "";
	public static final String P_EVALUATION_PEAK_DETECTOR = PeakDetectorTypeSupplierMSD.CATEGORY;
	public static final String DEF_EVALUATION_PEAK_DETECTOR = "";
	public static final String P_EVALUATION_PEAK_INTEGRATOR = PeakIntegratorTypeSupplier.CATEGORY;
	public static final String DEF_EVALUATION_PEAK_INTEGRATOR = "";
	public static final String P_EVALUATION_CHROMATOGRAM_CALCULATOR = ChromatogramCalculatorTypeSupplier.CATEGORY;
	public static final String DEF_EVALUATION_CHROMATOGRAM_CALCULATOR = "";
	public static final String P_EVALUATION_PEAK_IDENTIFIER = PeakIdentifierTypeSupplierMSD.CATEGORY;
	public static final String DEF_EVALUATION_PEAK_IDENTIFIER = "";
	//
	public static final String P_SAMPLEQUANT_FILTER_PATH_CHROMATOGRAM = "samplequantFilterPathChromatogram";
	public static final String DEF_SAMPLEQUANT_FILTER_PATH_CHROMATOGRAM = "";
	public static final String P_SAMPLEQUANT_FILTER_PATH_RTERES = "samplequantFilterPathRteres";
	public static final String DEF_SAMPLEQUANT_FILTER_PATH_RTERES = "";
	public static final String P_SAMPLEQUANT_FILTER_PATH_SUMRPT = "samplequantFilterPathSumRpt";
	public static final String DEF_SAMPLEQUANT_FILTER_PATH_SUMRPT = "";
	public static final String P_SAMPLEQUANT_FILTER_PATH_TARGETS = "samplequantFilterPathTargets";
	public static final String DEF_SAMPLEQUANT_FILTER_PATH_TARGETS = "";
	public static final double SAMPLEQUANT_MATCH_QUALITY_MIN = 0.0d;
	public static final double SAMPLEQUANT_MATCH_QUALITY_MAX = 100.0d;
	public static final double SAMPLEQUANT_MATCH_QUALITY_DEF = 95.0d;
	public static final String P_SAMPLEQUANT_MIN_MATCH_QUALITY = "samplequantMinMatchQuality";
	public static final double DEF_SAMPLEQUANT_MIN_MATCH_QUALITY = SAMPLEQUANT_MATCH_QUALITY_DEF;
	public static final String P_SAMPLEQUANT_SCAN_IDENTIFIER = "samplequantScanIdentifier";
	public static final String DEF_SAMPLEQUANT_SCAN_IDENTIFIER = "";
	public static final String P_SAMPLEQUANT_SEARCH_CASE_SENSITIVE = "samplequantSearchCaseSensitive"; // $NON-NLS-1$
	public static final boolean DEF_SAMPLEQUANT_SEARCH_CASE_SENSITIVE = false; // $NON-NLS-1$
	//
	private static IPreferenceSupplier preferenceSupplier;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public IScopeContext getScopeContext() {

		return InstanceScope.INSTANCE;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public Map<String, String> getDefaultValues() {

		Map<String, String> defaultValues = new HashMap<String, String>();
		defaultValues.put(P_EVALUATION_CHROMATOGRAM_MSD_FILTER, DEF_EVALUATION_CHROMATOGRAM_MSD_FILTER);
		defaultValues.put(P_EVALUATION_CHROMATOGRAM_FILTER, DEF_EVALUATION_CHROMATOGRAM_FILTER);
		defaultValues.put(P_EVALUATION_BASELINE_DETECTOR, DEF_EVALUATION_BASELINE_DETECTOR);
		defaultValues.put(P_EVALUATION_PEAK_DETECTOR, DEF_EVALUATION_PEAK_DETECTOR);
		defaultValues.put(P_EVALUATION_PEAK_INTEGRATOR, DEF_EVALUATION_PEAK_INTEGRATOR);
		defaultValues.put(P_EVALUATION_CHROMATOGRAM_CALCULATOR, DEF_EVALUATION_CHROMATOGRAM_CALCULATOR);
		defaultValues.put(P_EVALUATION_PEAK_IDENTIFIER, DEF_EVALUATION_PEAK_IDENTIFIER);
		//
		defaultValues.put(P_SAMPLEQUANT_FILTER_PATH_CHROMATOGRAM, DEF_SAMPLEQUANT_FILTER_PATH_CHROMATOGRAM);
		defaultValues.put(P_SAMPLEQUANT_FILTER_PATH_RTERES, DEF_SAMPLEQUANT_FILTER_PATH_RTERES);
		defaultValues.put(P_SAMPLEQUANT_FILTER_PATH_SUMRPT, DEF_SAMPLEQUANT_FILTER_PATH_SUMRPT);
		defaultValues.put(P_SAMPLEQUANT_FILTER_PATH_TARGETS, DEF_SAMPLEQUANT_FILTER_PATH_TARGETS);
		defaultValues.put(P_SAMPLEQUANT_MIN_MATCH_QUALITY, Double.toString(DEF_SAMPLEQUANT_MIN_MATCH_QUALITY));
		defaultValues.put(P_SAMPLEQUANT_SEARCH_CASE_SENSITIVE, Boolean.toString(DEF_SAMPLEQUANT_SEARCH_CASE_SENSITIVE));
		//
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static String[][] getMassSpectrumIdentifier() {

		try {
			IMassSpectrumIdentifierSupport support = MassSpectrumIdentifier.getMassSpectrumIdentifierSupport();
			List<String> ids = support.getAvailableIdentifierIds();
			//
			String[][] elements = new String[ids.size() + 1][2];
			for(int i = 0; i < ids.size(); i++) {
				String id = ids.get(i);
				ISupplier supplier = support.getIdentifierSupplier(id);
				elements[i][0] = supplier.getIdentifierName();
				elements[i][1] = supplier.getId();
			}
			//
			elements[ids.size()][0] = "";
			elements[ids.size()][1] = "";
			//
			return elements;
		} catch(Exception e) {
			//
			return new String[][]{{"No Mass Spectrum Identifier Available", ""}};
		}
	}

	public static String[][] getPeakIdentifier() {

		try {
			IPeakIdentifierSupportMSD support = PeakIdentifierMSD.getPeakIdentifierSupport();
			List<String> ids = support.getAvailableIdentifierIds();
			//
			String[][] elements = new String[ids.size() + 1][2];
			for(int i = 0; i < ids.size(); i++) {
				String id = ids.get(i);
				ISupplier supplier = support.getIdentifierSupplier(id);
				elements[i][0] = supplier.getIdentifierName();
				elements[i][1] = supplier.getId();
			}
			//
			elements[ids.size()][0] = "";
			elements[ids.size()][1] = "";
			//
			return elements;
		} catch(Exception e) {
			//
			return new String[][]{{"No Peak Identifier Available", ""}};
		}
	}

	public static String[][] getChromatogramFilterMSD() {

		try {
			IChromatogramFilterSupportMSD support = ChromatogramFilterMSD.getChromatogramFilterSupport();
			List<String> ids = support.getAvailableFilterIds();
			//
			String[][] elements = new String[ids.size() + 1][2];
			for(int i = 0; i < ids.size(); i++) {
				String id = ids.get(i);
				IChromatogramFilterSupplierMSD supplier = support.getFilterSupplier(id);
				elements[i][0] = supplier.getFilterName();
				elements[i][1] = supplier.getId();
			}
			//
			elements[ids.size()][0] = "";
			elements[ids.size()][1] = "";
			//
			return elements;
		} catch(Exception e) {
			//
			return new String[][]{{"No Filter MSD Available", ""}};
		}
	}

	public static String[][] getChromatogramFilter() {

		try {
			IChromatogramFilterSupport support = ChromatogramFilter.getChromatogramFilterSupport();
			List<String> ids = support.getAvailableFilterIds();
			//
			String[][] elements = new String[ids.size() + 1][2];
			for(int i = 0; i < ids.size(); i++) {
				String id = ids.get(i);
				IChromatogramFilterSupplier supplier = support.getFilterSupplier(id);
				elements[i][0] = supplier.getFilterName();
				elements[i][1] = supplier.getId();
			}
			//
			elements[ids.size()][0] = "";
			elements[ids.size()][1] = "";
			//
			return elements;
		} catch(Exception e) {
			//
			return new String[][]{{"No Filter Available", ""}};
		}
	}

	public static String[][] getBaselineDetectors() {

		try {
			IBaselineDetectorSupport support = BaselineDetector.getBaselineDetectorSupport();
			List<String> ids = support.getAvailableDetectorIds();
			//
			String[][] elements = new String[ids.size() + 1][2];
			for(int i = 0; i < ids.size(); i++) {
				String id = ids.get(i);
				IBaselineDetectorSupplier supplier = support.getBaselineDetectorSupplier(id);
				elements[i][0] = supplier.getDetectorName();
				elements[i][1] = supplier.getId();
			}
			//
			elements[ids.size()][0] = "";
			elements[ids.size()][1] = "";
			//
			return elements;
		} catch(Exception e) {
			//
			return new String[][]{{"No Baseline Detector Available", ""}};
		}
	}

	public static String[][] getPeakDetectors() {

		try {
			IPeakDetectorSupport support = PeakDetectorMSD.getPeakDetectorSupport();
			List<String> ids = support.getAvailablePeakDetectorIds();
			//
			String[][] elements = new String[ids.size() + 1][2];
			for(int i = 0; i < ids.size(); i++) {
				String id = ids.get(i);
				IPeakDetectorSupplier supplier = support.getPeakDetectorSupplier(id);
				elements[i][0] = supplier.getPeakDetectorName();
				elements[i][1] = supplier.getId();
			}
			//
			elements[ids.size()][0] = "";
			elements[ids.size()][1] = "";
			//
			return elements;
		} catch(Exception e) {
			//
			return new String[][]{{"No Peak Detector Available", ""}};
		}
	}

	public static String[][] getPeakIntegrators() {

		try {
			IPeakIntegratorSupport support = PeakIntegrator.getPeakIntegratorSupport();
			List<String> ids = support.getAvailableIntegratorIds();
			//
			String[][] elements = new String[ids.size() + 1][2];
			for(int i = 0; i < ids.size(); i++) {
				String id = ids.get(i);
				IPeakIntegratorSupplier supplier = support.getIntegratorSupplier(id);
				elements[i][0] = supplier.getIntegratorName();
				elements[i][1] = supplier.getId();
			}
			//
			elements[ids.size()][0] = "";
			elements[ids.size()][1] = "";
			//
			return elements;
		} catch(Exception e) {
			//
			return new String[][]{{"No Peak Integrator Available", ""}};
		}
	}

	public static String[][] getChromatogramCalculators() {

		try {
			IChromatogramCalculatorSupport support = ChromatogramCalculator.getChromatogramCalculatorSupport();
			List<String> ids = support.getAvailableCalculatorIds();
			//
			String[][] elements = new String[ids.size() + 1][2];
			for(int i = 0; i < ids.size(); i++) {
				String id = ids.get(i);
				IChromatogramCalculatorSupplier supplier = support.getCalculatorSupplier(id);
				elements[i][0] = supplier.getCalculatorName();
				elements[i][1] = supplier.getId();
			}
			//
			elements[ids.size()][0] = "";
			elements[ids.size()][1] = "";
			//
			return elements;
		} catch(Exception e) {
			//
			return new String[][]{{"No Chromatogram Calculator Available", ""}};
		}
	}
}
