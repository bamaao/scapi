/**
* This file is part of SCAPI.
* SCAPI is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
* SCAPI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with SCAPI.  If not, see <http://www.gnu.org/licenses/>.
*
* Any publication and/or code referring to and/or based on SCAPI must contain an appropriate citation to SCAPI, including a reference to http://crypto.cs.biu.ac.il/SCAPI.
*
* SCAPI uses Crypto++, Miracl, NTL and Bouncy Castle. Please see these projects for any further licensing issues.
*
*/
package edu.biu.scapi.comm;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 
 */

/** 
 * @author LabTest
 */
public class Message extends MessageAbs implements Serializable{
	
	

	/**
	 * @param specialData
	 */
	public Message(byte[] specialData) {
		super(specialData);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public Message() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 
	 * 
	 */
	private byte[] data = null;

	/** 
	 * @param data
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	/** 
	 * @return
	 */
	public byte[] getData() {
		return data;
	}

	@Override
	public String toString() {
		return "Message [data=" + Arrays.toString(data) + "]";
	}
	
	
}
