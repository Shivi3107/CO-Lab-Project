package generic;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import processor.Clock;
import processor.Processor;
import generic.Statistics;

public class Simulator {
		
	static Processor processor;
	static boolean simulationComplete;
	static EventQueue eventQueue;

	public static EventQueue getEventQueue()
	{
		return eventQueue;
	}
	
	public static void setupSimulation(String assemblyProgramFile, Processor p)
	{
		Simulator.processor = p;
		try
		{
			loadProgram(assemblyProgramFile);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		eventQueue = new EventQueue();
		simulationComplete = false;
	}
	
	static void loadProgram(String assemblyProgramFile) throws IOException
	{
		/* 1. load the program into memory according to the program layout described
		 *    in the ISA specification
		 * 2. set PC to the address of the first instruction in the main
		 * 3. set the following registers:
		 *     x0 = 0
		 *     x1 = 65535
		 *     x2 = 65535
		 */
		InputStream InSt = new FileInputStream(assemblyProgramFile);
		DataInputStream DataInSt = new DataInputStream(InSt);

		int address = -1;
		while(DataInSt.available() > 0)
		{
			if(address == -1)
			{
				processor.getRegisterFile().setProgramCounter(DataInSt.readInt());
			}
			else
			{
				processor.getMainMemory().setWord(address, DataInSt.readInt());
			}
			address += 1;
		}
        
		processor.getRegisterFile().setValue(0, 0);
		processor.getRegisterFile().setValue(1, 65535);
		processor.getRegisterFile().setValue(2, 65535);
		
	}
			
	public static void simulate()
	{
		while(simulationComplete == false)
		{
			processor.getRWUnit().performRW();
			processor.getMAUnit().performMA();
			processor.getEXUnit().performEX();
			eventQueue.processEvents();
			processor.getOFUnit().performOF();
			processor.getIFUnit().performIF();
			Clock.incrementClock();
			
			Statistics.setNumberOfCycles(Clock.getCurrentTime());
		}
	}
	
	public static void setSimulationComplete(boolean value)
	{
		simulationComplete = value;
	}
}
