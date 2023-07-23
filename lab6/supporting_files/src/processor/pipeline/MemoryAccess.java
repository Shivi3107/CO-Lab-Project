package processor.pipeline;
import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Simulator;
import processor.Clock;
import generic.Element;
import generic.MemoryReadEvent;
import generic.MemoryResponseEvent;
import generic.MemoryWriteEvent;
import generic.Event;
import generic.Event.EventType;
import configuration.Configuration;

public class MemoryAccess implements Element {
	Processor containingProcessor;
	public EX_MA_LatchType EX_MA_Latch;
	public MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	Instruction instruction;

	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_EnableLatch)
	{
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}

	public void performMA()
	{
		if (EX_MA_Latch.isMA_busy())
		{
			containingProcessor.getEXUnit().OF_EX_Latch.EX_busy = true;
			return;
		}
		else
		{
			containingProcessor.getEXUnit().OF_EX_Latch.EX_busy = false;
		}

		if (EX_MA_Latch.getIsNOP())
		{
			MA_RW_Latch.setIsNOP(true);
			MA_RW_Latch.setInstruction(null);
			EX_MA_Latch.setIsNOP(false);
		}
		else if (EX_MA_Latch.isMA_enable())
		{
			instruction = EX_MA_Latch.getInstruction();
			int aluResult = EX_MA_Latch.getALU_result();
			MA_RW_Latch.setALU_result(aluResult);
			MA_RW_Latch.setInstruction(instruction);
			OperationType operation_type = instruction.getOperationType();
			switch (operation_type)
			{
				case load:
					Simulator.getEventQueue().addEvent( new MemoryReadEvent(Clock.getCurrentTime()
						+containingProcessor.get_l1d().latency,
						this, containingProcessor.get_l1d(), aluResult));
						
					EX_MA_Latch.setMA_busy(true);
					return;
				case store:
					int st_value = containingProcessor.getRegisterFile()
							.getValue(instruction.getSourceOperand1().getValue());

					Simulator.getEventQueue().addEvent( new MemoryWriteEvent( Clock.getCurrentTime()
						+containingProcessor.get_l1d().latency,
						this, containingProcessor.get_l1d(), aluResult, st_value));
						
					EX_MA_Latch.setMA_busy(true);
					return;
			}

			if (instruction.getOperationType().ordinal() == 29)
			{
				IF_EnableLatch.setIF_enable(false);
			} 
			MA_RW_Latch.setRW_enable(true);
		}
	}
	
	@Override
	public void handleEvent(Event e)
	{
		if (e.getEventType() == EventType.MemoryResponse)
		{
			MemoryResponseEvent event = (MemoryResponseEvent) e;
			int ld_value = event.getValue();

			MA_RW_Latch.setLoad_result(ld_value);
			MA_RW_Latch.setInstruction(instruction);

			EX_MA_Latch.setMA_busy(false);
			MA_RW_Latch.setRW_enable(true);
			containingProcessor.getEXUnit().OF_EX_Latch.EX_busy = false;
			EX_MA_Latch.setMA_enable(false);
		}
	}
}
