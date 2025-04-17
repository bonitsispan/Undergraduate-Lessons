#$ Mponitsis Pantelis    AM: 4742    Username: cse94742 #$
#$ Sidiropoulos Georgios AM: 4789    Username: cse94789 #$

#$ The function factorial calculates the factorial of a number using recursion. #$
#$ The function count_digits calculates the number of digits in a number using recursion. #$

#$ An example usage of the program on Risc-V is as follows: #$

#$ Give me input: 4 #$
#$ 24 #$
#$ Give me input: 12345 #$
#$ 5 #$

def main_f3():
#{
    #declare x, y
    def factorial(n):
    #{
        if(n == 0):
            return(1);
        else:
            return(n*factorial(n-1));
    #}

    def count_digits(n):
    #{
        if(n < 10):
            return(1);
        else:
            return(count_digits(n // 10) + 1);
    #}

    x = int(input());
    print(factorial(x));
    y = int(input());
    print(count_digits(y));
#}

   
if __name__=="__main__":
    main_f3();