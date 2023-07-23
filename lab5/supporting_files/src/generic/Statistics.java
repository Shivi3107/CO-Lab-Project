package generic;
import java.io.PrintWriter;

public class Statistics {
	
	static int numberOfInstructions;
	static long numberOfCycles;
	static int numberOfBranchTaken;

	public static void printStatistics(String statFile)
	{
		try
		{
			PrintWriter writer = new PrintWriter(statFile);
			
			writer.println("Number of instructions executed = " + numberOfInstructions);
			writer.println("Number of cycles taken = " + numberOfCycles);
			writer.println("instructions per cycle = " + ((float)numberOfInstructions / (float)numberOfCycles));
			
			writer.close();
		}
		catch(Exception e)
		{
			Misc.printErrorAndExit(e.getMessage());
		}
	}
	
	// TODO write functions to update statistics
	public static void setNumberOfInstructions(int numberOfInstructions) {
		Statistics.numberOfInstructions = numberOfInstructions;
	}

	public static void setNumberOfCycles(long numberOfCycles) {
		Statistics.numberOfCycles = numberOfCycles;
	}

	public static int getNumberOfInstructions() {
		return numberOfInstructions;
	}

	public static long getNumberOfCycles() {
		return numberOfCycles;
	}
	
	public static void setNumberOfBranchTaken(int numberOfBranchTaken) {
		Statistics.numberOfBranchTaken = numberOfBranchTaken;
	}
	
	public static int getNumberOfBranchTaken() {
		return numberOfBranchTaken;
	}

}
