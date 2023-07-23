package processor.pipeline;
import generic.Instruction;

public class EX_MA_LatchType {
	
	boolean MA_enable;
	int aluResult;
	Instruction instruction;
	
	public EX_MA_LatchType()
	{
		MA_enable = false;
	}

	public boolean isMA_enable() {
		return MA_enable;
	}
	
	public void setMA_enable(boolean mA_enable) {
		MA_enable = mA_enable;
	}
	
	public void setInstruction(Instruction inst) {
		instruction = inst;
	}

	public void setALU_result(int result) {
		aluResult = result;
	}

	public Instruction getInstruction() {
		return instruction;
	}

	public int getALU_result() {
		return aluResult;
	}

}
