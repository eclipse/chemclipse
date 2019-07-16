/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.text.DecimalFormat;
import java.util.Iterator;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.pcr.model.core.IChannel;
import org.eclipse.chemclipse.pcr.model.core.IPlateTableEntry;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Image;

public class PlateListLabelProvider extends AbstractChemClipseLabelProvider {

	private static IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
	//
	public static String[] TITLES = {//
			"", //
			"1", //
			"2", //
			"3", //
			"4", //
			"5", //
			"6", //
			"7", //
			"8", //
			"9", //
			"10", //
			"11", //
			"12" //
	};
	//
	public static int[] BOUNDS = {//
			60, //
			110, //
			110, //
			110, //
			110, //
			110, //
			110, //
			110, //
			110, //
			110, //
			110, //
			110, //
			110 //
	};

	public static String getCellText(IWell well) {

		String text = "";
		if(well != null) {
			if(well.isEmptyMeasurement()) {
				text = "Position " + getPosition(well);
			} else {
				if(well.isActiveSubset()) {
					StringBuilder builder = new StringBuilder();
					builder.append(well.getSampleId());
					appendHeaderInfo(well, builder);
					appendCrossingPointInfo(well, builder);
					text = builder.toString();
				} else {
					String sampleSubset = well.getSampleSubset();
					text = sampleSubset.equals("") ? "--" : sampleSubset + " <" + getPosition(well) + ">";
				}
			}
		}
		return text;
	}

	private static void appendHeaderInfo(IWell well, StringBuilder builder) {

		String sampleSubset = well.getSampleSubset();
		String targetName = well.getTargetName();
		builder.append("\n");
		builder.append(sampleSubset.equals("") ? "--" : sampleSubset);
		//
		if(preferences.getBoolean(PreferenceConstants.P_PCR_SHOW_TARGET_NAME)) {
			builder.append(" | ");
			builder.append(targetName.equals("") ? "--" : targetName);
		}
	}

	private static void appendCrossingPointInfo(IWell well, StringBuilder builder) {

		builder.append("\n");
		/*
		 * Crossing Points
		 */
		IChannel activeChannel = well.getActiveChannel();
		if(activeChannel == null) {
			/*
			 * All channels
			 */
			Iterator<IChannel> iterator = well.getChannels().values().iterator();
			while(iterator.hasNext()) {
				IChannel channel = iterator.next();
				appendChannelCrossingPoint(channel, builder);
				if(iterator.hasNext()) {
					builder.append(" | ");
				}
			}
		} else {
			/*
			 * Active channel
			 */
			appendChannelCrossingPoint(activeChannel, builder);
		}
	}

	private static void appendChannelCrossingPoint(IChannel channel, StringBuilder builder) {

		if(channel != null) {
			IPoint crossingPoint = channel.getCrossingPoint();
			if(crossingPoint != null && crossingPoint.getX() > 0.0d) {
				DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();
				builder.append(decimalFormat.format(crossingPoint.getX()));
			} else {
				builder.append("--");
			}
		}
	}

	private static int getPosition(IWell well) {

		return well.getPosition().getId() + 1;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof IPlateTableEntry) {
			IPlateTableEntry entry = (IPlateTableEntry)element;
			if(columnIndex == 0) {
				text = entry.getRow();
			} else {
				IWell well = entry.getWells().get(columnIndex);
				text = getText(well);
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return null;
	}
}
