.data
str_nl: .asciz "\n" 
in: .asciz "Give me input: "
.text

L_goToMain:
	j Lmain
L100:
	#(begin_block, main_factorial, _, _)
	addi sp, sp, 32
	sw ra, (sp)
	mv gp, sp
L101:
	#(in, x, _, _)
	la a0, in
	li a7, 4
	ecall
	li a7, 5
	ecall
	sw a0,-12(gp)
L102:
	#(:=, 1, _, fact)
	li t1, 1
	sw t1,-20(gp)
L103:
	#(:=, 1, _, i)
	li t1, 1
	sw t1,-16(gp)
L104:
	#(<=, i, x, 106)
	lw t1,-16(gp)
	lw t2,-12(gp)
	ble t1, t2, L106
L105:
	#(jump, _, _, 111)
	j L111
L106:
	#(*, fact, i, T%1)
	lw t1,-20(gp)
	lw t2,-16(gp)
	mul t1, t1, t2
	sw t1,-24(sp)
L107:
	#(:=, T%1, _, fact)
	lw t1,-24(sp)
	sw t1,-20(gp)
L108:
	#(+, i, 1, T%2)
	lw t1,-16(gp)
	li t2, 1
	add t1, t1, t2
	sw t1,-28(sp)
L109:
	#(:=, T%2, _, i)
	lw t1,-28(sp)
	sw t1,-16(gp)
L110:
	#(jump, _, _, 104)
	j L104
L111:
	#(out, fact, _, _)
	lw a0,-20(gp)
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall
L112:
	#(end_block, main_factorial, _, _)
	lw ra, (sp)
	addi sp, sp, -32
	jr ra
L113:
	#(begin_block, fibonacci, _, _)
	sw ra, (sp)
L114:
	#(<=, x, 1, 116)
	lw t1,-12(sp)
	li t2, 1
	ble t1, t2, L116
L115:
	#(jump, _, _, 118)
	j L118
L116:
	#(ret, x, _, _)
	lw t1,-12(sp)
	lw t0, -8(sp)
	sw t1, (t0)
	lw ra, (sp)
	jr ra
L117:
	#(jump, _, _, 128)
	j L128
L118:
	#(-, x, 1, T%3)
	lw t1,-12(sp)
	li t2, 1
	sub t1, t1, t2
	sw t1,-16(sp)
L119:
	#(par, T%3, cv, _)
	addi fp, sp, 36
	lw t0,-16(sp)
	sw t0, -12(fp)
L120:
	#(par, T%4, ret, _)
	addi t0, sp, -20
	sw t0, -8(fp)
L121:
	#(call, fibonacci, _, _)
	lw t0, -4(sp)
	sw t0, -4(fp)
	addi sp, sp, 36
	jal L113
	addi sp, sp, -36
L122:
	#(-, x, 2, T%5)
	lw t1,-12(sp)
	li t2, 2
	sub t1, t1, t2
	sw t1,-24(sp)
L123:
	#(par, T%5, cv, _)
	addi fp, sp, 36
	lw t0,-24(sp)
	sw t0, -12(fp)
L124:
	#(par, T%6, ret, _)
	addi t0, sp, -28
	sw t0, -8(fp)
L125:
	#(call, fibonacci, _, _)
	lw t0, -4(sp)
	sw t0, -4(fp)
	addi sp, sp, 36
	jal L113
	addi sp, sp, -36
L126:
	#(+, T%4, T%6, T%7)
	lw t1,-20(sp)
	lw t2,-28(sp)
	add t1, t1, t2
	sw t1,-32(sp)
L127:
	#(ret, T%7, _, _)
	lw t1,-32(sp)
	lw t0, -8(sp)
	sw t1, (t0)
	lw ra, (sp)
	jr ra
L128:
	#(end_block, fibonacci, _, _)
	lw ra, (sp)
	jr ra
