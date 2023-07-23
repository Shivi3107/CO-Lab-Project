package processor.pipeline;

import processor.Processor;

import java.util.Arrays;

import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand;
import generic.Operand.OperandType;

public class Execute {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	
	public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch, EX_IF_LatchType eX_IF_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
	}
	
	public void performEX()
	{
		if(OF_EX_Latch.isEX_enable())
		{
			Instruction instruction = OF_EX_Latch.getInstruction();
			EX_MA_Latch.setInstruction(instruction);
			OperationType operation_type = instruction.getOperationType();
			int opcode = Arrays.asList(OperationType.values()).indexOf(operation_type);
			int currentPC = containingProcessor.getRegisterFile().getProgramCounter() - 1;
			System.out.println(instruction);

			int aluResult = 0;

			if((opcode % 2 == 0) && (opcode < 21))
			{
				int operand_1 = containingProcessor.getRegisterFile().getValue(
						instruction.getSourceOperand1().getValue());
				int operand_2 = containingProcessor.getRegisterFile().getValue(
						instruction.getSourceOperand2().getValue());

				switch(operation_type)
				{
					case add:
						aluResult = operand_1 + operand_2;
						break;
					case sub:
						aluResult = operand_1 - operand_2;
						break;
					case mul:
						aluResult = operand_1 * operand_2;
						break;
					case div:
						aluResult = operand_1 / operand_2;
						int remainder = operand_1 % operand_2;
						containingProcessor.getRegisterFile().setValue(31, remainder);
						break;
					case and:
						aluResult = operand_1 & operand_2;
						break;
					case or:
						aluResult = operand_1 | operand_2;
						break;
					case xor:
						aluResult = operand_1 ^ operand_2;
						break;
					case slt:
						if(operand_1 < operand_2)
							aluResult = 1;
						else
							aluResult = 0;
						break;
					case sll:
						aluResult = operand_1 << operand_2;
						break;
					case srl:
						aluResult = operand_1 >>> operand_2;
						break;
					case sra:
						aluResult = operand_1 >> operand_2;
						break;
				}
				System.out.println(operand_1);
				System.out.println(operand_2);
			}
			else if(opcode < 24)
			{
				int operand_1 = containingProcessor.getRegisterFile().getValue(
							instruction.getSourceOperand1().getValue());
				int operand_2 = instruction.getSourceOperand2().getValue();

				switch(operation_type)
				{
					case addi:
						aluResult = operand_1 + operand_2;
						break;
					case subi:
						aluResult = operand_1 - operand_2;
						break;
					case muli:
						aluResult = operand_1 * operand_2;
						break;
					case divi:
						aluResult = operand_1 / operand_2;
						int remainder = operand_1 % operand_2;
						containingProcessor.getRegisterFile().setValue(31, remainder);
						break;
					case andi:
						aluResult = operand_1 & operand_2;
						break;
					case ori:
						aluResult = operand_1 | operand_2;
						break;
					case xori:
						aluResult = operand_1 ^ operand_2;
						break;
					case slti:
						if(operand_1 < operand_2)
							aluResult = 1;
						else
							aluResult = 0;
						break;
					case slli:
						aluResult = operand_1 << operand_2;
						break;
					case srli:
						aluResult = operand_1 >>> operand_2;
						break;
					case srai:
						aluResult = operand_1 >> operand_2;
						break;
					case load:
						aluResult = operand_1 + operand_2;
						break;
					case store:
						operand_1 = containingProcessor.getRegisterFile().getValue(
							instruction.getDestinationOperand().getValue());
						aluResult = operand_1 + operand_2;
						break;
				}
				System.out.println(operand_1);
				System.out.println(operand_2);
			}
			else if(opcode == 24)
			{
				OperandType optype = instruction.getDestinationOperand().getOperandType();
				int imm = 0;
				if (optype == OperandType.Register)
				{
					imm = containingProcessor.getRegisterFile().getValue(
						instruction.getDestinationOperand().getValue());
				}
				else
				{
					imm = instruction.getDestinationOperand().getValue();
				}
				aluResult = imm + currentPC;
				EX_IF_Latch.setIS_enable(true, aluResult);
			}
			else if(opcode < 29)
			{
				int operand_1 = containingProcessor.getRegisterFile().getValue(
							instruction.getSourceOperand1().getValue());
				int operand_2 = containingProcessor.getRegisterFile().getValue(
							instruction.getSourceOperand2().getValue());
				int imm = instruction.getDestinationOperand().getValue();
				
				switch(operation_type)
				{
					case beq:
						if(operand_1 == operand_2)
						{
							aluResult = imm + currentPC;
							EX_IF_Latch.setIS_enable(true, aluResult);
						}
						break;
					case bne:
						if(operand_1 != operand_2)
						{
							aluResult = imm + currentPC;
							EX_IF_Latch.setIS_enable(true, aluResult);
						}

						break;
					case blt:
						if(operand_1 < operand_2)
						{
							aluResult = imm + currentPC;
							EX_IF_Latch.setIS_enable(true, aluResult);
						}
						break;
					case bgt:
						if(operand_1 > operand_2)
						{
							aluResult = imm + currentPC;
							EX_IF_Latch.setIS_enable(true, aluResult);
						}
						break;
					default:
						break;
				}
				System.out.println(operand_1);
				System.out.println(operand_2);
			}
			EX_MA_Latch.setALU_result(aluResult);
		}

		OF_EX_Latch.setEX_enable(false);
		EX_MA_Latch.setMA_enable(true);
	}
}
