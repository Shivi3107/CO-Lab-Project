n:
10
.text
main:
sub %x0, %x0, %x3
addi %x3, 65535, %x3
load %x0, $n, %x4
sub %x4, %x4, %x5
addi %x5, 1, %x6
addi %x5, 1, %x7
store %x6, 0, %x3
subi %x3, 1, %x3
store %x7, 0, %x3
subi %x3, 1, %x3
subi %x4, 2, %x4
loop:
beq %x4, %x5, endl
add %x7, %x6, %x8
store %x8, 0, %x3
subi %x3, 1, %x3
subi %x4, 1, %x4
add %x7, %x5, %x6
add %x8, %x5, %x7
jmp loop
endl:
end
