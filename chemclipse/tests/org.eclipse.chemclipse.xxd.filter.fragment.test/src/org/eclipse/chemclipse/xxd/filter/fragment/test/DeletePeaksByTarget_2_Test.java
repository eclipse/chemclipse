/*******************************************************************************
 * Copyright (c) 2021, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.fragment.test;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.implementation.Peak;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.model.implementation.PeakModel;
import org.eclipse.chemclipse.model.implementation.Scan;
import org.eclipse.chemclipse.xxd.filter.peaks.DeletePeaksByTargetFilter;
import org.eclipse.chemclipse.xxd.filter.peaks.settings.DeletePeaksByTargetFilterSettings;
import org.eclipse.chemclipse.xxd.filter.support.PeaksDeleteOption;

import junit.framework.TestCase;

public class DeletePeaksByTarget_2_Test extends TestCase {

	private IPeak peak;
	private PeaksDeleteOption peaksDeleteOption = PeaksDeleteOption.CAS;
	private String value = "1115-07-7";
	private String regex = "(.*)(-07-)(.*)";

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * Create a mock peak.
		 */
		IScan peakMaximum = new Scan(1000);
		IPeakIntensityValues peakIntensityValues = new PeakIntensityValues();
		peakIntensityValues.addIntensityValue(100, 10.f);
		peakIntensityValues.addIntensityValue(200, 30.f);
		peakIntensityValues.addIntensityValue(300, 80.f);
		peakIntensityValues.addIntensityValue(400, 100.f);
		peakIntensityValues.addIntensityValue(500, 70.f);
		peakIntensityValues.addIntensityValue(600, 40.f);
		IPeakModel peakModel = new PeakModel(peakMaximum, peakIntensityValues, 0.0f, 0.0f);
		peakModel.setStrictModel(true);
		peak = new Peak(peakModel);
		//
		ILibraryInformation libraryInformation = new LibraryInformation();
		libraryInformation.setName("");
		libraryInformation.setCasNumber(value);
		IIdentificationTarget identificationTarget = new IdentificationTarget(libraryInformation, ComparisonResult.COMPARISON_RESULT_BEST_MATCH);
		peak.getTargets().add(identificationTarget);
	}

	@Override
	protected void tearDown() throws Exception {

		peak = null;
		super.tearDown();
	}

	public void test1() {

		DeletePeaksByTargetFilterSettings configuration = new DeletePeaksByTargetFilterSettings();
		configuration.setPeaksDeleteOption(peaksDeleteOption);
		configuration.setValue(value);
		configuration.setCaseSensitive(false);
		configuration.setRegularExpression(false);
		//
		assertTrue(DeletePeaksByTargetFilter.isDeletePeak(peak, configuration));
	}

	public void test2() {

		DeletePeaksByTargetFilterSettings configuration = new DeletePeaksByTargetFilterSettings();
		configuration.setPeaksDeleteOption(peaksDeleteOption);
		configuration.setValue(value);
		configuration.setCaseSensitive(true);
		configuration.setRegularExpression(false);
		//
		assertTrue(DeletePeaksByTargetFilter.isDeletePeak(peak, configuration));
	}

	public void test3() {

		assertTrue(value.matches(regex));
		//
		DeletePeaksByTargetFilterSettings configuration = new DeletePeaksByTargetFilterSettings();
		configuration.setPeaksDeleteOption(peaksDeleteOption);
		configuration.setValue(regex);
		configuration.setCaseSensitive(false);
		configuration.setRegularExpression(true);
		//
		assertTrue(DeletePeaksByTargetFilter.isDeletePeak(peak, configuration));
	}

	public void test4() {

		assertTrue(value.matches(regex));
		//
		DeletePeaksByTargetFilterSettings configuration = new DeletePeaksByTargetFilterSettings();
		configuration.setPeaksDeleteOption(peaksDeleteOption);
		configuration.setValue(regex);
		configuration.setCaseSensitive(true); // Has no effect when regex is activated.
		configuration.setRegularExpression(true);
		//
		assertTrue(DeletePeaksByTargetFilter.isDeletePeak(peak, configuration));
	}

	public void test5() {

		DeletePeaksByTargetFilterSettings configuration = new DeletePeaksByTargetFilterSettings();
		configuration.setPeaksDeleteOption(peaksDeleteOption);
		configuration.setValue("");
		configuration.setCaseSensitive(false);
		configuration.setRegularExpression(false);
		//
		assertFalse(DeletePeaksByTargetFilter.isDeletePeak(peak, configuration));
	}

	public void test6() {

		DeletePeaksByTargetFilterSettings configuration = new DeletePeaksByTargetFilterSettings();
		configuration.setPeaksDeleteOption(peaksDeleteOption);
		configuration.setValue("");
		configuration.setCaseSensitive(true);
		configuration.setRegularExpression(false);
		//
		assertFalse(DeletePeaksByTargetFilter.isDeletePeak(peak, configuration));
	}

	public void test7() {

		DeletePeaksByTargetFilterSettings configuration = new DeletePeaksByTargetFilterSettings();
		configuration.setPeaksDeleteOption(peaksDeleteOption);
		configuration.setValue("");
		configuration.setCaseSensitive(false);
		configuration.setRegularExpression(true);
		//
		assertFalse(DeletePeaksByTargetFilter.isDeletePeak(peak, configuration));
	}

	public void test8() {

		DeletePeaksByTargetFilterSettings configuration = new DeletePeaksByTargetFilterSettings();
		configuration.setPeaksDeleteOption(peaksDeleteOption);
		configuration.setValue("");
		configuration.setCaseSensitive(true);
		configuration.setRegularExpression(true);
		//
		assertFalse(DeletePeaksByTargetFilter.isDeletePeak(peak, configuration));
	}

	public void test9() {

		DeletePeaksByTargetFilterSettings configuration = new DeletePeaksByTargetFilterSettings();
		configuration.setPeaksDeleteOption(peaksDeleteOption);
		configuration.setValue(value.toLowerCase()); // CAS is just numbers and "-", case doesn't matter
		configuration.setCaseSensitive(true);
		configuration.setRegularExpression(false);
		//
		assertTrue(DeletePeaksByTargetFilter.isDeletePeak(peak, configuration));
	}

	public void test10() {

		DeletePeaksByTargetFilterSettings configuration = new DeletePeaksByTargetFilterSettings();
		configuration.setPeaksDeleteOption(peaksDeleteOption);
		configuration.setValue(null);
		configuration.setCaseSensitive(false);
		configuration.setRegularExpression(false);
		//
		assertFalse(DeletePeaksByTargetFilter.isDeletePeak(peak, configuration));
	}

	public void test11() {

		DeletePeaksByTargetFilterSettings configuration = new DeletePeaksByTargetFilterSettings();
		configuration.setPeaksDeleteOption(peaksDeleteOption);
		configuration.setValue("");
		configuration.setCaseSensitive(false);
		configuration.setRegularExpression(false);
		//
		assertFalse(DeletePeaksByTargetFilter.isDeletePeak(null, configuration));
	}

	public void test12() {

		DeletePeaksByTargetFilterSettings configuration = new DeletePeaksByTargetFilterSettings();
		configuration.setPeaksDeleteOption(peaksDeleteOption);
		configuration.setValue(null);
		configuration.setCaseSensitive(false);
		configuration.setRegularExpression(false);
		//
		assertFalse(DeletePeaksByTargetFilter.isDeletePeak(null, configuration));
	}
}