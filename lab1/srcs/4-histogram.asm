	.data
count:
	0
	0
	0
	0
	0
	0
	0
	0
  0
  0
  0
marks:
	2
  3
  0
  5
  10
  7
  1
  10
  10
  8
  9
  6
  7
  8
  2
  4
  5
  0
  9
  1
n:
  20
	.text
main:
	add %x0, %x0, %x3
	add %x0, %x0, %x4
	load %x0, $n, %x5
	sub %x5, %x5, %x6
	addi %x6, 11, %x7
	sub %x5, %x5, %x8
	sub %x5, %x5, %x10
loop:
	beq %x5, %x6, loop2
	load %x3, $marks, %x9
	subi %x5, 1, %x5
	addi %x3, 1, %x3
	beq %x8, %x9, success
	jmp loop
loop2:
	store %x10, $count, %x4
	sub %x5, %x5, %x10
	addi %x4, 1, %x4
	load %x0, $n, %x5
	add %x0, %x0, %x3
	addi %x8, 1, %x8
	subi %x7, 1, %x7
	beq %x7, %x6, endl
	jmp loop
success:
	addi %x10, 1, %x10
	jmp loop
endl:
	end
