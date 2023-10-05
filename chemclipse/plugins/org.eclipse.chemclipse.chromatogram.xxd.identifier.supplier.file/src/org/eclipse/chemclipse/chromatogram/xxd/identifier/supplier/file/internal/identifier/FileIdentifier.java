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
 * Christoph Läubrich - add validationmethod/getter, optimize progressmonitor usage and retrieval of mass spectrum comparator, unify peak/scan identification methods
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.internal.identifier;

import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;
import java.util.function.Function;

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.IMassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.IMassSpectrumComparisonSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.support.DatabasesCache;
import org.eclipse.chemclipse.chromatogram.msd.identifier.support.TargetBuilderMSD;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.IFileIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.ILibraryIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.DeltaCalculationSupport;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.model.identifier.MatchConstraints;
import org.eclipse.chemclipse.model.identifier.PeakIdentificationResults;
import org.eclipse.chemclipse.model.identifier.PenaltyCalculationSupport;
import org.eclipse.chemclipse.model.support.LimitSupport;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.support.settings.UserManagement;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.util.FileListUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;

public class FileIdentifier {

	private static final Logger logger = Logger.getLogger(FileIdentifier.class);
	//
	public static final String IDENTIFIER = "File Identifier";
	//
	private static final Comparator<IComparisonResult> RESULT_COMPARATOR = Collections.reverseOrder(IComparisonResult.MATCH_FACTOR_COMPARATOR);
	private static final TargetBuilderMSD TARGETBUILDER = new TargetBuilderMSD();
	//
	private final DatabasesCache databasesCache = new DatabasesCache(PreferenceSupplier.getMassSpectraFiles());

	public IMassSpectra runIdentification(List<IScanMSD> massSpectraList, ILibraryIdentifierSettings fileIdentifierSettings, IProgressMonitor monitor) throws FileNotFoundException {

		SubMonitor subMonitor = SubMonitor.convert(monitor, "Running mass spectra identification", 100);
		/*
		 * Pre-filter the mass spectra to identify.
		 */
		float limitMatchFactor = fileIdentifierSettings.getLimitMatchFactor();
		IMassSpectra massSpectra = new MassSpectra();
		for(IScanMSD scanMSD : massSpectraList) {
			if(LimitSupport.doIdentify(scanMSD.getTargets(), limitMatchFactor)) {
				massSpectra.addMassSpectrum(scanMSD);
			}
		}
		/*
		 * The alternate identifier is used, when another plugin tries to use this file identification process.
		 * The LibraryService uses the identifier to get a mass spectrum of a given target.
		 * It would then use this plugin instead of the plugin who used this identifier.
		 */
		String identifier = IDENTIFIER;
		String alternateIdentifierId = fileIdentifierSettings.getAlternateIdentifierId();
		if(alternateIdentifierId != null && !alternateIdentifierId.isEmpty()) {
			identifier = alternateIdentifierId;
		}
		/*
		 * Load the mass spectra database only if the raw file or its content has changed.
		 */
		List<String> files = extractFiles(fileIdentifierSettings.getMassSpectraFiles());
		Map<String, IMassSpectra> databases = databasesCache.getDatabases(files, subMonitor.split(10));
		subMonitor.setWorkRemaining(databases.size() * 100);
		for(Map.Entry<String, IMassSpectra> database : databases.entrySet()) {
			compareMassSpectraAgainstDatabase(massSpectra.getList(), database.getValue().getList(), fileIdentifierSettings, identifier, database.getKey(), subMonitor.split(100, SubMonitor.SUPPRESS_NONE));
		}
		//
		return massSpectra;
	}

	/**
	 * Run the peak identification.
	 *
	 * @param peaks
	 * @param peakIdentifierSettings
	 * @param processingInfo
	 * @param monitor
	 * @return {@link IPeakIdentificationResults}
	 * @throws FileNotFoundException
	 */
	public IPeakIdentificationResults runPeakIdentification(List<? extends IPeakMSD> peaks, PeakIdentifierSettings peakIdentifierSettings, IProcessingInfo<?> processingInfo, IProgressMonitor monitor) throws FileNotFoundException {

		SubMonitor subMonitor = SubMonitor.convert(monitor, "Running mass spectra identification", 100);
		/*
		 * Pre-filter the mass spectra to identify.
		 */
		float limitMatchFactor = peakIdentifierSettings.getLimitMatchFactor();
		List<IPeakMSD> peaksToIdentify = new ArrayList<>();
		for(IPeakMSD peak : peaks) {
			if(LimitSupport.doIdentify(peak.getTargets(), limitMatchFactor)) {
				peaksToIdentify.add(peak);
			}
		}
		/*
		 * The alternate identifier is used, when another plugin tries to use this file identification process.
		 * The LibraryService uses the identifier to get a mass spectrum of a given target.
		 * It would then use this plugin instead of the plugin who used this identifier.
		 */
		IPeakIdentificationResults identificationResults = new PeakIdentificationResults();
		String identifier = IDENTIFIER;
		String alternateIdentifierId = peakIdentifierSettings.getAlternateIdentifierId();
		if(alternateIdentifierId != null && !alternateIdentifierId.isEmpty()) {
			identifier = alternateIdentifierId;
		}
		/*
		 * Load the mass spectra database only if the raw file or its content has changed.
		 */
		List<String> files = extractFiles(peakIdentifierSettings.getMassSpectraFiles());
		Map<String, IMassSpectra> databases = databasesCache.getDatabases(files, subMonitor.split(10));
		subMonitor.setWorkRemaining(databases.size() * 100);
		for(Map.Entry<String, IMassSpectra> database : databases.entrySet()) {
			comparePeaksAgainstDatabase(peaksToIdentify, database.getValue().getList(), peakIdentifierSettings, identifier, database.getKey(), subMonitor.split(100, SubMonitor.SUPPRESS_NONE));
		}
		//
		return identificationResults;
	}

	private List<String> extractFiles(String massSpectraFiles) {

		FileListUtil fileListUtil = new FileListUtil();
		List<String> files = fileListUtil.getFiles(massSpectraFiles);
		//
		logger.info(IDENTIFIER);
		for(String file : files) {
			logger.info("Use: " + file);
		}
		//
		return files;
	}

	/**
	 * Returns identified mass spectra from the database.
	 *
	 * @param identificationTarget
	 * @param monitor
	 * @return {@link IMassSpectra}
	 */
	public IMassSpectra getMassSpectra(IIdentificationTarget identificationTarget, IProgressMonitor monitor) {

		IMassSpectra massSpectra = new MassSpectra();
		if(isValid(identificationTarget)) {
			/*
			 * Extract the target library information.
			 * Old *.ocb version don't store the identifier id.
			 * Hence, try to get mass spectrum anyhow.
			 */
			massSpectra.addMassSpectra(databasesCache.getDatabaseMassSpectra(identificationTarget, monitor));
		}
		//
		return massSpectra;
	}

	public boolean isValid(IIdentificationTarget identificationTarget) {

		if(identificationTarget != null) {
			String id = identificationTarget.getIdentifier();
			return IDENTIFIER.equals(id) || "".equals(id);
		}
		return false;
	}

	public DatabasesCache getDatabasesCache() {

		return databasesCache;
	}

	public static int compareMassSpectraAgainstDatabase(List<? extends IScanMSD> unknownList, List<? extends IScanMSD> references, ILibraryIdentifierSettings fileIdentifierSettings, String identifier, String databaseName, IProgressMonitor monitor) {

		return compareAgainstDatabase(unknownList, scan -> scan, references, fileIdentifierSettings, identifier, databaseName, monitor);
	}

	public static int comparePeaksAgainstDatabase(List<? extends IPeakMSD> unknownList, List<IScanMSD> references, PeakIdentifierSettings fileIdentifierSettings, String identifier, String databaseName, IProgressMonitor monitor) {

		return compareAgainstDatabase(unknownList, peak -> peak.getPeakModel().getPeakMassSpectrum(), references, fileIdentifierSettings, identifier, databaseName, monitor);
	}

	private static <T> int compareAgainstDatabase(Collection<T> unknownList, Function<T, IScanMSD> extractor, List<? extends IScanMSD> references, IFileIdentifierSettings fileIdentifierSettings, String identifier, String databaseName, IProgressMonitor monitor) {

		int matched = 0;
		long start = System.currentTimeMillis();
		//
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Comparing against database " + databaseName + " with " + references.size() + " massspectra", unknownList.size());
		IMassSpectrumComparator massSpectrumComparator = fileIdentifierSettings.getMassSpectrumComparator();
		IMassSpectrumComparisonSupplier comparisonSupplier = massSpectrumComparator.getMassSpectrumComparisonSupplier();
		String comparatorName = comparisonSupplier != null ? comparisonSupplier.getComparatorName() : "";
		//
		int count = 1;
		int total = unknownList.size();
		//
		for(T item : unknownList) {
			/*
			 * Abort condition.
			 */
			if(subMonitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			/*
			 * Do the match.
			 */
			subMonitor.subTask("Reference " + count + "/" + total + " (matches found: " + matched + ")");
			IScanMSD unknown = extractor.apply(item);
			Map<IComparisonResult, IScanMSD> matches = new FindMatchingSpectras(unknown, references, fileIdentifierSettings, massSpectrumComparator).invoke();
			if(matches.size() > 0) {
				matched++;
				List<IComparisonResult> resultList = new ArrayList<>(matches.keySet());
				Collections.sort(resultList, RESULT_COMPARATOR);
				int size = Math.min(fileIdentifierSettings.getNumberOfTargets(), matches.size());
				for(int i = 0; i < size; i++) {
					IComparisonResult comparisonResult = resultList.get(i);
					IIdentificationTarget massSpectrumTarget = TARGETBUILDER.getMassSpectrumTarget(matches.get(comparisonResult), comparisonResult, identifier, databaseName);
					massSpectrumTarget.getLibraryInformation().setMiscellaneous(comparatorName); // e.g. Cosine, Cosine Binary (0|1), ...
					unknown.getTargets().add(massSpectrumTarget);
				}
			}
			//
			count++;
			subMonitor.worked(1);
		}
		//
		if(UserManagement.isDevMode()) {
			long end = System.currentTimeMillis();
			NumberFormat integerFormat = ValueFormat.getDecimalFormatEnglish("0");
			NumberFormat timeFormat = ValueFormat.getDecimalFormatEnglish("0.000");
			logger.info("Identification of " + integerFormat.format(unknownList.size()) + " unknown items against database " + databaseName + " with " + integerFormat.format(references.size()) + " mass spectra took " + timeFormat.format((end - start) / 1000.0d) + " seconds and yields " + matched + " matches");
		}
		//
		return matched;
	}

	private static final class FindMatchingSpectras extends RecursiveTask<Map<IComparisonResult, IScanMSD>> {

		private static final long serialVersionUID = 1L;
		/**
		 * Define the number of items where no more splitting occurs
		 */
		private static final int THRESHOLD = 400;
		//
		private final IScanMSD unknown;
		private final List<? extends IScanMSD> references;
		private final IFileIdentifierSettings settings;
		private final IMassSpectrumComparator spectrumComparator;
		//
		private MatchConstraints matchConstraints;

		public FindMatchingSpectras(IScanMSD unknown, List<? extends IScanMSD> references, IFileIdentifierSettings settings, IMassSpectrumComparator spectrumComparator) {

			this.unknown = unknown;
			this.references = references;
			this.settings = settings;
			this.spectrumComparator = spectrumComparator;
			/*
			 * Adjusted to max 1.0 = 100%
			 */
			float minMatchFactor = settings.getMinMatchFactor() / 100.0f;
			float minReverseMatchFactor = settings.getMinReverseMatchFactor() / 100.0f;
			this.matchConstraints = new MatchConstraints(minMatchFactor, minReverseMatchFactor);
		}

		@Override
		protected Map<IComparisonResult, IScanMSD> compute() {

			int size = references.size();
			if(size > THRESHOLD) {
				int half = size / 2;
				FindMatchingSpectras forkPart = new FindMatchingSpectras(unknown, references.subList(0, half), settings, spectrumComparator);
				forkPart.fork();
				FindMatchingSpectras directPart = new FindMatchingSpectras(unknown, references.subList(half, size), settings, spectrumComparator);
				Map<IComparisonResult, IScanMSD> map = directPart.compute();
				map.putAll(forkPart.join());
				return map;
			} else {
				return findMatchingReferences();
			}
		}

		private Map<IComparisonResult, IScanMSD> findMatchingReferences() {

			Map<IComparisonResult, IScanMSD> results = new IdentityHashMap<>();
			float minMatchFactor = settings.getMinMatchFactor();
			float minReverseMatchFactor = settings.getMinReverseMatchFactor();
			//
			for(IScanMSD reference : references) {
				IProcessingInfo<IComparisonResult> infoCompare = spectrumComparator.compare(unknown, reference, matchConstraints);
				IComparisonResult comparisonResult = infoCompare.getProcessingResult();
				boolean useTarget = DeltaCalculationSupport.useTarget(unknown, reference, settings);
				if(useTarget) {
					PenaltyCalculationSupport.applyPenalty(unknown, reference, comparisonResult, settings);
					if(isValidTarget(comparisonResult, minMatchFactor, minReverseMatchFactor)) {
						results.put(comparisonResult, reference);
					}
				}
			}
			return results;
		}

		private static boolean isValidTarget(IComparisonResult comparisonResult, float minMatchFactor, float minReverseMatchFactor) {

			if(comparisonResult.getMatchFactor() >= minMatchFactor && comparisonResult.getReverseMatchFactor() >= minReverseMatchFactor) {
				return true;
			}
			return false;
		}
	}
}
