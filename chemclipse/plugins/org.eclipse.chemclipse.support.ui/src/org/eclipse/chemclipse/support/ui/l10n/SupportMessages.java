/*******************************************************************************
 * Copyright (c) 2015, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - new API and re-implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.l10n;

import org.eclipse.osgi.util.NLS;

public class SupportMessages extends NLS {

	public static String add;
	public static String allowedRange;
	public static String allowedRetentionTimeRange;
	public static String clear;
	public static String clipboardTableDefaultSorting;
	public static String copyClipboard;
	public static String clipboardSettings;
	public static String copyHeaderToClipboard;
	public static String copyHeaderToolTip;
	public static String noClipboardSettingsAvailable;
	public static String tableColumns;
	public static String deselectAll;
	public static String enterIon;
	public static String errorMessageEven;
	public static String errorMessageOdd;
	public static String errorMessageOddIncludingZero;
	public static String exportTableSelection;
	public static String fileName;
	public static String filePath;
	public static String files;
	public static String inputMustBeIntegerOrIntegerRange;
	public static String inputMustBeValidRange;
	public static String ions;
	public static String ionValueAlreadyExists;
	public static String labelCategory;
	public static String labelDataType;
	public static String labelDescription;
	public static String labelID;
	public static String labellabelNA;
	public static String labelName;
	public static String maximumAllowedUndoSteps;
	public static String processorImage;
	public static String processorQuickAccess;
	public static String remove;
	public static String removeAllFilesFromList;
	public static String removeTICbeforeAddingIonValues;
	public static String searchAvailableProcessorItems;
	public static String selectAll;
	public static String selectEntriesinList;
	public static String selectFile;
	public static String selectProcessorToolbarItems;
	public static String settings;
	public static String showLabelsInToolbar;
	public static String showText;
	public static String standardValuesWaterNitrogenSolventTailingColumnBleed;
	public static String supportSettings;
	public static String tablePopUpMenu;
	public static String ticMustNotBeAdded;
	public static String toolbar;
	public static String columns;
	public static String resetColumnOrder;
	public static String resetColumnWidth;
	//
	static {
		NLS.initializeMessages("org.eclipse.chemclipse.support.ui.l10n.messages", SupportMessages.class); //$NON-NLS-1$
	}

	private SupportMessages() {

	}
}