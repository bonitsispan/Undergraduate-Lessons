#$ Mponitsis Pantelis    AM: 4742    Username: cse94742 #$
#$ Sidiropoulos Georgios AM: 4789    Username: cse94789 #$

#$ This test contains a semantic analysis error on line 20. #$

def main_factorial():
    #{
	
	#$ declarations #$

	#declare x
	#declare i

	#$ body of main_factorial #$
	x = int(input());
	fact = 1;
	i = 1;
	while (i<=x):
	#{
		fact = fact * i;  #$ The variable fact is being used in an assignment without being declared. #$ 
		i = i + 1;
	#}
	print(fact);
#}

if __name__ == "__main__":
	#$ call of main functions #$
	main_factorial();