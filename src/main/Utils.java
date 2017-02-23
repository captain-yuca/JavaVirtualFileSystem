package main;

/**
 * The Class Utils is a utilities class made for this project.
 * 
 * 
 * @author Manuel A. Baez Gonzalez
 */
public class Utils {
	
	/**
	 * Verifies if the numberToCompare is a powerOf2
	 * @param numberToCompare The number to verify if it's a power of 2.
	 * @return True if its a power of 2 and false if its not.
	 */
	public static boolean powerOf2(int numberToCompare) {
		while(numberToCompare >=2){
			if(numberToCompare % 2 !=0){
				return false;
			}
			numberToCompare/=2;
		}
		return true;
	}
	
}
