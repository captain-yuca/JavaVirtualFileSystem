package diskUtilities;

public class INode {
	private byte fileType;
	private int fileSize;
	private int firstBlockIndex;
	
	public INode(byte fileType,int fileSize, int firstBlockIndex){
		this.fileType=fileType;
		this.fileSize=fileSize;
		this.firstBlockIndex=firstBlockIndex;
	}
	
	
	/**
	 * @return the fileSize
	 */
	public int getFileSize() {
		return fileSize;
	}
	/**
	 * @param fileSize the fileSize to set
	 */
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	/**
	 * @return the fileType
	 */
	public byte getFileType() {
		return fileType;
	}
	/**
	 * @param fileType the fileType to set
	 */
	public void setFileType(byte fileType) {
		this.fileType = fileType;
	}
	/**
	 * @return the firstBlockIndex
	 */
	public int getFirstBlockIndex() {
		return firstBlockIndex;
	}
	/**
	 * @param firstBlockIndex the firstBlockIndex to set
	 */
	public void setFirstBlockIndex(int firstBlockIndex) {
		this.firstBlockIndex = firstBlockIndex;
	}
	
	
}
