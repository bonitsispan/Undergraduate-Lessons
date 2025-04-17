#$ Mponitsis Pantelis    AM: 4742    Username: cse94742 #$
#$ Sidiropoulos Georgios AM: 4789    Username: cse94789 #$

#$ The program takes as input two numbers, x and limit. #$
#$ The function f_up (f_down) starts by displaying x and then the limit numbers greater (smaller) than it. #$
#$ Finally, it displays the counts of the greater and smaller numbers using a counter variable and, if the limit variable is not zero, it displays the number 100. #$

#$ An example usage of the program on Risc-V is as follows: #$

#$ Give me input: 1 #$
#$ Give me input: 2 #$
#$ 1 #$
#$ 2 #$
#$ 3 #$
#$ 1 #$
#$ 0 #$
#$ -1 #$
#$ 2 #$
#$ 2 #$
#$ 100 #$

def main_f4():
#{
    #declare x, y, limit, number_up, number_down

    def f_up(a, b):
    #{
       #declare counter, temp

       temp = a;
       counter = 0;

       while(temp <= (a+b)):
       #{
            print(temp);
            temp = temp + 1;
            counter = counter + 1;
       #} 
        return(counter-1);
    #}

    def f_down(a, b):
    #{
       #declare counter, temp

       temp = a;
       counter = 0;

       while(temp >= (a-b)):
       #{
            print(temp);
            temp = temp - 1;
            counter = counter + 1;
       #} 
        return(counter-1);
    #}


    x = int(input());
    y = 100;
    limit = int(input());

    number_up = f_up(x, limit);
    number_down = f_down(x, limit);
    
    print(number_up);
    print(number_down);

    if(limit != 0):
        print(y);
#}

if __name__=="__main__":
    main_f4();