/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.traces;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TraceFactory {

	private static final Pattern PATTERN_DIGIT = Pattern.compile("(\\d+\\.?\\d?)(\\s?)(.*)");
	private static final Pattern PATTERN_TANDEM_MSD = Pattern.compile("(\\d+)(\\s+>\\s+)(\\d+\\.?\\d?)(\\s+@)(\\d+)(.*)");
	private static final Pattern PATTERN_HIGHRES_MSD = Pattern.compile("(\\d+\\.?\\d+)(.*)");

	public static <T extends ITrace> T parseTrace(String content, Class<T> clazz) {

		T traceSpecific = null;
		try {
			/*
			 * TODO - WSD_HIGHRES
			 * TODO - GENERIC (TandemMS - q3 trace instead of q1)
			 */
			traceSpecific = clazz.getDeclaredConstructor().newInstance();
			boolean success = false;
			//
			if(traceSpecific instanceof TraceNominalMSD traceNominalMSD) {
				success = parseTraceNominalMSD(content, traceNominalMSD);
			} else if(traceSpecific instanceof TraceTandemMSD traceTandemMSD) {
				success = parseTraceTandemMSD(content, traceTandemMSD);
			} else if(traceSpecific instanceof TraceHighResMSD traceHighResMSD) {
				success = parseTraceHighResMSD(content, traceHighResMSD);
			} else if(traceSpecific instanceof TraceRasteredWSD traceRasteredWSD) {
				success = parseTraceRasteredWSD(content, traceRasteredWSD);
			} else if(traceSpecific instanceof TraceRasteredVSD traceRasteredVSD) {
				success = parseTraceRasteredVSD(content, traceRasteredVSD);
			}
			/*
			 * In case that the content can't be parsed.
			 */
			if(!success) {
				traceSpecific = null;
			}
		} catch(Exception e) {
		}
		//
		return traceSpecific;
	}

	private static boolean parseTraceNominalMSD(String content, TraceNominalMSD traceSpecific) {

		/*
		 * 69
		 * 69.4
		 * 69.5
		 * 79 (x10.5)
		 * 79 (x8)
		 * 79.4 (x8)
		 * 79.5 (x8)
		 * 79 (x0.27)
		 */
		Matcher matcher = PATTERN_DIGIT.matcher(content);
		if(matcher.matches()) {
			double mz = parseDouble(matcher.group(1)); // 79
			if(mz > 0) {
				traceSpecific.setMZ(mz);
				assignScaleFactor(matcher.group(3), traceSpecific); // 10.5
				return true;
			}
		}
		/*
		 * Content couldn't be matched.
		 */
		return false;
	}

	private static boolean parseTraceTandemMSD(String content, TraceTandemMSD traceSpecific) {

		/*
		 * 139 > 111.0 @12
		 * 139 > 111.0 @12 (x5.8)
		 */
		Matcher matcher = PATTERN_TANDEM_MSD.matcher(content);
		if(matcher.matches()) {
			double parentMZ = parseDouble(matcher.group(1)); // 139
			if(parentMZ > 0) {
				double daughterMZ = parseDouble(matcher.group(3)); // 111.0
				if(daughterMZ > 0) {
					double collisionEnergy = parseDouble(matcher.group(5)); // 12
					if(collisionEnergy > 0) {
						traceSpecific.setParentMZ(parentMZ);
						traceSpecific.setDaughterMZ(daughterMZ);
						traceSpecific.setCollisionEnergy(collisionEnergy);
						assignScaleFactor(matcher.group(6), traceSpecific); // 5.8
						return true;
					}
				}
			}
		}
		/*
		 * Content couldn't be matched.
		 */
		return false;
	}

	private static boolean parseTraceHighResMSD(String content, TraceHighResMSD traceSpecific) {

		/*
		 * 427.240
		 * 279.092 (x5.3)
		 * 400.01627±10ppm
		 * 400.01627±10ppm (x4.7)
		 * 400.01627±0.02
		 * 400.01627±0.02 (x2.9)
		 * 400.01627+-0.02
		 * 400.01627+-0.02 (x2.9)
		 * 400.01627+-10ppm
		 * 400.01627+-10ppm (x4.7)
		 */
		Matcher matcher = PATTERN_HIGHRES_MSD.matcher(content);
		if(matcher.matches()) {
			double mz = parseDouble(matcher.group(1)); // 400.01627
			if(mz > 0) {
				/*
				 * Set m/z and match range.
				 */
				traceSpecific.setMZ(mz);
				String part = matcher.group(2).trim();
				if(part.contains(ITrace.INFIX_HIGHRES_RANGE_STANDARD) || part.contains(ITrace.INFIX_HIGHRES_RANGE_SIMPLE)) {
					String[] parts = part.split("\\s");
					String range = parts[0].replace(ITrace.INFIX_HIGHRES_RANGE_STANDARD, "").replace(ITrace.INFIX_HIGHRES_RANGE_SIMPLE, "").trim();
					double delta = -1;
					if(range.endsWith(ITrace.POSTFIX_HIGHRES_PPM)) {
						double ppm = parseDouble(range.replace(ITrace.POSTFIX_HIGHRES_PPM, ""));
						if(ppm >= 0) {
							delta = mz * ppm / ITrace.MILLION;
						}
					} else {
						delta = parseDouble(range);
					}
					//
					if(delta >= 0) {
						traceSpecific.setDelta(delta);
						if(parts.length > 1) {
							assignScaleFactor(parts[1], traceSpecific); // 5.3
						}
						return true;
					}
				} else {
					assignScaleFactor(part, traceSpecific); // 5.3
					return true;
				}
			}
		}
		/*
		 * Content couldn't be matched.
		 */
		return false;
	}

	private static boolean parseTraceRasteredWSD(String content, TraceRasteredWSD traceSpecific) {

		/*
		 * 200
		 * 200.4
		 * 200.5
		 * 201 (x1.6)
		 */
		Matcher matcher = PATTERN_DIGIT.matcher(content);
		if(matcher.matches()) {
			double wavelength = parseDouble(matcher.group(1)); // 200
			if(wavelength > 0) {
				traceSpecific.setWavelength(wavelength);
				assignScaleFactor(matcher.group(3), traceSpecific); // 1.6
				return true;
			}
		}
		/*
		 * Content couldn't be matched.
		 */
		return false;
	}

	private static boolean parseTraceRasteredVSD(String content, TraceRasteredVSD traceSpecific) {

		/*
		 * 1800
		 * 1800.4
		 * 1800.5
		 * 1801 (x1.6)
		 */
		Matcher matcher = PATTERN_DIGIT.matcher(content);
		if(matcher.matches()) {
			double wavenumber = parseDouble(matcher.group(1)); // 200
			if(wavenumber > 0) {
				traceSpecific.setWavenumber(wavenumber);
				assignScaleFactor(matcher.group(3), traceSpecific); // 1.6
				return true;
			}
		}
		/*
		 * Content couldn't be matched.
		 */
		return false;
	}

	private static void assignScaleFactor(String value, ITrace trace) {

		if(!value.isEmpty()) {
			value = value.replace(ITrace.PREFIX_SCALE_FACTOR, "");
			value = value.replace(ITrace.POSTFIX_SCALE_FACTOR, "");
			double scaleFactor = parseDouble(value);
			if(scaleFactor > 0) {
				trace.setScaleFactor(scaleFactor);
			}
		}
	}

	private static double parseDouble(String value) {

		try {
			return Double.parseDouble(value.trim());
		} catch(NumberFormatException e) {
			return -1;
		}
	}
}