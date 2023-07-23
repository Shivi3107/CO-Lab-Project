.data
a:
	10
	.text
main:
	load %x0, $a, %x3
	load %x0, $a, %x4
	add %x0, %x0, %x5
	divi %x4, 2, %x4
	add %x0, %x0, %x6
	add %x0, %x0, %x7
	add %x0, %x0, %x10
	addi %x0, 1, %x10
loop:
	beq %x4, %x5, endl
	addi %x5, 1, %x5
	div %x3, %x5, %x6
	mul %x6, %x5, %x7
	beq %x7, %x3, success
	jmp loop
success:
	subi %x10, 2, %x10
	end
endl:
	end
