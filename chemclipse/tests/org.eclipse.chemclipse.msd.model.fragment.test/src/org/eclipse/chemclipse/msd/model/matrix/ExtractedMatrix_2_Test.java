package org.eclipse.chemclipse.msd.model.matrix;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.ScanIon;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;

import junit.framework.TestCase;

public class ExtractedMatrix_2_Test extends TestCase {
	
	private IVendorMassSpectrum supplierMassSpectrum;
	private IScanIon defaultIon;
	private IChromatogramMSD chromatogram;
	private float[] signalArray;
	
	@Override
	protected void setUp() throws Exception {

		int scans = 10;
		int ionStart = 25;
		int ionStop = 30;

		/*
		 * missing ion 28
		 */
		chromatogram = new ChromatogramMSD();
		for(int scan = 1; scan <= scans; scan++) {
			supplierMassSpectrum = new VendorMassSpectrum();
			supplierMassSpectrum.setRetentionTime(scan);
			supplierMassSpectrum.setRetentionIndex(scan / 60.0f);
			for(int ion = ionStart; ion <= ionStop; ion++) {
				defaultIon = new ScanIon(ion, ion * scan);
				if (ion != 28) {
					supplierMassSpectrum.addIon(defaultIon);
				}	
			}
			chromatogram.addScan(supplierMassSpectrum);
		}
		
		signalArray = new float[60];
		for(int i = 0; i < 10 * 6; i++ ) {
			signalArray[i] = 10.1f;
		}
		
		
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;

		super.tearDown();
	}
	
	public void testConstructor_1() {

		try {
			ChromatogramSelectionMSD selection = new ChromatogramSelectionMSD(chromatogram);
			ExtractedMatrix extracted = new ExtractedMatrix(selection);
			assertNotNull(extracted);	
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testUpdate_1() {
		try {
			ChromatogramSelectionMSD selection = new ChromatogramSelectionMSD(chromatogram);
			ExtractedMatrix extracted = new ExtractedMatrix(selection);
			extracted.updateSignal(signalArray, 10, 6);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	

}
