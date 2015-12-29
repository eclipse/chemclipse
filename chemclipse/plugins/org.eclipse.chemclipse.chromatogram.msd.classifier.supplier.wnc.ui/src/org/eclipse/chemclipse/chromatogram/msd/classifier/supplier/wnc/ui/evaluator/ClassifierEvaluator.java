/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.evaluator;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.classifier.core.ChromatogramClassifier;
import org.eclipse.chemclipse.chromatogram.msd.classifier.processing.IChromatogramClassifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIons;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.settings.IWncClassifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.settings.WncClassifierSettings;
import org.eclipse.chemclipse.model.processor.AbstractChromatogramProcessor;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ClassifierEvaluator extends AbstractChromatogramProcessor implements IRunnableWithProgress {

	private static final String description = "WNC Classifier";
	private static final String FILTER_ID = "org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc";

	public ClassifierEvaluator(IChromatogramSelectionMSD chromatogramSelection) {
		super(chromatogramSelection);
	}

	@Override
	public void execute(IProgressMonitor monitor) {

		final IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			/*
			 * The classifier settings.
			 */
			IWncClassifierSettings chromatogramClassifierSettings = new WncClassifierSettings();
			IWncIons wncIons = PreferenceSupplier.getWNCIons();
			chromatogramClassifierSettings.getWNCIons().add(wncIons);
			/*
			 * Apply the classifier.
			 */
			final IChromatogramClassifierProcessingInfo processingInfo = ChromatogramClassifier.applyClassifier((IChromatogramSelectionMSD)chromatogramSelection, chromatogramClassifierSettings, FILTER_ID, monitor);
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, true);
			chromatogramSelection.update(true);
		}
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		/*
		 * The doOperation calls the execute of the chromatogram inspector
		 * method.<br/> Why not doing it directly? Model and GUI should be
		 * handled separately.
		 */
		try {
			monitor.beginTask("WNC Classifier", IProgressMonitor.UNKNOWN);
			getChromatogramSelection().getChromatogram().doOperation(this, false, false, monitor);
		} finally {
			monitor.done();
		}
	}
}
