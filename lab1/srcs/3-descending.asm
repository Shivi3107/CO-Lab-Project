	.data
a:
	70
	80
	40
	20
	10
	30
	50
	60
n:
	8
	.text
main:
	add %x0, %x0, %x3
	load %x0, $n, %x4
	load %x0, $n, %x5
	sub %x5, %x5, %x6
	sub %x5, %x5, %x7
	addi %x6, 1, %x6
	addi %x7, 1, %x7
loop:
	beq %x4, %x6, loop2
	load %x3, $a, %x8
	subi %x4, 1, %x4
	addi %x3, 1, %x3
	load %x3, $a, %x9
	bgt %x8, %x9, exchange
	jmp loop
loop2:
	load %x0, $n, %x4
	addi %x6, 1, %x6
	subi %x5, 1, %x5
	add %x0, %x0, %x3
	beq %x5, %x7, endl
	jmp loop
exchange:
	store %x8, $a, %x3
	subi %x3, 1, %x3
	store %x9, $a, %x3
	addi %x3, 1, %x3
	jmp loop
endl:
	end
