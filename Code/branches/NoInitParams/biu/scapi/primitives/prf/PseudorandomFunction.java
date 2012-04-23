package edu.biu.scapi.primitives.prf;

import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;


/** 
 * General interface for pseudorandom function. Every class in this family should implement this interface. <p>
 * 
 * In cryptography, a pseudorandom function family, abbreviated PRF, 
 * is a collection of efficiently-computable functions which emulate a random oracle in the following way: 
 * no efficient algorithm can distinguish (with significant advantage) between a function chosen randomly from the PRF family and a random oracle 
 * (a function whose outputs are fixed completely at random).
 * 
 * @author Cryptography and Computer Security Research Group Department of Computer Science Bar-Ilan University (Meital Levy)
 */
public interface PseudorandomFunction {
	
	/**
	 * Sets the secret key for this prf.
	 * The key can be changed at any time. 
	 * @param secretKey secret key
	 * @throws InvalidKeyException 
	 */
	public void setKey(SecretKey secretKey) throws InvalidKeyException;
	
	/**
	 * An object trying to use an instance of prf needs to check if it has already been initialized.
	 * @return true if the object was initialized by calling the function setKey.
	 */
	public boolean isKeySet();

	/** 
	 * @return The algorithm name
	 */
	public String getAlgorithmName() ;

	/** 
	 * @return the input block size in bytes
	 */
	public int getBlockSize();
	
	/**
	 * Generates a secret key to initialize this prf object.
	 * @param keySize algorithmParameterSpec contains the required parameters for the key generation
	 * @return the generated secret key
	 * @throws InvalidParameterSpecException 
	 */
	public SecretKey generateKey(AlgorithmParameterSpec keyParams) throws InvalidParameterSpecException;
	
	/**
	 * Generates a secret key to initialize this prf object.
	 * @param keySize is the required secret key size in bits 
	 * @return the generated secret key 
	 */
	public SecretKey generateKey(int keySize);

	/** 
	 * Computes the function using the secret key. <p>
	 * The user supplies the input byte array and the offset from which to take the data from. 
	 * The user also supplies the output byte array as well as the offset. 
	 * The computeBlock function will put the output in the output array starting at the offset. <p> 
	 * This function is suitable for block ciphers where the input/output length is known in advance.
	 * @param inBytes input bytes to compute
	 * @param inOff input offset in the inBytes array
	 * @param outBytes output bytes. The resulted bytes of compute
	 * @param outOff output offset in the outBytes array to put the result from
	 * @throws IllegalBlockSizeException 
	 */
	public void computeBlock(byte[] inBytes, int inOff, byte[] outBytes, int outOff) throws IllegalBlockSizeException;
	
	/**
	 * Computes the function using the secret key. <p>
	 * This function is provided in the interface especially for the sub-family PrfVaryingIOLength, which may have variable input and output length.
	 * If the implemented algorithm is a block cipher then the size of the input as well as the output is known in advance and 
	 * the use may call the other computeBlock function where length is not require.
	 * @param inBytes input bytes to compute
	 * @param inOff input offset in the inBytes array
	 * @param inLen the length of the input array
	 * @param outBytes output bytes. The resulted bytes of compute
	 * @param outOff output offset in the outBytes array to put the result from
	 * @param outLen the length of the output array
	 * @throws IllegalBlockSizeException 
	 */
	public void computeBlock(byte[] inBytes, int inOff, int inLen, byte[] outBytes, int outOff, int outLen) throws IllegalBlockSizeException;
	
	/**
	 * Computes the function using the secret key. <p>
	 * This function is provided in this PseudorandomFunction interface for the sake of interfaces (or classes) for which 
	 * the input length can be different for each computation. Hmac and Prf/Prp with variable input length are examples of 
	 * such interfaces.
	 * 
	 * @param inBytes input bytes to compute
	 * @param inOff input offset in the inBytes array
	 * @param inLen the length of the input array
	 * @param outBytes output bytes. The resulted bytes of compute.
	 * @param outOff output offset in the outBytes array to put the result from
	 * @throws IllegalBlockSizeException 
	 */
	public void computeBlock(byte[] inBytes, int inOffset, int inLen, byte[] outBytes, int outOffset) throws IllegalBlockSizeException;

	
}