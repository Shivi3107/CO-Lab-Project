	.data
n:
	5
l:
	2
	-1
	7
	5
	3
	.text
main:
	add %x0, %x0, %x3
	load %x0, $n, %x5
	sub %x5, %x5, %x6
	sub %x5, %x5, %x10
loop:
	beq %x5, %x6, endl
	load %x3, $l, %x4
	subi %x5, 1, %x5
	addi %x3, 1, %x3
	blt %x4, %x6, loop
	srli %x4, 1, %x7
	slli %x7, 1, %x7
	sub %x4, %x7, %x8
	beq %x8, %x6, success
	jmp loop
success:
	addi %x10, 1, %x10
	jmp loop
endl:
	end
