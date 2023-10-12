/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.IRatingSupplier;
import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Image;

public class TargetsLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String VERIFIED = ExtensionMessages.verified;
	public static final String NAME = ExtensionMessages.name;
	public static final String MATCH_FACTOR = ExtensionMessages.matchFactor;
	public static final String REVERSE_MATCH_FACTOR = ExtensionMessages.reverseMatchFactor;
	public static final String MATCH_FACTOR_DIRECT = ExtensionMessages.matchFactorDirect;
	public static final String REVERSE_MATCH_FACTOR_DIRECT = ExtensionMessages.reverseMatchFactorDirect;
	public static final String PROBABILITY = ExtensionMessages.probability;
	public static final String MOL_WEIGHT = ExtensionMessages.molWeight;
	public static final String EXACT_MASS = ExtensionMessages.excactMass;
	public static final String ADVICE = ExtensionMessages.advice;
	public static final String IDENTIFIER = ExtensionMessages.identifier;
	public static final String MISCELLANEOUS = ExtensionMessages.miscellaneous;
	public static final String DATABASE = ExtensionMessages.database;
	public static final String DATABASE_INDEX = ExtensionMessages.databaseIndex;
	public static final String RATING = ExtensionMessages.rating;
	public static final String CAS = "CAS";
	public static final String COMMENTS = ExtensionMessages.comments;
	public static final String FORMULA = ExtensionMessages.formula;
	public static final String SMILES = "SMILES";
	public static final String INCHI = "InChI";
	public static final String INCHI_KEY = "InChI Key";
	public static final String CONTRIBUTOR = ExtensionMessages.contributor;
	public static final String RETENTION_TIME = ExtensionMessages.retentionTime;
	public static final String RETENTION_INDEX = ExtensionMessages.retentionIndex;
	public static final String REFERENCE_ID = ExtensionMessages.referenceID;
	public static final String INLIB_FACTOR = ExtensionMessages.inLibFactor;
	//
	public static final int INDEX_RATING = 1;
	public static final int INDEX_NAME = 2;
	public static final int INDEX_RETENTION_TIME = 23;
	public static final int INDEX_RETENTION_INDEX = 24;
	//
	private static final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	//
	public static final String[] TITLES = { //
			VERIFIED, //
			RATING, //
			NAME, //
			CAS, //
			MATCH_FACTOR, //
			REVERSE_MATCH_FACTOR, //
			MATCH_FACTOR_DIRECT, //
			REVERSE_MATCH_FACTOR_DIRECT, //
			PROBABILITY, //
			FORMULA, //
			SMILES, //
			INCHI, //
			INCHI_KEY, //
			MOL_WEIGHT, //
			EXACT_MASS, //
			ADVICE, //
			IDENTIFIER, //
			MISCELLANEOUS, //
			COMMENTS, //
			DATABASE, //
			DATABASE_INDEX, //
			CONTRIBUTOR, //
			REFERENCE_ID, //
			RETENTION_TIME, //
			RETENTION_INDEX, //
			INLIB_FACTOR //
	};
	//
	public static final int[] BOUNDS = { //
			30, //
			30, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100 //
	};

	public static String getRetentionTimeText(ILibraryInformation libraryInformation, Integer retentionTime) {

		DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");
		String deltaRetentionTime = "";
		if(retentionTime != null) {
			if(preferenceStore.getBoolean(PreferenceConstants.P_TARGETS_TABLE_SHOW_DEVIATION_RT)) {
				int delta = libraryInformation.getRetentionTime() - retentionTime;
				deltaRetentionTime = " [" + decimalFormat.format(delta / IChromatogramOverview.MINUTE_CORRELATION_FACTOR) + "]";
			}
		}
		/*
		 * Label
		 */
		String libraryRetentionTime = decimalFormat.format(libraryInformation.getRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
		return libraryRetentionTime + deltaRetentionTime;
	}

	public static String getRetentionIndexText(ILibraryInformation libraryInformation, Float retentionIndex) {

		DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");
		DecimalFormat decimalFormatInteger = ValueFormat.getDecimalFormatEnglish("0");
		boolean showRetentionIndexWithoutDecimals = PreferenceSupplier.showRetentionIndexWithoutDecimals();
		//
		String deltaRetentionIndex = "";
		if(retentionIndex != null) {
			if(preferenceStore.getBoolean(PreferenceConstants.P_TARGETS_TABLE_SHOW_DEVIATION_RI)) {
				float delta = libraryInformation.getRetentionIndex() - retentionIndex;
				if(showRetentionIndexWithoutDecimals) {
					deltaRetentionIndex = " [" + decimalFormatInteger.format(delta) + "]";
				} else {
					deltaRetentionIndex = " [" + decimalFormat.format(delta) + "]";
				}
			}
		}
		/*
		 * Label
		 */
		String libraryRetentionIndex = "";
		if(showRetentionIndexWithoutDecimals) {
			libraryRetentionIndex = decimalFormatInteger.format(libraryInformation.getRetentionIndex());
		} else {
			libraryRetentionIndex = decimalFormat.format(libraryInformation.getRetentionIndex());
		}
		//
		return libraryRetentionIndex + deltaRetentionIndex;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			/*
			 * CheckBox
			 */
			if(element instanceof IIdentificationTarget identificationTarget) {
				if(identificationTarget.isVerified()) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED, IApplicationImageProvider.SIZE_16x16);
				} else {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DESELECTED, IApplicationImageProvider.SIZE_16x16);
				}
			}
		} else if(columnIndex == INDEX_RATING) {
			/*
			 * Rating
			 */
			if(element instanceof IIdentificationTarget identificationTarget) {
				IComparisonResult comparisonResult = identificationTarget.getComparisonResult();
				IRatingSupplier ratingSupplier = comparisonResult.getRatingSupplier();
				//
				String fileName;
				switch(ratingSupplier.getStatus()) {
					case VERY_GOOD:
						fileName = IApplicationImage.IMAGE_RATING_VERY_GOOD;
						break;
					case GOOD:
						fileName = IApplicationImage.IMAGE_RATING_GOOD;
						break;
					case AVERAGE:
						fileName = IApplicationImage.IMAGE_RATING_AVERAGE;
						break;
					case BAD:
						fileName = IApplicationImage.IMAGE_RATING_BAD;
						break;
					case VERY_BAD:
						fileName = IApplicationImage.IMAGE_RATING_VERY_BAD;
						break;
					default:
						fileName = "";
						break;
				}
				//
				if(!fileName.isEmpty()) {
					return ApplicationImageFactory.getInstance().getImage(fileName, IApplicationImage.SIZE_16x16);
				}
			}
		} else if(columnIndex == INDEX_NAME) {
			/*
			 * Entry
			 */
			return getImage(element);
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		DecimalFormat decimalFormat = getDecimalFormat();
		//
		String text = "";
		if(element instanceof IIdentificationTarget identificationTarget) {
			ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
			IComparisonResult comparisonResult = identificationTarget.getComparisonResult();
			//
			switch(columnIndex) {
				case 0:
					text = "";
					break;
				case 1: // Rating
					text = "";
					break;
				case 2: // Name
					text = libraryInformation.getName();
					break;
				case 3: // CAS
					text = libraryInformation.getCasNumber();
					break;
				case 4: // MQ
					text = decimalFormat.format(comparisonResult.getMatchFactor());
					break;
				case 5: // RMQ
					text = decimalFormat.format(comparisonResult.getReverseMatchFactor());
					break;
				case 6: // MQD
					text = decimalFormat.format(comparisonResult.getMatchFactorDirect());
					break;
				case 7: // RMQD
					text = decimalFormat.format(comparisonResult.getReverseMatchFactorDirect());
					break;
				case 8: // Probability
					text = decimalFormat.format(comparisonResult.getProbability());
					break;
				case 9: // Formula
					text = libraryInformation.getFormula();
					break;
				case 10: // SMILES
					text = libraryInformation.getSmiles();
					break;
				case 11: // InChI
					text = libraryInformation.getInChI();
					break;
				case 12: // InChI Key
					text = libraryInformation.getInChIKey();
					break;
				case 13: // Mol Weight
					text = decimalFormat.format(libraryInformation.getMolWeight());
					break;
				case 14: // Exact Mass
					text = decimalFormat.format(libraryInformation.getExactMass());
					break;
				case 15: // Advise
					text = comparisonResult.getRatingSupplier().getAdvise();
					break;
				case 16: // Identifier
					text = identificationTarget.getIdentifier();
					break;
				case 17: // Miscellaneous
					text = libraryInformation.getMiscellaneous();
					break;
				case 18: // Comments
					text = libraryInformation.getComments();
					break;
				case 19:
					text = libraryInformation.getDatabase();
					break;
				case 20:
					text = Integer.toString(libraryInformation.getDatabaseIndex());
					break;
				case 21:
					text = libraryInformation.getContributor();
					break;
				case 22:
					text = libraryInformation.getReferenceIdentifier();
					break;
				case 23:
					text = getRetentionTimeText(libraryInformation, null);
					break;
				case 24:
					text = getRetentionIndexText(libraryInformation, null);
					break;
				case 25:
					text = decimalFormat.format(comparisonResult.getInLibFactor());
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TARGETS, IApplicationImageProvider.SIZE_16x16);
	}
}