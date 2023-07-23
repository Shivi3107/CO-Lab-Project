package processor.pipeline;

import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand;
import generic.Operand.OperandType;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
		
	public OperandFetch(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
	}
	
	public void performOF()
	{
		if(IF_OF_Latch.isOF_enable())
		{
			OperationType[] operationType = OperationType.values();
			int instruction = IF_OF_Latch.getInstruction();
			int opcode = instruction >>> 27;
			OperationType operation_type = operationType[opcode];

			Instruction inst = new Instruction();
			OF_EX_Latch.setInstruction(inst);
			inst.setOperationType(operation_type);
			int registerNo;
			switch(operation_type)
			{
				case add:
				case sub:
				case mul:
				case div:
				case and:
				case or:
				case xor:
				case slt:
				case sll:
				case srl:
				case sra:
					Operand rs1 = new Operand();
					rs1.setOperandType(OperandType.Register);
					registerNo = instruction << 5;
					registerNo = registerNo >>> 27;
					rs1.setValue(registerNo);

					Operand rs2 = new Operand();
					rs2.setOperandType(OperandType.Register);
					registerNo = instruction << 10;
					registerNo = registerNo >>> 27;
					rs2.setValue(registerNo);

					Operand rd = new Operand();
					rd.setOperandType(OperandType.Register);
					registerNo = instruction << 15;
					registerNo = registerNo >>> 27;
					rd.setValue(registerNo);

					
					inst.setSourceOperand1(rs1);
					inst.setSourceOperand2(rs2);
					inst.setDestinationOperand(rd);
					break;

				case end:
					break;
				case jmp:
					Operand op = new Operand();
					int imm = instruction << 10;
					imm = imm >> 10;
					if (imm != 0)
					{
						op.setOperandType(OperandType.Immediate);
						op.setValue(imm);
					}
					else
					{
						registerNo = instruction << 5;
						registerNo = registerNo >>> 27;
						op.setOperandType(OperandType.Register);
						op.setValue(registerNo);
					}

					inst.setDestinationOperand(op);
					break;
			
				case beq:
				case bne:
				case blt:
				case bgt:
					rs1 = new Operand();
					rs1.setOperandType(OperandType.Register);
					registerNo = instruction << 5;
					registerNo = registerNo >>> 27;
					rs1.setValue(registerNo);

					// destination register
					rs2 = new Operand();
					rs2.setOperandType(OperandType.Register);
					registerNo = instruction << 10;
					registerNo = registerNo >>> 27;
					rs2.setValue(registerNo);

					// Immediate value
					rd = new Operand();
					rd.setOperandType(OperandType.Immediate);
					imm = instruction << 15;
					imm = imm >> 15;
					
					rd.setValue(imm);

					inst.setSourceOperand1(rs1);
					inst.setSourceOperand2(rs2);
					inst.setDestinationOperand(rd);
					break;

				default:
					// Source register 1
					rs1 = new Operand();
					rs1.setOperandType(OperandType.Register);
					registerNo = instruction << 5;
					registerNo = registerNo >>> 27;
					rs1.setValue(registerNo);

					// Destination register
					rd = new Operand();
					rd.setOperandType(OperandType.Register);
					registerNo = instruction << 10;
					registerNo = registerNo >>> 27;
					rd.setValue(registerNo);

					// Immediate values
					rs2 = new Operand();
					rs2.setOperandType(OperandType.Immediate);
					imm = instruction << 15;
					imm = imm >> 15;
					
					rs2.setValue(imm);

					inst.setSourceOperand1(rs1);
					inst.setSourceOperand2(rs2);
					inst.setDestinationOperand(rd);
					break;
			}

			IF_OF_Latch.setOF_enable(false);
			OF_EX_Latch.setEX_enable(true);
		}
	}

}
