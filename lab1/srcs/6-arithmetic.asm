.data
a:
1
d:
3
n:
7
.text
main:
add %x0, %x0, %x3
addi %x3, 65535, %x3
load %x0, $n, %x4
load %x0, $a, %x5
load %x0, $d, %x6
sub %x5, %x5, %x7
loop:
beq %x4, %x7, endl
store %x5, 0, %x3
subi %x3, 1, %x3
add %x5, %x6, %x5
subi %x4, 1, %x4
jmp loop
endl:
end
