/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraWriter;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.model.IVendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumTarget;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.MassSpectrumTarget;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractAmdisWriter implements IMassSpectraWriter {

	public float NORMALIZATION_BASE = 1000.0f;
	public String CRLF = "\r\n";
	//
	private static final Logger logger = Logger.getLogger(AbstractAmdisWriter.class);
	//
	private static final float FACTOR = 100.0f;
	//
	private NumberFormat numberFormat;
	private static final int FRACTION_DIGITS = 3;
	private static final int MAX_SPECTRA_CHUNK = 65535;
	//
	private TargetExtendedComparator targetExtendedComparator;

	public AbstractAmdisWriter() {
		numberFormat = NumberFormat.getInstance(Locale.US);
		numberFormat.setMinimumFractionDigits(FRACTION_DIGITS);
		numberFormat.setMaximumFractionDigits(FRACTION_DIGITS);
		targetExtendedComparator = new TargetExtendedComparator(SortOrder.DESC);
	}

	@Override
	public void write(File file, IScanMSD massSpectrum, boolean append, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		FileWriter fileWriter = new FileWriter(file, append);
		writeMassSpectrum(fileWriter, massSpectrum, monitor);
		fileWriter.close();
	}

	@Override
	public void write(File file, IMassSpectra massSpectra, boolean append, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		if(massSpectra.size() > 65535 && PreferenceSupplier.isSplitLibrary()) {
			/*
			 * Split the export file to several files.
			 */
			file.delete();
			List<IMassSpectra> splittedMassSpectra = getSplittedMassSpectra(massSpectra);
			int counter = 1;
			for(IMassSpectra massSpectraChunk : splittedMassSpectra) {
				String filePath = file.getAbsolutePath();
				String fileExtension = filePath.substring(filePath.lastIndexOf("."), filePath.length());
				filePath = filePath.replace(fileExtension, "-" + counter + fileExtension);
				FileWriter fileWriter = new FileWriter(new File(filePath), append);
				writeMassSpectra(fileWriter, massSpectraChunk, monitor);
				fileWriter.close();
				counter++;
			}
		} else {
			/*
			 * <= 65535 mass spectra
			 */
			FileWriter fileWriter = new FileWriter(file, append);
			writeMassSpectra(fileWriter, massSpectra, monitor);
			fileWriter.close();
		}
	}

	private List<IMassSpectra> getSplittedMassSpectra(IMassSpectra massSpectra) {

		IMassSpectra massSpectraChunk;
		List<IMassSpectra> splittedMassSpectra = new ArrayList<IMassSpectra>();
		//
		massSpectraChunk = new MassSpectra();
		int counter = 1;
		/*
		 * Split
		 */
		for(int i = 1; i <= massSpectra.size(); i++) {
			IScanMSD massSpectrum = massSpectra.getMassSpectrum(i);
			if(counter <= MAX_SPECTRA_CHUNK) {
				massSpectraChunk.addMassSpectrum(massSpectrum);
				counter++;
			} else {
				splittedMassSpectra.add(massSpectraChunk);
				massSpectraChunk = new MassSpectra();
				massSpectraChunk.addMassSpectrum(massSpectrum);
				counter = 1;
			}
		}
		splittedMassSpectra.add(massSpectraChunk);
		//
		return splittedMassSpectra;
	}

	/**
	 * Writes the mass spectra with the given file writer.
	 * 
	 * @throws IOException
	 */
	private void writeMassSpectra(FileWriter fileWriter, IMassSpectra massSpectra, IProgressMonitor monitor) throws IOException {

		/*
		 * Get all mass spectra, test to null and append them with the given
		 * file writer.
		 */
		for(int i = 1; i <= massSpectra.size(); i++) {
			IScanMSD massSpectrum = massSpectra.getMassSpectrum(i);
			/*
			 * There must be at least one ion.
			 */
			if(massSpectrum != null && massSpectrum.getNumberOfIons() > 0) {
				writeMassSpectrum(fileWriter, massSpectrum, monitor);
			}
		}
	}

	/**
	 * Makes a deep copy of the mass spectrum, normalizes it and removes too low abundances.
	 * 
	 * @param massSpectrum
	 * @return {@link IScanMSD}
	 * @throws CloneNotSupportedException
	 */
	protected IScanMSD makeDeepCopyAndNormalize(IScanMSD massSpectrum) throws CloneNotSupportedException {

		IScanMSD normalizedMassSpectrum = massSpectrum.makeDeepCopy();
		normalizedMassSpectrum.normalize(NORMALIZATION_BASE);
		/*
		 * Remove m/z with an abundance less than 1.
		 */
		removeIonsWithAnTooLowAbundance(normalizedMassSpectrum, 1.0f);
		//
		return normalizedMassSpectrum;
	}

	/**
	 * Removes the ions below the given minimum abundance.
	 * 
	 * @param normalizedMassSpectrum
	 * @param minimumAbundance
	 */
	protected void removeIonsWithAnTooLowAbundance(IScanMSD normalizedMassSpectrum, float minimumAbundance) {

		List<IIon> ionsToRemove = new ArrayList<IIon>();
		for(IIon ion : normalizedMassSpectrum.getIons()) {
			if(ion.getAbundance() < minimumAbundance) {
				ionsToRemove.add(ion);
			}
		}
		// Remove the selected ions.
		for(IIon ion : ionsToRemove) {
			normalizedMassSpectrum.removeIon(ion);
		}
	}

	/**
	 * Returns the name information from the mass spectrum.
	 * 
	 * @param massSpectrum
	 * @return String
	 */
	protected String getNameField(IScanMSD massSpectrum, IIdentificationTarget identificationTarget) {

		/*
		 * The identifier is very important when tagging files
		 * for identification processes. Hence, if an identifier
		 * is available, set it.
		 */
		String field = "NAME: ";
		String identifier = massSpectrum.getIdentifier();
		//
		if(identifier != null && !identifier.equals("")) {
			field += identifier;
		} else if(identificationTarget != null) {
			field += identificationTarget.getLibraryInformation().getName();
		} else {
			field += "NO IDENTIFIER AVAILABLE";
		}
		return field;
	}

	/**
	 * This method returns the identification target or null if there is none.
	 * 
	 * @param massSpectrum
	 * @return
	 */
	protected IIdentificationTarget getIdentificationTarget(IScanMSD massSpectrum) {

		IIdentificationTarget identificationTarget = null;
		if(massSpectrum instanceof IRegularLibraryMassSpectrum) {
			/*
			 * Library MS
			 */
			IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)massSpectrum;
			try {
				identificationTarget = new MassSpectrumTarget(libraryMassSpectrum.getLibraryInformation(), new ComparisonResult(FACTOR, FACTOR, FACTOR, FACTOR));
			} catch(ReferenceMustNotBeNullException e) {
				logger.warn(e);
			}
		} else if(massSpectrum instanceof IRegularMassSpectrum) {
			/*
			 * Scan/Chromatogram MS
			 */
			List<IMassSpectrumTarget> targets = massSpectrum.getTargets();
			Collections.sort(targets, targetExtendedComparator);
			if(targets.size() >= 1) {
				identificationTarget = targets.get(0);
			}
		}
		return identificationTarget;
	}

	/**
	 * Returns the CAS number information from the mass spectrum.
	 * 
	 * @param massSpectrum
	 * @return String
	 */
	protected String getCasNumberField(IIdentificationTarget identificationTarget) {

		String field = "CASNO: ";
		if(identificationTarget != null) {
			field += identificationTarget.getLibraryInformation().getCasNumber();
		}
		return field;
	}

	/**
	 * Returns the CAS number information from the mass spectrum.
	 * 
	 * @param massSpectrum
	 * @return String
	 */
	protected String getSmilesField(IIdentificationTarget identificationTarget) {

		String field = "SMILES: ";
		if(identificationTarget != null) {
			field += identificationTarget.getLibraryInformation().getSmiles();
		}
		return field;
	}

	/**
	 * Returns the comments information from the mass spectrum.
	 * 
	 * @param massSpectrum
	 * @return String
	 */
	protected String getCommentsField(IScanMSD massSpectrum) {

		String field = "COMMENTS: ";
		if(massSpectrum instanceof IRegularLibraryMassSpectrum) {
			IRegularLibraryMassSpectrum regularMassSpectrum = (IRegularLibraryMassSpectrum)massSpectrum;
			field += regularMassSpectrum.getLibraryInformation().getComments();
		}
		return field;
	}

	/**
	 * Returns the source information from the mass spectrum.
	 * 
	 * @param massSpectrum
	 * @return String
	 */
	protected String getSourceField(IScanMSD massSpectrum, IIdentificationTarget identificationTarget) {

		String field = "SOURCE: ";
		if(massSpectrum instanceof IVendorLibraryMassSpectrum) {
			IVendorLibraryMassSpectrum amdisMassSpectrum = (IVendorLibraryMassSpectrum)massSpectrum;
			field += amdisMassSpectrum.getSource();
		} else {
			if(identificationTarget != null) {
				field += identificationTarget.getIdentifier();
			}
		}
		return field;
	}

	/**
	 * Returns the retention time information from the mass spectrum.
	 * 
	 * @param massSpectrum
	 * @return String
	 */
	protected String getRetentionTimeField(IScanMSD massSpectrum) {

		String field = "RT: ";
		if(massSpectrum instanceof IRegularMassSpectrum) {
			IRegularMassSpectrum regularMassSpectrum = (IRegularMassSpectrum)massSpectrum;
			field += numberFormat.format(regularMassSpectrum.getRetentionTime() / (1000.0d * 60.0d)); // RT in minutes
		} else {
			field += numberFormat.format(0.0d);
		}
		return field;
	}

	/**
	 * Returns the retention index information from the mass spectrum.
	 * 
	 * @param massSpectrum
	 * @return String
	 */
	protected String getRetentionIndexField(IScanMSD massSpectrum) {

		String field = "RI: ";
		if(massSpectrum instanceof IRegularMassSpectrum) {
			IRegularMassSpectrum regularMassSpectrum = (IRegularMassSpectrum)massSpectrum;
			field += numberFormat.format(regularMassSpectrum.getRetentionIndex());
		} else {
			field += numberFormat.format(0.0d);
		}
		return field;
	}

	/**
	 * Returns the name information from the mass spectrum.
	 * 
	 * @param massSpectrum
	 * @return String
	 */
	protected String getNumberOfPeaks(IScanMSD massSpectrum) {

		String field = "NUM PEAKS: ";
		field += massSpectrum.getNumberOfIons();
		return field;
	}

	/**
	 * Returns the formula information from the mass spectrum.
	 * 
	 * @param massSpectrum
	 * @return String
	 */
	protected String getFormulaField(IScanMSD massSpectrum) {

		String field = "FORMULA: ";
		if(massSpectrum instanceof IRegularLibraryMassSpectrum) {
			IRegularLibraryMassSpectrum regularMassSpectrum = (IRegularLibraryMassSpectrum)massSpectrum;
			field += regularMassSpectrum.getLibraryInformation().getFormula();
		}
		return field;
	}

	/**
	 * Returns the MW information from the mass spectrum.
	 * 
	 * @param massSpectrum
	 * @return String
	 */
	protected String getMWField(IScanMSD massSpectrum) {

		String field = "MW: ";
		if(massSpectrum instanceof IRegularLibraryMassSpectrum) {
			IRegularLibraryMassSpectrum regularMassSpectrum = (IRegularLibraryMassSpectrum)massSpectrum;
			field += regularMassSpectrum.getLibraryInformation().getMolWeight();
		}
		return field;
	}
}
