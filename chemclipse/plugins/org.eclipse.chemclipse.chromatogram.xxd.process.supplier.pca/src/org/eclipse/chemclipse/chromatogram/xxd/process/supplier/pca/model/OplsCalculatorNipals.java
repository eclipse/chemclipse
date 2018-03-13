/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

public class OplsCalculatorNipals extends AbstractMultivariateCalculator {

	@Override
	public void compute(int numComps) {
		// T_ortho<-NULL ### NULL vector
		// P_ortho<-NULL ### NULL vector
		// W_ortho<-NULL ### NULL vector
		//
		// X<-as.matrix(X)
		// avg_y<-opls_preproc(t(as.matrix(y)),1)$vectors[1,]
		// y<-t(t(y))
		//
		// if (preprocess_method==0){
		// pre_data_vector<-opls_preproc(X,0) ### If statements not finished!
		//
		// } else if(preprocess_method==1){
		// pre_data_vector<-opls_preproc(X,1)
		//
		// } else if (preprocess_method==2){
		// pre_data_vector<-opls_preproc(X,2)
		// }
		//
		// avg_X<-pre_data_vector$vectors[1,]
		// std_X<-pre_data_vector$vectors[2,]
		// X<-pre_data_vector$data
		// Eo_PLS<-X
		//
		// ##########################################################################
		// ### Start algorithm
		// #1
		// w<-(t(y)%*%X)/as.vector((t(y)%*%y)) ### Dot (scalar) product calculations
		// w<-t(w)
		// #2
		// w<-w/as.vector(sqrt(t(w)%*%w)) # Generates vector
		// ##########################################################################
		// ### Start calculation for ortho factors first and predictive in the end
		// #3
		// for (counterLVs in 1:nLVs){
		// te<-X%*%w/as.vector(t(w)%*%w) # Generates vector
		// #4
		// ce<-(t(te)%*%y)/as.vector(t(te)%*%te) # Generates scalar
		// ce<-t(ce)
		// #5
		// u<-(y%*%ce)/as.vector(t(ce)%*%ce) # Generates vector
		// #6
		// p<-t(te)%*%X/as.vector(t(te)%*%te)
		// p<-t(p)
		// ### End of calculation of predictive vector
		// ### The algorithms will stop here after having already calculated the orthogonol components
		//
		// if (counterLVs<nLVs){
		// #7
		// w_ortho<-p-(as.vector(t(w)%*%p)/as.vector(t(w)%*%w))*(w)
		// #8
		// w_ortho<-w_ortho/as.vector((sqrt(t(w_ortho)%*%w_ortho)))
		// #9
		// t_ortho<-(X%*%w_ortho)/as.vector(t(w_ortho)%*%w_ortho)
		// #10
		// p_ortho<-(t(t_ortho)%*%X)/as.vector(t(t_ortho)%*%t_ortho) # Generates a row vector
		// p_ortho<-t(p_ortho)
		// #11
		// Eo_PLS<-X-as.vector(t_ortho%*%t(p_ortho))
		// #12
		// T_ortho<-c(T_ortho,t_ortho)
		// P_ortho<-c(P_ortho,p_ortho)
		// W_ortho<-c(W_ortho,w_ortho)
		// X<-Eo_PLS
		// }
		// }
		//
		// if(nLVs>1){
		// T_ortho<-matrix(T_ortho,ncol=(nLVs-1)) ### reforming the vectors to matrices
		// }
		//
		// if(nLVs>1){P_ortho<-matrix(P_ortho,ncol=(nLVs-1))}
		// if(nLVs>1){W_ortho<-matrix(W_ortho,ncol=(nLVs-1))}
		//
		// #if(nLVs>1){T_ortho<-matrix(T_ortho,nrow=(nLVs-1),byrow=TRUE)}
		// #if(nLVs>1){P_ortho<-matrix(P_ortho,nrow=(nLVs-1),byrow=TRUE)}
		// #if(nLVs>1){W_ortho<-matrix(W_ortho,nrow=(nLVs-1),byrow=TRUE)}
		//
		// b<-w%*%ce
		//
		// #OPLS_result<-list(p_OPLS=p, u_OPLS=u,w_OPLS=w, t_OPLS=te, c_OPLS=ce, b_OPLS=b, T_ortho=T_ortho, P_ortho=P_ortho,W_ortho=W_ortho,Eo_PLS=X, pre_vectors=pre_data_vector$vectors, avg_y=avg_y)
		//
		// opls_result <- list()
		// opls_result$p_opls <- p
		// opls_result$u_opls <- u
		// opls_result$w_opls <- w
		// opls_result$t_opls <- te
		// opls_result$c_opls <- ce
		// opls_result$b_opls <- b
		// opls_result$t_ortho <- T_ortho
		// opls_result$p_ortho <- P_ortho
		// opls_result$w_ortho <- W_ortho
		// opls_result$Eo_PLS <- X
		// opls_result$pre_vectors <- pre_data_vector$vectors
		// opls_result$avg_y <- avg_y
		//
		// return(opls_result)

	}
}
