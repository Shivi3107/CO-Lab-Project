package processor.pipeline;

import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand;
import generic.Statistics;
import generic.Operand.OperandType;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;

	public OperandFetch(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_EnableLatch) {
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}
	
	public static boolean checkConflict(Instruction instruct, int register1, int register2)
	{
		int InstOrdi = ((instruct != null) && (instruct.getOperationType() != null)) ?
					instruct.getOperationType().ordinal() : 1000;
		if (InstOrdi < 24 )
		{
			int DestiReg = (instruct != null) ? instruct.getDestinationOperand().getValue() : -1;
			if (register1 == DestiReg || register2 == DestiReg)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	public void Bubble ()
	{
		IF_EnableLatch.setIF_enable(false);
		OF_EX_Latch.setIsNOP(true);
	}
	
	public void performOF()
	{
		if (IF_OF_Latch.OF_busy)
		{
			return;
		}
		if (IF_EnableLatch.isIF_busy())
		{
			OF_EX_Latch.setIsNOP(true);
			return;
		}
		
		if(IF_OF_Latch.isOF_enable())
		{
			OperationType[] operationType = OperationType.values();
			int instruction = IF_OF_Latch.getInstruction();
			int opcode = instruction >>> 27;
			OperationType operation_type = operationType[opcode];
			
			if ((opcode > 23) && (opcode < 29))
			{
				IF_EnableLatch.setIF_enable(false);
			}
			
			boolean conflict = false;
			Instruction instruction_ex_stage = OF_EX_Latch.getInstruction();
			Instruction instruction_ma_stage = EX_MA_Latch.getInstruction();
			Instruction instruction_rw_stage = MA_RW_Latch.getInstruction();

			Instruction inst = new Instruction();
			
			
			int registerNo1;
			int registerNo2;
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
					registerNo1 = instruction << 5;
					registerNo1 = registerNo1 >>> 27;
					rs1.setValue(registerNo1);

					Operand rs2 = new Operand();
					rs2.setOperandType(OperandType.Register);
					registerNo2 = instruction << 10;
					registerNo2 = registerNo2 >>> 27;
					rs2.setValue(registerNo2);
					if (checkConflict(instruction_ex_stage, registerNo1, registerNo2))
						conflict = true;
					if (checkConflict(instruction_ma_stage, registerNo1, registerNo2))
						conflict = true;
					if (checkConflict(instruction_rw_stage, registerNo1, registerNo2))
						conflict = true;
					
					if (conflict == true)
					{
						this.Bubble();
						break;
					}

					Operand rd = new Operand();
					rd.setOperandType(OperandType.Register);
					registerNo1 = instruction << 15;
					registerNo1 = registerNo1 >>> 27;
					rd.setValue(registerNo1);

					inst.setOperationType(operation_type);
					inst.setSourceOperand1(rs1);
					inst.setSourceOperand2(rs2);
					inst.setDestinationOperand(rd);
					break;

				case end:
					inst.setOperationType(operation_type);
					IF_EnableLatch.setIF_enable(false);
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
						registerNo1 = instruction << 5;
						registerNo1 = registerNo1 >>> 27;
						op.setOperandType(OperandType.Register);
						op.setValue(registerNo1);
					}

					inst.setOperationType(operation_type);
					inst.setDestinationOperand(op);
					break;
			
				case beq:
				case bne:
				case blt:
				case bgt:
					rs1 = new Operand();
					rs1.setOperandType(OperandType.Register);
					registerNo1 = instruction << 5;
					registerNo1 = registerNo1 >>> 27;
					rs1.setValue(registerNo1);

					rs2 = new Operand();
					rs2.setOperandType(OperandType.Register);
					registerNo2 = instruction << 10;
					registerNo2 = registerNo2 >>> 27;
					rs2.setValue(registerNo2);
					if (checkConflict(instruction_ex_stage, registerNo1, registerNo2))
						conflict = true;
					if (checkConflict(instruction_ma_stage, registerNo1, registerNo2))
						conflict = true;
					if (checkConflict(instruction_rw_stage, registerNo1, registerNo2))
						conflict = true;
					
					if (conflict == true)
					{
						this.Bubble();
						break;
					}

					rd = new Operand();
					rd.setOperandType(OperandType.Immediate);
					imm = instruction << 15;
					imm = imm >> 15;
					
					rd.setValue(imm);

					inst.setOperationType(operation_type);
					inst.setSourceOperand1(rs1);
					inst.setSourceOperand2(rs2);
					inst.setDestinationOperand(rd);
					break;

				default:
					rs1 = new Operand();
					rs1.setOperandType(OperandType.Register);
					registerNo1 = instruction << 5;
					registerNo1 = registerNo1 >>> 27;
					rs1.setValue(registerNo1);
					if (checkConflict(instruction_ex_stage, registerNo1, registerNo1))
						conflict = true;	
					if (checkConflict(instruction_ma_stage, registerNo1, registerNo1))
						conflict = true;
					if (checkConflict(instruction_rw_stage, registerNo1, registerNo1))
						conflict = true;
					
					if (conflict == true)
					{
						this.Bubble();
						break;
					}

					rd = new Operand();
					rd.setOperandType(OperandType.Register);
					registerNo1 = instruction << 10;
					registerNo1 = registerNo1 >>> 27;
					rd.setValue(registerNo1);

					rs2 = new Operand();
					rs2.setOperandType(OperandType.Immediate);
					imm = instruction << 15;
					imm = imm >> 15;
					
					rs2.setValue(imm);

					inst.setOperationType(operation_type);
					inst.setSourceOperand1(rs1);
					inst.setSourceOperand2(rs2);
					inst.setDestinationOperand(rd);
					break;
			}
			OF_EX_Latch.setInstruction(inst);
			OF_EX_Latch.setEX_enable(true);
			if (!OF_EX_Latch.getIsNOP())
			{
				IF_OF_Latch.setOF_enable(false);
			}
		}
	}
}
