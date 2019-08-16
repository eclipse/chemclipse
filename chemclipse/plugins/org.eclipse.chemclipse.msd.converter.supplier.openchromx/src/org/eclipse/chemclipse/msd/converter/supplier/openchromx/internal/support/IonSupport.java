/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.openchromx.internal.support;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.model.VendorIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.logging.core.Logger;

public class IonSupport {

	private static final Logger logger = Logger.getLogger(IonSupport.class);
	private static final Pattern ionPattern = Pattern.compile("(\\d+\\.\\d+),(\\d+\\.\\d+)"); // 45.2,7829.3
																								// 83.3,93920.3
	private static final String valueDelimiter = ",";
	private static final String ionDelimiter = " ";

	/**
	 * Encodes the given ions.
	 * 
	 * @param massSpectrum
	 * @return String
	 */
	public static String encodeIons(IVendorMassSpectrum massSpectrum) {

		List<IIon> ions = massSpectrum.getIons();
		StringBuilder builder = new StringBuilder();
		for(IIon ion : ions) {
			builder.append(ion.getIon());
			builder.append(valueDelimiter);
			builder.append(ion.getAbundance());
			builder.append(ionDelimiter);
		}
		return builder.toString();
	}

	/**
	 * Decodes the given Base64 encoded ions and stores them in the
	 * given mass spectrum.
	 * 
	 * @param massSpectrum
	 * @param encodedIons
	 */
	public static void decodeIons(IVendorMassSpectrum supplierMassSpectrum, String encodedIons) {

		String ions;
		String decodedIons;
		String[] values;
		IScanIon supplierIon;
		float ion;
		float abundance;
		decodedIons = encodedIons;
		Matcher matcher = ionPattern.matcher(decodedIons);
		while(matcher.find()) {
			try {
				/*
				 * Get the ion and abundance values.
				 */
				ions = matcher.group(0);
				values = ions.split(valueDelimiter);
				/*
				 * Get the ion and abundance values.
				 */
				ion = Float.parseFloat(values[0]);
				abundance = Float.parseFloat(values[1]);
				/*
				 * Create the ion and store it in mass spectrum.
				 */
				supplierIon = new VendorIon(ion, abundance);
				supplierMassSpectrum.addIon(supplierIon);
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			} catch(IonLimitExceededException e) {
				logger.warn(e);
			}
		}
	}
}
