/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.splash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;

/**
 * @see Wohlgemuth, G, et al. SPLASH, a Hashed Identifier for Mass Spectra.
 *      Nature Biotechnology 34, 1099-101 (2016).
 *      <a href="https://doi.org/10.1038/nbt.3689">https://doi.org/10.1038/nbt.3689</a>
 */
public class SplashFactory {

	private static final Logger logger = Logger.getLogger(SplashFactory.class);
	//
	private static final float NORMALIZATION_FACTOR = 100f;
	private static final int SPLASH_VERSION = 0;
	private static final int SPECTRA_TYPE = 1; // MS
	private static final String HASH_SEPERATOR = "-";
	//
	private static final double EPS_CORRECTION = 1.0e-7d;
	//
	private static final int PREFILTER_BASE = 3;
	private static final int PREFILTER_LENGTH = 10;
	private static final int PREFILTER_BIN_SIZE = 5;
	//
	private static final int SIMILARITY_BASE = 10;
	private static final int SIMILARITY_LENGTH = 10;
	private static final int SIMILARITY_BIN_SIZE = 100;
	//
	private static final char[] BASE_36_MAP = new char[]{ //
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', //
			'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', //
			'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' //
	};
	//
	private static final String ION_SEPERATOR = " ";
	private static final long MZ_PRECISION_FACTOR = (long)Math.pow(10, 6);
	private static final long INTENSITY_PRECISION_FACTOR = (long)Math.pow(10, 0);
	//
	private String splash;

	public SplashFactory(IScanMSD massSpectrum) {

		try {
			IScanMSD normalizedMassSpectrum = massSpectrum.makeDeepCopy().normalize(NORMALIZATION_FACTOR);
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("splash" + SPECTRA_TYPE + SPLASH_VERSION);
			stringBuilder.append(HASH_SEPERATOR);
			List<IIon> filteredIons = filterSpectrum(normalizedMassSpectrum, 10, 0.1);
			String prefilterHistogram = calculateHistogramBlock(filteredIons, PREFILTER_BASE, PREFILTER_LENGTH, PREFILTER_BIN_SIZE);
			stringBuilder.append(translateBase(prefilterHistogram, PREFILTER_BASE, 36, 4));
			stringBuilder.append(HASH_SEPERATOR);
			stringBuilder.append(calculateHistogramBlock(normalizedMassSpectrum.getIons(), SIMILARITY_BASE, SIMILARITY_LENGTH, SIMILARITY_BIN_SIZE));
			stringBuilder.append(HASH_SEPERATOR);
			stringBuilder.append(encodeSpectrum(normalizedMassSpectrum));
			splash = stringBuilder.toString();
		} catch(CloneNotSupportedException e) {
			logger.error(e);
		}
	}

	public String getSplash() {

		return splash;
	}

	private List<IIon> filterSpectrum(IScanMSD massSpectrum, int topIons, double basePeakPercentage) {

		double basePeakIntensity = massSpectrum.getBasePeakAbundance();
		List<IIon> ions = massSpectrum.getIons();
		if(basePeakPercentage >= 0) {
			List<IIon> filteredIons = new ArrayList<IIon>();
			for(IIon ion : ions) {
				if(ion.getAbundance() + EPS_CORRECTION >= basePeakPercentage * basePeakIntensity) {
					filteredIons.add(new Ion(ion.getIon(), ion.getAbundance()));
				}
			}
			ions = filteredIons;
		}
		if(topIons > 0 && ions.size() > topIons) {
			Collections.sort(ions, new IntensityThenMassComparator());
			ions = ions.subList(0, topIons);
		}
		return ions;
	}

	private String calculateHistogramBlock(List<IIon> ions, int base, int length, int binSize) {

		double[] binnedIons = new double[length];
		double maxIntensity = 0;
		for(IIon ion : ions) {
			int bin = (int)(ion.getIon() / binSize) % length;
			binnedIons[bin] += ion.getAbundance();
			if(binnedIons[bin] > maxIntensity) {
				maxIntensity = binnedIons[bin];
			}
		}
		for(int i = 0; i < length; i++) {
			binnedIons[i] = (base - 1) * binnedIons[i] / maxIntensity;
		}
		StringBuilder result = new StringBuilder();
		for(int i = 0; i < length; i++) {
			int bin = (int)(EPS_CORRECTION + binnedIons[i]);
			result.append(BASE_36_MAP[bin]);
		}
		return result.toString();
	}

	protected String translateBase(String number, int initialBase, int finalBase, int fill) {

		int n = Integer.parseInt(number, initialBase);
		StringBuilder result = new StringBuilder();
		while(n > 0) {
			result.insert(0, BASE_36_MAP[n % finalBase]);
			n /= finalBase;
		}
		while(result.length() < fill) {
			result.insert(0, '0');
		}
		return result.toString();
	}

	protected String encodeSpectrum(IScanMSD spectrum) {

		List<IIon> ions = new ArrayList<IIon>(spectrum.getIons());
		StringBuilder buffer = new StringBuilder();
		Collections.sort(ions, new MassThenIntensityComparator());
		for(int i = 0; i < ions.size(); i++) {
			buffer.append(formatMZ(ions.get(i).getIon()));
			buffer.append(":");
			buffer.append(formatIntensity(ions.get(i).getAbundance()));
			if(i < ions.size() - 1) {
				buffer.append(ION_SEPERATOR);
			}
		}
		String block = buffer.toString();
		String hash = DigestUtils.sha256Hex(block).substring(0, 20);
		return hash;
	}

	private String formatMZ(double value) {

		return String.format("%d", (long)((value + EPS_CORRECTION) * MZ_PRECISION_FACTOR));
	}

	private String formatIntensity(double value) {

		return String.format("%d", (long)((value + EPS_CORRECTION) * INTENSITY_PRECISION_FACTOR));
	}
}
