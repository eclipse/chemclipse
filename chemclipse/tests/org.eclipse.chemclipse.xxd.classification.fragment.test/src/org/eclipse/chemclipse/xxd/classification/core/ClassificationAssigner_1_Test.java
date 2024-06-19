/*******************************************************************************
 * Copyright (c) 2022, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.classification.core;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.implementation.Peak;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.model.implementation.PeakModel;
import org.eclipse.chemclipse.model.implementation.Scan;
import org.eclipse.chemclipse.xxd.classification.model.ClassificationRule;
import org.eclipse.chemclipse.xxd.classification.model.Reference;
import org.eclipse.chemclipse.xxd.classification.settings.ClassifierAssignFilterSettings;

import junit.framework.TestCase;

public class ClassificationAssigner_1_Test extends TestCase {

	private static final String NAME = "Heptanoic acid, 3-methylbutyl ester";
	private static final String CAS = "109-25-1";
	private static final String REFERENCE_ID = "ID-202206";
	private static final String CLASSIFICATION = "Ester";
	//
	private IPeak peak;
	private IIdentificationTarget identificationTarget;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		//
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
		identificationTarget = createIdentificationTarget();
		peak.getTargets().add(identificationTarget);
		//
		assertTrue(peak.getClassifier().isEmpty());
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test0a() {

		ClassificationRule rule = createClassificationRule("", CLASSIFICATION);
		ClassifierAssignFilterSettings settings = createSettings(false, false, false, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertTrue(peak.getClassifier().isEmpty());
	}

	public void test0b() {

		ClassificationRule rule = createClassificationRule("Heptanoic acid, 3-methylbutyl ester", "");
		ClassifierAssignFilterSettings settings = createSettings(false, false, false, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertTrue(peak.getClassifier().isEmpty());
	}

	public void test0c() {

		ClassificationRule rule = createClassificationRule("", "");
		ClassifierAssignFilterSettings settings = createSettings(false, false, false, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertTrue(peak.getClassifier().isEmpty());
	}

	public void test1a() {

		ClassificationRule rule = createClassificationRule("Heptanoic acid, 3-methylbutyl ester", CLASSIFICATION);
		ClassifierAssignFilterSettings settings = createSettings(false, false, false, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertEquals(peak.getClassifier().size(), 1);
		assertTrue(peak.getClassifier().contains(CLASSIFICATION));
	}

	public void test1b() {

		ClassificationRule rule = createClassificationRule("HEPTANOIC acid, 3-methylbutyl ester", CLASSIFICATION);
		ClassifierAssignFilterSettings settings = createSettings(false, false, false, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertEquals(peak.getClassifier().size(), 1);
		assertTrue(peak.getClassifier().contains(CLASSIFICATION));
	}

	public void test2() {

		ClassificationRule rule = createClassificationRule("Heptanoic acid, 3-methylbutyl ester", CLASSIFICATION);
		ClassifierAssignFilterSettings settings = createSettings(true, false, false, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertEquals(peak.getClassifier().size(), 1);
		assertTrue(peak.getClassifier().contains(CLASSIFICATION));
	}

	public void test3() {

		ClassificationRule rule = createClassificationRule("Heptanoic acid, 3-methylbutyl ester", CLASSIFICATION);
		ClassifierAssignFilterSettings settings = createSettings(true, true, false, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertEquals(peak.getClassifier().size(), 1);
		assertTrue(peak.getClassifier().contains(CLASSIFICATION));
	}

	public void test4() {

		ClassificationRule rule = createClassificationRule("Heptanoic acid, 3-methylbutyl ester", CLASSIFICATION);
		ClassifierAssignFilterSettings settings = createSettings(true, true, true, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertEquals(peak.getClassifier().size(), 1);
		assertTrue(peak.getClassifier().contains(CLASSIFICATION));
	}

	public void test5a() {

		ClassificationRule rule = createClassificationRule("3-methylbutyl", CLASSIFICATION);
		ClassifierAssignFilterSettings settings = createSettings(false, false, false, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertTrue(peak.getClassifier().isEmpty());
	}

	public void test5b() {

		ClassificationRule rule = createClassificationRule("3-methylbutyl", CLASSIFICATION);
		ClassifierAssignFilterSettings settings = createSettings(true, false, false, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertTrue(peak.getClassifier().isEmpty());
	}

	public void test5c() {

		ClassificationRule rule = createClassificationRule("3-methylbutyl", CLASSIFICATION);
		ClassifierAssignFilterSettings settings = createSettings(false, true, false, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertEquals(peak.getClassifier().size(), 1);
		assertTrue(peak.getClassifier().contains(CLASSIFICATION));
	}

	public void test5d() {

		ClassificationRule rule = createClassificationRule("3-METHYLBUTYL", CLASSIFICATION);
		ClassifierAssignFilterSettings settings = createSettings(false, true, false, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertEquals(peak.getClassifier().size(), 1);
		assertTrue(peak.getClassifier().contains(CLASSIFICATION));
	}

	public void test5e() {

		ClassificationRule rule = createClassificationRule("3-methylbutyl", CLASSIFICATION);
		ClassifierAssignFilterSettings settings = createSettings(true, true, false, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertEquals(peak.getClassifier().size(), 1);
		assertTrue(peak.getClassifier().contains(CLASSIFICATION));
	}

	public void test5f() {

		ClassificationRule rule = createClassificationRule("(.*)(3-methylbutyl)(.*)", CLASSIFICATION);
		ClassifierAssignFilterSettings settings = createSettings(true, false, true, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertEquals(peak.getClassifier().size(), 1);
		assertTrue(peak.getClassifier().contains(CLASSIFICATION));
	}

	public void test5g() {

		ClassificationRule rule = createClassificationRule("(.*)(3-methylbutyl)", CLASSIFICATION);
		ClassifierAssignFilterSettings settings = createSettings(true, false, true, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertTrue(peak.getClassifier().isEmpty());
	}

	public void test5h() {

		ClassificationRule rule = createClassificationRule("(.*)(3-methylbutyl)", CLASSIFICATION);
		ClassifierAssignFilterSettings settings = createSettings(true, true, true, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertEquals(peak.getClassifier().size(), 1);
		assertTrue(peak.getClassifier().contains(CLASSIFICATION));
	}

	public void test5i() {

		ClassificationRule rule = createClassificationRule("(.*)(3-Methylbutyl)(.*)", CLASSIFICATION);
		ClassifierAssignFilterSettings settings = createSettings(false, false, true, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertEquals(peak.getClassifier().size(), 1);
		assertTrue(peak.getClassifier().contains(CLASSIFICATION));
	}

	public void test5j() {

		ClassificationRule rule = createClassificationRule("(.*)(3-Methylbutyl)", CLASSIFICATION);
		ClassifierAssignFilterSettings settings = createSettings(false, false, true, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertTrue(peak.getClassifier().isEmpty());
	}

	public void test5k() {

		ClassificationRule rule = createClassificationRule("(.*)(3-Methylbutyl)", CLASSIFICATION);
		ClassifierAssignFilterSettings settings = createSettings(false, true, true, rule);
		ClassificationAssigner.apply(peak, settings);
		//
		assertEquals(peak.getClassifier().size(), 1);
		assertTrue(peak.getClassifier().contains(CLASSIFICATION));
	}

	private IIdentificationTarget createIdentificationTarget() {

		ILibraryInformation libraryInformation = new LibraryInformation();
		libraryInformation.setName(NAME);
		libraryInformation.setCasNumber(CAS);
		libraryInformation.setReferenceIdentifier(REFERENCE_ID);
		IComparisonResult comparisonResult = ComparisonResult.COMPARISON_RESULT_BEST_MATCH;
		return new IdentificationTarget(libraryInformation, comparisonResult);
	}

	private ClassificationRule createClassificationRule(String searchExpression, String classification) {

		return new ClassificationRule(searchExpression, classification, Reference.NAME);
	}

	private ClassifierAssignFilterSettings createSettings(boolean caseSensitive, boolean matchPartly, boolean useRegularExpression, ClassificationRule classificationRule) {

		ClassifierAssignFilterSettings settings = new ClassifierAssignFilterSettings();
		//
		settings.setCaseSensitive(caseSensitive);
		settings.setMatchPartly(matchPartly);
		settings.setUseRegularExpression(useRegularExpression);
		settings.getClassificationDictionary().add(classificationRule);
		//
		return settings;
	}
}