package processor.pipeline;

import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;

public class MemoryAccess {
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	
	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
	}
	
	public void performMA()
	{
		if(EX_MA_Latch.isMA_enable())
		{
			Instruction instruction = EX_MA_Latch.getInstruction();
			int aluResult = EX_MA_Latch.getALU_result();
			MA_RW_Latch.setALU_result(aluResult);
			MA_RW_Latch.setInstruction(instruction);
			OperationType operation_type = instruction.getOperationType();
			switch(operation_type)
			{
				case load:
					int ld_value = containingProcessor.getMainMemory().getWord(aluResult);
					MA_RW_Latch.setLoad_result(ld_value);
					break;
				case store:
					int st_value = containingProcessor.getRegisterFile().getValue(
							instruction.getSourceOperand1().getValue());
					containingProcessor.getMainMemory().setWord(aluResult, st_value);
					break;
			}
			EX_MA_Latch.setMA_enable(false);
			MA_RW_Latch.setRW_enable(true);
		}
	}

}
