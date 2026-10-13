/*******************************************************************************
 * Copyright (c) 2019, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Matthias Mailänder - add support for MALDI
 * Philip Wenig - add support for TSD
 *******************************************************************************/
package org.eclipse.chemclipse.processing;

import org.eclipse.chemclipse.support.l10n.TranslationSupport;
import org.eclipse.chemclipse.support.text.ILabel;

public enum DataCategory implements ILabel {

	MSD(TranslationSupport.getTranslationService().translate("%DataCategory.MSD", TranslationSupport.getContributorURI(DataCategory.class))), //
	CSD(TranslationSupport.getTranslationService().translate("%DataCategory.CSD", TranslationSupport.getContributorURI(DataCategory.class))), //
	WSD(TranslationSupport.getTranslationService().translate("%DataCategory.WSD", TranslationSupport.getContributorURI(DataCategory.class))), //
	VSD(TranslationSupport.getTranslationService().translate("%DataCategory.VSD", TranslationSupport.getContributorURI(DataCategory.class))), //
	FSD(TranslationSupport.getTranslationService().translate("%DataCategory.FSD", TranslationSupport.getContributorURI(DataCategory.class))), //
	TSD(TranslationSupport.getTranslationService().translate("%DataCategory.TSD", TranslationSupport.getContributorURI(DataCategory.class))), //
	NMR(TranslationSupport.getTranslationService().translate("%DataCategory.NMR", TranslationSupport.getContributorURI(DataCategory.class))), //
	PCR(TranslationSupport.getTranslationService().translate("%DataCategory.PCR", TranslationSupport.getContributorURI(DataCategory.class))), //
	MALDI(TranslationSupport.getTranslationService().translate("%DataCategory.MALDI", TranslationSupport.getContributorURI(DataCategory.class))), //
	MSD_DATABASE(TranslationSupport.getTranslationService().translate("%DataCategory.MSD_DATABASE", TranslationSupport.getContributorURI(DataCategory.class))), //
	/**
	 * Suggests that this Filter can support a wide range of datatypes and content-sensing is the only option to check if the filter, processor or converter can really handle the data or not
	 */
	AUTO_DETECT(TranslationSupport.getTranslationService().translate("%DataCategory.AUTO_DETECT", TranslationSupport.getContributorURI(DataCategory.class))); //$NON-NLS-1$

	private String label;

	private DataCategory(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}

	/**
	 * This DataCategory was formerly used to map VSD (vibrational spectroscopy chromatograms)
	 * 
	 * @return
	 */
	public static String ISD_LEGACY() {

		return "ISD";
	}

	public static DataCategory[] chromatographyCategories() {

		return new DataCategory[]{CSD, MSD, VSD, WSD, FSD};
	}

	public static DataCategory[] spectroscopyCategories() {

		return new DataCategory[]{NMR};
	}

	public static DataCategory[] spectrometryCategories() {

		return new DataCategory[]{MALDI};
	}
}
