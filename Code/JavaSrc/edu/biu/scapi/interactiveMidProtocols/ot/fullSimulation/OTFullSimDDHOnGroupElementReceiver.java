/**
* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
* 
* Copyright (c) 2012 - SCAPI (http://crypto.biu.ac.il/scapi)
* This file is part of the SCAPI project.
* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
* to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
* and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
* FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
* 
* We request that any publication and/or code referring to and/or based on SCAPI contain an appropriate citation to SCAPI, including a reference to
* http://crypto.biu.ac.il/SCAPI.
* 
* SCAPI uses Crypto++, Miracl, NTL and Bouncy Castle. Please see these projects for any further licensing issues.
* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
* 
*/
package edu.biu.scapi.interactiveMidProtocols.ot.fullSimulation;

import java.io.IOException;
import java.security.SecureRandom;

import edu.biu.scapi.comm.Channel;
import edu.biu.scapi.exceptions.CheatAttemptException;
import edu.biu.scapi.exceptions.CommitValueException;
import edu.biu.scapi.exceptions.FactoriesException;
import edu.biu.scapi.exceptions.InvalidDlogGroupException;
import edu.biu.scapi.exceptions.SecurityLevelException;
import edu.biu.scapi.generals.ScapiDefaultConfiguration;
import edu.biu.scapi.interactiveMidProtocols.ot.OTRInput;
import edu.biu.scapi.interactiveMidProtocols.ot.OTROutput;
import edu.biu.scapi.interactiveMidProtocols.ot.OTReceiver;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.dh.SigmaDHProverComputation;
import edu.biu.scapi.interactiveMidProtocols.zeroKnowledge.ZKPOKFromSigmaCmtPedersenProver;
import edu.biu.scapi.primitives.dlog.DlogGroup;
import edu.biu.scapi.securityLevel.DDH;
import edu.biu.scapi.securityLevel.Malicious;
import edu.biu.scapi.securityLevel.StandAlone;
import edu.biu.scapi.tools.Factories.DlogGroupFactory;

/**
 * Concrete implementation of the receiver side in oblivious transfer based on the DDH assumption that achieves full simulation.<p>
 * This implementation can also be used as batch OT that achieves full simulation. <p>
 * In batch oblivious transfer, the parties run an initialization phase and then can carry out concrete OTs later 
 * whenever they have new inputs and wish to carry out an OT. <p>
 * 
 * This class derived from OTFullSimDDHReceiverAbs and implements the functionality 
 * related to the GroupElement inputs.
 * 
 * @author Cryptography and Computer Security Research Group Department of Computer Science Bar-Ilan University (Moriya Farbstein)
 *
 */
public class OTFullSimDDHOnGroupElementReceiver implements OTReceiver, Malicious, StandAlone{
	
	private DlogGroup dlog;
	private SecureRandom random;
	
	private OTFullSimPreprocessPhaseValues preprocessOutput; //Values calculated by the preprocess phase.
	
	/**
	 * Constructor that gets the channel and chooses default values of DlogGroup and SecureRandom.
	 * @param channel
	 * @throws ClassNotFoundException if there was a problem during the serialization mechanism in the preprocess phase.
	 * @throws CheatAttemptException if the receiver suspects that the sender is trying to cheat in the preprocess phase.
	 * @throws IOException if there was a problem during the communication in the preprocess phase.
	 * @throws CommitValueException can occur in case of ElGamal commitment scheme.
	 */
	public OTFullSimDDHOnGroupElementReceiver(Channel channel) throws IOException, CheatAttemptException, ClassNotFoundException, CommitValueException{
		//Read the default DlogGroup name from a configuration file.
		String dlogName = ScapiDefaultConfiguration.getInstance().getProperty("DDHDlogGroup");
		DlogGroup dlog = null;
		try {
			//Create the default DlogGroup by the factory.
			dlog = DlogGroupFactory.getInstance().getObject(dlogName);
		} catch (FactoriesException e1) {
			// Should not occur since the dlog name in the configuration file is valid.
		}
		
		try {
			doConstruct(channel, dlog, new SecureRandom());
		} catch (SecurityLevelException e1) {
			// Should not occur since the dlog in the configuration file is as secure as needed.
		} catch (InvalidDlogGroupException e) {
			// Should not occur since the dlog in the configuration file is valid.
		}
	}
	
	/**
	 * Constructor that sets the given channel, dlogGroup and random.
	 * @param channel
	 * @param dlog must be DDH secure.
	 * @param random
	 * @throws SecurityLevelException if the given dlog is not DDH secure
	 * @throws InvalidDlogGroupException if the given DlogGroup is not valid.
	 * @throws ClassNotFoundException if there was a problem during the serialization mechanism in the preprocess phase.
	 * @throws CheatAttemptException if the receiver suspects that the sender is trying to cheat in the preprocess phase.
	 * @throws IOException if there was a problem during the communication in the preprocess phase.
	 * @throws CommitValueException can occur in case of ElGamal commitment scheme.
	 */
	public OTFullSimDDHOnGroupElementReceiver(Channel channel, DlogGroup dlog, SecureRandom random) throws SecurityLevelException, InvalidDlogGroupException, IOException, CheatAttemptException, ClassNotFoundException, CommitValueException{
		
		doConstruct(channel, dlog, random);
	}
	
	/**
	 * Sets the given members.
	 * Runs the following line from the protocol:
	 * "IF NOT VALID_PARAMS(G,q,g)
	 *   		REPORT ERROR and HALT".
	 * @param channel
	 * @param dlog must be DDH secure.
	 * @param random
	 * @throws SecurityLevelException if the given dlog is not DDH secure
	 * @throws InvalidDlogGroupException if the given DlogGroup is not valid.
	 * @throws ClassNotFoundException 
	 * @throws CheatAttemptException 
	 * @throws IOException 
	 * @throws CommitValueException 
	 */
	private void doConstruct(Channel channel, DlogGroup dlog, SecureRandom random) throws SecurityLevelException, InvalidDlogGroupException, IOException, CheatAttemptException, ClassNotFoundException, CommitValueException {
		//The underlying dlog group must be DDH secure.
		if (!(dlog instanceof DDH)){
			throw new SecurityLevelException("DlogGroup should have DDH security level");
		}
		//Check that the given dlog is valid.
		// In Zp case, the check is done by Crypto++ library.
		//In elliptic curves case, by default SCAPI uploads a file with NIST recommended curves, 
		//and in this case we assume the parameters are always correct and the validateGroup function always return true.
		//It is also possible to upload a user-defined configuration file. In this case,
		//it is the user's responsibility to check the validity of the parameters by override the implementation of this function.
		if(!dlog.validateGroup())
			throw new InvalidDlogGroupException();
		
		this.dlog = dlog;
		this.random = random;

		//read the default statistical parameter used in sigma protocols from a configuration file.
		String statisticalParameter = ScapiDefaultConfiguration.getInstance().getProperty("StatisticalParameter");
		int t = Integer.parseInt(statisticalParameter);	
		//Creates the underlying ZKPOK. 
		ZKPOKFromSigmaCmtPedersenProver zkProver = new ZKPOKFromSigmaCmtPedersenProver(channel, new SigmaDHProverComputation(dlog, t, random));
		
		// Some OT protocols have a pre-process stage before the transfer. 
		// Usually, pre process is done once at the beginning of the protocol and will not be executed later, 
		// and then the transfer function could be called multiple times.
		// We implement the preprocess stage at construction time. 
		// A protocol that needs to call preprocess after the construction time, should create a new instance.
		//Call the utility function that executes the preprocess phase.
		preprocessOutput = OTFullSimReceiverPreprocessUtil.preProcess(dlog, zkProver, channel, random);
		
	}
	
	/**
	 * 
	 * Run the transfer phase of the OT protocol.<p>
	 * Transfer Phase (with input sigma) <p>
	 *		SAMPLE a random value r <- {0, . . . , q-1} <p>
	 *		COMPUTE<p>
	 *		4.	g = (gSigma)^r<p>
	 *		5.	h = (hSigma)^r<p>
	 *		SEND (g,h) to S<p>
	 *		WAIT for messages (u0,c0) and (u1,c1) from S<p>
	 *		IF  NOT<p>
	 *		�	u0, u1, c0, c1 in G<p>
	 *		      REPORT ERROR<p>
	 *		OUTPUT  xSigma = cSigma * (uSigma)^(-r)<p>
	 */
	public OTROutput transfer(Channel channel, OTRInput input) throws IOException, ClassNotFoundException, CheatAttemptException {
		//Creates the utility class that executes the transfer phase.
		OTFullSimOnGroupElementReceiverTransferUtil transferUtil = new OTFullSimOnGroupElementReceiverTransferUtil(dlog, random);
		return transferUtil.transfer(channel, input, preprocessOutput);
	}
}
