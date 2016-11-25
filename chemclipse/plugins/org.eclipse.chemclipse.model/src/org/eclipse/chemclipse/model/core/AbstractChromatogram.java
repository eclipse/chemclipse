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
package org.eclipse.chemclipse.model.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.baseline.BaselineModel;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.implementation.TripleQuadMethod;
import org.eclipse.chemclipse.model.notifier.IChromatogramSelectionUpdateNotifier;
import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.processor.IChromatogramProcessor;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.model.updates.IChromatogramUpdateListener;
import org.eclipse.chemclipse.model.versioning.IVersionManagement;
import org.eclipse.chemclipse.model.versioning.VersionManagement;
import org.eclipse.chemclipse.support.history.EditHistory;
import org.eclipse.chemclipse.support.history.EditInformation;
import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.chemclipse.support.preferences.SupportPreferences;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;

public abstract class AbstractChromatogram implements IChromatogram {

	private static final Logger logger = Logger.getLogger(AbstractChromatogram.class);
	//
	private String converterId = "";
	private File file = null; // The file object of the chromatogram.
	private int scanDelay = 4500;
	private int scanInterval = 1000; // 1000ms = 1 scan per second
	/*
	 * This flag marks whether the chromatogram has been
	 * set to unload modus. It is used e.g. when loading
	 * the scan proxies in the background. If unload is true,
	 * no further actions are neccessary.
	 */
	private boolean unloaded = false;
	/*
	 * This is a list, which holds other participants that listen on changes in
	 * the chromatogram.<br/> It is important e.g. for the GUI to known when it
	 * has to update itself.
	 */
	private List<IChromatogramUpdateListener> updateSupport;
	/*
	 * The version management handles the temporary files and revision names.
	 */
	private IVersionManagement versionManagement;
	/*
	 * EditHistory stores all information about the edit operations processed on
	 * the chromatogram.
	 */
	private IEditHistory editHistory;
	/*
	 * The baseline model.
	 */
	private IBaselineModel baselineModel = null;
	/*
	 * Store all scans in this list.<br/>
	 */
	private List<IScan> scans;
	/*
	 * Chromatogram results map
	 */
	private Map<String, IChromatogramResult> chromatogramResults;
	/*
	 * Additional Sample Info
	 */
	private String operator = "";
	private Date date = new Date();
	private String miscInfo = "";
	private String miscInfoSeparated = "";
	private Map<String, String> miscellaneous;
	private String shortInfo = "";
	private String detailedInfo = "";
	private String sampleGroup = "";
	private String barcode = "";
	private String barcodeType = "";
	private double sampleWeight = 0;
	private String weightUnit = "";
	private String dataName = "";
	/*
	 * Some vendors store several chromatograms in one file.
	 */
	private List<IChromatogram> referencedChromatograms;
	/*
	 * Integration entries.
	 */
	private String chromatogramIntegratorDescription = "";
	private List<IIntegrationEntry> chromatogramIntegrationEntries;
	private String backgroundIntegratorDescription = "";
	private List<IIntegrationEntry> backgroundIntegrationEntries;
	//
	private IMethod method;

	/**
	 * Constructs a normal chromatogram.
	 * Several initialization will be performed.
	 */
	public AbstractChromatogram() {
		updateSupport = new ArrayList<IChromatogramUpdateListener>(5);
		versionManagement = new VersionManagement();
		editHistory = new EditHistory();
		baselineModel = new BaselineModel(this);
		scans = new ArrayList<IScan>();
		chromatogramResults = new HashMap<String, IChromatogramResult>();
		referencedChromatograms = new ArrayList<IChromatogram>();
		chromatogramIntegrationEntries = new ArrayList<IIntegrationEntry>();
		backgroundIntegrationEntries = new ArrayList<IIntegrationEntry>();
		method = new TripleQuadMethod();
		miscellaneous = new HashMap<String, String>();
	}

	@Override
	public void recalculateTheNoiseFactor() {

	}

	@Override
	public float getSignalToNoiseRatio(float abundance) {

		return 0;
	}

	@Override
	public String getConverterId() {

		return converterId;
	}

	@Override
	public void setConverterId(String converterId) {

		this.converterId = converterId;
	}

	@Override
	public int getScanDelay() {

		return scanDelay;
	}

	@Override
	public void setScanDelay(int milliseconds) {

		if(scanDelay >= MIN_SCANDELAY && scanDelay <= MAX_SCANDELAY) {
			this.scanDelay = milliseconds;
		}
	}

	@Override
	public int getScanInterval() {

		return scanInterval;
	}

	@Override
	public void setScanInterval(int milliseconds) {

		if(milliseconds >= MIN_SCANINTERVAL && milliseconds <= MAX_SCANINTERVAL) {
			this.scanInterval = milliseconds;
		}
	}

	@Override
	public void setScanInterval(float scansPerSecond) {

		/*
		 * Calculates a new scanInterval for the given value scans per second.
		 */
		if(scansPerSecond >= MIN_SCANS_PER_SECOND && scansPerSecond <= MAX_SCANS_PER_SECOND) {
			int milliseconds = Math.round(1000 / scansPerSecond);
			setScanInterval(milliseconds);
		}
	}

	@Override
	public String getIdentifier() {

		return versionManagement.getChromatogramIdentifier();
	}

	@Override
	public File getStorageDirectory() {

		return versionManagement.getStorageDirectory();
	}

	@Override
	public File getFile() {

		return this.file;
	}

	@Override
	public void setFile(File file) {

		this.file = file;
	}

	@Override
	public String getName() {

		if(file != null) {
			return file.getName();
		} else {
			return DEFAULT_CHROMATOGRAM_NAME;
		}
	}

	@Override
	public String getOperator() {

		return operator;
	}

	@Override
	public void setOperator(String operator) {

		this.operator = operator;
	}

	@Override
	public Date getDate() {

		return new Date(date.getTime());
	}

	@Override
	public void setDate(final Date date) {

		this.date = new Date(date.getTime());
	}

	@Override
	public String getMiscInfo() {

		return miscInfo;
	}

	@Override
	public void setMiscInfo(String miscInfo) {

		if(miscInfo != null) {
			String[] values = miscInfo.split(PreferenceSupplier.getMiscSeparator());
			if(values.length >= 2) {
				this.miscInfo = values[0];
				StringBuilder builder = new StringBuilder();
				for(int i = 1; i < values.length; i++) {
					builder.append(values[i].trim());
					builder.append(PreferenceSupplier.getMiscSeparatedDelimiter());
				}
				this.miscInfoSeparated = builder.toString().trim();
			} else {
				this.miscInfo = miscInfo;
			}
		} else {
			this.miscInfo = "";
			this.miscInfoSeparated = "";
		}
	}

	@Override
	public String getMiscInfoSeparated() {

		return miscInfoSeparated;
	}

	@Override
	public void setMiscInfoSeparated(String miscInfoSeparated) {

		this.miscInfoSeparated = miscInfoSeparated;
	}

	@Override
	public Map<String, String> getMiscellaneous() {

		return miscellaneous;
	}

	// TODO optimieren - verbraucht zu viel Prozessorzeit
	@Override
	public float getMinSignal() {

		float minSignal = Float.MAX_VALUE;
		float actSignal = 0.0f;
		if(getNumberOfScans() < 1) {
			return 0;
		}
		/*
		 * Do the for loop if at least one scan exists.
		 */
		for(IScan scan : scans) {
			actSignal = scan.getTotalSignal();
			minSignal = (minSignal > actSignal) ? actSignal : minSignal;
		}
		return minSignal;
	}

	@Override
	public float getMaxSignal() {

		return getMaxSignal(containsScanCycles());
	}

	// TODO optimieren - verbraucht zu viel Prozessorzeit
	@Override
	public float getMaxSignal(boolean condenseCycleNumberScans) {

		float maxSignal = 0;
		if(getNumberOfScans() >= 1) {
			/*
			 * Do the for loop if at least one scan exists.
			 */
			try {
				ITotalScanSignalExtractor totalIonSignalExtractor;
				totalIonSignalExtractor = new TotalScanSignalExtractor(this);
				ITotalScanSignals signals = totalIonSignalExtractor.getTotalScanSignals(this, true, condenseCycleNumberScans);
				maxSignal = signals.getMaxSignal();
			} catch(ChromatogramIsNullException e) {
				logger.warn(e);
			}
		}
		return maxSignal;
	}

	@Override
	public int getStartRetentionTime() {

		int lastScan = getNumberOfScans();
		if(lastScan == 0) {
			return 0;
		}
		return getScan(1).getRetentionTime();
	}

	@Override
	public int getStopRetentionTime() {

		int lastScan = getNumberOfScans();
		if(lastScan == 0) {
			return 0;
		}
		return getScan(lastScan).getRetentionTime();
	}

	@Override
	public int getNumberOfScans() {

		return scans.size();
	}

	@Override
	public int getScanNumber(float retentionTime) {

		int milliseconds = (int)(retentionTime * 1000 * 60);
		return getScanNumber(milliseconds);
	}

	@Override
	public int getScanNumber(int retentionTime) {

		/*
		 * Check if the retention time is out of limits.
		 */
		if(retentionTime < getStartRetentionTime() || retentionTime > getStopRetentionTime()) {
			return 0;
		}
		/*
		 * If the scan interval is null, there is no scan stored.
		 */
		int scanInterval = getScanInterval();
		if(scanInterval == 0) {
			return 0;
		}
		/*
		 * If the given retention time fits the last scan, return the scan
		 * number of the last scan.
		 */
		if(retentionTime == getStopRetentionTime()) {
			return getNumberOfScans();
		}
		/*
		 * Calculate the scan number starting point to not iterate through all
		 * scans.
		 */
		// TODO optimize!! It doesn't work if the scan intervals are not equal of length!!
		/*
		 * int startScan = (retentionTime - getScanDelay()) / scanInterval - 1;
		 * if(startScan < 1) {
		 * startScan = 1;
		 * }
		 */
		// TODO optimieren? Collections.binarySearch?
		for(int scan = 1; scan <= getNumberOfScans(); scan++) {
			if(getScan(scan).getRetentionTime() > retentionTime) {
				return --scan;
			}
		}
		/*
		 * If there was no fit, return 0.
		 */
		return 0;
	}

	@Override
	public float getTotalSignal() {

		float totalSignal = 0.0f;
		/*
		 * If there is no scan stored, return 0;
		 */
		if(getNumberOfScans() < 1) {
			return 0;
		}
		/*
		 * Do the for loop if at least one scan exists.
		 */
		for(IScan scan : scans) {
			totalSignal += scan.getTotalSignal();
		}
		return totalSignal;
	}

	@Override
	public void recalculateScanNumbers() {

		int scanNumber = 1;
		for(IScan scan : scans) {
			scan.setScanNumber(scanNumber);
			scanNumber++;
		}
	}

	@Override
	public void recalculateRetentionTimes() {

		int actual = getScanDelay();
		for(IScan scan : scans) {
			scan.setRetentionTime(actual);
			actual += getScanInterval();
		}
		/*
		 * Forces all listeners to be updated.
		 */
		fireUpdateChange(true);
	}

	@Override
	public void addScan(IScan scan) {

		scan.setParentChromatogram(this);
		int lastScan = scans.size();
		scan.setScanNumber(++lastScan);
		scans.add(scan);
	}

	@Override
	public IScan getScan(int scan) {

		int position = scan;
		if(position > 0 && position <= scans.size()) {
			return scans.get(--position);
		}
		return null;
	}

	@Override
	public List<IScan> getScans() {

		return scans;
	}

	@Override
	public void removeScan(final int scan) {

		int position = scan;
		if(position > 0 && position <= scans.size()) {
			scans.remove(--position);
		}
	}

	@Override
	public void removeScans(int from, int to) {

		int start;
		int end;
		if(from > to) {
			start = to;
			end = from;
		} else {
			start = from;
			end = to;
		}
		for(int i = start; i <= end; i++) {
			removeScan(start);
		}
		/*
		 * Forces all listeners to be updated.
		 */
		// TODO Test updateChange
		// fireUpdateChange(true);
	}

	@Override
	public boolean isUndoable() {

		return SupportPreferences.isUndoable();
	}

	// TODO Junit
	@Override
	public boolean canUndo() {

		boolean result = false;
		if(isUndoable() && versionManagement.getRevision() > 0) {
			result = true;
		}
		return result;
	}

	// TODO Junit
	@Override
	public boolean canRedo() {

		boolean result = false;
		if(isUndoable() && versionManagement.getNextScanRevision().exists()) {
			result = true;
		}
		return result;
	}

	@Override
	public void hibernate() {

		if(!isUndoable()) {
			return;
		}
		File file = versionManagement.getActualScanRevision();
		writeSerializedChromatogram(file);
		// TODO set scans = null;
	}

	@Override
	public void wakeUp() {

		if(!isUndoable()) {
			return;
		}
		File file = versionManagement.getActualScanRevision();
		if(file.exists()) {
			readSerializedChromatogram(file);
		}
	}

	@Override
	public void writeSerializedChromatogram(File file) {

		assert file != null : getClass().getName() + " writeSerializedFile: The file must not be null.";
		ObjectOutputStream outputStream = null;
		try {
			outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			outputStream.writeObject(scans);
		} catch(FileNotFoundException e) {
			logger.warn(e);
		} catch(IOException e) {
			logger.warn(e);
		} finally {
			if(outputStream != null) {
				try {
					outputStream.close();
				} catch(IOException e) {
					logger.warn(e);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readSerializedChromatogram(File file) {

		assert file != null : getClass().getName() + " readSerializedFile: The file must not be null.";
		ObjectInputStream inputStream = null;
		Object inputObject;
		try {
			inputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			inputObject = inputStream.readObject();
			if(inputObject instanceof ArrayList) {
				scans = null;
				scans = (ArrayList<IScan>)inputObject;
			}
		} catch(FileNotFoundException e) {
			logger.warn(e);
		} catch(IOException e) {
			logger.warn(e);
		} catch(ClassNotFoundException e) {
			logger.warn(e);
		} finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch(IOException e) {
					logger.warn(e);
				}
			}
		}
	}

	// -----------------------------IAdaptable
	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Object getAdapter(Class adapter) {

		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	// -----------------------------------------------IChromatogramVersioning
	@Override
	public IVersionManagement getVersionManagement() {

		return versionManagement;
	}

	// -----------------------------------------------IChromatogramEditHistory
	@Override
	public IEditHistory getEditHistory() {

		return editHistory;
	}

	// -----------------------------------------------IChromatogramBaseline
	@Override
	public IBaselineModel getBaselineModel() {

		return baselineModel;
	}

	// -----------------------------------------------IUpdater
	@Override
	public void addChromatogramUpdateListener(IChromatogramUpdateListener listener) {

		updateSupport.add(listener);
	}

	@Override
	public void removeChromatogramUpdateListener(IChromatogramUpdateListener listener) {

		updateSupport.remove(listener);
	}

	/**
	 * When a chromatogram value has changed, call fireUpdateChange() to inform
	 * all registered listeners ({@link IChromatogramUpdateListener}). See {@link IChromatogramSelectionUpdateNotifier} for the explanation of forceReload.
	 */
	protected void fireUpdateChange(final boolean forceReload) {

		/**
		 * Before the listeners should be informed, internally some values
		 * should be marked to be recalculated if they are needed.
		 */
		recalculateTheNoiseFactor();
		/**
		 * Inform all listeners if a chromatogram value has changed, for example
		 * a mass spectrum.<br/>
		 * The update action is encapsulated in a ISafeRunnable object to handle
		 * failures of the implementing listeners.
		 */
		for(final IChromatogramUpdateListener listener : updateSupport) {
			ISafeRunnable runnable = new ISafeRunnable() {

				@Override
				public void handleException(Throwable exception) {

					logger.warn(exception);
				}

				@Override
				public void run() throws Exception {

					listener.update(forceReload);
				}
			};
			SafeRunner.run(runnable);
		}
	}

	// -----------------------------------------------Name methods
	@Override
	public String extractNameFromDirectory(String nameDefault, String directoryExtension) {

		File file = getFile();
		if(file == null) {
			return nameDefault;
		}
		//
		StringTokenizer tokenizer = new StringTokenizer(getFile().getAbsolutePath(), File.separator);
		int element = tokenizer.countTokens() - 1;
		/*
		 * Get the chromatogram directory.
		 */
		for(int i = 1; i < element; i++) {
			if(tokenizer.hasMoreElements()) {
				tokenizer.nextToken();
			}
		}
		if(tokenizer.hasMoreElements()) {
			nameDefault = tokenizer.nextToken();
		}
		/*
		 * Shorten the directory. The extension is not needed, e.g. ".D" is not needed.
		 */
		if(nameDefault != null) {
			nameDefault = nameDefault.substring(0, nameDefault.length() - directoryExtension.length());
		}
		return nameDefault;
	}

	@Override
	public String extractNameFromFile(String nameDefault) {

		File file = getFile();
		if(file != null) {
			String fileName = file.getName();
			if(fileName != "" && fileName != null) {
				/*
				 * Extract the file name.
				 */
				String[] parts = fileName.split("\\.");
				if(parts.length > 2) {
					StringBuilder builder = new StringBuilder();
					for(int i = 0; i < parts.length - 1; i++) {
						builder.append(parts[i]);
						builder.append(".");
					}
					String name = builder.toString();
					nameDefault = name.substring(0, name.length() - 1);
				} else {
					/*
					 * If there are not 2 parts, it's assumed that the file had no extension.
					 */
					if(parts.length == 2) {
						nameDefault = parts[0];
					}
				}
			}
		}
		return nameDefault;
	}

	// -----------------------------------------------IChromatogramResultSupport
	@Override
	public void addChromatogramResult(IChromatogramResult chromatogramResult) {

		if(chromatogramResult != null) {
			chromatogramResults.put(chromatogramResult.getIdentifier(), chromatogramResult);
		}
	}

	@Override
	public IChromatogramResult getChromatogramResult(String identifier) {

		return chromatogramResults.get(identifier);
	}

	@Override
	public void deleteChromatogramResult(String identifier) {

		chromatogramResults.remove(identifier);
	}

	@Override
	public void removeAllChromatogramResults() {

		chromatogramResults.clear();
	}

	@Override
	public Collection<IChromatogramResult> getChromatogramResults() {

		return chromatogramResults.values();
	}

	@Override
	public String getShortInfo() {

		return shortInfo;
	}

	@Override
	public void setShortInfo(String shortInfo) {

		if(shortInfo != null) {
			this.shortInfo = shortInfo;
		}
	}

	@Override
	public String getDetailedInfo() {

		return detailedInfo;
	}

	@Override
	public void setDetailedInfo(String detailedInfo) {

		if(detailedInfo != null) {
			this.detailedInfo = detailedInfo;
		}
	}

	@Override
	public String getSampleGroup() {

		return sampleGroup;
	}

	@Override
	public void setSampleGroup(String sampleGroup) {

		if(sampleGroup != null) {
			this.sampleGroup = sampleGroup;
		}
	}

	@Override
	public String getBarcode() {

		return barcode;
	}

	@Override
	public void setBarcode(String barcode) {

		if(barcode != null) {
			this.barcode = barcode;
		}
	}

	@Override
	public String getBarcodeType() {

		return barcodeType;
	}

	@Override
	public void setBarcodeType(String barcodeType) {

		if(barcodeType != null) {
			this.barcodeType = barcodeType;
		}
	}

	@Override
	public double getSampleWeight() {

		return sampleWeight;
	}

	public void setSampleWeight(double sampleWeight) {

		if(sampleWeight >= 0) {
			this.sampleWeight = sampleWeight;
		}
	}

	@Override
	public String getWeightUnit() {

		return weightUnit;
	}

	@Override
	public void setWeightUnit(String weightUnit) {

		if(weightUnit != null) {
			this.weightUnit = weightUnit;
		}
	}

	@Override
	public String getDataName() {

		return dataName;
	}

	@Override
	public void setDataName(String dataName) {

		if(dataName != null) {
			this.dataName = dataName;
		}
	}

	@Override
	public List<IChromatogram> getReferencedChromatograms() {

		return referencedChromatograms;
	}

	@Override
	public void addReferencedChromatogram(IChromatogram chromatogram) {

		referencedChromatograms.add(chromatogram);
	}

	@Override
	public void removeReferencedChromatogram(IChromatogram chromatogram) {

		referencedChromatograms.remove(chromatogram);
	}

	@Override
	public String getChromatogramIntegratorDescription() {

		return chromatogramIntegratorDescription;
	}

	@Override
	public void setChromatogramIntegratorDescription(String chromatogramIntegratorDescription) {

		if(chromatogramIntegratorDescription != null) {
			this.chromatogramIntegratorDescription = chromatogramIntegratorDescription;
		}
	}

	@Override
	public double getChromatogramIntegratedArea() {

		return getIntegratedArea(chromatogramIntegrationEntries);
	}

	@Override
	public void setChromatogramIntegratedArea(List<IIntegrationEntry> chromatogramIntegrationEntries, String chromatogramIntegratorDescription) {

		if(chromatogramIntegrationEntries != null) {
			setChromatogramIntegratorDescription(chromatogramIntegratorDescription);
			this.chromatogramIntegrationEntries = chromatogramIntegrationEntries;
		}
	}

	@Override
	public List<IIntegrationEntry> getChromatogramIntegrationEntries() {

		return chromatogramIntegrationEntries;
	}

	@Override
	public String getBackgroundIntegratorDescription() {

		return backgroundIntegratorDescription;
	}

	@Override
	public void setBackgroundIntegratorDescription(String backgroundIntegratorDescription) {

		if(backgroundIntegratorDescription != null) {
			this.backgroundIntegratorDescription = backgroundIntegratorDescription;
		}
	}

	@Override
	public double getBackgroundIntegratedArea() {

		return getIntegratedArea(backgroundIntegrationEntries);
	}

	@Override
	public void setBackgroundIntegratedArea(List<IIntegrationEntry> backgroundIntegrationEntries, String backgroundIntegratorDescription) {

		if(backgroundIntegrationEntries != null) {
			setBackgroundIntegratorDescription(backgroundIntegratorDescription);
			this.backgroundIntegrationEntries = backgroundIntegrationEntries;
		}
	}

	@Override
	public List<IIntegrationEntry> getBackgroundIntegrationEntries() {

		return backgroundIntegrationEntries;
	}

	private double getIntegratedArea(List<IIntegrationEntry> integrationEntries) {

		double integratedArea = 0.0d;
		if(integrationEntries.size() > 0) {
			for(IIntegrationEntry integrationEntry : integrationEntries) {
				integratedArea += integrationEntry.getIntegratedArea();
			}
		}
		return integratedArea;
	}

	@Override
	public void removeAllBackgroundIntegrationEntries() {

		backgroundIntegrationEntries.clear();
	}

	@Override
	public void removeAllChromatogramIntegrationEntries() {

		chromatogramIntegrationEntries.clear();
	}

	@Override
	public void doOperation(IChromatogramProcessor chromatogramProcessor, IProgressMonitor monitor) {

		boolean isUndoable = isUndoable();
		doOperation(chromatogramProcessor, isUndoable, true, monitor);
	}

	@Override
	public void doOperation(IChromatogramProcessor chromatogramProcessor, boolean isUndoable, boolean isDataModifying, IProgressMonitor monitor) {

		/*
		 * Return if the chromatogram modifier instance is null.<br/> Set the
		 * monitor to the state done.
		 */
		if(chromatogramProcessor == null) {
			return;
		}
		/*
		 * Test, that chromatogram selection is != null and the given
		 * chromatogram is == this chromatogram.
		 */
		final IChromatogramSelection chromatogramSelection = chromatogramProcessor.getChromatogramSelection();
		if(chromatogramSelection != null && chromatogramSelection.getChromatogram() == this) {
			File file;
			/*
			 * Save the first revision if neccessary.
			 */
			if(isUndoable) {
				if(getVersionManagement().isBaseRevision()) {
					monitor.subTask("Save the actual state.");
					file = getVersionManagement().getActualScanRevision();
					writeSerializedChromatogram(file);
				}
			}
			/*
			 * if(monitor.isCanceled()) { return; }
			 */
			/*
			 * Remove all detected peaks and targets and the baseline.
			 */
			if(isDataModifying) {
				removeAllPeaks();
				removeAllTargets();
				removeAllChromatogramResults();
				IBaselineModel baselineModel = getBaselineModel();
				if(baselineModel != null) {
					baselineModel.removeBaseline();
				}
			}
			/*
			 * Perform the operation.
			 */
			chromatogramProcessor.execute(monitor);
			/*
			 * Save the actual state.
			 */
			if(isUndoable) {
				monitor.subTask("Save the undoable state.");
				getVersionManagement().doOperation();
				file = getVersionManagement().getActualScanRevision();
				writeSerializedChromatogram(file);
			}
			monitor.subTask("Edit the history entries.");
			getEditHistory().add(new EditInformation(chromatogramProcessor.getDescription()));
			/*
			 * Set the S/N recalculate flag.
			 */
			chromatogramSelection.getChromatogram().recalculateTheNoiseFactor();
			//
			fireUpdate(chromatogramSelection);
		}
	}

	// TODO Junit
	@Override
	public void redoOperation(IChromatogramSelection chromatogramSelection) {

		if(chromatogramSelection != null && chromatogramSelection.getChromatogram() == this) {
			/*
			 * Redo
			 */
			if(isUndoable()) {
				File file = getVersionManagement().getNextScanRevision();
				if(file.exists()) {
					readSerializedChromatogram(file);
					getVersionManagement().redoOperation();
					getEditHistory().add(new EditInformation("redo operation performed"));
				}
				//
				fireUpdate(chromatogramSelection);
			}
		}
	}

	// TODO Junit
	@Override
	public void undoOperation(IChromatogramSelection chromatogramSelection) {

		if(chromatogramSelection != null && chromatogramSelection.getChromatogram() == this) {
			/*
			 * Undo
			 */
			if(isUndoable()) {
				File file = getVersionManagement().getPreviousScanRevision();
				if(file.exists()) {
					readSerializedChromatogram(file);
					getVersionManagement().undoOperation();
					getEditHistory().add(new EditInformation("undo operation performed"));
				}
				//
				fireUpdate(chromatogramSelection);
			}
		}
	}

	@Override
	public void removeAllPeaks() {

	}

	@Override
	public int getNumberOfPeaks() {

		return 0;
	}

	@Override
	public void removeAllTargets() {

	}

	@Override
	public void setUnloaded() {

		unloaded = true;
	}

	@Override
	public boolean isUnloaded() {

		return unloaded;
	}

	@Override
	public boolean containsScanCycles() {

		int defaultCycleNumber = 1;
		for(IScan scan : scans) {
			if(scan.getCycleNumber() != defaultCycleNumber) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<IScan> getScanCycleScans(int scanCycle) {

		List<IScan> scanCycleScans = new ArrayList<IScan>();
		if(scanCycle > 0) {
			if(containsScanCycles()) {
				/*
				 * Yes, there are cycle scan available.
				 */
				for(IScan scan : scans) {
					if(scan.getCycleNumber() == scanCycle) {
						scanCycleScans.add(scan);
					}
				}
			} else {
				/*
				 * If the cycle is 1, return the complete list.
				 * Otherwise do nothing.
				 */
				if(scanCycle == 1) {
					scanCycleScans.addAll(scans);
				}
			}
		}
		return scanCycleScans;
	}

	@Override
	public IMethod getMethod() {

		return method;
	}

	// -----------------------------------------------IChromatogramProcessorSupport
	// ----------------------------hashCode, equals and toString
	@Override
	public boolean equals(Object otherObject) {

		if(this == otherObject) {
			return true;
		}
		if(otherObject == null) {
			return false;
		}
		if(getClass() != otherObject.getClass()) {
			return false;
		}
		IChromatogram other = (IChromatogram)otherObject;
		return getNumberOfScans() == other.getNumberOfScans() && getTotalSignal() == other.getTotalSignal() && getMinSignal() == other.getMinSignal() && getMaxSignal() == other.getMaxSignal() && getStartRetentionTime() == other.getStartRetentionTime() && getStopRetentionTime() == other.getStopRetentionTime();
	}

	@Override
	public int hashCode() {

		return 7 * Integer.valueOf(getNumberOfScans()).hashCode() + 9 * Float.valueOf(getTotalSignal()).hashCode() + 11 * Float.valueOf(getMinSignal()).hashCode() + 9 * Float.valueOf(getMaxSignal()).hashCode() + 7 * Integer.valueOf(getStartRetentionTime()).hashCode() + 9 * Integer.valueOf(getStopRetentionTime()).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("numberOfScans=" + getNumberOfScans());
		builder.append(",");
		builder.append("totalSignal=" + getTotalSignal());
		builder.append(",");
		builder.append("minSignal=" + getMinSignal());
		builder.append(",");
		builder.append("maxSignal=" + getMaxSignal());
		builder.append(",");
		builder.append("startRetentionTime=" + getStartRetentionTime());
		builder.append(",");
		builder.append("stopRetentionTime=" + getStopRetentionTime());
		builder.append("]");
		return builder.toString();
	}
	// ----------------------------hashCode, equals and toString
}
