/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.pcr.model.core.IChannel;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.pcr.model.core.IWell;

public class Utils {

	private static final Logger logger = Logger.getLogger(Utils.class);

	public static boolean isEmpty(IPlate plate, String targetSubset) {

		for(IWell well : plate.getWells()) {
			Map<String, String> dataMap = well.getHeaderDataMap();
			String sampleSubset = dataMap.getOrDefault(IWell.SAMPLE_SUBSET, "");
			if(sampleSubset.equals(targetSubset)) {
				for(IChannel channel : well.getChannels().values()) {
					if(channel.getCrossingPoint() > 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public static int[] parseChannels(String[] values, int i) {

		return parseChannels(values[i]);
	}

	public static int[] parseChannels(String input) {

		int[] channels = new int[1];
		try {
			if(input.contains("+")) {
				String[] strings = StringUtils.split(input, '+');
				channels = new int[strings.length];
				for(int i = 0; i < strings.length; i++) {
					channels[i] = Integer.parseInt(strings[i].trim());
				}
			} else {
				channels[0] = Integer.parseInt(input.trim());
			}
		} catch(NumberFormatException e) {
			logger.warn(e);
		}
		return channels;
	}

	public static int[] parseLogicalChannels(String[] values, int i) {

		return parseChannels(values[i]);
	}

	public static int[] parseLogicalChannels(String input) {

		int[] channels = new int[1];
		try {
			if(input.contains("&")) {
				String[] strings = StringUtils.split(input, '&');
				channels = new int[strings.length];
				for(int i = 0; i < strings.length; i++) {
					channels[i] = Integer.parseInt(strings[i].trim());
				}
			} else if(input.contains("/")) {
				String[] strings = StringUtils.split(input, '/');
				channels = new int[strings.length];
				for(int i = 0; i < strings.length; i++) {
					channels[i] = Integer.parseInt(strings[i].trim());
				}
			} else {
				channels[0] = Integer.parseInt(input.trim());
			}
		} catch(NumberFormatException e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return channels;
	}
}
