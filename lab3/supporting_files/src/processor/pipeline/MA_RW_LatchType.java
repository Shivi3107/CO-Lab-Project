package processor.pipeline;

import generic.Instruction;

public class MA_RW_LatchType {
	
	boolean RW_enable;
	Instruction instruction;
	int ldResult;
	int aluResult;
	
	public MA_RW_LatchType()
	{
		RW_enable = false;
	}

	public boolean isRW_enable() {
		return RW_enable;
	}

	public void setRW_enable(boolean rW_enable) {
		RW_enable = rW_enable;
	}

	public void setInstruction(Instruction inst) {
		instruction = inst;
	}

	public void setALU_result(int result1) {
		aluResult = result1;
	}

	public void setLoad_result(int result2) {
		ldResult = result2;
	}

	public Instruction getInstruction() {
		return instruction;
	}

	public int getALU_result() {
		return aluResult;
	}

	public int getLoad_result() {
		return ldResult;
	}

}
