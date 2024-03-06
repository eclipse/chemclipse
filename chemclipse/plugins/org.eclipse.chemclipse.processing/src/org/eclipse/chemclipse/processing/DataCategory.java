/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	MSD(TranslationSupport.getTranslationService().translate("%DataCategory.MSD", Activator.getContributorURI())), //
	CSD(TranslationSupport.getTranslationService().translate("%DataCategory.CSD", Activator.getContributorURI())), //
	WSD(TranslationSupport.getTranslationService().translate("%DataCategory.WSD", Activator.getContributorURI())), //
	VSD(TranslationSupport.getTranslationService().translate("%DataCategory.VSD", Activator.getContributorURI())), //
	TSD(TranslationSupport.getTranslationService().translate("%DataCategory.TSD", Activator.getContributorURI())), //
	NMR(TranslationSupport.getTranslationService().translate("%DataCategory.NMR", Activator.getContributorURI())), //
	PCR(TranslationSupport.getTranslationService().translate("%DataCategory.PCR", Activator.getContributorURI())), //
	MALDI(TranslationSupport.getTranslationService().translate("%DataCategory.MALDI", Activator.getContributorURI())), //
	MSD_DATABASE(TranslationSupport.getTranslationService().translate("%DataCategory.MSD_DATABASE", Activator.getContributorURI())), //
	/**
	 * Suggests that this Filter can support a wide range of datatypes and content-sensing is the only option to check if the filter, processor or converter can really handle the data or not
	 */
	AUTO_DETECT(TranslationSupport.getTranslationService().translate("%DataCategory.AUTO_DETECT", Activator.getContributorURI())); //$NON-NLS-1$

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

		return new DataCategory[]{CSD, MSD, VSD, WSD};
	}

	public static DataCategory[] spectroscopyCategories() {

		return new DataCategory[]{NMR};
	}

	public static DataCategory[] spectrometryCategories() {

		return new DataCategory[]{MALDI};
	}
}